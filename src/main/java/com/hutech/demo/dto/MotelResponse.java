package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MotelResponse {
    private String id;
    private String landlordId;
    private String landlordName;
    private String name;
    private String address;
    private String description;
    private List<String> images;
    private Integer totalRooms;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

