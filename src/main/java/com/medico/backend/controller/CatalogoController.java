package com.medico.backend.controller;

import com.medico.backend.dto.response.MedicoDTO;
import com.medico.backend.model.infrastructure.Especialidad;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.repository.EspecialidadRepository;
import com.medico.backend.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogo")
@RequiredArgsConstructor
public class CatalogoController {

    private final EspecialidadRepository especialidadRepository;
    private final MedicoRepository medicoRepository;

    // Endpoint para llenar el select de Especialidades
    @GetMapping("/especialidades")
    public ResponseEntity<List<Especialidad>> listarEspecialidades() {
        return ResponseEntity.ok(especialidadRepository.findAll());
    }

    @GetMapping("/medicos")
    public ResponseEntity<List<MedicoDTO>> listarMedicos() {
        // Convertimos la Entidad Medico -> MedicoDTO para enviar solo lo necesario
        List<MedicoDTO> lista = medicoRepository.findAll().stream()
                .map(m -> MedicoDTO.builder()
                        .idMedico(m.getIdMedico())
                        // Validamos si persona es null para evitar errores (NullPointerException)
                        .nombreCompleto(m.getPersona() != null ?
                                m.getPersona().getNombres() + " " + m.getPersona().getApellidoPaterno()
                                : "Sin Nombre")
                        .cmp(m.getCmp())
                        .idEspecialidad(m.getEspecialidad().getIdEspecialidad())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(lista);
    }
}