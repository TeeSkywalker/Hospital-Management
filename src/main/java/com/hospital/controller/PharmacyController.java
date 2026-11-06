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
@RequestMapping("/pharmacy")
public class PharmacyController {
    private final MedicineRepository repo;
    private final PrescriptionRepository prescriptions;
    private final BillingService billing;

    @GetMapping
    public String list(Model m) {
        m.addAttribute("medicines", repo.findAllByOrderByName());
        return "pharmacy/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("medicine", new Medicine());
        return "pharmacy/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("medicine", repo.findById(id).orElseThrow());
        return "pharmacy/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("medicine") Medicine med, BindingResult br) {
        if (br.hasErrors()) return "pharmacy/form";
        repo.save(med);
        return "redirect:/pharmacy";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/pharmacy";
    }

    @GetMapping("/prescriptions")
    public String pending(Model m) {
        m.addAttribute("prescriptions", prescriptions.findByDispensedFalse());
        return "pharmacy/prescriptions";
    }

    @PostMapping("/dispense/{id}")
    public String dispense(@PathVariable Long id, RedirectAttributes ra) {
        Prescription p = prescriptions.findById(id).orElseThrow();
        Medicine med = p.getMedicine();
        if (p.isDispensed()) {
            ra.addFlashAttribute("error", "Already dispensed");
        } else if (med.getExpiryDate() != null && med.getExpiryDate().isBefore(LocalDate.now())) {
            ra.addFlashAttribute("error", med.getName() + " has expired and cannot be dispensed");
        } else if (med.getStock() < p.getQuantity()) {
            ra.addFlashAttribute("error", "Not enough stock of " + med.getName() + " (in stock: " + med.getStock() + ")");
        } else {
            med.setStock(med.getStock() - p.getQuantity());
            repo.save(med);
            p.setDispensed(true);
            prescriptions.save(p);
            billing.addCharge(p.getRecord().getPatient(), "Medicine: " + med.getName() + " x" + p.getQuantity(),
                med.getUnitPrice().multiply(BigDecimal.valueOf(p.getQuantity())));
            ra.addFlashAttribute("msg", "Dispensed " + med.getName() + " x" + p.getQuantity() + " and added to the patient's bill");
        }
        return "redirect:/pharmacy/prescriptions";
    }
}
