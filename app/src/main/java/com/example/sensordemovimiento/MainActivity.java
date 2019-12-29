package com.example.sensordemovimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.VideoView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    EditText txtUsuario, txtPasswd;
    Button btnLogin, btnRegistro;
    ProgressBar progressBar;
    //datos para iniciar sesion
    String email;
    String password;
    //obtener informacion del usuario
    FirebaseAuth nAuth;
    // atributos para el video
    private VideoView videoBG;
    MediaPlayer mMediaPlayer;
    int mCurrentVideoPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Referencias a los controles

        nAuth = FirebaseAuth.getInstance();
       //progressBar = findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
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
        if(nAuth.getCurrentUser()!=null){
            startActivity(new Intent(MainActivity.this, RegistroExitoso.class));
            finish();

        }
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
                String email = txtUsuario.getText().toString();
                String password=txtPasswd.getText().toString();

                if(TextUtils.isEmpty(email)){
                    txtUsuario.setError("SE REQUIERE CORREO ELECTRONICO");
                }
                if(TextUtils.isEmpty(password)){
                    txtPasswd.setError("SE REQUIERE SU CONTRASEÑA");
                    return;
                }
                if(password.length()<6){
                    txtPasswd.setError("Se necesita contraseña >= 6 caracteres");
                }


                //INICIO DE SESION CON FIREBASE
                nAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(MainActivity.this,"INGRESO DE USUARIO EXITOSO",Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, RegistroExitoso.class));

                        }else{
                            Toast.makeText(MainActivity.this,"ERROR !"+ task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });

    }
    //metodo para evitar que le sesion se cierre al cerrar la aplicacion
    @Override
    protected void onStart() {
        super.onStart();
        //si el usuario ha iniciado sesion
        FirebaseUser currentUser = nAuth.getCurrentUser();
        if(currentUser!=null){
            startActivity(new Intent(MainActivity.this, RegistroExitoso.class));

        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Capture the current video position and pause the video.
        mCurrentVideoPosition = mMediaPlayer.getCurrentPosition();
        videoBG.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Restart the video when resuming the Activity
        videoBG.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // When the Activity is destroyed, release our MediaPlayer and set it to null.
        mMediaPlayer.release();
        mMediaPlayer = null;
    }
}
