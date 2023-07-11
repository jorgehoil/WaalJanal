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

import com.carloshoil.waaljanal.DTO.Variedad;
import com.carloshoil.waaljanal.FragmentVariedades;
import com.carloshoil.waaljanal.R;

public class DialogABCVariedad  extends DialogFragment {

    EditText edNombreVariedad,edPrecio;
    CheckBox ckDisponible;
    Button btnGuardar, btnCancelar;
    Variedad variedad;
    Context context;
    String cTipo;
    FragmentVariedades fv;
    public DialogABCVariedad(Context context, Variedad variedad, String cTipo, FragmentVariedades fv)
    {
        this.context=context;
        this.variedad=variedad;
        this.cTipo=cTipo;
        this.fv=fv;
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
        View view =layoutInflater.inflate(R.layout.dialog_abc_variedad, null);
        dialog.setView(view);
        edNombreVariedad=view.findViewById(R.id.edNombreVariedad);
        edPrecio=view.findViewById(R.id.edPrecioVar);
        ckDisponible=view.findViewById(R.id.ckDisponibleVar);
        btnGuardar=view.findViewById(R.id.btnGuardarABCVar);
        btnGuardar.setOnClickListener(v->{
            if(ValidaGuardado(ObtenerVariedad()))
            {
                LimpiaCampos();
                fv.agregaVariedad(ObtenerVariedad());
            }
        });
        btnCancelar=view.findViewById(R.id.btnCancelarABCVar);
        btnCancelar.setOnClickListener(v->{
            dismiss();
        });
        CargaDatos(variedad);
        return dialog.create();
    }

    private void LimpiaCampos() {
        edPrecio.setText("");
        edNombreVariedad.setText("");
    }

    private Variedad ObtenerVariedad()
    {
        Variedad variedadR= new Variedad();
        variedadR.cKey=variedad==null?"":variedad.cKey;
        variedadR.cNombre=edNombreVariedad.getText().toString();
        variedadR.cPrecio=edPrecio.getText().toString();
        variedadR.lDisponible=ckDisponible.isChecked();
        return variedadR;
    }
    private void CargaDatos(Variedad variedad) {
        if(cTipo.equals("M"))
        {
            edNombreVariedad.setText(variedad.cNombre);
            edPrecio.setText(variedad.cPrecio);
            ckDisponible.setChecked(variedad.lDisponible);
        }
    }
    private boolean ValidaGuardado(Variedad variedadL)
    {
        if(variedadL.cNombre.trim().isEmpty())
        {
            edNombreVariedad.setError("Debes ingresar un nombre");
            return false;
        }
        if(variedadL.cPrecio.trim().isEmpty())
        {
            edPrecio.setError("Debes ingresar un precio");
            return false;
        }
        return true;

    }
}
