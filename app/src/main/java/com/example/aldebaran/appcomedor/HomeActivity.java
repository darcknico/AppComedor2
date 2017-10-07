package com.example.aldebaran.appcomedor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.adapter.TicketMenuAdapter;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaToken;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;
import com.example.aldebaran.appcomedor.modelos.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends AppCompatActivity {

    private ConstraintLayout homeLayout;
    private TextView homeNombreUsuario,homeTicketUsuario,homeSaldoUsuario,homeDocumentoUsuario,homeEstadoUsuario;
    private RecyclerView homeRecyclerView;
    private String token;
    private TicketMenuAdapter adapter;
    private ArrayList<TicketMenu> listaTicketMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        homeLayout = (ConstraintLayout) findViewById(R.id.homeLayout);
        homeNombreUsuario = (TextView) findViewById(R.id.homeNombreUsuario);
        homeTicketUsuario = (TextView) findViewById(R.id.homeTicketUsuario);
        homeSaldoUsuario = (TextView) findViewById(R.id.homeSaldoUsuario);
        homeDocumentoUsuario = (TextView) findViewById(R.id.homeDocumentoUsuario);
        homeEstadoUsuario = (TextView) findViewById(R.id.homeEstadoUsuario);
        homeRecyclerView = (RecyclerView) findViewById(R.id.homeRecyclerView);

        actualizarUsuario();

        listaTicketMenu = new ArrayList<TicketMenu>();
        adapter = new TicketMenuAdapter(this,listaTicketMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, OrientationHelper.VERTICAL, false);
        homeRecyclerView.setLayoutManager(linearLayoutManager);
        homeRecyclerView.setItemAnimator(new DefaultItemAnimator());
        homeRecyclerView.setAdapter(adapter);

        obtenerMenus();
        obtenerTickets();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        actualizarUsuario();
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
                        homeSaldoUsuario.setText(item.getNombre() + " " + item.getApellido());
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
                    Type listType = new TypeToken<List<Menu>>() {}.getType();
                    List<Menu> lista= gson.fromJson(respuesta.getSalida(),listType);
                    if(lista.size()>0) {
                        Menu menu = lista.get(0);
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
