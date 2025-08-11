package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Grupo;
import java.util.List;

public interface AprendizAsesoriaServicio {
    public List<Asesoria> obtenerAsesoriasPorAprendiz(Long idAprendiz);

    public List<Asesoria> obtenerAsesoriasActivas();
    
    public List<Asesoria> obtenerAsesoriasPorGrupo(Grupo grupo);
    
    public List<Asesoria> obtenerAsesoriasPorGrupo(int idGrupo);
}
