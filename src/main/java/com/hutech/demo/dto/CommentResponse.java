package com.hutech.demo.dto;

import com.hutech.demo.model.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponse {
    private String id;
    private UserInfo author;
    private String postId;
    private String content;
    private String parentCommentId;
    private int likeCount;
    private boolean isLikedByCurrentUser;
    private List<CommentResponse> replies = new ArrayList<>(); // Danh sách reply
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Chuyển từ Comment entity sang CommentResponse
    public static CommentResponse fromComment(Comment comment) {
        return CommentResponse.builder()
                .id(comment.getId())
                .author(UserInfo.fromUser(comment.getAuthor()))
                .postId(comment.getPost() != null ? comment.getPost().getId() : null)
                .content(comment.getContent())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .likeCount(comment.getLikeCount())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .build();
    }

    // Chuyển từ Comment entity với thông tin isLiked
    public static CommentResponse fromComment(Comment comment, boolean isLiked) {
        CommentResponse response = fromComment(comment);
        response.setLikedByCurrentUser(isLiked);
        return response;
    }
}

