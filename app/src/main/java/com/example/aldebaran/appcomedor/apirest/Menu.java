package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by aldebaran on 28/06/17.
 */

public class Menu extends ticketMenu implements Serializable{
    @SerializedName("idMenu")
    @Expose
    private Integer idMenu;
    @SerializedName("fecha")
    @Expose
    private String fecha;
    @SerializedName("precio")
    @Expose
    private String precio;
    @SerializedName("finalizado")
    @Expose
    private String finalizado;
    @SerializedName("descripcion")
    @Expose
    private String descripcion;
    @SerializedName("creado")
    @Expose
    private String creado;
    @SerializedName("modificado")
    @Expose
    private String modificado;

    /**
     * No args constructor for use in serialization
     *
     */
    public Menu() {
    }

    /**
     *
     * @param finalizado
     * @param precio
     * @param fecha
     * @param creado
     * @param modificado
     * @param descripcion
     * @param idMenu
     */
    public Menu(Integer idMenu, String fecha, String precio, String finalizado, String descripcion, String creado, String modificado) {
        super();
        this.idMenu = idMenu;
        this.fecha = fecha;
        this.precio = precio;
        this.finalizado = finalizado;
        this.descripcion = descripcion;
        this.creado = creado;
        this.modificado = modificado;
    }

    public Integer getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(Integer idMenu) {
        this.idMenu = idMenu;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getFinalizado() {
        return finalizado;
    }

    public void setFinalizado(String finalizado) {
        this.finalizado = finalizado;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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
}
