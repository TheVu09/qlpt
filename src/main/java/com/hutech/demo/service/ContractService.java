package com.hutech.demo.service;

import com.hutech.demo.model.Contract;
import com.hutech.demo.model.Room;
import com.hutech.demo.model.User;
import com.hutech.demo.repository.ContractRepository;
import com.hutech.demo.repository.RoomRepository;
import com.hutech.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContractService {

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    public Contract createContract(String roomId, String tenantId, Double deposit, LocalDate startDate, LocalDate endDate) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));
        User tenant = userRepository.findById(tenantId)
                .orElseThrow(() -> new RuntimeException("Tenant not found"));

        Contract contract = new Contract();
        contract.setRoom(room);
        contract.setTenant(tenant);
        contract.setDeposit(deposit);
        contract.setMonthlyPrice(room.getPrice()); // Lock price at contract start
        contract.setStartDate(startDate);
        contract.setEndDate(endDate);
        contract.setStatus("ACTIVE");
        contract.setCreatedAt(LocalDateTime.now());
        contract.setUpdatedAt(LocalDateTime.now());

        // Update room status
        room.setStatus("occupied");
        roomRepository.save(room);

        return contractRepository.save(contract);
    }

    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public List<Contract> getContractsByTenant(String tenantId) {
        return contractRepository.findByTenantId(tenantId);
    }
    
    public List<Contract> getContractsByRoom(String roomId) {
        return contractRepository.findByRoomId(roomId);
    }

    public Contract terminateContract(String contractId) {
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        contract.setStatus("TERMINATED");
        contract.setUpdatedAt(LocalDateTime.now());
        
        // Check if there are other active contracts for this room before freeing it
        // simplified logic: if terminated, you might want to free the room or not depending on context. 
        // Here assuming if contract ends, room might be available.
        // real logic needs to check if room is shared or private.
        
        return contractRepository.save(contract);
    }
}
