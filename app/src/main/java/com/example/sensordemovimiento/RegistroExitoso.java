package com.example.sensordemovimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegistroExitoso extends AppCompatActivity {

    private Button button, btnDatos;
    //cerrar sesion en Firebase
    private FirebaseAuth nAuth;
    //TextView para mostrar informacion del usuario
    private TextView nTextViewNombre,nTextViewEmail;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_exitoso);
        //intanciando variables de tipo textview
        nTextViewNombre=(TextView)findViewById(R.id.textViewNombre);
        nTextViewEmail=(TextView)findViewById(R.id.textViewEmail);
        mDatabase= FirebaseDatabase.getInstance().getReference();

        //instanciando nAuth
        nAuth=FirebaseAuth.getInstance();
        //btnDatos=(Button)findViewById(R.id.btnEstadoTanque);
       /* btnDatos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegistroExitoso.this, PantallaDatos.class));

            }
        });*/

        button=(Button)findViewById(R.id.btnCerrarSesion);
        //implementacion de logOut
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nAuth.signOut();
                startActivity(new Intent(RegistroExitoso.this,MainActivity.class));
                finish();

            }
        });
        obtenerInformacion();
    }

    private void obtenerInformacion(){
        String id=nAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String name=dataSnapshot.child("name").getValue().toString();
                    String email=dataSnapshot.child("email").getValue().toString();

                    nTextViewNombre.setText(name);
                    nTextViewEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
