package com.app.web.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.app.web.entities.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {


}
