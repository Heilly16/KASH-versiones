
package com.proyecto.KASH.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;

@Entity
@Table(name = "pqrs")
public class PQRS {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "idusuario")
    private Long idUsuario;

    @Column(name = "idcoordinador")
    private Long idCoordinador;

    @Column(name = "nombre")
    private String nombre;

    @Column(name = "email")
    private String email;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "tipo")
    private String tipo;

    @Column(name = "detalles")
    private String detalles;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "estado")
    private EstadoPQRS estado;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Long getIdCoordinador() {
        return idCoordinador;
    }

    public void setIdCoordinador(Long idCoordinador) {
        this.idCoordinador = idCoordinador;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public String getDetalles() {
        return detalles;
    }

    public void setDetalles(String detalles) {
        this.detalles = detalles;
    }

    public EstadoPQRS getEstado() {
        System.out.println("EstadoPQRS value from database: " + estado);
        return estado;
    }

    public void setEstado(EstadoPQRS estado) {
        this.estado = estado;
    }


    @Override
    public String toString() {
        return "PQRS{"
                + "id=" + id
                + ", nombre='" + nombre + '\''
                + ", idUsuario=" + idUsuario
                + ", correo='" + email + '\''
                + ", fecha='" + fecha + '\''
                + ", tipo='" + tipo + '\''
                + ", detalles='" + detalles + '\''
                + ", estado='" + estado + '\''
                + '}';
    }
}
