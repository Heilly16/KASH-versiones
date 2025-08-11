package com.proyecto.KASH.servicio;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CorreoServicio {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }
    
    public void sendMassEmail(List<String> recipients, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipients.toArray(new String[0]));
        message.setSubject(subject);
        message.setText(body);
        
        mailSender.send(message);
    }
    
    /**
     * Envía un correo electrónico a un usuario que ha sido inactivado
     * 
     * @param to Dirección de correo del destinatario
     * @param nombre Nombre del usuario
     * @param motivo Motivo de la inactivación
     */
    public void sendUserInactivationEmail(String to, String nombre, String motivo) {
        String subject = "KASH - Tu cuenta ha sido inactivada";
        
        StringBuilder body = new StringBuilder();
        body.append("Estimado/a ").append(nombre).append(",\n\n");
        body.append("Te informamos que tu cuenta en la plataforma KASH ha sido inactivada.\n\n");
        body.append("Motivo: ").append(motivo).append("\n\n");
        body.append("Mientras tu cuenta permanezca inactiva, no podrás acceder a la plataforma ni a sus servicios. ");
        body.append("Si consideras que esto es un error o necesitas más información, por favor contacta al administrador del sistema.\n\n");
        body.append("Atentamente,\n");
        body.append("El equipo de KASH");
        
        sendEmail(to, subject, body.toString());
    }
    
    /**
     * Envía un correo electrónico a un usuario que ha sido activado
     * 
     * @param to Dirección de correo del destinatario
     * @param nombre Nombre del usuario
     * @param motivo Motivo de la activación (opcional)
     */
    public void sendUserActivationEmail(String to, String nombre, String motivo) {
        String subject = "KASH - Tu cuenta ha sido activada";
        
        StringBuilder body = new StringBuilder();
        body.append("Estimado/a ").append(nombre).append(",\n\n");
        body.append("¡Buenas noticias! Tu cuenta en la plataforma KASH ha sido activada nuevamente.\n\n");
        
        if (motivo != null && !motivo.isEmpty()) {
            body.append("Motivo: ").append(motivo).append("\n\n");
        }
        
        body.append("Ya puedes acceder a la plataforma con tus credenciales habituales y utilizar todos los servicios disponibles.\n\n");
        body.append("Si tienes alguna pregunta o necesitas asistencia, no dudes en contactar al administrador del sistema.\n\n");
        body.append("Atentamente,\n");
        body.append("El equipo de KASH");
        
        sendEmail(to, subject, body.toString());
    }
} 