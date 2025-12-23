package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "invoices")
public class Invoice {

    @Id
    private String id;

    @DBRef
    private Contract contract;

    private Integer month;

    private Integer year;

    private Double roomFee; // Tiền phòng

    private Double serviceFee; // Tổng tiền dịch vụ

    private Double totalAmount; // Tổng cộng

    private String status; // UNPAID, PAID, OVERDUE

    private LocalDateTime createdAt;
    
    private LocalDateTime paymentDate;
}
