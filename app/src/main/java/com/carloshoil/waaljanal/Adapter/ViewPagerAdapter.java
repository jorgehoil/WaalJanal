package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ActivityConfiguracion;
import com.carloshoil.waaljanal.DTO.ViewPagerData;
import com.carloshoil.waaljanal.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ViewPagerAdapter extends RecyclerView.Adapter<ViewPagerAdapter.ViewHolder> {
    private List<ViewPagerData> lstData;
    Context context;
    ActivityConfiguracion configuracion;
    public ViewPagerAdapter(List<ViewPagerData> lstData, Context context, ActivityConfiguracion activityConfiguracion)
    {
        this.lstData=lstData;
        this.context=context;
        this.configuracion=activityConfiguracion;
    }
    @NonNull
    @Override
    public ViewPagerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.view_pagerimages, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewPagerAdapter.ViewHolder holder, int position) {
        ViewPagerData pagerData= lstData.get(position);
        if(pagerData!=null)
        {
            if(pagerData.lUrl)
            {
                Picasso.get().load(pagerData.cUrl).placeholder(R.drawable.ic_time).into(holder.imageViewMenu);
            }
            else
            {
                if(pagerData.cUrl.equals("loc"))
                {
                    holder.imageViewMenu.setImageDrawable(context.getDrawable(R.drawable.ic_upload));
                }
            }
            holder.btnCambiaImagen.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                   configuracion.abrirCropperViewPager(pagerData.cKey, holder.getAdapterPosition());
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return lstData.size();
    }

    public void Actualiza(int iPosicion, ViewPagerData viewPagerData)
    {
        lstData.remove(iPosicion);
        lstData.add(iPosicion, viewPagerData);
        notifyItemChanged(iPosicion);
    }
    public List<ViewPagerData> obtenerDatos()
    {
        return this.lstData;
    }
    public static class ViewHolder  extends RecyclerView.ViewHolder{
        ImageView imageViewMenu;
        Button btnCambiaImagen;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageViewMenu=itemView.findViewById(R.id.ivImagenMenu);
            btnCambiaImagen=itemView.findViewById(R.id.btnCambiaImagen);

        }
    }
}
