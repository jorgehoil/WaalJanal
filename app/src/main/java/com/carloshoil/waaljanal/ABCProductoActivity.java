package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ABCProductoActivity extends AppCompatActivity {
    String cTemporal="wjag1";
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceMenu;
    EditText edNombreProducto, edPrecioProducto, edDescripconProd;
    RadioButton rbDisProducto, rbNoDisProd;
    Spinner spCategorias;
    Button btnGuardar, btnGaleria, btnCamara;
    ImageView imProducto;
    DialogoCarga dialogoCarga;
    List<String> lstCategorias;
    List<String> lstIdCategorias;
    String cIdProducto, cIdCategoriaG;
    Producto productoG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abcproducto);
        ABCProductoActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Init();
    }

    private void Init() {
        cIdProducto=getIntent().getStringExtra("cIdProducto")==null?"":getIntent().getStringExtra("cIdProducto");
        lstCategorias= new ArrayList<>();
        lstIdCategorias= new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cTemporal);
        edNombreProducto= findViewById(R.id.edNombreProducto);
        edPrecioProducto= findViewById(R.id.edPrecioProducto);
        edDescripconProd=findViewById(R.id.edDescripcionProducto);
        rbDisProducto=findViewById(R.id.rbDispProd);
        rbNoDisProd=findViewById(R.id.rbNoDispProd);
        spCategorias= findViewById(R.id.spCategorias);
        btnCamara=findViewById(R.id.btnCamaraProducto);
        btnGaleria=findViewById(R.id.btnGaleriaProducto);
        btnGuardar=findViewById(R.id.btnGuardarProducto);
        imProducto=findViewById(R.id.imImagenProducto);
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GuardarDatos();
            }
        });
        ObtenerEdicion();
        ObtenerCategorias();
    }

    private void cargaDatosProducto()
    {
        if(productoG!=null)
        {
            edNombreProducto.setText(productoG.cNombre);
            edPrecioProducto.setText(productoG.cPrecio);
            edDescripconProd.setText(productoG.cDescripcion);
            rbDisProducto.setChecked(productoG.lDisponible);
            rbNoDisProd.setChecked(!productoG.lDisponible);

        }
    }
    private void ObtenerEdicion() {
        if(!cIdProducto.isEmpty())
        {
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
                        productoG.lDisponible=dataSnapshot.child("lDisponible").getValue()==null?false:dataSnapshot.child("lDisponible").getValue(boolean.class);
                        cargaDatosProducto();
                    }


                }
            });
        }
    }

    private void SubirImagen()
    {

    }
    private void GuardarDatos()
    {
        String cMenu="menus/"+cTemporal+"/";
        HashMap<String, Object> updates= new HashMap<>();

        if(ValidarDatos(edNombreProducto.getText().toString(), edPrecioProducto.getText().toString())){
            abrirDialogoCarga();
            Producto producto= obtenerProductoGuardar();
            if(cIdProducto.isEmpty())//NUEVO PRODUCTO
            {
                String cLlave= databaseReferenceMenu.child("productos").push().getKey();
                updates.put(cMenu+"/productos/"+cLlave,producto);
                updates.put(cMenu+"/menu_publico/"+ObtenerIdCatSeleccionada()+"/"+cLlave,producto);
                firebaseDatabase.getReference().updateChildren(updates).addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        cerrarDialogoCarga();
                        Toast.makeText(ABCProductoActivity.this, "??Registro exitoso!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else //ACTUALIZAR PRODUCTO
            {
                updates.put(cMenu+"/productos/"+cIdProducto,producto);
                if(productoG.cIdCategoria.equals(ObtenerIdCatSeleccionada()))
                {
                    if(!productoG.lDisponible){
                        updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto, null);
                    }
                    else
                    {
                        updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto,producto);
                    }
                }
                else
                {
                    updates.put(cMenu+"/menu_publico/"+productoG.cIdCategoria+"/"+cIdProducto,null);
                    if(productoG.lDisponible)
                    {
                        updates.put(cMenu+"/menu_publico/"+ObtenerIdCatSeleccionada()+"/"+cIdProducto,producto);
                    }
                }
                firebaseDatabase.getReference().updateChildren(updates).addOnCompleteListener(task -> {
                    cerrarDialogoCarga();
                    if(task.isSuccessful())
                    {

                        Toast.makeText(ABCProductoActivity.this,
                                "??Registro exitoso!", Toast.LENGTH_SHORT).show();
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
    private Producto obtenerProductoGuardar()
    {
        Producto producto= new Producto();
        producto.cNombre=edNombreProducto.getText().toString();
        producto.lDisponible=(rbDisProducto.isChecked()?true:false);
        producto.cUrlImagen="prueba";
        producto.cLlave="";
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
                        btnGuardar.setEnabled(false);
                        Toast.makeText(ABCProductoActivity.this, "No existe categorias, reg??strelos en la secci??n correspondiente", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        CargaCategorias(lstCategorias);
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
        if(!cIdProducto.isEmpty())
        {
            int iPosition=0;
            int i=0;
            for(String cIdCat: lstIdCategorias)
            {
                if(cIdCat.equals(productoG.cIdCategoria))
                {
                    iPosition=i;
                }
                i++;
            }
            spCategorias.setSelection(iPosition);
        }
        else
        {
            spCategorias.setSelection(0);
        }
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
}