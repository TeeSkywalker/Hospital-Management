package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    List<MedicalRecord> findByPatientIdOrderByVisitDateDesc(Long patientId);
    List<MedicalRecord> findTop50ByOrderByVisitDateDesc();
}
