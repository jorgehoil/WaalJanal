package com.carloshoil.waaljanal;


import android.app.Activity;
//import android.app.Fragment;
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
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.carloshoil.waaljanal.Adapter.ViewPager2Adapter;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.DTO.Variedad;
import com.carloshoil.waaljanal.Dialog.DialogABCIngrediente;
import com.carloshoil.waaljanal.Dialog.DialogABCVariedad;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
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
    String cIdMenu="", cUrlImagen="", cUrlMin="", cUriImagen, cIdProducto, cIdCategoriaSel, cLlaveG;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceMenu;
    StorageReference storageReference;
    EditText edNombreProducto, edPrecioProducto, edDescripconProd;
    Spinner spCategorias;
    DialogoCarga dialogoCarga;
    List<String> lstCategorias;
    List<String> lstIdCategorias;
    Producto productoG;
    ImageView ivProducto;
    private CheckBox ckPublicar;
    private String CCLAVEMENU="cIdMenu";
    private FragmentProductos fragmentProductos;
    private boolean lImagenSubida=false, lRegistrosNuevos=false, lCambioCat=false;
    ViewPager2 viewPager2;
    ViewPager2Adapter viewPager2Adapter;
    private TabLayout tabLayoutVarIng;
    Button btnNuevoVariedad, btnNuevoIng, btnConfIng;
    FragmentExtras fragmentExtras;
    FragmentVariedades fragmentVariedades;


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
        AbrirRetorno(false,lRegistrosNuevos,null);
        finish();
        return true;
    }

    private void Init() {
        Log.d("DEBUGX", "Init");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager2=findViewById(R.id.viewPager2Prod);
        tabLayoutVarIng=findViewById(R.id.tabLayout);
        setViewPager2Adapter();
        InicializarFragments();
        new TabLayoutMediator(tabLayoutVarIng, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
               if(position==0)
               {
                   tab.setText("Variedades");
               }
               else {
                   tab.setText("Ingredientes ext.");
               }
            }
        }).attach();
        cIdProducto=getIntent().getStringExtra("cIdProducto")==null?"":getIntent().getStringExtra("cIdProducto");
        cIdCategoriaSel=getIntent().getStringExtra("cIdCatSel")==null?"":getIntent().getStringExtra("cIdCatSel");
        lstCategorias= new ArrayList<>();
        lstIdCategorias= new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        btnConfIng=findViewById(R.id.btnConfigIng);
        btnNuevoIng=findViewById(R.id.btnNuevoIng);
        btnNuevoVariedad=findViewById(R.id.btnNuevoVar);
        edNombreProducto= findViewById(R.id.edNombreProducto);
        edPrecioProducto= findViewById(R.id.edPrecioProducto);
        edDescripconProd=findViewById(R.id.edDescripcionProducto);
        ivProducto=findViewById(R.id.ivProducto);
        ckPublicar=findViewById(R.id.ckPublicar);
        spCategorias= findViewById(R.id.spCategorias);
        cIdMenu=Global.RecuperaPreferencia(CCLAVEMENU,this);
        fragmentProductos= new FragmentProductos();
        ivProducto.setOnClickListener(view -> {
            if(!checkPermiso())
            {
                solicitaPermiso();
            }
            else {
                cropImage.launch("image/*");
            }
        });
        if(!cIdMenu.isEmpty())
        {
            storageReference= FirebaseStorage.getInstance().getReference();
            databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cIdMenu);
            btnNuevoVariedad.setOnClickListener(v->{
                AbrirNuevoVariante();
            });
            btnNuevoIng.setOnClickListener(v->{
                AbrirNuevoIngrediente();
            });
            btnConfIng.setOnClickListener(v->{

            });
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
        Log.d("DEBUGX", "cargaDatosProducto");
        if(productoG!=null)
        {
            edNombreProducto.setText(productoG.cNombre);
            edPrecioProducto.setText(productoG.lstVariedad.size()==0?productoG.cPrecio:"0");
            edDescripconProd.setText(productoG.cDescripcion);
            ckPublicar.setChecked(productoG.lDisponible);
            cUrlMin=productoG.cUrlImagenMin;
            cUrlImagen=productoG.cUrlImagen;
            fragmentVariedades.cargaVariedades(productoG.lstVariedad==null?new ArrayList<>():productoG.lstVariedad);
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
        Log.d("DEBUGX", "ObtenerEdicion");
        abrirDialogoCarga();
        List<Variedad> lstVariedad= new ArrayList<>();
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
                    for(DataSnapshot dataSnapshot1: dataSnapshot.child("lstVariedad").getChildren())
                    {
                        lstVariedad.add(new Variedad(
                           dataSnapshot1.getKey(),
                           dataSnapshot1.child("cNombre").getValue(String.class),
                           dataSnapshot1.child("cPrecio").getValue(String.class),
                           dataSnapshot1.child("lDisponible").getValue(boolean.class)
                        ));
                    }
                    productoG.lstVariedad=lstVariedad;
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
    private void setViewPager2Adapter()
    {
        Log.d("DEBUGX", "setViewPager2Adapter");
       viewPager2Adapter = new ViewPager2Adapter(this);
       List<Fragment> lstFragments= new ArrayList<>();
       lstFragments.add(new FragmentVariedades());
       lstFragments.add(new FragmentExtras());
       viewPager2Adapter.setFragments(lstFragments);
       viewPager2.setAdapter(viewPager2Adapter);
       viewPager2.setOffscreenPageLimit(2);
    }
    private void GuardarDatos()
    {
        String cMenu="menus/"+cIdMenu;
        String cLLaveVar="";
        HashMap<String, Object> updates= new HashMap<>();
        Producto producto= obtenerProductoGuardar();

        if(ValidarDatos(producto)) {
            abrirDialogoCarga();
            cLlaveG = databaseReferenceMenu.child("productos").push().getKey();
            cLlaveG = cIdProducto.isEmpty() ? cLlaveG : cIdProducto;
            producto.cLlave=cLlaveG;
            producto.cPrecio=producto.lstVariedad.size()!=0?"":producto.cPrecio;
            updates.put(cMenu+"/productos/"+ producto.cLlave, producto);
            if(producto.lDisponible)
            {
                updates.put(cMenu+ "/menu_publico/"+ producto.cIdCategoria+"/"+ producto.cLlave, producto);
            }
            else {
                updates.put(cMenu+ "/menu_publico/"+ producto.cIdCategoria+"/"+ producto.cLlave,null);
            }
            if(productoG!=null&&!productoG.cIdCategoria.equals(ObtenerIdCatSeleccionada()))//CAMBIO DE CATEGORIA
            {
                lCambioCat=true;
                updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto,null);
            }
            firebaseDatabase.getReference().updateChildren(updates).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    producto.cLlave = cLlaveG;
                    productoG = producto;
                    if (lImagenSubida) {
                        SubirImagen();
                    } else {
                        cerrarDialogoCarga();
                        limpiarCampos();
                        if(!lRegistrosNuevos)
                        {
                            AbrirRetorno(true, false, producto);
                            finish();
                        }
                        Toast.makeText(ABCProductoActivity.this, "¡Registro exitoso!", Toast.LENGTH_SHORT).show();
                    }

                }else
                {
                    Global.MostrarMensaje(ABCProductoActivity.this,
                            "Error al guardar",
                            "Se ha presentado un error al guardar, intenta de nuevo");
                }
            });
        }

    }




    private void AbrirRetorno(boolean lActualiza, boolean lNuevos, Producto producto)
    {
        Log.d("DEBUGX", "lActualiza:"+ lActualiza+"lNuevos"+lNuevos);
        Intent intent = new Intent();
        intent.putExtra("lNuevos", lNuevos);
        intent.putExtra("lActualiza", lActualiza);
        intent.putExtra("entProd", producto);
        intent.putExtra("lCambioCat", lCambioCat);
        setResult(Activity.RESULT_OK, intent);

    }

    @Override
    public void onBackPressed() {
        AbrirRetorno(false,lRegistrosNuevos,null);
        super.onBackPressed();

    }

    private void limpiarCampos() {
        edNombreProducto.setText("");
        edPrecioProducto.setText("");
        edDescripconProd.setText("");
        ivProducto.setImageURI(null);
        ivProducto.setImageDrawable(getDrawable(R.drawable.add_photo));
        fragmentVariedades.Limpiar();
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
        Log.d("DEBUGX", "obtenerProductoGuardar");
        Producto producto= new Producto();
        producto.cNombre=edNombreProducto.getText().toString();
        producto.lDisponible=ckPublicar.isChecked();
        producto.cUrlImagen=cUrlImagen;
        producto.cUrlImagenMin=cUrlMin;
        producto.cLlave=cIdProducto;
        producto.cIdCategoria=ObtenerIdCatSeleccionada();
        producto.cPrecio=edPrecioProducto.getText().toString();
        producto.cDescripcion=edDescripconProd.getText().toString();
        producto.lstVariedad=ObtenerVariedades();
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

    private void InicializarFragments()
    {
        Log.d("DEBUGX", "InicializarFragments");
        fragmentExtras= (FragmentExtras)viewPager2Adapter.obtenerFragment(1);
        fragmentVariedades=(FragmentVariedades)viewPager2Adapter.obtenerFragment(0);
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

    private boolean ValidarDatos(Producto producto)
    {
        if(producto.cNombre.isEmpty())
        {
            edNombreProducto.setError("Este campo es obligatorio");
            return false;
        }
        if(producto.lstVariedad.size()==0&&producto.cPrecio.isEmpty())
        {
            edPrecioProducto.setError("Es necesario ingresar un precio");
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
       hashMapUpdate.put("menus/"+ cIdMenu+ "/productos/"+ productoG.cLlave+ "/cUrlImagen", productoG.cUrlImagen);
        hashMapUpdate.put("menus/"+ cIdMenu+ "/productos/"+ productoG.cLlave+ "/cUrlImagenMin", productoG.cUrlImagen);
       if(productoG.lDisponible)
       {
           hashMapUpdate.put("menus/"+ cIdMenu+ "/menu_publico/"+ productoG.cIdCategoria+ "/"+ productoG.cLlave+ "/cUrlImagen", productoG.cUrlImagen);
           hashMapUpdate.put("menus/"+ cIdMenu+ "/menu_publico/"+ productoG.cIdCategoria+ "/"+ productoG.cLlave+ "/cUrlImagenMin", productoG.cUrlImagenMin);
       }

       firebaseDatabase.getReference().updateChildren(hashMapUpdate).addOnCompleteListener(task -> {
           cerrarDialogoCarga();
           if(task.isSuccessful())
           {
               Toast.makeText(ABCProductoActivity.this, "¡Guardado exitoso!", Toast.LENGTH_SHORT).show();
               if(cIdProducto.isEmpty())//SI ES NUEVO
               {
                   limpiarCampos();
                   lRegistrosNuevos=true;
               }
               else {
                   AbrirRetorno(true, false, productoG);
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
        bitMapFinal.compress(Bitmap.CompressFormat.JPEG, 100, baos);
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
    private List<Variedad> ObtenerVariedades()
    {
        return fragmentVariedades.RetornaVariedades();
    }
    private void AbrirNuevoVariante()
    {
        DialogABCVariedad dialogABCVariedad= new DialogABCVariedad(this, null, "N", fragmentVariedades);
        dialogABCVariedad.show(getSupportFragmentManager(), "dialog_var");
        dialogABCVariedad.setCancelable(false);
    }
    private void AbrirNuevoIngrediente()
    {
        DialogABCIngrediente dialogABCIngrediente= new DialogABCIngrediente(this, null, "N", fragmentExtras);
        dialogABCIngrediente.show(getSupportFragmentManager(), "dialog_ing");
        dialogABCIngrediente.setCancelable(false);
    }

}