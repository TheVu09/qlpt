package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.model.RoomApplication;
import com.hutech.demo.service.RoomApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/applications")
@CrossOrigin(origins = "*")
public class RoomApplicationController {

    @Autowired
    private RoomApplicationService applicationService;

    // Tạo đơn đăng ký phòng (User)
    @PostMapping("/create")
    public ResponseEntity<ApiResponse<RoomApplication>> createApplication(
            @RequestBody Map<String, String> request) {
        try {
            String userId = request.get("userId");
            String roomId = request.get("roomId");
            String message = request.get("message");

            RoomApplication application = applicationService.createApplication(userId, roomId, message);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo đơn đăng ký thành công", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả đơn đăng ký (Admin)
    @GetMapping("/all")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<List<RoomApplication>>> getAllApplications() {
        try {
            List<RoomApplication> applications = applicationService.getAllApplications();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách đơn thành công", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy đơn đăng ký theo trạng thái (Admin)
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RoomApplication>>> getApplicationsByStatus(
            @PathVariable String status) {
        try {
            List<RoomApplication> applications = applicationService.getApplicationsByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách đơn thành công", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy đơn đăng ký của user
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<RoomApplication>>> getApplicationsByUser(
            @PathVariable String userId) {
        try {
            List<RoomApplication> applications = applicationService.getApplicationsByUser(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách đơn thành công", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy đơn đăng ký theo phòng (Admin)
    @GetMapping("/room/{roomId}")
    public ResponseEntity<ApiResponse<List<RoomApplication>>> getApplicationsByRoom(
            @PathVariable String roomId) {
        try {
            List<RoomApplication> applications = applicationService.getApplicationsByRoom(roomId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách đơn thành công", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy đơn đăng ký theo khu trọ (Admin)
    @GetMapping("/motel/{motelId}")
    public ResponseEntity<ApiResponse<List<RoomApplication>>> getApplicationsByMotel(
            @PathVariable String motelId) {
        try {
            List<RoomApplication> applications = applicationService.getApplicationsByMotel(motelId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách đơn thành công", applications));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Duyệt đơn đăng ký (Admin)
    @PostMapping("/{applicationId}/approve")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<RoomApplication>> approveApplication(
            @PathVariable String applicationId,
            @RequestBody Map<String, String> request) {
        try {
            String adminId = request.get("adminId");
            String adminNote = request.get("adminNote");

            RoomApplication application = applicationService.approveApplication(
                    applicationId, adminId, adminNote);
            return ResponseEntity.ok(new ApiResponse<>(true, "Duyệt đơn thành công", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Từ chối đơn đăng ký (Admin)
    @PostMapping("/{applicationId}/reject")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<RoomApplication>> rejectApplication(
            @PathVariable String applicationId,
            @RequestBody Map<String, String> request) {
        try {
            String adminId = request.get("adminId");
            String adminNote = request.get("adminNote");

            RoomApplication application = applicationService.rejectApplication(
                    applicationId, adminId, adminNote);
            return ResponseEntity.ok(new ApiResponse<>(true, "Từ chối đơn thành công", application));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Hủy đơn đăng ký (User)
    @DeleteMapping("/{applicationId}/cancel")
    public ResponseEntity<ApiResponse<String>> cancelApplication(
            @PathVariable String applicationId,
            @RequestParam String userId) {
        try {
            applicationService.cancelApplication(applicationId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Hủy đơn thành công", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy thống kê đơn đăng ký (Admin)
    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN') or hasRole('LANDLORD')")
    public ResponseEntity<ApiResponse<RoomApplicationService.ApplicationStatistics>> getStatistics() {
        try {
            RoomApplicationService.ApplicationStatistics stats = applicationService.getStatistics();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thống kê thành công", stats));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
