package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aldebaran on 26/06/17.
 */

public class LoginBody {

    @SerializedName("dni")
    @Expose
    private String dni;
    @SerializedName("contrase\u00f1a")
    @Expose
    private String contraseA;

    /**
     * No args constructor for use in serialization
     *
     */
    public LoginBody() {
    }

    /**
     *
     * @param contraseA
     * @param dni
     */
    public LoginBody(String dni, String contraseA) {
        super();
        this.dni = dni;
        this.contraseA = contraseA;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getContraseA() {
        return contraseA;
    }

    public void setContraseA(String contraseA) {
        this.contraseA = contraseA;
    }

}
