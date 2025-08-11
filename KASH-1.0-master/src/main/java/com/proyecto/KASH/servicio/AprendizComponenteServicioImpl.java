package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AprendizComponenteRepositorio;
import com.proyecto.KASH.Repository.AsesoriaRepositorio;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.Asesoria;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Programa;
import com.proyecto.KASH.entidad.Usuario;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AprendizComponenteServicioImpl implements AprendizComponenteServicio {

    @Autowired
    private AprendizComponenteRepositorio componenteRepositorio;

    @Autowired
    private AsesoriaRepositorio asesoriaRepositorio;
    
    @Autowired
    private ProgramaServicio programaServicio;
    
    @Autowired
    private Usuario2Repositorio usuarioRepositorio;

    @Override
    public List<Componente> obtenerComponentePorUsuario(Long idUsuario) {
        return componenteRepositorio.findByUsuario_IdUsuario(idUsuario); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<String> obtenerNombresComponentesUnicos() {
        return componenteRepositorio.findDistinctComponentNames(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
    @Override
    public List<String> obtenerComponentesNoRegistradosPorUsuario(Long idUsuario) {
        // Obtener el usuario para verificar su programa
        Usuario usuario = usuarioRepositorio.findById(idUsuario).orElse(null);
        if (usuario == null) {
            return Collections.emptyList();
        }
        
        String programaUsuario = usuario.getPrograma();
        
        // Obtener todos los componentes disponibles
        List<String> todosComponentes = obtenerNombresComponentesUnicos();
        
        // Filtrar componentes que ya están registrados por el usuario
        List<Componente> componentesRegistrados = obtenerComponentePorUsuario(idUsuario);
        Set<String> nombresComponentesRegistrados = componentesRegistrados.stream()
                .map(Componente::getNombre)
                .collect(Collectors.toSet());
        
        // Filtrar componentes que pertenecen al programa del usuario y que no están registrados
        return todosComponentes.stream()
                .filter(componente -> !nombresComponentesRegistrados.contains(componente))
                .filter(componente -> componentePerteneceAPrograma(componente, programaUsuario))
                .collect(Collectors.toList());
    }

    @Override
    public Componente guardarComponente(Componente componente) {
        return componenteRepositorio.save(componente); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public void eliminarComponente(Long idComponente, Long idUsuario) {
        componenteRepositorio.deleteByIdAndUsuario_IdUsuario(idComponente, idUsuario); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean eliminarComponenteSiNoTieneAsesoriasActivas(Long idComponente) {
        Componente componente = componenteRepositorio.findById(idComponente).orElse(null);
        if (componente == null) {
            return false;
        }

        Usuario aprendiz = componente.getUsuario();

        // Buscar asesorías activas donde el aprendiz está inscrito
        List<Asesoria> asesoriasActivas = asesoriaRepositorio.findAsesoriasActivasPorAprendiz(aprendiz);

        if (!asesoriasActivas.isEmpty()) {
            // Tiene asesorías activas, no eliminar
            return false;
        }

        componenteRepositorio.delete(componente);
        return true;
    }

    @Override
    public boolean componentePerteneceAPrograma(String nombreComponente, String nombrePrograma) {
        // Si no hay programa especificado, no podemos filtrar
        if (nombrePrograma == null || nombrePrograma.isEmpty()) {
            return true; // Permitimos todos los componentes
        }
        
        try {
            // Usar el nuevo método del repositorio para buscar componentes por nombre y programa
            List<Componente> componentes = componenteRepositorio.findByNombreAndPrograma(nombreComponente, nombrePrograma);
            
            // Si encontramos al menos un componente, entonces pertenece al programa
            return !componentes.isEmpty();
        } catch (Exception e) {
            // Si hay algún error, registramos el error y permitimos el acceso
            System.err.println("Error al verificar programa del componente: " + e.getMessage());
            e.printStackTrace();
            return true; // Por seguridad, permitimos el acceso en caso de error
        }
    }

    @Override
    public List<Componente> findByPrograma(String programa) {
        return componenteRepositorio.findByPrograma(programa);
    }
    
    @Override
    public List<Componente> findAll() {
        return componenteRepositorio.findAll();
    }
    
    @Override
    public List<Componente> findComponentesByAprendizId(Long idUsuario) {
        return componenteRepositorio.findComponentesByAprendizId(idUsuario);
    }
    
    @Override
    public List<Componente> findComponentesNoRegistradosPorUsuarioYPrograma(Long idUsuario, String programa) {
        return componenteRepositorio.findComponentesNoRegistradosPorUsuarioYPrograma(idUsuario, programa);
    }

    @Override
    public void eliminarComponentePorId(Integer id) {
        componenteRepositorio.deleteById(id.longValue());
    }

    @Override
    public void registrarComponenteParaAprendiz(Long idAprendiz, String nombreComponente) {
        // Obtener el usuario (aprendiz)
        Usuario aprendiz = usuarioRepositorio.findById(idAprendiz).orElse(null);
        if (aprendiz == null) {
            throw new IllegalArgumentException("Aprendiz no encontrado con ID: " + idAprendiz);
        }
        
        // Buscar el componente por nombre
        Componente componente = componenteRepositorio.findByNombre(nombreComponente)
                .stream()
                .findFirst()
                .orElse(null);
        
        if (componente == null) {
            // Si el componente no existe, crear uno nuevo
            componente = new Componente();
            componente.setNombre(nombreComponente);
            
            // Si el aprendiz tiene un programa, buscar ese programa y asociarlo al componente
            if (aprendiz.getPrograma() != null && !aprendiz.getPrograma().isEmpty()) {
                List<Programa> programasAprendiz = programaServicio.buscarPorNombre(aprendiz.getPrograma());
                if (!programasAprendiz.isEmpty()) {
                    componente.setProgramas(programasAprendiz);
                }
            }
        }
        
        // Establecer el usuario (aprendiz) en el componente
        componente.setUsuario(aprendiz);
        
        // Guardar el componente
        componenteRepositorio.save(componente);
    }
}
