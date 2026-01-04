package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.MessageResponse;
import com.hutech.demo.model.Message;
import com.hutech.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    // Gửi tin nhắn private
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<Message>> sendPrivateMessage(
            @RequestBody Map<String, String> request) {
        try {
            String senderId = request.get("senderId");
            String receiverId = request.get("receiverId");
            String content = request.get("content");

            Message message = messageService.sendPrivateMessage(senderId, receiverId, content);
            return ResponseEntity.ok(new ApiResponse<>(true, "Gửi tin nhắn thành công", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Chat khu trọ đã bị bỏ - chỉ còn chat phòng và chat 1vs1

    // Gửi thông báo từ admin
    @PostMapping("/announcement")
    public ResponseEntity<ApiResponse<Message>> sendAnnouncement(
            @RequestBody Map<String, String> request) {
        try {
            String adminId = request.get("adminId");
            String motelId = request.get("motelId");
            String content = request.get("content");

            Message message = messageService.sendAnnouncement(adminId, motelId, content);
            return ResponseEntity.ok(new ApiResponse<>(true, "Gửi thông báo thành công", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tin nhắn trong khu trọ
    @GetMapping("/motel/{motelId}")
    public ResponseEntity<ApiResponse<List<Message>>> getMotelMessages(
            @PathVariable String motelId) {
        try {
            List<Message> messages = messageService.getMotelMessages(motelId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tin nhắn chat private giữa 2 người
    @GetMapping("/private/{userId1}/{userId2}")
    public ResponseEntity<ApiResponse<List<Message>>> getPrivateMessages(
            @PathVariable String userId1,
            @PathVariable String userId2) {
        try {
            List<Message> messages = messageService.getPrivateMessages(userId1, userId2);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tin nhắn chat private giữa 2 người với populated data
    @GetMapping("/private/{userId1}/{userId2}/details")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getPrivateMessagesWithDetails(
            @PathVariable String userId1,
            @PathVariable String userId2) {
        try {
            List<MessageResponse> messages = messageService.getPrivateMessagesWithDetails(userId1, userId2);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tin nhắn chưa đọc
    @GetMapping("/unread/{userId}")
    public ResponseEntity<ApiResponse<List<Message>>> getUnreadMessages(
            @PathVariable String userId) {
        try {
            List<Message> messages = messageService.getUnreadMessages(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn chưa đọc thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đếm số tin nhắn chưa đọc
    @GetMapping("/unread/count/{userId}")
    public ResponseEntity<ApiResponse<Long>> countUnreadMessages(
            @PathVariable String userId) {
        try {
            long count = messageService.countUnreadMessages(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đếm tin nhắn chưa đọc thành công", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đánh dấu tin nhắn đã đọc
    @PutMapping("/{messageId}/read")
    public ResponseEntity<ApiResponse<Message>> markAsRead(
            @PathVariable String messageId) {
        try {
            Message message = messageService.markAsRead(messageId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đánh dấu đã đọc thành công", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đánh dấu tất cả tin nhắn đã đọc
    @PutMapping("/read-all/{userId}")
    public ResponseEntity<ApiResponse<String>> markAllAsRead(
            @PathVariable String userId) {
        try {
            messageService.markAllAsRead(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đánh dấu tất cả đã đọc thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa tin nhắn
    @DeleteMapping("/{messageId}")
    public ResponseEntity<ApiResponse<String>> deleteMessage(
            @PathVariable String messageId,
            @RequestParam String userId) {
        try {
            messageService.deleteMessage(messageId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa tin nhắn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy thông báo trong khu trọ
    @GetMapping("/announcements/{motelId}")
    public ResponseEntity<ApiResponse<List<Message>>> getMotelAnnouncements(
            @PathVariable String motelId) {
        try {
            List<Message> messages = messageService.getMotelAnnouncements(motelId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông báo thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Chat khu trọ đã bị bỏ

    // Gửi tin nhắn nhóm trong phòng trọ
    @PostMapping("/room-group")
    public ResponseEntity<ApiResponse<Message>> sendRoomGroupMessage(
            @RequestBody Map<String, String> request) {
        try {
            String senderId = request.get("senderId");
            String roomId = request.get("roomId");
            String content = request.get("content");

            Message message = messageService.sendRoomGroupMessage(senderId, roomId, content);
            return ResponseEntity.ok(new ApiResponse<>(true, "Gửi tin nhắn thành công", message));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tin nhắn group trong phòng trọ
    @GetMapping("/room-group/{roomId}")
    public ResponseEntity<ApiResponse<List<Message>>> getRoomGroupMessages(
            @PathVariable String roomId) {
        try {
            List<Message> messages = messageService.getRoomGroupMessages(roomId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn nhóm phòng thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tin nhắn group trong phòng trọ với populated data
    @GetMapping("/room-group/{roomId}/details")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getRoomGroupMessagesWithDetails(
            @PathVariable String roomId) {
        try {
            List<MessageResponse> messages = messageService.getRoomGroupMessagesWithDetails(roomId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn nhóm phòng thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả tin nhắn trong phòng
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<Message>>> getRoomMessages(
            @PathVariable String roomId) {
        try {
            List<Message> messages = messageService.getRoomMessages(roomId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn phòng thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đếm số tin nhắn chưa đọc trong một chat
    @GetMapping("/unread/chat/{chatId}")
    public ResponseEntity<ApiResponse<Long>> countUnreadMessagesInChat(
            @PathVariable String chatId,
            @RequestParam String userId) {
        try {
            long count = messageService.countUnreadMessagesInChat(chatId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đếm tin nhắn chưa đọc thành công", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đánh dấu tất cả tin nhắn trong chat là đã đọc
    @PutMapping("/read-chat/{chatId}")
    public ResponseEntity<ApiResponse<String>> markChatMessagesAsRead(
            @PathVariable String chatId,
            @RequestParam String userId) {
        try {
            messageService.markChatMessagesAsRead(chatId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đánh dấu tất cả tin nhắn đã đọc thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
