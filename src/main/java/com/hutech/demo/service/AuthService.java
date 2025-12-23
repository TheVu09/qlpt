package com.hutech.demo.service;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.LoginRequest;
import com.hutech.demo.dto.LoginResponse;
import com.hutech.demo.dto.RegisterRequest;
import com.hutech.demo.dto.UserInfo;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.UserRepository;
import com.hutech.demo.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private PasswordEncoder passwordEncoder;

    // Register
    public ApiResponse<String> register(RegisterRequest request) {
        // Kiểm tra email đã tồn tại
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        // Kiểm tra số điện thoại đã tồn tại
        if (request.getPhone() != null && userRepository.findByPhone(request.getPhone()).isPresent()) {
            throw new RuntimeException("Số điện thoại đã tồn tại");
        }

        // Tạo user mới dựa trên User model
        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .phone(request.getPhone())
                .gender(request.getGender())
                .role(request.getRole() != null ? request.getRole() : "ROLE_TENANT") // Mặc định là ROLE_TENANT
                .enabled(true)
                .locked(false)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        userRepository.save(user);

        return new ApiResponse<>(true, "Đăng ký thành công", null);
    }

    // Login
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        // Tìm user bằng email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

        // Kiểm tra tài khoản có bị khóa không
        if (user.isLocked()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // Kiểm tra tài khoản có được kích hoạt không
        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        // Tạo JWT token với email và role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Tạo UserInfo
        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getFullName(),
                user.getPhone(),
                user.getGender());

        LoginResponse loginResponse = new LoginResponse(userInfo, token);

        return new ApiResponse<>(true, "Đăng nhập thành công", loginResponse);
    }

    // Admin Login - Only for ROLE_ADMIN and ROLE_LANDLORD
    public ApiResponse<LoginResponse> adminLogin(LoginRequest request) {
        // Tìm user bằng email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email hoặc mật khẩu không đúng"));

        // Kiểm tra tài khoản có bị khóa không
        if (user.isLocked()) {
            throw new RuntimeException("Tài khoản đã bị khóa");
        }

        // Kiểm tra tài khoản có được kích hoạt không
        if (!user.isEnabled()) {
            throw new RuntimeException("Tài khoản chưa được kích hoạt");
        }

        // Kiểm tra mật khẩu
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email hoặc mật khẩu không đúng");
        }

        // Kiểm tra quyền admin hoặc landlord
        if (!"ROLE_ADMIN".equals(user.getRole()) && !"ROLE_LANDLORD".equals(user.getRole())) {
            throw new RuntimeException("Bạn không có quyền truy cập trang quản trị");
        }

        // Tạo JWT token với email và role
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // Tạo UserInfo
        UserInfo userInfo = new UserInfo(
                user.getId(),
                user.getEmail(),
                user.getRole(),
                user.getFullName(),
                user.getPhone(),
                user.getGender());

        LoginResponse loginResponse = new LoginResponse(userInfo, token);

        return new ApiResponse<>(true, "Đăng nhập quản trị thành công", loginResponse);
    }
}
