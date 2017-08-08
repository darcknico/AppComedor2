package com.example.aldebaran.appcomedor;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.apirest.Menu;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.apirest.Ticket;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.WHITE;

public class verTicket extends AppCompatActivity {

    TextView fecha, descripcion, estado, codigo;
    ImageView codigoQR;
    Button btnCancelar;
    private int idTicket;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_ticket);

        toolbar = (Toolbar) findViewById(R.id.toolbar_verTicket);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("Ver ticket");

        fecha = (TextView) findViewById(R.id.txt_fecha_verticket);
        descripcion = (TextView) findViewById(R.id.txt_descripcion_verticket);
        estado = (TextView) findViewById(R.id.txt_estado_verticket);
        codigo = (TextView) findViewById(R.id.txt_codigo_verticket);
        codigoQR = (ImageView) findViewById(R.id.img_codigo_verticket);
        btnCancelar = (Button) findViewById(R.id.btn_cancelar_verticket);
        idTicket = getIntent().getExtras().getInt("idticket");
        codigo.setText(""+idTicket);
        obtener_ticket(idTicket);

        try {
            Bitmap bitmap = encodeAsBitmap(codigo.getText().toString());
            codigoQR.setImageBitmap(bitmap);
        }catch (WriterException e){
            e.printStackTrace();
        }


    }

    Bitmap encodeAsBitmap (String string) throws WriterException{
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(string,
                    BarcodeFormat.QR_CODE, 200,200,null);
        } catch (IllegalArgumentException e){
            e.printStackTrace();
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int [] pixcels = new int[w*h];
        for (int y=0; y<h;y++){
            int offset = y*w;
            for (int x = 0; x<w;x++){
                pixcels[offset+x] = result.get(x,y)? BLACK : WHITE;
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w,h,Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixcels,0,200,0,0,w,h);
        return bitmap;
    }
    public void obtener_ticket(int id){

        Call<RespuestaAPI> call = RestClient.getClient().ticketObtener(id);
        call.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                RespuestaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Ticket item = gson.fromJson(respuesta.getSalida(),Ticket.class);

                    if(item.getEstado()!=null){
                        estado.setText(item.getEstado());
                    }
                    if(item.getMenu().getFecha()!=null){
                        fecha.setText(item.getMenu().getFecha());
                    }
                    if(item.getMenu().getDescripcion()!=null){
                        descripcion.setText(item.getMenu().getDescripcion().toString());
                    }
                    if (TextUtils.equals(estado.getText().toString(),"Cancelado")) {
                        btnCancelar.setVisibility(View.INVISIBLE);
                    } else {
                        btnCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                eliminar_ticket();
                            }
                        });
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

    public void eliminar_ticket(){

        Call<RespuestaAPI> call = RestClient.getClient().ticketEliminar(idTicket);
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

    public void showResultado(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }
}
