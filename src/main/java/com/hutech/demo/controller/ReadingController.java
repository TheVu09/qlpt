package com.hutech.demo.controller;

import com.hutech.demo.model.MeterReading;
import com.hutech.demo.service.MeterReadingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/readings")
@CrossOrigin("*")
public class ReadingController {

    @Autowired
    private MeterReadingService meterReadingService;

    @PostMapping
    public MeterReading recordReading(
            @RequestParam String roomId,
            @RequestParam String serviceId,
            @RequestParam Double newIndex,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return meterReadingService.recordReading(roomId, serviceId, newIndex, month, year);
    }

    @GetMapping("/room/{roomId}")
    public List<MeterReading> getReadingsByRoom(@PathVariable String roomId) {
        return meterReadingService.getReadingsByRoom(roomId);
    }
}
