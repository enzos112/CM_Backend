package com.medico.backend.service.implementation;

import com.medico.backend.model.infrastructure.HorarioMedico;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.model.infrastructure.ModalidadCita;
import com.medico.backend.repository.HorarioMedicoRepository;
import com.medico.backend.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HorarioService {

    private final HorarioMedicoRepository horarioRepository;
    private final MedicoRepository medicoRepository;

    @Transactional
    public HorarioMedico registrarHorario(String emailMedico, Integer dia, LocalTime inicio, LocalTime fin, Integer idModalidad) {

        // 1. Validar horas coherentes
        if (inicio.isAfter(fin)) {
            throw new RuntimeException("La hora de inicio no puede ser mayor a la hora de fin");
        }

        // 2. Obtener al médico logueado
        Medico medico = medicoRepository.findByUsuarioEmail(emailMedico)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado con email: " + emailMedico));

        // 3. Validar que no se cruce con otro turno suyo
        boolean hayCruce = horarioRepository.existeCruce(medico.getIdMedico(), dia, inicio, fin);
        if (hayCruce) {
            throw new RuntimeException("El horario se cruza con otro turno existente");
        }

        // 4. Crear y guardar
        HorarioMedico horario = new HorarioMedico();
        horario.setMedico(medico);
        horario.setDiaSemana(dia);
        horario.setHoraInicio(inicio);
        horario.setHoraFin(fin);
        horario.setActivo(true);

        if (idModalidad != null) {
            ModalidadCita mod = new ModalidadCita();
            mod.setIdModalidad(idModalidad); // Asignamos el ID directamente (Lazy load)
            horario.setModalidad(mod);
        }

        return horarioRepository.save(horario);
    }

    public List<HorarioMedico> listarMisHorarios(String email) {
        Medico medico = medicoRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RuntimeException("Médico no encontrado"));
        return horarioRepository.findByMedicoIdMedico(medico.getIdMedico());
    }

    public void eliminarHorario(Integer idHorario) {
        horarioRepository.deleteById(idHorario);
    }
}