package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfo {
    private String id;
    private String email;
    private String role;
    private String fullName;
    private String phone;
    private String gender;
}

