package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.GrupoAsesorRepositorio;
import com.proyecto.KASH.Repository.GrupoRepositorio;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.Usuario;
import jakarta.persistence.EntityManager;
import jakarta.persistence.ParameterMode;
import jakarta.persistence.StoredProcedureQuery;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import static org.springframework.data.jpa.domain.AbstractPersistable_.id;
import org.springframework.stereotype.Service;
import java.util.Optional;


@Service
public class GrupoServicioImpl implements GrupoServicio {

    @Autowired
    private GrupoAsesorRepositorio grupoRepository;
    
    @Autowired
    Usuario2Repositorio usuarioRepositorio;

    @Autowired
    private EntityManager entityManager;

    @Override
    @Transactional
    public void registrarAprendizEnComponente(Long idUsuario, String nombreComponente) {
        StoredProcedureQuery query = entityManager
                .createStoredProcedureQuery("registrar_aprendiz_en_componente");
        query.registerStoredProcedureParameter("p_idUsuario", Long.class, ParameterMode.IN);
        query.registerStoredProcedureParameter("p_nombreComponente", String.class, ParameterMode.IN);

        query.setParameter("p_idUsuario", idUsuario);
        query.setParameter("p_nombreComponente", nombreComponente);

        query.execute();
    }

    @Override
    public List<Grupo> buscarGruposPorNombres(List<String> nombres) {
        return grupoRepository.findByNombreIn(nombres);
    }

    @Override
    public Grupo buscarPorId(Integer id) {
        return grupoRepository.findById(id).orElse(null);
    }

    @Override
    public void guardar(Grupo grupo) {
        grupoRepository.save(grupo);
    }


    @Transactional
    public void asignarAsesorAGrupo(Integer idGrupo, Long idAsesor) {
        Grupo grupo = buscarPorId(idGrupo);
        if (grupo == null) {
            throw new RuntimeException("Grupo no encontrado");
        }

        var asesorOpt = usuarioRepositorio.findById(idAsesor);
        if (asesorOpt.isEmpty()) {
            throw new RuntimeException("Asesor no encontrado");
        }

        grupo.setAsesor(asesorOpt.get());
        guardar(grupo);
    }
    
    @Autowired
    private GrupoRepositorio grupoRepositorio;
    
    @Override
    public List<Grupo> obtenerGruposAceptadosPorAsesor(Long idUsuario) {
          return grupoRepositorio.findByAsesor_idUsuario(idUsuario);
    }

    @Override
    public Grupo obtenerGrupoPorId(int idGrupo) {
        return buscarPorId(idGrupo);
    }   

    @Override
    public Grupo obtenerPorId(Integer grupoId) {
       return grupoRepositorio.findById(grupoId).orElse(null);
    }

    @Override
    public List<Grupo> obtenerGruposPorAsesor(Long idAsesor) {
        return grupoRepository.findGruposByAsesor(idAsesor);
    }

    @Override
    public List<Grupo> obtenerGruposActivosPorAsesor(Long idAsesor) {
        // Obtener todos los grupos del asesor
        List<Grupo> todosLosGrupos = obtenerGruposPorAsesor(idAsesor);
        
        // Filtrar solo los grupos con estado "Activo"
        return todosLosGrupos.stream()
                .filter(grupo -> "Activo".equalsIgnoreCase(grupo.getEstado()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Grupo> obtenerGruposPorComponente(String nombreComponente) {
        return grupoRepositorio.findByNombreIn(List.of(nombreComponente));
    }
    
    @Override
    public int contarGruposPorComponente(String nombreComponente) {
        return obtenerGruposPorComponente(nombreComponente).size();
    }
    
    @Override
    public List<Grupo> obtenerGruposSinAsesor() {
        return grupoRepositorio.findGruposSinAsesor();
    }
    
    @Override
    public List<Grupo> listarGrupos() {
        return grupoRepositorio.findAll();
    }
    
    @Override
    public Optional<Grupo> buscarPorId(Long id) {
        return grupoRepositorio.findById(id.intValue());
    }

    @Override
    public boolean crearGrupoSiNecesario(String nombreComponente) {
        // Obtener todos los grupos para este componente
        List<Grupo> gruposExistentes = obtenerGruposPorComponente(nombreComponente);
        
        // Verificar si todos los grupos están llenos o tienen asesorías activas
        boolean necesitaNuevoGrupo = true;
        
        for (Grupo grupo : gruposExistentes) {
            // Un grupo está disponible si no tiene asesor o si tiene menos de 5 estudiantes
            if (grupo.getAsesor() == null && (grupo.getAprendices() == null || grupo.getAprendices().size() < 5)) {
                necesitaNuevoGrupo = false;
                break;
            }
        }
        
        // Si se necesita un nuevo grupo, crearlo
        if (necesitaNuevoGrupo) {
            Grupo nuevoGrupo = new Grupo();
            nuevoGrupo.setNombre(nombreComponente);
            nuevoGrupo.setEstado("Activo");
            nuevoGrupo.setCantidad(0);
            guardar(nuevoGrupo);
            return true;
        }
        
        return false;
    }
}
