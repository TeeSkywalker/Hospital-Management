package com.hospital.service;

import com.hospital.model.Appointment;
import com.hospital.repository.AppointmentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReminderService {
    private final AppointmentRepository appointments;
    private final ObjectProvider<JavaMailSender> mail;

    @Value("${spring.mail.username:noreply@hospital.local}")
    private String from;

    /** Runs every day at 08:00 and emails patients who have an appointment tomorrow. */
    @Scheduled(cron = "0 0 8 * * *")
    public int sendTomorrowReminders() {
        JavaMailSender sender = mail.getIfAvailable();
        if (sender == null) {
            log.info("Email is not configured (spring.mail.*), so no reminders were sent.");
            return 0;
        }
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        int sent = 0;
        for (Appointment a : appointments.findByAppointmentDateBetween(tomorrow.atStartOfDay(), tomorrow.plusDays(1).atStartOfDay())) {
            String email = a.getPatient().getEmail();
            if (!"SCHEDULED".equals(a.getStatus()) || email == null || email.isBlank()) continue;
            try {
                SimpleMailMessage m = new SimpleMailMessage();
                m.setFrom(from);
                m.setTo(email);
                m.setSubject("Appointment reminder");
                m.setText("Dear " + a.getPatient().getFullName() + ",\n\nThis is a reminder of your appointment with Dr. "
                    + a.getDoctor().getFullName() + " on "
                    + a.getAppointmentDate().format(DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm")) + ".\n\nHospital Management System");
                sender.send(m);
                sent++;
            } catch (Exception e) {
                log.warn("Could not send reminder to {}: {}", email, e.getMessage());
            }
        }
        return sent;
    }
}
