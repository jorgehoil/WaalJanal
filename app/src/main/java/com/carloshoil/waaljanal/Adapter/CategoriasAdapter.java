package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Categoria;
import com.carloshoil.waaljanal.Dialog.DialogoCategoria;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class CategoriasAdapter extends RecyclerView.Adapter<CategoriasAdapter.ViewHolder> {
    Context context;
    List<Categoria> lstCategoria;
    DialogoCategoria dialogoCategoria;

    public CategoriasAdapter(Context context, List<Categoria> lstCategoria)
    {
        this.lstCategoria=lstCategoria;
        this.context=context;
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
            holder.ckDisponible.setChecked(categoria.lDisponible);
            holder.tvNombreCat.setText(categoria.cNombre);
            holder.layoutRowCat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    CargaDialogo(categoria);
                }
            });
        }
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
        CheckBox ckDisponible;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCat=itemView.findViewById(R.id.tvNombreCat);
            layoutRowCat=itemView.findViewById(R.id.layoutRowCat);
            ckDisponible=itemView.findViewById(R.id.ckDisponibleCat);

        }
    }
}
