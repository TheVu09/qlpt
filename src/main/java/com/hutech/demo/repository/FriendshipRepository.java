package com.hutech.demo.repository;

import com.hutech.demo.model.Friendship;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FriendshipRepository extends MongoRepository<Friendship, String> {
    
    // Tìm friendship giữa 2 users (bất kể ai là requester/receiver)
    @Query("{ $or: [ " +
           "{ 'requester.$id': ?0, 'receiver.$id': ?1 }, " +
           "{ 'requester.$id': ?1, 'receiver.$id': ?0 } " +
           "] }")
    Optional<Friendship> findByUsers(String userId1, String userId2);
    
    // Tìm tất cả friendships của user (đã accept)
    List<Friendship> findByStatusAndRequesterId(String status, String requesterId);
    List<Friendship> findByStatusAndReceiverId(String status, String receiverId);
}

