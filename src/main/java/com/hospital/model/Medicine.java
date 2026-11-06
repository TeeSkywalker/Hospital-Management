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
@Table(name = "medicines")
public class Medicine {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotBlank(message = "Name is required")
    private String name;
    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;
    @NotNull(message = "Enter a price") @DecimalMin(value = "0", message = "Price cannot be negative")
    private BigDecimal unitPrice;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate expiryDate;
    @Min(value = 0, message = "Cannot be negative")
    private int reorderLevel = 10;
}
