package com.hutech.demo.repository;

import com.hutech.demo.model.Contract;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContractRepository extends MongoRepository<Contract, String> {
    List<Contract> findByTenantId(String tenantId);
    List<Contract> findByRoomId(String roomId);
    List<Contract> findByStatus(String status);
}
