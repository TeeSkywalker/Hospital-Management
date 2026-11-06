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
@RequestMapping("/wards")
public class WardController {
    private final BedRepository beds;
    private final AdmissionRepository admissions;
    private final PatientRepository patients;

    @GetMapping
    public String index(Model m) {
        m.addAttribute("beds", beds.findAllByOrderByWardAscBedNumberAsc());
        m.addAttribute("freeBeds", beds.findByOccupiedFalseOrderByWardAscBedNumberAsc());
        m.addAttribute("admissions", admissions.findAllByOrderByAdmittedAtDesc());
        m.addAttribute("patients", patients.findAll());
        return "wards/index";
    }

    @PostMapping("/beds/save")
    public String saveBed(@RequestParam String ward, @RequestParam String bedNumber, RedirectAttributes ra) {
        if (ward.isBlank() || bedNumber.isBlank()) {
            ra.addFlashAttribute("error", "Ward and bed number are required");
        } else {
            Bed b = new Bed();
            b.setWard(ward.trim());
            b.setBedNumber(bedNumber.trim());
            beds.save(b);
        }
        return "redirect:/wards";
    }

    @PostMapping("/beds/delete/{id}")
    public String deleteBed(@PathVariable Long id, RedirectAttributes ra) {
        Bed b = beds.findById(id).orElseThrow();
        if (b.isOccupied()) ra.addFlashAttribute("error", "Cannot delete an occupied bed");
        else beds.delete(b);
        return "redirect:/wards";
    }

    @PostMapping("/admit")
    public String admit(@RequestParam Long patientId, @RequestParam Long bedId, @RequestParam String reason, RedirectAttributes ra) {
        Bed bed = beds.findById(bedId).orElseThrow();
        if (bed.isOccupied()) {
            ra.addFlashAttribute("error", "That bed is already occupied");
            return "redirect:/wards";
        }
        Admission a = new Admission();
        a.setPatient(patients.findById(patientId).orElseThrow());
        a.setBed(bed);
        a.setReason(reason);
        admissions.save(a);
        bed.setOccupied(true);
        beds.save(bed);
        ra.addFlashAttribute("msg", "Patient admitted");
        return "redirect:/wards";
    }

    @PostMapping("/discharge/{id}")
    public String discharge(@PathVariable Long id, RedirectAttributes ra) {
        Admission a = admissions.findById(id).orElseThrow();
        if (a.getDischargedAt() == null) {
            a.setDischargedAt(LocalDateTime.now());
            admissions.save(a);
            Bed bed = a.getBed();
            bed.setOccupied(false);
            beds.save(bed);
            ra.addFlashAttribute("msg", "Patient discharged");
        }
        return "redirect:/wards";
    }
}
