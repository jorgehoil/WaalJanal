package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ABCProductoActivity;
import com.carloshoil.waaljanal.DTO.Variedad;
import com.carloshoil.waaljanal.FragmentVariedades;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class VariedadesAdapter extends RecyclerView.Adapter <VariedadesAdapter.ViewHolder> {
    Context context;
    List<Variedad> lstVariedad;
    FragmentVariedades fv;
    public VariedadesAdapter(Context context, List<Variedad> lstVariedad, FragmentVariedades fv)
    {
        this.context=context;
        this.lstVariedad=lstVariedad;
        this.fv=fv;
    }
    @NonNull
    @Override
    public VariedadesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_variante,parent, false);
        return new  VariedadesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VariedadesAdapter.ViewHolder holder, int position) {
        Variedad variedad= lstVariedad.get(holder.getAdapterPosition());
        if(variedad!=null)
        {
            holder.tvPrecio.setText("$ " +variedad.cPrecio);
            holder.tvNombre.setText(variedad.cNombre);
            holder.ckDisponible.setChecked(variedad.lDisponible);
            holder.ckDisponible.setOnCheckedChangeListener((compoundButton, b) -> {
                lstVariedad.get(holder.getAdapterPosition()).lDisponible=holder.ckDisponible.isChecked();
            });
            holder.btnOpciones.setOnClickListener(v->{
                PopupMenu popupMenu= new PopupMenu(context, holder.btnOpciones);
                popupMenu.inflate(R.menu.menu_opciones_abc);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int iIdItem=menuItem.getItemId();
                    if(iIdItem==R.id.eliminarABC)
                    {
                       eliminaVariedad(holder.getAdapterPosition());
                    } else if(iIdItem==R.id.editarABC) {
                        fv.abrirEdicion(variedad);
                    }

                    return false;
                });
                popupMenu.show();
            });
        }

    }

    @Override
    public int getItemCount() {
        return lstVariedad.size();
    }


    public void agregaVariedad(Variedad variedad)
    {
        int iPosition=0, iContador=0;
        if(variedad.cKey.isEmpty())
        {
            lstVariedad.add(variedad);
            notifyItemInserted(lstVariedad.size()-1);
        }
        else{
            for(Variedad variedadItem: lstVariedad)
            {
                if(variedad.cKey.equals(variedadItem.cKey))
                {
                    iPosition=iContador;
                }
                iContador++;
            }
            lstVariedad.remove(iPosition);
            lstVariedad.add(iPosition, variedad);
            notifyItemChanged(iPosition);
        }

    }
    public void eliminaVariedad(int iPosition)
    {
        lstVariedad.remove(iPosition);
        notifyItemRemoved(iPosition);
        notifyItemRangeChanged(iPosition, lstVariedad.size());
    }
    public void cargaVariedades(List<Variedad> _lstVariedad)
    {
        this.lstVariedad=_lstVariedad;
        notifyDataSetChanged();
    }
    public List<Variedad> obtenerVariedades()
    {
        return lstVariedad;
    }

    public void limpiar() {
        lstVariedad.clear();
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox ckDisponible;
        TextView tvNombre, tvPrecio;
        Button btnOpciones;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ckDisponible=itemView.findViewById(R.id.ckDisponibleRowVar);
            tvNombre=itemView.findViewById(R.id.tvNombreRowVar);
            tvPrecio=itemView.findViewById(R.id.tvPrecioRowVar);
            btnOpciones=itemView.findViewById(R.id.btnOpcionesRowVar);
        }
    }
}
