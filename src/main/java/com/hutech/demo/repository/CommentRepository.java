package com.hutech.demo.repository;

import com.hutech.demo.model.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends MongoRepository<Comment, String> {
    
    // Tìm tất cả comment của một bài viết (chỉ comment gốc, không bao gồm reply)
    List<Comment> findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(String postId);
    
    // Tìm tất cả reply của một comment
    List<Comment> findByParentCommentIdOrderByCreatedAtAsc(String parentCommentId);
    
    // Tìm comment theo tác giả
    List<Comment> findByAuthorIdOrderByCreatedAtDesc(String authorId);
    
    // Đếm số comment của một bài viết (bao gồm cả reply)
    long countByPostId(String postId);
    
    // Đếm số comment của một user
    long countByAuthorId(String authorId);
    
    // Xóa tất cả comment của một bài viết
    void deleteByPostId(String postId);
}

