package com.carloshoil.waaljanal.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.carloshoil.waaljanal.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ConcurrentModificationException;

public class DialogEstatusSuscripcion extends DialogFragment {
    TextView tvPaqueteActivo;
    TextView tvFechaVencimiento;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    Context context;
    Button btnCerrar;
    public DialogEstatusSuscripcion(Context context)
    {
        this.context=context;
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
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
        View view =layoutInflater.inflate(R.layout.dialog_suscripcion_estatus, null);
        dialog.setView(view);
        btnCerrar=view.findViewById(R.id.btnCerrarSuscripcion);
        tvFechaVencimiento=view.findViewById(R.id.tvFechaVencimientoSus);
        tvPaqueteActivo=view.findViewById(R.id.tvPaqueteSuscripcion);
        btnCerrar.setOnClickListener(v->{
            dismiss();
        });
        ConsultaDatos();
        return dialog.create();
    }
    private void ConsultaDatos()
    {
        if(firebaseAuth.getUid()!=null)
        {
            firebaseDatabase.getReference()
                    .child("usuarios")
                    .child(firebaseAuth.getUid())
                    .child("dataInfoUso")
                    .get()
                    .addOnCompleteListener(task -> {
                        if(task.isSuccessful())
                        {
                            String cFechaVencimiento=task.getResult()
                                    .child("cFechaVencimiento")
                                    .getValue()==null?"No disponible":
                                     task.getResult()
                                    .child("cFechaVencimiento")
                                    .getValue().toString();
                            String cPaquete=task.getResult()
                                    .child("cPaquete")
                                    .getValue()==null?"No disponible":
                                    task.getResult()
                                            .child("cPaquete")
                                            .getValue().toString();
                            tvFechaVencimiento.setText(cFechaVencimiento);
                            tvPaqueteActivo.setText(cPaquete);
                        }
                        else {
                            tvFechaVencimiento.setText("Error");
                            tvPaqueteActivo.setText("Error");
                        }

                    });
        }

    }
    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(android.R.color.transparent);
    }
}
