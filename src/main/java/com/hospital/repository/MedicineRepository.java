package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface MedicineRepository extends JpaRepository<Medicine, Long> {
    List<Medicine> findAllByOrderByName();
    @Query("select m from Medicine m where m.stock <= m.reorderLevel")
    List<Medicine> findLowStock();
}
