package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by aldebaran on 26/06/17.
 */

public class RegisterBody {

    @SerializedName("dni")
    @Expose
    private String dni;
    @SerializedName("contrase\u00f1a")
    @Expose
    private String contraseA;
    @SerializedName("nombre")
    @Expose
    private String nombre;

    public RegisterBody(String dni, String nombre, String contraseA) {
        super();
        this.dni = dni;
        this.contraseA = contraseA;
        this.nombre = nombre;
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

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
}
