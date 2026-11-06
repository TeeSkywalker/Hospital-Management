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
@Table(name = "prescriptions")
public class Prescription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(optional = false) @JoinColumn(name = "record_id")
    private MedicalRecord record;
    @ManyToOne(optional = false) @JoinColumn(name = "medicine_id")
    private Medicine medicine;
    private String dosage;
    private int quantity;
    private boolean dispensed;
}
