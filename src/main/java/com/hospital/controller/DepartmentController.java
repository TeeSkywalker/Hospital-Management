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
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentRepository repo;

    @GetMapping
    public String list(Model m) {
        m.addAttribute("departments", repo.findAll());
        m.addAttribute("department", new Department());
        return "departments/list";
    }

    @PostMapping("/save")
    public String save(@Valid @ModelAttribute("department") Department d, BindingResult br, Model m) {
        if (br.hasErrors()) {
            m.addAttribute("departments", repo.findAll());
            return "departments/list";
        }
        if (repo.findAll().stream().anyMatch(x -> x.getName().equalsIgnoreCase(d.getName()))) {
            br.rejectValue("name", "dup", "That department already exists");
            m.addAttribute("departments", repo.findAll());
            return "departments/list";
        }
        repo.save(d);
        return "redirect:/departments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/departments";
    }
}
