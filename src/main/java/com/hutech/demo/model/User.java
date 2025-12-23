package com.hutech.demo.model;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "users")
public class User {

    @Id
    private String id;
    private String email;
    private String password;
    private String gender;

    /* ===== Phân quyền ===== */
    private String role;
    // ROLE_ADMIN, ROLE_LANDLORD, ROLE_TENANT

    /* ===== Thông tin cá nhân ===== */
    private String fullName;
    private String phone;
    private String avatar; // URL ảnh đại diện

    /* ===== Trạng thái tài khoản ===== */
    private boolean enabled = true;
    private boolean locked = false;

    /* ===== Quan hệ nghiệp vụ ===== */

    // Tenant đang ở phòng nào
    private String currentRoomId;

    // Landlord sở hữu các khu trọ nào
    private List<String> motelIds;

    /* ===== Audit ===== */
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
