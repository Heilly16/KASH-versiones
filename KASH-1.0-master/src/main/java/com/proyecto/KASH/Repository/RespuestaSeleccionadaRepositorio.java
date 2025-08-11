package com.proyecto.KASH.Repository;

import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.RespuestaSeleccionada;
import com.proyecto.KASH.entidad.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;


public interface RespuestaSeleccionadaRepositorio extends JpaRepository<RespuestaSeleccionada, Long>{
    List<RespuestaSeleccionada> findByAprendizAndPrueba(Usuario aprendiz, Prueba prueba);

    List<RespuestaSeleccionada> findByPrueba(Prueba prueba);

    List<RespuestaSeleccionada> findByAprendiz(Usuario aprendiz);

    public List<RespuestaSeleccionada> findByPruebaAndAprendiz(Prueba prueba, Usuario aprendiz);
}
