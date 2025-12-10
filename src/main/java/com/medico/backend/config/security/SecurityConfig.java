package com.medico.backend.config.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // 1. ZONA PÚBLICA (Login, Registro, Catálogos)
                        .requestMatchers("/auth/**", "/catalogo/**").permitAll()

                        // 2. ZONA CITAS (Reglas Específicas)
                        .requestMatchers("/citas/agenda-medico").hasAnyAuthority("MEDICO", "ADMIN")
                        .requestMatchers("/citas/horarios-ocupados").hasAnyAuthority("PACIENTE", "MEDICO", "ADMIN")

                        // 3. SOLICITUDES DE CANCELACIÓN (Tu Módulo)
                        .requestMatchers("/solicitudes/crear").hasAnyAuthority("PACIENTE")
                        .requestMatchers("/solicitudes/pendientes", "/solicitudes/*/resolver").hasAnyAuthority("MEDICO", "ADMIN")

                        // 4. ZONA INTRANET (Módulos de tu Compañera) <--- ¡CORREGIDO AQUÍ!
                        // AdminController está en /intranet/admin
                        .requestMatchers("/intranet/admin/**").hasAuthority("ADMIN")
                        // MedicoDashboardController y Horarios están en /intranet/medico
                        .requestMatchers("/intranet/medico/**").hasAnyAuthority("MEDICO", "ADMIN")

                        // 5. ZONA WEB PACIENTE (PerfilPacienteController) <--- ¡FALTABA ESTO!
                        .requestMatchers("/web/**").hasAuthority("PACIENTE")

                        // 6. GENERALES (Para endpoints viejos que no tengan prefijo)
                        .requestMatchers("/citas/**").hasAnyAuthority("PACIENTE", "ADMIN")

                        .requestMatchers("/intranet/medico/atencion/**").hasAnyAuthority("MEDICO")

                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}