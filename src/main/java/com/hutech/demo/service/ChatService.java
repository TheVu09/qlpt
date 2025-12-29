package com.hutech.demo.service;

import com.hutech.demo.model.Chat;
import com.hutech.demo.model.Motel;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.ChatRepository;
import com.hutech.demo.repository.MotelRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MotelRepository motelRepository;

    @Autowired
    private com.hutech.demo.repository.RoomRepository roomRepository;

    // Tạo hoặc lấy private chat giữa 2 users
    public Chat getOrCreatePrivateChat(String userId1, String userId2) {
        User user1 = userRepository.findById(userId1)
                .orElseThrow(() -> new RuntimeException("User 1 không tồn tại"));
        User user2 = userRepository.findById(userId2)
                .orElseThrow(() -> new RuntimeException("User 2 không tồn tại"));

        // Tìm chat đã tồn tại
        List<String> participantIds = new ArrayList<>();
        participantIds.add(userId1);
        participantIds.add(userId2);
        
        List<Chat> existingChats = chatRepository.findByChatTypeAndParticipantsId("private", userId1);
        for (Chat chat : existingChats) {
            if (chat.getParticipants() != null && chat.getParticipants().size() == 2) {
                List<String> chatParticipantIds = chat.getParticipants().stream()
                        .map(User::getId)
                        .collect(Collectors.toList());
                if (chatParticipantIds.contains(userId1) && chatParticipantIds.contains(userId2)) {
                    return chat;
                }
            }
        }

        // Tạo chat mới
        Chat chat = Chat.builder()
                .chatType("private")
                .participants(List.of(user1, user2))
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return chatRepository.save(chat);
    }

    // Tạo hoặc lấy room chat
    public Chat getOrCreateRoomChat(String roomId) {
        // Kiểm tra room có tồn tại không
        com.hutech.demo.model.Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        // Tìm chat đã tồn tại
        return chatRepository.findByChatTypeAndRoomId("room", roomId)
                .orElseGet(() -> {
                    Chat chat = Chat.builder()
                            .chatType("room")
                            .roomId(roomId)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return chatRepository.save(chat);
                });
    }

    // Tạo hoặc lấy group chat (motel)
    public Chat getOrCreateGroupChat(String motelId) {
        // Kiểm tra motel có tồn tại không
        Motel motel = motelRepository.findById(motelId)
                .orElseThrow(() -> new RuntimeException("Khu trọ không tồn tại"));

        // Tìm chat đã tồn tại
        return chatRepository.findByChatTypeAndMotelId("group", motelId)
                .orElseGet(() -> {
                    Chat chat = Chat.builder()
                            .chatType("group")
                            .motelId(motelId)
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build();
                    return chatRepository.save(chat);
                });
    }

    // Lấy chat theo ID
    public Chat getChatById(String chatId) {
        return chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat không tồn tại"));
    }

    // Lấy tất cả private chats của user
    public List<Chat> getPrivateChatsByUser(String userId) {
        return chatRepository.findByChatTypeAndParticipantsId("private", userId);
    }

    // Lấy room chat của user
    public Chat getRoomChatByUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        if (user.getCurrentRoomId() == null || user.getCurrentRoomId().isEmpty()) {
            return null;
        }

        return chatRepository.findByChatTypeAndRoomId("room", user.getCurrentRoomId())
                .orElse(null);
    }

    // Lấy group chat của motel
    public Chat getGroupChatByMotel(String motelId) {
        return chatRepository.findByChatTypeAndMotelId("group", motelId)
                .orElse(null);
    }

    // Cập nhật lastMessageAt
    public Chat updateLastMessageAt(String chatId) {
        Chat chat = getChatById(chatId);
        chat.setLastMessageAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());
        return chatRepository.save(chat);
    }

    // Convert Chat to ChatResponse với populated data
    public com.hutech.demo.dto.ChatResponse toChatResponse(Chat chat) {
        com.hutech.demo.dto.ChatResponse.ChatResponseBuilder builder = com.hutech.demo.dto.ChatResponse.builder()
                .id(chat.getId())
                .chatType(chat.getChatType())
                .createdAt(chat.getCreatedAt())
                .updatedAt(chat.getUpdatedAt())
                .lastMessageAt(chat.getLastMessageAt());

        if (chat.isPrivateChat() && chat.getParticipants() != null) {
            builder.participants(chat.getParticipants().stream()
                    .map(com.hutech.demo.dto.UserInfo::fromUser)
                    .collect(Collectors.toList()));
        }

        if (chat.isRoomChat() && chat.getRoomId() != null) {
            builder.roomId(chat.getRoomId());
            try {
                com.hutech.demo.model.Room room = roomRepository.findById(chat.getRoomId()).orElse(null);
                if (room != null) {
                    builder.roomNumber(room.getRoomNumber());
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        if (chat.isGroupChat() && chat.getMotelId() != null) {
            builder.motelId(chat.getMotelId());
            try {
                Motel motel = motelRepository.findById(chat.getMotelId()).orElse(null);
                if (motel != null) {
                    builder.motelName(motel.getName());
                }
            } catch (Exception e) {
                // Ignore
            }
        }

        return builder.build();
    }
}

