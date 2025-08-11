package com.proyecto.KASH.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "IDforo", nullable = false, length = 100)
    private int idForo;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "contenido", nullable = false, length = 100)
    private String contenido;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getIdForo() {
        return idForo;
    }

    public void setIdForo(int idForo) {
        this.idForo = idForo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public Comentario(Long id, int idForo, String nombre, String contenido) {
        this.id = id;
        this.idForo = idForo;
        this.nombre = nombre;
        this.contenido = contenido;
    }

    public Comentario(int idForo, String nombre, String contenido) {
        this.idForo = idForo;
        this.nombre = nombre;
        this.contenido = contenido;
    }

    public Comentario() {
    }

    @Override
    public String toString() {
        return "Comentario{" + "id=" + id + ", idForo=" + idForo + ", nombre=" + nombre + ", contenido=" + contenido + '}';
    }
}
