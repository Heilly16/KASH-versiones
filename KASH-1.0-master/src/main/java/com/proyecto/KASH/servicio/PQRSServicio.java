package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.PQRS;
import java.util.List;



public interface PQRSServicio {
    PQRS guardarPQRS(PQRS pqrs);
    List<PQRS> obtenerTodasPQRS();
    PQRS obtenerPQRSPorId(Long id);
    void eliminarPQRS(Long id);
    public List<PQRS> listarTodas();
    public List<PQRS> buscarPorCriterio(String filtro);
    
}
