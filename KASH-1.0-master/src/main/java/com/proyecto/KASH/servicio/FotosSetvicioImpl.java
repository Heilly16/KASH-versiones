package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.FotosRepositorio;
import com.proyecto.KASH.entidad.fotos;
import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.rowset.serial.SerialBlob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FotosSetvicioImpl implements FotoServicio {

    @Autowired
    private FotosRepositorio Repositorio;

    @Override
    public void guardarFoto(MultipartFile foto, Long idUsuario) throws IOException {
        // Convertir la imagen en un Blob
        byte[] fotoBytes = foto.getBytes();
        Blob fotoBlob = null;
        try {
            fotoBlob = new SerialBlob(fotoBytes);
        } catch (SQLException ex) {
            Logger.getLogger(FotosSetvicioImpl.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Crear una nueva foto y guardarla
        fotos nuevaFoto = new fotos();
        nuevaFoto.setIdUsuario(idUsuario);
        nuevaFoto.setFoto(fotoBlob);

        // Guardar la foto en la base de datos
        Repositorio.save(nuevaFoto);
    }

    @Override
    public fotos obtenerFotoPorIdUsuario(Long idUsuario) {
        return Repositorio.findByIdUsuario(idUsuario);
    }

    @Override
    public void actualizarFoto(MultipartFile foto, Long idUsuario) throws IOException {
        fotos fotoExistente = Repositorio.findByIdUsuario(idUsuario);

        if (fotoExistente != null) {
            try {
                // Si existe, actualiza la foto
                Blob nuevaFoto = new SerialBlob(foto.getBytes());  // Esto puede lanzar SQLException
                fotoExistente.setFoto(nuevaFoto);  // Asumiendo que tienes un campo 'foto' en la entidad 'fotos'
                Repositorio.save(fotoExistente); // Guarda la foto actualizada
            } catch (SQLException e) {
                throw new IOException("Error al convertir la foto a Blob", e);  // Maneja la excepción y la convierte en IOException
            }
        }
    }

}
