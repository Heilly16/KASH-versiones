package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;

public interface GrupoAprendizServicio {

    List<Grupo> obtenerGruposPorAprendiz(Long idAprendiz);
    
    List<Grupo> obtenerGruposActivosPorAprendiz(Long idAprendiz);

    List<Usuario> obtenerAprendicesPorGrupo(int idGrupo);

    List<GrupoAprendiz> obtenerGrupoAprendizPorGrupo(int idGrupo);

    boolean verificarAprendizEnGrupo(Long idAprendiz, int idGrupo);
    
    int contarAprendicesEnGrupo(int idGrupo);
    
    void guardarGrupoAprendiz(GrupoAprendiz grupoAprendiz);
    
    boolean eliminarAprendizDeGrupo(Long idGrupo, Long idAprendiz);

    /**
     * Verifica si un aprendiz ya está registrado en un grupo específico.
     * @param idAprendiz ID del aprendiz
     * @param idGrupo ID del grupo
     * @return true si el aprendiz ya está en el grupo, false en caso contrario
     */
    boolean existeAprendizEnGrupo(Long idAprendiz, int idGrupo);

    /**
     * Registra un aprendiz en un grupo específico.
     * @param idAprendiz ID del aprendiz
     * @param idGrupo ID del grupo
     * @return true si el registro fue exitoso, false en caso contrario
     */
    boolean registrarAprendizEnGrupo(Long idAprendiz, int idGrupo);
}
