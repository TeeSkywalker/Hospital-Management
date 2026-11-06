package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface BillRepository extends JpaRepository<Bill, Long> {
    List<Bill> findAllByOrderByCreatedAtDesc();
    List<Bill> findByPatientIdOrderByCreatedAtDesc(Long patientId);
    Optional<Bill> findFirstByPatientIdAndStatusOrderByCreatedAtDesc(Long patientId, String status);
}
