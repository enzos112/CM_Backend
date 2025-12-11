package com.medico.backend.config.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.medico.backend.model.core.Persona;
import com.medico.backend.model.core.Usuario;
import com.medico.backend.repository.PersonaRepository;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Autowired
    private PersonaRepository personaRepository;

    // --- GENERAR TOKEN CON DATOS DE PERSONA ---
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> extraClaims = new HashMap<>();

        // 1. Rol
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("USER");
        extraClaims.put("role", role);

        // 2. Datos del Usuario (Cuenta)
        if (userDetails instanceof Usuario) {
            Usuario usuario = (Usuario) userDetails;
            extraClaims.put("id", usuario.getIdUsuario());
            extraClaims.put("email", usuario.getEmail());
            if (usuario.getFechaRegistro() != null) {
                extraClaims.put("fechaRegistro", usuario.getFechaRegistro().toString());
            }

            // 3. BUSCAR DATOS PERSONALES (Aquí está la clave)
            // Buscamos la entidad Persona vinculada a este Usuario
            Optional<Persona> personaOpt = personaRepository.findByUsuario(usuario);

            if (personaOpt.isPresent()) {
                Persona persona = personaOpt.get();

                // Inyectamos los datos personales reales
                extraClaims.put("nombres", persona.getNombres());
                extraClaims.put("apellidoPaterno", persona.getApellidoPaterno());
                extraClaims.put("apellidoMaterno", persona.getApellidoMaterno());
                extraClaims.put("numeroDocumento", persona.getNumeroDocumento());

                // Contacto
                extraClaims.put("telefonoMovil", persona.getTelefonoMovil());
                extraClaims.put("direccionCalle", persona.getDireccionCalle());

                // Ubigeo (Opcional, si el front lo usa)
                extraClaims.put("region", persona.getRegion());
                extraClaims.put("provincia", persona.getProvincia());
                extraClaims.put("distrito", persona.getDistrito());

                // Emergencia
                extraClaims.put("contactoEmergenciaNombre", persona.getContactoEmergenciaNombre());
                extraClaims.put("contactoEmergenciaTelefono", persona.getContactoEmergenciaTelefono());
            }
        }

        return generateToken(extraClaims, userDetails);
    }

    public String generateToken(Map<String, Object> extraClaims, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24)) // 24 horas
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}