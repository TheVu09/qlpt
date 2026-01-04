package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.model.Notification;
import com.hutech.demo.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Lấy tất cả notifications
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @PathVariable String userId) {
        try {
            List<Notification> notifications = notificationService.getNotifications(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông báo thành công", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy notifications chưa đọc
    @GetMapping("/{userId}/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(
            @PathVariable String userId) {
        try {
            List<Notification> notifications = notificationService.getUnreadNotifications(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông báo chưa đọc thành công", notifications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đếm số notifications chưa đọc
    @GetMapping("/{userId}/unread/count")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotifications(
            @PathVariable String userId) {
        try {
            long count = notificationService.countUnreadNotifications(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đếm thành công", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đánh dấu đã đọc
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Notification>> markAsRead(
            @PathVariable String notificationId) {
        try {
            Notification notification = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đánh dấu đã đọc thành công", notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đánh dấu tất cả đã đọc
    @PutMapping("/{userId}/read-all")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @PathVariable String userId) {
        try {
            notificationService.markAllAsRead(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đánh dấu tất cả đã đọc thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa notification
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable String notificationId,
            @RequestParam String userId) {
        try {
            notificationService.deleteNotification(notificationId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa thông báo thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa tất cả notifications
    @DeleteMapping("/{userId}/all")
    public ResponseEntity<ApiResponse<String>> deleteAllNotifications(
            @PathVariable String userId) {
        try {
            notificationService.deleteAllNotifications(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa tất cả thông báo thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Test endpoint - create custom notification
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<Notification>> createTestNotification(
            @RequestBody java.util.Map<String, String> request) {
        try {
            String recipientId = request.get("recipientId");
            String senderId = request.get("senderId");
            String type = request.get("type");
            String title = request.get("title");
            String content = request.get("content");
            String actionUrl = request.get("actionUrl");

            Notification notification = notificationService.createNotification(
                recipientId, senderId, type, title, content, null, null, actionUrl
            );

            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo test notification thành công", notification));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

