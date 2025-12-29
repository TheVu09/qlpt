package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.RoomDetailResponse;
import com.hutech.demo.dto.RoomRequest;
import com.hutech.demo.dto.RoomResponse;
import com.hutech.demo.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/rooms")
@CrossOrigin(origins = "*")
public class RoomController {

    @Autowired
    private RoomService roomService;

    // Tạo phòng mới
    @PostMapping
    public ResponseEntity<ApiResponse<RoomResponse>> createRoom(@RequestBody RoomRequest request) {
        try {
            RoomResponse room = roomService.createRoom(request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tạo phòng thành công", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy tất cả phòng
    @GetMapping
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAllRooms() {
        try {
            List<RoomResponse> rooms = roomService.getAllRooms();
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phòng thành công", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy phòng theo ID
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> getRoomById(@PathVariable String id) {
        try {
            RoomResponse room = roomService.getRoomById(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phòng thành công", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy phòng theo ID với đầy đủ thông tin (populated tenants)
    @GetMapping("/{id}/details")
    public ResponseEntity<ApiResponse<RoomDetailResponse>> getRoomByIdWithDetails(@PathVariable String id) {
        try {
            RoomDetailResponse room = roomService.getRoomByIdWithDetails(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy thông tin phòng chi tiết thành công", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy phòng theo nhà trọ
    @GetMapping("/motel/{motelId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByMotel(@PathVariable String motelId) {
        try {
            List<RoomResponse> rooms = roomService.getRoomsByMotel(motelId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phòng theo nhà trọ thành công", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy phòng theo trạng thái
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getRoomsByStatus(@PathVariable String status) {
        try {
            List<RoomResponse> rooms = roomService.getRoomsByStatus(status);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phòng theo trạng thái thành công", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Tìm kiếm phòng theo khoảng giá
    @GetMapping("/search/price")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> searchByPriceRange(
            @RequestParam Double minPrice,
            @RequestParam Double maxPrice) {
        try {
            List<RoomResponse> rooms = roomService.searchRoomsByPriceRange(minPrice, maxPrice);
            return ResponseEntity.ok(new ApiResponse<>(true, "Tìm kiếm phòng theo giá thành công", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Cập nhật phòng
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<RoomResponse>> updateRoom(
            @PathVariable String id,
            @RequestBody RoomRequest request) {
        try {
            RoomResponse room = roomService.updateRoom(id, request);
            return ResponseEntity.ok(new ApiResponse<>(true, "Cập nhật phòng thành công", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa phòng
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRoom(@PathVariable String id) {
        try {
            roomService.deleteRoom(id);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa phòng thành công", null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Thêm người thuê vào phòng
    @PostMapping("/{roomId}/tenants/{tenantId}")
    public ResponseEntity<ApiResponse<RoomResponse>> addTenant(
            @PathVariable String roomId,
            @PathVariable String tenantId) {
        try {
            RoomResponse room = roomService.addTenantToRoom(roomId, tenantId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Thêm người thuê thành công", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Xóa người thuê khỏi phòng
    @DeleteMapping("/{roomId}/tenants/{tenantId}")
    public ResponseEntity<ApiResponse<RoomResponse>> removeTenant(
            @PathVariable String roomId,
            @PathVariable String tenantId) {
        try {
            RoomResponse room = roomService.removeTenantFromRoom(roomId, tenantId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Xóa người thuê thành công", room));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy danh sách phòng trống (có slot) - Public API không cần đăng nhập
    @GetMapping("/available")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRooms() {
        try {
            List<RoomResponse> rooms = roomService.getRoomsByStatus("available");
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phòng trống thành công", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Lấy phòng trống theo nhà trọ - Public API
    @GetMapping("/available/motel/{motelId}")
    public ResponseEntity<ApiResponse<List<RoomResponse>>> getAvailableRoomsByMotel(
            @PathVariable String motelId) {
        try {
            List<RoomResponse> rooms = roomService.getAvailableRoomsByMotel(motelId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách phòng trống thành công", rooms));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }

    // Kiểm tra phòng còn chỗ không
    @GetMapping("/{roomId}/available")
    public ResponseEntity<ApiResponse<Boolean>> checkRoomAvailability(
            @PathVariable String roomId) {
        try {
            boolean available = roomService.isRoomAvailable(roomId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Kiểm tra thành công", available));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
}
