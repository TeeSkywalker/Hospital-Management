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
@Table(name = "audit_logs")
public class AuditLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;
    private String action;
    private LocalDateTime timestamp = LocalDateTime.now();
}
