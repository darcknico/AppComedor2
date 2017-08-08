package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karen on 27/6/2017.
 */

public class RespuestaListaAPI {
    @SerializedName("resultado")
    @Expose
    private String resultado;
    @SerializedName("salida")
    @Expose
    private JsonArray salida;
    @SerializedName("numfilas")
    @Expose
    private Integer numfilas;

    public RespuestaListaAPI(String resultado, JsonArray salida, Integer numfilas) {
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

    public JsonArray getSalida() {
        return salida;
    }

    public void setSalida(JsonArray salida) {
        this.salida = salida;
    }

    public Integer getNumfilas() {
        return numfilas;
    }

    public void setNumfilas(Integer numfilas) {
        this.numfilas = numfilas;
    }
}
