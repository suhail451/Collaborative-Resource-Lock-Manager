package com.suhail.collaborative_resource_lock_manager.Authentication;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class AuthVaultValidationFilter extends OncePerRequestFilter {

    private final RestTemplate restTemplate;
    private final AuthVaultConfig config;

    public AuthVaultValidationFilter(RestTemplate restTemplate, AuthVaultConfig config) {
        this.restTemplate = restTemplate;
        this.config = config;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        // If you have endpoints that don't need AuthVault check, skip them here
        return path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // Sanitize path by removing literal double quotes if sent accidentally via Postman URL
        String path = request.getRequestURI().replace("\"", "");
        String method = request.getMethod();
        String authHeader = request.getHeader("Authorization");

        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        try {
            // 1. Prepare dynamic payload matching AuthVault ValidationRequest DTO fields exactly
            Map<String, String> payload = new HashMap<>();
            payload.put("token", token);
            payload.put("clientId", config.getClientId());
            payload.put("path", path);
            payload.put("method", method); // Maps to 'method' field in ValidationRequest

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-API-KEY", config.getApiKey()); // Sends your Developer API key

            HttpEntity<Map<String, String>> entity = new HttpEntity<>(payload, headers);

            // 2. Ask AuthVault if this specific request is allowed
            ResponseEntity<AuthResponse> authResponse = restTemplate.postForEntity(
                    config.getValidateUrl(),
                    entity,
                    AuthResponse.class
            );

            if (authResponse.getStatusCode() == HttpStatus.OK && authResponse.getBody() != null) {
                AuthResponse body = authResponse.getBody();

                if (body.isValid()) {
                    // AuthVault says YES. Set local security context.
                    String principalUser = body.getUsername() != null ? body.getUsername() : "anonymous";

                    // Carry the role AuthVault reports (e.g. "CLIENT", "ROLE_ADMIN") as a
                    // GrantedAuthority so @PreAuthorize/hasRole(...) checks work downstream.
                    // Spring's hasRole() expects a "ROLE_" prefix, so normalize for it while
                    // still exposing the raw role via hasAuthority(...) if needed.
                    List<GrantedAuthority> authorities;
                    String role = body.getRole();
                    if (role == null || role.isBlank()) {
                        authorities = Collections.emptyList();
                    } else {
                        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                        authorities = Collections.singletonList(new SimpleGrantedAuthority(normalizedRole));
                    }

                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(principalUser, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authentication);

                    // Let the request proceed to your Lock Manager controller
                    filterChain.doFilter(request, response);
                    return;
                }
            }
        } catch (Exception e) {
            // Log connection errors or AuthVault server crashes
            System.err.println("AuthVault communication failed: " + e.getMessage());
        }

        // 3. AuthVault returned invalid, or could not be reached -> Block immediately
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\": \"Unauthorized by AuthVault Gateway\"}");
    }

    private static class AuthResponse {
        private boolean valid;
        private String username;
        private String role;

        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
}