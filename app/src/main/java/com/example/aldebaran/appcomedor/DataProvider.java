package com.example.aldebaran.appcomedor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by aldebaran on 23/06/17.
 */

public class DataProvider {
    public static HashMap<String,List<String>> getInfo(){
        HashMap<String,List<String>> DetalleEncabezados = new HashMap<String, List<String>>();
        List<String> DetalleLineasTickets = new ArrayList<String>();
        DetalleLineasTickets.add("Ver tickets");
        DetalleLineasTickets.add("Comprar Ticket");
        List<String> DetalleLineasTrans = new ArrayList<String>();
        DetalleLineasTrans.add("Ver transacciones");
        DetalleEncabezados.put("Tickets",DetalleLineasTickets);
        DetalleEncabezados.put("Transacciones",DetalleLineasTrans);
        return DetalleEncabezados;
    }
}
