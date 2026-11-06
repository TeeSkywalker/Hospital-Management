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
@RequestMapping("/lab")
public class LabController {
    private final LabTestRepository repo;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
    private final BillingService billing;
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
    public String list(Model m) {
        m.addAttribute("tests", repo.findAllByOrderByRequestedAtDesc());
        return "lab/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long patientId, Authentication auth, Model m) {
        LabTest t = new LabTest();
        if (patientId != null) t.setPatient(patientRepo.findById(patientId).orElse(null));
        if (ctx.has(auth, "DOCTOR")) t.setDoctor(ctx.get(auth).getDoctor());
        if (t.getPatient() == null) t.setPatient(new Patient());
        if (t.getDoctor() == null) t.setDoctor(new Doctor());
        m.addAttribute("labTest", t);
        return "lab/form";
    }

    @PostMapping("/request")
    public String request(@Valid @ModelAttribute("labTest") LabTest t, BindingResult br) {
        if (br.hasErrors()) return "lab/form";
        t.setStatus("REQUESTED");
        repo.save(t);
        return "redirect:/lab";
    }

    @GetMapping("/result/{id}")
    public String resultForm(@PathVariable Long id, Model m) {
        m.addAttribute("test", repo.findById(id).orElseThrow());
        return "lab/result";
    }

    @PostMapping("/result/{id}")
    public String saveResult(@PathVariable Long id, @RequestParam String result, RedirectAttributes ra) {
        LabTest t = repo.findById(id).orElseThrow();
        boolean firstTime = !"COMPLETED".equals(t.getStatus());
        t.setResult(result);
        t.setStatus("COMPLETED");
        t.setCompletedAt(LocalDateTime.now());
        repo.save(t);
        if (firstTime && t.getPrice() != null && t.getPrice().signum() > 0) {
            billing.addCharge(t.getPatient(), "Lab test: " + t.getTestName(), t.getPrice());
        }
        ra.addFlashAttribute("msg", "Result saved");
        return "redirect:/lab";
    }
}
