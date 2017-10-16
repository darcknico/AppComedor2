package com.example.aldebaran.appcomedor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaErrorApi;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Transaccion;
import com.google.gson.Gson;
import com.mercadopago.constants.PaymentMethods;
import com.mercadopago.constants.PaymentTypes;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPagoCheckout;
import com.mercadopago.model.Item;
import com.mercadopago.model.PaymentData;
import com.mercadopago.preferences.CheckoutPreference;
import com.mercadopago.util.JsonUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class crearTransaccion extends AppCompatActivity {

    TextView fecha;
    Spinner concepto;
    EditText importe;
    Button btnConfirmar,btnCancelar;

    private String token;
    private String[] opcionesConcepto;
    private String conceptoSeleccionado;

    private String publicKey="TEST-9d1721be-7370-4a7b-a17a-630a92674c52";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_transaccion);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_creartransacciones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("Creando transacción");


        opcionesConcepto = new String[]{"Cargar saldo"};
        fecha = (TextView) findViewById(R.id.txt_fecha_creartransaccion);
        concepto = (Spinner) findViewById(R.id.spinner_creartransaccion);
        importe = (EditText) findViewById(R.id.etxt_importe_creartransaccion);
        btnConfirmar = (Button) findViewById(R.id.btn_confirmar_creartransaccion);
        btnCancelar = (Button) findViewById(R.id.btn_cancelar_creartransaccion);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        ArrayAdapter adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_dropdown_item,opcionesConcepto);
        concepto.setAdapter(adapter);
        concepto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                conceptoSeleccionado = (String) parent.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                //no hacemos nada
            }
        });

        btnConfirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String imp = importe.getText().toString();
                List<String> excluirTiposPagos = new ArrayList<String>();
                excluirTiposPagos.add(PaymentTypes.CREDIT_CARD);
                excluirTiposPagos.add(PaymentTypes.BANK_TRANSFER);
                excluirTiposPagos.add(PaymentTypes.ATM);
                excluirTiposPagos.add(PaymentTypes.PREPAID_CARD);

                if(!TextUtils.isEmpty(imp)) {
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
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void crear_transaccion(Transaccion body){
        String simporte = importe.getText().toString();
        Call<RespuestaAPI> registerCall = RestClient.getClient().crearTransaccion(token,body);
        registerCall.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                RespuestaAPI respuesta = null;
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Toast.makeText(getApplicationContext(),"Transacción Exitosa",Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Gson gson = new Gson();
                    try {
                        RespuestaErrorApi resp = gson.fromJson(response.errorBody().string(), RespuestaErrorApi.class);
                        showLoginError(resp.getResultado());
                        if (response.code() == 400) {
                            Toast.makeText(getApplicationContext(),"Se produjo un error en la transaccion",Toast.LENGTH_LONG).show();
                        } else if (resp.getSalida()!=null) {
                            showLoginError(resp.getSalida().getAsString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {
                showLoginError(t.getMessage());
            }
        });
    }
    public void showLoginError(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private void startMercadoPagoCheckout(CheckoutPreference checkoutPreference) {
        new MercadoPagoCheckout.Builder()
                .setActivity(crearTransaccion.this)
                .setPublicKey(publicKey)
                .setCheckoutPreference(checkoutPreference)
                .startForPaymentData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MercadoPagoCheckout.CHECKOUT_REQUEST_CODE) {
            if (resultCode == MercadoPagoCheckout.PAYMENT_DATA_RESULT_CODE) {
                PaymentData paymentData = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentData"), PaymentData.class);
                String paymentMethodId = paymentData.getPaymentMethod().getId();
                Long cardIssuerId = paymentData.getIssuer() == null ? 0 : paymentData.getIssuer().getId();
                Integer installment = paymentData.getPayerCost() == null ? 0 : paymentData.getPayerCost().getInstallments();
                String cardToken = paymentData.getToken() == null ? " " : paymentData.getToken().getId();
                Long campaignId = paymentData.getDiscount() == null ? 0 : paymentData.getDiscount().getId();
                Transaccion transaccion = new Transaccion();
                transaccion.setMonto(Double.parseDouble(importe.getText().toString()));
                transaccion.setConcepto(conceptoSeleccionado);
                transaccion.setFecha_acreditacion(fecha.getText().toString());
                transaccion.setPaymentMethodId(paymentMethodId);
                transaccion.setCardIssuerId(String.valueOf(cardIssuerId));
                transaccion.setInstallment(String.valueOf(installment));
                transaccion.setCardToken(cardToken);
                transaccion.setCampaignId(String.valueOf(campaignId));
                crear_transaccion(transaccion);
            } else if (resultCode == RESULT_CANCELED) {
                if (data != null && data.getStringExtra("mercadoPagoError") != null) {
                    //Resolve error in checkout
                } else {
                    //Resolve canceled checkout
                }
            }
        }
    }


}
