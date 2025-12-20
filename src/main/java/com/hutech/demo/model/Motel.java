package com.hutech.demo.model;

import lombok.AllArgsConstructor;
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
@Document(collection = "motels")
public class Motel {

    @Id
    private String id;

    @DBRef
    private User landlord; // Chủ nhà trọ

    private String name;

    private String address;

    private String description;

    private List<String> images = new ArrayList<>();

    @DBRef
    private List<Room> rooms = new ArrayList<>();

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

