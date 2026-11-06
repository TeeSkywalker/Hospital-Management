package com.hospital.service;

import com.hospital.model.*;
import com.hospital.repository.BillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class BillingService {
    private final BillRepository bills;

    /** Adds a charge to the patient's open (unpaid) bill, creating one if needed. */
    @Transactional
    public void addCharge(Patient patient, String description, BigDecimal amount) {
        Bill bill = bills.findFirstByPatientIdAndStatusOrderByCreatedAtDesc(patient.getId(), "UNPAID")
            .orElseGet(() -> {
                Bill b = new Bill();
                b.setPatient(patient);
                return b;
            });
        BillItem item = new BillItem();
        item.setDescription(description);
        item.setAmount(amount);
        bill.getItems().add(item);
        bills.save(bill);
    }
}
