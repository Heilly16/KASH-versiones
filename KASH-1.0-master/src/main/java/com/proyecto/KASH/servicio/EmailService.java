package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Asesoria;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String messageText) throws jakarta.mail.MessagingException {
        try {
            jakarta.mail.internet.MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("kasheducacion@gmail.com"); // O usa spring.mail.smtp.from si está configurado
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(messageText);

            mailSender.send(message);
            System.out.println("Correo enviado exitosamente a: " + to);
        } catch (MailException e) {
            System.out.println("Error al enviar el correo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void sendMassEmail(List<String> recipients, String subject, String messageText) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("kasheducacion@gmail.com");
            helper.setSubject(subject);
            helper.setText(messageText);

            // Agregar los destinatarios en BCC para que no vean las direcciones de los demás
            for (String recipient : recipients) {
                helper.addBcc(recipient);
            }

            mailSender.send(message);
            System.out.println("Correo masivo enviado exitosamente a " + recipients.size() + " destinatarios.");
        } catch (MessagingException | MailException e) {
            System.out.println("Error al enviar correos masivos: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void notificarAsesoria(Asesoria asesoria) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
