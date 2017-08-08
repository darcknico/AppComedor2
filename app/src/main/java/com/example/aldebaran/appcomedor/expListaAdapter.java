package com.example.aldebaran.appcomedor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

/**
 * Created by aldebaran on 22/06/17.
 */

public class expListaAdapter extends BaseExpandableListAdapter {

    private Context context;
    private HashMap<String,List<String>> titulosLineas;
    private List<String> titulosEncabezados;

    public expListaAdapter(Context context, HashMap<String, List<String>> titulosLineas, List<String> titulosEncabezados) {
        this.context = context;
        this.titulosLineas = titulosLineas;
        this.titulosEncabezados = titulosEncabezados;
    }

    public expListaAdapter () {

    }
    @Override
    public int getGroupCount() {
        return titulosEncabezados.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return titulosLineas.get(titulosEncabezados.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return titulosEncabezados.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return titulosLineas.get(titulosEncabezados.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String titulo = (String) this.getGroup(groupPosition);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.encabezado_personalizado, parent);
        }
        TextView tagTitulo = (TextView) convertView.findViewById(R.id.titulo_encabezado);
        tagTitulo.setText(titulo);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String tituloLinea = (String) this.getChild(groupPosition,childPosition);

        if (convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.lineas_encabezado_personalizado, parent);
        }

        TextView tagTituloLinea = (TextView) convertView.findViewById(R.id.titulo_lineas_encabezado);
        tagTituloLinea.setText(tituloLinea);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
