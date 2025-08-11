package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import java.util.List;
import java.util.Optional;

public interface GrupoServicio {
    public void registrarAprendizEnComponente(Long idUsuario, String nombreComponente);
        
    public List<Grupo> buscarGruposPorNombres(List<String> nombres);
    
    public Grupo buscarPorId(Integer id);
    
    public void guardar(Grupo grupo);

    public void asignarAsesorAGrupo(Integer idGrupo, Long idUsuario);

    public List<Grupo> obtenerGruposAceptadosPorAsesor(Long idUsuario);

    public Grupo obtenerGrupoPorId(int idGrupo);

    public Grupo obtenerPorId(Integer grupoId);

    public List<Grupo> obtenerGruposPorAsesor(Long idAsesor);

    public List<Grupo> obtenerGruposActivosPorAsesor(Long idAsesor);

    public List<Grupo> obtenerGruposPorComponente(String nombreComponente);
    
    public int contarGruposPorComponente(String nombreComponente);
    
    public List<Grupo> obtenerGruposSinAsesor();
    
    public List<Grupo> listarGrupos();
    
    public Optional<Grupo> buscarPorId(Long id);

    /**
     * Crea un nuevo grupo para un componente si es necesario.
     * Se considera necesario cuando todos los grupos existentes para ese componente
     * están llenos (5 o más estudiantes) o tienen asesorías activas.
     * @param nombreComponente El nombre del componente para el que se verificará la necesidad de un nuevo grupo
     * @return true si se creó un nuevo grupo, false si no fue necesario
     */
    boolean crearGrupoSiNecesario(String nombreComponente);
}
