package com.hospital.controller;

import com.hospital.model.Appointment;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/appointments")
@RequiredArgsConstructor
public class AppointmentController {
    private final AppointmentRepository repo;
    private final PatientRepository patients;
    private final DoctorRepository doctors;

    @GetMapping
    public String list(Model m) {
        m.addAttribute("appointments", repo.findAllByOrderByAppointmentDateAsc());
        return "appointments/list";
    }

    @GetMapping("/new")
    public String newForm(Model m) {
        return form(new Appointment(), m);
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model m) {
        return form(repo.findById(id).orElseThrow(), m);
    }

    private String form(Appointment a, Model m) {
        m.addAttribute("appointment", a);
        m.addAttribute("patients", patients.findAll());
        m.addAttribute("doctors", doctors.findAll());
        return "appointments/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Appointment appointment) {
        repo.save(appointment);
        return "redirect:/appointments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        repo.deleteById(id);
        return "redirect:/appointments";
    }
}
