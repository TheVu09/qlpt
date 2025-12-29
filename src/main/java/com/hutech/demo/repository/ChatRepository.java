package com.hutech.demo.repository;

import com.hutech.demo.model.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends MongoRepository<Chat, String> {
    // Tìm chat theo room
    Optional<Chat> findByChatTypeAndRoomId(String chatType, String roomId);
    
    // Tìm chat theo motel
    Optional<Chat> findByChatTypeAndMotelId(String chatType, String motelId);
    
    // Tìm tất cả chats của user (private chats) - participants chứa userId
    List<Chat> findByChatTypeAndParticipantsId(String chatType, String userId);
    
    // Tìm chat theo room
    List<Chat> findByRoomId(String roomId);
    
    // Tìm chat theo motel
    List<Chat> findByMotelId(String motelId);
}

