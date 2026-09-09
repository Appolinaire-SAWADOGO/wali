# ============================================================
# 1. TERRAFORM & PROVIDER
# ============================================================

terraform {
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 6.0"
    }
  }
}

# Le provider AWS permet à Terraform de communiquer avec AWS.
# Toutes les ressources AWS seront créées dans cette région.
provider "aws" {
  region = "us-east-1"
}


# ============================================================
# 2. VPC
# ============================================================

# La VPC est le réseau privé principal de notre infrastructure AWS.
#
# 10.0.0.0/16 signifie que notre VPC dispose d'un grand espace
# d'adresses IP privées dans lequel nous allons créer nos subnets.

resource "aws_vpc" "main" {
  cidr_block = "10.0.0.0/16"

  instance_tenancy = "default"

  tags = {
    Name = "wali-vpc"
  }
}


# ============================================================
# 3. SUBNET PUBLIC - AZ A
# ============================================================

# Ce subnet sera utilisé pour les ressources qui doivent pouvoir
# communiquer directement avec Internet, notamment le Load Balancer.

resource "aws_subnet" "public_a" {
  vpc_id = aws_vpc.main.id

  # Ce CIDR ne chevauche pas celui des autres subnets.
  cidr_block = "10.0.1.0/24"

  # Une instance lancée ici peut recevoir automatiquement une
  # adresse IPv4 publique.
  map_public_ip_on_launch = true

  // zone de dispobilite
  availability_zone = "us-east-1a"

  tags = {
    Name = "wali-public-subnet-a"
  }
}


# ============================================================
# 4. SUBNET PUBLIC - AZ B
# ============================================================

# Deuxième subnet public dans une autre plage IP.
#
# Pour EKS et la haute disponibilité, il est préférable de
# travailler avec plusieurs Availability Zones.

resource "aws_subnet" "public_b" {
    vpc_id = aws_vpc.main.id

    cidr_block = "10.0.2.0/24"

    map_public_ip_on_launch = true

    // zone de dispobilite
    availability_zone = "us-east-1b"

    tags = {
      Name = "wali-public-subnet-b"
    }
}


# ============================================================
# 5. SUBNET PRIVATE - AZ A
# ============================================================

# Ce subnet est destiné aux ressources qui ne doivent pas être
# directement accessibles depuis Internet.
#
# Exemple :
# - workloads EKS
# - services backend
# - éventuellement RDS

resource "aws_subnet" "private_a" {
  vpc_id = aws_vpc.main.id

  cidr_block = "10.0.10.0/24"

  # Pas d'adresse IP publique automatique.
  map_public_ip_on_launch = false

  // zone de dispobilite
  availability_zone = "us-east-1a"

  tags = {
    Name = "wali-private-subnet-a"
  }
}


# ============================================================
# 6. SUBNET PRIVATE - AZ B
# ============================================================

# Deuxième subnet privé dans une autre Availability Zone.

resource "aws_subnet" "private_b" {
  vpc_id = aws_vpc.main.id

  cidr_block = "10.0.11.0/24"

  map_public_ip_on_launch = false

  // zone de dispobilite
  availability_zone = "us-east-1b"

  tags = {
    Name = "wali-private-subnet-b"
  }
}


# ============================================================
# 7. INTERNET GATEWAY
# ============================================================

# L'Internet Gateway permet à la VPC de communiquer avec Internet.

resource "aws_internet_gateway" "igw" {
  vpc_id = aws_vpc.main.id

  tags = {
    Name = "wali-igw"
  }
}


# ============================================================
# 8. ROUTE TABLE PUBLIQUE
# ============================================================

# Une route table contient les règles qui déterminent où le trafic
# réseau doit être envoyé.

resource "aws_route_table" "public_rt" {
  vpc_id = aws_vpc.main.id

  # 0.0.0.0/0 signifie :
  # "toute destination Internet".
  #
  # Le trafic Internet sortant passe par l'Internet Gateway.

  route {
    cidr_block = "0.0.0.0/0"

    gateway_id = aws_internet_gateway.igw.id
  }

  tags = {
    Name = "wali-public-route-table"
  }
}


# ============================================================
# 9. ASSOCIATION ROUTE TABLE → PUBLIC SUBNET A
# ============================================================

# On associe la route table publique au premier subnet public.

resource "aws_route_table_association" "public_a" {
  subnet_id = aws_subnet.public_a.id

  route_table_id = aws_route_table.public_rt.id
}


# ============================================================
# 10. ASSOCIATION ROUTE TABLE → PUBLIC SUBNET B
# ============================================================

# Même route table pour le deuxième subnet public.

resource "aws_route_table_association" "public_b" {
  subnet_id = aws_subnet.public_b.id

  route_table_id = aws_route_table.public_rt.id
}


# ============================================================
# 11. EKS
# ============================================================

# Ici nous utilisons un module Terraform officiel/populaire
# de la communauté Terraform AWS.
#
# Le module simplifie énormément la création d'un cluster EKS.
#
# Terraform va utiliser les informations fournies ici pour
# construire l'infrastructure nécessaire au cluster.

module "eks" {
  source  = "terraform-aws-modules/eks/aws"
  version = "~> 21.0"

  # Nom du cluster EKS.
  name = "wali-eks"

  # Version de Kubernetes utilisée par le cluster.
  kubernetes_version = "1.33"

  # L'API Kubernetes sera accessible depuis Internet.
  #
  # C'est pratique pour un environnement de développement,
  # mais il faudra réfléchir à la sécurité pour la production.
  endpoint_public_access = true

  # Donne automatiquement à l'utilisateur qui exécute Terraform
  # les permissions administrateur sur le cluster.
  enable_cluster_creator_admin_permissions = true

  # Configuration du compute utilisé par EKS.
  compute_config = {
    enabled = true

    node_pools = [
    "general-purpose"
    ]
  }

  # IMPORTANT :
  # On ne met pas ici un ID VPC fictif.
  #
  # On réutilise directement la VPC créée plus haut par Terraform.

  vpc_id = aws_vpc.main.id

  # Pour EKS, nous utilisons les subnets privés.
  #
  # Terraform connaît automatiquement leurs IDs grâce aux
  # références aws_subnet.private_a.id / private_b.id.

  subnet_ids = [
    aws_subnet.private_a.id,
    aws_subnet.private_b.id
  ]

  tags = {
    Environment = "dev"
    Terraform   = "true"
  }
}


# ============================================================
# 12. ROUTE 53
# ============================================================

# ATTENTION :
#
# Un record Route 53 doit appartenir à une Hosted Zone.
#
# Dans ton ancien Terraform, tu faisais référence à :
#
# aws_route53_zone.primary.zone_id
#
# mais cette Hosted Zone n'était jamais créée.
#
# Pour cette raison, nous ne créons pas encore le record ici.
#
# Quand tu auras ton vrai domaine et ta Hosted Zone Route 53,
# tu pourras créer le record DNS et le faire pointer vers ton
# Load Balancer.
#
# Le flux final sera alors :
#
# Utilisateur
#      ↓
# Route 53
#      ↓
# Application Load Balancer
#      ↓
# Frontend / API Gateway
#      ↓
# EKS