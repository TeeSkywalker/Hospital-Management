package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface BedRepository extends JpaRepository<Bed, Long> {
    List<Bed> findAllByOrderByWardAscBedNumberAsc();
    List<Bed> findByOccupiedFalseOrderByWardAscBedNumberAsc();
}
