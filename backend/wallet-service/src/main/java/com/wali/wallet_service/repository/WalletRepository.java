package com.wali.wallet_service.repository;

import com.wali.wallet_service.entities.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
     /* Toute les methodes avec le champ  id (findById , deleteById etc...)
      * sont deja fournit par defaut il faut pas les recree si non ca vas causer une erreur
      */

    Optional<WalletEntity> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("UPDATE WalletEntity e SET e.balance = :balance WHERE e.id = :walletId")
    void  updateWalletBalanceByWalletId(UUID walletId, BigDecimal balance);
}