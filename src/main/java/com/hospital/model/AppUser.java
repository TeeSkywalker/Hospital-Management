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
@Table(name = "users")
public class AppUser {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true, nullable = false)
    private String username;
    @Column(nullable = false)
    private String password;
    private String fullName;
    @Enumerated(EnumType.STRING)
    private Role role;
    @ManyToOne @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    @ManyToOne @JoinColumn(name = "patient_id")
    private Patient patient;
}
