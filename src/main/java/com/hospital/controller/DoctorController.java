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
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorRepository repo;
    private final DepartmentRepository departmentRepo;

    @ModelAttribute("departments")
    public List<Department> departments() {
        return departmentRepo.findAll();
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("doctors", repo.findAll());
        return "doctors/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        Doctor d = new Doctor();
        d.setDepartment(new Department());
        m.addAttribute("doctor", d);
        return "doctors/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        Doctor d = repo.findById(id).orElseThrow();
        if (d.getDepartment() == null) d.setDepartment(new Department());
        m.addAttribute("doctor", d);
        return "doctors/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("doctor") Doctor d, BindingResult br) {
        if (br.hasErrors()) return "doctors/form";
        repo.save(d);
        return "redirect:/doctors";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/doctors";
    }
}
