package com.example.aldebaran.appcomedor;

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

import java.io.IOException;

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
                crear_transaccion();
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
}
