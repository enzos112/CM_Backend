package com.medico.backend.service.implementation;

import com.medico.backend.repository.IGenericRepository;
import com.medico.backend.service.IGenericService; // Importa la interfaz que acabamos de crear
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

// Clase abstracta: No se puede instanciar directamente, solo heredar
public abstract class GenericService<T, ID> implements IGenericService<T, ID> {

    protected abstract IGenericRepository<T, ID> getRepo();

    @Override
    @Transactional
    public T registrar(T t) throws Exception {
        return getRepo().save(t);
    }

    @Override
    @Transactional
    public T modificar(T t) throws Exception {
        return getRepo().save(t);
    }

    @Override
    @Transactional(readOnly = true)
    public List<T> listar() throws Exception {
        return getRepo().findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public T listarPorId(ID id) throws Exception {
        Optional<T> x = getRepo().findById(id);
        return x.orElse(null); // Retorna null si no existe (luego manejamos excepciones mejor)
    }

    @Override
    @Transactional
    public void eliminar(ID id) throws Exception {
        getRepo().deleteById(id);
    }
}