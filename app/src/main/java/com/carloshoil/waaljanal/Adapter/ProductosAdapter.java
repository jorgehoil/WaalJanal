package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ABCProductoActivity;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.FragmentProductos;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    Context context;
    List<Producto> lstProducto;
    FragmentProductos fragmentProductos;
    public ProductosAdapter(Context context, List<Producto> lstProducto, FragmentProductos fragmentProductos)
    {
        this.context=context;
        this.lstProducto=lstProducto;
        this.fragmentProductos=fragmentProductos;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_producto,parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Producto producto= lstProducto.get(position);
        if(producto!=null)
        {
           holder.ckProdDisp.setChecked(producto.lDisponible);
           holder.tvPrecio.setText("$"+producto.cPrecio);
           holder.tvNombreProducto.setText(producto.cNombre);
           holder.layoutRow.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   Intent i= new Intent(context, ABCProductoActivity.class);
                   i.putExtra("cIdProducto",producto.cLlave);
                   context.startActivity(i);
               }
           });
           holder.ckProdDisp.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   lstProducto.get(holder.getAdapterPosition()).lDisponible=holder.ckProdDisp.isChecked();
                   fragmentProductos.MostrarBotonPublicar(true);
               }
           });
        }
    }
    public List<Producto> getLstProducto()
    {
        return this.lstProducto;
    }

    @Override
    public int getItemCount() {
        return lstProducto.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNombreProducto, tvPrecio;
        Button btnEliminarProd;
        CheckBox ckProdDisp;
        LinearLayout layoutRow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto=itemView.findViewById(R.id.tvProductoNombre);
            tvPrecio=itemView.findViewById(R.id.tvProductoPrecio);
            btnEliminarProd=itemView.findViewById(R.id.btnEliminarProd);
            ckProdDisp=itemView.findViewById(R.id.ckProdDisponible);
            layoutRow=itemView.findViewById(R.id.layoutRowProd);
        }
    }
    public void LimpiarLista() {
        lstProducto.clear();
        notifyDataSetChanged();
    }
    public void Agregar(List<Producto> lstProducto)
    {
        this.lstProducto.addAll(lstProducto);
        notifyDataSetChanged();
    }

}
