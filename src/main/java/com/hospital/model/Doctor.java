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
@Table(name = "doctors")
public class Doctor {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Full name is required")
    private String fullName;
    private String specialization;
    @Pattern(regexp = "^$|^[+0-9 ()-]{7,20}$", message = "Enter a valid phone number")
    private String phone;
    @Email(message = "Enter a valid email address")
    private String email;
    @NotNull(message = "Choose a department")
    @ManyToOne @JoinColumn(name = "department_id")
    private Department department;
}
