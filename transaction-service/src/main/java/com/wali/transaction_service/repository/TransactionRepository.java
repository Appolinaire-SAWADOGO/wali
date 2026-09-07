package com.wali.transaction_service.repository;

import com.wali.transaction_service.entities.TransactionEntity;
import com.wali.transaction_service.enums.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {
     /* Toute les methodes avec le champ  id (findById , deleteById etc...)
      * sont deja fournit par defaut il faut pas les recree si non ca vas causer une erreur
      */

    @Query("SELECT t FROM TransactionEntity t WHERE t.senderWalletId = :walletId OR t.receiverWalletId = :walletId")
    List<TransactionEntity> findAllByWalletId(@Param("walletId") UUID walletId);

    @Modifying
    @Query("UPDATE TransactionEntity t SET t.status = :status WHERE t.id = :id")
    void updateStatusById(@Param("id") UUID id,  @Param("status") TransactionStatus status);
}