package com.hospital.config;

import com.hospital.model.AuditLog;
import com.hospital.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** Records every successful data-changing request (POST) with the user and the time. */
@Component
@RequiredArgsConstructor
public class AuditInterceptor implements HandlerInterceptor {
    private final AuditLogRepository logs;

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if ("POST".equals(request.getMethod()) && response.getStatus() < 400) {
            Authentication a = SecurityContextHolder.getContext().getAuthentication();
            AuditLog log = new AuditLog();
            log.setUsername(a != null ? a.getName() : "anonymous");
            log.setAction("POST " + request.getRequestURI());
            logs.save(log);
        }
    }
}
