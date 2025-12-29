package com.hutech.demo.service;

import com.hutech.demo.dto.RoomDetailResponse;
import com.hutech.demo.dto.RoomRequest;
import com.hutech.demo.dto.RoomResponse;
import com.hutech.demo.dto.UserInfo;
import com.hutech.demo.model.Motel;
import com.hutech.demo.model.Room;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.MotelRepository;
import com.hutech.demo.repository.RoomRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class RoomService {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private MotelRepository motelRepository;

    @Autowired
    private UserRepository userRepository;

    // Tạo phòng mới
    public RoomResponse createRoom(RoomRequest request) {
        Motel motel = motelRepository.findById(request.getMotelId())
                .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));

        Room room = new Room();
        room.setMotel(motel);
        room.setRoomNumber(request.getRoomNumber());
        room.setPrice(request.getPrice());
        room.setArea(request.getArea());
        room.setMaxTenants(request.getMaxTenants() != null ? request.getMaxTenants() : 1);
        room.setStatus(request.getStatus() != null ? request.getStatus() : "available");
        room.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        room.setTenants(new ArrayList<>());
        room.setCreatedAt(LocalDateTime.now());
        room.setUpdatedAt(LocalDateTime.now());

        Room savedRoom = roomRepository.save(room);

        // Cập nhật danh sách phòng trong nhà trọ
        if (motel.getRooms() == null) {
            motel.setRooms(new ArrayList<>());
        }
        motel.getRooms().add(savedRoom);
        motel.setUpdatedAt(LocalDateTime.now());
        motelRepository.save(motel);

        return convertToResponse(savedRoom);
    }

    // Lấy tất cả phòng
    public List<RoomResponse> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy phòng theo ID
    public RoomResponse getRoomById(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return convertToResponse(room);
    }

    // Lấy phòng theo ID với đầy đủ thông tin (populated tenants)
    public RoomDetailResponse getRoomByIdWithDetails(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return convertToDetailResponse(room);
    }

    // Lấy phòng theo nhà trọ
    public List<RoomResponse> getRoomsByMotel(String motelId) {
        return roomRepository.findByMotelId(motelId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy phòng theo trạng thái
    public List<RoomResponse> getRoomsByStatus(String status) {
        return roomRepository.findByStatus(status).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Tìm kiếm phòng theo khoảng giá
    public List<RoomResponse> searchRoomsByPriceRange(Double minPrice, Double maxPrice) {
        return roomRepository.findByPriceBetween(minPrice, maxPrice).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Cập nhật phòng
    public RoomResponse updateRoom(String id, RoomRequest request) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        if (request.getMotelId() != null) {
            Motel motel = motelRepository.findById(request.getMotelId())
                    .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));
            room.setMotel(motel);
        }

        if (request.getRoomNumber() != null) {
            room.setRoomNumber(request.getRoomNumber());
        }
        if (request.getPrice() != null) {
            room.setPrice(request.getPrice());
        }
        if (request.getArea() != null) {
            room.setArea(request.getArea());
        }
        if (request.getMaxTenants() != null) {
            room.setMaxTenants(request.getMaxTenants());
        }
        if (request.getStatus() != null) {
            room.setStatus(request.getStatus());
        }
        if (request.getImages() != null) {
            room.setImages(request.getImages());
        }

        room.setUpdatedAt(LocalDateTime.now());

        Room updatedRoom = roomRepository.save(room);
        return convertToResponse(updatedRoom);
    }

    // Xóa phòng
    public void deleteRoom(String id) {
        Room room = roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        // Kiểm tra xem phòng trọ có người ở không
        if (room.getTenants() != null && !room.getTenants().isEmpty()) {
            int tenantCount = room.getTenants().size();
            throw new RuntimeException("Không thể xóa phòng trọ. Phòng này đang có " + tenantCount + " người ở. Vui lòng chuyển tất cả người thuê ra khỏi phòng trước khi xóa.");
        }

        // Xóa phòng khỏi danh sách của nhà trọ
        Motel motel = room.getMotel();
        if (motel != null && motel.getRooms() != null) {
            motel.getRooms().removeIf(r -> r.getId().equals(id));
            motel.setUpdatedAt(LocalDateTime.now());
            motelRepository.save(motel);
        }

        roomRepository.deleteById(id);
    }

    // Thêm người thuê vào phòng
    public RoomResponse addTenantToRoom(String roomId, String tenantId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        // Kiểm tra role của user có phải là TENANT không
        if (!tenant.getRole().equals("ROLE_TENANT") && !tenant.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Chỉ người thuê mới có thể thuê phòng");
        }

        // Kiểm tra người thuê đã có phòng chưa
        if (tenant.getCurrentRoomId() != null && !tenant.getCurrentRoomId().isEmpty()) {
            throw new RuntimeException("Người thuê đã thuê phòng khác");
        }

        if (room.getTenants() == null) {
            room.setTenants(new ArrayList<>());
        }

        // Kiểm tra số lượng người thuê tối đa
        if (room.getTenants().size() >= room.getMaxTenants()) {
            throw new RuntimeException("Phòng đã đạt số lượng người thuê tối đa");
        }

        // Kiểm tra người thuê đã tồn tại chưa
        boolean alreadyExists = room.getTenants().stream()
                .anyMatch(t -> t.getId().equals(tenantId));

        if (alreadyExists) {
            throw new RuntimeException("Người thuê đã ở trong phòng này");
        }

        room.getTenants().add(tenant);

        // Cập nhật trạng thái phòng
        if (room.getTenants().size() >= room.getMaxTenants()) {
            room.setStatus("occupied");
        }

        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);

        // Cập nhật currentRoomId của tenant
        tenant.setCurrentRoomId(roomId);
        tenant.setUpdatedAt(LocalDateTime.now());
        userRepository.save(tenant);

        return convertToResponse(updatedRoom);
    }

    // Xóa người thuê khỏi phòng
    public RoomResponse removeTenantFromRoom(String roomId, String tenantId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));

        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Người dùng không tồn tại"));

        if (room.getTenants() == null) {
            throw new RuntimeException("Phòng không có người thuê");
        }

        boolean removed = room.getTenants().removeIf(t -> t.getId().equals(tenantId));

        if (!removed) {
            throw new RuntimeException("Người thuê không ở trong phòng này");
        }

        // Cập nhật trạng thái phòng
        if (room.getTenants().isEmpty()) {
            room.setStatus("available");
        } else if (room.getTenants().size() < room.getMaxTenants() && room.getStatus().equals("occupied")) {
            room.setStatus("available");
        }

        room.setUpdatedAt(LocalDateTime.now());
        Room updatedRoom = roomRepository.save(room);

        // Xóa currentRoomId của tenant
        tenant.setCurrentRoomId(null);
        tenant.setUpdatedAt(LocalDateTime.now());
        userRepository.save(tenant);

        return convertToResponse(updatedRoom);
    }

    // Kiểm tra phòng có sẵn không
    public boolean isRoomAvailable(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return "available".equals(room.getStatus()) &&
                (room.getTenants() == null || room.getTenants().size() < room.getMaxTenants());
    }

    // Lấy danh sách phòng trống trong nhà trọ
    public List<RoomResponse> getAvailableRoomsByMotel(String motelId) {
        return roomRepository.findByMotelIdAndStatus(motelId, "available").stream()
                .filter(room -> room.getTenants() == null || room.getTenants().size() < room.getMaxTenants())
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy danh sách người thuê trong phòng
    public List<User> getTenantsInRoom(String roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Phòng không tồn tại"));
        return room.getTenants() != null ? room.getTenants() : new ArrayList<>();
    }

    // Convert Room entity to RoomResponse DTO
    private RoomResponse convertToResponse(Room room) {
        RoomResponse response = new RoomResponse();
        response.setId(room.getId());
        
        // Handle null motel reference (DBRef might not be loaded)
        if (room.getMotel() != null) {
            response.setMotelId(room.getMotel().getId());
            response.setMotelName(room.getMotel().getName());
        } else {
            response.setMotelId(null);
            response.setMotelName("N/A");
        }
        
        response.setRoomNumber(room.getRoomNumber());
        response.setPrice(room.getPrice());
        response.setArea(room.getArea());
        response.setMaxTenants(room.getMaxTenants());
        response.setStatus(room.getStatus());
        response.setDescription(room.getDescription());
        response.setFacilities(room.getFacilities());

        if (room.getTenants() != null) {
            response.setTenantIds(room.getTenants().stream()
                    .map(User::getId)
                    .collect(Collectors.toList()));
            response.setTenantNames(room.getTenants().stream()
                    .map(User::getFullName)
                    .collect(Collectors.toList()));
        } else {
            response.setTenantIds(new ArrayList<>());
            response.setTenantNames(new ArrayList<>());
        }

        response.setImages(room.getImages());
        response.setCreatedAt(room.getCreatedAt());
        response.setUpdatedAt(room.getUpdatedAt());
        return response;
    }

    // Convert Room entity to RoomDetailResponse DTO (with populated tenants)
    private RoomDetailResponse convertToDetailResponse(Room room) {
        RoomDetailResponse response = new RoomDetailResponse();
        response.setId(room.getId());
        
        // Handle null motel reference (DBRef might not be loaded)
        if (room.getMotel() != null) {
            response.setMotelId(room.getMotel().getId());
            response.setMotelName(room.getMotel().getName());
        } else {
            response.setMotelId(null);
            response.setMotelName("N/A");
        }
        
        response.setRoomNumber(room.getRoomNumber());
        response.setPrice(room.getPrice());
        response.setArea(room.getArea());
        response.setMaxTenants(room.getMaxTenants());
        response.setStatus(room.getStatus());
        response.setDescription(room.getDescription());
        response.setFacilities(room.getFacilities());

        // Populate full tenant info
        if (room.getTenants() != null && !room.getTenants().isEmpty()) {
            List<UserInfo> tenantInfos = room.getTenants().stream()
                    .map(UserInfo::fromUser)
                    .collect(Collectors.toList());
            response.setTenants(tenantInfos);
        } else {
            response.setTenants(new ArrayList<>());
        }

        response.setImages(room.getImages());
        response.setCreatedAt(room.getCreatedAt());
        response.setUpdatedAt(room.getUpdatedAt());
        return response;
    }
}
