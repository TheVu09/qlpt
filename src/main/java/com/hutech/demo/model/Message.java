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
@Document(collection = "messages")
public class Message {

    @Id
    private String id;

    @DBRef
    private Chat chat; // Chat mà message này thuộc về

    @DBRef
    private User sender; // Người gửi

    private String content; // Nội dung tin nhắn

    private boolean isRead; // Đã đọc chưa

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
