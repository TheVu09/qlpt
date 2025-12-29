package com.hutech.demo.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

@Controller
@CrossOrigin(origins = "*")
public class WebSocketController {

    private final SimpMessagingTemplate messagingTemplate;

    public WebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Handle user connection setup
     */
    @MessageMapping("/user.connect")
    @SendTo("/topic/online-users")
    public Map<String, Object> userConnect(@Payload Map<String, String> payload, SimpMessageHeaderAccessor headerAccessor) {
        String userId = payload.get("userId");
        String username = payload.get("username");

        // Store user in WebSocket session
        headerAccessor.getSessionAttributes().put("userId", userId);
        headerAccessor.getSessionAttributes().put("username", username);

        return Map.of(
                "type", "USER_CONNECTED",
                "userId", userId,
                "username", username,
                "timestamp", System.currentTimeMillis());
    }

    /**
     * Handle user disconnection
     */
    @MessageMapping("/user.disconnect")
    @SendTo("/topic/online-users")
    public Map<String, Object> userDisconnect(@Payload Map<String, String> payload) {
        return Map.of(
                "type", "USER_DISCONNECTED",
                "userId", payload.get("userId"),
                "timestamp", System.currentTimeMillis());
    }

    /**
     * Handle private messages
     */
    @MessageMapping("/message.private")
    public void sendPrivateMessage(@Payload Map<String, Object> message, Principal principal) {
        String recipientId = (String) message.get("recipientId");

        // Send to specific user
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "PRIVATE_MESSAGE");
        wsMessage.put("content", message.get("content"));
        wsMessage.put("senderId", message.get("senderId"));
        wsMessage.put("senderName", message.get("senderName"));
        wsMessage.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSendToUser(recipientId, "/queue/messages", wsMessage);
    }

    /**
     * Handle group messages (motel chat)
     */
    @MessageMapping("/message.group")
    public void sendGroupMessage(@Payload Map<String, Object> message) {
        String motelId = (String) message.get("motelId");
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "GROUP_MESSAGE");
        wsMessage.put("id", message.get("id"));
        wsMessage.put("content", message.get("content"));
        wsMessage.put("senderId", message.get("senderId"));
        wsMessage.put("senderName", message.get("senderName"));
        wsMessage.put("senderEmail", message.get("senderEmail"));
        wsMessage.put("senderAvatar", message.get("senderAvatar"));
        wsMessage.put("motelId", motelId);
        wsMessage.put("createdAt", message.get("createdAt"));
        wsMessage.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/motel." + motelId, wsMessage);
    }

    /**
     * Handle room group messages
     */
    @MessageMapping("/message.room-group")
    public void sendRoomGroupMessage(@Payload Map<String, Object> message) {
        String roomId = (String) message.get("roomId");
        Map<String, Object> wsMessage = new HashMap<>();
        wsMessage.put("type", "ROOM_GROUP_MESSAGE");
        wsMessage.put("id", message.get("id"));
        wsMessage.put("content", message.get("content"));
        wsMessage.put("senderId", message.get("senderId"));
        wsMessage.put("senderName", message.get("senderName"));
        wsMessage.put("senderEmail", message.get("senderEmail"));
        wsMessage.put("senderAvatar", message.get("senderAvatar"));
        wsMessage.put("roomId", roomId);
        wsMessage.put("roomNumber", message.get("roomNumber"));
        wsMessage.put("createdAt", message.get("createdAt"));
        wsMessage.put("timestamp", System.currentTimeMillis());
        messagingTemplate.convertAndSend("/topic/room." + roomId, wsMessage);
    }

    /**
     * Handle admin announcements
     */
    @MessageMapping("/message.announcement")
    @SendTo("/topic/announcements")
    public Map<String, Object> sendAnnouncement(@Payload Map<String, Object> message) {
        return Map.of(
                "type", "ANNOUNCEMENT",
                "title", message.get("title"),
                "content", message.get("content"),
                "senderId", message.get("senderId"),
                "senderName", message.get("senderName"),
                "timestamp", System.currentTimeMillis());
    }

    /**
     * Notify about room application updates
     */
    public void notifyApplicationUpdate(String userId, String applicantId, Map<String, Object> data) {
        // Notify admin/landlord
        messagingTemplate.convertAndSendToUser(
                userId,
                "/queue/notifications",
                Map.of(
                        "type", "APPLICATION_UPDATE",
                        "data", data,
                        "timestamp", System.currentTimeMillis()));

        // Also notify applicant
        if (applicantId != null && !applicantId.equals(userId)) {
            messagingTemplate.convertAndSendToUser(
                    applicantId,
                    "/queue/notifications",
                    Map.of(
                            "type", "APPLICATION_STATUS",
                            "data", data,
                            "timestamp", System.currentTimeMillis()));
        }
    }
}
