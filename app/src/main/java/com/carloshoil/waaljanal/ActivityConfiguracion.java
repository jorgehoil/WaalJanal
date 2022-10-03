
package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class ActivityConfiguracion extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String cIdMenu;
    EditText edNombreRes, edHorarioRes, edDireccionRes, edTelefono;
    Button btnGuardar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Init();
    }

    private void Init() {
        ActivityConfiguracion.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        edDireccionRes=findViewById(R.id.edDireccionRes);
        edHorarioRes=findViewById(R.id.edHorarioRes);
        edNombreRes=findViewById(R.id.edNombreRes);
        edTelefono=findViewById(R.id.edTelefonoRes);
        btnGuardar=findViewById(R.id.btnGuardaConfig);
        cIdMenu=getIntent().getStringExtra("cIdMenu");
        firebaseDatabase=FirebaseDatabase.getInstance();
        if(!cIdMenu.isEmpty())
            databaseReference=firebaseDatabase.getReference().child("menus").child(cIdMenu).child("info");
        else
            btnGuardar.setEnabled(false);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidaGuardar())
                    Guardar();
            }
        });
        ConsultaDatos();
    }

    private boolean ValidaGuardar()
    {
        if(edNombreRes.getText().toString().isEmpty())
        {
            edNombreRes.setError("Es necesario llenar este campo");
            return false;
        }
        return  true;
    }
    private void Guardar()
    {

        btnGuardar.setEnabled(false);
        HashMap<String,Object> hashMapData= new HashMap<>();
        if(databaseReference!=null)
        {

            hashMapData.put("cNombre", edNombreRes.getText().toString());
            hashMapData.put("cDireccion", edDireccionRes.getText().toString());
            hashMapData.put("cTelefono", edTelefono.getText().toString());
            hashMapData.put("cHorario", edHorarioRes.getText().toString());
            databaseReference.setValue(hashMapData).addOnCompleteListener(task -> {
                if(task.isSuccessful())
                {
                    btnGuardar.setEnabled(true);
                    Toast.makeText(ActivityConfiguracion.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    Global.MostrarMensaje(ActivityConfiguracion.this, "Error", "Se ha producido un error al guardar");
                }
            });
        }

    }
    private void ConsultaDatos()
    {
        if(databaseReference!=null)
        {
            databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        if(task.getResult().exists())
                        {
                            DataSnapshot dataSnapshot= task.getResult();
                            edNombreRes.setText(dataSnapshot.child("cNombre")==null?"":dataSnapshot.child("cNombre").getValue(String.class));
                            edHorarioRes.setText(dataSnapshot.child("cHorario")==null?"":dataSnapshot.child("cHorario").getValue(String.class));
                            edTelefono.setText(dataSnapshot.child("cTelefono")==null?"":dataSnapshot.child("cTelefono").getValue(String.class));
                            edDireccionRes.setText(dataSnapshot.child("cDireccion")==null?"":dataSnapshot.child("cDireccion").getValue(String.class));
                        }
                        else
                        {
                            Toast.makeText(ActivityConfiguracion.this, "No existen datos registrados", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Toast.makeText(ActivityConfiguracion.this, "Error al consultar", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


}