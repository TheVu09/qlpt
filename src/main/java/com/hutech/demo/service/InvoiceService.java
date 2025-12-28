package com.hutech.demo.service;

import com.hutech.demo.model.*;
import com.hutech.demo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class InvoiceService {

    @Autowired
    private InvoiceRepository invoiceRepository;
    
    @Autowired
    private ContractRepository contractRepository;
    
    @Autowired
    private MeterReadingRepository meterReadingRepository;
    
    @Autowired
    private ServiceRepository serviceRepository;

    public Invoice generateInvoice(String contractId, Integer month, Integer year) {
        Contract contract = contractRepository.findById(contractId)
            .orElseThrow(() -> new RuntimeException("Contract not found"));
            
        Double roomFee = contract.getMonthlyPrice();
        Double serviceFee = 0.0;
        
        // Metered services
        List<MeterReading> readings = meterReadingRepository.findByRoomId(contract.getRoom().getId()); 
        
        for (MeterReading reading : readings) {
            if (reading.getMonth().equals(month) && reading.getYear().equals(year)) {
                 Double usage = reading.getNewIndex() - reading.getOldIndex();
                 serviceFee += usage * reading.getService().getPrice();
            }
        }
        
        Invoice invoice = new Invoice();
        invoice.setContract(contract);
        invoice.setMonth(month);
        invoice.setYear(year);
        invoice.setRoomFee(roomFee);
        invoice.setServiceFee(serviceFee);
        invoice.setTotalAmount(roomFee + serviceFee);
        invoice.setStatus("UNPAID");
        invoice.setCreatedAt(LocalDateTime.now());
        
        return invoiceRepository.save(invoice);
    }
    
    public Invoice payInvoice(String invoiceId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
            .orElseThrow(() -> new RuntimeException("Invoice not found"));
        
        invoice.setStatus("PAID");
        invoice.setPaymentDate(LocalDateTime.now());
        return invoiceRepository.save(invoice);
    }
}
