package com.hutech.demo.service;

import com.hutech.demo.dto.MotelRequest;
import com.hutech.demo.dto.MotelResponse;
import com.hutech.demo.model.Motel;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.MotelRepository;
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

    // Tạo nhà trọ mới
    public MotelResponse createMotel(MotelRequest request) {
        User landlord = userRepository.findById(request.getLandlordId())
                .orElseThrow(() -> new RuntimeException("Chủ nhà trọ không tồn tại"));

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
        if (!motelRepository.existsById(id)) {
            throw new RuntimeException("Nhà trọ không tồn tại");
        }
        motelRepository.deleteById(id);
    }

    // Convert Motel entity to MotelResponse DTO
    private MotelResponse convertToResponse(Motel motel) {
        MotelResponse response = new MotelResponse();
        response.setId(motel.getId());
        response.setLandlordId(motel.getLandlord().getId());
        response.setLandlordName(motel.getLandlord().getUsername());
        response.setName(motel.getName());
        response.setAddress(motel.getAddress());
        response.setDescription(motel.getDescription());
        response.setImages(motel.getImages());
        response.setTotalRooms(motel.getRooms() != null ? motel.getRooms().size() : 0);
        response.setCreatedAt(motel.getCreatedAt());
        response.setUpdatedAt(motel.getUpdatedAt());
        return response;
    }
}

