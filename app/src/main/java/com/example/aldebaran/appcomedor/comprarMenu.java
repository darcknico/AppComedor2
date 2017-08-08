package com.example.aldebaran.appcomedor;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.apirest.Menu;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RespuestaErrorApi;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.apirest.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class comprarMenu extends AppCompatActivity {
    private String token;
    private int idmenu;
    TextView fecha, descripcion, saldo, precio;
    private int tickets_restantes;
    Button btnComprar;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comprar_menu);

        toolbar = (Toolbar) findViewById(R.id.toolbar_comprarmenu);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("Comprar menús");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        idmenu = getIntent().getExtras().getInt("idmenu");
        fecha = (TextView) findViewById(R.id.txt_fecha_comprarmenu);
        descripcion = (TextView) findViewById(R.id.txt_descripcion_comprarmenu);
        saldo = (TextView) findViewById(R.id.txt_saldo_comprarmenu);
        precio = (TextView) findViewById(R.id.txt_precio_comprarmenu);
        btnComprar = (Button) findViewById(R.id.btn_comprarmenu);
        obtener_menu(idmenu);
        obtener_usuario();
        btnComprar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                comprar_menu();
            }
        });
    }

    public void obtener_menu(int id){

        Call<RespuestaAPI> call = RestClient.getClient().menuObtener(id);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                RespuestaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Menu item = gson.fromJson(respuesta.getSalida(),Menu.class);
                    if(item.getFecha()!=null){
                        fecha.setText(item.getFecha());
                    }
                    if(item.getDescripcion()!=null){
                        descripcion.setText(item.getDescripcion());
                    }
                    if(item.getPrecio()!=null){
                        precio.setText(item.getPrecio().toString());
                    }

                } else {
                    try {
                        respuesta = gson.fromJson(response.errorBody().string(),RespuestaAPI.class);
                        showResultado(respuesta.getResultado());
                        if (response.code() == 400) {
                        } else if(respuesta.getSalida() != null){
                            showResultado(respuesta.getSalida().getAsString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {
                showResultado(t.getMessage());
            }
        });
    }

    public void obtener_usuario(){

        Call<RespuestaAPI> call = RestClient.getClient().usuarioObtener(token);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                RespuestaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Usuario item = gson.fromJson(respuesta.getSalida(),Usuario.class);
                    if(item.getSaldo()!=null){
                        saldo.setText(item.getSaldo());
                    }
                    if(item.getTicketsRestantes()!=null){
                        tickets_restantes = Integer.parseInt(item.getTicketsRestantes());
                    }

                } else {
                    try {
                        respuesta = gson.fromJson(response.errorBody().string(),RespuestaAPI.class);
                        showResultado(respuesta.getResultado());
                        if (response.code() == 400) {
                        } else if(respuesta.getSalida() != null){
                            showResultado(respuesta.getSalida().getAsString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {
                showResultado(t.getMessage());
            }
        });
    }

    public void comprar_menu(){

        Call<RespuestaAPI> call = RestClient.getClient().comprarTicket(token,idmenu);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {

                RespuestaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    showResultado(respuesta.getResultado());
                    finish();
                } else {
                    try {
                        RespuestaErrorApi resp = gson.fromJson(response.errorBody().string(),RespuestaErrorApi.class);
                        showResultado(resp.getResultado());

                        if(resp.getSalida() != null){
                            showResultado(resp.getSalida().getAsString());
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {
                showResultado(t.getMessage());
            }
        });
    }

    private void showResultado(String resultado) {
        Toast.makeText(this, resultado, Toast.LENGTH_LONG).show();
    }

}
