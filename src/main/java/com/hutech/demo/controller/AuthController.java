package com.hutech.demo.controller;

import com.hutech.demo.dto.LoginRequest;
import com.hutech.demo.dto.RegisterRequest;
import com.hutech.demo.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.ok(authService.register(registerRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/admin/login")
    public ResponseEntity<?> adminLogin(
            @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.adminLogin(request));
    }

    // Check Authentication - Lấy thông tin user hiện tại
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        try {
            return ResponseEntity.ok(authService.checkAuth());
        } catch (Exception e) {
            return ResponseEntity.status(401)
                    .body(new com.hutech.demo.dto.ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
