package com.proyecto.KASH.entidad;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Entity
@Table(name = "asesoria")
public class Asesoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "idGrupo")
    private Grupo grupo;

    @Column(name = "fecha")
    private LocalDate fecha;

    @Column(name = "hora")
    private LocalTime hora;

    @Column(name = "link")
    private String link;

    @Column(name = "dias_asesoria")
    private Integer dias_asesoria;

    @Column(name = "fecha_inicio")
    private LocalDate fecha_inicio;

    private boolean completada;

    @Column(name = "estado")
    private String estado;

    //getters and setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Grupo getGrupo() {
        return grupo;
    }

    public void setGrupo(Grupo grupo) {
        this.grupo = grupo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getDias_asesoria() {
        return dias_asesoria;
    }

    public void setDias_asesoria(Integer dias_asesoria) {
        this.dias_asesoria = dias_asesoria;
    }

    public LocalDate getFecha_inicio() {
        return fecha_inicio;
    }

    public void setFecha_inicio(LocalDate fecha_inicio) {
        this.fecha_inicio = fecha_inicio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public boolean isCompletada() {
        return completada;
    }

    public void setCompletada(boolean completada) {
        this.completada = completada;
    }

    public String getFechaFormateada() {
        if (fecha == null) {
            return "";
        }
        DayOfWeek diaSemana = fecha.getDayOfWeek();
        String nombreDia = diaSemana.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        String nombreMes = fecha.getMonth().getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
        return nombreDia + " " + fecha.getDayOfMonth() + " de " + nombreMes;
    }

    public String getDiaAsesoriaNombre() {
        if (fecha == null) {
            return "";
        }
        DayOfWeek dia = fecha.getDayOfWeek();
        return dia.getDisplayName(TextStyle.FULL, new Locale("es", "ES"));
    }

    public String getHoraFormateada() {
        if (hora == null) {
            return "";
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", new Locale("es", "ES"));
        return hora.format(formatter);
    }

}
