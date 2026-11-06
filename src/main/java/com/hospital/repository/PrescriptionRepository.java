package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface PrescriptionRepository extends JpaRepository<Prescription, Long> {
    List<Prescription> findByRecordId(Long recordId);
    List<Prescription> findByDispensedFalse();
    List<Prescription> findByRecordPatientId(Long patientId);
}
