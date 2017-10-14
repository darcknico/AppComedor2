package com.example.aldebaran.appcomedor.modelos;

import android.view.View;

/**
 * Created by karen on 13/10/2017.
 */

public class Empty {

    public static final int TRANSACCION_TYPE=1;
    public static final int EMPTY_TYPE=0;

    private int tipo = EMPTY_TYPE;
    private String info;

    private View.OnClickListener listener = null;

    public int getTipo() {
        return tipo;
    }

    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public View.OnClickListener getListener() {
        return listener;
    }

    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
}
