package com.carloshoil.waaljanal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.DTO.ViewPagerData;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
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

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ABCProductoActivity extends AppCompatActivity {
    String cIdMenu="", cUrlImagen, cUrlMin, cUriImagen;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceMenu;
    StorageReference storageReference;
    EditText edNombreProducto, edPrecioProducto, edDescripconProd;
    Spinner spCategorias;
    DialogoCarga dialogoCarga;
    List<String> lstCategorias;
    List<String> lstIdCategorias;
    String cIdProducto, cIdCategoriaSel;
    Producto productoG;
    ImageView ivProducto;
    boolean lImagenSubida=false;
    private CheckBox ckPublicar;
    private String CCLAVEMENU="cIdMenu";

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
            Intent i= new Intent(ABCProductoActivity.this, CropperActivity.class);
            i.putExtra("imageData", result.toString());
            i.putExtra("iWidth", 960);
            i.putExtra("iHeight", 960);
            i.putExtra("iX", 9);
            i.putExtra("iY", 9);
            i.putExtra("iPorcentaje", 15);
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
        setContentView(R.layout.activity_abcproducto);
        ABCProductoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Init();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void Init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cIdProducto=getIntent().getStringExtra("cIdProducto")==null?"":getIntent().getStringExtra("cIdProducto");
        cIdCategoriaSel=getIntent().getStringExtra("cIdCatSel")==null?"":getIntent().getStringExtra("cIdCatSel");
        lstCategorias= new ArrayList<>();
        lstIdCategorias= new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        edNombreProducto= findViewById(R.id.edNombreProducto);
        edPrecioProducto= findViewById(R.id.edPrecioProducto);
        edDescripconProd=findViewById(R.id.edDescripcionProducto);
        ivProducto=findViewById(R.id.ivProducto);
        ckPublicar=findViewById(R.id.ckPublicar);
        spCategorias= findViewById(R.id.spCategorias);
        cIdMenu=Global.RecuperaPreferencia(CCLAVEMENU,this);
        ivProducto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!checkPermiso())
                {
                    solicitaPermiso();
                }
                else {
                    cropImage.launch("image/*");
                }
            }
        });
        if(!cIdMenu.isEmpty())
        {
            storageReference= FirebaseStorage.getInstance().getReference();
            databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cIdMenu);

            addMenuProvider(new MenuProvider() {
                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menuInflater.inflate(R.menu.menu_guardar, menu);
                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    int iId= menuItem.getItemId();
                    if(iId==R.id.itemGuardar)
                    {
                        GuardarDatos();
                    }
                    return  false;
                }
            });
            if(!cIdProducto.isEmpty())
            {
                ObtenerEdicion();
            }
            else
            {
                ObtenerCategorias();
            }
        }

    }

    private void cargaDatosProducto()
    {
        if(productoG!=null)
        {
            edNombreProducto.setText(productoG.cNombre);
            edPrecioProducto.setText(productoG.cPrecio);
            edDescripconProd.setText(productoG.cDescripcion);
            ckPublicar.setChecked(productoG.lDisponible);
            cUrlMin=productoG.cUrlImagenMin;
            cUrlImagen=productoG.cUrlImagen;
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

    private void ObtenerEdicion() {
        abrirDialogoCarga();
        databaseReferenceMenu.child("productos").child(cIdProducto).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    cerrarDialogoCarga();
                    DataSnapshot dataSnapshot=task.getResult();
                    productoG= new Producto();
                    productoG.cLlave= dataSnapshot.getKey();
                    productoG.cNombre=dataSnapshot.child("cNombre").getValue()==null?"":dataSnapshot.child("cNombre").getValue().toString();
                    productoG.cDescripcion=dataSnapshot.child("cDescripcion").getValue()==null?"": dataSnapshot.child("cDescripcion").getValue().toString();
                    productoG.cPrecio=dataSnapshot.child("cPrecio").getValue()==null?"":dataSnapshot.child("cPrecio").getValue().toString();
                    productoG.cIdCategoria=dataSnapshot.child("cIdCategoria").getValue()==null?"":dataSnapshot.child("cIdCategoria").getValue().toString();
                    productoG.cUrlImagen=dataSnapshot.child("cUrlImagen").getValue()==null?"":dataSnapshot.child("cUrlImagen").getValue().toString();
                    productoG.cUrlImagenMin=dataSnapshot.child("cUrlImagenMin").getValue()==null?"":dataSnapshot.child("cUrlImagenMin").getValue().toString();
                    productoG.lDisponible=dataSnapshot.child("lDisponible").getValue()==null?false:dataSnapshot.child("lDisponible").getValue(boolean.class);
                    if(!productoG.cUrlImagen.isEmpty())
                    {
                        Picasso.get().load(productoG.cUrlImagen).placeholder(R.drawable.ic_time).into(ivProducto);
                    }
                    else {
                        ivProducto.setImageDrawable(getDrawable(R.drawable.add_photo));
                    }
                    ObtenerCategorias();
                }

            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100&&resultCode==101)
        {
            lImagenSubida=true;
            cUriImagen=data.getStringExtra("CROP");
            ivProducto.setImageURI(null);
            ivProducto.setImageURI(Uri.parse(cUriImagen));
        }

    }

    private void GuardarDatos()
    {
        String cMenu="menus/"+cIdMenu+"/";
        HashMap<String, Object> updates= new HashMap<>();
        if(ValidarDatos(edNombreProducto.getText().toString(), edPrecioProducto.getText().toString())){
            abrirDialogoCarga();
            Producto producto= obtenerProductoGuardar();
            if(cIdProducto.isEmpty())//NUEVO PRODUCTO
            {
                String cLlave= databaseReferenceMenu.child("productos").push().getKey();
                updates.put(cMenu+"/productos/"+cLlave,producto);
                if(producto.lDisponible)
                {
                    updates.put(cMenu+"/menu_publico/"+ObtenerIdCatSeleccionada()+"/"+cLlave,producto);
                }
                firebaseDatabase.getReference().updateChildren(updates).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        producto.cLlave=cLlave;
                        productoG=producto;
                        if(lImagenSubida)
                        {
                            SubirImagen();
                        }
                        else {
                            cerrarDialogoCarga();
                            limpiarCampos();
                            Toast.makeText(ABCProductoActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
            else //ACTUALIZAR PRODUCTO
            {
                updates.put(cMenu+"/productos/"+cIdProducto,producto);
                if(productoG.cIdCategoria.equals(ObtenerIdCatSeleccionada()))//SI NO CAMBIA DE CATEGORIA
                {
                    if(!producto.lDisponible){
                        updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto, null);
                    }
                    else
                    {
                        updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto,producto);
                    }
                }
                else //CAMBIO DE CATEGORIA
                {
                    updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto,null);
                    if(productoG.lDisponible)
                    {
                        updates.put(cMenu+"/menu_publico/"+ObtenerIdCatSeleccionada()+"/"+cIdProducto,producto);
                    }
                }
                firebaseDatabase.getReference().updateChildren(updates).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        if(lImagenSubida)
                        {
                            SubirImagen();
                        }
                        else {
                            cerrarDialogoCarga();
                            limpiarCampos();
                            finish();
                            Toast.makeText(ABCProductoActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
                        Global.MostrarMensaje(ABCProductoActivity.this,
                                "Error al guardar",
                                "Se ha presentado un error al guardar, intenta de nuevo");
                    }
                });
            }

        };

    }

    private void limpiarCampos() {
        edNombreProducto.setText("");
        edPrecioProducto.setText("");
        edDescripconProd.setText("");
        ivProducto.setImageURI(null);
        ivProducto.setImageDrawable(getDrawable(R.drawable.ic_image));
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
    private Producto obtenerProductoGuardar()
    {
        Producto producto= new Producto();
        producto.cNombre=edNombreProducto.getText().toString();
        producto.lDisponible=ckPublicar.isChecked();
        producto.cUrlImagen=cUrlImagen;
        producto.cUrlImagenMin=cUrlMin;
        producto.cLlave=cIdProducto;
        producto.cIdCategoria=ObtenerIdCatSeleccionada();
        producto.cPrecio=edPrecioProducto.getText().toString();
        producto.cDescripcion=edDescripconProd.getText().toString();
        return producto;
    }
    private String ObtenerIdCatSeleccionada()
    {
        int position=spCategorias.getSelectedItemPosition();
        return lstIdCategorias.get(position);
    }
    private void ObtenerCategorias()
    {
        databaseReferenceMenu.child("categorias").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    String cIdCat;
                    String cCat;
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        cIdCat=dataSnapshot.getKey();
                        cCat=dataSnapshot.child("cNombre").getValue().toString();
                        lstIdCategorias.add(cIdCat);
                        lstCategorias.add(cCat);
                    }
                    if(lstCategorias.size()==0)
                    {
                        Toast.makeText(ABCProductoActivity.this, "No existe categorias, regístrelos en la sección correspondiente", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        CargaCategorias(lstCategorias);
                        cargaDatosProducto();
                        SeleccionaCategoria();
                    }
                }
                else
                {
                    ObtenerCategorias();
                }
            }
        });
    }

    private void SeleccionaCategoria() {
        String cIdCategoria=cIdProducto.isEmpty()?cIdCategoriaSel:productoG.cIdCategoria;
        int iPosition=0;
        int i=0;
        for(String cIdCat: lstIdCategorias)
        {
            if(cIdCat.equals(cIdCategoria))
            {
                iPosition=i;
            }
            i++;
        }
        spCategorias.setSelection(iPosition);


    }

    private void CargaCategorias(List<String> lstCategorias) {
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, lstCategorias);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(arrayAdapter);
    }

    private boolean ValidarDatos(String cNombre, String cPrecio)
    {
        if(cNombre.isEmpty())
        {
            edNombreProducto.setError("Este campo es obligatorio");
            return false;
        }
        if(cPrecio.isEmpty())
        {
            edPrecioProducto.setError("Este campo es obligatorio");
            return  false;
        }
    return true;

    }
    private void abrirDialogoCarga()
    {
        Log.d("DEBUG", "abrirDialogoCarga");
        dialogoCarga= new DialogoCarga(this);
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getSupportFragmentManager(), "dialogocarga");
    }
    private void cerrarDialogoCarga()
    {
        if(dialogoCarga!=null)
        {
            dialogoCarga.dismiss();
        }
    }
    private void SubirImagen()
    {
        if(!cUriImagen.isEmpty())
        {
            UploadTask uploadTask= storageReference
                    .child("imgproductos")
                    .child(cIdMenu)
                    .child(productoG.cLlave +".jpg")
                    .putFile(Uri.parse(cUriImagen));
            uploadTask.addOnSuccessListener(taskSnapshot -> {
                taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                    productoG.cUrlImagen=uri.toString();
                    SubirImagenMin();
                });
            }).addOnFailureListener(e -> {
                cerrarDialogoCarga();
                Global.MostrarMensaje(this, "Error", "Se ha presentado " +
                        "un error al subir la imagen del producto, intenta de nuevo");
            });

        }
    }

    private void ActualizaRutasImagen()
    {
       HashMap<String, Object> hashMapUpdate= new HashMap<>();
       hashMapUpdate.put("menus/"+ cIdMenu+ "/productos/"+ productoG.cLlave, productoG);
       if(productoG.lDisponible)
            hashMapUpdate.put("menus/"+ cIdMenu+ "/menu_publico/"+ productoG.cIdCategoria+ "/"+ productoG.cLlave+ "/", productoG);
       firebaseDatabase.getReference().updateChildren(hashMapUpdate).addOnCompleteListener(task -> {
           cerrarDialogoCarga();
           if(task.isSuccessful())
           {
               Toast.makeText(ABCProductoActivity.this, "¡Guardado exitoso!", Toast.LENGTH_SHORT).show();
               if(cIdProducto.isEmpty())//SI ES NUEVO
               {
                   limpiarCampos();
               }
               else {
                   finish();
               }
           }

           else
           {
               Global.MostrarMensaje(ABCProductoActivity.this,"Error", "Se ha presentado un error " +
                       "al actualizar rutas de imagen del producto "+ task.getException());
           }
       });


    }
    private void SubirImagenMin()
    {
        Bitmap bitmap = ((BitmapDrawable) ivProducto.getDrawable()).getBitmap();
        Bitmap bitMapFinal;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitMapFinal=Bitmap.createScaledBitmap(bitmap, 70, 70,false);
        bitMapFinal.compress(Bitmap.CompressFormat.JPEG, 95, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask= storageReference
                .child("imgproductosmin")
                .child(cIdMenu)
                .child(productoG.cLlave+".jpg")
                .putBytes(data);
        uploadTask.addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    productoG.cUrlImagenMin=uri.toString();
                    ActualizaRutasImagen();
                }
            });
        }).addOnFailureListener(e -> {
            cerrarDialogoCarga();
            Global.MostrarMensaje(this, "Error", "Se ha presentado " +
                    "un error al subir la imagen del producto, intenta de nuevo");
        });
    }
}