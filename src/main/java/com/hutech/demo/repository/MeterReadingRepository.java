package com.hutech.demo.repository;

import com.hutech.demo.model.MeterReading;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MeterReadingRepository extends MongoRepository<MeterReading, String> {
    List<MeterReading> findByRoomId(String roomId);
    Optional<MeterReading> findByRoomIdAndServiceIdAndMonthAndYear(String roomId, String serviceId, Integer month, Integer year);
}
