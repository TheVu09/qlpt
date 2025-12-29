package com.hutech.demo.dto;

import com.hutech.demo.model.RoomApplication;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApplicationResponse {
    private String id;
    
    // Applicant info (flattened)
    private String applicantId;
    private String applicantName;
    private String applicantEmail;
    private String applicantPhone;
    private String applicantAvatar;
    
    // Room info (flattened)
    private String roomId;
    private String roomNumber;
    private String motelId;
    private String motelName;
    private Double roomPrice;
    private Double roomArea;
    
    // Application details
    private String status; // pending, approved, rejected
    private String message; // Lời nhắn từ người đăng ký
    private String adminNote; // Ghi chú từ admin khi duyệt/từ chối
    
    // Reviewer info (flattened)
    private String reviewedById;
    private String reviewedByName;
    
    private LocalDateTime reviewedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Chuyển từ RoomApplication entity sang ApplicationResponse
    public static ApplicationResponse fromApplication(RoomApplication application) {
        ApplicationResponse response = ApplicationResponse.builder()
                .id(application.getId())
                .status(application.getStatus())
                .message(application.getMessage())
                .adminNote(application.getAdminNote())
                .createdAt(application.getCreatedAt())
                .updatedAt(application.getUpdatedAt())
                .reviewedAt(application.getReviewedAt())
                .build();

        // Flatten applicant info
        if (application.getApplicant() != null) {
            response.setApplicantId(application.getApplicant().getId());
            response.setApplicantName(application.getApplicant().getFullName());
            response.setApplicantEmail(application.getApplicant().getEmail());
            response.setApplicantPhone(application.getApplicant().getPhone());
            response.setApplicantAvatar(application.getApplicant().getAvatar());
        }

        // Flatten room info
        if (application.getRoom() != null) {
            response.setRoomId(application.getRoom().getId());
            response.setRoomNumber(application.getRoom().getRoomNumber());
            response.setRoomPrice(application.getRoom().getPrice());
            response.setRoomArea(application.getRoom().getArea());
            
            // Flatten motel info from room
            if (application.getRoom().getMotel() != null) {
                response.setMotelId(application.getRoom().getMotel().getId());
                response.setMotelName(application.getRoom().getMotel().getName());
            }
        }

        // Flatten reviewer info
        if (application.getReviewedBy() != null) {
            response.setReviewedById(application.getReviewedBy().getId());
            response.setReviewedByName(application.getReviewedBy().getFullName());
        }

        return response;
    }
}

