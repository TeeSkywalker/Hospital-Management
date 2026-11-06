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
@RequestMapping("/staff")
public class StaffController {
    private final StaffRepository repo;
    private final DepartmentRepository departmentRepo;

    @ModelAttribute("departments")
    public List<Department> departments() {
        return departmentRepo.findAll();
    }

    @GetMapping
    public String list(Model m) {
        m.addAttribute("staffList", repo.findAll());
        return "staff/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        Staff s = new Staff();
        s.setDepartment(new Department());
        m.addAttribute("staff", s);
        return "staff/form";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        Staff s = repo.findById(id).orElseThrow();
        if (s.getDepartment() == null) s.setDepartment(new Department());
        m.addAttribute("staff", s);
        return "staff/form";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("staff") Staff s, BindingResult br) {
        if (br.hasErrors()) return "staff/form";
        repo.save(s);
        return "redirect:/staff";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/staff";
    }
}
