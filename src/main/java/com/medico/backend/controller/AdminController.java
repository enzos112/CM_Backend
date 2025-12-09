package com.medico.backend.controller;

import com.medico.backend.model.core.Persona;
import com.medico.backend.model.infrastructure.Medico;
import com.medico.backend.repository.MedicoRepository;
import com.medico.backend.repository.OrdenPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/intranet/admin")
@RequiredArgsConstructor
public class AdminController {

    private final MedicoRepository medicoRepository;
    private final OrdenPagoRepository ordenPagoRepository;

    // EDITAR MÉDICO
    @PutMapping("/medicos/{id}")
    public Medico editarMedico(@PathVariable Integer id, @RequestBody Medico data) {

        Medico medico = medicoRepository.findById(id).orElseThrow();

        // Actualizar datos del médico
        medico.setCmp(data.getCmp());
        medico.setRne(data.getRne());
        medico.setBiografia(data.getBiografia());
        medico.setFirmaDigitalUrl(data.getFirmaDigitalUrl());
        medico.setEstadoOperativo(data.getEstadoOperativo());
        medico.setEspecialidad(data.getEspecialidad());

        // editar la persona relacionada:
        if (data.getPersona() != null) {
            Persona p = medico.getPersona();
            Persona newP = data.getPersona();

            p.setNombres(newP.getNombres());
            p.setApellidoPaterno(newP.getApellidoPaterno());
            p.setApellidoMaterno(newP.getApellidoMaterno());
            p.setTelefonoMovil(newP.getTelefonoMovil());
            p.setDireccionCalle(newP.getDireccionCalle());
            p.setDistrito(newP.getDistrito());
            p.setProvincia(newP.getProvincia());
            p.setRegion(newP.getRegion());
        }

        return medicoRepository.save(medico);
    }

    // REPORTE VENTAS
    @GetMapping("/resumen-ventas")
    public Double totalVentasHoy() {
        return ordenPagoRepository.sumByFecha(LocalDate.now());
    }
}