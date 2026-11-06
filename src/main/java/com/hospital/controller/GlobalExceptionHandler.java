package com.hospital.controller;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(DataIntegrityViolationException.class)
    public String conflict(Model m) {
        m.addAttribute("error", "This record is linked to other data (for example appointments, bills or records), so that change is not allowed. Remove or update the linked data first.");
        return "error-msg";
    }
}
