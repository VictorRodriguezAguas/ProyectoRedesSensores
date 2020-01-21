package com.example.sensordemovimiento.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.sensordemovimiento.R;
import com.example.sensordemovimiento.models.Notificacion;

import java.util.ArrayList;

/**
 * Esta clase define el adaptador para las listview para listar las notificaciones.
 * @author: Mauricio Leiton Lázaro(mdleiton)
 * @version: 1.0
 */
public class NotificacionAdapter extends ArrayAdapter<Notificacion> {
    private ArrayList<Notificacion> notificaciones;
    private Context context;

    public NotificacionAdapter(Context context, ArrayList<Notificacion> notificaciones){
        super(context,0, notificaciones);
        this.context = context;
        this.notificaciones = notificaciones;
    }

    static class ViewHolder{
        public TextView id;  // va con region
        public TextView fecha;
        public TextView nodo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        if(convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_notificacion, parent,false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.id = (TextView) convertView.findViewById(R.id.editId);
            viewHolder.fecha = (TextView) convertView.findViewById(R.id.editFecha);
            viewHolder.nodo = (TextView) convertView.findViewById(R.id.editNodo);
            convertView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        Notificacion actualnoticacion = this.notificaciones.get(position);
        holder.id.setText("Id - Región : " + String.valueOf(actualnoticacion.getId()) + " - " + String.valueOf(actualnoticacion.getRegion()));
        holder.fecha.setText("Fecha : " + actualnoticacion.getFecha());
        holder.nodo.setText("Nodo : " + actualnoticacion.getNodo());
        return convertView;
    }
}