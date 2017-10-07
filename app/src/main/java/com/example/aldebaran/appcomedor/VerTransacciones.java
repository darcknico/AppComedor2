package com.example.aldebaran.appcomedor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Transaccion;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerTransacciones extends AppCompatActivity {
    private String token;
    private ListView lvTransacciones;
    private Date fechahoy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ver_transacciones);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_vertransacciones);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("Ver transacción");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        lvTransacciones = (ListView) findViewById(R.id.lv_transacciones);
        actualizar();
        lvTransacciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Transaccion transaccion = (Transaccion) parent.getAdapter().getItem(position);
                Toast.makeText(getApplicationContext(),"Falta para la jiunsa"+transaccion.getId(),Toast.LENGTH_LONG).show();

                /*Intent intent = new Intent(getApplicationContext(), comprarMenu.class);
                intent.putExtra("idmenu",menu.getIdMenu());
                startActivity(intent);*/
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_transacciones, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.item_agregar_transaccion) {
            Intent intent = new Intent(getApplicationContext(),crearTransaccion.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.item_regresar_transaccion) {
            finish();
            Intent intent = new Intent(getApplicationContext(),TicketsActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    void actualizar(){
        Call<RespuestaListaAPI> call = RestClient.getClient().transaccionLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                RespuestaListaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Type listType = new TypeToken<List<Transaccion>>() {}.getType();
                    List<Transaccion> lista = gson.fromJson(respuesta.getSalida(),listType);
                    transaccionAdapter adapter = new transaccionAdapter(getApplicationContext(),lista);
                    lvTransacciones.setAdapter(adapter);
                } else {
                    try {
                        respuesta = gson.fromJson(response.errorBody().string(),RespuestaListaAPI.class);
                        showResultado(respuesta.getResultado());
                        if (response.code() == 400) {
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaListaAPI> call, Throwable t) {
                showResultado(t.getMessage());
            }
        });
    }
    public void showResultado(String message){
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onRestart() {
        actualizar();
        super.onRestart();
    }

    public class transaccionAdapter extends ArrayAdapter<Transaccion> {

        public transaccionAdapter(@NonNull Context context, @NonNull List<Transaccion> objects) {
            super(context, 0, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext()
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            // ¿Existe el view actual?
            if (null == convertView) {
                convertView = inflater.inflate(
                        R.layout.list_item_transaccion,
                        parent,
                        false);
            }

            // Referencias UI.
            TextView fecha = (TextView) convertView.findViewById(R.id.txt_trasaccion_fecha);
            TextView importe = (TextView) convertView.findViewById(R.id.txt_trasaccion_importe);
            TextView concepto = (TextView) convertView.findViewById(R.id.txt_trasaccion_concepto);

            // Lead actual.
            Transaccion item = getItem(position);

            fecha.setText(item.getFecha_acreditacion());
            importe.setText(""+item.getMonto());
            concepto.setText(item.getConcepto());

            return convertView;
        }
    }
}
