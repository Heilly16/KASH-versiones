package com.proyecto.KASH.servicio;

import com.proyecto.KASH.Repository.GrupoAprendizRepositorio;
import com.proyecto.KASH.Repository.PreguntaRepositorio;
import com.proyecto.KASH.Repository.PruebaRepositorio;
import com.proyecto.KASH.Repository.RespuestaRepositorio;
import com.proyecto.KASH.Repository.RespuestaSeleccionadaRepositorio;
import com.proyecto.KASH.Repository.ResultadoPruebaRepositorio;
import com.proyecto.KASH.Repository.Usuario2Repositorio;
import com.proyecto.KASH.entidad.GrupoAprendiz;
import com.proyecto.KASH.entidad.Pregunta;
import com.proyecto.KASH.entidad.Prueba;
import com.proyecto.KASH.entidad.Respuesta;
import com.proyecto.KASH.entidad.RespuestaSeleccionada;
import com.proyecto.KASH.entidad.ResultadoPrueba;
import com.proyecto.KASH.entidad.Usuario;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AprendizPruebaServicioImpl implements AprendizPruebaServicio {

    @Autowired
    private GrupoAprendizRepositorio grupoAprendizRepo;

    @Autowired
    private PruebaRepositorio pruebaRepo;

    @Autowired
    private PreguntaRepositorio preguntaRepo;

    @Autowired
    private RespuestaRepositorio respuestaRepo;

    @Autowired
    private ResultadoPruebaRepositorio resultadoPruebaRepo;

    @Autowired
    private Usuario2Repositorio usuarioRepo;

    @Override
    public List<Prueba> obtenerPruebasPorAprendiz(Long idUsuario) {
        List<GrupoAprendiz> grupoAprendizList = grupoAprendizRepo.findByUsuarioId(idUsuario);
        List<Prueba> pruebas = new ArrayList<>();

        for (GrupoAprendiz ga : grupoAprendizList) {
            Long idGrupo = Long.valueOf(ga.getGrupo().getId());
            pruebas.addAll(pruebaRepo.findByGrupoId(idGrupo));
        }

        return pruebas;
    }

    @Override
    public Prueba obtenerPruebaPorId(Long id) {
        return pruebaRepo.findById(id).orElse(null);
    }

    @Autowired
    private RespuestaSeleccionadaRepositorio respuestaSeleccionadaRepo;

    @Override
    public double resolverPrueba(Long idUsuario, Long idPrueba, Map<Long, Long> respuestasSeleccionadas) {
        double puntuacion = 0.0;

        Usuario aprendiz = usuarioRepo.findById(idUsuario).orElse(null);
        Prueba prueba = pruebaRepo.findById(idPrueba).orElse(null);

        if (aprendiz == null || prueba == null) {
            return 0.0;
        }

        for (Map.Entry<Long, Long> entry : respuestasSeleccionadas.entrySet()) {
            Long idPregunta = entry.getKey();
            Long idRespuestaSeleccionada = entry.getValue();

            Pregunta pregunta = preguntaRepo.findById(idPregunta).orElse(null);
            Respuesta respuesta = respuestaRepo.findById(idRespuestaSeleccionada).orElse(null);

            if (pregunta != null && respuesta != null) {
                // Crear objeto de RespuestaSeleccionada
                RespuestaSeleccionada seleccionada = new RespuestaSeleccionada();
                seleccionada.setAprendiz(aprendiz);
                seleccionada.setPrueba(prueba);
                seleccionada.setPregunta(pregunta);
                seleccionada.setRespuestaSeleccionada(respuesta);

                // Guardar la respuesta
                respuestaSeleccionadaRepo.save(seleccionada);

                // Sumar puntaje si la respuesta es correcta
                if (respuesta.isEsCorrecta()) {
                    puntuacion += 1.0;
                }
            }
        }

        ResultadoPrueba resultado = new ResultadoPrueba();
        resultado.setAprendiz(aprendiz);
        resultado.setPrueba(prueba);
        resultado.setPuntuacion(Math.round(puntuacion));

        resultadoPruebaRepo.save(resultado);

        return Math.round(puntuacion);
    }

    @Override
    public List<Prueba> obtenerPruebasPorGrupo(Long idGrupo) {
        return pruebaRepo.findByGrupoId(idGrupo);
    }

    @Override
    public double actualizarPrueba(Long idUsuario, Long idPrueba, Map<Long, Long> respuestasSeleccionadas, ResultadoPrueba resultadoExistente) {
        double nuevaPuntuacion = 0.0;

        Usuario aprendiz = usuarioRepo.findById(idUsuario).orElse(null);
        Prueba prueba = pruebaRepo.findById(idPrueba).orElse(null);

        if (aprendiz == null || prueba == null) {
            return 0.0;
        }

        // Eliminar respuestas anteriores del aprendiz para esa prueba (opcional, si deseas actualizar completamente)
        List<RespuestaSeleccionada> respuestasAnteriores = respuestaSeleccionadaRepo.findByPruebaAndAprendiz(prueba, aprendiz);
        respuestaSeleccionadaRepo.deleteAll(respuestasAnteriores);

        // Registrar las nuevas respuestas seleccionadas
        for (Map.Entry<Long, Long> entry : respuestasSeleccionadas.entrySet()) {
            Long idPregunta = entry.getKey();
            Long idRespuestaSeleccionada = entry.getValue();

            Pregunta pregunta = preguntaRepo.findById(idPregunta).orElse(null);
            Respuesta respuesta = respuestaRepo.findById(idRespuestaSeleccionada).orElse(null);

            if (pregunta != null && respuesta != null) {
                RespuestaSeleccionada seleccionada = new RespuestaSeleccionada();
                seleccionada.setAprendiz(aprendiz);
                seleccionada.setPrueba(prueba);
                seleccionada.setPregunta(pregunta);
                seleccionada.setRespuestaSeleccionada(respuesta);

                respuestaSeleccionadaRepo.save(seleccionada);

                if (respuesta.isEsCorrecta()) {
                    nuevaPuntuacion += 1.0;
                }
            }
        }

        // Actualizar la puntuación del resultado existente
        resultadoExistente.setPuntuacion(Math.round(nuevaPuntuacion));
        resultadoPruebaRepo.save(resultadoExistente);

        return Math.round(nuevaPuntuacion);
    }

    private double calcularPuntuacion(Map<Long, Long> respuestasSeleccionadas) {
        int correctas = 0;
        for (Long idRespuestaSeleccionada : respuestasSeleccionadas.values()) {
            Optional<Respuesta> respuestaOpt = respuestaRepo.findById(idRespuestaSeleccionada);
            if (respuestaOpt.isPresent() && respuestaOpt.get().isEsCorrecta()) {
                correctas++;
            }
        }
        return correctas;
    }

}
