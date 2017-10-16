package com.example.aldebaran.appcomedor.adapter;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by karen on 6/10/2017.
 */

public class TicketMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<TicketMenu> lista;

    private class TicketViewHolder extends RecyclerView.ViewHolder{
        private TextView ticketFechaCard;
        private TextView ticketEstadoCard;
        private ImageView ticketImageCard;

        private TicketViewHolder(View view){
            super(view);
            ticketFechaCard = (TextView) view.findViewById(R.id.ticketFechaCard);
            ticketEstadoCard = (TextView) view.findViewById(R.id.ticketEstadoCard);
            ticketImageCard = (ImageView) view.findViewById(R.id.ticketImageCard);
        }
    }

    private class MenuViewHolder extends RecyclerView.ViewHolder{
        private TextView menuFechaCard;
        private TextView menuPrecioCard;
        private ImageView menuImageCard;

        private MenuViewHolder(View view){
            super(view);
            menuFechaCard = (TextView) view.findViewById(R.id.menuFechaCard);
            menuPrecioCard = (TextView) view.findViewById(R.id.menuPrecioCard);
            menuImageCard = (ImageView) view.findViewById(R.id.menuImageCard);
        }
    }

    private class EmptyViewHolder extends RecyclerView.ViewHolder{
        private TextView emptyInfoCard;
        private ImageView emptyImageCard;

        private EmptyViewHolder(View view){
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
                    ((TicketViewHolder) holder).ticketFechaCard.setText(parseFechaView(ticket.getFecha()));
                    if(ticket.getListener() != null) {
                        ((TicketViewHolder) holder).ticketImageCard.setOnClickListener(ticket.getListener());
                    }
                    GradientDrawable shape =  new GradientDrawable();
                    shape.setCornerRadius( 8 );
                    switch (ticket.getCondicion()){
                        case "cancelado":
                            shape.setColor(ResourcesCompat.getColor(mContext.getResources(),R.color.md_red_400,null));
                            break;
                        case "usado":
                            shape.setColor(ResourcesCompat.getColor(mContext.getResources(),R.color.md_deep_orange_400,null));
                            break;
                        case "activo":
                            shape.setColor(ResourcesCompat.getColor(mContext.getResources(),R.color.md_green_400,null));
                            break;
                        case "vencido":
                            shape.setColor(ResourcesCompat.getColor(mContext.getResources(),R.color.md_deep_purple_400,null));
                            break;

                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        ((TicketViewHolder) holder).ticketEstadoCard.setBackground(shape);
                    }
                    break;
                case TicketMenu.MENU_TYPE:
                    Menu menu = (Menu)object;
                    ((MenuViewHolder) holder).menuPrecioCard.setText("Precio: "+DecimalFormat.getCurrencyInstance().format(menu.getPrecio()));
                    ((MenuViewHolder) holder).menuFechaCard.setText(parseFechaView(menu.getFecha()));
                    if(menu.getListener() != null) {
                        ((MenuViewHolder) holder).menuImageCard.setOnClickListener(menu.getListener());
                    }
                    break;
                case TicketMenu.EMPTY_TYPE:
                    ((EmptyViewHolder) holder).emptyInfoCard.setText(object.getInfo());
                    if(object.getListener()!=null) {
                        ((EmptyViewHolder) holder).emptyImageCard.setOnClickListener(object.getListener());
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

    public void add(TicketMenu item, int position) {
        lista.add(position, item);
        notifyItemInserted(position);
    }


    public String parseFechaView(String fecha){
        String resultado = "";
        SimpleDateFormat simpleDateFormatBase=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatView=new SimpleDateFormat("EEEE dd MMM");
        try {
            Date date = simpleDateFormatBase.parse(fecha);
            return simpleDateFormatView.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultado;
    }
}
