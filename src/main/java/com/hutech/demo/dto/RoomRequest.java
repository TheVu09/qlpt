package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomRequest {
    private String motelId;
    private String roomNumber;
    private Double price;
    private Double area;
    private Integer maxTenants;
    private String status;
    private List<String> images;
}

