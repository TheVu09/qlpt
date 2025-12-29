package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.ChatResponse;
import com.hutech.demo.dto.MessageResponse;
import com.hutech.demo.model.Chat;
import com.hutech.demo.service.ChatService;
import com.hutech.demo.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private ChatService chatService;

    @Autowired
    private MessageService messageService;

    // Lấy hoặc tạo private chat
    @PostMapping("/private")
    public ResponseEntity<ApiResponse<ChatResponse>> getOrCreatePrivateChat(@RequestBody Map<String, String> request) {
        try {
            String userId1 = request.get("userId1");
            String userId2 = request.get("userId2");
            Chat chat = chatService.getOrCreatePrivateChat(userId1, userId2);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chat thành công", chatService.toChatResponse(chat)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy hoặc tạo room chat
    @PostMapping("/room")
    public ResponseEntity<ApiResponse<ChatResponse>> getOrCreateRoomChat(@RequestBody Map<String, String> request) {
        try {
            String roomId = request.get("roomId");
            Chat chat = chatService.getOrCreateRoomChat(roomId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chat thành công", chatService.toChatResponse(chat)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Chat khu trọ đã bị bỏ

    // Lấy chat theo ID
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<ChatResponse>> getChatById(@PathVariable String chatId) {
        try {
            Chat chat = chatService.getChatById(chatId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chat thành công", chatService.toChatResponse(chat)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy messages trong chat
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> getChatMessages(@PathVariable String chatId) {
        try {
            List<MessageResponse> messages = messageService.getMessagesByChatId(chatId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy tin nhắn thành công", messages));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả private chats của user
    @GetMapping("/user/{userId}/private")
    public ResponseEntity<ApiResponse<List<ChatResponse>>> getUserPrivateChats(@PathVariable String userId) {
        try {
            List<Chat> chats = chatService.getPrivateChatsByUser(userId);
            List<ChatResponse> chatResponses = chats.stream()
                    .map(chatService::toChatResponse)
                    .collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách chat thành công", chatResponses));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy room chat của user
    @GetMapping("/user/{userId}/room")
    public ResponseEntity<ApiResponse<ChatResponse>> getUserRoomChat(@PathVariable String userId) {
        try {
            Chat chat = chatService.getRoomChatByUser(userId);
            if (chat == null) {
                return ResponseEntity.ok(new ApiResponse<>(false, "User chưa có phòng", null));
            }
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy chat thành công", chatService.toChatResponse(chat)));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Chat khu trọ đã bị bỏ
}

