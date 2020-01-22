package com.example.sensordemovimiento;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sensordemovimiento.models.Nodo;
import com.example.sensordemovimiento.models.Notificacion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Videoview extends AppCompatActivity {

    VideoView videoView;
    ProgressDialog pd;
    TextView idView, fechaView, nodoView, ubicacionView, responsable;
    Uri uri;
    Notificacion notificacion;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_videoview);

        sharedPreferences = getSharedPreferences(Utils.MyPREFERENCES, this.MODE_PRIVATE);
        token = sharedPreferences.getString(Utils.TOKEN,"");

        idView = (TextView) findViewById(R.id.editId);
        fechaView = (TextView) findViewById(R.id.editFecha);
        nodoView = (TextView) findViewById(R.id.editNodo);
        ubicacionView = (TextView) findViewById(R.id.editUbicacion);
        responsable = (TextView) findViewById(R.id.editresponsable);

        Intent intent = getIntent();
        if (intent.getIntExtra("notificacionId", 0) != 0) {
            notificacion = new Notificacion(intent.getIntExtra("notificacionId", 0),
                    intent.getStringExtra("fecha"), intent.getIntExtra("region",0),
                    intent.getStringExtra("urlVideo"),new Nodo(intent.getIntExtra("nodoId",0)));

            idView.setText("Id - Región: " + notificacion.getId() + " - " + notificacion.getRegion());
            fechaView.setText("Fecha: " + notificacion.getFecha());

            actualizarInformacionNodo();
        }

        try {
            final MediaController mediacontroller = new MediaController(this);
            mediacontroller.setAnchorView(videoView);

            videoView = (VideoView)findViewById(R.id.video);
            pd = new ProgressDialog(Videoview.this);
            pd.show();
            System.out.println(Environment.getExternalStorageDirectory());
            uri = Uri.parse(Environment.getExternalStorageDirectory() + "/s.mp4");

            videoView.setMediaController(mediacontroller);
            videoView.setVideoURI(uri);
            videoView.requestFocus();

            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Toast.makeText(getApplicationContext(), "Video finalizado", Toast.LENGTH_SHORT).show();
                    mp.release();
                }
            });

            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                    pd.dismiss();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Toast.makeText(getApplicationContext(), "ERROR:" + what + "EXTRA " + extra, Toast.LENGTH_SHORT).show();
                    pd.dismiss();
                    return false;
                }
            });

        } catch (Exception e) {
            // TODO: handle exception
            Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
        }
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse( notificacion.getUrlVideo())));
    }

    /*----------------------------- retroceder----------------------------------------*/
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, Notificaciones.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            videoView.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    void actualizarInformacionNodo(){
        try {
            String ip = sharedPreferences.getString("ip","");
            int puerto = sharedPreferences.getInt("puerto",0);
            if(puerto != 0 && ip != ""){
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Utils.getServidor(ip, puerto);
                String url = server + "/nodes/" + notificacion.getNodo().getId();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    notificacion.getNodo().setMac(response.getString("mac"));
                                    nodoView.setText("Nodo: "+ notificacion.getNodo().getId() + " -  mac: " + notificacion.getNodo().getMac());
                                    actualizarLocalizacion(response.getInt("location"));
                                    JSONArray usuarios = response.getJSONArray("users");
                                    for (int i = 0; i < 1; i++) {
                                        int idUsuario = usuarios.getInt(0);
                                        actualizarResponsable(idUsuario);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Videoview.this, Utils.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(Videoview.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(Videoview.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
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

    void actualizarLocalizacion(int location){
        try {
            String ip = sharedPreferences.getString("ip","");
            int puerto = sharedPreferences.getInt("puerto",0);
            if(puerto != 0 && ip != ""){
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Utils.getServidor(ip, puerto);
                String url = server + "/locations/" + location;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    nodoView.setText(nodoView.getText() + " " + response.getString("description"));
                                    ubicacionView.setText("Ubicación: lat-> " + response.getDouble("lat") + " , long->" + response.getDouble("lon"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Videoview.this, Utils.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(Videoview.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(Videoview.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
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

    void actualizarResponsable(int usuarios){
        try {
            String ip = sharedPreferences.getString("ip","");
            int puerto = sharedPreferences.getInt("puerto",0);
            if(puerto != 0 && ip != ""){
                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                String server = Utils.getServidor(ip, puerto);
                String url = server + "/users/" + usuarios;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    responsable.setText("Responsable: " + response.getString("username") + ", celular: 0989389265");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(Videoview.this, Utils.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try {
                                    int code = error.networkResponse.statusCode;
                                    JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                    String message = "Error " + String.valueOf(code) + json.getString("message");
                                    Toast.makeText(Videoview.this, message, Toast.LENGTH_SHORT).show();
                                }catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(Videoview.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
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

}
