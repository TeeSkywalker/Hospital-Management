package com.hospital.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Data
@Entity
@Table(name = "bills")
public class Bill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "patient_id")
    private Patient patient;
    private LocalDateTime createdAt = LocalDateTime.now();
    private String status = "UNPAID";
    private String paymentMethod;
    private LocalDateTime paidAt;
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "bill_id")
    private List<BillItem> items = new ArrayList<>();

    public BigDecimal getTotal() {
        return items.stream().map(BillItem::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
