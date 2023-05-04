package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.Suscripcion;
import com.carloshoil.waaljanal.R;

import java.util.List;

public class SuscripcionAdapter extends RecyclerView.Adapter<SuscripcionAdapter.ViewHolder> {

    Context context;
    List<Suscripcion> lstSuscripcion;
    public SuscripcionAdapter(Context context, List<Suscripcion> lstSuscripcion)
    {
        this.context=context;
        this.lstSuscripcion=lstSuscripcion;

    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_suscripcion_opcion,parent, false);
        return new SuscripcionAdapter.ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Suscripcion suscripcion= lstSuscripcion.get(position);
            if(suscripcion!=null)
            {
                holder.tvNombreSus.setText(suscripcion.cNombre);
                holder.tvPrecioSus.setText(suscripcion.cPrecio);
                holder.btnSuscribirse.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       AbrirExploradorSuscripcion(suscripcion.cUrl);
                    }
                });
                holder.tvAhorroSus.setText(suscripcion.cAhorro.isEmpty()?"":"Ahorra un " +suscripcion.cAhorro +"%");
            }

    }

    private void AbrirExploradorSuscripcion(String cUrl) {
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(cUrl));
        context.startActivity(browserIntent);

    }

    @Override
    public int getItemCount() {
        return lstSuscripcion.size();
    }

    public void AgregaLista(List<Suscripcion> lstSuscripcion)
    {
        this.lstSuscripcion=lstSuscripcion;
        notifyDataSetChanged();
    }
    public void LimpiarLista()
    {
        this.lstSuscripcion.clear();
        notifyDataSetChanged();
    }
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreSus, tvPrecioSus, tvAhorroSus;
        Button btnSuscribirse;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreSus=itemView.findViewById(R.id.tvTituloSus);
            tvPrecioSus=itemView.findViewById(R.id.tvPrecioSus);
            btnSuscribirse=itemView.findViewById(R.id.btnComprarSus);
            tvAhorroSus=itemView.findViewById(R.id.tvAhorroSus);
        }
    }
}
