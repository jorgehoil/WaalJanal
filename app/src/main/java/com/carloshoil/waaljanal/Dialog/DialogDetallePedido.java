package com.carloshoil.waaljanal.Dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.Adapter.AdapterPedidoDetalle;
import com.carloshoil.waaljanal.Adapter.AdapterPedidos;
import com.carloshoil.waaljanal.DTO.Pedido;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DialogDetallePedido extends DialogFragment {
    Context context;
    List<String> lstDetalles;
    RecyclerView recyclerViewDetalles;
    TextView tvOrden, tvPrecio, tvNombreCliente, tvDireccion, tvMesa;
    Button btnCerrar, btnTomar;
    Pedido pedido;
    AdapterPedidoDetalle adapterPedidoDetalle;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ProgressBar progressBarCargaDetalle;
    String cTipoPedido="";
    public DialogDetallePedido(Context context, List<String> lstDetalles, Pedido pedido, String cTipoPedido)
    {
        this.context=context;
        this.lstDetalles=lstDetalles;
        this.pedido=pedido;
        this.cTipoPedido=cTipoPedido;
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return creaDialogo();
    }
    private Dialog creaDialogo()
    {
        AlertDialog.Builder dialog= new AlertDialog.Builder(context);
        LayoutInflater layoutInflater= getActivity().getLayoutInflater();
        View view =layoutInflater.inflate(R.layout.dialog_resumen_pedido, null);
        dialog.setView(view);
        btnCerrar=view.findViewById(R.id.btnCerrarPedidoDetalle);
        btnTomar=view.findViewById(R.id.btnTomarPedidoDialog);
        tvOrden=view.findViewById(R.id.tvNumPedidoDetalle);
        tvPrecio=view.findViewById(R.id.tvPrecioPedidoDetalle);
        tvMesa=view.findViewById(R.id.tvMesaPedidoDetalle);
        tvDireccion=view.findViewById(R.id.tvDireccionPedidoDetalle);
        tvNombreCliente=view.findViewById(R.id.tvClientePedidoDetalle);
        progressBarCargaDetalle=view.findViewById(R.id.pbCargaDetalle);
        recyclerViewDetalles=view.findViewById(R.id.recycleViewDetallePedido);
        btnCerrar.setOnClickListener(v->{
            dismiss();
        });
        btnTomar.setOnClickListener(v->{
            ProcesaTomar(pedido);
        });
        CargaDatos();
        return dialog.create();

    }

    private void IniciaAdapter() {

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewDetalles.setLayoutManager(linearLayoutManager);
        adapterPedidoDetalle = new AdapterPedidoDetalle(getActivity(), lstDetalles);
        recyclerViewDetalles.setAdapter(adapterPedidoDetalle);
    }

    private void CargaDatos() {
        IniciaAdapter();
        tvPrecio.setText("$ "+ pedido.cTotal);
        tvOrden.setText("Detalle de Pedido #"+ pedido.cKey);
        tvNombreCliente.setText(pedido.cNombreCliente);
        tvDireccion.setText(pedido.cDireccion);
        tvMesa.setText(pedido.cMesa);
    }

    private void ProcesaTomar(Pedido pedido) {
        String cIdMenu= Global.RecuperaPreferencia("cIdMenu", getActivity());
        HashMap<String, Object> hashMapUpdates= new HashMap<>();
        if(!cIdMenu.isEmpty())
        {
            btnTomar.setEnabled(false);
            btnCerrar.setEnabled(false);
            progressBarCargaDetalle.setVisibility(View.VISIBLE);
            hashMapUpdates.put("pedidos/"+ cIdMenu+"/generados/"+cTipoPedido+"/recibido/"+pedido.cKey, null);
            hashMapUpdates.put("pedidos/"+ cIdMenu+"/generados/"+cTipoPedido+"/tomado/"+pedido.cKey, pedido);
            hashMapUpdates.put("pedidos/"+ cIdMenu+"/todo/"+pedido.cKey+"/cEstatus", "tomado");
            hashMapUpdates.put("pedidos/"+ cIdMenu+"/count/"+cTipoPedido+"/data", ServerValue.increment(-1));
            databaseReference.updateChildren(hashMapUpdates).addOnCompleteListener(task -> {
                btnTomar.setEnabled(true);
                btnCerrar.setEnabled(true);
                progressBarCargaDetalle.setVisibility(View.INVISIBLE);
                if(task.isSuccessful())
                {
                    Toast.makeText(context, "¡Orden tomada!", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
                else{
                    Toast.makeText(context, "Error al tomar orden, intenta de nuevo", Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(context, "Error al procesar, intente de nuevo", Toast.LENGTH_SHORT).show();
        }
    }
}
