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
@RequestMapping("/billing")
public class BillingController {
    private final BillRepository repo;
    private final PatientRepository patients;
    private final PdfService pdf;

    @GetMapping
    public String list(Model m) {
        m.addAttribute("bills", repo.findAllByOrderByCreatedAtDesc());
        m.addAttribute("patients", patients.findAll());
        return "billing/list";
    }

    @PostMapping("/create")
    public String create(@RequestParam Long patientId) {
        Bill b = new Bill();
        b.setPatient(patients.findById(patientId).orElseThrow());
        repo.save(b);
        return "redirect:/billing/" + b.getId();
    }

    @GetMapping("/{id}")
    public String view(@PathVariable Long id, Model m) {
        m.addAttribute("bill", repo.findById(id).orElseThrow());
        return "billing/view";
    }

    @PostMapping("/{id}/item")
    public String addItem(@PathVariable Long id, @RequestParam String description, @RequestParam BigDecimal amount, RedirectAttributes ra) {
        Bill b = repo.findById(id).orElseThrow();
        if (!"UNPAID".equals(b.getStatus())) {
            ra.addFlashAttribute("error", "A paid bill cannot be changed");
        } else if (description.isBlank() || amount.signum() < 0) {
            ra.addFlashAttribute("error", "Enter a description and a valid amount");
        } else {
            BillItem i = new BillItem();
            i.setDescription(description);
            i.setAmount(amount);
            b.getItems().add(i);
            repo.save(b);
        }
        return "redirect:/billing/" + id;
    }

    @PostMapping("/{id}/item/delete/{index}")
    public String deleteItem(@PathVariable Long id, @PathVariable int index, RedirectAttributes ra) {
        Bill b = repo.findById(id).orElseThrow();
        if (!"UNPAID".equals(b.getStatus())) {
            ra.addFlashAttribute("error", "A paid bill cannot be changed");
        } else if (index >= 0 && index < b.getItems().size()) {
            b.getItems().remove(index);
            repo.save(b);
        }
        return "redirect:/billing/" + id;
    }

    @PostMapping("/{id}/pay")
    public String pay(@PathVariable Long id, @RequestParam String method, RedirectAttributes ra) {
        Bill b = repo.findById(id).orElseThrow();
        if (b.getItems().isEmpty()) {
            ra.addFlashAttribute("error", "Add at least one item before marking the bill as paid");
        } else if ("UNPAID".equals(b.getStatus())) {
            b.setStatus("PAID");
            b.setPaymentMethod(method);
            b.setPaidAt(LocalDateTime.now());
            repo.save(b);
            ra.addFlashAttribute("msg", "Payment recorded");
        }
        return "redirect:/billing/" + id;
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> pdf(@PathVariable Long id) throws Exception {
        Bill b = repo.findById(id).orElseThrow();
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=bill-" + id + ".pdf")
            .body(pdf.receipt(b));
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/billing";
    }
}
