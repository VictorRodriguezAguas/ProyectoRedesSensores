package com.example.sensordemovimiento;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sensordemovimiento.adapters.NotificacionAdapter;
import com.example.sensordemovimiento.models.Nodo;
import com.example.sensordemovimiento.models.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Notificaciones extends AppCompatActivity implements AdapterView.OnItemSelectedListener  {
    private ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();
    private NotificacionAdapter notificacionAdapter;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout refresh;
    private ListView listNotificaciones;
    TextView NotificacionesText;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notificaciones);
        getSupportActionBar().setTitle("Notificaciones"); // for set actionbar title
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // for add back arrow in action ba

        refresh = (SwipeRefreshLayout) findViewById(R.id.refresh);
        NotificacionesText = (TextView) findViewById(R.id.textView);

        listNotificaciones = (ListView) findViewById(R.id.listanotificaciones);
        listNotificaciones.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Intent intent = new Intent(Notificaciones.this, Videoview.class);
                Notificacion tmpNotificacion = notificaciones.get(position);
                notificaciones.clear();
                notificacionAdapter.notifyDataSetChanged();
                intent.putExtra("fecha",tmpNotificacion.getFecha());
                intent.putExtra("notificacionId", tmpNotificacion.getId());
                intent.putExtra("nodoId", tmpNotificacion.getNodo().getId());
                intent.putExtra("urlVideo", tmpNotificacion.getUrlVideo());
                intent.putExtra("region", tmpNotificacion.getRegion());
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, this.MODE_PRIVATE);
        token = sharedPreferences.getString(Utils.TOKEN,"");
        llenar();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(),"Actualizando", Toast.LENGTH_SHORT).show();
                notificaciones.clear();
                llenar();
                refresh.setRefreshing(false);
                Toast.makeText(getApplicationContext(),"Actualizado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void llenar(){
        actualizarLista();
    }


    void actualizarLista(){
        try {
            String ip = sharedPreferences.getString("ip","");
            int puerto = sharedPreferences.getInt("puerto",0);
            if(puerto != 0 && ip != ""){
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Utils.getServidor(ip, puerto);
                String url = server + "/novelties/";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray notificacionesResponse = response.getJSONArray("results");
                                    NotificacionesText.setText("Total notificaciones: " + notificacionesResponse.length() );
                                    for (int i = 0; i < notificacionesResponse.length(); i++) {
                                        JSONObject notificacion = notificacionesResponse.getJSONObject(i);
                                        notificaciones.add(new Notificacion(notificacion.getInt("id"),
                                                notificacion.getString("date_time"),
                                                notificacion.getInt("region"),
                                                notificacion.getString("video"),
                                                new Nodo(notificacion.getInt("node"))));
                                    }
                                    notificacionAdapter = new NotificacionAdapter(Notificaciones.this, notificaciones);
                                    listNotificaciones.setAdapter(notificacionAdapter);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Notificaciones.this, Utils.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(Notificaciones.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(Notificaciones.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                {

                    /**
                     * Passing some request headers
                     */
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Authorization", "Token " + token);
                        return headers;
                    }
                };
                requestQueue.add(jsonObjectRequest);
            }else{
                Toast.makeText(this, Utils.CONF_ERROR_1, Toast.LENGTH_LONG).show();
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    /*----------------------------- retroceder----------------------------------------*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
