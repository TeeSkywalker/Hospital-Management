package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface PatientRepository extends JpaRepository<Patient, Long> {
    Page<Patient> findByFullNameContainingIgnoreCase(String name, Pageable pageable);
}
