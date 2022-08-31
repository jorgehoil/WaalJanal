package com.carloshoil.waaljanal.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.carloshoil.waaljanal.DTO.Categoria;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DialogoCategoria extends DialogFragment {
    Context context;
    Categoria categoria;
    EditText edNombreCat;
    Button btnGuardar;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceCat;


    public DialogoCategoria (Context context, Categoria categoria, String cIdMenu)
    {
        this.categoria=categoria;
        this.context=context;
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReferenceCat=firebaseDatabase.getReference().child("menus").child(cIdMenu).child("categorias");

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
        View view =layoutInflater.inflate(R.layout.dialog_abc_categoria, null);
        dialog.setView(view);
        edNombreCat=view.findViewById(R.id.edNombreCategoria);
        btnGuardar=view.findViewById(R.id.btnGuardarCategoria);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Guardar();
            }
        });
        if(categoria!=null)
        {
            edNombreCat.setText(categoria.cNombre);

        }

        return dialog.create();
    }

    private void Guardar()
    {
        Categoria categoriaGuardar=obtenerDatos();
        if(edNombreCat.getText().toString().isEmpty())
        {
            edNombreCat.setText("Campo obligatorio");
        }
        else if(categoriaGuardar.cLlave.isEmpty())
        {
            btnGuardar.setEnabled(false);
            String cLlave=databaseReferenceCat.push().getKey();
            databaseReferenceCat.child(cLlave).setValue(categoriaGuardar).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    btnGuardar.setEnabled(true);
                    if(task.isSuccessful())
                    {
                        Toast.makeText(context, "!Guardado exitoso!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    else
                    {

                        Global.MostrarMensaje(context, "Error al guardar", "Se ha presentado un error al guardar, inténtalo de nuevo");
                    }
                }
            });
        }
        else
        {
            btnGuardar.setEnabled(false);
            databaseReferenceCat.child(categoria.cLlave).setValue(categoriaGuardar).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful())
                    {

                        Toast.makeText(context, "!Guardado exitoso!", Toast.LENGTH_SHORT).show();
                        dismiss();
                    }
                    else
                    {
                        btnGuardar.setEnabled(true);
                        Global.MostrarMensaje(context, "Error al guardar", "Se ha presentado un error al guardar, inténtalo de nuevo");
                    }
                }
            });
        }

    }
    private Categoria obtenerDatos()
    {
        Categoria categoriaG= new Categoria();
        categoriaG.cLlave= categoria
                ==null?"": categoria.cLlave;
        categoriaG.cNombre=edNombreCat.getText().toString();
        return categoriaG;
    }
}
