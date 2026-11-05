package com.hospital.controller;

import com.hospital.model.Patient;
import com.hospital.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientRepository repo;

    @GetMapping
    public String list(@RequestParam(required = false) String q, Model m) {
        m.addAttribute("patients", (q == null || q.isBlank()) ? repo.findAll() : repo.findByFullNameContainingIgnoreCase(q));
        m.addAttribute("q", q);
        return "patients/list";
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
    public String save(@ModelAttribute Patient patient) {
        repo.save(patient);
        return "redirect:/patients";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/patients";
    }
}
