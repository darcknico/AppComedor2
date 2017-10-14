package com.example.aldebaran.appcomedor;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.transition.Fade;
import android.transition.Slide;
import android.transition.TransitionSet;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aldebaran.appcomedor.apirest.RespuestaAPI;
import com.example.aldebaran.appcomedor.apirest.RestClient;
import com.example.aldebaran.appcomedor.fragment.HomeFragment;
import com.example.aldebaran.appcomedor.fragment.ListaFragment;
import com.example.aldebaran.appcomedor.fragment.TransaccionFragment;
import com.example.aldebaran.appcomedor.modelos.Transaccion;
import com.example.aldebaran.appcomedor.modelos.Usuario;
import com.google.gson.Gson;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final long MOVE_DEFAULT_TIME = 300;
    public static final long FADE_DEFAULT_TIME = 300;

    private CoordinatorLayout coordinatorLayout;
    private NavigationView navigationView;
    private FragmentManager mFragmentManager;
    private Handler mDelayedTransactionHandler;

    //nav header
    private TextView mainNombreUsuario;
    private ImageView mainImageUsuario;

    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();


        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        //inicio de sesion
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        token = sp.getString("token","");

        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        //nav header
        mainNombreUsuario = (TextView) headerView.findViewById(R.id.mainNombreUsuario);
        mainImageUsuario = (ImageView) headerView.findViewById(R.id.mainImageUsuario);

        actualizarUsuario();

        mFragmentManager = getSupportFragmentManager();
        mDelayedTransactionHandler = new Handler();

        mFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.anim.mpsdk_slide_up_to_down_in,R.anim.mpsdk_slide_down_to_top_out)
                .replace(R.id.content_frame,  new HomeFragment())
                .commit();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        actualizarUsuario();
    }

    public void actualizarUsuario(){
        Call<RespuestaAPI> loginCall = RestClient.getClient().usuarioObtener(token);
        loginCall.enqueue(new Callback<RespuestaAPI>() {
            @Override
            public void onResponse(Call<RespuestaAPI> call, Response<RespuestaAPI> response) {
                Gson gson = new Gson();
                if (response.isSuccessful()) {
                    if(!response.body().getSalida().isJsonArray()) {
                        Usuario item = gson.fromJson(response.body().getSalida(), Usuario.class);

                        mainNombreUsuario.setText(item.getNombre() + " " + item.getApellido());
                    } else {
                        mainNombreUsuario.setText("ADMINISTRADOR");
                    }
                } else {
                    if (response.code() == 401) {
                        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                        sp.edit().clear().apply();
                        Snackbar.make(coordinatorLayout, "Perdio la session", Snackbar.LENGTH_LONG)
                                .show();
                        finish();
                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onFailure(Call<RespuestaAPI> call, Throwable t) {

            }
        });
    }


    @Override
    public void onBackPressed() {
        if(navigationView.getMenu().getItem(0).isChecked()){
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            navigationView.getMenu().getItem(0).setChecked(true);
            displaySelectedScreen(R.id.nav_home);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    Fragment fragment = null;
    public void displaySelectedScreen(int id){
        fragment = null;
        switch (id){
            case R.id.nav_home:
                fragment = new HomeFragment();
                break;
            case R.id.nav_menu:
                fragment = new ListaFragment().setOption(ListaFragment.MENU);
                break;
            case R.id.nav_ticket:
                fragment = new ListaFragment().setOption(ListaFragment.TICKET);
                break;
            case R.id.nav_logout:
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                sp.edit().clear().apply();
                finish();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.nav_transaccion:
                fragment = new TransaccionFragment();
                break;
            case R.id.nav_close:
                finish();
                break;
        }
        if (fragment != null) {

            mDelayedTransactionHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Fragment previousFragment = mFragmentManager.findFragmentById(R.id.content_frame);
                    FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();

                    Slide exitFade = new Slide();
                    exitFade.setSlideEdge(Gravity.TOP);
                    exitFade.setDuration(FADE_DEFAULT_TIME);
                    previousFragment.setExitTransition(exitFade);

                    TransitionSet enterTransitionSet = new TransitionSet();
                    enterTransitionSet.addTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(android.R.transition.slide_top));
                    enterTransitionSet.setDuration(MOVE_DEFAULT_TIME);
                    enterTransitionSet.setStartDelay(FADE_DEFAULT_TIME);
                    fragment.setSharedElementEnterTransition(enterTransitionSet);

                    Slide enterFade = new Slide();
                    enterFade.setSlideEdge(Gravity.TOP);
                    enterFade.setStartDelay(MOVE_DEFAULT_TIME + FADE_DEFAULT_TIME);
                    enterFade.setDuration(FADE_DEFAULT_TIME);
                    fragment.setEnterTransition(enterFade);

                    fragmentTransaction.replace(R.id.content_frame, fragment);
                    fragmentTransaction.commitAllowingStateLoss();
                    /*
                    mFragmentManager
                            .beginTransaction()
                            .setCustomAnimations(R.anim.mpsdk_slide_up_to_down_in,R.anim.mpsdk_slide_down_to_top_out)
                            .replace(R.id.content_frame, fragment)
                            .commit();
                            */
                }
            }, 250);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
        fragment.onActivityResult(requestCode, resultCode, data);
    }
}
