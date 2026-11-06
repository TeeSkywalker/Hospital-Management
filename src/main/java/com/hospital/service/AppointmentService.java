package com.hospital.service;

import com.hospital.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AppointmentService {
    private final AppointmentRepository repo;

    /** Appointments are treated as 30-minute slots: another booking within 29 minutes counts as a clash. */
    public boolean hasClash(Long doctorId, LocalDateTime when, Long excludeId) {
        return repo.findByDoctorIdAndAppointmentDateBetweenAndStatusNot(
                doctorId, when.minusMinutes(29), when.plusMinutes(29), "CANCELLED")
            .stream().anyMatch(a -> !a.getId().equals(excludeId));
    }
}
