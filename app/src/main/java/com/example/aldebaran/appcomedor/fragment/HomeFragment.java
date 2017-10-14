package com.example.aldebaran.appcomedor.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.MainActivity;
import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.adapter.TicketMenuAdapter;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Empty;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.modelos.TicketMenu;
import com.example.aldebaran.appcomedor.modelos.Usuario;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
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
    private FrameLayout homeFrame;
    private TicketMenuAdapter adapter;
    private ArrayList<TicketMenu> listaTicketMenu;

    private View v;
    private String token;
    private int idMenu;
    private int idTicket;

    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_home, container, false);
        homeRecyclerView = (RecyclerView) v.findViewById(R.id.homeRecyclerView);
        listaTicketMenu = new ArrayList<>();
        adapter = new TicketMenuAdapter(v.getContext(),listaTicketMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        homeRecyclerView.setLayoutManager(linearLayoutManager);
        SlideInDownAnimator animator = new SlideInDownAnimator(new OvershootInterpolator(1f));
        homeRecyclerView.setItemAnimator(animator);
        homeRecyclerView.setAdapter(adapter);
        homeRecyclerView.getItemAnimator().setAddDuration(750);
        homeRecyclerView.getItemAnimator().setRemoveDuration(750);
        homeRecyclerView.getItemAnimator().setMoveDuration(750);
        homeRecyclerView.getItemAnimator().setChangeDuration(750);
        return v;
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
        homeFrame = (FrameLayout) view.findViewById(R.id.homeFrame);

        homeFrame.animate().setDuration(1000);

        getActivity().setTitle("Applicacion del Comedor");
        actualizarUsuario();

        Handler mDelayedTransactionHandler = new Handler();
        mDelayedTransactionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                obtenerTickets();
            }
        }, MainActivity.MOVE_DEFAULT_TIME + MainActivity.FADE_DEFAULT_TIME);
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
                obtenerMenus();
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaListaAPI respuesta = response.body();
                    Type listType = new TypeToken<List<Ticket>>() {}.getType();
                    List<Ticket> lista = gson.fromJson(respuesta.getSalida(),listType);
                    if(lista.size()>0) {
                        Ticket ticket = lista.get(0);
                        ticket.setTipo(TicketMenu.TICKET_TYPE);
                        idTicket = ticket.getId();
                        ticket.setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("id",idTicket);
                                TicketFragment fragment = new TicketFragment();
                                fragment.setArguments(bundle);
                                _LoadFragment(fragment);
                            }
                        });
                        adapter.add(ticket,0);
                    } else {
                        TicketMenu ticket = new TicketMenu();
                        ticket.setInfo("No tiene tickets");
                        adapter.add(ticket,0);
                    }
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
                        Menu menu = lista.get(0);
                        menu.setTipo(TicketMenu.MENU_TYPE);
                        idMenu = menu.getId();
                        menu.setListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Bundle bundle = new Bundle();
                                bundle.putInt("id",idMenu);
                                MenuFragment fragment = new MenuFragment();
                                fragment.setArguments(bundle);
                                _LoadFragment(fragment);
                            }
                        });
                        if(listaTicketMenu.size()==0) {
                            adapter.add(menu,0);
                        } else {
                            adapter.add(menu,1);
                        }
                    } else {
                        TicketMenu menu = new TicketMenu();
                        menu.setInfo("No hay menus nuevos");
                        if(listaTicketMenu.size()==0) {
                            adapter.add(menu,0);
                        } else {
                            adapter.add(menu,1);
                        }
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

    public void _LoadFragment(Fragment _frag){
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.mpsdk_slide_up_to_down_in,R.anim.mpsdk_slide_down_to_top_out)
                .replace(R.id.homeFrame, _frag)
                .addToBackStack(null)
                .commit();
    }
}
