package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.HashMap;

import javax.security.auth.login.LoginException;

public class CreaCuentaActvity extends AppCompatActivity {

    private EditText edCorreo, edContrasena, edConfirmaContrasena, edNombre;
    private TextView tvTerminos;
    private Button btnCreaCuenta;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    DialogoCarga dialogoCarga;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crea_cuenta_actvity);
        Init();
    }

    private void Init()
    {
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth= FirebaseAuth.getInstance();
        tvTerminos=findViewById(R.id.tvAbrirCondiciones);
        databaseReference=firebaseDatabase.getReference();
        edCorreo=findViewById(R.id.edCorreoCuenta);
        edContrasena=findViewById(R.id.edContrasenaCuenta);
        edConfirmaContrasena=findViewById(R.id.edContrasenaConf);
        btnCreaCuenta=findViewById(R.id.btnCreaCuenta);
        edNombre=findViewById(R.id.edNomCuenta);
        tvTerminos.setOnClickListener(view -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://waaljanal.web.app/docs/terminos.pdf"));
            startActivity(browserIntent);
        });
        btnCreaCuenta.setOnClickListener(view -> {
            String cCorreo= edCorreo.getText().toString();
            String cNombre=edNombre.getText().toString();
            String cPassword=edContrasena.getText().toString();
            String cPasswordConf=edConfirmaContrasena.getText().toString();
            if(ValidaDatos(cCorreo,cNombre,cPassword,cPasswordConf))
            {
                CrearCuenta(cNombre,cCorreo,cPassword);
            }
        });

    }
    private boolean ValidaDatos(String cCorreo, String cNombre, String cPassword, String cPasswordConf)
    {
        if(cNombre.isEmpty())
        {
           Global.MostrarMensaje(this, "Ingrese su nombre", "Debe ingresar por lo menos un nombre y un apellido");
        }
        if(cPasswordConf.isEmpty()||cPassword.isEmpty()||cCorreo.isEmpty())
        {
            Global.MostrarMensaje(this, "Llene todos los campos", "Es necesario llenar todos los campos");
            return false;
        }
        if(!cPassword.equals(cPasswordConf))
        {
            Global.MostrarMensaje(this, "Error de contraseña", "Las constraseñas ingresadas no coinciden, intenta de nuevo");
            return false;
        }
        if(cPassword.length()<8)
        {
            Global.MostrarMensaje(this, "Error de contraseña", "La contraseña debe tener al menos 8 caracteres");
            return false;
        }
        if(!cCorreo.contains("@"))
        {
            Global.MostrarMensaje(this,"Error de correo", "Ingrese un correo válido");
            return false;
        }
        return true;
    }
    private void CrearCuenta(String cNombre, String cCorreo, String cContrasena)
    {
        abrirDialogoCarga();
        firebaseAuth.createUserWithEmailAndPassword(cCorreo, cContrasena)
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        CreaRegistro(cNombre);
                    }
                    else
                    {
                        cerrarDialogoCarga();
                        Global.MostrarMensaje(CreaCuentaActvity.this, "Error al crear " +
                                "cuenta", "Ha ocurrido un error al crear la cuenta. Es posible " +
                                "que este correo ya haya sido utilizado, intenta de nuevo");
                    }
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
        dialogoCarga.dismiss();
    }
    private void CreaRegistro(String cNombre) {
        HashMap<String,Object> hashMapUpdate= new HashMap<>();
        HashMap<String, Object> hashMapInfoUso= new HashMap<>();
        HashMap<String, Object> hashMapData= new HashMap<>();
        hashMapInfoUso.put("iLimiteMenus", 1);
        hashMapInfoUso.put("iTotalMenus", 0);
        hashMapInfoUso.put("iPeriodoEstatus", Values.PERIODO_INACTIVO);
        hashMapData.put("cNombre", cNombre);
        hashMapData.put("lAdmin", false);
        hashMapData.put("dataInfoUso",hashMapInfoUso);
        hashMapUpdate.put("usuarios/"+firebaseAuth.getUid(), hashMapData);
        databaseReference.updateChildren(hashMapUpdate).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                EnviarVerificacion(firebaseAuth.getCurrentUser());
            }
            else
            {
                CreaRegistro(cNombre);
            }
        });
    }

    private void EnviarVerificacion(FirebaseUser firebaseUser)
    {
        if(!firebaseUser.isEmailVerified())
        {
            firebaseUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    cerrarDialogoCarga();
                    if(task.isSuccessful())
                    {
                        IniciaLogin();
                    }
                    else
                    {
                        Global.MostrarMensaje(CreaCuentaActvity.this,"Error al enviar",
                                "Se ha presentado un error al enviar, verifica tu correo y reintenta." +
                                        "Si el problema persiste, comunícate a waaljanal@gmail.com");
                    }
                }
            });
        }
        else
        {
            cerrarDialogoCarga();
            Global.MostrarMensaje(this, "Información", "El correo "+ firebaseUser.getEmail()
                    +" ya ha sido verificado, puede iniciar sesión usando su correo creados");
        }
    }
    public void IniciaLogin()
    {
        Intent i= new Intent(CreaCuentaActvity.this, ActivityLogin.class);
        i.putExtra("cData", "VERIFY_MAIL");
        startActivity(i);

    }

}