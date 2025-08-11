package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.AsesorComponenteRepositorio;
import com.proyecto.KASH.entidad.Componente;
import com.proyecto.KASH.entidad.Usuario;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsesorComponenteServicioImpl implements AsesorComponenteServicio {

    @Autowired
    private AsesorComponenteRepositorio componenteRepositorio;

    @Override
    public List<Componente> obtenerComponentePorUsuario(Long idUsuario) {
        return componenteRepositorio.findByUsuario_IdUsuario(idUsuario);
    }

    @Override
    public List<String> obtenerNombresComponentesUnicos() {
        return componenteRepositorio.findDistinctComponentNames();
    }

    @Override
    public Componente guardarComponente(Componente componente) {
        return componenteRepositorio.save(componente);
    }

    @Override
    @Transactional
    public void eliminarComponente(Long idComponente, Long idUsuario) {
         componenteRepositorio.deleteByIdAndUsuario_IdUsuario(idComponente, idUsuario);
    }

    @Override
    public List<Componente> findByUsuario(Usuario usuario) {
         return componenteRepositorio.findByUsuario(usuario);
    }

}
