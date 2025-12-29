package com.hutech.demo.service;

import com.hutech.demo.dto.MessageResponse;
import com.hutech.demo.model.Message;
import com.hutech.demo.model.Motel;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.MessageRepository;
import com.hutech.demo.repository.MotelRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MotelRepository motelRepository;

    @Autowired
    private com.hutech.demo.repository.RoomRepository roomRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private com.hutech.demo.config.WebSocketEventListener webSocketEventListener;

    @Autowired
    private com.hutech.demo.service.RoomService roomService;

    @Autowired
    private ChatService chatService;

    // Gửi tin nhắn private (1-1)
    public Message sendPrivateMessage(String senderId, String receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Người nhận không tồn tại"));

        // Lấy hoặc tạo private chat
        com.hutech.demo.model.Chat chat = chatService.getOrCreatePrivateChat(senderId, receiverId);

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // Cập nhật lastMessageAt
        chatService.updateLastMessageAt(chat.getId());
        
        // Gửi message qua WebSocket cho receiver
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "PRIVATE_MESSAGE");
        wsMessage.put("id", savedMessage.getId());
        wsMessage.put("chatId", chat.getId());
        wsMessage.put("content", savedMessage.getContent());
        wsMessage.put("senderId", sender.getId());
        wsMessage.put("senderName", sender.getFullName());
        wsMessage.put("senderEmail", sender.getEmail());
        wsMessage.put("senderAvatar", sender.getAvatar());
        wsMessage.put("receiverId", receiver.getId());
        wsMessage.put("createdAt", savedMessage.getCreatedAt().toString());
        wsMessage.put("timestamp", System.currentTimeMillis());
        
        // Emit tới user rooms
        System.out.println("Sending private message to receiver: " + receiverId);
        messagingTemplate.convertAndSend("/topic/user." + receiverId, wsMessage);
        
        // Gửi lại cho sender để hiển thị trong chat của họ
        System.out.println("Sending private message to sender: " + senderId);
        messagingTemplate.convertAndSend("/topic/user." + senderId, wsMessage);

        return savedMessage;
    }

    // Gửi tin nhắn nhóm trong khu trọ
    public Message sendGroupMessage(String senderId, String motelId, String content) {
        if (motelId == null || motelId.isEmpty()) {
            throw new RuntimeException("Motel ID không được để trống");
        }
        
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        // Lấy hoặc tạo group chat
        com.hutech.demo.model.Chat chat = chatService.getOrCreateGroupChat(motelId);

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // Cập nhật lastMessageAt
        chatService.updateLastMessageAt(chat.getId());
        
        // Lấy tất cả rooms trong motel (vì motel là DBRef, cần query khác)
        List<com.hutech.demo.model.Room> roomsInMotel = new java.util.ArrayList<>();
        try {
            // Lấy motel object để query
            Motel motelObj = motelRepository.findById(motelId).orElse(null);
            if (motelObj != null) {
                // Query rooms có motel reference = motelObj
                List<com.hutech.demo.model.Room> allRooms = roomRepository.findAll();
                for (com.hutech.demo.model.Room room : allRooms) {
                    if (room.getMotel() != null && room.getMotel().getId() != null && 
                        room.getMotel().getId().equals(motelId)) {
                        roomsInMotel.add(room);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding rooms in motel " + motelId + ": " + e.getMessage());
            e.printStackTrace();
        }
        
        System.out.println("Found " + roomsInMotel.size() + " rooms in motel " + motelId);
        
        // Lấy tất cả tenants từ các rooms trong motel
        java.util.Set<String> userIdsInMotel = new java.util.HashSet<>();
        for (com.hutech.demo.model.Room room : roomsInMotel) {
            if (room.getTenants() != null) {
                for (User tenant : room.getTenants()) {
                    if (tenant != null && tenant.getId() != null) {
                        userIdsInMotel.add(tenant.getId());
                    }
                }
            }
        }
        
        // Đảm bảo sender cũng được thêm vào
        userIdsInMotel.add(senderId);
        
        System.out.println("Users in motel " + motelId + ": " + userIdsInMotel.size() + " users");
        
        // Gửi message qua WebSocket tới từng user trong motel
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "GROUP_MESSAGE");
        wsMessage.put("id", savedMessage.getId());
        wsMessage.put("chatId", chat.getId());
        wsMessage.put("content", savedMessage.getContent());
        wsMessage.put("senderId", sender.getId());
        wsMessage.put("senderName", sender.getFullName());
        wsMessage.put("senderEmail", sender.getEmail());
        wsMessage.put("senderAvatar", sender.getAvatar());
        wsMessage.put("motelId", motelId);
        wsMessage.put("createdAt", savedMessage.getCreatedAt().toString());
        wsMessage.put("timestamp", System.currentTimeMillis());
        
        // Emit tới từng user trong motel
        int sentCount = 0;
        for (String userId : userIdsInMotel) {
            if (webSocketEventListener.isUserOnline(userId)) {
                messagingTemplate.convertAndSend("/topic/user." + userId, wsMessage);
                sentCount++;
                System.out.println("Sent motel message to user: " + userId);
            }
        }
        System.out.println("Total sent motel messages: " + sentCount + " / " + userIdsInMotel.size());

        return savedMessage;
    }

    // Gửi thông báo từ admin
    public Message sendAnnouncement(String adminId, String motelId, String content) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

        if (!admin.getRole().equals("ROLE_ADMIN") && !admin.getRole().equals("ROLE_LANDLORD")) {
            throw new RuntimeException("Chỉ admin hoặc chủ nhà mới có thể gửi thông báo");
        }

        // Lấy hoặc tạo group chat
        com.hutech.demo.model.Chat chat = chatService.getOrCreateGroupChat(motelId);

        Message message = Message.builder()
                .chat(chat)
                .sender(admin)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // Cập nhật lastMessageAt
        chatService.updateLastMessageAt(chat.getId());
        
        // Gửi message qua WebSocket tới tất cả users online
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "ANNOUNCEMENT");
        wsMessage.put("id", savedMessage.getId());
        wsMessage.put("chatId", chat.getId());
        wsMessage.put("title", "Thông báo từ Admin");
        wsMessage.put("content", savedMessage.getContent());
        wsMessage.put("senderId", admin.getId());
        wsMessage.put("senderName", admin.getFullName());
        wsMessage.put("createdAt", savedMessage.getCreatedAt().toString());
        wsMessage.put("timestamp", System.currentTimeMillis());

        for (String userId : webSocketEventListener.getOnlineUsers()) {
            messagingTemplate.convertAndSend("/topic/user." + userId, wsMessage);
        }

        return savedMessage;
    }

    // Lấy tin nhắn trong khu trọ (legacy - query qua Chat)
    public List<Message> getMotelMessages(String motelId) {
        // Tìm chat của motel
        com.hutech.demo.model.Chat chat = chatService.getGroupChatByMotel(motelId);
        if (chat == null) {
            return new java.util.ArrayList<>();
        }
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    // Lấy tin nhắn chat private giữa 2 người (query qua Chat)
    public List<Message> getPrivateMessages(String userId1, String userId2) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreatePrivateChat(userId1, userId2);
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    // Lấy tin nhắn chat private giữa 2 người với populated data
    public List<MessageResponse> getPrivateMessagesWithDetails(String userId1, String userId2) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreatePrivateChat(userId1, userId2);
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy tin nhắn chưa đọc (cần refactor để query qua Chat)
    public List<Message> getUnreadMessages(String userId) {
        // TODO: Refactor để query qua Chat và participants
        // Tạm thời trả về empty list vì không có receiver field nữa
        return new java.util.ArrayList<>();
    }

    // Đánh dấu tin nhắn đã đọc
    public Message markAsRead(String messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Tin nhắn không tồn tại"));

        message.setRead(true);
        message.setUpdatedAt(LocalDateTime.now());

        return messageRepository.save(message);
    }

    // Đánh dấu tất cả tin nhắn đã đọc
    public void markAllAsRead(String userId) {
        List<Message> unreadMessages = getUnreadMessages(userId);
        unreadMessages.forEach(message -> {
            message.setRead(true);
            message.setUpdatedAt(LocalDateTime.now());
        });
        messageRepository.saveAll(unreadMessages);
    }

    // Xóa tin nhắn
    public void deleteMessage(String messageId, String userId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Tin nhắn không tồn tại"));

        if (!message.getSender().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa tin nhắn này");
        }

        messageRepository.deleteById(messageId);
    }

    // Lấy thông báo trong khu trọ (legacy - query qua Chat)
    public List<Message> getMotelAnnouncements(String motelId) {
        com.hutech.demo.model.Chat chat = chatService.getGroupChatByMotel(motelId);
        if (chat == null) {
            return new java.util.ArrayList<>();
        }
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    // Lấy tin nhắn theo chatId
    public List<MessageResponse> getMessagesByChatId(String chatId) {
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Legacy methods (deprecated - query qua Chat)
    public List<Message> getMotelGroupMessages(String motelId) {
        com.hutech.demo.model.Chat chat = chatService.getGroupChatByMotel(motelId);
        if (chat == null) {
            return new java.util.ArrayList<>();
        }
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    public List<MessageResponse> getMotelGroupMessagesWithDetails(String motelId) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreateGroupChat(motelId);
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Đếm số tin nhắn chưa đọc
    public long countUnreadMessages(String userId) {
        return getUnreadMessages(userId).size();
    }

    // Gửi tin nhắn nhóm trong phòng trọ (chỉ người trong phòng)
    public Message sendRoomGroupMessage(String senderId, String roomId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        com.hutech.demo.model.Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng trọ không tồn tại"));

        // Kiểm tra người gửi có phải là người thuê phòng này không
        boolean isTenant = room.getTenants() != null && room.getTenants().stream()
                .anyMatch(tenant -> tenant != null && tenant.getId() != null && tenant.getId().equals(senderId));

        if (!isTenant) {
            throw new RuntimeException("Bạn không phải là người thuê phòng này");
        }

        // Lấy hoặc tạo room chat
        com.hutech.demo.model.Chat chat = chatService.getOrCreateRoomChat(roomId);

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(content)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Message savedMessage = messageRepository.save(message);
        
        // Cập nhật lastMessageAt
        chatService.updateLastMessageAt(chat.getId());
        
        // Lấy danh sách tenants trong room
        List<User> tenants = room.getTenants() != null ? room.getTenants() : new java.util.ArrayList<>();
        
        // Tạo set để đảm bảo không trùng lặp và bao gồm cả sender
        java.util.Set<String> userIdsInRoom = new java.util.HashSet<>();
        for (User tenant : tenants) {
            if (tenant != null && tenant.getId() != null) {
                userIdsInRoom.add(tenant.getId());
            }
        }
        // Đảm bảo sender cũng được thêm vào
        userIdsInRoom.add(senderId);
        
        System.out.println("Users in room " + roomId + ": " + userIdsInRoom.size() + " users (tenants: " + tenants.size() + ")");
        
        // Gửi message qua WebSocket tới từng tenant trong room
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "ROOM_GROUP_MESSAGE");
        wsMessage.put("id", savedMessage.getId());
        wsMessage.put("chatId", chat.getId());
        wsMessage.put("content", savedMessage.getContent());
        wsMessage.put("senderId", sender.getId());
        wsMessage.put("senderName", sender.getFullName());
        wsMessage.put("senderEmail", sender.getEmail());
        wsMessage.put("senderAvatar", sender.getAvatar());
        wsMessage.put("roomId", roomId);
        wsMessage.put("roomNumber", room.getRoomNumber());
        wsMessage.put("createdAt", savedMessage.getCreatedAt().toString());
        wsMessage.put("timestamp", System.currentTimeMillis());
        
        // Emit tới từng user trong room (bao gồm cả offline để đảm bảo consistency)
        int sentCount = 0;
        for (String userId : userIdsInRoom) {
            messagingTemplate.convertAndSend("/topic/user." + userId, wsMessage);
            sentCount++;
            System.out.println("Sent room message to user: " + userId + " (online: " + webSocketEventListener.isUserOnline(userId) + ")");
        }
        System.out.println("Total sent room messages: " + sentCount + " / " + userIdsInRoom.size());

        return savedMessage;
    }

    // Lấy tin nhắn group trong phòng trọ (legacy - query qua Chat)
    public List<Message> getRoomGroupMessages(String roomId) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreateRoomChat(roomId);
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    // Lấy tin nhắn group trong phòng trọ với populated data
    public List<MessageResponse> getRoomGroupMessagesWithDetails(String roomId) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreateRoomChat(roomId);
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy tất cả tin nhắn trong phòng (legacy - query qua Chat)
    public List<Message> getRoomMessages(String roomId) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreateRoomChat(roomId);
        return messageRepository.findByChatIdOrderByCreatedAtDesc(chat.getId());
    }

    // Lấy tất cả tin nhắn trong phòng với populated data
    public List<MessageResponse> getRoomMessagesWithDetails(String roomId) {
        com.hutech.demo.model.Chat chat = chatService.getOrCreateRoomChat(roomId);
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chat.getId()).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Convert Message entity to MessageResponse DTO (with populated data)
    private MessageResponse convertToResponse(Message message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setRead(message.isRead());
        response.setCreatedAt(message.getCreatedAt());
        response.setUpdatedAt(message.getUpdatedAt());

        // Populate chat info
        if (message.getChat() != null) {
            com.hutech.demo.model.Chat chat = message.getChat();
            response.setChatId(chat.getId());
            response.setMessageType(chat.getChatType());
            
            // Populate based on chat type
            if (chat.isPrivateChat() && chat.getParticipants() != null && chat.getParticipants().size() == 2) {
                // For private chat, set receiver as the other participant
                User sender = message.getSender();
                if (sender != null) {
                    User receiver = chat.getParticipants().stream()
                            .filter(p -> p != null && !p.getId().equals(sender.getId()))
                            .findFirst()
                            .orElse(null);
                    if (receiver != null) {
                        response.setReceiverId(receiver.getId());
                        response.setReceiverName(receiver.getFullName());
                        response.setReceiverEmail(receiver.getEmail());
                        response.setReceiverAvatar(receiver.getAvatar());
                    }
                }
            }
            
            if (chat.isRoomChat() && chat.getRoomId() != null) {
                response.setRoomId(chat.getRoomId());
                // Có thể populate roomNumber từ roomId nếu cần
                try {
                    com.hutech.demo.model.Room room = roomRepository.findById(chat.getRoomId()).orElse(null);
                    if (room != null) {
                        response.setRoomNumber(room.getRoomNumber());
                        if (room.getMotel() != null) {
                            response.setMotelId(room.getMotel().getId());
                            response.setMotelName(room.getMotel().getName());
                        }
                    }
                } catch (Exception e) {
                    // Ignore if room not found
                }
            }
            
            if (chat.isGroupChat() && chat.getMotelId() != null) {
                response.setMotelId(chat.getMotelId());
                // Có thể populate motelName từ motelId nếu cần
                try {
                    Motel motel = motelRepository.findById(chat.getMotelId()).orElse(null);
                    if (motel != null) {
                        response.setMotelName(motel.getName());
                    }
                } catch (Exception e) {
                    // Ignore if motel not found
                }
            }
        }

        // Populate sender info
        if (message.getSender() != null) {
            User sender = message.getSender();
            response.setSenderId(sender.getId());
            response.setSenderName(sender.getFullName());
            response.setSenderEmail(sender.getEmail());
            response.setSenderAvatar(sender.getAvatar());
        }

        return response;
    }
}


