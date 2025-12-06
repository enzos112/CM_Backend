package com.medico.backend.service.implementation;

import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.repository.IGenericRepository;
import com.medico.backend.repository.MedicoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MedicoService extends GenericService<Medico, Integer> {

    private final MedicoRepository repo;

    @Override
    protected IGenericRepository<Medico, Integer> getRepo() {
        return repo;
    }
}