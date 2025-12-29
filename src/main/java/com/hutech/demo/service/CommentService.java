package com.hutech.demo.service;

import com.hutech.demo.dto.CommentRequest;
import com.hutech.demo.dto.CommentResponse;
import com.hutech.demo.model.Comment;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LikeRepository likeRepository;

    // Tạo comment mới
    @Transactional
    public CommentResponse createComment(CommentRequest request, String userId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        Post post = postRepository.findById(request.getPostId())
                .orElseThrow(() -> new RuntimeException("Bài viết không tồn tại"));

        Comment parentComment = null;
        if (request.getParentCommentId() != null) {
            parentComment = commentRepository.findById(request.getParentCommentId())
                    .orElseThrow(() -> new RuntimeException("Comment cha không tồn tại"));
        }

        Comment comment = Comment.builder()
                .post(post)
                .author(author)
                .content(request.getContent())
                .parentComment(parentComment)
                .likeCount(0)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Tăng số lượng comment của bài viết
        post.incrementCommentCount();
        postRepository.save(post);

        return CommentResponse.fromComment(savedComment, false);
    }

    // Lấy tất cả comment của một bài viết (chỉ comment gốc, reply sẽ nằm trong)
    public List<CommentResponse> getCommentsByPost(String postId, String currentUserId) {
        List<Comment> comments = commentRepository
                .findByPostIdAndParentCommentIsNullOrderByCreatedAtAsc(postId);

        return comments.stream()
                .map(comment -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, comment.getId(), "comment");
                    
                    CommentResponse response = CommentResponse.fromComment(comment, isLiked);
                    
                    // Lấy tất cả reply của comment này
                    List<CommentResponse> replies = getRepliesByComment(comment.getId(), currentUserId);
                    response.setReplies(replies);
                    
                    return response;
                })
                .collect(Collectors.toList());
    }

    // Lấy tất cả reply của một comment
    public List<CommentResponse> getRepliesByComment(String commentId, String currentUserId) {
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);

        return replies.stream()
                .map(reply -> {
                    boolean isLiked = currentUserId != null && 
                            likeRepository.existsByUserIdAndTargetIdAndTargetType(
                                    currentUserId, reply.getId(), "comment");
                    return CommentResponse.fromComment(reply, isLiked);
                })
                .collect(Collectors.toList());
    }

    // Lấy comment theo ID
    public CommentResponse getCommentById(String commentId, String currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment không tồn tại"));

        boolean isLiked = currentUserId != null && 
                likeRepository.existsByUserIdAndTargetIdAndTargetType(
                        currentUserId, commentId, "comment");

        return CommentResponse.fromComment(comment, isLiked);
    }

    // Cập nhật comment
    public CommentResponse updateComment(String commentId, String content, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment không tồn tại"));

        // Kiểm tra quyền sở hữu
        if (!comment.getAuthor().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa comment này");
        }

        comment.setContent(content);
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);

        boolean isLiked = likeRepository.existsByUserIdAndTargetIdAndTargetType(
                userId, commentId, "comment");

        return CommentResponse.fromComment(updatedComment, isLiked);
    }

    // Xóa comment
    @Transactional
    public void deleteComment(String commentId, String userId, boolean isAdmin) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment không tồn tại"));

        // Kiểm tra quyền: chủ comment hoặc admin
        if (!comment.getAuthor().getId().equals(userId) && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xóa comment này");
        }

        // Xóa tất cả reply của comment này
        List<Comment> replies = commentRepository.findByParentCommentIdOrderByCreatedAtAsc(commentId);
        for (Comment reply : replies) {
            // Xóa likes của reply
            likeRepository.deleteByTargetIdAndTargetType(reply.getId(), "comment");
            // Xóa reply
            commentRepository.deleteById(reply.getId());
            
            // Giảm comment count của post
            Post post = comment.getPost();
            post.decrementCommentCount();
            postRepository.save(post);
        }

        // Xóa likes của comment
        likeRepository.deleteByTargetIdAndTargetType(commentId, "comment");

        // Giảm số lượng comment của bài viết
        Post post = comment.getPost();
        post.decrementCommentCount();
        postRepository.save(post);

        // Xóa comment
        commentRepository.deleteById(commentId);
    }
}

