package com.hutech.demo.controller;

import com.hutech.demo.model.ServiceEntity;
import com.hutech.demo.service.ServiceManagementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin("*")
public class ServiceController {

    @Autowired
    private ServiceManagementService serviceManagementService;

    @PostMapping
    public ServiceEntity createService(@RequestBody ServiceEntity serviceEntity) {
        return serviceManagementService.createService(serviceEntity);
    }

    @GetMapping
    public List<ServiceEntity> getAllServices() {
        return serviceManagementService.getAllServices();
    }
    
    @PutMapping("/{id}")
    public ServiceEntity updateService(@PathVariable String id, @RequestBody ServiceEntity serviceEntity) {
        return serviceManagementService.updateService(id, serviceEntity);
    }
    
    @DeleteMapping("/{id}")
    public void deleteService(@PathVariable String id) {
        serviceManagementService.deleteService(id);
    }
}
