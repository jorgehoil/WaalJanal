package com.carloshoil.waaljanal;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.waaljanal.Dialog.DialogPassWord;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.carloshoil.waaljanal.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainBinding binding;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference databaseReferenceUsers;
    private FirebaseAuth firebaseAuth;

    private DialogoCarga dialogoCarga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_categorias, R.id.nav_configuracion, R.id.nav_productos, R.id.nav_qr)
                .setOpenableLayout(drawer)
                .build();
        Init();
        abrirDialogoCarga();
        ObtenerDatosIniciales();


    }
    private void ObtenerDatosIniciales()
    {

        databaseReferenceUsers.child(firebaseAuth.getCurrentUser().getUid()).child("data").child("dataconfig").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    ProcesaDatos(task.getResult());
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
        databaseReferenceUsers=databaseReference.child("usuarios");
    }


    private void ProcesaDatos(DataSnapshot datos)
    {
        boolean lActivo=datos.child("lActivo").getValue(Boolean.class);
        boolean lBloqueado=datos.child("lBloqueado").getValue(Boolean.class);
        boolean lAdmin=datos.child("lAdmin").getValue(Boolean.class);
        String cNombre= datos.child("cNombre").getValue(String.class);
        if(!lActivo||!lBloqueado)
        {


            if(!lAdmin)
            {
                ObtenerPermisos(datos);
            }
            else
            {
                ConfiguraBarraNav(null, true, datos);

            }
        }
        else
        {
            cerrarDialogoCarga();
            Toast.makeText(this, "La cuenta está bloqueada o inactiva", Toast.LENGTH_SHORT).show();
        }



    }

    private void ObtenerPermisos(DataSnapshot datos) {
        databaseReferenceUsers.child("data").child("datapermisos").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    ConfiguraBarraNav(task.getResult(), false, datos);
                }
                else
                {
                    ObtenerPermisos(datos);
                }
            }
        });
    }

    private void ConfiguraBarraNav(DataSnapshot result, boolean lAdmin, DataSnapshot datos) {
       boolean lPrimeraVez= datos.child("lPrimeraVez").getValue(boolean.class);
        String cNombre= datos.child("cNombre").getValue(String.class);
        NavigationView navigationView = binding.navView;
        if(!lAdmin&&result!=null)
        {
            boolean lModCat, lModDisProd, lModInfoRest, lModProd;
            lModCat=result.child("lModCat").getValue(boolean.class);
            lModDisProd=result.child("lModDisProd").getValue(boolean.class);
            lModInfoRest=result.child("lModInfoRest").getValue(boolean.class);
            lModProd=result.child("lModProd").getValue(boolean.class);


            if(!lModCat)
            {
                navigationView.getMenu().findItem(R.id.nav_categorias).setVisible(false);
            }
            if(!lModDisProd)
            {
                navigationView.getMenu().findItem(R.id.nav_productos).setVisible(false);
            }
            if(!lModInfoRest)
            {
                navigationView.getMenu().findItem(R.id.nav_configuracion).setVisible(false);
            }
            navigationView.getMenu().findItem(R.id.nav_restaurantes).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_qr).setVisible(true);
        }

        View headerView= navigationView.getHeaderView(0);
        TextView tvHeaderText= headerView.findViewById(R.id.tvHeaderText);
        tvHeaderText.setText(cNombre);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        cerrarDialogoCarga();
        MuestraConfiguracionPassWord(lPrimeraVez);

    }

    private void MuestraConfiguracionPassWord(boolean lPrimeraVez) {
        if(lPrimeraVez)
        {
            abrirDialogoContrasena();
        }
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
    private void abrirDialogoContrasena()
    {
        Log.d("DEBUG", "Se abre dialogo carga");
        DialogPassWord dialogPassWord= new DialogPassWord(this);
        dialogPassWord.setCancelable(false);
        dialogPassWord.show(getSupportFragmentManager(), "dialogopass");

    }



}