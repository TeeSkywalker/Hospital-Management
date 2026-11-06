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
@RequestMapping("/appointments")
public class AppointmentController {
    private final AppointmentRepository repo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final AppointmentService service;
    private final UserContext ctx;

    @ModelAttribute("patients")
    public List<Patient> patients() {
        return patientRepo.findAll();
    }

    @ModelAttribute("doctors")
    public List<Doctor> doctors() {
        return doctorRepo.findAll();
    }

    @GetMapping
    public String list(@RequestParam(required = false) Long doctorId,
                       @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
                       @RequestParam(defaultValue = "") String status,
                       @RequestParam(defaultValue = "0") int page,
                       Authentication auth, Model m) {
        if (ctx.has(auth, "DOCTOR")) {
            Doctor own = ctx.get(auth).getDoctor();
            if (own != null) doctorId = own.getId();
        }
        LocalDateTime from = date == null ? LocalDateTime.of(2000, 1, 1, 0, 0) : date.atStartOfDay();
        LocalDateTime to = date == null ? LocalDateTime.of(2100, 1, 1, 0, 0) : date.plusDays(1).atStartOfDay();
        m.addAttribute("page", repo.search(doctorId == null ? 0L : doctorId, from, to,
            status.isBlank() ? "%" : status, PageRequest.of(page, 10)));
        m.addAttribute("doctorId", doctorId);
        m.addAttribute("date", date);
        m.addAttribute("status", status);
        return "appointments/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        Appointment a = new Appointment();
        a.setPatient(new Patient());
        a.setDoctor(new Doctor());
        m.addAttribute("appointment", a);
        return "appointments/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        Appointment a = repo.findById(id).orElseThrow();
        m.addAttribute("appointment", a);
        return "appointments/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("appointment") Appointment a, BindingResult br) {
        if (!br.hasErrors() && a.getAppointmentDate() != null) {
            if (a.getId() == null && a.getAppointmentDate().isBefore(LocalDateTime.now())) {
                br.rejectValue("appointmentDate", "past", "An appointment cannot be booked in the past");
            } else if (a.getDoctor() != null && a.getDoctor().getId() != null
                       && !"CANCELLED".equals(a.getStatus())
                       && service.hasClash(a.getDoctor().getId(), a.getAppointmentDate(), a.getId())) {
                br.rejectValue("appointmentDate", "clash", "This doctor already has an appointment around that time");
            }
        }
        if (br.hasErrors()) return "appointments/form";
        repo.save(a);
        return "redirect:/appointments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/appointments";
    }
}
