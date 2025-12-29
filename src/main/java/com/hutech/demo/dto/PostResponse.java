package com.hutech.demo.dto;

import com.hutech.demo.model.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponse {
    private String id;
    private UserInfo author;
    private String motelId;
    private String motelName;
    private String content;
    private List<String> images;
    private String postType;
    private boolean isPinned;
    private int likeCount;
    private int commentCount;
    private boolean isLikedByCurrentUser; // User hiện tại đã like chưa
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Chuyển từ Post entity sang PostResponse
    public static PostResponse fromPost(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .author(UserInfo.fromUser(post.getAuthor()))
                .motelId(post.getMotel() != null ? post.getMotel().getId() : null)
                .motelName(post.getMotel() != null ? post.getMotel().getName() : null)
                .content(post.getContent())
                .images(post.getImages())
                .postType(post.getPostType())
                .isPinned(post.isPinned())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }

    // Chuyển từ Post entity với thông tin isLiked
    public static PostResponse fromPost(Post post, boolean isLiked) {
        PostResponse response = fromPost(post);
        response.setLikedByCurrentUser(isLiked);
        return response;
    }
}

