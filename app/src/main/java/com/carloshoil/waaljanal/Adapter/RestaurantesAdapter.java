package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Restaurante;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class RestaurantesAdapter  extends RecyclerView.Adapter<RestaurantesAdapter.ViewHolder> {
    Context context;
    List<Restaurante> lstRestaurante;
    public RestaurantesAdapter(Context context, List<Restaurante> lstRestaurante)
    {
        this.context=context;
        this.lstRestaurante=lstRestaurante;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lstRestaurante.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreRestaurante;
        CheckBox ckSelecionado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRestaurante=itemView.findViewById(R.id.tvNombreRestauranteRow);
            ckSelecionado=itemView.findViewById(R.id.ckRestauranteSeleccionado);

        }
    }
}
