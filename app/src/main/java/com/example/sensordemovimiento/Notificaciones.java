package com.example.sensordemovimiento;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
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

public class Notificaciones extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ArrayList<Notificacion> notificaciones = new ArrayList<Notificacion>();
    private NotificacionAdapter notificacionAdapter;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout refresh;
    private ListView listNotificaciones;
    TextView NotificacionesText;
    private String token;
    String ip;
    int puerto;

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
                intent.putExtra("fecha", tmpNotificacion.getFecha());
                intent.putExtra("notificacionId", tmpNotificacion.getId());
                intent.putExtra("nodoId", tmpNotificacion.getNodo().getId());
                intent.putExtra("urlVideo", tmpNotificacion.getUrlVideo());
                intent.putExtra("region", tmpNotificacion.getRegion());
                startActivity(intent);
                finish();
            }
        });

        sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, this.MODE_PRIVATE);
        ip = sharedPreferences.getString("ip", ip);
        puerto = sharedPreferences.getInt("puerto", puerto);
        token = sharedPreferences.getString(Utils.TOKEN, "");
        llenar();

        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Toast.makeText(getApplicationContext(), "Actualizando", Toast.LENGTH_SHORT).show();
                notificaciones.clear();
                llenar();
                refresh.setRefreshing(false);
                Toast.makeText(getApplicationContext(), "Actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("10", "Alertas", importance);
            channel.setDescription("Alertas de los nodos de seguridad");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

        Runnable runnable = new Runnable() {
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10000);
                    } catch (Exception e) {
                        e.getLocalizedMessage();
                    }
                    obtenerNuevasNotificaciones();
                }
            }
        };
        Thread mythread = new Thread(runnable);
        mythread.start();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub
    }

    private void llenar() {
        actualizarLista();
    }


    void actualizarLista() {
        try {
            ip = sharedPreferences.getString("ip", "");
            puerto = sharedPreferences.getInt("puerto", 0);
            if (puerto != 0 && ip != "") {
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Utils.getServidor(ip, puerto);
                String url = server + "/novelties/";
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    JSONArray notificacionesResponse = response.getJSONArray("results");
                                    NotificacionesText.setText("Total notificaciones: " + notificacionesResponse.length());
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
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(Notificaciones.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }) {

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
            } else {
                Toast.makeText(this, Utils.CONF_ERROR_1, Toast.LENGTH_LONG).show();
            }
        } catch (Exception e) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.configurarServidor:
                LayoutInflater layoutInflaterAndroid = LayoutInflater.from(Notificaciones.this);
                View mView = layoutInflaterAndroid.inflate(R.layout.configurar, null);
                AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(Notificaciones.this);
                alertDialogBuilderUserInput.setView(mView);
                final EditText userInputIP = (EditText) mView.findViewById(R.id.userInputIp);
                final EditText userInputPort = (EditText) mView.findViewById(R.id.userInputPort);

                userInputIP.setText(ip);
                userInputPort.setText(String.valueOf(puerto));

                alertDialogBuilderUserInput
                        .setCancelable(false)
                        .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                String port_new = userInputPort.getText().toString();
                                String ip_new = userInputIP.getText().toString();
                                if (!port_new.isEmpty() && !ip_new.isEmpty()){
                                    try{
                                        int port_int = Integer.parseInt(port_new);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("ip", ip_new.toLowerCase());
                                        editor.putInt("puerto", port_int);
                                        editor.apply();
                                        ip = ip_new.toLowerCase();
                                        puerto = port_int;

                                        Toast.makeText(Notificaciones.this, Utils.CONF_ACTUALIZADO, Toast.LENGTH_LONG).show();
                                    }catch (Exception e){
                                        Toast.makeText(Notificaciones.this, Utils.CONF_ERROR_1, Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(Notificaciones.this, Utils.CONF_ERROR_2, Toast.LENGTH_LONG).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancelar",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialogBox, int id) {
                                        dialogBox.cancel();
                                    }
                                });
                AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
                alertDialogAndroid.show();
                return true;
            default:
                return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void obtenerNuevasNotificaciones() {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        String ip = sharedPreferences.getString("ip", "");
        int puerto = sharedPreferences.getInt("puerto", 0);
        String server = Utils.getServidor(ip, puerto);
        String url = server + "/notificaciones";
        try {
            JsonArrayRequest jsonObjectRequest = new JsonArrayRequest
                    (Request.Method.GET, url, null, new Response.Listener<JSONArray>() {

                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject notificacion = response.getJSONObject(i);
                                    notificaciones.add(new Notificacion(notificacion.getInt("id"),
                                            notificacion.getString("date_time"),
                                            notificacion.getInt("region"),
                                            notificacion.getString("video"),
                                            new Nodo(notificacion.getInt("node"))));

                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getBaseContext(), "10")
                                            .setSmallIcon(R.mipmap.ic_launcher_round)
                                            .setContentTitle("Aviso: Nueva alerta.")
                                            .setAutoCancel(false)    //swipe for delete
                                            .setContentText("Nodo: " + notificacion.getInt("node") + " Región: " + notificacion.getInt("region"));
                                    Toast.makeText(Notificaciones.this, "Nueva alerta.", Toast.LENGTH_SHORT).show();
                                    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(Notificaciones.this);
                                    notificationManager.notify(1, builder.build());
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
                            } catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(Notificaciones.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}