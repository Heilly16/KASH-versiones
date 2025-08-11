package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;

public interface AprendizComponenteServicio {

    List<Componente> obtenerComponentePorUsuario(Long idUsuario);

    List<String> obtenerNombresComponentesUnicos();

    List<String> obtenerComponentesNoRegistradosPorUsuario(Long idUsuario);

    Componente guardarComponente(Componente componente);

    void eliminarComponente(Long idComponente, Long idUsuario);
    
    public boolean eliminarComponenteSiNoTieneAsesoriasActivas(Long idComponente);
    
    boolean componentePerteneceAPrograma(String nombreComponente, String nombrePrograma);

    void registrarComponenteParaAprendiz(Long idAprendiz, String nombreComponente);

    void eliminarComponentePorId(Integer id);

    List<Componente> findByPrograma(String programa);

    List<Componente> findAll();

    List<Componente> findComponentesByAprendizId(Long idUsuario);

    List<Componente> findComponentesNoRegistradosPorUsuarioYPrograma(Long idUsuario, String programa);
}
