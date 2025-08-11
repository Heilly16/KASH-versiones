package com.proyecto.KASH.controlador;

import com.proyecto.KASH.Repository.ComentarioRepositorio;
import com.proyecto.KASH.Repository.foroRepositorio;
import com.proyecto.KASH.servicio.ComentarioServicio;
import com.proyecto.KASH.entidad.Comentario;
import com.proyecto.KASH.entidad.foro;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/foro")
public class ComentarioControlador {

    @Autowired
    private ComentarioServicio servicioComentario;

    @Autowired
    private ComentarioRepositorio comentarioRepositorio;

    @Autowired
    private foroRepositorio foroRepositorio;

    @GetMapping("/{idForo}")
    public String obtenerComentarios(@PathVariable("idForo") int idForo, Model model) {
        // Obtener los comentarios del foro
        List<Comentario> comentarios = servicioComentario.ObtenerComentario(idForo);

        // Agregar los comentarios al modelo
        model.addAttribute("comentarios", comentarios);

        // Redirigir a la vista donde el modal será mostrado
        return "foro";  // Esta es la vista donde mostrarás los comentarios en el modal
    }

    @GetMapping("/comentarios/{idForo}")
    @ResponseBody
    public List<Comentario> obtenerComentarios(@PathVariable("idForo") int idForo) {
        return servicioComentario.ObtenerComentario(idForo);  // Retorna los comentarios en formato JSON
    }

    @PostMapping("/comentarios/agregar")
    public String agregarComentario(@RequestParam String nombre,
            @RequestParam String contenido,
            @RequestParam int foroId) {
        // Lógica para guardar el comentario en la base de datos
        System.out.println("Nombre: " + nombre);
        System.out.println("Contenido: " + contenido);
        System.out.println("ID del foro: " + foroId);  // Verificar que se recibe correctamente

        // Crear el comentario
        Comentario comentario = new Comentario();
        comentario.setNombre(nombre);
        comentario.setContenido(contenido);
        comentario.setIdForo(foroId);  // Asignar el ID del foro directamente al comentario

        // Guardar el comentario
        comentarioRepositorio.save(comentario);  // Guardar el comentario en la base de datos

        // Redirigir de vuelta al foro
        return "redirect:/foro";  // Redirigir a la página del foro específico
    }

}
