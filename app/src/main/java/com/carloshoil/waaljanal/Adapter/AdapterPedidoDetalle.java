package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.R;

import java.util.List;

public class AdapterPedidoDetalle extends RecyclerView.Adapter<AdapterPedidoDetalle.ViewHolder> {
    List<String> lstDetalle;
    Context context;
    public AdapterPedidoDetalle(Context context, List<String> lstDetalle)
    {
        this.context=context;
        this.lstDetalle=lstDetalle;
    }
    @NonNull
    @Override
    public AdapterPedidoDetalle.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_resumen_pedido,parent, false);
        return new AdapterPedidoDetalle.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterPedidoDetalle.ViewHolder holder, int position) {
        String cPedido=lstDetalle.get(holder.getAdapterPosition());
        if(cPedido!=null&&!cPedido.isEmpty())
        {
            holder.tvDetalle.setText(cPedido);
        }
    }

    @Override
    public int getItemCount() {
        return lstDetalle.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDetalle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDetalle=itemView.findViewById(R.id.tvProductoPedidoResumenRow);
        }
    }
}
