package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aldebaran on 30/06/17.
 */

public class CrearTransaccionBody {

    @SerializedName("concepto")
    @Expose
    private String concepto;
    @SerializedName("monto")
    @Expose
    private String monto;

    public CrearTransaccionBody(String concepto, String monto) {
        this.concepto = concepto;
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }
}
