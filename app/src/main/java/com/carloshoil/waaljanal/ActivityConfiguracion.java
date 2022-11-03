
package com.carloshoil.waaljanal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.carloshoil.waaljanal.Adapter.CategoriasAdapter;
import com.carloshoil.waaljanal.Adapter.PersonalizacionAdapter;
import com.carloshoil.waaljanal.Adapter.ViewPagerAdapter;
import com.carloshoil.waaljanal.DTO.MenuPersonalizado;
import com.carloshoil.waaljanal.DTO.ViewPagerData;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCropActivity;

import java.io.File;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

public class ActivityConfiguracion extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceMenu;
    DatabaseReference databaseReference;
    FirebaseStorage firebaseStorage;
    StorageReference storageReferenceImagenes;
    TextView cTextPrice1, cTextPrice2, cTextPlat1, cTextPlat2, cTextDescrip1, cTextDescrip2, cTextCat, tvTituloMenu;
    String cIdMenu, cKeyImagen;
    CardView cFondo;
    LinearLayout cFondoPlat, cFondoPlat2;
    EditText edNombreRes, edHorarioRes, edDireccionRes, edTelefono;
    Button btnGuardar;
    DialogoCarga dialogoCarga;
    Uri uriImagenCarga;
    ViewPagerAdapter viewPagerAdapter;
    ViewPager2 viewPager2;
    int iPositionEdicionViewPager;
    RecyclerView recyclerViewMenuPer;
    PersonalizacionAdapter personalizacionAdapter;
    ActivityResultLauncher<String> cropImage = registerForActivityResult(new ActivityResultContracts.GetContent(), result -> {
        if(result!=null)
        {
            Intent i= new Intent(ActivityConfiguracion.this, CropperActivity.class);
            i.putExtra("imageData", result.toString());
            startActivityForResult(i, 100);

        }
        else
        {
            Toast.makeText(this, "No ha seleccionado ninguna imagen", Toast.LENGTH_SHORT).show();
        }

    });


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Init();
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
    private void Init() {
        ActivityConfiguracion.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recyclerViewMenuPer=findViewById(R.id.recycleMenuPer);
        tvTituloMenu=findViewById(R.id.tvTituloMenu);
        cFondo= findViewById(R.id.cardMenuPerFondo);
        cTextCat=findViewById(R.id.tvMenuPerCat);
        cTextPlat1=findViewById(R.id.tvMenuPerTextPlato);
        cTextPlat2=findViewById(R.id.tvMenuPerTextPlato2);
        cTextPrice1=findViewById(R.id.tvMenuPerPrice);
        cTextPrice2=findViewById(R.id.tvMenuPerPrice2);
        cTextDescrip1=findViewById(R.id.tvMenuPerDescrip);
        cTextDescrip2=findViewById(R.id.tvMenuPerDescrip2);
        cFondoPlat=findViewById(R.id.layoutMenuPerFondoPlato);
        cFondoPlat2=findViewById(R.id.layoutMenuPerFondoPlato2);
        viewPager2=findViewById(R.id.viewPagerMenu);
        edDireccionRes=findViewById(R.id.edDireccionRes);
        edHorarioRes=findViewById(R.id.edHorarioRes);
        edNombreRes=findViewById(R.id.edNombreRes);
        edTelefono=findViewById(R.id.edTelefonoRes);
        btnGuardar=findViewById(R.id.btnGuardaConfig);
        cIdMenu=getIntent().getStringExtra("cIdMenu");
        firebaseStorage=FirebaseStorage.getInstance();
        storageReferenceImagenes=firebaseStorage.getReference().child("imgmenus");
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        if(!cIdMenu.isEmpty())
            databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cIdMenu).child("info");
        else
            btnGuardar.setEnabled(false);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ValidaGuardar())
                {
                    Guardar();
                }

            }
        });
        PreparaAdapterMenuPer();
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
    private String obtenerIdImagen()
    {
        DateFormat dtForm = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
        String cDate = dtForm.format(Calendar.getInstance().getTime());
        return cIdMenu+cDate;
    }
    private void SubirImagen(String cUri) {

        String cIdImagen=(cKeyImagen.isEmpty()?obtenerIdImagen():cKeyImagen);
        if(!cUri.isEmpty())
        {
            muestradialogoCarga();
            UploadTask uploadTask= storageReferenceImagenes
                    .child(cIdMenu)
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
                            databaseReferenceMenu
                                    .child("dataImages")
                                    .child(cIdImagen)
                                    .updateChildren(data)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            ocultaDialogoCarga();
                                            if(task.isSuccessful())
                                            {
                                                cKeyImagen="";
                                                Toast.makeText(ActivityConfiguracion.this,
                                                        "Se ha subido correctamente la imagen",
                                                        Toast.LENGTH_SHORT).show();
                                                viewPagerAdapter.Actualiza(iPositionEdicionViewPager,
                                                        new ViewPagerData(true,uri.toString(), cIdImagen));
                                            }
                                            else
                                            {
                                                Global.MostrarMensaje(ActivityConfiguracion.this,
                                                        "Error"
                                                ,"Se ha presentado un error al cargar imagen");
                                            }
                                        }
                                    });
                        }
                    })).addOnFailureListener(e -> {
                        ocultaDialogoCarga();
                        Global.MostrarMensaje(ActivityConfiguracion.this, "Error",
                                "Se ha presentado un error al cargar la imagen, intenta de nuevo");
                    });
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
    private void PreparaAdapterMenuPer()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerViewMenuPer.setLayoutManager(linearLayoutManager);
        personalizacionAdapter= new PersonalizacionAdapter(this, new ArrayList<>(), this);
        recyclerViewMenuPer.setAdapter(personalizacionAdapter);

    }
    public void ConfiguraMenu(MenuPersonalizado menuPersonalizado)
    {
        //Fondo
        cFondo.setCardBackgroundColor(Color.parseColor(menuPersonalizado.cFondo));
        //cTextDescrip
        cTextDescrip1.setTextColor(Color.parseColor(menuPersonalizado.cTextDescrip));
        cTextDescrip2.setTextColor(Color.parseColor(menuPersonalizado.cTextDescrip));
        //cTextPrice
        cTextPrice1.setTextColor(Color.parseColor(menuPersonalizado.cTextPrice));
        cTextPrice2.setTextColor(Color.parseColor(menuPersonalizado.cTextPrice));
        //cTextPlat
        cTextPlat1.setTextColor(Color.parseColor(menuPersonalizado.cTextPlat));
        cTextPlat2.setTextColor(Color.parseColor(menuPersonalizado.cTextPlat));
        //cFondoPlat
        cFondoPlat.setBackgroundColor(Color.parseColor(menuPersonalizado.cFondoPlat));
        cFondoPlat2.setBackgroundColor(Color.parseColor(menuPersonalizado.cFondoPlat));
        //cCategoria
        cTextCat.setTextColor(Color.parseColor(menuPersonalizado.cTextCat));
        cTextCat.setBackgroundColor(Color.parseColor(menuPersonalizado.cFondoCat));
        //
        tvTituloMenu.setTextColor(Color.parseColor(menuPersonalizado.lOscuro?"#C8C8C8":"#2B2B2B"));

    }

    private void ConsultaMenusPersonalizados()
    {
        String cIdMenuPer= Global.RecuperaPreferencia("cIdMenuPersonal", this);
        databaseReference.child("menusperinfo").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                ocultaDialogoCarga();
                if(task.isSuccessful())
                {
                    MenuPersonalizado menuPersonalizado;
                    List<MenuPersonalizado> lstmenuPersonalizados= new ArrayList<>();
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        menuPersonalizado= new MenuPersonalizado();
                        menuPersonalizado.cKey=dataSnapshot.getKey();
                        menuPersonalizado.cNombre= dataSnapshot.child("cNombre").getValue()==null?"":
                                dataSnapshot.child("cNombre").getValue(String.class);
                        menuPersonalizado.cFondo=dataSnapshot.child("cFondo").getValue()==null?"":
                                dataSnapshot.child("cFondo").getValue(String.class);
                        menuPersonalizado.cFondoCat=dataSnapshot.child("cFondoCat").getValue()==null?"":
                                dataSnapshot.child("cFondoCat").getValue(String.class);
                        menuPersonalizado.cFondoPlat=dataSnapshot.child("cFondoPlat").getValue()==null?"":
                                dataSnapshot.child("cFondoPlat").getValue(String.class);
                        menuPersonalizado.cTextCat=dataSnapshot.child("cTextCat").getValue()==null?"":
                                dataSnapshot.child("cTextCat").getValue(String.class);
                        menuPersonalizado.cTextDescrip=dataSnapshot.child("cTextDescrip").getValue()==null?"":
                                dataSnapshot.child("cTextDescrip").getValue(String.class);
                        menuPersonalizado.cTextPlat=dataSnapshot.child("cTextPlat").getValue()==null?"":
                                dataSnapshot.child("cTextPlat").getValue(String.class);
                        menuPersonalizado.cTextPrice=dataSnapshot.child("cTextPrice").getValue()==null?"":
                                dataSnapshot.child("cTextPrice").getValue(String.class);
                        menuPersonalizado.lOscuro=dataSnapshot.child("lOscuro").getValue()==null?false:
                                dataSnapshot.child("lOscuro").getValue(boolean.class);
                        if(menuPersonalizado.cKey.equals(cIdMenuPer))
                            ConfiguraMenu(menuPersonalizado);
                        lstmenuPersonalizados.add(menuPersonalizado);

                    }

                    personalizacionAdapter.Actualiza(lstmenuPersonalizados);


                }
            }
        });

    }
    private void Guardar()
    {
        muestradialogoCarga();
        HashMap<String,Object> hashMapData= new HashMap<>();
        if(databaseReferenceMenu!=null)
        {
            hashMapData.put("cNombre", edNombreRes.getText().toString());
            hashMapData.put("cDireccion", edDireccionRes.getText().toString());
            hashMapData.put("cTelefono", edTelefono.getText().toString());
            hashMapData.put("cHorario", edHorarioRes.getText().toString());
            hashMapData.put("menuperinfo", personalizacionAdapter.obtenerSeleccionado());
            databaseReferenceMenu.updateChildren(hashMapData).addOnCompleteListener(task -> {
                ocultaDialogoCarga();
                if(task.isSuccessful())
                {
                    Global.GuardarPreferencias("cIdMenuPersonal", personalizacionAdapter.obtenerSeleccionado()==null?"":personalizacionAdapter.obtenerSeleccionado().cKey, this);
                    btnGuardar.setEnabled(true);
                    Toast.makeText(ActivityConfiguracion.this, "¡Guardado exitoso!", Toast.LENGTH_SHORT).show();
                    finish();
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

        if(databaseReferenceMenu!=null)
        {
            muestradialogoCarga();
            databaseReferenceMenu.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    if(task.isSuccessful())
                    {
                        int iAux=0;
                        ViewPagerData viewPagerData;
                        List<ViewPagerData> lstData= new ArrayList<>();
                        if(task.getResult().exists())
                        {
                            DataSnapshot dataSnapshot= task.getResult();
                            edNombreRes.setText(dataSnapshot.child("cNombre").getValue()==null?"":dataSnapshot.child("cNombre").getValue(String.class));
                            edHorarioRes.setText(dataSnapshot.child("cHorario").getValue()==null?"":dataSnapshot.child("cHorario").getValue(String.class));
                            edTelefono.setText(dataSnapshot.child("cTelefono").getValue()==null?"":dataSnapshot.child("cTelefono").getValue(String.class));
                            edDireccionRes.setText(dataSnapshot.child("cDireccion").getValue()==null?"":dataSnapshot.child("cDireccion").getValue(String.class));
                            if(dataSnapshot.child("dataImages").getValue()!=null)
                            {
                                for(DataSnapshot dataSnapshot1:dataSnapshot.child("dataImages").getChildren())
                                {
                                  viewPagerData= new ViewPagerData();
                                  viewPagerData.cKey=dataSnapshot1.getKey();
                                  viewPagerData.lUrl= dataSnapshot1.child("cUrl").getValue() != null &&
                                          !dataSnapshot1.child("cUrl").getValue(String.class).isEmpty();
                                  viewPagerData.cUrl=dataSnapshot1.child("cUrl").getValue()==null?"":
                                          dataSnapshot1.child("cUrl").getValue(String.class);
                                  lstData.add(viewPagerData);
                                }

                            }
                            iAux=lstData.size();
                           for(int i=0; i<(Values.IMAGENESMENU-iAux);i++)
                           {
                               lstData.add(new ViewPagerData(false,"loc",""));
                           }
                           cargaImagenViewPager(lstData);
                           ConsultaMenusPersonalizados();
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
    private void cargaImagenViewPager(List<ViewPagerData> listData) {
        viewPagerAdapter= new ViewPagerAdapter(listData, this, this);
        viewPager2.setAdapter(viewPagerAdapter);

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