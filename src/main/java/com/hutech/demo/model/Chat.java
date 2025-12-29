package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "chats")
public class Chat {
    
    @Id
    private String id;
    
    private String chatType; // private, room, group
    
    // For private chat (1vs1)
    @DBRef
    private List<User> participants = new ArrayList<>(); // 2 users for private chat
    
    // For room chat
    private String roomId; // Room ID for room chat
    
    // For group chat (motel)
    private String motelId; // Motel ID for group chat
    
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    private LocalDateTime lastMessageAt; // Thời gian tin nhắn cuối cùng
    
    // Helper methods
    public boolean isPrivateChat() {
        return "private".equals(chatType);
    }
    
    public boolean isRoomChat() {
        return "room".equals(chatType);
    }
    
    public boolean isGroupChat() {
        return "group".equals(chatType);
    }
}

