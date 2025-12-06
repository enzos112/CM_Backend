package com.medico.backend.service.implementation;

import com.medico.backend.model.administrative.DetalleOrden;
import com.medico.backend.model.administrative.OrdenPago;
import com.medico.backend.model.administrative.Cita;
import com.medico.backend.repository.DetalleOrdenRepository;
import com.medico.backend.repository.IGenericRepository;
import com.medico.backend.repository.OrdenPagoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class OrdenPagoService extends GenericService<OrdenPago, Integer> {

    private final OrdenPagoRepository ordenPagoRepo;
    private final DetalleOrdenRepository detalleOrdenRepo;

    @Override
    protected IGenericRepository<OrdenPago, Integer> getRepo() {
        return ordenPagoRepo;
    }

    /**
     * Crea y registra una Orden de Pago basada en una Cita Médica.
     */
    @Transactional
    public OrdenPago generarOrdenPagoParaCita(Cita cita, String metodoPago) {
        if (cita.getTarifa() == null) {
            throw new RuntimeException("La cita no tiene una tarifa asignada para generar la orden de pago.");
        }
        BigDecimal precioCita = cita.getTarifa().getPrecio();

        // 1. Crear la Orden de Pago Principal
        OrdenPago orden = OrdenPago.builder()
                .usuarioPagador(cita.getPaciente().getUsuario())
                .montoTotal(precioCita)
                .metodoPago(metodoPago)
                .estado("PENDIENTE_PAGO")
                .build();

        OrdenPago savedOrden = ordenPagoRepo.save(orden);

        // 2. Crear el Detalle de la Orden (Línea de la factura)
        DetalleOrden detalle = DetalleOrden.builder()
                .ordenPago(savedOrden)
                .cita(cita) // Enlazamos a la entidad Cita
                .tipoItem("CITA_MEDICA")
                .descripcion("Cita: " + cita.getMedico().getPersona().getApellidoPaterno() + " / " + cita.getMedico().getEspecialidad().getNombre())
                .precioUnitario(precioCita)
                .cantidad(1)
                .subtotal(precioCita)
                .build();

        detalleOrdenRepo.save(detalle);

        return savedOrden;
    }
}