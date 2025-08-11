package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.ComentarioRepositorio;
import com.proyecto.KASH.entidad.Comentario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ComentarioServicioImpl implements ComentarioServicio {
    
    @Autowired
    private ComentarioRepositorio repositorio;
    
    public List<Comentario> ObtenerComentario(int idForo) {
        return repositorio.findByIdForo(idForo); // Llamada al repositorio con el parámetro
    }

    @Override
    public void eliminarComentario(Long idComentario) {
        repositorio.deleteById(idComentario);
    }

 }
