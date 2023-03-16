package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.CategoriasAdapter;
import com.carloshoil.waaljanal.Adapter.RestaurantesSelAdapter;
import com.carloshoil.waaljanal.DTO.Restaurante;
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
import com.google.firebase.database.Transaction;
import com.google.firebase.database.core.utilities.Utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ActivityInicioSeleccion extends AppCompatActivity {

    RecyclerView recyclerViewRestSel;
    TextView tvTextoRestSel;
    Button btnSiguiente;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceAdminLugares;
    DatabaseReference databaseReference;
    DialogoCarga dialogoCarga;
    RestaurantesSelAdapter restaurantesSelAdapter;

    boolean lExisteListRest=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio_seleccion);
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        Init();
    }

    private void Init() {
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        databaseReferenceAdminLugares=firebaseDatabase.getReference().child("usuarios").child(firebaseAuth.getUid()).child("adminlugares");
        recyclerViewRestSel=findViewById(R.id.recycleRestaurantSel);
        tvTextoRestSel=findViewById(R.id.tvTextoSel);
        btnSiguiente=findViewById(R.id.btnSigRestSel);
        btnSiguiente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(lExisteListRest) {
                    GuardarSeleccion();
                }
                else {
                    VerificaEstatusUsuario();
                }

            }
        });
    }


    private void VerificaEstatusUsuario()
    {
        btnSiguiente.setEnabled(false);
        databaseReference.child("usuarios")
                .child(firebaseAuth.getUid())
                .child("dataInfoUso")
                .child("iPeriodoEstatus")
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful())
                    {
                        btnSiguiente.setEnabled(true);
                        if(task.getResult().exists()) {
                            int iEstatusPrueba = task.getResult().getValue(Integer.class);
                            if(iEstatusPrueba==Values.PERIODO_INACTIVO||iEstatusPrueba==Values.PERIODO_ACTIVO)
                            {
                                AbrirRegistroMenu(iEstatusPrueba);
                            }
                            else {
                                AbrirPrincipal();
                            }

                        }
                    }
                    else
                    {
                        Global.MostrarMensaje(ActivityInicioSeleccion.this,
                                "Error", "Ha ocurrido un error, intenta de nuevo");
                    }
                });
    }

    private void AbrirPrincipal() {
        Intent i= new Intent(ActivityInicioSeleccion.this, MainActivity.class);
        i.putExtra("cData", "FINALIZADO");
        startActivity(i);
    }

    private void GuardarSeleccion() {
        String cIdMenu= restaurantesSelAdapter.ObtenerSeleccionado();
        if(cIdMenu.isEmpty())
        {
            Toast.makeText(this, "Debes seleccionar una opción", Toast.LENGTH_SHORT).show();
        }
        else {
            Global.GuardarPreferencias("cIdMenu", cIdMenu, this);
            IniciaPrincipal();
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        AjustaAdapter();
        VerificaMenu();
    }

    private void AjustaAdapter() {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewRestSel.setLayoutManager(linearLayoutManager);
        restaurantesSelAdapter= new RestaurantesSelAdapter(new ArrayList<>(),this);
        recyclerViewRestSel.setAdapter(restaurantesSelAdapter);
    }
    private void CargaDatosAdapter(List<Restaurante> lstRestaurante)
    {
        restaurantesSelAdapter.CargaDatos(lstRestaurante);
    }
    private void abrirDialogoCarga()
    {
        dialogoCarga= new DialogoCarga(this);
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getSupportFragmentManager(), "dialogocarga");
    }
    private void cerrarDialogoCarga()
    {
        Log.d("DEBUG", "Se cierra dialogo carga");
        if(dialogoCarga!=null)
        {
            dialogoCarga.dismiss();
        }
    }
    private void VerificaMenu() {

        String cIdMenu= Global.RecuperaPreferencia("cIdMenu", this);
        if(cIdMenu.isEmpty())
        {
            abrirDialogoCarga();
            databaseReferenceAdminLugares.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    cerrarDialogoCarga();
                    if(task.isSuccessful())
                    {
                        Restaurante restaurante;
                        List<Restaurante> lstRestaurantes= new ArrayList<>();
                        for(DataSnapshot snapshot: task.getResult().getChildren())
                        {
                            restaurante= new Restaurante(
                                    snapshot.getKey(),
                                    snapshot.child("cNombre").getValue()==null?"":snapshot.child("cNombre").getValue(String.class),
                                    snapshot.getKey(),
                                    snapshot.child("lDisponible").getValue()==null?true:snapshot.child("lDisponible").getValue(boolean.class));
                            lstRestaurantes.add(restaurante);

                        }
                        if(lstRestaurantes.size()>0)
                        {
                            CargaRestaurantes(lstRestaurantes);
                        }

                    }
                    else{
                        VerificaMenu();
                    }
                }
            });
        }
        else
        {
            IniciaPrincipal();
        }
    }

    private void AbrirRegistroMenu(int iEstatusPrueba) {
        Intent i= new Intent(ActivityInicioSeleccion.this, ActivityConfiguracion.class);
        i.putExtra("lInicio", true);
        i.putExtra("iEstatusPeriodo", iEstatusPrueba);
        startActivity(i);
    }

    private void IniciaPrincipal() {
        Intent i= new Intent(ActivityInicioSeleccion.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    @SuppressLint("SetTextI18n")
    private void CargaRegistroRest() {
        tvTextoRestSel.setText("¿Cómo se llama tu establecimiento?");
        recyclerViewRestSel.setVisibility(View.GONE);
    }

    private void CargaRestaurantes(List<Restaurante> restaurantes) {
        tvTextoRestSel.setText("Selecciona un menú para empezar a administrarlo");
        lExisteListRest=true;
        CargaDatosAdapter(restaurantes);
    }
}