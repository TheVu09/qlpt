package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "services")
public class ServiceEntity {

    @Id
    private String id;

    private String name; // Electricity, Water, Internet, etc.

    private Double price; // Unit price

    private String unit; // KWH, M3, PERSON, ROOM, MONTH

    private String description;
}
