package com.hutech.demo.service;

import com.hutech.demo.model.User;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Lấy tất cả users
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // Lấy user theo ID
    public User getUserById(String userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }

    // Toggle Lock/Unlock user
    public User toggleLock(String userId) {
        User user = getUserById(userId);
        user.setLocked(!user.isLocked());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Toggle Enable/Disable user
    public User toggleEnable(String userId) {
        User user = getUserById(userId);
        user.setEnabled(!user.isEnabled());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Thay đổi role
    public User changeRole(String userId, String newRole) {
        User user = getUserById(userId);
        
        // Validate role
        if (!newRole.equals("ROLE_ADMIN") && 
            !newRole.equals("ROLE_LANDLORD") && 
            !newRole.equals("ROLE_TENANT")) {
            throw new RuntimeException("Role không hợp lệ");
        }
        
        user.setRole(newRole);
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Xóa user
    public void deleteUser(String userId) {
        User user = getUserById(userId);
        
        // Kiểm tra user có đang thuê phòng không
        if (user.getCurrentRoomId() != null && !user.getCurrentRoomId().isEmpty()) {
            throw new RuntimeException("Không thể xóa người dùng đang thuê phòng");
        }
        
        userRepository.deleteById(userId);
    }

    // Cập nhật thông tin user
    public User updateUser(String userId, User updatedUser) {
        User user = getUserById(userId);
        
        if (updatedUser.getFullName() != null) {
            user.setFullName(updatedUser.getFullName());
        }
        if (updatedUser.getPhone() != null) {
            user.setPhone(updatedUser.getPhone());
        }
        if (updatedUser.getGender() != null) {
            user.setGender(updatedUser.getGender());
        }
        if (updatedUser.getAvatar() != null) {
            user.setAvatar(updatedUser.getAvatar());
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    // Tìm users theo role
    public List<User> getUsersByRole(String role) {
        return userRepository.findByRole(role);
    }

    // Đếm users theo role
    public long countUsersByRole(String role) {
        return userRepository.findByRole(role).size();
    }

    // Lấy users đang thuê phòng
    public List<User> getUsersWithRoom() {
        return userRepository.findAll().stream()
                .filter(user -> user.getCurrentRoomId() != null && !user.getCurrentRoomId().isEmpty())
                .toList();
    }

    // Lấy users trong cùng motel (để chat)
    public List<User> getUsersInSameMotel(String userId) {
        User currentUser = getUserById(userId);
        if (currentUser.getCurrentRoomId() == null || currentUser.getCurrentRoomId().isEmpty()) {
            return List.of(); // User chưa có phòng
        }
        
        // Lấy tất cả users có cùng currentRoomId hoặc cùng motel
        // Tạm thời lấy tất cả users (có thể filter theo motel sau)
        return userRepository.findAll().stream()
                .filter(user -> !user.getId().equals(userId)) // Loại bỏ chính user
                .filter(user -> user.getCurrentRoomId() != null && !user.getCurrentRoomId().isEmpty())
                .toList();
    }

    // Lấy user theo email
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));
    }
}

