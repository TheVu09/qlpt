package com.hutech.demo.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                String email = jwtUtil.extractUsername(token);
                String role = jwtUtil.extractRole(token);

                logger.debug("JWT Token - Email: " + email + ", Role: " + role);

                if (email != null && role != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                    // Tạo authority từ role (đảm bảo role đúng format)
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role);

                    logger.debug("Setting authentication with authority: " + role);

                    // Tạo authentication token
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            email,
                            null,
                            Collections.singletonList(authority));

                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("JWT token missing email or role, or authentication already set");
                }
            } catch (Exception e) {
                // Token không hợp lệ, bỏ qua
                logger.error("JWT token validation failed: " + e.getMessage(), e);
            }
        }

        filterChain.doFilter(request, response);
    }
}
