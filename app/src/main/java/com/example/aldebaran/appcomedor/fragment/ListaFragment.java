package com.example.aldebaran.appcomedor.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.adapter.TicketMenuAdapter;
import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListaFragment extends Fragment {

    private RecyclerView listaRecyclerView;
    private TicketMenuAdapter adapter;
    private ArrayList<TicketMenu> listaTicketMenu;
    private String token;

    private int option = 0;

    public static final int TICKET = 0;
    public static final int MENU = 1;

    public ListaFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_lista, container, false);
    }

    public ListaFragment setOption(int option){
        this.option = option;
        return this;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        token = sp.getString("token","");


        listaRecyclerView = (RecyclerView)view.findViewById(R.id.listaRecyclerView);

        listaTicketMenu = new ArrayList<TicketMenu>();
        adapter = new TicketMenuAdapter(view.getContext(),listaTicketMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);
        listaRecyclerView.setLayoutManager(linearLayoutManager);
        listaRecyclerView.setItemAnimator(new DefaultItemAnimator());
        listaRecyclerView.setAdapter(adapter);

        switch(option){
            case TICKET:
                getActivity().setTitle("Listado de Tickets");
                obtenerTickets();
                break;
            case MENU:
                getActivity().setTitle("Listado de Menus");
                obtenerMenus();
                break;
        }

    }

    void obtenerMenus(){
        Call<RespuestaListaAPI> call = RestClient.getClient().menuLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                RespuestaListaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Type listType = new TypeToken<List<Menu>>() {}.getType();
                    List<com.example.aldebaran.appcomedor.modelos.Menu> lista= gson.fromJson(respuesta.getSalida(),listType);
                    listaTicketMenu.clear();
                    if(lista.size()>0) {
                        for(Menu menu: lista){
                            menu.setTipo(TicketMenu.MENU_TYPE);
                            listaTicketMenu.add(menu);
                        }
                    } else {
                        TicketMenu ticket = new TicketMenu();
                        ticket.setInfo("No hay menus nuevos");
                        listaTicketMenu.add(ticket);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                    if (response.code() == 401) {
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaListaAPI> call, Throwable t) {
            }
        });
    }

    public void obtenerTickets(){
        Call<RespuestaListaAPI> call = RestClient.getClient().ticketLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                RespuestaListaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Type listType = new TypeToken<List<Ticket>>() {}.getType();
                    List<Ticket> lista = gson.fromJson(respuesta.getSalida(),listType);
                    if(lista.size()>0) {
                        for (Ticket ticket : lista){
                            ticket.setTipo(TicketMenu.TICKET_TYPE);
                            listaTicketMenu.add(ticket);
                        }
                    } else {
                        TicketMenu ticket = new TicketMenu();
                        ticket.setInfo("No tiene tickets");
                        listaTicketMenu.add(ticket);
                    }
                    adapter.notifyDataSetChanged();
                } else {
                }
            }

            @Override
            public void onFailure(Call<RespuestaListaAPI> call, Throwable t) {
            }
        });
    }


}
