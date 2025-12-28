package com.hutech.demo.service;

import com.hutech.demo.model.MeterReading;
import com.hutech.demo.model.Room;
import com.hutech.demo.model.ServiceEntity;
import com.hutech.demo.repository.MeterReadingRepository;
import com.hutech.demo.repository.RoomRepository;
import com.hutech.demo.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MeterReadingService {

    @Autowired
    private MeterReadingRepository meterReadingRepository;
    
    @Autowired
    private RoomRepository roomRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;

    public MeterReading recordReading(String roomId, String serviceId, Double newIndex, Integer month, Integer year) {
        Room room = roomRepository.findById(roomId)
             .orElseThrow(() -> new RuntimeException("Room not found"));
        ServiceEntity service = serviceRepository.findById(serviceId)
             .orElseThrow(() -> new RuntimeException("Service not found"));
             
        // Check if reading exists for this month
        Optional<MeterReading> existing = meterReadingRepository.findByRoomIdAndServiceIdAndMonthAndYear(roomId, serviceId, month, year);
        if (existing.isPresent()) {
            throw new RuntimeException("Reading already exists for this room, service and month");
        }

        MeterReading reading = new MeterReading();
        reading.setRoom(room);
        reading.setService(service);
        reading.setNewIndex(newIndex);
        
        // Auto-fetch old index
        Integer prevMonth = (month == 1) ? 12 : month - 1;
        Integer prevYear = (month == 1) ? year - 1 : year;
        Optional<MeterReading> prevReading = meterReadingRepository.findByRoomIdAndServiceIdAndMonthAndYear(roomId, serviceId, prevMonth, prevYear);
        if (prevReading.isPresent()) {
            reading.setOldIndex(prevReading.get().getNewIndex());
        } else {
            reading.setOldIndex(0.0);
        }

        reading.setMonth(month);
        reading.setYear(year);
        reading.setRecordedDate(LocalDate.now());
        
        return meterReadingRepository.save(reading);
    }
    
    public List<MeterReading> getReadingsByRoom(String roomId) {
        return meterReadingRepository.findByRoomId(roomId);
    }
}
