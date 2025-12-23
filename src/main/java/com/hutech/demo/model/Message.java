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
    private User sender; // Người gửi

    @DBRef
    private User receiver; // Người nhận (null nếu là tin nhắn chung trong cộng đồng)

    @DBRef
    private Motel motel; // Khu trọ (để chat theo từng khu trọ)

    private String content; // Nội dung tin nhắn

    private String messageType; // private (1-1), group (nhóm trong khu trọ), announcement (thông báo từ admin)

    private boolean isRead; // Đã đọc chưa

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
