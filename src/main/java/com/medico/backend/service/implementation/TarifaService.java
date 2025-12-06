package com.medico.backend.service.implementation;

import com.medico.backend.model.infrastructure.Especialidad;
import com.medico.backend.model.infrastructure.ModalidadCita;
import com.medico.backend.model.infrastructure.Tarifa;
import com.medico.backend.repository.IGenericRepository;
import com.medico.backend.repository.TarifaRepository;
import com.medico.backend.service.IGenericService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TarifaService extends GenericService<Tarifa, Integer> {

    private final TarifaRepository repo;

    @Override
    protected IGenericRepository<Tarifa, Integer> getRepo() {
        return repo;
    }

    // Método de negocio para obtener la tarifa, usado por CitaService
    public Optional<Tarifa> buscarTarifaActiva(Especialidad especialidad, ModalidadCita modalidad) {
        return repo.findActiveByEspecialidadAndModalidad(especialidad, modalidad);
    }
}