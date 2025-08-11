package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;

public interface AsesorComponenteServicio {

    List<Componente> obtenerComponentePorUsuario(Long idUsuario);

    List<String> obtenerNombresComponentesUnicos();

    Componente guardarComponente(Componente componente);

    void eliminarComponente(Long idComponente, Long idUsuario);
    
    List<Componente> findByUsuario(Usuario usuario);
}
