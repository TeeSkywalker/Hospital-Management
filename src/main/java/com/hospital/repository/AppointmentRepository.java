package com.hospital.repository;

import com.hospital.model.*;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.time.*;
import java.util.*;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    @Query("select a from Appointment a where (:doctorId = 0L or a.doctor.id = :doctorId) and a.appointmentDate >= :fromDt and a.appointmentDate < :toDt and a.status like :status order by a.appointmentDate")
    Page<Appointment> search(@Param("doctorId") long doctorId, @Param("fromDt") LocalDateTime fromDt, @Param("toDt") LocalDateTime toDt, @Param("status") String status, Pageable pageable);
    List<Appointment> findByDoctorIdAndAppointmentDateBetweenAndStatusNot(Long doctorId, LocalDateTime from, LocalDateTime to, String status);
    List<Appointment> findByAppointmentDateBetween(LocalDateTime from, LocalDateTime to);
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    @Query("select d.name, count(a) from Appointment a join a.doctor dr left join dr.department d group by d.name")
    List<Object[]> countByDepartment();
    @Query("select a.status, count(a) from Appointment a group by a.status")
    List<Object[]> countByStatus();
}
