
package com.carloshoil.waaljanal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.MenuProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import com.carloshoil.waaljanal.Adapter.PersonalizacionAdapter;
import com.carloshoil.waaljanal.Adapter.ViewPagerAdapter;
import com.carloshoil.waaljanal.DTO.MenuPersonalizado;
import com.carloshoil.waaljanal.DTO.ViewPagerData;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class ActivityConfiguracion extends AppCompatActivity {

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceMenu;
    DatabaseReference databaseReference;
    FirebaseAuth firebaseAuth;
    TextView cTextPrice1, cTextPrice2, cTextPlat1, cTextPlat2, cTextDescrip1, cTextDescrip2, cTextCat, tvTituloMenu;
    String cIdMenuG, cIdMenuActual;
    CardView cFondo;
    LinearLayout cFondoPlat, cFondoPlat2;
    EditText edNombreRes, edHorarioRes, edTelefono;
    DialogoCarga dialogoCarga;

    RecyclerView recyclerViewMenuPer;
    PersonalizacionAdapter personalizacionAdapter;
    Spinner spMoneda;
    boolean lInicio;
    int iEstatusPrueba;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);
        Init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void Init() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ActivityConfiguracion.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        recyclerViewMenuPer=findViewById(R.id.recycleMenuPer);
        tvTituloMenu=findViewById(R.id.tvTituloMenu);
        spMoneda=findViewById(R.id.spMoneda);
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
        edHorarioRes=findViewById(R.id.edHorarioRes);
        edNombreRes=findViewById(R.id.edNombreRes);
        edTelefono=findViewById(R.id.edTelefonoRes);
        cIdMenuG=getIntent().getStringExtra("cIdMenu")==null?"":getIntent().getStringExtra("cIdMenu");
        lInicio=getIntent().getBooleanExtra("lInicio", false);
        iEstatusPrueba=getIntent().getIntExtra("iEstatusPeriodo", 3);
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        cIdMenuActual=Global.RecuperaPreferencia("cIdMenu", this);
        databaseReference=firebaseDatabase.getReference();
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
                    if(ValidaGuardar())
                    {
                        IniciarGuardado();
                    }
                }
                return false;
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
    private void PreparaAdapterMenuPer()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.HORIZONTAL);
        recyclerViewMenuPer.setLayoutManager(linearLayoutManager);
        personalizacionAdapter= new PersonalizacionAdapter(this, new ArrayList<>(), this, "");
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

    private void ConsultaMenusPersonalizados(String cIdMenuPer)
    {
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
                    personalizacionAdapter.ActualizaIdMenuPer(cIdMenuPer);
                    personalizacionAdapter.Actualiza(lstmenuPersonalizados);
                }
            }
        });

    }
    private void IniciarGuardado()
    {

        if(!cIdMenuG.isEmpty()) {
            muestradialogoCarga();
            Guardar(cIdMenuG, false);
        }
        else {
            if(lInicio)
            {
                if(iEstatusPrueba==Values.PERIODO_INACTIVO)
                {
                    MostrarMensajeInicioPrueba();
                }
                else {
                    muestradialogoCarga();
                    GenerarNuevoMenu();
                }

            }
            else {
                muestradialogoCarga();
                GenerarNuevoMenu();
            }

        }

    }

    private void MostrarMensajeInicioPrueba() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(ActivityConfiguracion.this);
        alertDialog.setTitle("¿Iniciar prueba?");
        alertDialog.setIcon(R.drawable.ic_info);
        alertDialog.setMessage("Se creará tu menu y podrás usarlo sin costo por 30 días, luego" +
                ", si te ha gustado WaalJanal, podrás adquirir una suscripción.");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) ->{
            CreaRegistroPrueba();
        });
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    private void CreaRegistroPrueba() {
        muestradialogoCarga();
        HashMap<String, Object> hashMapInfoPrueba= new HashMap<>();
        HashMap<String, Object> hashMapUpdate= new HashMap<>();
        hashMapInfoPrueba.put("cMail", Global.RecuperaPreferencia("cEmailId", this));
        hashMapInfoPrueba.put("cNombre", Global.RecuperaPreferencia("cNombreUsuario", this));
        hashMapInfoPrueba.put("dateCreacion", ServerValue.TIMESTAMP);
        hashMapUpdate.put("usuariosprueba/"+ firebaseAuth.getUid(), hashMapInfoPrueba);
        hashMapUpdate.put("usuarios/"+firebaseAuth.getUid()+"/dataInfoUso/iPeriodoEstatus", Values.PERIODO_ACTIVO);
        firebaseDatabase.getReference()
                .updateChildren(hashMapUpdate)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            GenerarNuevoMenu();
                        }
                        else {
                            ocultaDialogoCarga();
                            Global.MostrarMensaje(ActivityConfiguracion.this, "Error",
                                    "Se ha producido un error al crear el menú, " +
                                            "por favor intentalo de nuevo");
                        }
                    }
                });



    }

    private void GenerarNuevoMenu() {
        firebaseDatabase.getReference().child("datamenu").runTransaction(new Transaction.Handler() {
            @NonNull
            @Override
            public Transaction.Result doTransaction(@NonNull MutableData currentData) {
                if(currentData.child("lastIdMenu").getValue(String.class)==null)
                    return Transaction.success(currentData);
                int iValor=0;
                String cData=currentData.child("lastIdMenu").getValue(String.class);
                try{
                    iValor=Integer.parseInt(cData.split("-")[1]);
                }catch (Exception ex){
                    iValor=0;
                }
                if(iValor>0)
                {
                    iValor=iValor+1;
                    currentData.child("lastIdMenu").setValue("wj-"+iValor);
                    return Transaction.success(currentData);
                }
                else
                {
                    return Transaction.abort();
                }

            }

            @Override
            public void onComplete(@Nullable DatabaseError error, boolean committed, @Nullable DataSnapshot currentData) {

                if(committed)
                {
                    String cClave= currentData.child("lastIdMenu").getValue(String.class);
                    Guardar(cClave, true);
                }
                else
                {
                    Toast.makeText(ActivityConfiguracion.this, "Error al guardar, vuelva a intentar", Toast.LENGTH_SHORT).show();
                   ocultaDialogoCarga();
                }
            }
        });

    }

    private void GuardarMenuPub(String cIdMenu, boolean lNuevo)
    {
        HashMap<String, Object> hashMapUpdate= new HashMap<>();
        hashMapUpdate.put("menus/"+cIdMenu+"/info/cNombre", edNombreRes.getText().toString() );
        hashMapUpdate.put("menus/"+cIdMenu+"/info/cTelefono", edTelefono.getText().toString() );
        hashMapUpdate.put("menus/"+cIdMenu+"/info/cHorario", edHorarioRes.getText().toString() );
        hashMapUpdate.put("menus/"+ cIdMenu+"/info/iIdMoneda", spMoneda.getSelectedItemPosition());
        hashMapUpdate.put("menus/"+cIdMenu+"/info/cIdMenuPer", personalizacionAdapter.obtenerSeleccionado().cKey );
        hashMapUpdate.put("menus/"+cIdMenu+"/info/menuperinfo",personalizacionAdapter.obtenerSeleccionado());
        if(lNuevo)
        {
            hashMapUpdate.put("menus/"+cIdMenu+"/info/lDisponible", false);
            hashMapUpdate.put("menus/"+cIdMenu+"/info/lActivo", true);
        }
        databaseReference.updateChildren(hashMapUpdate).addOnCompleteListener(task -> {
            ocultaDialogoCarga();
            if(task.isSuccessful())
            {

                Toast.makeText(ActivityConfiguracion.this, "¡Guardado exitoso!", Toast.LENGTH_SHORT).show();
                if(cIdMenuActual.isEmpty())
                {
                    Global.GuardarPreferencias("cIdMenu",cIdMenu, ActivityConfiguracion.this );
                }
                if(lInicio)
                {
                    IniciaMain();
                }
                else {
                    finish();
                }
            }
            else {
                Global.MostrarMensaje(ActivityConfiguracion.this, "Error",
                        "Se ha producido un error al guardar, intenta de nuevo");
            }
        });
    }
    private void Guardar(String cIdMenu, boolean lNuevo)
    {
        HashMap<String, Object> hashMapUpdate= new HashMap<>();
        hashMapUpdate.put("usuarios/"+firebaseAuth.getUid()+"/adminlugares/"+cIdMenu+"/cNombre",edNombreRes.getText().toString());
        if(lNuevo)
        {
            hashMapUpdate.put("usuarios/"+firebaseAuth.getUid()+"/adminlugares/"+cIdMenu+"/lDisponible",false);
            hashMapUpdate.put("usuarios/"+firebaseAuth.getUid()+"/dataInfoUso/iTotalMenus", ServerValue.increment(1));
        }
        databaseReference.updateChildren(hashMapUpdate).addOnCompleteListener(task -> {

            if(task.isSuccessful())
            {
                GuardarMenuPub(cIdMenu, lNuevo);
            }
            else
            {
                ocultaDialogoCarga();
                Global.MostrarMensaje(ActivityConfiguracion.this, "Error",
                        "Se ha producido un error al guardar, intenta de nuevo");
            }
        });
    }
    private void IniciaMain()
    {
        Intent i= new Intent(ActivityConfiguracion.this, MainActivity.class);
        i.putExtra("cData", "NUEVO");
        startActivity(i);
    }
    private void ConsultaDatos()
    {

        if(!cIdMenuG.isEmpty())
        {
            muestradialogoCarga();
            databaseReference.child("menus")
                    .child(cIdMenuG)
                    .child("info")
                    .get()
                    .addOnCompleteListener(task -> {

                        if(task.isSuccessful())
                        {

                            String cIdMenuPer="";

                            if(task.getResult().exists())
                            {
                                DataSnapshot dataSnapshot= task.getResult();
                                edNombreRes.setText(dataSnapshot.child("cNombre").getValue()==null?"":dataSnapshot.child("cNombre").getValue(String.class));
                                edHorarioRes.setText(dataSnapshot.child("cHorario").getValue()==null?"":dataSnapshot.child("cHorario").getValue(String.class));
                                edTelefono.setText(dataSnapshot.child("cTelefono").getValue()==null?"":dataSnapshot.child("cTelefono").getValue(String.class));
                                spMoneda.setSelection(dataSnapshot.child("iIdMoneda").getValue()==null?0:dataSnapshot.child("iIdMoneda").getValue(Integer.class));
                                cIdMenuPer=dataSnapshot.child("cIdMenuPer").getValue()==null?"menusperautumn":dataSnapshot.child("cIdMenuPer").getValue().toString();
                                ConsultaMenusPersonalizados(cIdMenuPer);
                            }
                            else
                            {
                                ocultaDialogoCarga();
                                Toast.makeText(ActivityConfiguracion.this, "No existen datos registrados", Toast.LENGTH_SHORT).show();
                            }

                        }
                        else
                        {
                            ocultaDialogoCarga();
                            Toast.makeText(ActivityConfiguracion.this, "Error al consultar", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
        else {
            muestradialogoCarga();
            ConsultaMenusPersonalizados("menusperautumn");
        }

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