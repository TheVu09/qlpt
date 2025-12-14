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
        if(userRepository.findByUsername(request.getUsername()).isPresent()){
            throw new RuntimeException("Username đã tồn tại");
        }

        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email đã tồn tại");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ROLE_USER");

        userRepository.save(user);

        return new ApiResponse<>(true, "Đăng ký thành công", null);
    }

    // Login
    public ApiResponse<LoginResponse> login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu không đúng");
        }

        String token = jwtUtil.generateToken(user.getUsername(), user.getRole());

        UserInfo userInfo = new UserInfo(
                user.getUsername(),
                user.getEmail(),
                user.getRole()
        );

        LoginResponse loginResponse = new LoginResponse(userInfo, token);

        return new ApiResponse<>(true, "Đăng nhập thành công", loginResponse);
    }
}
