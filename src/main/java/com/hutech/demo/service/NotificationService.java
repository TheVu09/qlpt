package com.hutech.demo.service;

import com.hutech.demo.model.Notification;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.NotificationRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // Tạo notification
    public Notification createNotification(String recipientId, String senderId, String type, 
                                          String title, String content, String relatedId, 
                                          String relatedType, String actionUrl) {
        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new RuntimeException("Recipient not found"));

        User sender = null;
        if (senderId != null) {
            sender = userRepository.findById(senderId).orElse(null);
        }

        Notification notification = Notification.builder()
                .recipient(recipient)
                .sender(sender)
                .type(type)
                .title(title)
                .content(content)
                .relatedId(relatedId)
                .relatedType(relatedType)
                .actionUrl(actionUrl)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        Notification saved = notificationRepository.save(notification);

        // Send real-time notification via WebSocket
        sendRealtimeNotification(saved);

        return saved;
    }

    // Gửi notification real-time qua WebSocket
    private void sendRealtimeNotification(Notification notification) {
        try {
            Map<String, Object> payload = new HashMap<>();
            payload.put("type", "NEW_NOTIFICATION");
            payload.put("notification", convertToMap(notification));

            messagingTemplate.convertAndSend(
                "/topic/user." + notification.getRecipient().getId(),
                payload
            );
        } catch (Exception e) {
            System.err.println("Error sending realtime notification: " + e.getMessage());
        }
    }

    // Convert notification to map for WebSocket
    private Map<String, Object> convertToMap(Notification notification) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", notification.getId());
        map.put("type", notification.getType());
        map.put("title", notification.getTitle());
        map.put("content", notification.getContent());
        map.put("actionUrl", notification.getActionUrl());
        map.put("isRead", notification.isRead());
        map.put("createdAt", notification.getCreatedAt().toString());

        if (notification.getSender() != null) {
            Map<String, Object> sender = new HashMap<>();
            sender.put("id", notification.getSender().getId());
            sender.put("fullName", notification.getSender().getFullName());
            sender.put("avatar", notification.getSender().getAvatar());
            map.put("sender", sender);
        }

        return map;
    }

    // Lấy tất cả notifications của user
    public List<Notification> getNotifications(String userId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }

    // Lấy notifications chưa đọc
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    // Đếm số notifications chưa đọc
    public long countUnreadNotifications(String userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    // Đánh dấu đã đọc
    public Notification markAsRead(String notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        notification.markAsRead();
        return notificationRepository.save(notification);
    }

    // Đánh dấu tất cả đã đọc
    public void markAllAsRead(String userId) {
        List<Notification> notifications = getUnreadNotifications(userId);
        notifications.forEach(Notification::markAsRead);
        notificationRepository.saveAll(notifications);
    }

    // Xóa notification
    public void deleteNotification(String notificationId, String userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (!notification.getRecipient().getId().equals(userId)) {
            throw new RuntimeException("You don't have permission to delete this notification");
        }

        notificationRepository.delete(notification);
    }

    // Xóa tất cả notifications
    public void deleteAllNotifications(String userId) {
        List<Notification> notifications = getNotifications(userId);
        notificationRepository.deleteAll(notifications);
    }

    // === Notification Helpers ===

    // Friend request notification
    public void notifyFriendRequest(String recipientId, String senderId) {
        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) return;

        createNotification(
            recipientId,
            senderId,
            "FRIEND_REQUEST",
            "Lời mời kết bạn",
            sender.getFullName() + " đã gửi lời mời kết bạn cho bạn",
            null,
            "friendship",
            "/profile/" + senderId
        );
    }

    // Friend accept notification
    public void notifyFriendAccept(String recipientId, String senderId) {
        User sender = userRepository.findById(senderId).orElse(null);
        if (sender == null) return;

        createNotification(
            recipientId,
            senderId,
            "FRIEND_ACCEPT",
            "Chấp nhận kết bạn",
            sender.getFullName() + " đã chấp nhận lời mời kết bạn của bạn",
            null,
            "friendship",
            "/profile/" + senderId
        );
    }

    // New application notification (for admin/landlord)
    public void notifyNewApplication(String recipientId, String applicantId, String applicationId) {
        User applicant = userRepository.findById(applicantId).orElse(null);
        if (applicant == null) return;

        createNotification(
            recipientId,
            applicantId,
            "APPLICATION_NEW",
            "Đơn đăng ký mới",
            applicant.getFullName() + " đã gửi đơn đăng ký thuê phòng",
            applicationId,
            "application",
            "/admin/applications"
        );
    }

    // Application approved notification
    public void notifyApplicationApproved(String recipientId, String applicationId) {
        createNotification(
            recipientId,
            null,
            "APPLICATION_APPROVED",
            "Đơn được duyệt",
            "Đơn đăng ký của bạn đã được chấp nhận!",
            applicationId,
            "application",
            "/user/my-applications"
        );
    }

    // Application rejected notification
    public void notifyApplicationRejected(String recipientId, String applicationId, String reason) {
        String content = "Đơn đăng ký của bạn đã bị từ chối";
        if (reason != null && !reason.isEmpty()) {
            content += ": " + reason;
        }

        createNotification(
            recipientId,
            null,
            "APPLICATION_REJECTED",
            "Đơn bị từ chối",
            content,
            applicationId,
            "application",
            "/user/my-applications"
        );
    }
}

