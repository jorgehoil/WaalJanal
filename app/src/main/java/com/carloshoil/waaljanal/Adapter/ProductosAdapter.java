package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContentInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProductosAdapter extends RecyclerView.Adapter<ProductosAdapter.ViewHolder> {
    Context context;
    List<Producto> lstProducto;
    FragmentProductos fragmentProductos;
    FirebaseDatabase firebaseDatabase;
    StorageReference storageReference;
    String cIdMenu="";
    boolean lModificacionPrecios;
    ActivityResultLauncher<Intent> mStartForResult;
    public ProductosAdapter(Context context, List<Producto> lstProducto, FragmentProductos fragmentProductos, String cIdMenu, boolean lModificacionPrecios, ActivityResultLauncher<Intent> mStartForResult)
    {
        this.cIdMenu=cIdMenu;
        this.context=context;
        this.lstProducto=lstProducto;
        this.fragmentProductos=fragmentProductos;
        this.lModificacionPrecios=lModificacionPrecios;
        firebaseDatabase=FirebaseDatabase.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();
        this.mStartForResult=mStartForResult;
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
           holder.tvNombreProducto.setText(producto.cNombre);
           holder.ckProdDisp.setChecked(producto.lDisponible);
           if(lModificacionPrecios)
           {
               holder.edPrecio.setEnabled(producto.lstVariedad.size()==0);
               holder.tvPrecio.setText("$");
               holder.edPrecio.setText(producto.cPrecio);
               holder.ckProdDisp.setEnabled(false);
               holder.btnOpcionesProd.setVisibility(View.GONE);
               holder.edPrecio.setVisibility(View.VISIBLE);
               holder.edPrecio.addTextChangedListener(new TextWatcher() {
                   @Override
                   public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                   }

                   @Override
                   public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                       if(!charSequence.toString().isEmpty())
                       {
                           producto.cPrecio=charSequence.toString();
                       }
                       else {
                           producto.cPrecio="0";
                       }

                   }

                   @Override
                   public void afterTextChanged(Editable editable) {

                   }
               });

           }
           else {
               holder.edPrecio.setVisibility(View.GONE);
               holder.btnOpcionesProd.setVisibility(View.VISIBLE);
               holder.tvPrecio.setText("$"+(producto.cPrecio.isEmpty()?"--":producto.cPrecio));
               holder.ckProdDisp.setOnClickListener(view -> {
                   lstProducto.get(holder.getAdapterPosition()).lDisponible=holder.ckProdDisp.isChecked();
                   fragmentProductos.CargaOpcionPublicar(true);
               });
               holder.ckProdDisp.setEnabled(true);
               holder.btnOpcionesProd.setOnClickListener(view -> {
                   PopupMenu popupMenu= new PopupMenu(context, holder.btnOpcionesProd);
                   popupMenu.inflate(R.menu.menu_opciones_abc);
                   popupMenu.setOnMenuItemClickListener(menuItem -> {
                       int iIdItem=menuItem.getItemId();
                       if(iIdItem==R.id.eliminarABC)
                       {
                           ConfirmaEliminar(producto,holder.getAdapterPosition());
                       } else if(iIdItem==R.id.editarABC) {
                           Intent i= new Intent(context, ABCProductoActivity.class);
                           i.putExtra("cIdProducto",producto.cLlave);
                           mStartForResult.launch(i);
                       }

                       return false;
                   });
                   popupMenu.show();

               });
           }


        }
    }
    public void CargaModificarPrecios(boolean lModificacion)
    {
        lModificacionPrecios=lModificacion;
        notifyDataSetChanged();
    }
    public void ActualizaProducto(Producto producto)
    {

        int iPosition=0;
        int iContador=0;
        for(Producto producto1:lstProducto)
        {
            if(producto1.cLlave.equals(producto.cLlave))
            {
                iPosition=iContador;
            }
            iContador++;
        }
        Log.d("DEBUGX", "SE ACTUALIZA ITEM"+ iPosition+", " +iContador);
        lstProducto.remove(iPosition);
        lstProducto.add(iPosition,producto);
        notifyItemChanged(iPosition);

    }
    public void EliminaProductoLista(String cKey)
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
    public void MarcarTodos()
    {
        boolean lMarcar=true;
        int iMarcados=0, iDesMarcados=0;
        for(Producto producto: lstProducto)
        {
            if(producto.lDisponible)
                iMarcados++;
            else
                iDesMarcados++;
        }
        if(iMarcados>iDesMarcados)
        {
            lMarcar=false;
        }

        List<Producto> lstProductosTemp= new ArrayList<>();
        for(Producto producto: lstProducto)
        {
            producto.lDisponible=lMarcar;
            lstProductosTemp.add(producto);

        }
        lstProducto.clear();
        lstProducto.addAll(lstProductosTemp);
        notifyDataSetChanged();
    }
    public void EliminaProducto(Producto producto)
    {
        HashMap<String, Object> hashMapUpdate= new HashMap<>();
        hashMapUpdate.put("menus/"+cIdMenu+"/productos/"+producto.cLlave, null);
        hashMapUpdate.put("menus/"+cIdMenu+"/menu_publico/"+producto.cIdCategoria+"/"+producto.cLlave, null);
        firebaseDatabase.getReference().updateChildren(hashMapUpdate).addOnCompleteListener(task -> {
            if(task.isSuccessful())
            {
                Toast.makeText(context, "Producto eliminado", Toast.LENGTH_SHORT).show();
                if(!producto.cUrlImagenMin.isEmpty())
                {
                    storageReference.child("imgproductos/"+cIdMenu+ "/"+ producto.cLlave+ ".jpg").delete();
                    storageReference.child("imgproductosmin/"+cIdMenu+ "/"+ producto.cLlave+ ".jpg").delete();
                }
                EliminaProductoLista(producto.cLlave);

            }
            else
            {
                Global.MostrarMensaje(context, "Error", "No se ha podido eliminar el producto, intenta de nuevo");
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
        Button btnOpcionesProd;
        CheckBox ckProdDisp;
        EditText edPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreProducto=itemView.findViewById(R.id.tvProductoNombre);
            tvPrecio=itemView.findViewById(R.id.tvProductoPrecio);
            btnOpcionesProd=itemView.findViewById(R.id.btnOpcionesProd);
            ckProdDisp=itemView.findViewById(R.id.ckProdDisponible);
            edPrecio=itemView.findViewById(R.id.edPrecioProdMod);
        }
    }
    public void LimpiarLista()
    {
        lstProducto.clear();
        notifyDataSetChanged();
    }
    public void Agregar(List<Producto> lstProducto)
    {
        this.lstProducto.addAll(lstProducto);
        notifyDataSetChanged();
    }

}
