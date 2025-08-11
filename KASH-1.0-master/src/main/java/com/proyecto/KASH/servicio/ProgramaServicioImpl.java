package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.ProgramaRepositorio;
import com.proyecto.KASH.entidad.Programa;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgramaServicioImpl implements ProgramaServicio {
    
    @Autowired
    private ProgramaRepositorio programaRepositorio;
    
    @Override
    public List<Programa> listarProgramas() {
        return programaRepositorio.findAll();
    }
    
    @Override
    public List<String> listarNombresProgramas() {
        return programaRepositorio.findAllProgramNames();
    }
    
    @Override
    public Optional<Programa> buscarPorId(Long id) {
        return programaRepositorio.findById(id);
    }
    
    @Override
    public Programa guardar(Programa programa) {
        return programaRepositorio.save(programa);
    }
    
    @Override
    public boolean existePorNombre(String nombre) {
        return programaRepositorio.existsByNombre(nombre);
    }
    
    @Override
    public List<Programa> buscarPorNombre(String nombre) {
        return programaRepositorio.findByNombreContainingIgnoreCase(nombre);
    }
} 