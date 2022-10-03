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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ABCProductoActivity;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.FragmentProductos;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.core.utilities.Utilities;

import java.util.HashMap;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    Context context;
    List<Producto> lstProducto;
    FragmentProductos fragmentProductos;
    FirebaseDatabase firebaseDatabase;
    String cIdMenu="";
    public ProductosAdapter(Context context, List<Producto> lstProducto, FragmentProductos fragmentProductos, String cIdMenu)
    {
        this.cIdMenu=cIdMenu;
        this.context=context;
        this.lstProducto=lstProducto;
        this.fragmentProductos=fragmentProductos;
        firebaseDatabase=FirebaseDatabase.getInstance();
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
           holder.btnEliminarProd.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   ConfirmaEliminar(producto,holder.getAdapterPosition());
               }
           });
        }
    }
    private void EliminaProductoLista(String cKey)
    {
        int iPosicion=0, iContador=0;
        for(Producto producto: lstProducto)
        {
            if(producto.cLlave.equals(cKey))
            {
                iPosicion=iContador;
            }
            iContador++;
        }
        lstProducto.remove(iPosicion);
        notifyItemRemoved(iPosicion);
        notifyItemRangeChanged(iPosicion, lstProducto.size());
    }
    private  void EliminaProducto(Producto producto)
    {
        HashMap<String, Object> hashMapUpdate= new HashMap<>();
        hashMapUpdate.put("menus/"+cIdMenu+"/productos/"+producto.cLlave, null);
        hashMapUpdate.put("menus/"+cIdMenu+"/menu_publico/"+producto.cIdCategoria+"/"+producto.cLlave, null);
        firebaseDatabase.getReference().updateChildren(hashMapUpdate).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                    EliminaProductoLista(producto.cLlave);
                }
                else
                {
                    Global.MostrarMensaje(context, "Error", "No se ha podido eliminar el producto, intenta de nuevo");
                }
            }
        });

    }
    private void ConfirmaEliminar(Producto producto, int iPosicion) {

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(context);
        alertDialog.setTitle("¿Eliminar producto?");
        alertDialog.setMessage("Al eliminar el producto se eliminará de eliminará también del menú");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) ->EliminaProducto(producto));
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();

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
