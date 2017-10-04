package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karen on 30/7/2017.
 */

public class RespuestaErrorApi {
    @SerializedName("resultado")
    @Expose
    private String resultado;
    @SerializedName("salida")
    @Expose
    private JsonPrimitive salida = null;
    @SerializedName("numfilas")
    @Expose
    private Integer numfilas;

    /**
     * No args constructor for use in serialization
     *
     */
    public RespuestaErrorApi() {
    }

    /**
     *
     * @param numfilas
     * @param salida
     * @param resultado
     */
    public RespuestaErrorApi(String resultado, JsonPrimitive salida, Integer numfilas) {
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

    public JsonPrimitive getSalida() {
        return salida;
    }

    public void setSalida(JsonPrimitive salida) {
        this.salida = salida;
    }

    public Integer getNumfilas() {
        return numfilas;
    }

    public void setNumfilas(Integer numfilas) {
        this.numfilas = numfilas;
    }
}
