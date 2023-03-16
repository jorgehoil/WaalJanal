package com.carloshoil.waaljanal;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.carloshoil.waaljanal.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUsers;
    private DatabaseReference databaseReferenceMenus;
    private FirebaseAuth firebaseAuth;

    private DialogoCarga dialogoCarga;
    private ValueEventListener valueEventListener;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.appBarMain.toolbarMain);
        DrawerLayout drawer = binding.drawerLayout;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_categorias, R.id.nav_productos, R.id.nav_registropagos,R.id.nav_suscripcion
                , R.id.nav_restaurantes, R.id.nav_acerca_de)
                .setOpenableLayout(drawer)
                .build();
        IniciaLizaEscuchaEstatus();
        Init();
        CargaDatosIniciales();

    }

    private void IniciaLizaEscuchaEstatus() {
        valueEventListener= new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int iEstatusPrueba;
                iEstatusPrueba=snapshot.getValue()==null?3:
                        snapshot.getValue(Integer.class);
                if(iEstatusPrueba==Values.PERIODO_FINALIZADO)
                {
                    Global.MostrarMensaje(MainActivity.this,"Suscripción vencida",
                            "Tu periodo de prueba/suscripción ha vencido," +
                                    " si WaalJanal te ha parecido útil, puedes reactivarlo " +
                                    "comprando una suscripción en el apartado de <<Suscripciones>>");
                } else if(iEstatusPrueba==3)
                {
                    Global.MostrarMensaje(MainActivity.this,"Error desconocido",
                            "Se ha presentado un error desconocido, por favor comunicate" +
                                    "al correo de contacto.");
                    Toast.makeText(MainActivity.this, "", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };


    }

    private void CargaDatosIniciales() {
        String cNombre;
        String cDatosCargados= Global.RecuperaPreferencia("cDatosCargados", this);
        if(cDatosCargados.isEmpty())
        {
            ObtenerDatosIniciales();
        }
        else
        {
            cNombre=Global.RecuperaPreferencia("cNombreUsuario",this );
            Configura(cNombre);
        }
    }

    private void Configura(String cNombre) {
        NavigationView navigationView = binding.navView;
        navigationView.getMenu().findItem(R.id.nav_salir).setOnMenuItemClickListener(menuItem -> {
            ConfirmaCerrarSesion();
            return false;
        });
        navigationView.setCheckedItem(R.id.nav_restaurantes);
        View headerView= navigationView.getHeaderView(0);
        TextView tvHeaderText= headerView.findViewById(R.id.tvNombreCuenta);
        tvHeaderText.setText(cNombre);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

    }

    private void ConfirmaCerrarSesion() {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(MainActivity.this);
        alertDialog.setTitle("¿Cerrar sesión?");
        alertDialog.setMessage("Será necesario ingresar sus datos para volver a usar la app");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) ->CierraSesion());
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    private void CierraSesion() {
        firebaseAuth.signOut();
        Global.GuardarPreferencias("cEstatusLogin","0",MainActivity.this);
        Global.GuardarPreferencias("cIdMenu", "", MainActivity.this);
        Global.GuardarPreferencias("cEmailId", "", MainActivity.this);
        Global.GuardarPreferencias("cNombreUsuario", "", MainActivity.this);
        Intent i= new Intent(MainActivity.this, ActivityLogin.class);
        startActivity(i);
        finish();
    }

    private void ObtenerDatosIniciales()
    {
        abrirDialogoCarga();
        databaseReferenceUsers.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    cerrarDialogoCarga();
                    String cNombre=task.getResult().child("cNombre").getValue()==null?"":task.getResult().child("cNombre").getValue(String.class);
                    Global.GuardarPreferencias("cNombreCuenta", cNombre, MainActivity.this);
                    Global.GuardarPreferencias("cDatosCargados", "1", MainActivity.this);
                    Configura(cNombre);
                }
                else
                {
                    ObtenerDatosIniciales();
                }
            }
        });
    }


    private void Init() {
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth.getUid()!=null)
        {
            databaseReferenceUsers=databaseReference.child("usuarios").child(firebaseAuth.getUid());
            databaseReferenceUsers
                    .child("dataInfoUso")
                    .child("iPeriodoEstatus")
                    .addValueEventListener(valueEventListener);
        }
        else
        {
            AbreLogin();
        }

    }

    private void AbreLogin() {
        Global.GuardarPreferencias("cEstatusLogin", "0",this);
        Intent i= new Intent(MainActivity.this, ActivityLogin.class);
        i.putExtra("cData", "USER_NULL");
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
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

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        databaseReferenceUsers.removeEventListener(valueEventListener);
        super.onStop();
    }
}