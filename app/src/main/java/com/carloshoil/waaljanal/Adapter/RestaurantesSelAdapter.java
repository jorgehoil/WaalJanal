package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Restaurante;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class RestaurantesSelAdapter extends RecyclerView.Adapter<RestaurantesSelAdapter.ViewHolder> {
    Context context;
    List<Restaurante> lstRestaurantes;
    String cIdTemp="";
    public RestaurantesSelAdapter(List<Restaurante> lstRestaurantes, Context context)
    {
        this.context=context;
        this.lstRestaurantes=lstRestaurantes;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_restaurante_selec,parent, false);
        return new RestaurantesSelAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurante restaurante= lstRestaurantes.get(position);
        if(restaurante!=null)
        {
            holder.tvNombreRest.setText(restaurante.cNombre);
            holder.ckSeleccionado.setChecked(cIdTemp.equals(restaurante.cIdMenu));
            holder.ckSeleccionado.setOnCheckedChangeListener((compoundButton, b) -> {
                if(b)
                {
                    cIdTemp=restaurante.cIdMenu;
                    notifyDataSetChanged();
                }

            });

        }
    }

    public String ObtenerSeleccionado()
    {
        return cIdTemp;
    }
    public void CargaDatos(List<Restaurante> lstRestaurantes)
    {
        this.lstRestaurantes.clear();
        this.lstRestaurantes=lstRestaurantes;
        notifyDataSetChanged();
    }
    @Override
    public int getItemCount() {
        return lstRestaurantes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreRest;
        CheckBox ckSeleccionado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRest=itemView.findViewById(R.id.tvRestaurantSel);
            ckSeleccionado=itemView.findViewById(R.id.ckRestaurantSel);
        }
    }
}
