package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "notifications")
public class Notification {
    
    @Id
    private String id;
    
    @DBRef
    private User recipient; // Người nhận thông báo
    
    @DBRef
    private User sender; // Người gây ra thông báo (có thể null cho system notification)
    
    private String type; // FRIEND_REQUEST, FRIEND_ACCEPT, APPLICATION_NEW, APPLICATION_APPROVED, APPLICATION_REJECTED, MESSAGE
    
    private String title; // Tiêu đề thông báo
    
    private String content; // Nội dung thông báo
    
    private String relatedId; // ID liên quan (friendshipId, applicationId, etc.)
    
    private String relatedType; // friendship, application, message, etc.
    
    private String actionUrl; // URL để navigate khi click vào notification
    
    private boolean isRead; // Đã đọc chưa
    
    private LocalDateTime createdAt;
    
    private LocalDateTime readAt; // Thời gian đọc
    
    // Helper methods
    public boolean isUnread() {
        return !isRead;
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
}

