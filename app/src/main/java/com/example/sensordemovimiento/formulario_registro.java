package com.example.sensordemovimiento;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class formulario_registro extends AppCompatActivity {

    EditText editTextNAME, editTextEMAIL, editTextUSER, editTextPASSWORD;
    Button buttonRegistrar;
    FirebaseAuth nAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario_registro);

        editTextNAME = (EditText) findViewById(R.id.editNombre);
        editTextEMAIL = (EditText) findViewById(R.id.editEmail);
        editTextUSER = (EditText) findViewById(R.id.editUsuario);
        editTextPASSWORD = (EditText) findViewById(R.id.editClave);
        buttonRegistrar = (Button) findViewById(R.id.btnGrabaregistro);

        //Accion del boton
        buttonRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //variables de los datos a registrar
                final String name=editTextNAME.getText().toString();
                final String user=editTextUSER.getText().toString();
                final String email = editTextEMAIL.getText().toString();
                final String password=editTextPASSWORD.getText().toString();

                if(TextUtils.isEmpty(name)){
                    editTextNAME.setError("SE REQUIERE INGRESO SE SU NOMBRE");
                    return;
                }
                if(TextUtils.isEmpty(user)){
                    editTextUSER.setError("SE REQUIERE INGRESO DE UN USUARIO");
                    return;
                }
                if(TextUtils.isEmpty(email)){
                    editTextEMAIL.setError("SE REQUIERE CORREO ELECTRONICO");
                    return;
                }
                if(TextUtils.isEmpty(password)){
                    editTextPASSWORD.setError("SE REQUIERE SU CONTRASEÑA");
                    return;
                }
                //MODO DE AUTENTICACION EXIGE 6 AL MENOS 6 CARACTERES
                if(password.length()<6){
                    editTextPASSWORD.setError("Se necesita contraseña >= 6 caracteres");
                    return;
                }
            }
        });
    }
}
