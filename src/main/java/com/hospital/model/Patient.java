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
@Table(name = "patients")
public class Patient {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Full name is required")
    private String fullName;
    @Past(message = "Date of birth must be in the past")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dateOfBirth;
    private String gender;
    @Pattern(regexp = "^$|^[+0-9 ()-]{7,20}$", message = "Enter a valid phone number")
    private String phone;
    @Email(message = "Enter a valid email address")
    private String email;
    private String address;
    @Column(columnDefinition = "TEXT")
    private String medicalHistory;
}
