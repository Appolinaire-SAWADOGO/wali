package com.wali.auth_service.repository;

import com.wali.auth_service.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, UUID> {
     /* Toute les methodes avec le champ  id (findById , deleteById etc...)
      * sont deja fournit par defaut il faut pas les recree si non ca vas causer une erreur
      */

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
