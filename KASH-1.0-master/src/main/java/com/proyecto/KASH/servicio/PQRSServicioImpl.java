package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.PQRSRepository;
import com.proyecto.KASH.entidad.PQRS;
import jakarta.transaction.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PQRSServicioImpl implements PQRSServicio {

    @Autowired
    private PQRSRepository pqrsRepository;

    @Override
    @Transactional
    public PQRS guardarPQRS(PQRS pqrs) {
        System.out.println("PQRSServicioImpl.guardarPQRS() called");
        System.out.println("PQRS object: " + pqrs);
        PQRS savedPQRS = pqrsRepository.save(pqrs);
        System.out.println("PQRS saved successfully with ID: " + savedPQRS.getId());
        return savedPQRS;
    }

    @Override
    public List<PQRS> obtenerTodasPQRS() {
        System.out.println("PQRSServicioImpl.obtenerTodasPQRS() called");
        return pqrsRepository.findAll();
    }

    @Override
    public PQRS obtenerPQRSPorId(Long id) {
        Optional<PQRS> pqrs = pqrsRepository.findById(id);
        return pqrs.orElse(null);
    }

    @Override
    @Transactional
    public void eliminarPQRS(Long id) {
        pqrsRepository.deleteById(id);
    }

    @Override
    public List<PQRS> listarTodas() {
        return pqrsRepository.findAll();
    }

    @Override
    public List<PQRS> buscarPorCriterio(String filtro) {
        return pqrsRepository.buscarPorCriterio(filtro.toLowerCase());
    }
}
