package com.carloshoil.waaljanal.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.R;

public class Global {
    public static void GuardarPreferencias(String cClave, String cValor, Context context)
    {
        SharedPreferences sharedPreferences= context.getSharedPreferences(context.getString(R.string.name_filepreferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putString(cClave, cValor);
        editor.commit();
    }
    public static String RecuperaPreferencia(String cClave, Context context)
    {
        String cRespuesta;
        SharedPreferences sharedPreferences=context.getSharedPreferences(context.getString(R.string.name_filepreferences), Context.MODE_PRIVATE);
        cRespuesta=sharedPreferences.getString(cClave, "");
        return cRespuesta;
    }
    public static void MostrarMensaje(Context context, String cTitulo, String cMensaje)
    {
        AlertDialog.Builder alert= new AlertDialog.Builder(context);
        alert.setTitle(cTitulo);
        alert.setMessage(cMensaje);
        alert.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        alert.show();
    }

}
