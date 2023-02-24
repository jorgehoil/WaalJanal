package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ActivityConfiguracion;
import com.carloshoil.waaljanal.DTO.MenuPersonalizado;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.squareup.picasso.Picasso;

import java.util.ConcurrentModificationException;
import java.util.List;

public class PersonalizacionAdapter extends RecyclerView.Adapter<PersonalizacionAdapter.ViewHolder> {
    Context context;
    List<MenuPersonalizado> lstMenuPer;
    ActivityConfiguracion activityConfiguracion;
    MenuPersonalizado menuPersonalizadoSeleccionado=null;
    String cIdMenuPer;
    public PersonalizacionAdapter(Context context, List<MenuPersonalizado> lstMenuPer, ActivityConfiguracion activityConfiguracion, String cIdMenuPer)
    {
        this.activityConfiguracion=activityConfiguracion;
        this.cIdMenuPer=cIdMenuPer;
        this.context=context;
        this.lstMenuPer=lstMenuPer;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_menu_personalizado,parent, false);
        return new  PersonalizacionAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MenuPersonalizado menuPersonalizado= lstMenuPer.get(position);
        if(menuPersonalizado!=null)
        {
            if(cIdMenuPer.equals(menuPersonalizado.cKey))
            {
                holder.ckMenuPer.setChecked(true);
            }
            else
            {
                holder.ckMenuPer.setChecked(false);
            }
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    menuPersonalizadoSeleccionado=menuPersonalizado;
                    cIdMenuPer=menuPersonalizado.cKey;
                    activityConfiguracion.ConfiguraMenu(menuPersonalizadoSeleccionado);
                    notifyDataSetChanged();
                }
            });

            holder.tvNombreMenu.setText(menuPersonalizado.cNombre);
        }
    }

    public MenuPersonalizado obtenerSeleccionado()
    {
        if(menuPersonalizadoSeleccionado==null){
            for(MenuPersonalizado menuPersonalizadoLoc: lstMenuPer)
            {
                if(menuPersonalizadoLoc.cKey.equals(cIdMenuPer))
                {
                    menuPersonalizadoSeleccionado=menuPersonalizadoLoc;
                }
            }
        }
        return menuPersonalizadoSeleccionado;
    }


    @Override
    public int getItemCount() {
        return lstMenuPer.size();
    }
    public void Actualiza(List<MenuPersonalizado> lstMenuPer)
    {
        this.lstMenuPer=lstMenuPer;
        notifyDataSetChanged();
    }
    public void ActualizaIdMenuPer(String cIdMenuPer)
    {
        this.cIdMenuPer=cIdMenuPer;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvNombreMenu;
        CheckBox ckMenuPer;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreMenu=itemView.findViewById(R.id.tvNombreMenuPer);
            ckMenuPer=itemView.findViewById(R.id.ckMenuPer);
            linearLayout=itemView.findViewById(R.id.layoutMenuPer);
        }
    }
}
