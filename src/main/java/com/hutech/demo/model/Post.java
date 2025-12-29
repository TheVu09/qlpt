package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "posts")
public class Post {

    @Id
    private String id;

    @DBRef
    private User author; // Người đăng bài

    @DBRef
    private Motel motel; // Khu trọ (post thuộc khu trọ nào)

    private String content; // Nội dung bài viết

    private List<String> images = new ArrayList<>(); // Danh sách hình ảnh đính kèm

    private String postType; // general, announcement, question, event
    // general: bài viết thường
    // announcement: thông báo
    // question: câu hỏi
    // event: sự kiện

    private boolean isPinned = false; // Admin có thể pin bài viết quan trọng

    private int likeCount = 0; // Số lượng like
    private int commentCount = 0; // Số lượng comment

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

    // Method để tăng số lượng comment
    public void incrementCommentCount() {
        this.commentCount++;
    }

    // Method để giảm số lượng comment
    public void decrementCommentCount() {
        if (this.commentCount > 0) {
            this.commentCount--;
        }
    }
}

