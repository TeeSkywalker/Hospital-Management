package com.hospital.config;

import com.hospital.model.*;
import com.hospital.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.util.List;

/** Seeds a default admin, some departments and a few beds on first run. */
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final AppUserRepository users;
    private final DepartmentRepository departments;
    private final BedRepository beds;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (users.count() == 0) {
            AppUser admin = new AppUser();
            admin.setUsername("admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setFullName("System Administrator");
            admin.setRole(Role.ADMIN);
            users.save(admin);
        }
        if (departments.count() == 0) {
            for (String n : List.of("General Medicine", "Pediatrics", "Surgery", "Obstetrics and Gynaecology", "Radiology", "Laboratory")) {
                Department d = new Department();
                d.setName(n);
                departments.save(d);
            }
        }
        if (beds.count() == 0) {
            for (int i = 1; i <= 6; i++) {
                Bed b = new Bed();
                b.setWard(i <= 3 ? "General Ward" : "Maternity Ward");
                b.setBedNumber("B" + i);
                beds.save(b);
            }
        }
    }
}
