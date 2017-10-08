package com.example.aldebaran.appcomedor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    //content main
    private CoordinatorLayout coordinatorLayout;
    private TextView homeNombreUsuario,homeTicketUsuario,homeSaldoUsuario,homeDocumentoUsuario,homeEstadoUsuario;
    private RecyclerView homeRecyclerView;
    private TicketMenuAdapter adapter;
    private ArrayList<TicketMenu> listaTicketMenu;

    //nav header
    private TextView mainNombreUsuario;
    private ImageView mainImageUsuario;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        drawer.closeDrawer(GravityCompat.START);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //inicio de sesion
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        //content main
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        homeNombreUsuario = (TextView) findViewById(R.id.homeNombreUsuario);
        homeTicketUsuario = (TextView) findViewById(R.id.homeTicketUsuario);
        homeSaldoUsuario = (TextView) findViewById(R.id.homeSaldoUsuario);
        homeDocumentoUsuario = (TextView) findViewById(R.id.homeDocumentoUsuario);
        homeEstadoUsuario = (TextView) findViewById(R.id.homeEstadoUsuario);
        homeRecyclerView = (RecyclerView) findViewById(R.id.homeRecyclerView);
        //nav header
        mainNombreUsuario = (TextView) headerView.findViewById(R.id.mainNombreUsuario);
        mainImageUsuario = (ImageView) headerView.findViewById(R.id.mainImageUsuario);

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
                        homeNombreUsuario.setText(item.getNombre() + " " + item.getApellido());
                        homeDocumentoUsuario.setText(item.getDni());
                        homeEstadoUsuario.setText(item.getCondicion());
                        homeSaldoUsuario.setText(item.getSaldo());
                        homeTicketUsuario.setText(item.getTickets());

                        mainNombreUsuario.setText(item.getNombre() + " " + item.getApellido());
                    } else {
                        homeNombreUsuario.setText("ADMINISTRADOR");
                    }
                } else {
                    if (response.code() == 401) {
                        Snackbar.make(coordinatorLayout, "Perdio la session", Snackbar.LENGTH_LONG)
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_refrescar) {
            // Handle the camera action
        } else if (id == R.id.nav_logout) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sp.edit().clear().apply();
            finish();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_close) {


        }
        return true;
    }
}
