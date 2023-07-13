package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
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
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class BuscarActivity extends AppCompatActivity {
    TextView tvCliente, tvDireccion, tvEstatus, tvTelefono, tvPrecio;
    Button btnOpciones, btnBuscar;
    RecyclerView recyclerViewProd;
    ProgressBar pbCarga;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    String cIdMenu;
    EditText edBuscar;
    ImageView imageViewTipo;
    AdapterPedidoDetalle adapterPedidoDetalle;
    MenuItem itemCancelar, itemRecibir, itemTomar;
    HashMap<String, String> hashInfo= new HashMap<>();
    Pedido pedido;
    PopupMenu popupMenu;
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
        edBuscar=findViewById(R.id.edNumOrdenDialogBuscar);
        btnBuscar=findViewById(R.id.btnBuscarPedidoDialogBuscar);
        pbCarga=findViewById(R.id.pbCargaBuscaPed);
        imageViewTipo=findViewById(R.id.ivTipoDialogBuscarPed);
        recyclerViewProd=findViewById(R.id.recycleDialogBuscarPed);
        btnOpciones=findViewById(R.id.btnOpcionesBuscar);
        popupMenu= new PopupMenu(this, btnOpciones);
        popupMenu.inflate(R.menu.menu_pedido_busqueda);
        popupMenu.setOnMenuItemClickListener(menuItem -> {
            int iIdItem=menuItem.getItemId();
            if(iIdItem==R.id.pedidoBusCancelar){
                ConfirmaCancelar();
            } else if(iIdItem==R.id.pedidoBusRecibir)
            {
                CambiarEstatusPedido("recibido");
            } else if(iIdItem==R.id.pedidoBusTomar)
            {
                CambiarEstatusPedido("tomado");
            }
            return false;
        });
        btnOpciones.setOnClickListener(v->{
            popupMenu.show();
        });
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
                if(ValidaCampo(edBuscar.getText().toString()))
                {
                    Buscar(edBuscar.getText().toString());
                }
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
    private void CambiarEstatusPedido(String cEstatusNuevo)
    {
        boolean lAumenta=cEstatusNuevo.equals("recibido");
        String cEstatusAnterior=hashInfo.get("cEstatus");
        String cTipoPedido= hashInfo.get("cTipo");
        String cIdMenu=Global.RecuperaPreferencia("cIdMenu", this);
        HashMap<String, Object> hashMapUpdates= new HashMap<>();
        Log.d("DEBUGX", "pedidos/"+ cIdMenu+"/generados/"+cTipoPedido+ "/"+cEstatusAnterior+"/"+pedido.cKey);
        Log.d("DEBUGX", "pedidos/"+ cIdMenu+"/generados/"+ cTipoPedido+ "/"+cEstatusNuevo+"/"+ pedido.cKey);
        Log.d("DEBUGX", "pedidos/"+ cIdMenu+"/todo/"+pedido.cKey+"/cEstatus");
        Log.d("DEBUGX", "pedidos/"+ cIdMenu+"/count/"+cTipoPedido+"/data");
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/generados/"+cTipoPedido+ "/"+cEstatusAnterior+"/"+pedido.cKey, null);
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/generados/"+ cTipoPedido+ "/"+cEstatusNuevo+"/"+ pedido.cKey, pedido);
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/todo/"+pedido.cKey+"/cEstatus", cEstatusNuevo);
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/count/"+cTipoPedido+"/data", ServerValue.increment(lAumenta?1:-1));
        firebaseDatabase.getReference().updateChildren(hashMapUpdates).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {

                Toast.makeText(this, "El pedido fue "+ cEstatusNuevo +" correctamente", Toast.LENGTH_SHORT).show();
                Buscar(pedido.cKey);
            }
            else{
                Global.MostrarMensaje(this, "Error al procesar", "Se ha presentado un error" +
                        " al realizar el proceso, intenta de nuevo" + task.getException().getMessage());
            }
        });
    }
    private void ConfirmaCancelar()
    {
        if(pedido!=null)
        {
            AlertDialog.Builder alertDialog= new AlertDialog.Builder(this);
            alertDialog.setTitle("¿Cancelar pedido?");
            alertDialog.setIcon(R.drawable.ic_warning);
            alertDialog.setMessage("Se procederá con la cancelación del pedido");
            alertDialog.setPositiveButton("SI", (dialogInterface, i) ->{
               CambiarEstatusPedido("cancelado");
            });
            alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
            alertDialog.show();
        }
        else {
            Global.MostrarMensaje(this, "Error", "Debe ingresar un número de orden");
        }

    }
    private void ObtenerDetalles(){
        String cEstatus, cTipo, cIdOrden;
        cEstatus= hashInfo.get("cEstatus");
        cIdOrden=hashInfo.get("cIdOrden");
        cTipo=hashInfo.get("cTipo");
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
                            pedido=task.getResult().getValue(Pedido.class);
                            pedido.cKey=task.getResult().getKey();
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
        databaseReference.child("todo").child(cNumeroOrden)
                .get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        String cEstatus, cTipo;
                        if(task.isSuccessful())
                        {
                            if(task.getResult().exists())
                            {

                                DataSnapshot dataSnapshot=task.getResult();
                                String cNumeroOrdenG=dataSnapshot.getKey();
                                cEstatus=dataSnapshot
                                        .child("cEstatus")
                                        .getValue()==null?"recibido":dataSnapshot
                                        .child("cEstatus").getValue().toString();
                                cTipo=dataSnapshot
                                        .child("cTipo")
                                        .getValue()==null?"comedor":dataSnapshot
                                        .child("cTipo").getValue().toString();

                                hashInfo.put("cEstatus", cEstatus);
                                hashInfo.put("cTipo", cTipo);
                                hashInfo.put("cIdOrden", cNumeroOrdenG);
                                ActivaDesactivaMenu(cEstatus);
                                tvEstatus.setText(cEstatus.toUpperCase(Locale.ROOT));
                                imageViewTipo.setImageDrawable(getDrawable(getImageResourceId(cTipo)));
                                ObtenerDetalles();
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

    private void ActivaDesactivaMenu(String cEstatus) {
        switch (cEstatus)
        {
            case "recibido":
                popupMenu.getMenu().getItem(1).setVisible(false);
                popupMenu.getMenu().getItem(0).setVisible(true);
                popupMenu.getMenu().getItem(2).setVisible(true);
                break;
            case "cancelado":
                popupMenu.getMenu().getItem(1).setVisible(true);
                popupMenu.getMenu().getItem(0).setVisible(false);
                popupMenu.getMenu().getItem(2).setVisible(false);
                break;
            case "tomado":
                popupMenu.getMenu().getItem(1).setVisible(true);
                popupMenu.getMenu().getItem(0).setVisible(false);
                popupMenu.getMenu().getItem(2).setVisible(true);
                break;
        }
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