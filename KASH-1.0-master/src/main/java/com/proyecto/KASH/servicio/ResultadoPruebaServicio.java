package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import java.util.Optional;


public interface ResultadoPruebaServicio {

   
     Optional<ResultadoPrueba> obtenerResultadoPorAprendizYPrueba(Long aprendizId, Long pruebaId);

    
}
