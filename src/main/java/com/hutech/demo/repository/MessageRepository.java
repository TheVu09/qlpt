package com.hutech.demo.repository;

import com.hutech.demo.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    // Tìm tin nhắn theo chat
    List<Message> findByChatIdOrderByCreatedAtAsc(String chatId);
    
    List<Message> findByChatIdOrderByCreatedAtDesc(String chatId);
    
    // Legacy methods đã được migrate sang query qua Chat
    // Các methods này không còn hoạt động vì Message không còn các field tương ứng
}
