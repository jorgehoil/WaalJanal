package com.carloshoil.waaljanal.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.carloshoil.waaljanal.ABCProductoActivity;
import com.carloshoil.waaljanal.R;

import java.util.HashMap;

public class DialogConfigIng extends DialogFragment {
    Button btnGuardarConfig, btnCerrar, btnAumentaMin, btnDisminuyeMin, btnAumentaMax, btnDisminuyeMax;
    TextView tvMinimo, tvMaximo, tvInfo;
    CheckBox ckMin, ckMax;
    Context context;
    int iMaximo=0, iMinimo=0, iActual;
    HashMap<String, Integer> hashMap;

    public DialogConfigIng(Context context, HashMap<String, Integer> hashMapInfo, int iActual)
    {
        iMaximo=hashMapInfo.get("iMaximo")==null?0:hashMapInfo.get("iMaximo");
        iMinimo=hashMapInfo.get("iMinimo")==null?0:hashMapInfo.get("iMinimo");
        hashMap=hashMapInfo;
        this.context=context;
        this.iActual=iActual;
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
        View view =layoutInflater.inflate(R.layout.dialog_config_ingrediente, null);
        dialog.setView(view);
        btnAumentaMax=view.findViewById(R.id.btnAumentaMax);
        btnDisminuyeMax=view.findViewById(R.id.btnDisminuyeMax);
        btnAumentaMin=view.findViewById(R.id.btnAumentaMin);
        btnDisminuyeMin=view.findViewById(R.id.btnDisminuyeMin);
        btnGuardarConfig=view.findViewById(R.id.btnGuardaIngreConfig);
        btnCerrar=view.findViewById(R.id.btnCerrarIngreConfig);
        btnCerrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        ckMin=view.findViewById(R.id.ckConfIngMin);
        ckMax=view.findViewById(R.id.ckConfIngMax);
        tvMinimo=view.findViewById(R.id.tvMinimoIng);
        tvMaximo=view.findViewById(R.id.tvMaximoIng);
        tvInfo=view.findViewById(R.id.tvInfoConfigIng);
        btnAumentaMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMinimo++;
                tvMinimo.setText(iMinimo+"");
            }
        });
        btnAumentaMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iMaximo++;
                tvMaximo.setText(""+ iMaximo);
            }
        });
        btnDisminuyeMin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iMinimo>=1)
                {
                    iMinimo--;
                    tvMinimo.setText(iMinimo+"");
                }


            }
        });
        btnDisminuyeMax.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(iMaximo>=1)
                {
                    iMaximo--;
                    tvMaximo.setText(iMaximo+"");
                }
            }
        });
        ckMin.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!b)
            {
                iMinimo=0;
                tvMinimo.setText("0");
                btnAumentaMin.setEnabled(false);
                btnDisminuyeMin.setEnabled(false);
            }
            else {
                iMinimo=0;
                btnAumentaMin.setEnabled(true);
                btnDisminuyeMin.setEnabled(true);
            }
        });
        ckMax.setOnCheckedChangeListener((compoundButton, b) -> {
            if(!b)
            {
                iMaximo=0;
                tvMaximo.setText("0");
                btnAumentaMax.setEnabled(false);
                btnDisminuyeMax.setEnabled(false);
            }
            else {
                iMaximo=0;
                btnAumentaMax.setEnabled(true);
                btnDisminuyeMax.setEnabled(true);
            }
        });
        btnGuardarConfig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!Valida().isEmpty())
                {
                    tvInfo.setText(Valida());
                    tvInfo.setVisibility(View.VISIBLE);
                }
                else {
                    hashMap.put("iMinimo", iMinimo);
                    hashMap.put("iMaximo", iMaximo);
                    dismiss();
                }
            }
        });

        CargaDatos();
        return dialog.create();
    }
    private void CargaDatos(){
        tvMaximo.setText(iMaximo+"");
        tvMinimo.setText(iMinimo+"");
        if(iMinimo==0)
        {
            ckMin.setChecked(false);
        }
        if(iMaximo==0)
        {
            ckMax.setChecked(false);
        }

    }
    private String Valida()
    {
        String cRetorno="";
        if(iMinimo>=iActual || iMaximo>=iActual)
        {
            cRetorno="Solo tienes "+iActual+ " ingredientes registrados y activos, " +
                    "no puedes configurar un valor superior";
        }
        else {
            if(iMinimo>0)
            {
                if(iMaximo>0)
                {
                    if(iMinimo>=iMaximo)
                    {
                        cRetorno="El valor máximo debe ser mayor que el mínimo";
                    }
                }
            }
        }

        return cRetorno;
    }

}
