    package com.proyecto.KASH.entidad;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;

@Entity
@Table(name="grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name="idAsesor")
    private Usuario asesor;

    @Column(name="nombre")
    private String nombre;

    @Column(name="cantidad")
    private int cantidad;

    @Column(name="estado")
    private String estado;

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    private List<GrupoAprendiz> aprendices;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Usuario getAsesor() {
        return asesor;
    }

    public void setAsesor(Usuario Asesor) {
        this.asesor = Asesor;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public List<GrupoAprendiz> getAprendices() {
        return aprendices;
    }

    public void setAprendices(List<GrupoAprendiz> aprendices) {
        this.aprendices = aprendices;
    }

    public Integer getIdGrupo() {
        return this.id;
    }


}
