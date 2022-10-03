package com.carloshoil.waaljanal;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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
    private DatabaseReference databaseReferenceMenus;
    private FirebaseAuth firebaseAuth;

    private DialogoCarga dialogoCarga;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        MainActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setSupportActionBar(binding.appBarMain.toolbarMain);
        DrawerLayout drawer = binding.drawerLayout;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_categorias, R.id.nav_productos, R.id.nav_cuenta, R.id.nav_restaurantes)
                .setOpenableLayout(drawer)
                .build();
        Init();
        CargaDatosIniciales();


    }

    private void CargaDatosIniciales() {
        String cNombre;
        Boolean lAdmin;
        String cDatosCargados= Global.RecuperaPreferencia("cDatosCargados", this);
        if(cDatosCargados.isEmpty())
        {
            ObtenerDatosIniciales();
        }
        else
        {
            cNombre=Global.RecuperaPreferencia("cNombreCuenta",this );
            lAdmin=Global.RecuperaPreferencia("lAdmin", this).equals("1");
            Configura(cNombre, lAdmin);
        }
    }

    private void Configura(String cNombre, Boolean lAdmin) {
        NavigationView navigationView = binding.navView;
        /*if(!lAdmin)
        {
            navigationView.getMenu().findItem(R.id.nav_categorias).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_productos).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_restaurantes).setVisible(false);
        }*/
        View headerView= navigationView.getHeaderView(0);
        TextView tvHeaderText= headerView.findViewById(R.id.tvNombreCuenta);
        tvHeaderText.setText(cNombre);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        ValidarMenu();
    }

    private void ObtenerDatosIniciales()
    {
        abrirDialogoCarga();
        databaseReferenceUsers.child("data").child("dataconfig").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    cerrarDialogoCarga();
                    String cNombre=task.getResult().child("cNombre")==null?"":task.getResult().child("cNombre").getValue(String.class);
                    Boolean lAdmin= task.getResult().child("lAdmin")==null?false:task.getResult().child("lAdmin").getValue(boolean.class);
                    Global.GuardarPreferencias("cNombreCuenta", cNombre, MainActivity.this);
                    Global.GuardarPreferencias("lAdmin", lAdmin?"1":"0", MainActivity.this);
                    Global.GuardarPreferencias("cDatosCargados", "1", MainActivity.this);
                    Configura(cNombre, lAdmin);
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
        databaseReferenceUsers=databaseReference.child("usuarios").child(firebaseAuth.getUid());
        databaseReferenceMenus=databaseReferenceUsers.child("adminlugares");
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
    private void ValidarMenu()
    {
        String cIdMenu= Global.RecuperaPreferencia("cIdMenu", MainActivity.this);
        if(cIdMenu.isEmpty())
        {
            Global.MostrarMensaje(this, "Información", "No tiene seleccionado o creado algún menú, " +
                    "vaya a la sección de Restaurantes y seleccione o cree menú para administrar");
        }
    }




}