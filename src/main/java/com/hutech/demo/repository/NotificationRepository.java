package com.hutech.demo.repository;

import com.hutech.demo.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {
    
    // Lấy tất cả notifications của user (sắp xếp mới nhất trước)
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(String recipientId);
    
    // Lấy notifications chưa đọc của user
    List<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(String recipientId);
    
    // Đếm số notifications chưa đọc
    long countByRecipientIdAndIsReadFalse(String recipientId);
    
    // Lấy notifications theo type
    List<Notification> findByRecipientIdAndTypeOrderByCreatedAtDesc(String recipientId, String type);
}

