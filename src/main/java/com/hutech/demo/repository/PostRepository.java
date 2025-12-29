package com.hutech.demo.repository;

import com.hutech.demo.model.Post;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends MongoRepository<Post, String> {
    
    // Tìm tất cả bài viết, sắp xếp theo thời gian tạo giảm dần
    List<Post> findAllByOrderByCreatedAtDesc();
    
    // Tìm tất cả bài viết theo khu trọ, sắp xếp theo thời gian tạo giảm dần
    List<Post> findByMotelIdOrderByCreatedAtDesc(String motelId);
    
    // Tìm bài viết theo tác giả
    List<Post> findByAuthorIdOrderByCreatedAtDesc(String authorId);
    
    // Tìm bài viết được pin theo khu trọ
    List<Post> findByMotelIdAndIsPinnedTrueOrderByCreatedAtDesc(String motelId);
    
    // Tìm bài viết theo loại
    List<Post> findByMotelIdAndPostTypeOrderByCreatedAtDesc(String motelId, String postType);
    
    // Đếm số bài viết trong khu trọ
    long countByMotelId(String motelId);
    
    // Đếm số bài viết của một user
    long countByAuthorId(String authorId);
}

