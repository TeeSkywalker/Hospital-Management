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
@Table(name = "appointments")
public class Appointment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull(message = "Choose a patient")
    @ManyToOne(optional = false) @JoinColumn(name = "patient_id")
    private Patient patient;
    @NotNull(message = "Choose a doctor")
    @ManyToOne(optional = false) @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @NotNull(message = "Choose a date and time")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentDate;
    private String reason;
    private String status = "SCHEDULED";
}
