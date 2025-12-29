package com.hutech.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {
    private String id;
    
    // Room info (populated)
    private String roomId;
    private String roomNumber;
    private String motelId;
    private String motelName;
    
    // Tenant info (populated)
    private String tenantId;
    private String tenantName;
    private String tenantEmail;
    private String tenantPhone;
    private String tenantAvatar;
    
    private LocalDate startDate;
    private LocalDate endDate;
    private Double deposit;
    private Double monthlyPrice;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

