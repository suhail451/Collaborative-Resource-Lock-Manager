package com.suhail.collaborative_resource_lock_manager.Authentication;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final AuthVaultValidationFilter authVaultValidationFilter;

    public SecurityConfig(AuthVaultValidationFilter authVaultValidationFilter) {
        this.authVaultValidationFilter = authVaultValidationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Allow all requests natively so only your custom filter decides what to block
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                )

                // Your filter still intercepts everything first
                .addFilterBefore(authVaultValidationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}