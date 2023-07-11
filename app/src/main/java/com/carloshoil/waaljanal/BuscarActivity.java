package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.AdapterPedidoDetalle;
import com.carloshoil.waaljanal.DTO.Pedido;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BuscarActivity extends AppCompatActivity {
    TextView tvCliente, tvDireccion, tvEstatus, tvTelefono, tvPrecio;
    Button btnCancelar, btnCmabiarEstatus, btnBuscar;
    RecyclerView recyclerViewProd;
    ProgressBar pbCarga;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String cIdMenu;
    EditText edBuscar;
    ImageView imageViewTipo;
    AdapterPedidoDetalle adapterPedidoDetalle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buscar);
        Init();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void Init() {
        cIdMenu= Global.RecuperaPreferencia("cIdMenu",this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        firebaseDatabase= FirebaseDatabase.getInstance();
        tvCliente=findViewById(R.id.tvClienteDialogBuscarPed);
        tvDireccion=findViewById(R.id.tvDireccionDialogBuscarPed);
        tvEstatus=findViewById(R.id.tvEstatusDialogBuscarPed);
        tvTelefono=findViewById(R.id.tvTelDialogBuscarPed);
        tvPrecio=findViewById(R.id.tvTotalDialogBuscarPedido);
        btnCancelar=findViewById(R.id.btnCancelarPedidoDialgoBuscarPed);
        edBuscar=findViewById(R.id.edNumOrdenDialogBuscar);
        btnCmabiarEstatus=findViewById(R.id.btnTomarRecDialgoBusPed);
        btnBuscar=findViewById(R.id.btnBuscarPedidoDialogBuscar);
        pbCarga=findViewById(R.id.pbCargaBuscaPed);
        imageViewTipo=findViewById(R.id.ivTipoDialogBuscarPed);
        recyclerViewProd=findViewById(R.id.recycleDialogBuscarPed);
        edBuscar.setOnKeyListener((view, i, keyEvent) -> {
            if(keyEvent.getAction()==KeyEvent.ACTION_DOWN&&i==KeyEvent.KEYCODE_ENTER){
                if(ValidaCampo(edBuscar.getText().toString()))
                {
                    Buscar(edBuscar.getText().toString());
                }
                return true;
            }
            return false;
        });
        if(!cIdMenu.isEmpty())
        {
            databaseReference=firebaseDatabase.getReference()
                    .child("pedidos")
                    .child(cIdMenu);
            btnBuscar.setOnClickListener(v->{


            });
        }
        IniciaAdapter();
    }
    private boolean ValidaCampo(String cDato)
    {
        if(cDato.isEmpty())
        {
            edBuscar.setError("Ingrese un número de orden");
            return false;
        }
        return true;
    }

    private void IniciaAdapter() {

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewProd.setLayoutManager(linearLayoutManager);
        adapterPedidoDetalle = new AdapterPedidoDetalle(this, new ArrayList<>());
        recyclerViewProd.setAdapter(adapterPedidoDetalle);
    }
    private void ObtenerDetalles(HashMap hashMapdata){
        String cEstatus, cTipo, cIdOrden;
        cEstatus=hashMapdata.get("cEstatus").toString();
        cIdOrden=hashMapdata.get("cIdOrden").toString();
        cTipo=hashMapdata.get("cTipo").toString();
        databaseReference.child("generados").child(cTipo).child(cEstatus).child(cIdOrden).get()
                .addOnCompleteListener(task -> {

                    btnBuscar.setEnabled(true);
                    pbCarga.setVisibility(View.INVISIBLE);
                    if(task.isSuccessful())
                    {
                        if(task.getResult().exists())
                        {
                            EsconderTeclado();
                            String[] arrayDetalles;
                            List<String> lstDetalles= new ArrayList<>();
                            Pedido pedido=task.getResult().getValue(Pedido.class);
                            tvTelefono.setText(pedido.cTelefono.isEmpty()?" *** ":pedido.cTelefono);
                            tvCliente.setText(pedido.cMesa.isEmpty()?pedido.cNombreCliente:"Mesa # "+pedido.cMesa);
                            tvDireccion.setText(pedido.cDireccion.isEmpty()?" *** ":pedido.cDireccion);
                            tvPrecio.setText("$ "+ pedido.cTotal);
                            arrayDetalles= pedido.cDetallePedido.split("/#/");
                            lstDetalles.addAll(Arrays.asList(arrayDetalles));
                            adapterPedidoDetalle.CargaDatos(lstDetalles);
                        }
                        else
                        {

                            Toast.makeText(this, "No se encontró detalles de pedido", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(this, "Error al buscar, intenta de nuevo", Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void EsconderTeclado() {
        View view= this.getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

    }
    private void Buscar(String cNumeroOrden) {
        pbCarga.setVisibility(View.VISIBLE);
        btnBuscar.setEnabled(false);
        HashMap<String, String> hashMapData= new HashMap<>();
        databaseReference.child("todo").child(cNumeroOrden)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        String cEstatus, cTipo, cIdOrden;
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {
                                DataSnapshot dataSnapshot=task.getResult();
                                cEstatus=dataSnapshot
                                        .child("cEstatus")
                                        .getValue()==null?"recibido":dataSnapshot
                                        .child("cEstatus").getValue().toString();
                                cTipo=dataSnapshot
                                        .child("cTipo")
                                        .getValue()==null?"comedor":dataSnapshot
                                        .child("cTipo").getValue().toString();
                                cIdOrden=dataSnapshot.getKey();
                                hashMapData.put("cEstatus", cEstatus);
                                hashMapData.put("cTipo", cTipo);
                                hashMapData.put("cIdOrden", cIdOrden);
                                tvEstatus.setText(cEstatus.toUpperCase(Locale.ROOT));
                                imageViewTipo.setImageDrawable(getDrawable(getImageResourceId(cTipo)));
                                ObtenerDetalles(hashMapData);
                            }
                            else {
                                btnBuscar.setEnabled(true);
                                pbCarga.setVisibility(View.INVISIBLE);
                                Toast.makeText(BuscarActivity.this, "No se encontró el pedido, verifica el # de orden", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            btnBuscar.setEnabled(true);
                            pbCarga.setVisibility(View.INVISIBLE);
                            Toast.makeText(BuscarActivity.this, "Error al buscar, intenta de nuevo", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
    private int getImageResourceId(String cTipo)
    {
        int iRetorno=R.drawable.ic_comedor;
        switch (cTipo)
        {
            case "comedor":
                iRetorno=R.drawable.ic_comedor;
                break;
            case "domicilio":
                iRetorno=R.drawable.ic_domicilio;
                break;
            case "recoger":
                iRetorno=R.drawable.ic_recoge_per;
                break;
        }
        return iRetorno;
    }

}