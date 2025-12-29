package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.service.CloudinaryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    @Autowired
    private CloudinaryService cloudinaryService;

    /**
     * Upload single image
     * @param file MultipartFile
     * @param folder Folder name (motels, rooms, avatars)
     * @return Image URL
     */
    @PostMapping("/image")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file, folder);
            
            Map<String, String> result = new HashMap<>();
            result.put("url", imageUrl);
            
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Upload ảnh thành công", result)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Lỗi khi upload ảnh: " + e.getMessage(), null));
        }
    }

    /**
     * Upload multiple images
     * @param files Array of MultipartFile
     * @param folder Folder name
     * @return List of image URLs
     */
    @PostMapping("/images")
    public ResponseEntity<ApiResponse<Map<String, List<String>>>> uploadMultipleImages(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "folder", defaultValue = "general") String folder) {
        try {
            List<String> imageUrls = new ArrayList<>();
            
            for (MultipartFile file : files) {
                if (!file.isEmpty()) {
                    String imageUrl = cloudinaryService.uploadImage(file, folder);
                    imageUrls.add(imageUrl);
                }
            }
            
            if (imageUrls.isEmpty()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(new ApiResponse<>(false, "Không có file nào được upload", null));
            }
            
            Map<String, List<String>> result = new HashMap<>();
            result.put("urls", imageUrls);
            
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Upload " + imageUrls.size() + " ảnh thành công", result)
            );
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Lỗi khi upload ảnh: " + e.getMessage(), null));
        }
    }

    /**
     * Delete image
     * @param imageUrl URL of image to delete
     */
    @DeleteMapping("/image")
    public ResponseEntity<ApiResponse<String>> deleteImage(@RequestParam("url") String imageUrl) {
        try {
            cloudinaryService.deleteImage(imageUrl);
            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Xóa ảnh thành công", null)
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, "Lỗi khi xóa ảnh: " + e.getMessage(), null));
        }
    }
}

