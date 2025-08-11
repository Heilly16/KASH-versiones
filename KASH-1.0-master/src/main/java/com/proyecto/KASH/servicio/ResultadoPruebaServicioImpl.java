package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.ResultadoPruebaRepositorio;
import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ResultadoPruebaServicioImpl implements ResultadoPruebaServicio {

    @Autowired
    private ResultadoPruebaRepositorio resultadoPruebaRepositorio;

    @Override
    public Optional<ResultadoPrueba> obtenerResultadoPorAprendizYPrueba(Long aprendizId, Long pruebaId) {
        return resultadoPruebaRepositorio.findByAprendizIdAndPruebaId(aprendizId, pruebaId);
    }


}
