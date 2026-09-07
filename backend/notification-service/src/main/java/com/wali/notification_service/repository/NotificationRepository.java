package com.wali.notification_service.repository;

import com.wali.notification_service.entities.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
     /* Toute les methodes avec le champ  id (findById , deleteById etc...)
      * sont deja fournit par defaut il faut pas les recree si non ca vas causer une erreur
      */

    @Query("SELECT t FROM NotificationEntity t WHERE t.walletId = :walletId")
    List<NotificationEntity> findAllByWalletId(@Param("walletId") UUID walletId);

}