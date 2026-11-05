package com.hospital.controller;

import com.hospital.model.Doctor;
import com.hospital.repository.DoctorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/doctors")
@RequiredArgsConstructor
public class DoctorController {
    private final DoctorRepository repo ;

    @GetMapping
    public String list(Model m) {
        m.addAttribute("doctors", repo.findAll());
        return "doctors/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("doctor", new Doctor());
        return "doctors/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        m.addAttribute("doctor", repo.findById(id).orElseThrow());
        return "doctors/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Doctor doctor) {
        repo.save(doctor);
        return "redirect:/doctors";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/doctors";
    }
}
