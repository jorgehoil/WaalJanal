package com.carloshoil.waaljanal.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

public class DialogoABCRestaurante extends DialogFragment {

    EditText edNombreRestaurante;
    Button btnGuardarRest;
    Context context;
    String cIdRest, cNombre;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceRest;
    DatabaseReference databaseReferenceMain;
    ProgressBar pbCargaRest;
    FirebaseAuth firebaseAuth;
    public DialogoABCRestaurante(Context context, String cIdRest, String cNombre)
    {
        this.context=context;
        this.cIdRest=cIdRest;
        this.cNombre=cNombre;
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        databaseReferenceRest=firebaseDatabase.getReference().child("usuarios").child(firebaseAuth.getUid()).child("adminlugares");
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
        View view =layoutInflater.inflate(R.layout.dialog_abc_menu, null);
        dialog.setView(view);
        pbCargaRest=view.findViewById(R.id.pbCargaMenu);
        edNombreRestaurante=view.findViewById(R.id.edNombreRestaurante);
        btnGuardarRest=view.findViewById(R.id.btnGuardaRestaurante);
        btnGuardarRest.setOnClickListener(view1 -> {
            String cNombre=edNombreRestaurante.getText().toString();
            if(Valida(cNombre))
            {
                Guardar(cNombre);
            }

        });
        if(!cIdRest.isEmpty())
        {
            edNombreRestaurante.setText(cNombre);
        }
        return dialog.create();
    }
    private boolean Valida(String cNombre)
    {
        if(cNombre.isEmpty())
        {
            edNombreRestaurante.setError("Campo obligatorio");
            return false;
        }
        return true;
    }
    private void Guardar(String cNombre)
    {
        btnGuardarRest.setEnabled(false);
        pbCargaRest.setVisibility(View.VISIBLE);
        if(!cIdRest.isEmpty())
        {
            databaseReferenceRest.child(cIdRest).child("cNombre").setValue(cNombre).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    btnGuardarRest.setEnabled(true);
                    pbCargaRest.setVisibility(View.INVISIBLE);
                    Toast.makeText(context, "¡Actualización correcta!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            });
        }
        else
        {
            firebaseDatabase.getReference().child("datamenu").runTransaction(new Transaction.Handler() {
                @NonNull
                @Override
                public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                   if(currentData.child("lastIdMenu").getValue(String.class)==null)
                        return Transaction.success(currentData);
                   int iValor=0;
                   String cData=currentData.child("lastIdMenu").getValue(String.class);
                   try{
                       iValor=Integer.parseInt(cData.split("-")[1]);
                   }catch (Exception ex){
                        iValor=0;
                   }
                   if(iValor>0)
                   {
                       iValor=iValor+1;
                       currentData.child("lastIdMenu").setValue("wj-"+iValor);
                       return Transaction.success(currentData);
                   }
                   else
                   {
                       return Transaction.abort();
                   }

                }

                @Override
                public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                    if(committed)
                    {
                        String cClave= currentData.child("lastIdMenu").getValue(String.class);
                        GeneraNuevoMenu(cClave);
                    }
                    else
                    {
                        pbCargaRest.setVisibility(View.INVISIBLE);
                        btnGuardarRest.setEnabled(true);
                        Global.MostrarMensaje(context, "Error", "Se ha producido un error al guardar");
                    }
                }
            });

        }


    }

    private void GeneraNuevoMenu(String cClave) {
        String cNombreRestaurante= edNombreRestaurante.getText().toString();
        HashMap<String, String> hashMapData= new HashMap<>();
        hashMapData.put("cIdMenu", cClave);
        hashMapData.put("cNombre", cNombreRestaurante);
        String cKey= databaseReferenceRest.push().getKey();
        databaseReferenceRest.child(cKey).setValue(hashMapData).addOnCompleteListener(new OnCompleteListener<Void>() {
           @Override
           public void onComplete(@NonNull Task<Void> task) {
               pbCargaRest.setVisibility(View.INVISIBLE);
               btnGuardarRest.setEnabled(true);
                if(task.isSuccessful())
                {

                    Toast.makeText(context, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else
                {
                    Global.MostrarMensaje(context, "Error", "Se ha producido un error al crear el menú, intente de nuevo");
                }
           }
       });
    }
}
