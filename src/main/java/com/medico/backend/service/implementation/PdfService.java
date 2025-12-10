package com.medico.backend.service.implementation;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.medico.backend.model.administrative.OrdenPago;
import com.medico.backend.model.administrative.Cita;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class PdfService {

    public void generarRecetaPdf(HttpServletResponse response, Cita cita) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Título
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitulo.setSize(18);
        Paragraph titulo = new Paragraph("RECETA MÉDICA", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph("\n")); // Espacio vacío

        // Datos
        document.add(new Paragraph("Paciente: " + cita.getPaciente().getNombres() + " " + cita.getPaciente().getApellidoPaterno()));
        document.add(new Paragraph("Médico: " + cita.getMedico().getPersona().getNombres()));
        document.add(new Paragraph("Fecha: " + cita.getFechaHoraInicio().toString()));

        document.add(new Paragraph("Fecha: " + cita.getFechaHoraInicio().toString()));

        // --- AGREGAMOS ESTO (de la BD) ---
        String motivo = cita.getMotivoConsultaPaciente();
        document.add(new Paragraph("Motivo de Consulta: " + (motivo != null ? motivo : "Sin especificar")));


        document.add(new Paragraph("\nINDICACIONES:"));
        document.add(new Paragraph("------------------------------------------------"));
        // Aquí podrías listar medicamentos si tuvieras una tabla de detalle receta
        document.add(new Paragraph("Reposar 3 días y tomar mucha agua."));
        document.add(new Paragraph("Paracetamol 500mg cada 8 horas."));

        document.close();
    }

    public void generarBoletaPdf(HttpServletResponse response, OrdenPago pago) throws IOException {
        Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, response.getOutputStream());

        document.open();

        // Título
        Font fontTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD);
        fontTitulo.setSize(18);
        Paragraph titulo = new Paragraph("COMPROBANTE DE PAGO", fontTitulo);
        titulo.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(titulo);

        document.add(new Paragraph("\n"));

        // Datos del Pago
        document.add(new Paragraph("Código Orden: " + pago.getCodigo()));
        document.add(new Paragraph("Fecha: " + pago.getFechaEmision().toString()));
        document.add(new Paragraph("Pagado por: " + pago.getUsuarioPagador().getEmail()));

        document.add(new Paragraph("\nDETALLE:"));
        document.add(new Paragraph("------------------------------------------------"));
        document.add(new Paragraph("Monto Total: S/ " + pago.getMontoTotal()));
        document.add(new Paragraph("Estado: " + pago.getEstado()));

        document.close();
    }
}