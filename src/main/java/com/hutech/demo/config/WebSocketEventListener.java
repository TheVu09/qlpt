package com.hutech.demo.config;

import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WebSocketEventListener {

    // Map để lưu userId và sessionId
    private final Map<String, String> userSessionMap = new ConcurrentHashMap<>();
    private final Map<String, String> sessionUserMap = new ConcurrentHashMap<>();
    
    // Set để track online users
    private final java.util.Set<String> onlineUsers = java.util.Collections.synchronizedSet(new java.util.HashSet<>());

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        // Lấy userId từ connect headers
        String userId = headerAccessor.getFirstNativeHeader("userId");
        if (userId != null && !userId.isEmpty()) {
            // Store in session attributes
            Map<String, Object> sessionAttributes = headerAccessor.getSessionAttributes();
            if (sessionAttributes != null) {
                sessionAttributes.put("userId", userId);
            }
            
            userSessionMap.put(userId, sessionId);
            sessionUserMap.put(sessionId, userId);
            onlineUsers.add(userId);
            System.out.println("User connected: " + userId + " with session: " + sessionId + " | Online users: " + onlineUsers.size());
        }
    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String sessionId = headerAccessor.getSessionId();
        
        String userId = sessionUserMap.remove(sessionId);
        if (userId != null) {
            userSessionMap.remove(userId);
            onlineUsers.remove(userId);
            System.out.println("User disconnected: " + userId + " with session: " + sessionId + " | Online users: " + onlineUsers.size());
        }
    }
    
    public java.util.Set<String> getOnlineUsers() {
        return new java.util.HashSet<>(onlineUsers);
    }
    
    public boolean isUserOnline(String userId) {
        return onlineUsers.contains(userId);
    }

    public String getSessionId(String userId) {
        return userSessionMap.get(userId);
    }

    public String getUserId(String sessionId) {
        return sessionUserMap.get(sessionId);
    }
}

