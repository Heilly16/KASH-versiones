package com.proyecto.KASH.entidad;

import jakarta.persistence.*;
import java.util.List;

@Entity
public class Componente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nombre;

    @ManyToOne(optional = false)
    @JoinColumn(name = "id_usuario", nullable = false) 
    private Usuario usuario;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "componente_programa",
        joinColumns = @JoinColumn(name = "componente_id"),
        inverseJoinColumns = @JoinColumn(name = "programa_id")
    )
    private List<Programa> programas;

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
    
    public List<Programa> getProgramas() {
        return programas;
    }

    public void setProgramas(List<Programa> programas) {
        this.programas = programas;
    }
}
