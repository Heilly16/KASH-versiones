package com.proyecto.KASH.entidad;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "usuarios")
public class Usuario {

    public static Long getId() {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Id
    @Column(name = "IDusuario", nullable = false, length = 100)
    private Long idUsuario;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Componente> componente;

    public List<Componente> getComponentes() {
        return componentes;
    }

    public void setComponentes(List<Componente> componentes) {
        this.componentes = componentes;
    }

    @Column(name = "rol", nullable = false)
    private String rolSeleccionado;

    @Column(name = "nombres", nullable = false)
    private String nombres;

    @Column(name = "primerA", nullable = false)
    private String primerA;

    @Column(name = "segundoA")
    private String segundoA;

    @Column(name = "fnacimiento", nullable = false)
    private LocalDate fNacimiento;

    @Column(name = "tdocumento", nullable = false)
    private String tDocumento;

    @Column(name = "trimestre")
    private String trimestre;

    @Column(name = "correo", unique = true, nullable = false)
    private String correo;

    @Column(name = "numero", nullable = false)
    private String numero;

    @Column(name = "usuario", unique = true, nullable = false)
    private String usuario;

    @Column(name = "pass", nullable = false)
    private String pass;

    @Column(name = "estado", nullable = false)
    private String estado = "activo"; // valor por defecto

    @Column(name = "etapa")
    private String etapa;

    @Column(name = "programa", length = 30)
    private String programa;

    // Getters y Setters
    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRolSeleccionado() {
        return rolSeleccionado;
    }

    public void setRolSeleccionado(String rolSeleccionado) {
        this.rolSeleccionado = rolSeleccionado;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getPrimerA() {
        return primerA;
    }

    public void setPrimerA(String primerA) {
        this.primerA = primerA;
    }

    public String getSegundoA() {
        return segundoA;
    }

    public void setSegundoA(String segundoA) {
        this.segundoA = segundoA;
    }

    public LocalDate getFNacimiento() {
        return fNacimiento;
    }

    public void setFNacimiento(LocalDate fNacimiento) {
        this.fNacimiento = fNacimiento;
    }

    public String getTDocumento() {
        return tDocumento;
    }

    public void setTDocumento(String tDocumento) {
        this.tDocumento = tDocumento;
    }

    public String getTrimestre() {
        return trimestre;
    }

    public void setTrimestre(String trimestre) {
        this.trimestre = trimestre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getEtapa() {
        return etapa;
    }

    public void setEtapa(String etapa) {
        this.etapa = etapa;
    }

    public String getPrograma() {
        return programa;
    }

    public void setPrograma(String programa) {
        this.programa = programa;
    }

    
    
    
    public Usuario(Long idUsuario, String rolSeleccionado, String nombres, String primerA, String segundoA, LocalDate fNacimiento, String tDocumento, String trimestre, String correo, String numero, String usuario, String pass) {
        this.idUsuario = idUsuario;
        this.rolSeleccionado = rolSeleccionado;
        this.nombres = nombres;
        this.primerA = primerA;
        this.segundoA = segundoA;
        this.fNacimiento = fNacimiento;
        this.tDocumento = tDocumento;
        this.trimestre = trimestre;
        this.correo = correo;
        this.numero = numero;
        this.usuario = usuario;
        this.pass = pass;
    }

    public Usuario(String rolSeleccionado, String nombres, String primerA, String segundoA, LocalDate fNacimiento, String tDocumento, String trimestre, String correo, String numero, String usuario, String pass) {
        this.rolSeleccionado = rolSeleccionado;
        this.nombres = nombres;
        this.primerA = primerA;
        this.segundoA = segundoA;
        this.fNacimiento = fNacimiento;
        this.tDocumento = tDocumento;
        this.trimestre = trimestre;
        this.correo = correo;
        this.numero = numero;
        this.usuario = usuario;
        this.pass = pass;
    }

    public Usuario() {
    }

    public Usuario(String usuario, String pass) {
        this.usuario = usuario;
        this.pass = pass;
    }

    @Override
    public String toString() {
        return "Usuario{" + "idUsuario=" + idUsuario + ", rolSeleccionado=" + rolSeleccionado + ", nombres=" + nombres + ", primerA=" + primerA + ", segundoA=" + segundoA + ", fNacimiento=" + fNacimiento + ", tDocumento=" + tDocumento + ", trimestre=" + trimestre + ", correo=" + correo + ", numero=" + numero + ", usuario=" + usuario + ", pass=" + pass + '}';
    }

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<Componente> componentes;

}
