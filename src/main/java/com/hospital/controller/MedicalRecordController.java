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
@RequestMapping("/records")
public class MedicalRecordController {
    private final MedicalRecordRepository repo;
    private final PrescriptionRepository prescriptions;
    private final MedicineRepository medicines;
    private final PatientRepository patientRepo;
    private final DoctorRepository doctorRepo;
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
        m.addAttribute("records", repo.findTop50ByOrderByVisitDateDesc());
        return "records/list";
    }

    @GetMapping("/new")
    public String newForm(@RequestParam(required = false) Long patientId, Authentication auth, Model m) {
        MedicalRecord r = new MedicalRecord();
        if (patientId != null) r.setPatient(patientRepo.findById(patientId).orElse(null));
        if (ctx.has(auth, "DOCTOR")) r.setDoctor(ctx.get(auth).getDoctor());
        if (r.getPatient() == null) r.setPatient(new Patient());
        if (r.getDoctor() == null) r.setDoctor(new Doctor());
        m.addAttribute("record", r);
        return "records/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("record", repo.findById(id).orElseThrow());
        return "records/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("record") MedicalRecord r, BindingResult br) {
        if (br.hasErrors()) return "records/form";
        repo.save(r);
        return "redirect:/records/" + r.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model m) {
        m.addAttribute("record", repo.findById(id).orElseThrow());
        m.addAttribute("prescriptions", prescriptions.findByRecordId(id));
        m.addAttribute("medicines", medicines.findAllByOrderByName());
        return "records/view";
    }

    @PostMapping("/{id}/prescribe")
    public String prescribe(@PathVariable Long id, @RequestParam Long medicineId,
                            @RequestParam String dosage, @RequestParam int quantity, RedirectAttributes ra) {
        if (quantity < 1) {
            ra.addFlashAttribute("error", "Quantity must be at least 1");
            return "redirect:/records/" + id;
        }
        Prescription p = new Prescription();
        p.setRecord(repo.findById(id).orElseThrow());
        p.setMedicine(medicines.findById(medicineId).orElseThrow());
        p.setDosage(dosage);
        p.setQuantity(quantity);
        prescriptions.save(p);
        return "redirect:/records/" + id;
    }

    @PostMapping("/{id}/prescription/delete/{pid}")
    public String deletePrescription(@PathVariable Long id, @PathVariable Long pid, RedirectAttributes ra) {
        Prescription p = prescriptions.findById(pid).orElseThrow();
        if (p.isDispensed()) ra.addFlashAttribute("error", "This prescription was already dispensed");
        else prescriptions.delete(p);
        return "redirect:/records/" + id;
    }
}
