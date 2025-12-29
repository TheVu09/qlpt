package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.CommentRequest;
import com.hutech.demo.dto.CommentResponse;
import com.hutech.demo.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/comments")
@CrossOrigin(origins = "*")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // Tạo comment mới
    @PostMapping
    public ResponseEntity<ApiResponse<CommentResponse>> createComment(
            @RequestBody CommentRequest request,
            @RequestParam String userId) {
        try {
            CommentResponse comment = commentService.createComment(request, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo comment thành công", comment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả comment của một bài viết
    @GetMapping("/post/{postId}")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getCommentsByPost(
            @PathVariable String postId,
            @RequestParam(required = false) String userId) {
        try {
            List<CommentResponse> comments = commentService.getCommentsByPost(postId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách comment thành công", comments));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả reply của một comment
    @GetMapping("/{commentId}/replies")
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getRepliesByComment(
            @PathVariable String commentId,
            @RequestParam(required = false) String userId) {
        try {
            List<CommentResponse> replies = commentService.getRepliesByComment(commentId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách reply thành công", replies));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy comment theo ID
    @GetMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> getCommentById(
            @PathVariable String commentId,
            @RequestParam(required = false) String userId) {
        try {
            CommentResponse comment = commentService.getCommentById(commentId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy comment thành công", comment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Cập nhật comment
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<CommentResponse>> updateComment(
            @PathVariable String commentId,
            @RequestBody Map<String, String> request,
            @RequestParam String userId) {
        try {
            String content = request.get("content");
            CommentResponse comment = commentService.updateComment(commentId, content, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật comment thành công", comment));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa comment
    @DeleteMapping("/{commentId}")
    public ResponseEntity<ApiResponse<String>> deleteComment(
            @PathVariable String commentId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        try {
            commentService.deleteComment(commentId, userId, isAdmin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa comment thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

