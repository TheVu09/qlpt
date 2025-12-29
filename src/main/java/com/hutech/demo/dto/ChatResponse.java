package com.hutech.demo.dto;

import com.hutech.demo.model.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String id;
    private String chatType; // private, room, group
    
    // For private chat
    private List<UserInfo> participants;
    
    // For room chat
    private String roomId;
    private String roomNumber;
    
    // For group chat
    private String motelId;
    private String motelName;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime lastMessageAt;
    
    // Last message preview
    private MessageResponse lastMessage;
    
    // Unread count (optional, for frontend)
    private Long unreadCount;

    public static ChatResponse fromChat(Chat chat) {
        ChatResponse.ChatResponseBuilder builder = ChatResponse.builder()
                .id(chat.getId())
                .chatType(chat.getChatType())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .lastMessageAt(chat.getLastMessageAt());

        if (chat.isPrivateChat() && chat.getParticipants() != null) {
            builder.participants(chat.getParticipants().stream()
                    .map(UserInfo::fromUser)
                    .collect(Collectors.toList()));
        }

        if (chat.isRoomChat() && chat.getRoomId() != null) {
            builder.roomId(chat.getRoomId());
            // Có thể populate roomNumber từ roomId nếu cần
        }

        if (chat.isGroupChat() && chat.getMotelId() != null) {
            builder.motelId(chat.getMotelId());
            // Có thể populate motelName từ motelId nếu cần
        }

        return builder.build();
    }
}

