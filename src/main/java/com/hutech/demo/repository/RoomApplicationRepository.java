package com.hutech.demo.repository;

import com.hutech.demo.model.RoomApplication;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface RoomApplicationRepository extends MongoRepository<RoomApplication, String> {
    List<RoomApplication> findByApplicantId(String applicantId);

    List<RoomApplication> findByRoomId(String roomId);

    List<RoomApplication> findByStatus(String status);

    List<RoomApplication> findByRoomMotelId(String motelId);

    Optional<RoomApplication> findByApplicantIdAndRoomIdAndStatus(String applicantId, String roomId, String status);
}
