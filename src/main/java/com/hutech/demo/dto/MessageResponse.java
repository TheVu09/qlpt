package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {
    private String id;
    private String chatId; // Chat mà message này thuộc về
    
    // Sender info (populated)
    private String senderId;
    private String senderName;
    private String senderEmail;
    private String senderAvatar;
    
    // Receiver info (populated, null for group messages)
    private String receiverId;
    private String receiverName;
    private String receiverEmail;
    private String receiverAvatar;
    
    // Motel info (populated, null for private messages)
    private String motelId;
    private String motelName;
    
    // Room info (populated, null if not room group message)
    private String roomId;
    private String roomNumber;
    
    private String content;
    private String messageType; // private, group, room_group, announcement
    private boolean isRead;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

