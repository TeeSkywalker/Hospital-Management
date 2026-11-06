package com.hospital.config;

import com.hospital.repository.AppUserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(a -> a
                .requestMatchers("/portal/**").hasRole("PATIENT")
                .requestMatchers("/users/**", "/audit/**", "/staff/**", "/departments/**", "/reports/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.POST, "/doctors/**", "/patients/delete/**").hasRole("ADMIN")
                .requestMatchers("/doctors/new", "/doctors/edit/**").hasRole("ADMIN")
                .requestMatchers("/patients/**", "/doctors/**", "/appointments/**").hasAnyRole("ADMIN", "RECEPTIONIST", "DOCTOR", "NURSE")
                .requestMatchers("/records/**").hasAnyRole("ADMIN", "DOCTOR", "NURSE")
                .requestMatchers("/billing/**").hasAnyRole("ADMIN", "RECEPTIONIST")
                .requestMatchers("/pharmacy/**").hasAnyRole("ADMIN", "PHARMACIST")
                .requestMatchers("/lab/result/**").hasAnyRole("ADMIN", "LAB_TECH")
                .requestMatchers("/lab/new", "/lab/request").hasAnyRole("ADMIN", "DOCTOR")
                .requestMatchers("/lab/**").hasAnyRole("ADMIN", "DOCTOR", "LAB_TECH")
                .requestMatchers("/wards/**").hasAnyRole("ADMIN", "NURSE", "RECEPTIONIST", "DOCTOR")
                .anyRequest().authenticated())
            .formLogin(Customizer.withDefaults())
            .logout(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    UserDetailsService userDetailsService(AppUserRepository users) {
        return username -> users.findByUsername(username)
            .map(u -> User.withUsername(u.getUsername()).password(u.getPassword()).roles(u.getRole().name()).build())
            .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
