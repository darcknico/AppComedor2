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
import android.widget.ListView;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.modelos.Empty;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;
import com.example.aldebaran.appcomedor.modelos.Transaccion;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by karen on 11/10/2017.
 */

public class TransaccionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private ArrayList<Empty> lista;

    public TransaccionAdapter(Context mContext, ArrayList<Empty> lista) {
        this.mContext = mContext;
        this.lista = lista;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case Empty.TRANSACCION_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ticket_card, parent, false);
                return new TransaccionAdapter.TransaccionViewHolder(view);
            case Empty.EMPTY_TYPE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.empty_card, parent, false);
                return new TransaccionAdapter.EmptyViewHolder(view);
        }
        return null;
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
    private class TransaccionViewHolder extends RecyclerView.ViewHolder{

        private ImageView transaccionImageCard;
        private TextView transaccionMontoCard;
        private TextView transaccionFechaAprobadoCard;
        private TextView transaccionFechaCreadoCard;

        private TransaccionViewHolder(View view){
            super(view);
            transaccionImageCard = (ImageView) view.findViewById(R.id.transaccionImageCard);
            transaccionMontoCard = (TextView) view.findViewById(R.id.transaccionMontoCard);
            transaccionFechaAprobadoCard = (TextView) view.findViewById(R.id.transaccionFechaAprobadoCard);
            transaccionFechaCreadoCard = (TextView) view.findViewById(R.id.transaccionFechaCreadoCard);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Empty object = lista.get(position);
        if (object != null) {
            switch (object.getTipo()) {
                case Empty.TRANSACCION_TYPE:
                    Transaccion transaccion = (Transaccion)object;
                    ((TransaccionViewHolder) holder).transaccionMontoCard.setText(DecimalFormat.getCurrencyInstance().format(transaccion.getMonto()));
                    ((TransaccionViewHolder) holder).transaccionFechaAprobadoCard.setText(parseFechaView(transaccion.getFecha_acreditacion()));
                    break;
                case Empty.EMPTY_TYPE:
                    ((TransaccionAdapter.EmptyViewHolder) holder).emptyInfoCard.setText(object.getInfo());
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

    public String parseFechaView(String fecha){
        String resultado = "";
        SimpleDateFormat simpleDateFormatBase=new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat simpleDateFormatView=new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = simpleDateFormatBase.parse(fecha);
            return simpleDateFormatView.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return resultado;
    }

    public void removeAt(int position) {
        lista.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, lista.size());
    }

    public void add(Empty item, int position) {
        lista.add(position, item);
        notifyItemInserted(position);
    }

}
