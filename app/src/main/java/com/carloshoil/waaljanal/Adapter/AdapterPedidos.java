package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Pedido;
import com.carloshoil.waaljanal.Dialog.DialogDetallePedido;
import com.carloshoil.waaljanal.FragmentPedidos;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class AdapterPedidos extends RecyclerView.Adapter<AdapterPedidos.ViewHolder> {

    Context context;
    List<Pedido> listPedidos;
    String cTipoPedidos;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    public AdapterPedidos(Context context, List<Pedido> listPedidos, String cTipoPedidos){
        this.context=context;
        this.listPedidos=listPedidos;
        this.cTipoPedidos=cTipoPedidos;
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
    }
    @NonNull
    @Override
    public AdapterPedidos.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_pedido,parent, false);
        return new AdapterPedidos.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPedidos.ViewHolder holder, int position) {
        Pedido pedido= listPedidos.get(holder.getAdapterPosition());
        if(pedido!=null)
        {
            holder.tvNombre.setText(pedido.cNombreCliente.isEmpty()?"Mesa # "+pedido.cMesa:pedido.cNombreCliente);
            holder.tvDireccion.setText(pedido.cDireccion.isEmpty()?"********":pedido.cDireccion);
            holder.tvNumeroOrden.setText("# "+pedido.cKey);
            holder.tvFecha.setText(ObtenerFecha(pedido.lFechaRegistro));
            holder.linearLayout.setOnClickListener(v->{
                AbrirDialogoPedidoDetalle(pedido.cDetallePedido, pedido);
            });
            holder.btnOpciones.setOnClickListener(v->{
                PopupMenu popupMenu= new PopupMenu(context, holder.btnOpciones);
                popupMenu.inflate(R.menu.menu_pedido);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int iIdItem=menuItem.getItemId();
                    if(iIdItem==R.id.detallesPedido)
                    {
                        AbrirDialogoPedidoDetalle(pedido.cDetallePedido, pedido);
                    }
                    else if(iIdItem==R.id.cancelarPedido)
                    {
                        MuestraConfirmacionCancelar(pedido);
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
    }

    private void MuestraConfirmacionCancelar(Pedido pedido) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(context);
        alertDialog.setTitle("¿Cancelar pedido?");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setMessage("Se procederá con la cancelación del pedido");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) ->{
            CancelarPedido(pedido);
        });
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    @Override
    public int getItemCount() {
        return listPedidos.size();
    }

    private void CancelarPedido(Pedido pedido)
    {
        String cIdMenu=Global.RecuperaPreferencia("cIdMenu", context);
        HashMap<String, Object> hashMapUpdates= new HashMap<>();
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/generados/"+ cTipoPedidos+ "/recibido/"+ pedido.cKey, null);
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/generados/"+ cTipoPedidos+ "/cancelado/"+ pedido.cKey, pedido);
        hashMapUpdates.put("pedidos/"+ cIdMenu+"/todo/"+pedido.cKey+"/cEstatus", "cancelado");
        hashMapUpdates.put("pedidos/"+ cIdMenu+ "/count/"+cTipoPedidos+"/data", ServerValue.increment(-1));
        databaseReference.updateChildren(hashMapUpdates).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(context, "El pedido fue cancelado", Toast.LENGTH_SHORT).show();
            }
            else{
                Global.MostrarMensaje(context, "Error al cancelar", "Se ha presentado un error" +
                        " al cancelar, intenta de nuevo");
            }
        });
    }

    public void EliminaItem(String cKey)
    {
        int iContador=0, iPosicion=0;
        for(Pedido pedido: listPedidos)
        {
            if(pedido.cKey.equals(cKey))
            {
                iPosicion=iContador;
            }
            iContador++;
        }
        listPedidos.remove(iPosicion);
        notifyItemRemoved(iPosicion);
        notifyItemRangeChanged(iPosicion, listPedidos.size());
    }
    public void LimpiaLista()
    {
        listPedidos.clear();
        notifyDataSetChanged();
    }
    private void AbrirDialogoPedidoDetalle(String cDetalles, Pedido pedido)
    {
        String[] arrayDetalles= cDetalles.split("/#/");
        List<String> lstDetalles= new ArrayList<>();
        for(String detalle: arrayDetalles)
        {
           lstDetalles.add(detalle);
        }
        DialogDetallePedido dialogDetallePedido= new DialogDetallePedido(context, lstDetalles, pedido,cTipoPedidos);
        dialogDetallePedido.show(((AppCompatActivity)context).getSupportFragmentManager(), "dialog_detalle_pedido");
        dialogDetallePedido.setCancelable(false);

    }
    public void AgregaItem(Pedido pedido)
    {
        listPedidos.add(pedido);
        notifyItemInserted(listPedidos.size()-1);
    }
    private String ObtenerFecha(long TimeStamp)
    {
        Date date= new Date(TimeStamp);
        String c = DateFormat.format("dd/MM/yyyy hh:mm a", date).toString();
        return c;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvFecha, tvNumeroOrden, tvDireccion;
        Button btnOpciones;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNumeroOrden=itemView.findViewById(R.id.tvNumeroPedidoRow);
            tvNombre=itemView.findViewById(R.id.tvNombrePedidoRow);
            tvFecha=itemView.findViewById(R.id.tvFechaPedidoRow);
            tvDireccion=itemView.findViewById(R.id.tvDireccionPedidoRow);
            btnOpciones=itemView.findViewById(R.id.btnOpcionesPedidoRow);
            linearLayout=itemView.findViewById(R.id.linearLayoutPedido);
        }
    }
}
