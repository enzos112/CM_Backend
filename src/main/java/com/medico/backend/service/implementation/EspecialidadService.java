package com.medico.backend.service.implementation;

import com.medico.backend.model.infrastructure.Especialidad;
import com.medico.backend.repository.EspecialidadRepository;
import com.medico.backend.repository.IGenericRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EspecialidadService extends GenericService<Especialidad, Integer> {

    private final EspecialidadRepository repo;

    @Override
    protected IGenericRepository<Especialidad, Integer> getRepo() {
        return repo;
    }
}