package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Comentario;
import java.util.List;

public interface ComentarioServicio {

    public List<Comentario> ObtenerComentario(int idForo);

    void eliminarComentario(Long idComentario);
}
