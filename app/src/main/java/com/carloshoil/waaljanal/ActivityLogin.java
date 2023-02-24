package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ActivityLogin extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    Button btnIngresar;
    EditText edCorreo, edContrasena;
    DialogoCarga dialogoCarga;
    TextView tvRegistrate;
    String cData="";
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
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        cData=getIntent().getStringExtra("cData");
        MuestraInfoSesion(cData);
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
        tvRegistrate=findViewById(R.id.tvRegistrar);
        tvRegistrate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirRegistro();
            }
        });
    }

    private void MuestraInfoSesion(String cData) {
        cData=cData==null?"":cData;
        String cTitulo, cMensaje;
        if(!cData.isEmpty())
        {
            switch (cData)
            {
                case "USER_NULL":
                    cMensaje="Se ha iniciado sesión en otro dispositivo, vuelve a iniciar sesión. Si " +
                            "no has sido tú, te recomendamos cambiar tu contraseña inmediatamente";
                    cTitulo="Sesión no válida";
                    break;
                case "VERIFY_MAIL":
                    cTitulo="Verifica tu correo electrónico";
                    cMensaje="Hemos enviado un correo a la direccion ingresada," +
                            " haz clic en el enlace adjunto a él para activar tu cuenta. " +
                            " " +
                            "Verifica todas tus bandejas, incluso las de spam.";
                    break;
                default:
                    cMensaje="Se ha producido un error, por favor vuelve a iniciar sesión";
                    cTitulo="Error de sesión";
                    break;

            }
            Global.MostrarMensaje(this, cTitulo, cMensaje);
        }


    }

    private void AbrirRegistro() {
        Intent i= new Intent(ActivityLogin.this, CreaCuentaActvity.class);
        startActivity(i);
    }

    private void Login()
    {
        String cUsuario=edCorreo.getText().toString().trim();
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
                                Global.GuardarPreferencias("cEmailId", cUsuario, ActivityLogin.this);
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if(user.isEmailVerified())
                                {
                                    ObtenerNombre(user);

                                }
                                else
                                {
                                    Global.MostrarMensaje(ActivityLogin.this, "Email no verificado", "" +
                                            "Ingresa a tu correo y haz clic en el enlace enviado. " +
                                            "Luego intenta nuevamente iniciar sesión, si el problema persiste, comunicate a" +
                                            " kookaydev@gmail.com");
                                }
                            }
                            else
                            {
                                Global.MostrarMensaje(ActivityLogin.this, "Error", "Verifica tu correo y/o contraseña");
                            }
                        }
                    });
        }

    }

    private void ObtenerNombre(FirebaseUser user) {
        databaseReference.child("usuarios")
                .child(firebaseAuth.getUid())
                .child("cNombre")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if(task.isSuccessful())
                        {
                            Global.GuardarPreferencias("cNombreUsuario", (task.getResult().getValue()==null?"":
                                    task.getResult().getValue(String.class)), ActivityLogin.this);
                            AbrirInicioSeleccion(user);
                        }
                    }
                });
    }

    private void AbrirInicioSeleccion(FirebaseUser user) {
        if(user!=null)
        {
            Intent i= new Intent(ActivityLogin.this, ActivityInicioSeleccion.class);
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