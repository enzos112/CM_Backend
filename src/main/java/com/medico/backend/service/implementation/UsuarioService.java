package com.medico.backend.service.implementation;

import com.medico.backend.model.core.Usuario;
import com.medico.backend.repository.IGenericRepository;
import com.medico.backend.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// CAMBIO 1: Nombre de clase termina en Impl (es una clase, no una interfaz)
// CAMBIO 2: extends GenericService (la clase abstracta), NO IGenericService
public class UsuarioService extends GenericService<Usuario, Integer> {

    private final UsuarioRepository repo;

    @Override
    protected IGenericRepository<Usuario, Integer> getRepo() {
        return repo;
    }
}