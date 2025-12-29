package com.hutech.demo.dto;

import com.hutech.demo.model.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserInfo {
    private String id;
    private String email;
    private String role;
    private String fullName;
    private String phone;
    private String gender;
    private String avatar;
    private String currentRoomId; // Phòng đang thuê

    // Chuyển từ User entity sang UserInfo DTO
    public static UserInfo fromUser(User user) {
        if (user == null) {
            return null;
        }
        return UserInfo.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .fullName(user.getFullName())
                .phone(user.getPhone())
                .gender(user.getGender())
                .avatar(user.getAvatar())
                .currentRoomId(user.getCurrentRoomId())
                .build();
    }
}

