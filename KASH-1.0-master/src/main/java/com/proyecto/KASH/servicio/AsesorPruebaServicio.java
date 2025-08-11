package com.proyecto.KASH.servicio;

import com.proyecto.KASH.entidad.Grupo;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.Respuesta;
import java.util.List;

public interface AsesorPruebaServicio {
  void guardarPrueba(Prueba prueba, List<Pregunta> preguntas);
  
}
