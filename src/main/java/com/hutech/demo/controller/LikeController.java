package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.LikeRequest;
import com.hutech.demo.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/likes")
@CrossOrigin(origins = "*")
public class LikeController {

    @Autowired
    private LikeService likeService;

    // Toggle like (like hoặc unlike)
    @PostMapping("/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleLike(
            @RequestBody LikeRequest request,
            @RequestParam String userId) {
        try {
            boolean isLiked = likeService.toggleLike(request, userId);
            long likeCount = likeService.getLikeCount(request.getTargetId(), request.getTargetType());
            
            Map<String, Object> response = new HashMap<>();
            response.put("isLiked", isLiked);
            response.put("likeCount", likeCount);
            
            String message = isLiked ? "Đã thích" : "Đã bỏ thích";
            return ResponseEntity.ok(new ApiResponse<>(true, message, response));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Kiểm tra user đã like chưa
    @GetMapping("/check")
    public ResponseEntity<ApiResponse<Boolean>> checkLike(
            @RequestParam String userId,
            @RequestParam String targetId,
            @RequestParam String targetType) {
        try {
            boolean isLiked = likeService.isLiked(userId, targetId, targetType);
            return ResponseEntity.ok(new ApiResponse<>(true, "Kiểm tra like thành công", isLiked));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy số lượng like
    @GetMapping("/count")
    public ResponseEntity<ApiResponse<Long>> getLikeCount(
            @RequestParam String targetId,
            @RequestParam String targetType) {
        try {
            long count = likeService.getLikeCount(targetId, targetType);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy số lượng like thành công", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

