package com.example.aldebaran.appcomedor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.Build;
import android.os.Bundle;

import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.aldebaran.appcomedor.apirest.LoginBody;
import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.apirest.Usuario;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class LoginActivity extends AppCompatActivity {


    private AutoCompleteTextView mDniView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private Button btnRegistrar;
    private Toolbar toolbar;
    private TextView titulo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (sp!=null) {
            String token = sp.getString("token", null);
            if(token != null){
                sp.edit().apply();
                finish();
                Intent i = new Intent(getApplicationContext(), TicketsActivity.class);
                startActivity(i);
            }
        }

        setContentView(R.layout.activity_login);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        titulo = (TextView) findViewById(R.id.titulo_toolbar);
        titulo.setText("App Comedor - Login");


        // Set up the login form.
        mDniView = (AutoCompleteTextView) findViewById(R.id.dni);
        mPasswordView = (EditText) findViewById(R.id.password);

        //si se escribio una contraseña se puede intentar el logeo
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button signInButton = (Button) findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
        btnRegistrar = (Button) findViewById(R.id.btn_registrar);
        btnRegistrar.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    private void attemptLogin() {

        String dni = mDniView.getText().toString();
        String password = mPasswordView.getText().toString();


        // Reset errors.
        mDniView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid dni.
        if (TextUtils.isEmpty(dni) && !isDniValid(dni)) {
            mDniView.setError(getString(R.string.error_field_required));
            focusView = mDniView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();

        } else {
            // no hay errores con los views del formulario, comienza el intento de logeo
            showProgress(true);

            Call<RespuestaAPI> loginCall = RestClient.getClient().login(new LoginBody(dni,password));
            loginCall.enqueue(new Callback<RespuestaAPI>() {
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
                        prefsEditor.putString("nombre",us.getNombre());
                        prefsEditor.commit();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), TicketsActivity.class);
                        startActivity(intent);

                    } else {
                        Gson gson = new Gson();
                        try {
                            respuesta = gson.fromJson(response.errorBody().string(),RespuestaAPI.class);
                            showLoginError(respuesta.getResultado());
                            if (response.code() == 400){
                                JsonObject salida = respuesta.getSalida();
                                if (salida.has("dni")){
                                    String error= salida.getAsJsonArray("dni").get(0).getAsString();
                                    mDniView.setError(error);
                                }
                                if (salida.has("contraseña")){
                                    String error = salida.getAsJsonArray("contraseña").get(0).getAsString();
                                    mPasswordView.setError(error);
                                }
                            }
                        }
                        catch (IOException e){
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(Call<RespuestaAPI> call, Throwable t) {
                    showLoginError(t.getMessage());
                    showProgress(false);
                }
            });
        }
    }

    private void showLoginError(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_LONG).show();
    }

    private boolean isDniValid(String dni) {
        //por ahora sin controles sobre el dni
        //TODO: Replace this with your own logic
        return (dni.length()>5);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        //password con al menos 8 caracteres a 128 como tope.
        return (password.length() >= 8 && password.length()<=128);
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

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

}

