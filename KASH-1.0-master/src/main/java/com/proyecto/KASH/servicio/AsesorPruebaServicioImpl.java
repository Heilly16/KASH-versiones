package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.PruebaRepositorio;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Prueba;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AsesorPruebaServicioImpl implements AsesorPruebaServicio {

    @Autowired
    private PruebaRepositorio pruebaRepositorio;

    @Override
    @Transactional
    public void guardarPrueba(Prueba prueba, List<Pregunta> preguntas) {
        prueba.setPreguntas(preguntas);
        pruebaRepositorio.save(prueba);
    }

}
