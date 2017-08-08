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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.LoginActivity;
import com.example.aldebaran.appcomedor.R;
import com.example.aldebaran.appcomedor.VerTransacciones;
import com.example.aldebaran.appcomedor.apirest.Menu;
import com.example.aldebaran.appcomedor.apirest.RespuestaListaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.apirest.Ticket;
import com.example.aldebaran.appcomedor.apirest.ticketMenu;
import com.example.aldebaran.appcomedor.comprarMenu;
import com.example.aldebaran.appcomedor.verTicket;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.lang.reflect.Type;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TicketsActivity extends AppCompatActivity {
    private String token;
    private ListView lvTickets;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_tickets);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        TextView titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("Menús y tickets");

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        lvTickets = (ListView) findViewById(R.id.lv_tickets);
        actualizar();
        lvTickets.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ticketMenu ticketMenu = (ticketMenu) parent.getAdapter().getItem(position);
                if (ticketMenu instanceof Menu){
                    Menu menu = (Menu) ticketMenu;
                    Intent intent = new Intent(getApplicationContext(), comprarMenu.class);
                    intent.putExtra("idmenu",menu.getIdMenu());
                    startActivity(intent);
                } else {
                    Ticket ticket = (Ticket) ticketMenu;
                    Intent intent = new Intent(getApplicationContext(), verTicket.class);
                    intent.putExtra("idticket",ticket.getIdTicket());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ticketsmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.ver_transacciones_item) {
            Intent intent = new Intent(getApplicationContext(), VerTransacciones.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.deslogear_item) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            sp.edit().clear().apply();
            finish();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            return true;
        }
        if (id == R.id.salir_item) {
            finish();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onRestart() {
        actualizar();
        super.onRestart();
    }

    void actualizar(){
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
                    actualizar2(lista);
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

    void actualizar2(final List<Ticket> ticketList){
        Call<RespuestaListaAPI> call = RestClient.getClient().menuLista(token);
        call.enqueue(new Callback<RespuestaListaAPI>() {
            @Override
            public void onResponse(Call<RespuestaListaAPI> call, Response<RespuestaListaAPI> response) {
                RespuestaListaAPI respuesta = null;
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    respuesta = response.body();
                    Type listType = new TypeToken<List<Menu>>() {}.getType();
                    List<Menu> listaMenu = gson.fromJson(respuesta.getSalida(),listType);
                    ArrayList<ticketMenu> lista = new ArrayList<ticketMenu>();
                    for (Menu menu:listaMenu) {
                        lista.add((ticketMenu)menu);
                    }
                    for (Ticket ticket:ticketList) {
                        lista.add((ticketMenu)ticket);
                    }
                    Collections.sort(lista, new Comparator<Object>() {
                        @Override
                        public int compare(Object o1, Object o2) {
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                            if (o1 instanceof Ticket) {
                                Ticket v1 = (Ticket) o1;
                                if (o2 instanceof Ticket) {
                                    Ticket v2 = (Ticket) o2;
                                    try {
                                        Date f1 = format.parse(v1.getMenu().getFecha());
                                        Date f2 = format.parse(v2.getMenu().getFecha());
                                        return  f1.compareTo(f2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Menu v2 = (Menu) o2;
                                    try {
                                        Date f1 = format.parse(v1.getMenu().getFecha());
                                        Date f2 = format.parse(v2.getFecha());
                                        return  f1.compareTo(f2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            } else {
                                Menu v1 = (Menu) o1;
                                if (o2 instanceof Ticket) {
                                    Ticket v2 = (Ticket) o2;
                                    try {
                                        Date f1 = format.parse(v1.getFecha());
                                        Date f2 = format.parse(v2.getMenu().getFecha());
                                        return f1.compareTo(f2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    Menu v2 = (Menu) o2;
                                    try {
                                        Date f1 = format.parse(v1.getFecha());
                                        Date f2 = format.parse(v2.getFecha());
                                        return f1.compareTo(f2);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            return 0;
                        }
                    });
                    lvTickets.setAdapter(new ticketsAdapter(getApplicationContext(),lista));
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

    private void showResultado(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    public class ticketsAdapter extends ArrayAdapter<ticketMenu>{

        public ticketsAdapter(@NonNull Context context, @NonNull List<ticketMenu> objects) {
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
                        R.layout.list_item_menu,
                        parent,
                        false);
            }

            // Referencias UI.
            TextView fecha = (TextView) convertView.findViewById(R.id.txt_menu_fecha);
            TextView estado = (TextView) convertView.findViewById(R.id.txt_menu_estado);
            ImageView img = (ImageView) convertView.findViewById(R.id.imageView3);
            // Lead actual.
            Object item = getItem(position);

            if (item instanceof Ticket) {
                Ticket object = (Ticket) item;
                img.setImageResource(R.drawable.ic_ticket);
                fecha.setText(object.getMenu().getFecha());
                estado.setText(object.getEstado());
            } else {
                    Menu object = (Menu) item;
                    fecha.setText(object.getFecha());
                    estado.setText("No comprado");
            }

            return convertView;
        }
    }
}
