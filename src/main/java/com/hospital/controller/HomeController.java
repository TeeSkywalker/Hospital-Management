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
public class HomeController {
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;
    private final MedicineRepository medicines;
    private final UserContext ctx;

    @GetMapping("/")
    public String home(Authentication auth, Model m) {
        if (ctx.has(auth, "PATIENT")) return "redirect:/portal";
        LocalDate today = LocalDate.now();
        m.addAttribute("patientCount", patients.count());
        m.addAttribute("doctorCount", doctors.count());
        m.addAttribute("todayCount", appointments.findByAppointmentDateBetween(today.atStartOfDay(), today.plusDays(1).atStartOfDay()).size());
        m.addAttribute("lowStock", medicines.findLowStock().size());
        return "index";
    }
}
