package com.example.aldebaran.appcomedor.fragment;


import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.MainActivity;
import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaErrorApi;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Ticket;
import com.example.aldebaran.appcomedor.utils.Singleton;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class TicketFragment extends Fragment {


    private Button ticketBajaButton;
    private Button ticketCloseButton;
    private TextView ticketTitleText;
    private TextView ticketSubtitleText;
    private ImageView ticketCodeImage;
    private int id;
    private String token;
    private LinearLayout frameLayout;
    private TextView ticketTopTextImage;
    private TextView ticketBottomTextImage;

    public TicketFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_ticket, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        frameLayout = (LinearLayout) view.findViewById(R.id.ticketLayout);
        ticketBajaButton = (Button) view.findViewById(R.id.ticketBajaButton);
        ticketCloseButton = (Button) view.findViewById(R.id.ticketCloseButton);
        ticketTitleText = (TextView) view.findViewById(R.id.ticketTitleText);
        ticketSubtitleText = (TextView) view.findViewById(R.id.ticketSubtitleText);
        ticketTopTextImage = (TextView) view.findViewById(R.id.ticketTopTextImage);
        ticketBottomTextImage = (TextView) view.findViewById(R.id.ticketBottomTextImage);
        ticketCodeImage = (ImageView) view.findViewById(R.id.ticketCodeImage);

        id = this.getArguments().getInt("id");
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        token = sp.getString("token","");

        ticketCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Slide exitFade = new Slide();
                exitFade.setSlideEdge(Gravity.TOP);
                exitFade.setDuration(MainActivity.FADE_DEFAULT_TIME);
                TicketFragment.this.setExitTransition(exitFade);

                getFragmentManager()
                        .beginTransaction()
                        //.setCustomAnimations(R.anim.mpsdk_slide_up_to_down_in,R.anim.mpsdk_slide_down_to_top_out)
                        .remove(TicketFragment.this)
                        .commitAllowingStateLoss();
            }
        });

        obtener_ticket();
    }

    public void obtener_ticket(){

        Call<RespuestaAPI> call = RestClient.getClient().ticketObtener(token,id);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaAPI respuesta = response.body();
                    Ticket item = gson.fromJson(respuesta.getSalida(),Ticket.class);
                    ticketTitleText.setText(item.getCondicion().toUpperCase()+" - "+parseFechaView(item.getFecha()));
                    ticketSubtitleText.setText(item.getMenu().getDescripcion());
                    ticketBottomTextImage.setText(item.getCodigo());
                    if (TextUtils.equals(item.getCondicion(),"cancelado") || TextUtils.equals(item.getCondicion(),"usado")) {
                        ticketBajaButton.setVisibility(View.INVISIBLE);
                    } else {
                        ticketBajaButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eliminar_ticket();
                            }
                        });
                    }
                    MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                    try {
                        int width = (int) (ticketCodeImage.getWidth() * 0.95f);
                        BitMatrix bitMatrix = multiFormatWriter.encode(item.getCodigo(), BarcodeFormat.CODE_128,width,175);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        ticketCodeImage.setImageBitmap(bitmap);
                    } catch (WriterException e) {
                        e.printStackTrace();
                    }

                    try {
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd");
                        Date today = Calendar.getInstance().getTime();
                        Date fecha = simpleDateFormat.parse(item.getFecha());
                        if(fecha.before(today)){
                            ticketBajaButton.setVisibility(View.GONE);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        RespuestaErrorApi respuesta = gson.fromJson(response.errorBody().string(), RespuestaErrorApi.class);
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

    public void eliminar_ticket(){

        Call<RespuestaAPI> call = RestClient.getClient().ticketEliminar(token,id);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaAPI respuesta = response.body();
                    Ticket item = gson.fromJson(respuesta.getSalida(),Ticket.class);
                    ticketTitleText.setText(item.getCondicion().toUpperCase()+" - "+parseFechaView(item.getFecha()));
                    snackbar(respuesta.getResultado());
                    ticketBajaButton.setVisibility(View.GONE);
                    Singleton.getInstance().getMainActivity().reloadFragment();
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

    public void snackbar(String message){
        Snackbar.make(frameLayout,message,Snackbar.LENGTH_SHORT).show();
    }
}
