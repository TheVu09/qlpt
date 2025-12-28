package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "contracts")
public class Contract {

    @Id
    private String id;

    @DBRef
    private Room room;

    @DBRef
    private User tenant;

    private LocalDate startDate; // Ngày bắt đầu hợp đồng

    private LocalDate endDate; // Ngày kết thúc hợp đồng

    private Double deposit; // Tiền cọc

    private Double monthlyPrice; // Giá thuê chốt tại thời điểm ký

    private String status; // ACTIVE, TERMINATED, EXPIRED

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
