package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "rooms")
public class Room {

    @Id
    private String id;

    @DBRef
    private Motel motel; // Tham chiếu đến nhà trọ

    private String roomNumber; // Số phòng

    private Double price; // Giá thuê

    private Double area; // Diện tích (m2)

    private Integer maxTenants = 4; // Số người tối đa (mặc định 4)

    private String status; // available, occupied, maintenance

    @DBRef
    private List<User> tenants = new ArrayList<>(); // Danh sách người thuê

    private List<String> images = new ArrayList<>(); // Danh sách hình ảnh

    private String description; // Mô tả phòng

    private String facilities; // Tiện nghi (điều hòa, tủ lạnh, giường, ...)

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    // Tính số slot còn trống
    public int getAvailableSlots() {
        return maxTenants - (tenants != null ? tenants.size() : 0);
    }

    // Kiểm tra phòng có còn chỗ không
    public boolean hasAvailableSlots() {
        return getAvailableSlots() > 0 && "available".equals(status);
    }
}
