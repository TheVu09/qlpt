package com.hutech.demo.repository;

import com.hutech.demo.model.Invoice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InvoiceRepository extends MongoRepository<Invoice, String> {
    List<Invoice> findByContractId(String contractId);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByMonthAndYear(Integer month, Integer year);
}
