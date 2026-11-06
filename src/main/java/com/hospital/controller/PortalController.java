package com.hospital.controller;

import com.hospital.model.*;
import com.hospital.repository.*;
import com.hospital.service.*;
import jakarta.servlet.http.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/portal")
public class PortalController {
    private final AppointmentRepository appointments;
    private final BillRepository bills;
    private final PrescriptionRepository prescriptions;
    private final MedicalRecordRepository records;
    private final LabTestRepository labs;
    private final DoctorRepository doctors;
    private final AppointmentService service;
    private final PdfService pdf;
    private final UserContext ctx;

    @GetMapping
    public String home(Authentication auth, Model m) {
        Patient p = ctx.get(auth).getPatient();
        m.addAttribute("user", ctx.get(auth));
        if (p != null) {
            m.addAttribute("appointments", appointments.findByPatientIdOrderByAppointmentDateDesc(p.getId()));
            m.addAttribute("bills", bills.findByPatientIdOrderByCreatedAtDesc(p.getId()));
            m.addAttribute("prescriptions", prescriptions.findByRecordPatientId(p.getId()));
            m.addAttribute("records", records.findByPatientIdOrderByVisitDateDesc(p.getId()));
            m.addAttribute("labs", labs.findByPatientIdOrderByRequestedAtDesc(p.getId()));
        }
        return "portal/index";
    }

    @GetMapping("/book")
    public String bookForm(Model m) {
        m.addAttribute("doctors", doctors.findAll());
        return "portal/book";
    }

    @PostMapping("/book")
    public String book(@RequestParam Long doctorId,
                       @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime when,
                       @RequestParam(defaultValue = "") String reason, Authentication auth, RedirectAttributes ra) {
        Patient p = ctx.get(auth).getPatient();
        if (p == null) {
            ra.addFlashAttribute("error", "Your account is not linked to a patient record. Please contact reception.");
        } else if (when.isBefore(LocalDateTime.now())) {
            ra.addFlashAttribute("error", "Please choose a time in the future");
            return "redirect:/portal/book";
        } else if (service.hasClash(doctorId, when, null)) {
            ra.addFlashAttribute("error", "That doctor is already booked around that time. Please choose another time.");
            return "redirect:/portal/book";
        } else {
            Appointment a = new Appointment();
            a.setPatient(p);
            a.setDoctor(doctors.findById(doctorId).orElseThrow());
            a.setAppointmentDate(when);
            a.setReason(reason);
            appointments.save(a);
            ra.addFlashAttribute("msg", "Appointment booked");
        }
        return "redirect:/portal";
    }

    @PostMapping("/appointments/cancel/{id}")
    public String cancel(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        Patient p = ctx.get(auth).getPatient();
        Appointment a = appointments.findById(id).orElseThrow();
        if (p != null && a.getPatient().getId().equals(p.getId())) {
            a.setStatus("CANCELLED");
            appointments.save(a);
            ra.addFlashAttribute("msg", "Appointment cancelled");
        }
        return "redirect:/portal";
    }

    @GetMapping("/bills/{id}/pdf")
    public ResponseEntity<byte[]> billPdf(@PathVariable Long id, Authentication auth) throws Exception {
        Patient p = ctx.get(auth).getPatient();
        Bill b = bills.findById(id).orElseThrow();
        if (p == null || !b.getPatient().getId().equals(p.getId())) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=bill-" + id + ".pdf")
            .body(pdf.receipt(b));
    }
}
