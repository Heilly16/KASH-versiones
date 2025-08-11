package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.RecuperacionRepositorio;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Recuperacion;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class RecuperacionServicioImpl implements RecuperacionServicio {

    @Autowired
    private RecuperacionRepositorio recuperacionRepositorio;

    @Autowired
    private Usuario2Repositorio usuarioRepositorio;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public String generarCodigo() {
        Random random = new Random();
        int codigo = 100000 + random.nextInt(900000);
        return String.valueOf(codigo);
    }

    @Override
    public boolean enviarCodigoRecuperacion(String correo) {
        if (!usuarioRepositorio.existsByCorreo(correo)) {
            return false; // El correo no está registrado
        }

        String codigo = generarCodigo();
        Optional<Recuperacion> recuperacionExistente = recuperacionRepositorio.findByCorreo(correo);

        Recuperacion recuperacion = recuperacionExistente.orElse(new Recuperacion());
        recuperacion.setCorreo(correo);
        recuperacion.setCodigo(codigo);
        recuperacion.setFechaCreacion(LocalDateTime.now());
        recuperacionRepositorio.save(recuperacion);

        // Enviar correo
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(correo);
        mensaje.setSubject("Código de recuperación");
        mensaje.setText("Tu código de recuperación es: " + codigo);
        mailSender.send(mensaje);

        return true;
    }

    @Override
    public boolean validarCodigo(String correo, String codigo) {
         Optional<Recuperacion> recuperacion = recuperacionRepositorio.findByCorreo(correo);
        return recuperacion.isPresent() && recuperacion.get().getCodigo().equals(codigo);
    }

    @Override
    public void eliminarCodigo(String correo) {
         recuperacionRepositorio.deleteByCorreo(correo);
    }
    

}
