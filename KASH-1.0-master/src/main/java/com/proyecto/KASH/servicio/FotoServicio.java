package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.fotos;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

/**
 *
 * @author USER
 */
public interface FotoServicio {

    void guardarFoto(MultipartFile foto, Long idUsuario) throws IOException;
    fotos obtenerFotoPorIdUsuario(Long idUsuario);
    void actualizarFoto(MultipartFile foto, Long idUsuario) throws IOException;
}
