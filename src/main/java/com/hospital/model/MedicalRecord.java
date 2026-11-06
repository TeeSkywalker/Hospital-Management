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
@Table(name = "medical_records")
public class MedicalRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(optional = false) @JoinColumn(name = "patient_id")
    private Patient patient;
    @NotNull @ManyToOne(optional = false) @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @Column(updatable = false)
    private LocalDateTime visitDate = LocalDateTime.now();
    @NotBlank(message = "Diagnosis is required")
    private String diagnosis;
    @Column(columnDefinition = "TEXT")
    private String notes;
}
