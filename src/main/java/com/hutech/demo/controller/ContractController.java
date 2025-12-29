package com.hutech.demo.controller;

import com.hutech.demo.dto.ApiResponse;
import com.hutech.demo.dto.ContractResponse;
import com.hutech.demo.model.Contract;
import com.hutech.demo.service.ContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    // Lấy contracts theo tenant với populated data
    @GetMapping("/tenant/{tenantId}/details")
    public ResponseEntity<ApiResponse<List<ContractResponse>>> getContractsByTenantWithDetails(@PathVariable String tenantId) {
        try {
            List<ContractResponse> contracts = contractService.getContractsByTenantWithDetails(tenantId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Lấy danh sách hợp đồng thành công", contracts));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse<>(false, e.getMessage(), null));
        }
    }
    
    @PostMapping("/{id}/terminate")
    public Contract terminateContract(@PathVariable String id) {
        return contractService.terminateContract(id);
    }
}
