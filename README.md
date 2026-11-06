# Hospital Management System (v2)

Spring Boot 3 + Thymeleaf + Spring Data JPA + Spring Security + MySQL.

## Run
1. JDK 17+, MySQL running. Put your MySQL password in `src/main/resources/application.properties`.
2. Run `HospitalApplication` (Eclipse: Run As > Java Application) or `mvn spring-boot:run`.
3. Open http://localhost:8080 and log in as `admin` / `admin123` (change it under Admin > Users > Reset).

Tables are created/updated automatically. Create users for each role under Admin > Users.

## Roles
ADMIN, DOCTOR, NURSE, RECEPTIONIST, PHARMACIST, LAB_TECH, PATIENT.
A DOCTOR user should be linked to a Doctor record and a PATIENT user to a Patient record (set when creating the user).
