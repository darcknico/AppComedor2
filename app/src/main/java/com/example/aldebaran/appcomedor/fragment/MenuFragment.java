package com.example.aldebaran.appcomedor.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.MainActivity;
import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaErrorApi;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Menu;
import com.example.aldebaran.appcomedor.utils.Singleton;
import com.google.gson.Gson;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MenuFragment extends Fragment {
    private String token;
    private int id;
    private Button menuCloseButton;
    private TextView menuFechaTextView;
    private TextView menuPrecioTextView;
    private Button menuComprarButton;
    private LinearLayout frameLayout;

    private View v;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_menu, container, false);
        id = this.getArguments().getInt("id");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(v.getContext());
        token = sp.getString("token","");

        frameLayout = (LinearLayout) v.findViewById(R.id.menuLayout);
        menuCloseButton = (Button) v.findViewById(R.id.menuCloseButton);
        menuFechaTextView = (TextView) v.findViewById(R.id.menuFechaTextView);
        menuPrecioTextView = (TextView) v.findViewById(R.id.menuPrecioTextView);
        menuComprarButton = (Button) v.findViewById(R.id.menuComprarButton);
        menuCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getFragmentManager()
                        .beginTransaction()
                        .setCustomAnimations(R.anim.mpsdk_slide_up_to_down_in,R.anim.mpsdk_slide_down_to_top_out)
                        .remove(MenuFragment.this)
                        .commitAllowingStateLoss();
            }
        });

        menuComprarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprar_menu();
            }
        });
        obtener_menu();
        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        Animation animation = super.onCreateAnimation(transit, enter, nextAnim);
        if (animation == null && nextAnim != 0) {
            animation = AnimationUtils.loadAnimation(getActivity(), nextAnim);
            animation.setDuration(MainActivity.FADE_DEFAULT_TIME + MainActivity.MOVE_DEFAULT_TIME);
        }

        if (animation != null) {
            getView().setLayerType(View.LAYER_TYPE_HARDWARE, null);

            animation.setAnimationListener(new Animation.AnimationListener() {

                @Override
                public void onAnimationStart(Animation animation) {

                }

                public void onAnimationEnd(Animation animation) {
                    getView().setLayerType(View.LAYER_TYPE_NONE, null);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }

            });
        }

        return animation;
    }

    public void obtener_menu(){
        Call<RespuestaAPI> call = RestClient.getClient().menuObtener(token,id);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaAPI respuesta = response.body();
                    Menu item = gson.fromJson(respuesta.getSalida(),Menu.class);
                    menuFechaTextView.setText(parseFechaView(item.getFecha()));
                    menuPrecioTextView.setText("Precio: "+DecimalFormat.getCurrencyInstance().format(item.getPrecio()));

                    try {
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        Date today = Calendar.getInstance().getTime();
                        Date fecha = simpleDateFormat.parse(item.getFecha());
                        if(fecha.before(today)){
                            menuComprarButton.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        RespuestaErrorApi respuesta = gson.fromJson(response.errorBody().string(),RespuestaErrorApi.class);
                        if (response.code() != 401) {
                            snackbar(respuesta.getResultado());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {
            }
        });
    }

    public void comprar_menu(){
        Call<RespuestaAPI> call = RestClient.getClient().comprarTicket(token,id);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaAPI respuesta = response.body();
                    snackbar(respuesta.getResultado());
                    menuComprarButton.setVisibility(View.GONE);
                    Singleton.getInstance().getMainActivity().reloadFragment();
                } else {
                    try {
                        RespuestaErrorApi respuesta = gson.fromJson(response.errorBody().string(),RespuestaErrorApi.class);
                        if (response.code() == 403) {
                            snackbar(respuesta.getSalida().getAsString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {
            }
        });
    }

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
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
