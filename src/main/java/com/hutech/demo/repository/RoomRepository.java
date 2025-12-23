package com.hutech.demo.repository;

import com.hutech.demo.model.Room;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface RoomRepository extends MongoRepository<Room, String> {
    List<Room> findByMotelId(String motelId);
    List<Room> findByStatus(String status);
    List<Room> findByMotelIdAndStatus(String motelId, String status);
    List<Room> findByPriceBetween(Double minPrice, Double maxPrice);
}


