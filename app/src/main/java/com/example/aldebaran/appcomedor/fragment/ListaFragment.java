package com.example.aldebaran.appcomedor.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.transition.TransitionInflater;
import android.transition.TransitionSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;

import com.example.aldebaran.appcomedor.MainActivity;
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

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
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
    private int id;

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
        View view = inflater.inflate(R.layout.fragment_lista, container, false);
        listaRecyclerView = (RecyclerView)view.findViewById(R.id.listaRecyclerView);
        listaTicketMenu = new ArrayList<>();
        TicketMenu item = new TicketMenu();
        item.setInfo("Recuperando informacion");
        listaTicketMenu.add(item);
        adapter = new TicketMenuAdapter(view.getContext(),listaTicketMenu);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        listaRecyclerView.setLayoutManager(linearLayoutManager);
        SlideInDownAnimator animator = new SlideInDownAnimator(new OvershootInterpolator(1f));
        listaRecyclerView.setItemAnimator(animator);
        listaRecyclerView.setAdapter(adapter);


        return view;
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

        listaRecyclerView.getItemAnimator().setAddDuration(750);
        listaRecyclerView.getItemAnimator().setRemoveDuration(750);
        listaRecyclerView.getItemAnimator().setMoveDuration(750);
        listaRecyclerView.getItemAnimator().setChangeDuration(750);

        Handler mDelayedTransactionHandler = new Handler();
        mDelayedTransactionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
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
        }, MainActivity.MOVE_DEFAULT_TIME + MainActivity.FADE_DEFAULT_TIME);
    }

    void obtenerMenus(){
        Call<RespuestaListaAPI> call = RestClient.getClient().menuLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaListaAPI respuesta = response.body();
                    Type listType = new TypeToken<List<Menu>>() {}.getType();
                    List<Menu> lista= gson.fromJson(respuesta.getSalida(),listType);
                    adapter.removeAt(0);
                    if(lista.size()>0) {
                        int i = 0;
                        for(Menu menu: lista){
                            menu.setTipo(TicketMenu.MENU_TYPE);
                            id = menu.getId();
                            menu.setListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("id",id);
                                    MenuFragment fragment = new MenuFragment();
                                    fragment.setArguments(bundle);
                                    _LoadFragment(fragment);
                                }
                            });
                            adapter.add(menu,i++);
                        }
                    } else {
                        TicketMenu menu = new TicketMenu();
                        menu.setInfo("No hay menus nuevos");
                        adapter.add(menu,0);
                    }

                } else {
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
                    adapter.removeAt(0);
                    if(lista.size()>0) {
                        int i = 0;
                        for (Ticket ticket : lista){
                            ticket.setTipo(TicketMenu.TICKET_TYPE);
                            id = ticket.getId();
                            ticket.setListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Bundle bundle = new Bundle();
                                    bundle.putInt("id",id);
                                    TicketFragment fragment = new TicketFragment();
                                    fragment.setArguments(bundle);
                                    _LoadFragment(fragment);
                                }
                            });
                            adapter.add(ticket,i++);
                        }
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

    public void _LoadFragment(Fragment _frag){
        TransitionSet enterTransitionSet = new TransitionSet();
        enterTransitionSet.addTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.slide_top));
        enterTransitionSet.setDuration(MainActivity.MOVE_DEFAULT_TIME);
        enterTransitionSet.setStartDelay(MainActivity.FADE_DEFAULT_TIME);
        _frag.setSharedElementEnterTransition(enterTransitionSet);

        Slide enterFade = new Slide();
        enterFade.setSlideEdge(Gravity.TOP);
        enterFade.setStartDelay(MainActivity.MOVE_DEFAULT_TIME + MainActivity.FADE_DEFAULT_TIME);
        enterFade.setDuration(MainActivity.FADE_DEFAULT_TIME);
        _frag.setEnterTransition(enterFade);

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.listaFrame, _frag)
                .commitAllowingStateLoss();

        /*
        getChildFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.mpsdk_slide_up_to_down_in,R.anim.mpsdk_slide_down_to_top_out)
                .replace(R.id.listaFrame, _frag)
                .addToBackStack(null)
                .commit();
                */
    }
}
