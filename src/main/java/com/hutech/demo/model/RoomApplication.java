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
@Document(collection = "room_applications")
public class RoomApplication {

    @Id
    private String id;

    @DBRef
    private User applicant; // Người đăng ký

    @DBRef
    private Room room; // Phòng muốn đăng ký

    private String status; // pending, approved, rejected

    private String message; // Lời nhắn từ người đăng ký

    private String adminNote; // Ghi chú từ admin khi duyệt/từ chối

    @DBRef
    private User reviewedBy; // Admin đã duyệt

    private LocalDateTime reviewedAt; // Thời gian duyệt

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
