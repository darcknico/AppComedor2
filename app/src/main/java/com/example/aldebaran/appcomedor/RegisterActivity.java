package com.example.aldebaran.appcomedor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


import com.example.aldebaran.appcomedor.apirest.RegisterBody;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.modelos.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.zxing.common.StringUtils;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class RegisterActivity extends AppCompatActivity {

    private TextView acountTextView;
    private TextView nameTextView;
    private TextView surnameTextView;
    private TextView passwordTextView;
    private Toolbar toolbar;
    private View progressBar;
    private TextInputLayout registerAcountLayout;
    private TextInputLayout registerNameLayout;
    private TextInputLayout registerPasswordLayout;
    private TextInputLayout registerSurnameLayout;
    private TextView repasswordTextView;
    private TextInputLayout registerRePasswordLayout;
    private View registerFormView;

    //    View registerFormView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        toolbar = (Toolbar) findViewById(R.id.toolbar_register);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        TextView titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("Registrar usuario");

        acountTextView = (TextView) findViewById(R.id.RegisterAcountTextView);
        nameTextView = (TextView) findViewById(R.id.RegisterNameTextView);
        passwordTextView = (TextView) findViewById(R.id.RegisterPasswordEditText);
        repasswordTextView = (TextView) findViewById(R.id.ReRegisterPasswordEditText);
        surnameTextView = (TextView) findViewById(R.id.RegisterSurnameTextView);
        registerFormView = findViewById(R.id.register_form);

        registerAcountLayout = (TextInputLayout) findViewById(R.id.registerAcountLayout);
        registerNameLayout = (TextInputLayout) findViewById(R.id.registerNameLayout);
        registerPasswordLayout = (TextInputLayout) findViewById(R.id.registerPasswordLayout);
        registerRePasswordLayout = (TextInputLayout) findViewById(R.id.registerRePasswordLayout);
        registerSurnameLayout = (TextInputLayout) findViewById(R.id.registerSurnameLayout);

        //registerFormView = findViewById(R.id.register_form);
        progressBar = findViewById(R.id.RegisterProgressBar);

        final Button registrar = (Button) findViewById(R.id.RegisterRegisterButton);
        registrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registrar();
            }
        });
        final Button cancelar = (Button) findViewById(R.id.RegisterCancelButton);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);


            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            registerFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });


            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            registerFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    private void showLoginError(String error) {
        Log.d("error",error);
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    private boolean registrar(){
        String acount = acountTextView.getText().toString();
        String name = nameTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String repassword = repasswordTextView.getText().toString();
        String surname = surnameTextView.getText().toString();

        registerAcountLayout.setError(null);
        registerNameLayout.setError(null);
        registerPasswordLayout.setError(null);
        registerRePasswordLayout.setError(null);
        registerSurnameLayout.setError(null);

        boolean cancel = true;
        if(!TextUtils.isEmpty(acount)){
            registerAcountLayout.setError(getString(R.string.error_field_required));
            cancel = false;
        }
        if(!TextUtils.isEmpty(name)){
            registerNameLayout.setError(getString(R.string.error_field_required));
            cancel = false;
        } else if(isAlphabetic(name)){
            registerNameLayout.setError("No puede contener digitos o caracteres especiales");
            cancel = false;
        }
        if(!TextUtils.isEmpty(surname)){
            registerSurnameLayout.setError(getString(R.string.error_field_required));
            cancel = false;
        } else if(isAlphabetic(surname)){
            registerSurnameLayout.setError("No puede contener digitos o caracteres especiales");
            cancel = false;
        }

        if(!TextUtils.isEmpty(password)){
            registerPasswordLayout.setError(getString(R.string.error_field_required));
            cancel = false;
        } else if(!isPasswordValid(password)) {
            registerPasswordLayout.setError("Al menos 8 caracteres");
            cancel = false;
        } else if(!repassword.equals(password)) {
            registerRePasswordLayout.setError("Las contraseñas no coinciden");
            cancel = false;
        }

        if(cancel) {
            showProgress(true);
            Usuario usuario = new Usuario();
            usuario.setDni(acount);
            usuario.setNombre(name);
            usuario.setContraseña(password);
            usuario.setApellido(surname);

            Call<RespuestaAPI> registerCall = RestClient.getClient().register(usuario);
            registerCall.enqueue(new Callback<RespuestaAPI>() {
                @Override
                public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                    showProgress(false);
                    RespuestaAPI respuesta = null;
                    if (response.isSuccessful()) {
                        respuesta = response.body();
                        Gson gson = new Gson();
                        Usuario us = gson.fromJson(respuesta.getSalida(), Usuario.class);

                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        SharedPreferences.Editor prefsEditor = sp.edit();
                        prefsEditor.putString("token", us.getToken());
                        prefsEditor.putString("nombre", us.getNombre());
                        prefsEditor.commit();
                        Toast.makeText(getApplicationContext(), "Se creo el usuario correctamente", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Gson gson = new Gson();
                        try {
                            respuesta = gson.fromJson(response.errorBody().string(), RespuestaAPI.class);
                            showLoginError(respuesta.getResultado());
                            if (response.code() == 400) {
                                JsonObject salida = respuesta.getSalida();
                                if (salida.has("dni")) {
                                    acountTextView.setError(salida.getAsJsonArray("dni").get(0).getAsString());
                                }
                                if (salida.has("nombre")) {
                                    nameTextView.setError(salida.getAsJsonArray("nombre").get(0).getAsString());
                                }
                                if (salida.has("contraseña")) {
                                    passwordTextView.setError(salida.getAsJsonArray("contraseña").get(0).getAsString());
                                }
                            } else if (respuesta.getSalida() != null) {
                                showLoginError(respuesta.getSalida().getAsString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RespuestaAPI> call, Throwable t) {
                    showProgress(false);
                    showLoginError(t.getMessage());
                }
            });
        }
        return true;
    }

    public boolean isAlphabetic(String string){
        for (char character:string.toCharArray()) {
            if (!Character.isLetter(character) || character!=' '){
                return false;
            }
        }
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //password con al menos 8 caracteres a 128 como tope.
        return (password.length() >= 8 && password.length()<=128);
    }
}
