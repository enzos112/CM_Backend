package com.medico.backend.service.implementation;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void enviarConfirmacionCita(String emailDestino, String paciente, String fecha, String medico) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(emailDestino);
            helper.setSubject("Confirmación de Cita - Centro Médico");

            // Cuerpo del correo en HTML para que se vea bonito
            String htmlContent = "<h1>Hola " + paciente + "</h1>" +
                    "<p>Tu cita ha sido agendada con éxito.</p>" +
                    "<ul>" +
                    "<li><b>Médico:</b> " + medico + "</li>" +
                    "<li><b>Fecha y Hora:</b> " + fecha + "</li>" +
                    "</ul>" +
                    "<p>¡Te esperamos!</p>";

            helper.setText(htmlContent, true);

            javaMailSender.send(message);
            System.out.println("Correo enviado a: " + emailDestino);

        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}