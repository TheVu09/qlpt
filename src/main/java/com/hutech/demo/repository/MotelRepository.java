package com.hutech.demo.repository;

import com.hutech.demo.model.Motel;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MotelRepository extends MongoRepository<Motel, String> {
    List<Motel> findByLandlordId(String landlordId);
    List<Motel> findByNameContainingIgnoreCase(String name);
    List<Motel> findByAddressContainingIgnoreCase(String address);
}

