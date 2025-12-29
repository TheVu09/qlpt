package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.model.User;
import com.hutech.demo.service.UserService;
import com.hutech.demo.config.WebSocketEventListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private WebSocketEventListener webSocketEventListener;

    // Lấy tất cả users (Admin only)
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<List<User>>> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách người dùng thành công", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy user theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> getUserById(@PathVariable String id) {
        try {
            User user = userService.getUserById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin người dùng thành công", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy danh sách online users
    @GetMapping("/online")
    public ResponseEntity<ApiResponse<Set<String>>> getOnlineUsers() {
        try {
            Set<String> onlineUserIds = webSocketEventListener.getOnlineUsers();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách users online thành công", onlineUserIds));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Kiểm tra user có online không
    @GetMapping("/{userId}/online")
    public ResponseEntity<ApiResponse<Boolean>> isUserOnline(@PathVariable String userId) {
        try {
            boolean isOnline = webSocketEventListener.isUserOnline(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Kiểm tra trạng thái thành công", isOnline));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Toggle Lock/Unlock user (Admin only)
    @PutMapping("/{id}/toggle-lock")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<User>> toggleLock(@PathVariable String id) {
        try {
            User user = userService.toggleLock(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái khóa thành công", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Toggle Enable/Disable user (Admin only)
    @PutMapping("/{id}/toggle-enable")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<User>> toggleEnable(@PathVariable String id) {
        try {
            User user = userService.toggleEnable(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái kích hoạt thành công", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Thay đổi role (Admin only)
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<User>> changeRole(
            @PathVariable String id,
            @RequestBody Map<String, String> request) {
        try {
            String newRole = request.get("role");
            User user = userService.changeRole(id, newRole);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật quyền thành công", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Cập nhật thông tin user (user chỉ có thể update thông tin của chính họ)
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<User>> updateUser(
            @PathVariable String id,
            @RequestBody User updatedUser) {
        try {
            // Lấy thông tin user hiện tại từ SecurityContext
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new ApiResponse<>(false, "Chưa đăng nhập", null));
            }
            
            // Lấy email từ authentication (đã được set bởi JwtAuthenticationFilter)
            String currentUserEmail = authentication.getName();
            String currentUserRole = authentication.getAuthorities().stream()
                    .findFirst()
                    .map(auth -> auth.getAuthority())
                    .orElse("");
            
            // Tìm user hiện tại từ email
            User currentUser = userService.getUserByEmail(currentUserEmail);
            
            // Kiểm tra quyền: user chỉ có thể update thông tin của chính họ
            // Admin có thể update bất kỳ user nào
            if (!currentUserRole.equals("ROLE_ADMIN") && !currentUser.getId().equals(id)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(new ApiResponse<>(false, "Bạn chỉ có thể cập nhật thông tin của chính mình", null));
            }
            
            User user = userService.updateUser(id, updatedUser);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật thông tin thành công", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa user (Admin only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApiResponse<String>> deleteUser(@PathVariable String id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa người dùng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy users theo role (Admin only)
    @GetMapping("/role/{role}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<List<User>>> getUsersByRole(@PathVariable String role) {
        try {
            List<User> users = userService.getUsersByRole(role);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách người dùng thành công", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy users đang thuê phòng (Admin only)
    @GetMapping("/with-room")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_LANDLORD')")
    public ResponseEntity<ApiResponse<List<User>>> getUsersWithRoom() {
        try {
            List<User> users = userService.getUsersWithRoom();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách người thuê thành công", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy users trong cùng motel (để chat)
    @GetMapping("/same-motel/{userId}")
    public ResponseEntity<ApiResponse<List<User>>> getUsersInSameMotel(@PathVariable String userId) {
        try {
            List<User> users = userService.getUsersInSameMotel(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách người dùng thành công", users));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

