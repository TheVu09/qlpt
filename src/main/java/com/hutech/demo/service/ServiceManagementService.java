package com.hutech.demo.service;

import com.hutech.demo.model.ServiceEntity;
import com.hutech.demo.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ServiceManagementService {

    @Autowired
    private ServiceRepository serviceRepository;

    public ServiceEntity createService(ServiceEntity serviceEntity) {
        // Check duplicate name
        Optional<ServiceEntity> existing = serviceRepository.findByName(serviceEntity.getName());
        if (existing.isPresent()) {
            throw new RuntimeException("Service with this name already exists");
        }
        return serviceRepository.save(serviceEntity);
    }

    public List<ServiceEntity> getAllServices() {
        return serviceRepository.findAll();
    }
    
    public ServiceEntity updateService(String id, ServiceEntity updatedInfo) {
        ServiceEntity service = serviceRepository.findById(id)
             .orElseThrow(() -> new RuntimeException("Service not found"));
             
        service.setName(updatedInfo.getName());
        service.setPrice(updatedInfo.getPrice());
        service.setUnit(updatedInfo.getUnit());
        service.setDescription(updatedInfo.getDescription());
        
        return serviceRepository.save(service);
    }

    public void deleteService(String id) {
        serviceRepository.deleteById(id);
    }
}
