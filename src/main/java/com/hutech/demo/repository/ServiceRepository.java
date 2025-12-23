package com.hutech.demo.repository;

import com.hutech.demo.model.ServiceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ServiceRepository extends MongoRepository<ServiceEntity, String> {
    Optional<ServiceEntity> findByName(String name);
}
