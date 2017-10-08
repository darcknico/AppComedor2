package com.example.aldebaran.appcomedor.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.adapter.TicketMenuAdapter;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;
import com.example.aldebaran.appcomedor.modelos.Usuario;
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
public class HomeFragment extends Fragment {

    private RelativeLayout homeLayout;
    private TextView homeNombreUsuario,homeTicketUsuario,homeSaldoUsuario,homeDocumentoUsuario,homeEstadoUsuario;
    private RecyclerView homeRecyclerView;
    private TicketMenuAdapter adapter;
    private ArrayList<TicketMenu> listaTicketMenu;

    private String token;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        token = sp.getString("token","");

        homeLayout = (RelativeLayout) view.findViewById(R.id.homeLayout);
        homeNombreUsuario = (TextView) view.findViewById(R.id.homeNombreUsuario);
        homeTicketUsuario = (TextView) view.findViewById(R.id.homeTicketUsuario);
        homeSaldoUsuario = (TextView) view.findViewById(R.id.homeSaldoUsuario);
        homeDocumentoUsuario = (TextView) view.findViewById(R.id.homeDocumentoUsuario);
        homeEstadoUsuario = (TextView) view.findViewById(R.id.homeEstadoUsuario);
        homeRecyclerView = (RecyclerView) view.findViewById(R.id.homeRecyclerView);
        homeEstadoUsuario = (TextView) view.findViewById(R.id.homeEstadoUsuario);

        actualizarUsuario();

        listaTicketMenu = new ArrayList<TicketMenu>();
        adapter = new TicketMenuAdapter(view.getContext(),listaTicketMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(view.getContext(), OrientationHelper.VERTICAL, false);
        homeRecyclerView.setLayoutManager(linearLayoutManager);
        homeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        homeRecyclerView.setAdapter(adapter);

        obtenerMenus();
        obtenerTickets();
        getActivity().setTitle("Applicacion del Comedor");
    }

    public void actualizarUsuario(){
        Call<RespuestaAPI> loginCall = RestClient.getClient().usuarioObtener(token);
        loginCall.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    if(!response.body().getSalida().isJsonArray()) {
                        Usuario item = gson.fromJson(response.body().getSalida(), Usuario.class);
                        homeNombreUsuario.setText(item.getNombre() + " " + item.getApellido());
                        homeDocumentoUsuario.setText(item.getDni());
                        homeEstadoUsuario.setText(item.getCondicion());
                        homeSaldoUsuario.setText(item.getSaldo());
                        homeTicketUsuario.setText(item.getTickets());
                    } else {
                        homeSaldoUsuario.setText("ADMINISTRADOR");
                    }
                } else {
                    if (response.code() == 401) {
                        Snackbar.make(homeLayout, "Perdio la session", Snackbar.LENGTH_LONG)
                                .show();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {

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
                        Ticket ticket = lista.get(0);
                        ticket.setTipo(TicketMenu.TICKET_TYPE);
                        listaTicketMenu.add(ticket);
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

    void obtenerMenus(){
        Call<RespuestaListaAPI> call = RestClient.getClient().menuLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                RespuestaListaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Type listType = new TypeToken<List<com.example.aldebaran.appcomedor.modelos.Menu>>() {}.getType();
                    List<com.example.aldebaran.appcomedor.modelos.Menu> lista= gson.fromJson(respuesta.getSalida(),listType);
                    if(lista.size()>0) {
                        com.example.aldebaran.appcomedor.modelos.Menu menu = lista.get(0);
                        menu.setTipo(TicketMenu.MENU_TYPE);
                        listaTicketMenu.add(menu);
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
}
