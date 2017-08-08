package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karen on 25/6/2017.
 */

public class Usuario {


    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("nombre")
    @Expose
    private String nombre;
    @SerializedName("dni")
    @Expose
    private Integer dni;
    @SerializedName("correo")
    @Expose
    private Object correo;
    @SerializedName("contrase\u00f1a")
    @Expose
    private String contraseA;
    @SerializedName("token")
    @Expose
    private String token;
    @SerializedName("rolUsuario")
    @Expose
    private String rolUsuario;
    @SerializedName("idUsuarioTipo")
    @Expose
    private Integer idUsuarioTipo;
    @SerializedName("miniatura")
    @Expose
    private Object miniatura;
    @SerializedName("imagen")
    @Expose
    private Object imagen;
    @SerializedName("creado")
    @Expose
    private String creado;
    @SerializedName("modificado")
    @Expose
    private String modificado;
    @SerializedName("saldo")
    @Expose
    private String saldo;
    @SerializedName("ticketsRestantes")
    @Expose
    private String ticketsRestantes;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Integer getDni() {
        return dni;
    }

    public void setDni(Integer dni) {
        this.dni = dni;
    }

    public Object getCorreo() {
        return correo;
    }

    public void setCorreo(Object correo) {
        this.correo = correo;
    }

    public String getContraseA() {
        return contraseA;
    }

    public void setContraseA(String contraseA) {
        this.contraseA = contraseA;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(String rolUsuario) {
        this.rolUsuario = rolUsuario;
    }

    public Integer getIdUsuarioTipo() {
        return idUsuarioTipo;
    }

    public void setIdUsuarioTipo(Integer idUsuarioTipo) {
        this.idUsuarioTipo = idUsuarioTipo;
    }

    public Object getMiniatura() {
        return miniatura;
    }

    public void setMiniatura(Object miniatura) {
        this.miniatura = miniatura;
    }

    public Object getImagen() {
        return imagen;
    }

    public void setImagen(Object imagen) {
        this.imagen = imagen;
    }

    public String getCreado() {
        return creado;
    }

    public void setCreado(String creado) {
        this.creado = creado;
    }

    public String getModificado() {
        return modificado;
    }

    public void setModificado(String modificado) {
        this.modificado = modificado;
    }

    public String getSaldo() {
        return saldo;
    }

    public void setSaldo(String saldo) {
        this.saldo = saldo;
    }

    public String getTicketsRestantes() {
        return ticketsRestantes;
    }

    public void setTicketsRestantes(String ticketsRestantes) {
        this.ticketsRestantes = ticketsRestantes;
    }
}
