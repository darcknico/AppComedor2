package com.example.aldebaran.appcomedor.fragment;


import android.content.Intent;
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
import android.text.Editable;
import android.text.TextUtils;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.MainActivity;
import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.adapter.TransaccionAdapter;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaErrorApi;
import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Empty;
import com.example.aldebaran.appcomedor.modelos.Transaccion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.common.StringUtils;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.model.Item;
import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.util.JsonUtil;

import java.io.IOException;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import jp.wasabeef.recyclerview.animators.SlideInDownAnimator;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;

/**
 * A simple {@link Fragment} subclass.
 */
public class TransaccionFragment extends Fragment {


    private TextView transaccionMontoTextView;
    private Button transaccionPagarButton;
    private String token;
    private RecyclerView transaccionRecyclerView;
    private ArrayList<Empty> lista;
    private TransaccionAdapter adapter;
    private LinearLayout transaccionLayout;

    private String publicKey="TEST-9d1721be-7370-4a7b-a17a-630a92674c52";

    public TransaccionFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_transaccion, container, false);
        transaccionRecyclerView = (RecyclerView) view.findViewById(R.id.transaccionRecyclerView);
        lista = new ArrayList<>();
        adapter = new TransaccionAdapter(view.getContext(),lista);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        transaccionRecyclerView.setLayoutManager(linearLayoutManager);
        SlideInDownAnimator animator = new SlideInDownAnimator(new OvershootInterpolator(1f));
        transaccionRecyclerView.setItemAnimator(animator);
        transaccionRecyclerView.setAdapter(adapter);
        transaccionRecyclerView.getItemAnimator().setAddDuration(750);
        transaccionRecyclerView.getItemAnimator().setRemoveDuration(750);
        transaccionRecyclerView.getItemAnimator().setMoveDuration(750);
        transaccionRecyclerView.getItemAnimator().setChangeDuration(750);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        transaccionMontoTextView = (TextView) view.findViewById(R.id.transaccionMontoTextView);
        transaccionPagarButton = (Button) view.findViewById(R.id.transaccionPagarButton);
        transaccionLayout = (LinearLayout) view.findViewById(R.id.transaccionLayout);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(view.getContext());
        token = sp.getString("token","");

        getActivity().setTitle("Listado de Transacciones");

        transaccionPagarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imp = transaccionMontoTextView.getText().toString();
                if(!TextUtils.isEmpty(imp)) {
                    setupMercadoPago(imp);
                }
            }
        });

        transaccionMontoTextView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String imp = transaccionMontoTextView.getText().toString();
                    if(!TextUtils.isEmpty(imp)){
                        setupMercadoPago(imp);
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });

        Handler mDelayedTransactionHandler = new Handler();
        mDelayedTransactionHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Empty item = new Empty();
                item.setInfo("Recuperando informacion");
                adapter.add(item,0);
                actualizar();

            }
        }, MainActivity.MOVE_DEFAULT_TIME + MainActivity.FADE_DEFAULT_TIME);

    }

    void actualizar(){
        Call<RespuestaListaAPI> call = RestClient.getClient().transaccionLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaListaAPI respuesta = response.body();
                    Type listType = new TypeToken<List<Transaccion>>() {}.getType();
                    List<Transaccion> listaTransaccions = gson.fromJson(respuesta.getSalida(),listType);
                    adapter.removeAt(0);
                    if(listaTransaccions.size()>0) {
                        int i = 0;
                        for(Transaccion transaccion: listaTransaccions){
                            transaccion.setTipo(Empty.TRANSACCION_TYPE);
                            adapter.add(transaccion,i++);
                        }
                    } else {
                        Empty empty = new Empty();
                        empty.setInfo("No tiene Transacciones");
                        adapter.add(empty,0);
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
            public void onFailure(Call<RespuestaListaAPI> call, Throwable t) {
            }
        });
    }

    public void crear_transaccion(Transaccion body){
        Call<RespuestaAPI> registerCall = RestClient.getClient().crearTransaccion(token,body);
        registerCall.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    RespuestaAPI respuesta = response.body();
                    snackbar(respuesta.getResultado());
                    Type itemType = new TypeToken<Transaccion>() {}.getType();
                    Transaccion transaccion = gson.fromJson(respuesta.getSalida(),itemType);
                    transaccion.setTipo(Empty.TRANSACCION_TYPE);
                    if(lista.get(0).getTipo()==Empty.EMPTY_TYPE){
                        adapter.removeAt(0);
                    }
                    adapter.add(transaccion);
                } else {
                    try {
                        RespuestaErrorApi respuesta = gson.fromJson(response.errorBody().string(),RespuestaErrorApi.class);
                        Snackbar.make(transaccionLayout,respuesta.getSalida().toString(),Snackbar.LENGTH_LONG).show();
                        if (response.code() != 401) {
                            //snackbar(respuesta.getResultado());
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

    private void setupMercadoPago(String imp){
        List<String> excluirTiposPagos = new ArrayList<String>();
        excluirTiposPagos.add(PaymentTypes.CREDIT_CARD);
        excluirTiposPagos.add(PaymentTypes.BANK_TRANSFER);
        excluirTiposPagos.add(PaymentTypes.ATM);
        excluirTiposPagos.add(PaymentTypes.PREPAID_CARD);
        GregorianCalendar gc = new GregorianCalendar();
        gc.add(Calendar.DATE, 1);
        CheckoutPreference checkoutPreference = new CheckoutPreference.Builder()
                .addItem(new Item("Carga Comedor", new BigDecimal(imp)))
                .setSite(Sites.ARGENTINA)
                .addExcludedPaymentTypes(excluirTiposPagos)
                .addExcludedPaymentMethod(PaymentMethods.ARGENTINA.VISA) //Exclude specific payment methods
                .setMaxInstallments(1) //Limit the amount of installments
                .setExpirationDate(gc.getTime())
                .setActiveFrom(new Date())
                .build();
        startMercadoPagoCheckout(checkoutPreference);
    }
    private void startMercadoPagoCheckout(CheckoutPreference checkoutPreference) {
        new MercadoPagoCheckout.Builder()
                .setActivity(getActivity())
                .setPublicKey(publicKey)
                .setCheckoutPreference(checkoutPreference)
                .startForPaymentData();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);
                String paymentMethodId = paymentData.getPaymentMethod().getId();
                Long cardIssuerId = paymentData.getIssuer() == null ? 0 : paymentData.getIssuer().getId();
                Integer installment = paymentData.getPayerCost() == null ? 0 : paymentData.getPayerCost().getInstallments();
                String cardToken = paymentData.getToken() == null ? " " : paymentData.getToken().getId();
                Long campaignId = paymentData.getDiscount() == null ? 0 : paymentData.getDiscount().getId();
                Transaccion transaccion = new Transaccion();
                transaccion.setMonto(Double.parseDouble(transaccionMontoTextView.getText().toString()));
                transaccion.setConcepto("saldo");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = simpleDateFormat.format(Calendar.getInstance().getTime());
                transaccion.setFecha_acreditacion(fecha);
                transaccion.setPaymentMethodId(paymentMethodId);
                transaccion.setCardIssuerId(String.valueOf(cardIssuerId));
                transaccion.setInstallment(String.valueOf(installment));
                transaccion.setCardToken(cardToken);
                transaccion.setCampaignId(String.valueOf(campaignId));
                crear_transaccion(transaccion);
                transaccionMontoTextView.clearFocus();
                transaccionMontoTextView.setText("");
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }


    public void snackbar(String message){
        Snackbar.make(transaccionLayout,message,Snackbar.LENGTH_SHORT).show();
    }



}
