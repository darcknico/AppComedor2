package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by aldebaran on 28/06/17.
 */

public class Ticket extends ticketMenu implements Serializable {

    @SerializedName("idTicket")
    @Expose
    private Integer idTicket;
    @SerializedName("idMenu")
    @Expose
    private String idMenu;
    @SerializedName("idUsuario")
    @Expose
    private String idUsuario;
    @SerializedName("precio")
    @Expose
    private String precio;
    @SerializedName("estado")
    @Expose
    private String estado;
    @SerializedName("creado")
    @Expose
    private String creado;
    @SerializedName("modificado")
    @Expose
    private String modificado;
    @SerializedName("menu")
    @Expose
    private Menu menu;

    /**
     * No args constructor for use in serialization
     *
     */
    public Ticket() {
    }

    /**
     *
     * @param estado
     * @param idUsuario
     * @param precio
     * @param creado
     * @param menu
     * @param modificado
     * @param idMenu
     * @param idTicket
     */
    public Ticket(Integer idTicket, String idMenu, String idUsuario, String precio, String estado, String creado, String modificado, Menu menu) {
        super();
        this.idTicket = idTicket;
        this.idMenu = idMenu;
        this.idUsuario = idUsuario;
        this.precio = precio;
        this.estado = estado;
        this.creado = creado;
        this.modificado = modificado;
        this.menu = menu;
    }

    public Integer getIdTicket() {
        return idTicket;
    }

    public void setIdTicket(Integer idTicket) {
        this.idTicket = idTicket;
    }

    public String getIdMenu() {
        return idMenu;
    }

    public void setIdMenu(String idMenu) {
        this.idMenu = idMenu;
    }

    public String getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
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

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

}
