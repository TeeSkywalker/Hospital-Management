package com.hospital.service;

import com.hospital.model.AppUser;
import com.hospital.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserContext {
    private final AppUserRepository users;

    public AppUser get(Authentication auth) {
        return users.findByUsername(auth.getName()).orElseThrow();
    }

    public boolean has(Authentication auth, String role) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_" + role));
    }
}
