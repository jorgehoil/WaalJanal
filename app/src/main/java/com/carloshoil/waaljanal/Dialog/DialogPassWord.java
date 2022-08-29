package com.carloshoil.waaljanal.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogPassWord extends DialogFragment {
    EditText edContrasenaActual, edContrasenaNuev, edContrasenaConf;
    ProgressBar progressBarCarga;
    Button btnCambiarPass;
    Context context;
    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;


    public DialogPassWord (Context context)
    {
        this.context=context;
        firebaseAuth= FirebaseAuth.getInstance();
        user=firebaseAuth.getCurrentUser();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("usuarios");

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return creaDialogo();
    }

    private Dialog creaDialogo()
    {
        AlertDialog.Builder dialog= new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= getActivity().getLayoutInflater();
        View view =layoutInflater.inflate(R.layout.dialog_cambiocontrasena, null);
        dialog.setView(view);
        progressBarCarga=view.findViewById(R.id.pbCambioPass);
        progressBarCarga.setVisibility(View.INVISIBLE);
        edContrasenaActual=view.findViewById(R.id.edCambioPassAct);
        edContrasenaConf=view.findViewById(R.id.edCambioPassConf);
        edContrasenaNuev=view.findViewById(R.id.edCambioPassNuev);
        btnCambiarPass=view.findViewById(R.id.btnActualizarPass);
        btnCambiarPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                VerificarContrasenia();


            }
        });
        return dialog.create();
    }

    private void VerificarContrasenia() {
        if(edContrasenaNuev.getText().toString().isEmpty()||
                edContrasenaActual.getText().toString().isEmpty()||
                edContrasenaConf.getText().toString().isEmpty())
        {
            Toast.makeText(context, "Es necesario que Llene todos los campos para continuar", Toast.LENGTH_SHORT).show();
        }
        else if(!edContrasenaNuev.getText().toString().equals(edContrasenaConf.getText().toString()))
        {
            Global.MostrarMensaje(context, "Error de contraseñas", "No coinciden las " +
                    "contraseñas, verifique e intente de nuevo");

        }
        else
        {
            VerificaContrasenaActual();
        }
    }

    private void VerificaContrasenaActual() {
        if(user==null)
        {
            Toast.makeText(context, "El usuario no es valido", Toast.LENGTH_SHORT).show();
            this.dismiss();
        }
        else
        {
            progressBarCarga.setVisibility(View.VISIBLE);
            btnCambiarPass.setEnabled(false);
            AuthCredential credential= EmailAuthProvider.getCredential(user.getEmail(),
                    edContrasenaActual.getText().toString());
            user.reauthenticate(credential).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    CambiarContrasena();
                }
                else
                {
                   progressBarCarga.setVisibility(View.INVISIBLE);
                   btnCambiarPass.setEnabled(true);
                   Global.MostrarMensaje(context, "Error de contraseña", "La contraseña ingresada es incorrecta, intente de nuevo");
                }


            });
        }

    }

    private void CambiarContrasena() {
        user.updatePassword(edContrasenaNuev.getText().toString()).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                ActualizaBandera();

            }
            else
            {
                progressBarCarga.setVisibility(View.INVISIBLE);
                btnCambiarPass.setEnabled(true);
                Toast.makeText(context, "Error al cambiar la contraseña, intente de nuevo", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void ActualizaBandera()
    {
        databaseReference
                .child(user.getUid())
                .child("data")
                .child("dataconfig")
                .child("lPrimeraVez")
                .setValue(false)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                       if(task.isSuccessful())
                       {
                           progressBarCarga.setVisibility(View.INVISIBLE);
                           btnCambiarPass.setEnabled(true);
                           Toast.makeText(context, "¡Contraseña Actualizada!", Toast.LENGTH_SHORT).show();
                           dismiss();
                       }
                    }
        });
    }


}
