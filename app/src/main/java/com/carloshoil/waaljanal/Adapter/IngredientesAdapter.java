package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Ingrediente;

import java.util.List;

public class IngredientesAdapter extends RecyclerView.Adapter<IngredientesAdapter.ViewHolder> {

    Context context;
    List<Ingrediente> lstIngredientes;
    public IngredientesAdapter(Context context, List<Ingrediente> lstIngredientes)
    {
        this.context=context;
        this.lstIngredientes=lstIngredientes;
    }
    @NonNull
    @Override
    public IngredientesAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientesAdapter.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return lstIngredientes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void cargaIngredientes(List<Ingrediente> lstIngredientes)
    {
        this.lstIngredientes=lstIngredientes;
        notifyDataSetChanged();
    }
}
