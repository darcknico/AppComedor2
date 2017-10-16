package com.example.aldebaran.appcomedor.listener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.FrameLayout;

import com.example.aldebaran.appcomedor.fragment.ListaFragment;
import com.example.aldebaran.appcomedor.fragment.MenuFragment;
import com.example.aldebaran.appcomedor.fragment.TicketFragment;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;

/**
 * Created by karen on 15/10/2017.
 */

public class TicketMenuListener implements View.OnClickListener {
    private TicketMenu ticketMenu;
    private ListaFragment listaFragment;

    public TicketMenuListener(TicketMenu ticketMenu, ListaFragment listaFragment){
        this.ticketMenu = ticketMenu;
        this.listaFragment = listaFragment;
    }
    @Override
    public void onClick(View v) {
        int id = 0;
        Bundle bundle = new Bundle();
        Fragment fragment = null;
        switch (ticketMenu.getTipo()){
            case TicketMenu.MENU_TYPE:
                id = ((Menu) ticketMenu).getId();
                fragment = new MenuFragment();
                bundle.putInt("id",id);
                fragment.setArguments(bundle);
                listaFragment._LoadFragment(fragment);
                break;
            case TicketMenu.TICKET_TYPE:
                id = ((Ticket) ticketMenu).getId();
                fragment = new TicketFragment();
                bundle.putInt("id",id);
                fragment.setArguments(bundle);
                listaFragment._LoadFragment(fragment);
                break;
        }

    }
}
