package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Categoria;
import com.carloshoil.waaljanal.Dialog.DialogoCategoria;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.ViewHolder> {
    Context context;
    List<Categoria> lstCategoria;
    DialogoCategoria dialogoCategoria;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    String cIdMenu;


    public CategoriasAdapter(Context context, List<Categoria> lstCategoria,String cIdMenu)
    {
        this.lstCategoria=lstCategoria;
        this.context=context;
        database=FirebaseDatabase.getInstance();
        databaseReference=database.getReference();
        this.cIdMenu=cIdMenu;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_categoria,parent, false);
        return new  CategoriasAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Categoria categoria= lstCategoria.get(position);
        if(categoria!=null)
        {
            holder.tvNombreCat.setText(categoria.cNombre);

            holder.btnOpciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    PopupMenu popupMenu= new PopupMenu(context, holder.btnOpciones);
                    popupMenu.inflate(R.menu.menu_opciones_abc);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            int iIdItem=menuItem.getItemId();
                            if(iIdItem==R.id.eliminarABC)
                            {
                                MuestraMensajeConfirmacion(categoria.cLlave, holder.getAdapterPosition());
                            } else if(iIdItem==R.id.editarABC)
                            {
                                CargaDialogo(categoria);
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                    //
                }
            });
        }
    }

    private void MuestraMensajeConfirmacion(String cLlave, int iPosition) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(context);
        alertDialog.setTitle("¿Eliminar categoria?");
        alertDialog.setMessage("Al eliminar la categoría se eliminarán todos los productos relacionados a él.");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) -> EliminarCategoria(cLlave, iPosition));
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();

    }

    public void LimpiaLista()
    {
        this.lstCategoria.clear();
        notifyDataSetChanged();
    }
    private void EliminaItem(int iPosition)
    {
        lstCategoria.remove(iPosition);
        notifyItemRemoved(iPosition);
        notifyItemRangeChanged(iPosition, lstCategoria.size());
    }
    private void EliminarCategoria(String cLlave, int iPosition) {
        databaseReference.child("menus").child(cIdMenu).child("productos")
                .orderByChild("cIdCategoria").equalTo(cLlave).get().addOnCompleteListener(task -> {
                    HashMap<String, Object> hashMapDelete= new HashMap<>();
                    if(task.isSuccessful())
                    {
                        for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                        {
                            hashMapDelete.put("menus/"+cIdMenu+"/productos/"+dataSnapshot.getKey(), null);
                        }
                        hashMapDelete.put("menus/"+cIdMenu+"/categorias/"+cLlave, null);
                        hashMapDelete.put("menus/"+cIdMenu+"/menu_publico/"+cLlave, null);
                        databaseReference.updateChildren(hashMapDelete).addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful())
                            {
                                EliminaItem(iPosition);
                                Toast.makeText(context, "Se ha eliminado correctamente", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                Global.MostrarMensaje(context, "Error", "Se ha presentado" +
                                        " un error al borrar la categoría, intenta de nuevo");
                            }

                        });
                    }
        });
    }
    public void ModificaCategoriaLista(Categoria categoria)
    {
        int iPosicion=0, iItemMod=0;
        for(Categoria cat: lstCategoria)
        {

            if(categoria.cLlave==cat.cLlave)
            {
                iItemMod=iPosicion;
            }
            iPosicion++;
        }
        lstCategoria.remove(iItemMod);
        lstCategoria.add(iItemMod, categoria);
        notifyItemChanged(iItemMod);
    }
    public void AgregaCategoriaLista(Categoria categoria)
    {
          this.lstCategoria.add(categoria);
          notifyItemInserted(lstCategoria.size()-1);
    }
    private void CargaDialogo(Categoria categoria) {
        dialogoCategoria= new DialogoCategoria(context, categoria, cIdMenu);
       dialogoCategoria.show(((AppCompatActivity)context).getSupportFragmentManager(),"dialogocat");
    }

    @Override
    public int getItemCount() {
        return lstCategoria.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCat;
        Button btnOpciones;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCat=itemView.findViewById(R.id.tvNombreCat);
            btnOpciones=itemView.findViewById(R.id.btnOpcionesCat);
        }
    }
}
