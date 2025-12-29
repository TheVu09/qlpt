package com.hutech.demo.config;

import com.hutech.demo.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register", "/api/auth/login", "/api/auth/admin/login").permitAll()
                        .requestMatchers("/api/auth/check").authenticated() // Check auth cần authentication
                        .requestMatchers("/ws/**").permitAll() // Allow WebSocket
                        .requestMatchers("/", "/error").permitAll()
                        // Các route công khai - chỉ GET không cần đăng nhập
                        .requestMatchers(HttpMethod.GET, "/api/rooms/available/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/rooms").permitAll() // GET /api/rooms - xem danh sách phòng
                        .requestMatchers(HttpMethod.GET, "/api/rooms/**").permitAll() // GET /api/rooms/{id} - xem chi tiết phòng
                        .requestMatchers(HttpMethod.GET, "/api/motels").permitAll() // GET /api/motels - xem danh sách khu trọ
                        .requestMatchers(HttpMethod.GET, "/api/motels/**").permitAll() // GET /api/motels/{id} - xem chi tiết khu trọ
                        // Tất cả các route khác cần authentication
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:3000",
                "http://localhost:5173"));
        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
