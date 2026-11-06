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
@Table(name = "lab_tests")
public class LabTest {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull @ManyToOne(optional = false) @JoinColumn(name = "patient_id")
    private Patient patient;
    @NotNull @ManyToOne(optional = false) @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @NotBlank(message = "Test name is required")
    private String testName;
    @DecimalMin(value = "0", message = "Price cannot be negative")
    private BigDecimal price = BigDecimal.ZERO;
    private String status = "REQUESTED";
    @Column(columnDefinition = "TEXT")
    private String result;
    @Column(updatable = false)
    private LocalDateTime requestedAt = LocalDateTime.now();
    private LocalDateTime completedAt;
}
