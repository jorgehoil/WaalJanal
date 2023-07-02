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

import com.carloshoil.waaljanal.DTO.Ingrediente;
import com.carloshoil.waaljanal.FragmentExtras;
import com.carloshoil.waaljanal.R;

public class DialogABCIngrediente extends DialogFragment {
    EditText edNombreIng, edPrecioIng;
    CheckBox ckPrecioAdicional;
    Context context;
    Button btnGuardar, btnCancelar;
    String cTipo;
    Ingrediente ingrediente;
    FragmentExtras fe;
    public DialogABCIngrediente(Context context, Ingrediente ingrediente, String cTipo, FragmentExtras fe)
    {
        this.context=context;
        this.cTipo=cTipo;
        this.ingrediente=ingrediente;
        this.fe=fe;
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
        View view =layoutInflater.inflate(R.layout.dialog_abc_ingrediente, null);
        dialog.setView(view);
        edNombreIng =view.findViewById(R.id.edNombreIng);
        edPrecioIng=view.findViewById(R.id.edPrecioIng);
        ckPrecioAdicional=view.findViewById(R.id.ckCostoAdicionalIng);
        btnGuardar=view.findViewById(R.id.btnGuardarIng);
        btnCancelar=view.findViewById(R.id.btnCancelarIng);
        btnGuardar.setOnClickListener(v->{
           /* if(ValidaGuardado(ObtenerIngrediente()))
            {
                Toast.makeText(context, "Guardado!", Toast.LENGTH_SHORT).show();
            }*/
            fe.MuestraMensaje();
        });
        btnCancelar.setOnClickListener(v->{
            dismiss();
        });
        CargaDatos(ingrediente);
        return dialog.create();
    }

    private Ingrediente ObtenerIngrediente() {
        Ingrediente ingredienteR= new Ingrediente();
        ingredienteR.cKey=ingrediente==null?"": ingrediente.cKey;
        ingredienteR.cNombre=edNombreIng.getText().toString().trim();
        ingredienteR.cPrecio=edPrecioIng.getText().toString().trim();
        ingredienteR.lPrecioAdicional=ckPrecioAdicional.isChecked();
        return ingredienteR;
    }

    private boolean ValidaGuardado(Ingrediente ingrediente)
    {
        if(ingrediente.lPrecioAdicional)
        {
            if(ingrediente.cPrecio.isEmpty())
            {
                edPrecioIng.setText("Debes ingresar un precio");
                return false;
            }
        }
        if(ingrediente.cNombre.isEmpty())
        {
            edNombreIng.setError("Debes ingresar un nombre");
            return false;
        }
        return true;
    }

    private void CargaDatos(Ingrediente ingrediente) {
        if(cTipo.equals("M"))
        {
            edNombreIng.setText(ingrediente.cNombre);
            edPrecioIng.setText(ingrediente.cPrecio);
            ckPrecioAdicional.setEnabled(ingrediente.lPrecioAdicional);
        }
    }
}
