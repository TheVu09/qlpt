package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CommentRequest {
    private String postId;
    private String content;
    private String parentCommentId; // Null nếu là comment gốc, có giá trị nếu là reply
}

