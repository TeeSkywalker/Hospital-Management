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
@RequestMapping("/patients")
public class PatientController {
    private final PatientRepository repo;
    private final MedicalRecordRepository records;
    private final AppointmentRepository appointments;
    private final BillRepository bills;
    private final LabTestRepository labs;
    private final AdmissionRepository admissions;
    private final UserContext ctx;

    @GetMapping
    public String list(@RequestParam(defaultValue = "") String q, @RequestParam(defaultValue = "0") int page, Model m) {
        m.addAttribute("page", repo.findByFullNameContainingIgnoreCase(q, PageRequest.of(page, 10, Sort.by("fullName"))));
        m.addAttribute("q", q);
        return "patients/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model m) {
        m.addAttribute("patient", repo.findById(id).orElseThrow());
        m.addAttribute("records", records.findByPatientIdOrderByVisitDateDesc(id));
        m.addAttribute("appointments", appointments.findByPatientIdOrderByAppointmentDateDesc(id));
        m.addAttribute("bills", bills.findByPatientIdOrderByCreatedAtDesc(id));
        m.addAttribute("labs", labs.findByPatientIdOrderByRequestedAtDesc(id));
        m.addAttribute("admissions", admissions.findByPatientIdOrderByAdmittedAtDesc(id));
        return "patients/view";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("patient", new Patient());
        return "patients/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("patient", repo.findById(id).orElseThrow());
        return "patients/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("patient") Patient p, BindingResult br, Authentication auth) {
        if (br.hasErrors()) return "patients/form";
        boolean canSeeHistory = ctx.has(auth, "ADMIN") || ctx.has(auth, "DOCTOR") || ctx.has(auth, "NURSE");
        if (p.getId() != null && !canSeeHistory) {
            repo.findById(p.getId()).ifPresent(old -> p.setMedicalHistory(old.getMedicalHistory()));
        }
        repo.save(p);
        return "redirect:/patients";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/patients";
    }
}
