package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aldebaran on 26/06/17.
 */

public class RespuestaAPI {
    @SerializedName("resultado")
    @Expose
    private String resultado;
    @SerializedName("salida")
    @Expose
    private JsonObject salida;
    @SerializedName("numfilas")
    @Expose
    private Integer numfilas;

    /**
     * No args constructor for use in serialization
     *
     */
    public RespuestaAPI() {
    }

    /**
     *
     * @param numfilas
     * @param salida
     * @param resultado
     */
    public RespuestaAPI(String resultado, JsonObject salida, Integer numfilas) {
        super();
        this.resultado = resultado;
        this.salida = salida;
        this.numfilas = numfilas;
    }

    public String getResultado() {
        return resultado;
    }

    public void setResultado(String resultado) {
        this.resultado = resultado;
    }

    public JsonObject getSalida() {
        return salida;
    }

    public void setSalida(JsonObject salida) {
        this.salida = salida;
    }

    public Integer getNumfilas() {
        return numfilas;
    }

    public void setNumfilas(Integer numfilas) {
        this.numfilas = numfilas;
    }
}
