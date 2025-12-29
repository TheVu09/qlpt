package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.PostRequest;
import com.hutech.demo.dto.PostResponse;
import com.hutech.demo.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin(origins = "*")
public class PostController {

    @Autowired
    private PostService postService;

    // Tạo bài viết mới
    @PostMapping
    public ResponseEntity<ApiResponse<PostResponse>> createPost(
            @RequestBody PostRequest request,
            @RequestParam String userId) {
        try {
            PostResponse post = postService.createPost(request, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo bài viết thành công", post));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả bài viết (feed toàn bộ)
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostResponse>>> getAllPosts(
            @RequestParam(required = false) String userId) {
        try {
            List<PostResponse> posts = postService.getAllPosts(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách bài viết thành công", posts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả bài viết theo khu trọ
    @GetMapping("/motel/{motelId}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByMotel(
            @PathVariable String motelId,
            @RequestParam(required = false) String userId) {
        try {
            List<PostResponse> posts = postService.getPostsByMotel(motelId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách bài viết thành công", posts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy bài viết theo ID
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> getPostById(
            @PathVariable String postId,
            @RequestParam(required = false) String userId) {
        try {
            PostResponse post = postService.getPostById(postId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy bài viết thành công", post));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy bài viết của một user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByUser(
            @PathVariable String userId,
            @RequestParam(required = false) String currentUserId) {
        try {
            List<PostResponse> posts = postService.getPostsByUser(userId, currentUserId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách bài viết thành công", posts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Cập nhật bài viết
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostResponse>> updatePost(
            @PathVariable String postId,
            @RequestBody PostRequest request,
            @RequestParam String userId) {
        try {
            PostResponse post = postService.updatePost(postId, request, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật bài viết thành công", post));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa bài viết
    @DeleteMapping("/{postId}")
    public ResponseEntity<ApiResponse<String>> deletePost(
            @PathVariable String postId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        try {
            postService.deletePost(postId, userId, isAdmin);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa bài viết thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Pin/Unpin bài viết (Admin only)
    @PostMapping("/{postId}/pin")
    public ResponseEntity<ApiResponse<PostResponse>> togglePinPost(
            @PathVariable String postId) {
        try {
            PostResponse post = postService.togglePinPost(postId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật trạng thái pin thành công", post));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy bài viết được pin
    @GetMapping("/motel/{motelId}/pinned")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPinnedPosts(
            @PathVariable String motelId,
            @RequestParam(required = false) String userId) {
        try {
            List<PostResponse> posts = postService.getPinnedPosts(motelId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách bài viết được pin thành công", posts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy bài viết theo loại
    @GetMapping("/motel/{motelId}/type/{postType}")
    public ResponseEntity<ApiResponse<List<PostResponse>>> getPostsByType(
            @PathVariable String motelId,
            @PathVariable String postType,
            @RequestParam(required = false) String userId) {
        try {
            List<PostResponse> posts = postService.getPostsByType(motelId, postType, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách bài viết theo loại thành công", posts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

