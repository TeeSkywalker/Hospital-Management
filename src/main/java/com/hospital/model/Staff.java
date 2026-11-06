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
@Table(name = "staff")
public class Staff {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Full name is required")
    private String fullName;
    @NotBlank(message = "Job title is required")
    private String jobTitle;
    @NotNull(message = "Choose a department")
    @ManyToOne @JoinColumn(name = "department_id")
    private Department department;
    private String shift;
    @Pattern(regexp = "^$|^[+0-9 ()-]{7,20}$", message = "Enter a valid phone number")
    private String phone;
}
