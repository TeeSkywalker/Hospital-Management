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
@RequestMapping("/users")
public class UserController {
    private final AppUserRepository repo;
    private final DoctorRepository doctors;
    private final PatientRepository patients;
    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    @GetMapping
    public String list(Model m) {
        m.addAttribute("users", repo.findAll());
        return "users/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        m.addAttribute("roles", Role.values());
        m.addAttribute("doctors", doctors.findAll());
        m.addAttribute("patients", patients.findAll());
        return "users/form";
    }

    @PostMapping("/save")
    public String save(@RequestParam String username, @RequestParam String fullName, @RequestParam String password,
                       @RequestParam Role role, @RequestParam(required = false) Long doctorId,
                       @RequestParam(required = false) Long patientId, RedirectAttributes ra) {
        if (username.isBlank() || repo.existsByUsername(username.trim())) {
            ra.addFlashAttribute("error", "Username is empty or already taken");
            return "redirect:/users/new";
        }
        if (password.length() < 6) {
            ra.addFlashAttribute("error", "Password must be at least 6 characters");
            return "redirect:/users/new";
        }
        AppUser u = new AppUser();
        u.setUsername(username.trim());
        u.setFullName(fullName);
        u.setPassword(encoder.encode(password));
        u.setRole(role);
        if (role == Role.DOCTOR && doctorId != null) u.setDoctor(doctors.findById(doctorId).orElse(null));
        if (role == Role.PATIENT && patientId != null) u.setPatient(patients.findById(patientId).orElse(null));
        repo.save(u);
        ra.addFlashAttribute("msg", "User created");
        return "redirect:/users";
    }

    @PostMapping("/password/{id}")
    public String resetPassword(@PathVariable Long id, @RequestParam String newPassword, RedirectAttributes ra) {
        if (newPassword.length() < 6) {
            ra.addFlashAttribute("error", "Password must be at least 6 characters");
        } else {
            AppUser u = repo.findById(id).orElseThrow();
            u.setPassword(encoder.encode(newPassword));
            repo.save(u);
            ra.addFlashAttribute("msg", "Password updated for " + u.getUsername());
        }
        return "redirect:/users";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id, Authentication auth, RedirectAttributes ra) {
        AppUser u = repo.findById(id).orElseThrow();
        if (u.getUsername().equals(auth.getName())) ra.addFlashAttribute("error", "You cannot delete your own account");
        else repo.delete(u);
        return "redirect:/users";
    }
}
