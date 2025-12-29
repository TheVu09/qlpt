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
@Document(collection = "likes")
public class Like {

    @Id
    private String id;

    @DBRef
    private User user; // Người thả like

    private String targetId; // ID của đối tượng được like (Post ID hoặc Comment ID)

    private String targetType; // post hoặc comment

    private LocalDateTime createdAt;

    // Constructor tiện lợi
    public Like(User user, String targetId, String targetType) {
        this.user = user;
        this.targetId = targetId;
        this.targetType = targetType;
        this.createdAt = LocalDateTime.now();
    }
}

