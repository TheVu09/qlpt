package com.hutech.demo.repository;

import com.hutech.demo.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByMotelIdOrderByCreatedAtDesc(String motelId);

    List<Message> findBySenderIdAndReceiverIdOrderByCreatedAtDesc(String senderId, String receiverId);

    List<Message> findByReceiverIdAndIsReadFalse(String receiverId);

    List<Message> findByMotelIdAndMessageType(String motelId, String messageType);
}
