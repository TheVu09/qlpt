package com.hutech.demo.service;

import com.hutech.demo.model.Message;
import com.hutech.demo.model.Motel;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.MessageRepository;
import com.hutech.demo.repository.MotelRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MotelRepository motelRepository;

    // Gửi tin nhắn private (1-1)
    public Message sendPrivateMessage(String senderId, String receiverId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new RuntimeException("Người nhận không tồn tại"));

        Message message = Message.builder()
                .sender(sender)
                .receiver(receiver)
                .content(content)
                .messageType("private")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    // Gửi tin nhắn nhóm trong khu trọ
    public Message sendGroupMessage(String senderId, String motelId, String content) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new RuntimeException("Người gửi không tồn tại"));

        Motel motel = motelRepository.findById(motelId)
                .orElseThrow(() -> new RuntimeException("Khu trọ không tồn tại"));

        Message message = Message.builder()
                .sender(sender)
                .motel(motel)
                .content(content)
                .messageType("group")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    // Gửi thông báo từ admin
    public Message sendAnnouncement(String adminId, String motelId, String content) {
        User admin = userRepository.findById(adminId)
                .orElseThrow(() -> new RuntimeException("Admin không tồn tại"));

        if (!admin.getRole().equals("ROLE_ADMIN") && !admin.getRole().equals("ROLE_LANDLORD")) {
            throw new RuntimeException("Chỉ admin hoặc chủ nhà mới có thể gửi thông báo");
        }

        Motel motel = motelRepository.findById(motelId)
                .orElseThrow(() -> new RuntimeException("Khu trọ không tồn tại"));

        Message message = Message.builder()
                .sender(admin)
                .motel(motel)
                .content(content)
                .messageType("announcement")
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    // Lấy tin nhắn trong khu trọ
    public List<Message> getMotelMessages(String motelId) {
        return messageRepository.findByMotelIdOrderByCreatedAtDesc(motelId);
    }

    // Lấy tin nhắn chat private giữa 2 người
    public List<Message> getPrivateMessages(String userId1, String userId2) {
        List<Message> messages1 = messageRepository.findBySenderIdAndReceiverIdOrderByCreatedAtDesc(userId1, userId2);
        List<Message> messages2 = messageRepository.findBySenderIdAndReceiverIdOrderByCreatedAtDesc(userId2, userId1);

        messages1.addAll(messages2);
        messages1.sort((m1, m2) -> m2.getCreatedAt().compareTo(m1.getCreatedAt()));

        return messages1;
    }

    // Lấy tin nhắn chưa đọc
    public List<Message> getUnreadMessages(String userId) {
        return messageRepository.findByReceiverIdAndIsReadFalse(userId);
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

    // Lấy thông báo trong khu trọ
    public List<Message> getMotelAnnouncements(String motelId) {
        return messageRepository.findByMotelIdAndMessageType(motelId, "announcement");
    }

    // Lấy tin nhắn group trong khu trọ
    public List<Message> getMotelGroupMessages(String motelId) {
        return messageRepository.findByMotelIdAndMessageType(motelId, "group");
    }

    // Đếm số tin nhắn chưa đọc
    public long countUnreadMessages(String userId) {
        return getUnreadMessages(userId).size();
    }
}
