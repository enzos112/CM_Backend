package com.medico.backend.config.security;

import com.medico.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UsuarioRepository usuarioRepository;

    @Bean
    public UserDetailsService userDetailsService() {
        return identifier -> {
            // Lógica Híbrida: DNI o Email

            // 1. Si es numérico y tiene entre 8 y 12 dígitos, asumimos DNI/Documento
            if (identifier.matches("^\\d{8,12}$")) {
                return usuarioRepository.findByDni(identifier)
                        .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con DNI: " + identifier));
            }

            // 2. Si no, buscamos por Email (Para Admins o login tradicional)
            return usuarioRepository.findByEmail(identifier)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con Email: " + identifier));
        };
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}