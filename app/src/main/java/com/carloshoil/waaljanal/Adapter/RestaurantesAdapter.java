package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ActivityConfiguracion;
import com.carloshoil.waaljanal.ActivityLogin;
import com.carloshoil.waaljanal.ActivityResponsable;
import com.carloshoil.waaljanal.DTO.Restaurante;
import com.carloshoil.waaljanal.Dialog.DialogoABCRestaurante;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;

import java.util.List;

public class RestaurantesAdapter  extends RecyclerView.Adapter<RestaurantesAdapter.ViewHolder> {
    Context context;
    List<Restaurante> lstRestaurante;
    private String CCLAVEMENU="cIdMenu";
    String cIdMenu="";
    public RestaurantesAdapter(Context context, List<Restaurante> lstRestaurante)
    {
        this.context=context;
        this.lstRestaurante=lstRestaurante;
        cIdMenu=Global.RecuperaPreferencia(CCLAVEMENU, context);
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_restaurante,parent, false);
        return new RestaurantesAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurante restaurante= lstRestaurante.get(position);
        if(restaurante!=null){
            holder.tvNombreRestaurante.setText(restaurante.cNombre);
            holder.ckSelecionado.setChecked(cIdMenu.equals(restaurante.cIdMenu));
            holder.btnOpciones.setOnClickListener(view -> {
                PopupMenu popupMenu= new PopupMenu(context, holder.btnOpciones);
                popupMenu.inflate(R.menu.menu_options_restaurant);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    switch (menuItem.getItemId())
                    {
                        case R.id.administrarRes:
                            MarcarRestaurante(restaurante.cIdMenu);
                            break;
                        case R.id.responsableRes:
                            AbrirResponsable();
                            break;
                        case R.id.informacionRes:
                            AbrirInformacion(restaurante.cIdMenu);
                            break;
                        case R.id.generarQRRes:
                            AbrirQRGen();
                            break;
                        case R.id.cambiarNombreRes:
                            AbrirCambiarNombre(restaurante.cLlave, restaurante.cNombre);
                            break;
                    }
                    return false;
                });
                popupMenu.show();
            });


        }


    }
    private void AbrirCambiarNombre(String cKey, String cNombre)
    {
        DialogoABCRestaurante dialogoABCRestaurante= new DialogoABCRestaurante(context,cKey,cNombre);
        dialogoABCRestaurante.show(((AppCompatActivity)context).getSupportFragmentManager(), "dialogoABCRest");
    }

    private void MarcarRestaurante(String cIdMenuP) {
        Global.GuardarPreferencias(CCLAVEMENU,cIdMenuP, context);
        cIdMenu=cIdMenuP;
        notifyDataSetChanged();
    }

    private void AbrirResponsable() {
        Intent i= new Intent(context, ActivityResponsable.class);
        context.startActivity(i);
    }

    private void AbrirQRGen() {
        Toast.makeText(context, "Se abre generar QR", Toast.LENGTH_SHORT).show();
        /*Intent i= new Intent(context, .class);
        context.startActivity(i); */
    }

    private void AbrirInformacion(String cKey) {
        Intent i= new Intent(context, ActivityConfiguracion.class);
        i.putExtra("cIdMenu", cKey);
        context.startActivity(i);
    }
    public void LimpiaLista()
    {
        this.lstRestaurante.clear();
        notifyDataSetChanged();
    }

    public void ActualizaRestaurante(Restaurante restaurante)
    {
        int iContador=0;
        int iPosicion=0;
        for(Restaurante rest: lstRestaurante)
        {
            if(rest.cLlave==restaurante.cLlave)
            {
                iPosicion=iContador;
            }
            iContador++;
        }
        lstRestaurante.remove(iPosicion);
        lstRestaurante.add(iPosicion, restaurante);
        notifyItemChanged(iPosicion);
    }
    public void EliminaRestaurante(String cKey)
    {
        int iContador=0;
        int iPosicion=0;
        for(Restaurante restaurante: lstRestaurante)
        {
            if(restaurante.cLlave.equals(cKey))
            {
                iPosicion=iContador;
            }
            iContador++;
        }
        lstRestaurante.remove(iPosicion);
        notifyItemRemoved(iPosicion);
        notifyItemRangeChanged(iPosicion, lstRestaurante.size());
    }
    public void AgregaRestaurante(Restaurante restaurante)
    {
        this.lstRestaurante.add(restaurante);
        notifyItemInserted(lstRestaurante.size()-1);
    }

    @Override
    public int getItemCount() {
        return lstRestaurante.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreRestaurante;
        Button btnOpciones;
        CheckBox ckSelecionado;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRestaurante=itemView.findViewById(R.id.tvNombreRestauranteRow);
            ckSelecionado=itemView.findViewById(R.id.ckRestauranteSeleccionado);
            btnOpciones=itemView.findViewById(R.id.btnOpcionesRes);

        }
    }
}
