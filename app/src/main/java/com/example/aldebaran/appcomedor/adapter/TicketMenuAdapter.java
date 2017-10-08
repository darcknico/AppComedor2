package com.example.aldebaran.appcomedor.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;

import java.util.ArrayList;

/**
 * Created by karen on 6/10/2017.
 */

public class TicketMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<TicketMenu> lista;
    private View.OnClickListener ticketItemClickListener = null,menuItemClickListener= null,emptyItemClickListener= null;

    public class TicketViewHolder extends RecyclerView.ViewHolder{
        public TextView ticketFechaCard;
        public TextView ticketEstadoCard;
        public ImageView ticketImageCard;

        public TicketViewHolder(View view){
            super(view);
            ticketFechaCard = (TextView) view.findViewById(R.id.ticketFechaCard);
            ticketEstadoCard = (TextView) view.findViewById(R.id.ticketEstadoCard);
            ticketImageCard = (ImageView) view.findViewById(R.id.ticketImageCard);
        }
    }

    public class MenuViewHolder extends RecyclerView.ViewHolder{
        public TextView menuFechaCard;
        public TextView menuPrecioCard;
        public ImageView menuImageCard;

        public MenuViewHolder(View view){
            super(view);
            menuFechaCard = (TextView) view.findViewById(R.id.menuFechaCard);
            menuPrecioCard = (TextView) view.findViewById(R.id.menuPrecioCard);
            menuImageCard = (ImageView) view.findViewById(R.id.menuImageCard);
        }
    }

    public class EmptyViewHolder extends RecyclerView.ViewHolder{
        public TextView emptyInfoCard;
        public ImageView emptyImageCard;

        public EmptyViewHolder(View view){
            super(view);
            emptyInfoCard = (TextView) view.findViewById(R.id.emptyInfoCard);
            emptyImageCard = (ImageView) view.findViewById(R.id.emptyImageCard);
        }

    }

    public TicketMenuAdapter(Context mContext, ArrayList<TicketMenu> lista){
        this.mContext = mContext;
        this.lista = lista;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case TicketMenu.TICKET_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_card, parent, false);
                return new TicketViewHolder(view);
            case TicketMenu.MENU_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_card, parent, false);
                return new MenuViewHolder(view);
            case TicketMenu.EMPTY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_card, parent, false);
                return new EmptyViewHolder(view);
        }
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        return lista.get(position).getTipo();
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        TicketMenu object = lista.get(position);
        if (object != null) {
            switch (object.getTipo()) {
                case TicketMenu.TICKET_TYPE:
                    Ticket ticket = (Ticket)object;
                    ((TicketViewHolder) holder).ticketEstadoCard.setText(ticket.getCondicion());
                    ((TicketViewHolder) holder).ticketFechaCard.setText(ticket.getFecha());
                    if(ticketItemClickListener!=null) {
                        ((TicketViewHolder) holder).ticketImageCard.setOnClickListener(this.ticketItemClickListener);
                    }
                    break;
                case TicketMenu.MENU_TYPE:
                    Menu menu = (Menu)object;
                    ((MenuViewHolder) holder).menuPrecioCard.setText(String.valueOf(menu.getPrecio()));
                    ((MenuViewHolder) holder).menuFechaCard.setText(menu.getFecha());
                    if(menuItemClickListener!=null) {
                        ((MenuViewHolder) holder).menuImageCard.setOnClickListener(this.menuItemClickListener);
                    }
                    break;
                case TicketMenu.EMPTY_TYPE:
                    ((EmptyViewHolder) holder).emptyInfoCard.setText(object.getInfo());
                    if(emptyItemClickListener!=null) {
                        ((EmptyViewHolder) holder).emptyImageCard.setOnClickListener(this.emptyItemClickListener);
                    }
                    break;
            }
        }
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }


    public void removeAt(int position) {
        lista.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, lista.size());
    }

    public void setTicketItemClickListener(View.OnClickListener ticketItemClickListener) {
        this.ticketItemClickListener = ticketItemClickListener;
    }

    public void setMenuItemClickListener(View.OnClickListener menuItemClickListener) {
        this.menuItemClickListener = menuItemClickListener;
    }

    public void setEmptyItemClickListener(View.OnClickListener emptyItemClickListener) {
        this.emptyItemClickListener = emptyItemClickListener;
    }
}
