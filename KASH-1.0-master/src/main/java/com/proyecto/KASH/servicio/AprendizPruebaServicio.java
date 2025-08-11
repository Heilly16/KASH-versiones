package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.ResultadoPrueba;
import java.util.List;
import java.util.Map;


public interface AprendizPruebaServicio {
    List<Prueba> obtenerPruebasPorAprendiz(Long idUsuario);
    Prueba obtenerPruebaPorId(Long id);
    double resolverPrueba(Long idUsuario, Long idPrueba, Map<Long, Long> respuestasSeleccionadas);

    public List<Prueba> obtenerPruebasPorGrupo(Long idGrupo);

    double actualizarPrueba(Long idUsuario, Long idPrueba, Map<Long, Long> respuestasSeleccionadas, ResultadoPrueba resultadoExistente);
}
