package com.example.aldebaran.appcomedor;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.apirest.CrearTransaccionBody;
import com.example.aldebaran.appcomedor.apirest.RegisterBody;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mercadopago.constants.Sites;
import com.mercadopago.core.MercadoPago;
import com.mercadopago.core.MerchantServer;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.model.ApiException;
import com.mercadopago.model.Issuer;
import com.mercadopago.model.Item;
import com.mercadopago.model.MerchantPayment;
import com.mercadopago.model.PayerCost;
import com.mercadopago.model.Payment;
import com.mercadopago.model.PaymentMethod;
import com.mercadopago.model.Token;
import com.mercadopago.util.JsonUtil;

import java.io.IOException;
import java.math.BigDecimal;

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
                onPaymentVaultButtonClicked(v);

            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }

    public void crear_transaccion(){
        String simporte = importe.getText().toString();
        Call<RespuestaAPI> registerCall = RestClient.getClient().crearTransaccion(token,new CrearTransaccionBody(conceptoSeleccionado,simporte));
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
                        respuesta = gson.fromJson(response.errorBody().string(),RespuestaAPI.class);
                        showLoginError(respuesta.getResultado());
                        if (response.code() == 400) {
                            JsonObject salida = respuesta.getSalida();
                            Toast.makeText(getApplicationContext(),"Se produjo un error en la transaccion",Toast.LENGTH_LONG).show();
                        } else if (respuesta.getSalida()!=null) {
                            showLoginError(respuesta.getSalida().getAsString());
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

    public void onPaymentVaultButtonClicked(View view) {
        String simporte = importe.getText().toString();
        new MercadoPago.StartActivityBuilder()
                .setActivity(this)
                .setPublicKey("TEST-9d1721be-7370-4a7b-a17a-630a92674c52")
                .setAmount(BigDecimal.valueOf(Double.valueOf(simporte)))
                .setSite(Sites.ARGENTINA)
                .startPaymentVaultActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == MercadoPago.PAYMENT_VAULT_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                PaymentMethod paymentMethod = JsonUtil.getInstance().fromJson(data.getStringExtra("paymentMethod"), PaymentMethod.class);
                Issuer issuer = JsonUtil.getInstance().fromJson(data.getStringExtra("issuer"), Issuer.class);
                Token token = JsonUtil.getInstance().fromJson(data.getStringExtra("token"), Token.class);
                PayerCost payerCost = JsonUtil.getInstance().fromJson(data.getStringExtra("payerCost"), PayerCost.class);
                crear_transaccion();
                //createPayment(paymentMethod., issuer, payerCost, token);
            } else {
                if ((data != null) && (data.hasExtra("mpException"))) {
                    MPException exception = JsonUtil.getInstance()
                            .fromJson(data.getStringExtra("mpException"), MPException.class);
                }
            }
        }
    }

    /*
    public void createPayment(final Activity activity, final PaymentMethod paymentMethod,
                              Issuer issuer, PayerCost payerCost, Token token) {

        if (paymentMethod != null) {

            // Crear el item que se está cobrando
            Item item = new Item("1", 1,new BigDecimal(10));

            // Obtener el ID del medio de pago
            String paymentMethodId = paymentMethod.getId();

            // Obtener el ID del banco que emite la tarjeta
            Long cardIssuerId = issuer.getId();

            // Obtener la cantidad de cuotas
            Integer installments = payerCost.getInstallments();

            // Obtener el ID del token
            String tokenId = token.getId();

            MerchantPayment payment = new MerchantPayment(item, installments,
                    cardIssuerId, tokenId, paymentMethodId, DUMMY_CAMPAIGN_ID, "TEST-1725237050630439-080809-bec7b365a12ef97016b86d8c13f67c8b__LC_LD__-188740775\n");

            // Enviar los datos a tu servidor
            MerchantServer.createPayment(activity, "http://proyectosinformaticos.esy.es/apirest.slim/public/", "transaccion",
                    payment, new Callback<Payment>() {
                        @Override
                        public void success(Payment payment) {
                            // Ya se realizó el pago.
                            // Inicio de componente de resultado.
                            new MercadoPago.StartActivityBuilder()
                                    .setPublicKey("TEST-9d1721be-7370-4a7b-a17a-630a92674c52")
                                    .setActivity(crearTransaccion.this)
                                    .setPayment(payment)
                                    .setPaymentMethod(paymentMethod)
                                    .startPaymentResultActivity();
                        }
                    }

            @Override
            public void failure(ApiException apiException) {

                // Ups, ha ocurrido un error.

            }
        });
    } else {
        Toast.makeText(crearTransaccion.this, "Invalid payment method", Toast.LENGTH_LONG).show();
    }
}   */

}
