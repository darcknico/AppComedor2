package com.example.aldebaran.appcomedor.modelos;

import android.view.View;

import java.util.Date;

/**
 * Created by aldebaran on 28/06/17.
 */

public class TicketMenu {

    public static final int TICKET_TYPE=1;
    public static final int MENU_TYPE=2;
    public static final int EMPTY_TYPE=0;

    private int tipo = EMPTY_TYPE;
    private String info;

    private View.OnClickListener listener = null;

    public TicketMenu() {
    }

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
