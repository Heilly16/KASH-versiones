package com.proyecto.KASH.entidad;

import jakarta.persistence.*;
import java.sql.Blob;
import java.time.LocalDate;

@Entity
@Table(name = "foro")
public class foro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "tema", nullable = false, length = 200)
    private String tema;

    @Column(name = "contenido", nullable = false, length = 1000)
    private String contenido;

    @Column(name = "fecha", nullable = false)
    private LocalDate fecha;  

    @Column(name = "IDusuario", nullable = false)
    private int idUsuario;

    @Column(name = "trimestre", nullable = false)
    private int trimestre;

    @Lob
    @Column(name = "foto", columnDefinition = "LONGBLOB")
    private Blob foto;

    // Constructor con todos los campos excepto la fecha
    public foro(Long id, String nombre, String tema, String contenido, int idUsuario, int trimestre, Blob foto) {
        this.id = id;
        this.nombre = nombre;
        this.tema = tema;
        this.contenido = contenido;
        this.fecha = LocalDate.now();  
        this.idUsuario = idUsuario;
        this.trimestre = trimestre;
        this.foto = foto;
    }

    // Constructor vacío necesario para JPA
    public foro() {
        this.fecha = LocalDate.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTema() {
        return tema;
    }

    public void setTema(String tema) {
        this.tema = tema;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(int trimestre) {
        this.trimestre = trimestre;
    }

    public Blob getFoto() {
        return foto;
    }

    public void setFoto(Blob foto) {
        this.foto = foto;
    }

    @Override
    public String toString() {
        return "Foro{"
                + "id=" + id
                + ", nombre='" + nombre + '\''
                + ", tema='" + tema + '\''
                + ", contenido='" + contenido + '\''
                + ", fecha=" + fecha
                + ", idUsuario=" + idUsuario
                + ", trimestre=" + trimestre
                + '}';
    }
}
