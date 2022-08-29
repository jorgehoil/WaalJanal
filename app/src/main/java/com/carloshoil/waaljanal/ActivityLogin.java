package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivityLogin extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    Button btnIngresar;
    EditText edCorreo, edContrasena;
    DialogoCarga dialogoCarga;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        Init();
    }
    private void Init()
    {
        firebaseAuth=FirebaseAuth.getInstance();
        btnIngresar= findViewById(R.id.btnIngresar);
        edContrasena=findViewById(R.id.edContrasenia);
        edCorreo=findViewById(R.id.edCorreo);
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Login();
            }
        });
    }
    private void Login()
    {
        String cUsuario=edCorreo.getText().toString();
        String cContrasena=edContrasena.getText().toString();
        if(ValidaCampos(cUsuario, cContrasena))
        {
            abrirDialogoCarga();
            firebaseAuth.signInWithEmailAndPassword(cUsuario, cContrasena)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            cerrarDialogoCarga();
                            if(task.isSuccessful())
                            {
                                Global.GuardarPreferencias("cEstatusLogin", "1", ActivityLogin.this);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                AbrirPrincipal(user);
                            }
                            else
                            {
                                Global.MostrarMensaje(ActivityLogin.this, "Error", "Verifica tu correo y/o contraseña");
                            }
                        }
                    });
        }

    }

    private void AbrirPrincipal(FirebaseUser user) {
        if(user!=null)
        {
            Intent i= new Intent(ActivityLogin.this, MainActivity.class);
            startActivity(i);
            finish();
        }
    }

    private void abrirDialogoCarga()
    {
        dialogoCarga= new DialogoCarga(this);
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getSupportFragmentManager(), "dialogocarga");
    }
    private boolean ValidaCampos(String cUsuario, String cPassword){

        if(!cUsuario.contains("@"))
        {
            edCorreo.setError("Ingrese un correo válido");
            return false;
        }
        if(cUsuario.isEmpty())
        {
            edCorreo.setError("Este campo es obligatorio");
            return false;
        }
        if(cPassword.isEmpty())
        {
            edContrasena.setError("Este campo es obligatorio");
            return false;
        }
        return true;
    }
    private void cerrarDialogoCarga()
    {
        if(dialogoCarga!=null)
        {
            dialogoCarga.dismiss();
        }
    }

}