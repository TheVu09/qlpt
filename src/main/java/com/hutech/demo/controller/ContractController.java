package com.hutech.demo.controller;

import com.hutech.demo.model.Contract;
import com.hutech.demo.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@CrossOrigin("*")
public class ContractController {

    @Autowired
    private ContractService contractService;

    @PostMapping
    public Contract createContract(
            @RequestParam String roomId,
            @RequestParam String tenantId,
            @RequestParam Double deposit,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        return contractService.createContract(roomId, tenantId, deposit, LocalDate.parse(startDate), LocalDate.parse(endDate));
    }

    @GetMapping
    public List<Contract> getAllContracts() {
        return contractService.getAllContracts();
    }
    
    @GetMapping("/tenant/{tenantId}")
    public List<Contract> getContractsByTenant(@PathVariable String tenantId) {
        return contractService.getContractsByTenant(tenantId);
    }
    
    @PostMapping("/{id}/terminate")
    public Contract terminateContract(@PathVariable String id) {
        return contractService.terminateContract(id);
    }
}
