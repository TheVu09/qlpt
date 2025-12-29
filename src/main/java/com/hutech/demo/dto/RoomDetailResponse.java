package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomDetailResponse {
    private String id;
    private String motelId;
    private String motelName;
    private String roomNumber;
    private Double price;
    private Double area;
    private Integer maxTenants;
    private String status;
    private String description;
    private String facilities;
    private List<UserInfo> tenants; // Full tenant info (populated)
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

