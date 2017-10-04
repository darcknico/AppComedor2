package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by karen on 27/6/2017.
 */

public class Transaccion {
    private int id;
    private int idUsuario;
    private String concepto;
    private String token;
    private String monto;
    private String estado_transaccion;
    private String fecha_acreditacion;
    private String paymentMethodId;
    private String cardIssuerId;
    private String installment;
    private String cardToken;
    private String campaignId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMonto() {
        return monto;
    }

    public void setMonto(String monto) {
        this.monto = monto;
    }

    public String getEstado_transaccion() {
        return estado_transaccion;
    }

    public void setEstado_transaccion(String estado_transaccion) {
        this.estado_transaccion = estado_transaccion;
    }

    public String getFecha_acreditacion() {
        return fecha_acreditacion;
    }

    public void setFecha_acreditacion(String fecha_acreditacion) {
        this.fecha_acreditacion = fecha_acreditacion;
    }

    public String getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(String paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }

    public String getCardIssuerId() {
        return cardIssuerId;
    }

    public void setCardIssuerId(String cardIssuerId) {
        this.cardIssuerId = cardIssuerId;
    }

    public String getInstallment() {
        return installment;
    }

    public void setInstallment(String installment) {
        this.installment = installment;
    }

    public String getCardToken() {
        return cardToken;
    }

    public void setCardToken(String cardToken) {
        this.cardToken = cardToken;
    }

    public String getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(String campaignId) {
        this.campaignId = campaignId;
    }
}
