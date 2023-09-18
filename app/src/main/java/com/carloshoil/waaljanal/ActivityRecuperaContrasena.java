package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ActivityRecuperaContrasena extends AppCompatActivity {
    Button btnRecuperacion;
    EditText edCorreo;
    FirebaseAuth firebaseAuth;
    DialogoCarga dialogoCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recupera_contrasena);
        btnRecuperacion=findViewById(R.id.btnEnviarCorreoRecu);
        edCorreo=findViewById(R.id.edCorreoRecup);
        firebaseAuth=FirebaseAuth.getInstance();
        btnRecuperacion.setOnClickListener(v->{
            PreparaEnviarCorreo();
        });
    }
    private void abrirDialogoCarga()
    {
        dialogoCarga= new DialogoCarga(this);
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getSupportFragmentManager(), "dialogocarga");
    }
    private void cerrarDialogoCarga()
    {
        Log.d("DEBUG", "Se cierra dialogo carga");
        if(dialogoCarga!=null)
        {
            dialogoCarga.dismiss();
        }
    }
    private void PreparaEnviarCorreo()
    {
        String cCorreo=edCorreo.getText().toString();
        if(ValidaCorreo(cCorreo))
        {
            EnviaCorreoVerificacion(cCorreo);
        }

    }
    private void EnviaCorreoVerificacion(String cCorreo)
    {
        abrirDialogoCarga();
        firebaseAuth.sendPasswordResetEmail(cCorreo).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                cerrarDialogoCarga();
                if(task.isSuccessful())
                {
                    edCorreo.setText("");
                    btnRecuperacion.setEnabled(false);
                    Global.MostrarMensaje(ActivityRecuperaContrasena.this, "Revisa tu correo",
                            "Hemos enviado un enlace en el correo ingresado," +
                                    " ingresa, revisa tus bandejas y restaura tu contraseña.");
                }
                else {
                    Global.MostrarMensaje(ActivityRecuperaContrasena.this, "Error",
                            "Se ha producido un error. Es posible que hayas intentado muchas veces o que el " +
                                    "correo ingresado no este registrado." +
                                    " Verifica y reintenta, si el problema persiste, contáctanos.");
                }
            }
        });
    }
    private boolean ValidaCorreo(String cCorreo)
    {
        if(cCorreo.trim().isEmpty())
        {
            edCorreo.setError("Debes ingresar tu correo");
            return false;
        }
        if(!cCorreo.contains("@"))
        {
            edCorreo.setError("Ingresa un correo válido");
            return false;
        }
        return true;

    }
}