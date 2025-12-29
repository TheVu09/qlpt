package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "comments")
public class Comment {

    @Id
    private String id;

    @DBRef
    private Post post; // Bài viết mà comment này thuộc về

    @DBRef
    private User author; // Người comment

    private String content; // Nội dung comment

    @DBRef
    private Comment parentComment; // Comment cha (null nếu là comment gốc)

    private int likeCount = 0; // Số lượng like cho comment

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Method để tăng số lượng like
    public void incrementLikeCount() {
        this.likeCount++;
    }

    // Method để giảm số lượng like
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }

    // Kiểm tra xem có phải là reply không
    public boolean isReply() {
        return this.parentComment != null;
    }
}

