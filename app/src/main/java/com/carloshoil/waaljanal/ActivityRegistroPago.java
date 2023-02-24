package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.renderscript.Sampler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Dialog.FragmenDatePicker;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;

import java.util.HashMap;

public class ActivityRegistroPago extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceMain;
    private EditText edReferenciaPago, edFechaPago;
    private Button btnRegistrarPago, btnSelectorFecha;
    private Spinner spNumeroMenus, spCantidadMeses;
    DialogoCarga dialogoCarga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_pago);
        Init();
    }
    private void Init()
    {
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReferenceMain=firebaseDatabase.getReference();
        firebaseAuth= FirebaseAuth.getInstance();
        edFechaPago=findViewById(R.id.edFechaPago);
        edReferenciaPago=findViewById(R.id.edReferenciaPago);
        btnRegistrarPago=findViewById(R.id.btnRegistrarPago);
        spCantidadMeses=findViewById(R.id.spCantidadMeses);
        spNumeroMenus=findViewById(R.id.spCantidadMenu);
        btnSelectorFecha=findViewById(R.id.btnAbrirSelectorFecha);
        btnRegistrarPago.setOnClickListener(v->{
            Guardar();
        });
        btnSelectorFecha.setOnClickListener(v->{
            AbrirDatePicker();
        });

    }

    private void AbrirDatePicker() {
        FragmenDatePicker fragmenDatePicker= new FragmenDatePicker(edFechaPago);
        fragmenDatePicker.show(getSupportFragmentManager(), "datepicker");
    }

    private boolean ValidaGuardar()
    {
        if(edReferenciaPago.getText().toString().isEmpty())
        {

            edReferenciaPago.setError("Este campo es obligatorio");
            return false;
        }
        if(edFechaPago.getText().toString().isEmpty())
        {
            edFechaPago.setError("Debes ingresar una fecha");
            return false;
        }
        return  true;

    }
    private void Guardar()
    {
        if(ValidaGuardar())
        {
            GeneraFolio();
        }
    }
    private void RegistrarPago(String cFolio)
    {
        HashMap<String, Object> hashMapRegPagoUser= new HashMap<>();
        HashMap<String, Object> hashMapRegPagoMain= new HashMap<>();
        HashMap<String, Object> hashMapUpdate= new HashMap<>();
        hashMapRegPagoUser.put("cFechaPago", edFechaPago.getText().toString());
        hashMapRegPagoUser.put("cReferencia", edReferenciaPago.getText().toString());
        hashMapRegPagoUser.put("iEstatus", Values.PAGO_REVISION);
        hashMapRegPagoUser.put("dateRegistro", ServerValue.TIMESTAMP);
        hashMapRegPagoUser.put("cPaquete", spNumeroMenus.getSelectedItem()+" - "+spCantidadMeses.getSelectedItem());
        hashMapRegPagoMain.put("cFechaPago",edFechaPago.getText().toString());
        hashMapRegPagoMain.put("iEstatus", Values.PAGO_REVISION);
        hashMapRegPagoMain.put("cReferencia", edReferenciaPago.getText().toString());
        hashMapRegPagoMain.put("dateRegistro", ServerValue.TIMESTAMP);
        hashMapRegPagoMain.put("cUserId", firebaseAuth.getUid());
        hashMapRegPagoMain.put("cNombreUsuario", Global.RecuperaPreferencia("cNombreUsuario", this));
        hashMapRegPagoMain.put("cCorreo", Global.RecuperaPreferencia("cEmailId", this));
        hashMapRegPagoMain.put("cPaquete", spNumeroMenus.getSelectedItem()+" - "+spCantidadMeses.getSelectedItem());
        hashMapUpdate.put("usuarios/"+ firebaseAuth.getUid()+"/registropagos/"+cFolio, hashMapRegPagoUser);
        hashMapUpdate.put("registropagos/"+cFolio, hashMapRegPagoMain);
        firebaseDatabase.getReference().updateChildren(hashMapUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ocultaDialogoCarga();
                if(task.isSuccessful())
                {
                    Toast.makeText(ActivityRegistroPago.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else {
                    Global.MostrarMensaje(ActivityRegistroPago.this, "Error al guardar"
                            , "Se ha presentado un error al guardar, intenta de nuevo");
                }

            }
        });

    }
    private void GeneraFolio()
    {
        muestradialogoCarga();
        databaseReferenceMain.child("dataPago").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if(currentData.child("lastId").getValue()==null)
                    return Transaction.success(currentData);
                currentData.child("lastId").setValue(ServerValue.increment(1));
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {
                if(committed)
                {
                    int iFolio=currentData.child("lastId").getValue()==null?0:
                            currentData.child("lastId").getValue(Integer.class);
                    RegistrarPago(iFolio+"");
                }
                else {
                    ocultaDialogoCarga();
                    Global.MostrarMensaje(ActivityRegistroPago.this, "Error al generar folio"
                            , "Se ha producido un error interno, por favor vuelve a intentarlo");
                }
            }
        });
    }
    private void muestradialogoCarga()
    {
        dialogoCarga= new DialogoCarga(this);
        dialogoCarga.show(getSupportFragmentManager(), "carga");
        dialogoCarga.setCancelable(false);
    }
    private void ocultaDialogoCarga()
    {
        dialogoCarga.dismiss();
    }
}