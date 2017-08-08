package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karen on 27/6/2017.
 */

public class Transaccion {
    @SerializedName("idTransaccion")
    @Expose
    private Integer idTransaccion;
    @SerializedName("idUsuario")
    @Expose
    private Integer idUsaurio;
    @SerializedName("concepto")
    @Expose
    private String concepto;
    @SerializedName("monto")
    @Expose
    private Double monto;
    @SerializedName("fecha")
    @Expose
    private String fecha;
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
    public Transaccion() {
    }

    /**
     *
     * @param concepto
     * @param fecha
     * @param creado
     * @param idTransaccion
     * @param modificado
     * @param idUsaurio
     * @param monto
     */
    public Transaccion(Integer idTransaccion, Integer idUsaurio, String concepto, Double monto, String fecha, String creado, String modificado) {
        super();
        this.idTransaccion = idTransaccion;
        this.idUsaurio = idUsaurio;
        this.concepto = concepto;
        this.monto = monto;
        this.fecha = fecha;
        this.creado = creado;
        this.modificado = modificado;
    }

    public Integer getIdTransaccion() {
        return idTransaccion;
    }

    public void setIdTransaccion(Integer idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public Integer getIdUsaurio() {
        return idUsaurio;
    }

    public void setIdUsaurio(Integer idUsaurio) {
        this.idUsaurio = idUsaurio;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
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
