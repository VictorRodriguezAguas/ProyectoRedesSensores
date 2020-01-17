package com.example.sensordemovimiento;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity{

    String ip = "192.168.0.9";
    int puerto = 8081;
    private SharedPreferences sharedpreferences;

    EditText txtUsuario, txtPasswd;
    Button btnLogin, btnRegistro;
    ProgressBar progressBar;

    // atributos para el video
    private VideoView videoBG;
    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // configuracion
        sharedpreferences = getSharedPreferences(Utils.MyPREFERENCES, this.MODE_PRIVATE);
        ip = sharedpreferences.getString("ip", ip);
        puerto = sharedpreferences.getInt("port", puerto);

        //Referencias a los controles


       //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
        /*
        videoBG = (VideoView) findViewById(R.id.videoView);
        Uri uri = Uri.parse("android.resource://" // First start with this,
                + getPackageName() // then retrieve your package name,
                + "/" // add a slash,
                + R.raw.smoke); // and then finally add your video resource. Make sure it is stored
        // in the raw folder.
        videoBG.setVideoURI(uri);
        // Start the VideoView
        videoBG.start();
        videoBG.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                mMediaPlayer = mediaPlayer;
                // We want our video to play over and over so we set looping to true.
                mMediaPlayer.setLooping(true);
                // We then seek to the current posistion if it has been set and play the video.
                if (mCurrentVideoPosition != 0) {
                    mMediaPlayer.seekTo(mCurrentVideoPosition);
                    mMediaPlayer.start();
                }
            }
        });
        */

        txtUsuario = (EditText) findViewById(R.id.txtUsuario);
        txtPasswd = (EditText) findViewById(R.id.txtPasswd);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        btnRegistro = (Button) findViewById(R.id.btnRegistro);
        btnRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                startActivity(new Intent(MainActivity.this, formulario_registro.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // progressBar.setVisibility(View.VISIBLE);
                String usuario = txtUsuario.getText().toString();
                String contrasena = txtPasswd.getText().toString();

                if(TextUtils.isEmpty(usuario)){
                    txtUsuario.setError("SE REQUIERE USUARIO");
                    txtUsuario.setFocusable(true);
                }
                if(TextUtils.isEmpty(contrasena)){
                    txtPasswd.setError("SE REQUIERE SU CONTRASEÑA");
                    txtPasswd.setFocusable(true);
                    return;
                }
                if(contrasena.length()<8){
                    txtPasswd.setError("Se necesita contraseña >= 8 caracteres");
                    txtPasswd.setFocusable(true);
                }
                api_login(usuario, contrasena);
                /*
                if(true){
                    Toast.makeText(MainActivity.this,"INGRESO DE USUARIO EXITOSO",Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MainActivity.this, RegistroExitoso.class));

                }else{
                    Toast.makeText(MainActivity.this,"ERROR !",Toast.LENGTH_SHORT).show();
                }*/
            }
        });

    }

    /**
     * Funcion consulta al api y devuelve el token del usuario
     * @param usuario correo del usuario
     * @param contrasena contraseña del usuario
     */
    public void api_login(String usuario, String contrasena){
        JSONObject parameters = new JSONObject();
        try {
            parameters.put("username", usuario);
            parameters.put("password", contrasena);
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            final String server = Utils.getServidor(ip, puerto);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST,server + Utils.endPointLogin , parameters, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                String token = response.getString(Utils.TOKEN);
                                SharedPreferences.Editor editor = sharedpreferences.edit();
                                editor.putString(Utils.TOKEN, token);
                                editor.apply();
                                Intent intent = new Intent(MainActivity.this, RegistroExitoso.class);
                                startActivity(intent);
                                finish();
                            } catch (JSONException e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, Utils.ERROR_LOGIN_RED, Toast.LENGTH_SHORT).show();
                            }
                        }
                    }, new Response.ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                int code = error.networkResponse.statusCode;
                                JSONObject json = new JSONObject(new String(error.networkResponse.data));
                                String message = "Error " + String.valueOf(code) + json.getString("message");
                                Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                                Toast.makeText(MainActivity.this, Utils.ERROR_LOGIN_RED_ACCESO, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    //metodo para evitar que le sesion se cierre al cerrar la aplicacion
    @Override
    protected void onStart() {
        super.onStart();
        //si el usuario ha iniciado sesion

        if(false){
            startActivity(new Intent(MainActivity.this, RegistroExitoso.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Capture the current video position and pause the video.
        //mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
        //videoBG.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the video when resuming the Activity
        //videoBG.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        //mMediaPlayer.release();
        //mMediaPlayer = null;
    }
}
