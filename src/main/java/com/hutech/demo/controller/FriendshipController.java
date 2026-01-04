package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.model.Friendship;
import com.hutech.demo.model.User;
import com.hutech.demo.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/friendships")
@CrossOrigin(origins = "*")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    // Gửi lời mời kết bạn
    @PostMapping("/request")
    public ResponseEntity<ApiResponse<Friendship>> sendFriendRequest(
            @RequestBody Map<String, String> request) {
        try {
            String requesterId = request.get("requesterId");
            String receiverId = request.get("receiverId");

            Friendship friendship = friendshipService.sendFriendRequest(requesterId, receiverId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Gửi lời mời kết bạn thành công", friendship));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Chấp nhận lời mời kết bạn
    @PutMapping("/{friendshipId}/accept")
    public ResponseEntity<ApiResponse<Friendship>> acceptFriendRequest(
            @PathVariable String friendshipId,
            @RequestParam String userId) {
        try {
            Friendship friendship = friendshipService.acceptFriendRequest(friendshipId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Chấp nhận lời mời kết bạn thành công", friendship));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Từ chối lời mời kết bạn
    @PutMapping("/{friendshipId}/reject")
    public ResponseEntity<ApiResponse<Friendship>> rejectFriendRequest(
            @PathVariable String friendshipId,
            @RequestParam String userId) {
        try {
            Friendship friendship = friendshipService.rejectFriendRequest(friendshipId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Từ chối lời mời kết bạn thành công", friendship));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Hủy kết bạn
    @DeleteMapping("/unfriend")
    public ResponseEntity<ApiResponse<String>> unfriend(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        try {
            friendshipService.unfriend(userId1, userId2);
            return ResponseEntity.ok(new ApiResponse<>(true, "Hủy kết bạn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy danh sách bạn bè
    @GetMapping("/friends/{userId}")
    public ResponseEntity<ApiResponse<List<User>>> getFriends(
            @PathVariable String userId) {
        try {
            List<User> friends = friendshipService.getFriends(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách bạn bè thành công", friends));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy lời mời kết bạn đang chờ
    @GetMapping("/pending/{userId}")
    public ResponseEntity<ApiResponse<List<Friendship>>> getPendingRequests(
            @PathVariable String userId) {
        try {
            List<Friendship> requests = friendshipService.getPendingRequests(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy lời mời kết bạn thành công", requests));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy trạng thái friendship giữa 2 users
    @GetMapping("/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getFriendshipStatus(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        try {
            String status = friendshipService.getFriendshipStatus(userId1, userId2);
            Friendship friendship = friendshipService.getFriendship(userId1, userId2);
            
            Map<String, Object> result = new HashMap<>();
            result.put("status", status);
            result.put("friendship", friendship);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy trạng thái thành công", result));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Đếm số bạn bè
    @GetMapping("/count/{userId}")
    public ResponseEntity<ApiResponse<Long>> countFriends(
            @PathVariable String userId) {
        try {
            long count = friendshipService.countFriends(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Đếm bạn bè thành công", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Kiểm tra 2 users có phải bạn bè không
    @GetMapping("/are-friends")
    public ResponseEntity<ApiResponse<Boolean>> areFriends(
            @RequestParam String userId1,
            @RequestParam String userId2) {
        try {
            boolean areFriends = friendshipService.areFriends(userId1, userId2);
            return ResponseEntity.ok(new ApiResponse<>(true, "Kiểm tra thành công", areFriends));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

