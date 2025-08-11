package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.foro;
import java.io.IOException;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface foroServicio {
    public List<foro> listarForos();

    public foro guardarForo(foro nuevoForo);
    
    //Guardar foto del foro
    void guardarFoto(MultipartFile foto, Long Id) throws IOException;
    foro obtenerFotoId(Long Id);
}
