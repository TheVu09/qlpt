package com.hutech.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "meter_readings")
public class MeterReading {

    @Id
    private String id;

    @DBRef
    private Room room;

    @DBRef
    private ServiceEntity service; // Should be Electricity or Water

    private Double oldIndex;

    private Double newIndex;

    private Integer month;

    private Integer year;

    private LocalDate recordedDate;
}
