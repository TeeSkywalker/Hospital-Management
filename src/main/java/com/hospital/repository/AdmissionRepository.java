package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface AdmissionRepository extends JpaRepository<Admission, Long> {
    List<Admission> findAllByOrderByAdmittedAtDesc();
    List<Admission> findByPatientIdOrderByAdmittedAtDesc(Long patientId);
}
