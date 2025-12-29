package com.hutech.demo.repository;

import com.hutech.demo.model.Like;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends MongoRepository<Like, String> {
    
    // Tìm like của một user cho một đối tượng cụ thể
    Optional<Like> findByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    
    // Kiểm tra user đã like chưa
    boolean existsByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    
    // Đếm số lượng like của một đối tượng
    long countByTargetIdAndTargetType(String targetId, String targetType);
    
    // Lấy danh sách user đã like một đối tượng
    List<Like> findByTargetIdAndTargetType(String targetId, String targetType);
    
    // Xóa like của user
    void deleteByUserIdAndTargetIdAndTargetType(String userId, String targetId, String targetType);
    
    // Xóa tất cả like của một đối tượng
    void deleteByTargetIdAndTargetType(String targetId, String targetType);
}

