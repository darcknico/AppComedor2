package com.example.aldebaran.appcomedor.modelos;

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
}
