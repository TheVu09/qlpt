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
@Document(collection = "friendships")
public class Friendship {
    
    @Id
    private String id;
    
    @DBRef
    private User requester; // Người gửi lời mời kết bạn
    
    @DBRef
    private User receiver; // Người nhận lời mời kết bạn
    
    private String status; // pending, accepted, rejected, blocked
    
    private LocalDateTime createdAt; // Thời gian gửi lời mời
    
    private LocalDateTime updatedAt; // Thời gian cập nhật
    
    private LocalDateTime acceptedAt; // Thời gian chấp nhận kết bạn
    
    // Helper methods
    public boolean isPending() {
        return "pending".equals(status);
    }
    
    public boolean isAccepted() {
        return "accepted".equals(status);
    }
    
    public boolean isRejected() {
        return "rejected".equals(status);
    }
    
    public boolean isBlocked() {
        return "blocked".equals(status);
    }
    
    // Check if user is part of this friendship
    public boolean involvesUser(String userId) {
        return (requester != null && requester.getId().equals(userId)) ||
               (receiver != null && receiver.getId().equals(userId));
    }
    
    // Get the other user in this friendship
    public User getOtherUser(String userId) {
        if (requester != null && requester.getId().equals(userId)) {
            return receiver;
        } else if (receiver != null && receiver.getId().equals(userId)) {
            return requester;
        }
        return null;
    }
}

