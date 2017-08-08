package com.example.aldebaran.appcomedor.apirest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by aldebaran on 26/06/17.
 */

public class RestClient {

    private static ApiRest REST_API;

    public static ApiRest getClient(){
        if (REST_API == null){
            createClient();
        }
        return REST_API;
    }

    private static void createClient() {

        Retrofit.Builder mBuilder = new Retrofit.Builder()
                .baseUrl(ApiRest.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create());

        REST_API = mBuilder.build().create(ApiRest.class);
    }

}
