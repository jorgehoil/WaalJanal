
package com.carloshoil.waaljanal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.yalantis.ucrop.UCropActivity;

import java.net.URI;
import java.util.HashMap;

public class ActivityConfiguracion extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String cIdMenu;
    EditText edNombreRes, edHorarioRes, edDireccionRes, edTelefono;
    Button btnGuardar;
    ImageView imgImagenRes;
    String cUrlImagen;
    ActivityResultLauncher<String> cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        Intent i= new Intent(ActivityConfiguracion.this, CropperActivity.class);
        i.putExtra("imageData", result.toString());
        startActivityForResult(i, 100);

    });


    boolean lImagen=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Init();
    }

    private void iniciarCropper() {

    }
    private void solicitaPermiso() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 400);
    }
    private boolean checkPermiso()
    {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 400) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (!writeStorage && !readStorage) {
                    Toast.makeText(this, "Es necesario otorgar todos los permisos para cargar una imagen", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    private void Init() {
        ActivityConfiguracion.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        imgImagenRes= findViewById(R.id.ivFotoRes);
        edDireccionRes=findViewById(R.id.edDireccionRes);
        edHorarioRes=findViewById(R.id.edHorarioRes);
        edNombreRes=findViewById(R.id.edNombreRes);
        edTelefono=findViewById(R.id.edTelefonoRes);
        imgImagenRes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkPermiso())
                {
                    cropImage.launch("image/*");
                }
                else
                {
                    solicitaPermiso();
                }
            }
        });
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
    private void SubirImagen()
    {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==101)
        {
            String result=data.getStringExtra("CROP");
            Uri uri=data.getData();
            if(result!=null)
            {
                uri=Uri.parse(result);
                imgImagenRes.setImageURI(uri);
            }
        }

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
            hashMapData.put("cUrlImagen", "");
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