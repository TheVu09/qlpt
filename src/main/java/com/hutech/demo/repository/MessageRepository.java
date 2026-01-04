package com.hutech.demo.repository;

import com.hutech.demo.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    // Tìm tin nhắn theo chat
    List<Message> findByChatIdOrderByCreatedAtAsc(String chatId);
    
    List<Message> findByChatIdOrderByCreatedAtDesc(String chatId);
    
    // Đếm tin nhắn chưa đọc trong chat (không phải của sender)
    // Note: Query với DBRef không hoạt động tốt, sử dụng logic trong service layer thay thế
    // @Query("{ 'chat.$id': ?0, 'sender.$id': { $ne: ?1 }, 'isRead': false }")
    // long countByChatIdAndSenderNotAndIsReadFalse(String chatId, String senderId);
    
    // Tìm tin nhắn chưa đọc trong chat (không phải của sender)
    // Note: Query với DBRef không hoạt động tốt, sử dụng logic trong service layer thay thế
    // @Query("{ 'chat.$id': ?0, 'sender.$id': { $ne: ?1 }, 'isRead': false }")
    // List<Message> findByChatIdAndSenderNotAndIsReadFalse(String chatId, String senderId);
    
    // Legacy methods đã được migrate sang query qua Chat
    // Các methods này không còn hoạt động vì Message không còn các field tương ứng
}
