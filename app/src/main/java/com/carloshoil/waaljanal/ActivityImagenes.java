package com.carloshoil.waaljanal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.ViewPagerAdapter;
import com.carloshoil.waaljanal.DTO.ViewPagerData;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ActivityImagenes extends AppCompatActivity {

    private ViewPagerAdapter viewPagerAdapter;
    private ViewPager2 viewPager2;
    String cIdMenuG;
    int iPositionEdicionViewPager;
    DialogoCarga dialogoCarga;
    String cKeyImagen;
    StorageReference storageReferenceImagenes;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceImagenesMenu;
    FirebaseStorage firebaseStorage;
    Button btnCerrarImagenes;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagenes);
        Init();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    private void Init()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager2= findViewById(R.id.viewPagerImagenes);
        cIdMenuG=getIntent().getStringExtra("cIdMenu");
        btnCerrarImagenes=findViewById(R.id.btnCerrarImagenes);
        btnCerrarImagenes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        Log.d("DEBUG", cIdMenuG);
        if(!cIdMenuG.isEmpty())
        {
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReferenceImagenesMenu=firebaseDatabase.getReference()
                    .child("menus")
                    .child(cIdMenuG)
                    .child("info")
                    .child("dataImages");
            firebaseStorage=FirebaseStorage.getInstance();
            storageReferenceImagenes=firebaseStorage.getReference().child("imgmenus");
            CargaImagenes();
        }else{
            Toast.makeText(this, "No existe menu relacionado, favor de seleccionar", Toast.LENGTH_SHORT).show();
            finish();
        }


    }
    private ActivityResultLauncher<String[]> permission= registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranded-> {
        if (!isGranded.containsValue(false)) {

        }
        else
        {
            Toast.makeText(this, "Es necesario otorgar todos los permisos", Toast.LENGTH_SHORT).show();
        }
    });
    private ActivityResultLauncher<String> cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if(result!=null)
        {
            Intent i= new Intent(ActivityImagenes.this, CropperActivity.class);
            i.putExtra("imageData", result.toString());
            startActivityForResult(i, 100);

        }
        else
        {
            Toast.makeText(this, "No ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }

    });
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

    private boolean checkPermiso()
    {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
    }
    private void solicitaPermiso() {
        permission.launch(new String[] {android.Manifest.permission.READ_EXTERNAL_STORAGE,android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
    }
    private String obtenerIdImagen()
    {
        DateFormat dtForm = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String cDate = dtForm.format(Calendar.getInstance().getTime());
        return cIdMenuG+cDate;
    }

    private void SubirImagen(String cUri) {

        String cIdImagen=(cKeyImagen.isEmpty()?obtenerIdImagen():cKeyImagen);
        if(!cUri.isEmpty())
        {
            muestradialogoCarga();
            UploadTask uploadTask= storageReferenceImagenes
                    .child(cIdMenuG)
                    .child(cIdImagen+".jpg")
                    .putFile(Uri.parse(cUri));
            uploadTask.addOnSuccessListener(taskSnapshot -> taskSnapshot
                    .getStorage()
                    .getDownloadUrl()
                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            HashMap<String, Object> data= new HashMap<>();
                            data.put("cUrl",uri.toString());
                                    databaseReferenceImagenesMenu
                                    .child(cIdImagen)
                                    .updateChildren(data)
                                    .addOnCompleteListener(task -> {
                                        ocultaDialogoCarga();
                                        if(task.isSuccessful())
                                        {
                                            cKeyImagen="";
                                            Toast.makeText(ActivityImagenes.this,
                                                    "Se ha subido correctamente la imagen",
                                                    Toast.LENGTH_SHORT).show();
                                            viewPagerAdapter.Actualiza(iPositionEdicionViewPager,
                                                    new ViewPagerData(true,uri.toString(), cIdImagen));
                                        }
                                        else
                                        {
                                            Global.MostrarMensaje(ActivityImagenes.this,
                                                    "Error"
                                                    ,"Se ha presentado un error al cargar imagen, intenta de nuevo");
                                        }
                                    });
                        }
                    })).addOnFailureListener(e -> {
                ocultaDialogoCarga();
                Global.MostrarMensaje(ActivityImagenes.this, "Error",
                        "Se ha presentado un error al cargar la imagen, intenta de nuevo");
            });
        }
    }

    public void abrirCropperViewPager(String cKey, int iPosition)
    {
        if(checkPermiso())
        {
            cropImage.launch("image/*");
            iPositionEdicionViewPager=iPosition;
            cKeyImagen=cKey;
        }
        else
        {
            solicitaPermiso();
        }


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==101)
        {
            String cUri=data.getStringExtra("CROP");
            SubirImagen(cUri);
        }

    }
    private void cargaImagenViewPager(List<ViewPagerData> listData) {
        viewPagerAdapter= new ViewPagerAdapter(listData, this, this);
        viewPager2.setAdapter(viewPagerAdapter);

    }

    private void CargaImagenes()
    {

        databaseReferenceImagenesMenu.get().addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {

                int iAux=0;
                ViewPagerData viewPagerData;
                List<ViewPagerData> lstData= new ArrayList<>();
                for (DataSnapshot dataSnapshot1 : task.getResult().getChildren()) {
                    Log.d("DEBUG", "Se carga imagenes");
                    viewPagerData = new ViewPagerData();
                    viewPagerData.cKey = dataSnapshot1.getKey();
                    viewPagerData.lUrl = dataSnapshot1.child("cUrl").getValue() != null &&
                            !dataSnapshot1.child("cUrl").getValue(String.class).isEmpty();
                    viewPagerData.cUrl = dataSnapshot1.child("cUrl").getValue() == null ? "" :
                            dataSnapshot1.child("cUrl").getValue(String.class);
                    lstData.add(viewPagerData);
                }
                iAux=lstData.size();
                for(int i = 0; i<(Values.IMAGENESMENU-iAux); i++)
                {
                    lstData.add(new ViewPagerData(false,"loc",""));
                }
                Log.d("DEBUG", "presente");
                cargaImagenViewPager(lstData);

            }
            else {
                CargaImagenes();
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