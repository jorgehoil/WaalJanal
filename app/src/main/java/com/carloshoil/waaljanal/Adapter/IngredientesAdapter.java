package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Ingrediente;
import com.carloshoil.waaljanal.FragmentExtras;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class IngredientesAdapter extends RecyclerView.Adapter<IngredientesAdapter.ViewHolder> {

    Context context;
    List<Ingrediente> lstIngredientes;
    FragmentExtras fe;
    public IngredientesAdapter(Context context, List<Ingrediente> lstIngredientes, FragmentExtras fe)
    {
        this.context=context;
        this.lstIngredientes=lstIngredientes;
        this.fe=fe;
    }
    @NonNull
    @Override
    public IngredientesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_variante,parent, false);
        return new IngredientesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientesAdapter.ViewHolder holder, int position) {
        Ingrediente ingrediente=lstIngredientes.get(holder.getAdapterPosition());
        if(ingrediente!=null)
        {
            holder.tvNombre.setText(ingrediente.cNombre);
            holder.tvPrecio.setText(ingrediente.lPrecioAdicional?"$ "+ingrediente.cPrecio : "$ 0");
            holder.ckDisponible.setChecked(ingrediente.lDisponible);
            holder.ckDisponible.setOnCheckedChangeListener((compoundButton, b) -> {
                lstIngredientes.get(holder.getAdapterPosition()).lDisponible=holder.ckDisponible.isChecked();
            });
            holder.btnOpciones.setOnClickListener(v->{
                PopupMenu popupMenu= new PopupMenu(context, holder.btnOpciones);
                popupMenu.inflate(R.menu.menu_opciones_abc);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int iIdItem=menuItem.getItemId();
                    if(iIdItem==R.id.eliminarABC)
                    {
                        eliminaIngrediente(holder.getAdapterPosition());
                    } else if(iIdItem==R.id.editarABC) {
                        fe.AbreDialogo(ingrediente);
                    }
                    return false;
                });
                popupMenu.show();
            });
        }
    }

    private void eliminaIngrediente(int iPosition) {
        lstIngredientes.remove(iPosition);
        notifyItemRemoved(iPosition);
        notifyItemRangeChanged(iPosition, lstIngredientes.size());
    }
    public List<Ingrediente> ObtenerIngredientes()
    {
        return lstIngredientes;
    }

    @Override
    public int getItemCount() {
        return lstIngredientes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPrecio, tvNombre;
        CheckBox ckDisponible;
        Button btnOpciones;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPrecio=itemView.findViewById(R.id.tvPrecioRowVar);
            tvNombre=itemView.findViewById(R.id.tvNombreRowVar);
            ckDisponible=itemView.findViewById(R.id.ckDisponibleRowVar);
            btnOpciones=itemView.findViewById(R.id.btnOpcionesRowVar);
        }
    }

    public void AgregaIngrediente(Ingrediente ingrediente) {
        lstIngredientes.add(ingrediente);
        notifyItemInserted(lstIngredientes.size()-1);
    }

    public void cargaIngredientes(List<Ingrediente> lstIngredientes)
    {
        this.lstIngredientes=lstIngredientes;
        notifyDataSetChanged();
    }
}
