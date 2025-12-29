package com.hutech.demo.service;

import com.hutech.demo.dto.LikeRequest;
import com.hutech.demo.model.Comment;
import com.hutech.demo.model.Like;
import com.hutech.demo.model.Post;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.CommentRepository;
import com.hutech.demo.repository.LikeRepository;
import com.hutech.demo.repository.PostRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class LikeService {

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    // Toggle like (like hoặc unlike)
    @Transactional
    public boolean toggleLike(LikeRequest request, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        String targetId = request.getTargetId();
        String targetType = request.getTargetType();

        // Kiểm tra targetType hợp lệ
        if (!targetType.equals("post") && !targetType.equals("comment")) {
            throw new RuntimeException("Loại đối tượng không hợp lệ");
        }

        // Kiểm tra đối tượng có tồn tại không
        if (targetType.equals("post")) {
            postRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));
        } else {
            commentRepository.findById(targetId)
                    .orElseThrow(() -> new RuntimeException("Comment không tồn tại"));
        }

        // Kiểm tra user đã like chưa
        Optional<Like> existingLike = likeRepository
                .findByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

        if (existingLike.isPresent()) {
            // Đã like -> Unlike
            likeRepository.deleteByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);

            // Giảm like count
            if (targetType.equals("post")) {
                Post post = postRepository.findById(targetId).get();
                post.decrementLikeCount();
                postRepository.save(post);
            } else {
                Comment comment = commentRepository.findById(targetId).get();
                comment.decrementLikeCount();
                commentRepository.save(comment);
            }

            return false; // Unlike
        } else {
            // Chưa like -> Like
            Like like = Like.builder()
                    .user(user)
                    .targetId(targetId)
                    .targetType(targetType)
                    .createdAt(LocalDateTime.now())
                    .build();

            likeRepository.save(like);

            // Tăng like count
            if (targetType.equals("post")) {
                Post post = postRepository.findById(targetId).get();
                post.incrementLikeCount();
                postRepository.save(post);
            } else {
                Comment comment = commentRepository.findById(targetId).get();
                comment.incrementLikeCount();
                commentRepository.save(comment);
            }

            return true; // Like
        }
    }

    // Kiểm tra user đã like chưa
    public boolean isLiked(String userId, String targetId, String targetType) {
        return likeRepository.existsByUserIdAndTargetIdAndTargetType(userId, targetId, targetType);
    }

    // Đếm số lượng like
    public long getLikeCount(String targetId, String targetType) {
        return likeRepository.countByTargetIdAndTargetType(targetId, targetType);
    }
}

