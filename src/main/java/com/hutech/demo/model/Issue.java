package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "issues")
public class Issue {

    @Id
    private String id;

    @DBRef
    private Room room;

    @DBRef
    private User reporter; // Người báo cáo

    private String title;

    private String description;

    private String status; // OPEN, IN_PROGRESS, DONE, CANCELLED

    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}
