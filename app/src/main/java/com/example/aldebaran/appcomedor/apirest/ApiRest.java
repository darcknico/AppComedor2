package com.example.aldebaran.appcomedor.apirest;

import com.example.aldebaran.appcomedor.modelos.Transaccion;
import com.example.aldebaran.appcomedor.modelos.Usuario;

import java.util.HashMap;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by aldebaran on 26/06/17.
 */

public interface ApiRest {
    //public static final String BASE_URL = "http://proyectosinformaticos.esy.es/apirest.slim/public/";
    public static final String BASE_URL = "http://vps142351.vps.ovh.ca/proyectos/comedor-rest/public/";

    @Headers("Content-Type: application/json")
    @POST("token")
    Call<RespuestaToken> token(
            @Body HashMap<String, Object> body
    );

    @Headers("Content-Type: application/json")
    @POST("acceder")
    Call<RespuestaAPI> login(@Body LoginBody loginBody);

    @Headers("Content-Type: application/json")
    @POST("registrar")
    Call<RespuestaAPI> register(@Body Usuario usuario);

    //OBTENER LISTA DE MENUS PARA COMPRAR DE SU TICKETS
    @Headers( "Content-Type: application/json" )
    @GET("menu")
    Call<RespuestaListaAPI> menuLista(
            @Header("Authorization") String token);

    //OBTIENE UN SOLO MENU
    @Headers( "Content-Type: application/json" )
    @GET("menu/{id}")
    Call<RespuestaAPI> menuObtener(
            @Header("Authorization") String token,
            @Path("id") int id);

    //COMPRA UN TICKET PARA EL MENU CON EL idMenu
    @Headers( "Content-Type: application/json" )
    @POST("menu/{idMenu}/ticket")
    Call<RespuestaAPI> comprarTicket(
            @Header("Authorization") String token,
            @Path("idMenu") int idMenu);

    //////GESTION DE TICKETS
    //Listado de mis tickets
    @Headers( "Content-Type: application/json" )
    @GET("ticket")
    Call<RespuestaListaAPI> ticketLista(
            @Header("Authorization") String token);
    //Listado de mis tickets por estado
    @Headers( "Content-Type: application/json" )
    @GET("ticket")
    Call<RespuestaListaAPI> ticketLista(
            @Header("Authorization") String token,
            @Header("estado") String estado);

    //Cambiar estado del ticket a Cancelado
    @Headers( "Content-Type: application/json" )
    @DELETE("ticket/{id}")
    Call<RespuestaAPI> ticketEliminar(
            @Header("Authorization") String token,
            @Path("id") int id);

    //OBTIENE UN SOLO USUARIO
    @Headers( "Content-Type: application/json" )
    @GET("usuario")
    Call<RespuestaAPI> usuarioObtener(
            @Header("Authorization") String token);

    //MODIFICA UN SOLO USUARIO
    @Headers( "Content-Type: application/json" )
    @PUT("usuario/{id}")
    Call<RespuestaAPI> usuarioModificar(
            @Header("Authorization") String token,@Path("id") int id,
            @Body Usuario usuario);

    //AGREGA UNA IMAGEN A UN SOLO USUARIO
    @Multipart
    @POST("usuario/{id}/imagen")
    Call<RespuestaAPI> usuarioSubirImagen(
            @Header("Authorization") String token,@Path("id") int id,
            @Part("profile_pic\"; filename=\"pp.png\" ") RequestBody file);

    //OBTIENE UN SOLO TICKET
    @Headers( "Content-Type: application/json" )
    @GET("ticket/{id}")
    Call<RespuestaAPI> ticketObtener(
            @Header("Authorization") String token,
            @Path("id") int id);

    //AGREGA UNA TRANSACCION
    @Headers("Content-Type: application/json")
    @POST("transaccion")
    Call<RespuestaAPI> crearTransaccion(
            @Header("Authorization") String token,
            @Body Transaccion crearTransaccionBody);

    //Listado de mis tickets
    @Headers( "Content-Type: application/json" )
    @GET("transaccion")
    Call<RespuestaListaAPI> transaccionLista(
            @Header("Authorization") String token);
}
