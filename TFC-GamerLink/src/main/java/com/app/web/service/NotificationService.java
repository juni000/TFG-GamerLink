package com.app.web.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.app.web.entities.Notification;
import com.app.web.entities.User;
import com.app.web.enums.NotificationType;
import com.app.web.repositories.NotificationRepository;

import jakarta.persistence.EntityNotFoundException;
@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserService userService;

    public Notification createNotification(User recipient, String message, NotificationType type) {
        Notification notification = new Notification();
        notification.setRecipient(recipient);
        notification.setMessage(message);
        notification.setType(type);
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
        return notificationRepository.save(notification);
    }
    
    public List<Notification> getUserUnreadNotifications(User user) {
        return notificationRepository.findByRecipientAndIsReadFalseOrderByCreatedAtDesc(user);
    }
    
    public List<Notification> getUserAllNotifications(User user) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user);
    }
    
    public void markAsRead(Long notificationId, User user) {
        Notification notification = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new EntityNotFoundException("Notificación no encontrada"));
        
        if (!notification.getRecipient().equals(user)) {
            throw new SecurityException("No tienes permiso para modificar esta notificación");
        }
        
        notification.setRead(true);
        notificationRepository.save(notification);
    }
    
    public void markAllAsRead(User user) {
        List<Notification> unreadNotifications = notificationRepository.findByRecipientAndIsReadFalse(user);
        unreadNotifications.forEach(notification -> notification.setRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }
    
    public int getUnreadCount(User user) {
        return notificationRepository.countByRecipientAndIsReadFalse(user);
    }
}