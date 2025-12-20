package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.MotelRequest;
import com.hutech.demo.dto.MotelResponse;
import com.hutech.demo.service.MotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/motels")
@CrossOrigin(origins = "*")
public class MotelController {

    @Autowired
    private MotelService motelService;

    // Tạo nhà trọ mới
    @PostMapping
    public ResponseEntity<ApiResponse<MotelResponse>> createMotel(@RequestBody MotelRequest request) {
        try {
            MotelResponse motel = motelService.createMotel(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo nhà trọ thành công", motel));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả nhà trọ
    @GetMapping
    public ResponseEntity<ApiResponse<List<MotelResponse>>> getAllMotels() {
        try {
            List<MotelResponse> motels = motelService.getAllMotels();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách nhà trọ thành công", motels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy nhà trọ theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MotelResponse>> getMotelById(@PathVariable String id) {
        try {
            MotelResponse motel = motelService.getMotelById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin nhà trọ thành công", motel));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy nhà trọ theo chủ nhà
    @GetMapping("/landlord/{landlordId}")
    public ResponseEntity<ApiResponse<List<MotelResponse>>> getMotelsByLandlord(@PathVariable String landlordId) {
        try {
            List<MotelResponse> motels = motelService.getMotelsByLandlord(landlordId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách nhà trọ theo chủ nhà thành công", motels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Tìm kiếm nhà trọ theo tên
    @GetMapping("/search/name")
    public ResponseEntity<ApiResponse<List<MotelResponse>>> searchByName(@RequestParam String name) {
        try {
            List<MotelResponse> motels = motelService.searchMotelsByName(name);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm nhà trọ theo tên thành công", motels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Tìm kiếm nhà trọ theo địa chỉ
    @GetMapping("/search/address")
    public ResponseEntity<ApiResponse<List<MotelResponse>>> searchByAddress(@RequestParam String address) {
        try {
            List<MotelResponse> motels = motelService.searchMotelsByAddress(address);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm nhà trọ theo địa chỉ thành công", motels));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Cập nhật nhà trọ
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<MotelResponse>> updateMotel(
            @PathVariable String id,
            @RequestBody MotelRequest request) {
        try {
            MotelResponse motel = motelService.updateMotel(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật nhà trọ thành công", motel));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa nhà trọ
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMotel(@PathVariable String id) {
        try {
            motelService.deleteMotel(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa nhà trọ thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}

