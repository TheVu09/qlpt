package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    private String email;
    private String password;
    private String fullName;
    private String phone;
    private String gender;
    private String role; // ROLE_ADMIN, ROLE_LANDLORD, ROLE_TENANT
}
