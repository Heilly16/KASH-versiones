package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.entidad.foro;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;

@Service
public class foroServicioImpl implements foroServicio {

    @Autowired
    private foroRepositorio repositorio;

    @Override
    public List<foro> listarForos() {
        return repositorio.findAll();
    }

 

    @Override
    @Transactional
    public void guardarFoto(MultipartFile foto, Long id) throws IOException {
        try {
            foro foro = repositorio.findById(id)
                    .orElseThrow(() -> new RuntimeException("Foro no encontrado"));

            byte[] fotoBytes = foto.getBytes();
            Blob fotoBlob = new SerialBlob(fotoBytes);

            foro.setFoto(fotoBlob);
            repositorio.save(foro); // Guardamos el foro con la foto
        } catch (SQLException e) {
            throw new IOException("Error al convertir la foto a BLOB", e);
        }
    }

    @Override
    public foro obtenerFotoId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new RuntimeException("Foro no encontrado"));
    }

    @Override
    public foro guardarForo(foro nuevoForo) {
       return repositorio.save(nuevoForo); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}
