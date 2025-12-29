package com.hutech.demo.service;

import com.hutech.demo.dto.MotelRequest;
import com.hutech.demo.dto.MotelResponse;
import com.hutech.demo.model.Motel;
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
public class MotelService {

    @Autowired
    private MotelRepository motelRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    // Tạo nhà trọ mới
    public MotelResponse createMotel(MotelRequest request) {
        User landlord = userRepository.findById(request.getLandlordId())
                .orElseThrow(() -> new RuntimeException("Chủ nhà trọ không tồn tại"));

        // Kiểm tra role của user có phải là LANDLORD không
        if (!landlord.getRole().equals("ROLE_LANDLORD") && !landlord.getRole().equals("ROLE_ADMIN")) {
            throw new RuntimeException("Chỉ chủ nhà trọ mới có thể tạo nhà trọ");
        }

        Motel motel = new Motel();
        motel.setLandlord(landlord);
        motel.setName(request.getName());
        motel.setAddress(request.getAddress());
        motel.setDescription(request.getDescription());
        motel.setImages(request.getImages() != null ? request.getImages() : new ArrayList<>());
        motel.setRooms(new ArrayList<>());
        motel.setCreatedAt(LocalDateTime.now());
        motel.setUpdatedAt(LocalDateTime.now());

        Motel savedMotel = motelRepository.save(motel);

        // Cập nhật danh sách motelIds của landlord
        if (landlord.getMotelIds() == null) {
            landlord.setMotelIds(new ArrayList<>());
        }
        landlord.getMotelIds().add(savedMotel.getId());
        landlord.setUpdatedAt(LocalDateTime.now());
        userRepository.save(landlord);

        return convertToResponse(savedMotel);
    }

    // Lấy tất cả nhà trọ
    public List<MotelResponse> getAllMotels() {
        return motelRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Lấy nhà trọ theo ID
    public MotelResponse getMotelById(String id) {
        Motel motel = motelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));
        return convertToResponse(motel);
    }

    // Lấy nhà trọ theo chủ nhà
    public List<MotelResponse> getMotelsByLandlord(String landlordId) {
        return motelRepository.findByLandlordId(landlordId).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Tìm kiếm nhà trọ theo tên
    public List<MotelResponse> searchMotelsByName(String name) {
        return motelRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Tìm kiếm nhà trọ theo địa chỉ
    public List<MotelResponse> searchMotelsByAddress(String address) {
        return motelRepository.findByAddressContainingIgnoreCase(address).stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Cập nhật nhà trọ
    public MotelResponse updateMotel(String id, MotelRequest request) {
        Motel motel = motelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));

        if (request.getLandlordId() != null) {
            User landlord = userRepository.findById(request.getLandlordId())
                    .orElseThrow(() -> new RuntimeException("Chủ nhà trọ không tồn tại"));
            motel.setLandlord(landlord);
        }

        if (request.getName() != null) {
            motel.setName(request.getName());
        }
        if (request.getAddress() != null) {
            motel.setAddress(request.getAddress());
        }
        if (request.getDescription() != null) {
            motel.setDescription(request.getDescription());
        }
        if (request.getImages() != null) {
            motel.setImages(request.getImages());
        }

        motel.setUpdatedAt(LocalDateTime.now());

        Motel updatedMotel = motelRepository.save(motel);
        return convertToResponse(updatedMotel);
    }

    // Xóa nhà trọ
    public void deleteMotel(String id) {
        Motel motel = motelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));

        // Kiểm tra xem khu trọ có phòng trọ nào không
        List<com.hutech.demo.model.Room> roomsInMotel = roomRepository.findByMotelId(id);
        if (roomsInMotel != null && !roomsInMotel.isEmpty()) {
            throw new RuntimeException("Không thể xóa khu trọ. Khu trọ này đang có " + roomsInMotel.size() + " phòng trọ. Vui lòng xóa tất cả phòng trọ trước khi xóa khu trọ.");
        }

        // Xóa motelId khỏi danh sách của landlord
        User landlord = motel.getLandlord();
        if (landlord != null && landlord.getMotelIds() != null) {
            landlord.getMotelIds().removeIf(motelId -> motelId.equals(id));
            landlord.setUpdatedAt(LocalDateTime.now());
            userRepository.save(landlord);
        }

        motelRepository.deleteById(id);
    }

    // Kiểm tra quyền sở hữu nhà trọ
    public boolean isMotelOwner(String motelId, String userId) {
        Motel motel = motelRepository.findById(motelId)
                .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));
        return motel.getLandlord().getId().equals(userId);
    }

    // Lấy thống kê nhà trọ
    public MotelStatistics getMotelStatistics(String motelId) {
        Motel motel = motelRepository.findById(motelId)
                .orElseThrow(() -> new RuntimeException("Nhà trọ không tồn tại"));

        int totalRooms = motel.getRooms() != null ? motel.getRooms().size() : 0;
        long availableRooms = motel.getRooms() != null
                ? motel.getRooms().stream().filter(room -> "available".equals(room.getStatus())).count()
                : 0;
        long occupiedRooms = motel.getRooms() != null
                ? motel.getRooms().stream().filter(room -> "occupied".equals(room.getStatus())).count()
                : 0;
        long maintenanceRooms = motel.getRooms() != null
                ? motel.getRooms().stream().filter(room -> "maintenance".equals(room.getStatus())).count()
                : 0;

        return new MotelStatistics(
                motelId,
                motel.getName(),
                totalRooms,
                (int) availableRooms,
                (int) occupiedRooms,
                (int) maintenanceRooms);
    }

    // Convert Motel entity to MotelResponse DTO
    private MotelResponse convertToResponse(Motel motel) {
        MotelResponse response = new MotelResponse();
        response.setId(motel.getId());
        response.setLandlordId(motel.getLandlord().getId());
        response.setLandlordName(motel.getLandlord().getFullName());
        response.setName(motel.getName());
        response.setAddress(motel.getAddress());
        response.setDescription(motel.getDescription());
        response.setImages(motel.getImages());
        response.setTotalRooms(motel.getRooms() != null ? motel.getRooms().size() : 0);
        response.setCreatedAt(motel.getCreatedAt());
        response.setUpdatedAt(motel.getUpdatedAt());
        return response;
    }

    // Inner class for statistics
    public static class MotelStatistics {
        private String motelId;
        private String motelName;
        private int totalRooms;
        private int availableRooms;
        private int occupiedRooms;
        private int maintenanceRooms;

        public MotelStatistics(String motelId, String motelName, int totalRooms,
                int availableRooms, int occupiedRooms, int maintenanceRooms) {
            this.motelId = motelId;
            this.motelName = motelName;
            this.totalRooms = totalRooms;
            this.availableRooms = availableRooms;
            this.occupiedRooms = occupiedRooms;
            this.maintenanceRooms = maintenanceRooms;
        }

        // Getters and setters
        public String getMotelId() {
            return motelId;
        }

        public void setMotelId(String motelId) {
            this.motelId = motelId;
        }

        public String getMotelName() {
            return motelName;
        }

        public void setMotelName(String motelName) {
            this.motelName = motelName;
        }

        public int getTotalRooms() {
            return totalRooms;
        }

        public void setTotalRooms(int totalRooms) {
            this.totalRooms = totalRooms;
        }

        public int getAvailableRooms() {
            return availableRooms;
        }

        public void setAvailableRooms(int availableRooms) {
            this.availableRooms = availableRooms;
        }

        public int getOccupiedRooms() {
            return occupiedRooms;
        }

        public void setOccupiedRooms(int occupiedRooms) {
            this.occupiedRooms = occupiedRooms;
        }

        public int getMaintenanceRooms() {
            return maintenanceRooms;
        }

        public void setMaintenanceRooms(int maintenanceRooms) {
            this.maintenanceRooms = maintenanceRooms;
        }
    }
}
