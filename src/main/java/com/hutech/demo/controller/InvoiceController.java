package com.hutech.demo.controller;

import com.hutech.demo.model.Invoice;
import com.hutech.demo.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/invoices")
@CrossOrigin("*")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @PostMapping("/generate")
    public Invoice generateInvoice(
            @RequestParam String contractId,
            @RequestParam Integer month,
            @RequestParam Integer year
    ) {
        return invoiceService.generateInvoice(contractId, month, year);
    }

    @PostMapping("/{id}/pay")
    public Invoice payInvoice(@PathVariable String id) {
        return invoiceService.payInvoice(id);
    }
}
