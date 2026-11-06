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
@RequestMapping("/reports")
public class ReportController {
    private final AppointmentRepository appointments;
    private final BillRepository bills;
    private final PatientRepository patients;
    private final DoctorRepository doctors;
    private final MedicineRepository medicines;
    private final ReminderService reminders;
    private final PdfService pdf;

    @GetMapping
    public String dashboard(Model m) {
        LocalDate today = LocalDate.now();
        List<Bill> all = bills.findAllByOrderByCreatedAtDesc();
        List<Appointment> week = appointments.findByAppointmentDateBetween(today.minusDays(6).atStartOfDay(), today.plusDays(1).atStartOfDay());
        List<String> days = new ArrayList<>();
        List<BigDecimal> revenue = new ArrayList<>();
        List<Long> perDay = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate d = today.minusDays(i);
            days.add(d.toString());
            revenue.add(all.stream()
                .filter(b -> "PAID".equals(b.getStatus()) && b.getPaidAt() != null && b.getPaidAt().toLocalDate().equals(d))
                .map(Bill::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
            perDay.add(week.stream().filter(a -> a.getAppointmentDate().toLocalDate().equals(d)).count());
        }
        List<String> statusLabels = new ArrayList<>();
        List<Long> statusValues = new ArrayList<>();
        for (Object[] r : appointments.countByStatus()) {
            statusLabels.add(String.valueOf(r[0]));
            statusValues.add(((Number) r[1]).longValue());
        }
        List<String> deptLabels = new ArrayList<>();
        List<Long> deptValues = new ArrayList<>();
        for (Object[] r : appointments.countByDepartment()) {
            deptLabels.add(r[0] == null ? "Unassigned" : String.valueOf(r[0]));
            deptValues.add(((Number) r[1]).longValue());
        }
        m.addAttribute("days", days);
        m.addAttribute("revenue", revenue);
        m.addAttribute("perDay", perDay);
        m.addAttribute("statusLabels", statusLabels);
        m.addAttribute("statusValues", statusValues);
        m.addAttribute("deptLabels", deptLabels);
        m.addAttribute("deptValues", deptValues);
        m.addAttribute("paidTotal", paid(all));
        m.addAttribute("unpaidTotal", all.stream().filter(b -> "UNPAID".equals(b.getStatus())).map(Bill::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        m.addAttribute("lowStock", medicines.findLowStock());
        return "reports/dashboard";
    }

    private BigDecimal paid(List<Bill> all) {
        return all.stream().filter(b -> "PAID".equals(b.getStatus())).map(Bill::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private String csv(String s) {
        return s == null ? "" : "\"" + s.replace("\"", "\"\"") + "\"";
    }

    @GetMapping("/export/bills.csv")
    public void billsCsv(HttpServletResponse r) throws IOException {
        r.setContentType("text/csv");
        r.setHeader("Content-Disposition", "attachment; filename=bills.csv");
        PrintWriter w = r.getWriter();
        w.println("Bill ID,Patient,Created,Status,Payment method,Total");
        for (Bill b : bills.findAllByOrderByCreatedAtDesc()) {
            w.println(b.getId() + "," + csv(b.getPatient().getFullName()) + "," + b.getCreatedAt() + ","
                + b.getStatus() + "," + csv(b.getPaymentMethod()) + "," + b.getTotal());
        }
    }

    @GetMapping("/export/appointments.csv")
    public void appointmentsCsv(HttpServletResponse r) throws IOException {
        r.setContentType("text/csv");
        r.setHeader("Content-Disposition", "attachment; filename=appointments.csv");
        PrintWriter w = r.getWriter();
        w.println("ID,Patient,Doctor,Date and time,Reason,Status");
        for (Appointment a : appointments.findAll(Sort.by("appointmentDate"))) {
            w.println(a.getId() + "," + csv(a.getPatient().getFullName()) + "," + csv(a.getDoctor().getFullName()) + ","
                + a.getAppointmentDate() + "," + csv(a.getReason()) + "," + a.getStatus());
        }
    }

    @GetMapping("/summary.pdf")
    public ResponseEntity<byte[]> summary() throws Exception {
        List<Bill> all = bills.findAll();
        Map<String, String> lines = new LinkedHashMap<>();
        lines.put("Date", LocalDate.now().toString());
        lines.put("Patients", String.valueOf(patients.count()));
        lines.put("Doctors", String.valueOf(doctors.count()));
        lines.put("Appointments (all)", String.valueOf(appointments.count()));
        lines.put("Revenue (paid bills)", paid(all).toPlainString());
        lines.put("Outstanding (unpaid bills)", all.stream().filter(b -> "UNPAID".equals(b.getStatus())).map(Bill::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add).toPlainString());
        lines.put("Medicines at or below reorder level", String.valueOf(medicines.findLowStock().size()));
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PDF_VALUE)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=summary.pdf")
            .body(pdf.summary(lines));
    }

    @PostMapping("/reminders")
    public String sendReminders(RedirectAttributes ra) {
        int n = reminders.sendTomorrowReminders();
        ra.addFlashAttribute("msg", n + " reminder email(s) sent for tomorrow's appointments"
            + (n == 0 ? " (nothing to send, or email is not configured in application.properties)" : ""));
        return "redirect:/reports";
    }
}
