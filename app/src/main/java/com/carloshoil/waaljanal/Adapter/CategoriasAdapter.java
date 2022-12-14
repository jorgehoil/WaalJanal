package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.DialogInterface;
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
            holder.layoutRowCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CargaDialogo(categoria);
                }
            });
            holder.btnEliminar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MuestraMensajeConfirmacion(categoria.cLlave, holder.getAdapterPosition());
                }
            });
        }
    }

    private void MuestraMensajeConfirmacion(String cLlave, int iPosition) {
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(context);
        alertDialog.setTitle("??Eliminar categoria?");
        alertDialog.setMessage("Al eliminar la categor??a se eliminar??n todos los productos relacionados a ??l.");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) -> EliminarCategoria(cLlave, iPosition));
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();

    }

    private void EliminaItem(int iPosition)
    {
        lstCategoria.remove(iPosition);
        notifyItemRemoved(iPosition);
        notifyItemRangeChanged(iPosition, lstCategoria.size());
    }
    private void EliminarCategoria(String cLlave, int iPosition) {
        databaseReference.child("menus").child(cIdMenu).child("productos").orderByChild("cIdCategoria").equalTo(cLlave).get().addOnCompleteListener(task -> {
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
                                " un error al borrar la categor??a, intenta de nuevo");
                    }

                });
            }
        });
    }

    private void CargaDialogo(Categoria categoria) {
         dialogoCategoria= new DialogoCategoria(context, categoria, "wjag1");
       dialogoCategoria.show(((AppCompatActivity)context).getSupportFragmentManager(),"dialogocat");
    }

    @Override
    public int getItemCount() {
        return lstCategoria.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCat;
        LinearLayout layoutRowCat;
        Button btnEliminar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCat=itemView.findViewById(R.id.tvNombreCat);
            layoutRowCat=itemView.findViewById(R.id.layoutRowCat);
            btnEliminar=itemView.findViewById(R.id.btnEliminarCat);
        }
    }
}
