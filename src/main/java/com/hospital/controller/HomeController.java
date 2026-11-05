package com.hospital.controller;

import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final AppointmentRepository appointments;

    @GetMapping("/")
    public String home(Model m) {
        m.addAttribute("patientCount", patients.count());
        m.addAttribute("doctorCount", doctors.count());
        m.addAttribute("appointmentCount", appointments.count());
        return "index";
    }
}
