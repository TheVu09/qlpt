package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomResponse {
    private String id;
    private String motelId;
    private String motelName;
    private String roomNumber;
    private Double price;
    private Double area;
    private Integer maxTenants;
    private String status;
    private List<String> tenantIds;
    private List<String> tenantNames;
    private List<String> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

