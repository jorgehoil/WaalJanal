package com.carloshoil.waaljanal.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.DTO.RegistroPago;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Values;

import java.util.Date;
import java.util.List;

public class PagosAdapter extends RecyclerView.Adapter<PagosAdapter.ViewHolder> {

    private Context context;
    private List<RegistroPago> lstRegistroPago;
    public PagosAdapter(List<RegistroPago> lstRegistroPago, Context context)
    {
        this.context=context;
        this.lstRegistroPago=lstRegistroPago;

    }
    @NonNull
    @Override
    public PagosAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_registropago,parent, false);
        return new  PagosAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PagosAdapter.ViewHolder holder, int position) {
        RegistroPago registroPago= lstRegistroPago.get(position);
        if(registroPago!=null)
        {
            holder.tvPaquete.setText(registroPago.cPaquete);
            holder.tvReferencia.setText(registroPago.cReferencia);
            holder.tvFolio.setText("Folio:"+registroPago.cFolio);
            holder.tvEstatus.setText(ObtenerInfoEstatus(registroPago.iEstatus));
            holder.tvFechaRegistro.setText(ObtenerFecha(registroPago.dateRegistro));
            holder.tvFechaPago.setText(registroPago.cFechaAprobacion);
            holder.btnMostrarInfo.setOnClickListener(view -> {
                Toast.makeText(context, registroPago.cMensaje, Toast.LENGTH_LONG).show();
            });
            holder.btnMostrarInfo.setEnabled(registroPago.iEstatus==Values.PAGO_ERROR);
            holder.btnMostrarInfo.setVisibility(registroPago.iEstatus==Values.PAGO_ERROR?View.VISIBLE: View.INVISIBLE);
            holder.imgViewEstatus.setImageResource(ObtenerImagen(registroPago.iEstatus));
            holder.imgViewEstatus.setColorFilter(ObtenerColor(registroPago.iEstatus));
            //holder.tvMensaje.setVisibility(registroPago.cMensaje.trim().isEmpty()?View.GONE: View.VISIBLE);
            //holder.ivInfoRechazo.setVisibility(registroPago.cMensaje.trim().isEmpty()?View.GONE: View.VISIBLE);
            holder.tvMensaje.setText(registroPago.cMensaje);
        }

    }

    private void AbrirRegistroPago(RegistroPago registroPago) {
        Intent i= new Intent(context, RegistroPago.class);

    }

    public int ObtenerColor(int iOpcion)
    {
        int iResultadoColor=0;
        switch (iOpcion)
        {
            case Values.PAGO_REVISION:
                iResultadoColor=Color.rgb(255,193,7);
                break;
            case Values.PAGO_APROBADO:
                iResultadoColor=Color.rgb(58,186,104);
                break;
            case Values.PAGO_ERROR:
                iResultadoColor=Color.rgb(221,99,110);
                break;
        }
        return iResultadoColor;
    }
    public int ObtenerImagen(int iOpcion)
    {
        int iResultado=0;
        switch (iOpcion)
        {
            case Values.PAGO_REVISION:
                iResultado=R.drawable.ic_time;
                break;
            case Values.PAGO_APROBADO:
                iResultado=R.drawable.ic_baseline_check_circle_24;
                break;
            case Values.PAGO_ERROR:
                iResultado=R.drawable.ic_info;
                break;
        }
        return iResultado;
    }
    public String ObtenerInfoEstatus(int iOpcion)
    {
        String cResultado="";
        switch (iOpcion)
        {
            case Values.PAGO_REVISION:
               cResultado="Pendiente";
                break;
            case Values.PAGO_APROBADO:
                cResultado="Aprobado";
                break;
            case Values.PAGO_ERROR:
               cResultado="No aprobado";
                break;
        }
        return cResultado;
    }
    public String ObtenerFecha(long TimeStamp)
    {
        Date date= new Date(TimeStamp);
        String c = DateFormat.format("dd/MM/yyyy hh:mm a", date).toString();
        return c;
    }
    public void AgregaPago(RegistroPago registroPago)
    {
        lstRegistroPago.add(registroPago);
        notifyItemInserted(lstRegistroPago.size()-1);

    }


    @Override
    public int getItemCount() {
        return lstRegistroPago.size();
    }
    public void LimpiaLista()
    {
        this.lstRegistroPago.clear();
        notifyDataSetChanged();
    }
    public void CargaDatos(List<RegistroPago> lstRegistroPago)
    {
        this.lstRegistroPago=lstRegistroPago;
        notifyDataSetChanged();
    }

    public static class ViewHolder  extends RecyclerView.ViewHolder{
        TextView tvFolio, tvFechaRegistro, tvFechaPago, tvReferencia, tvPaquete, tvEstatus, tvMensaje;
        Button btnMostrarInfo;
        ImageView imgViewEstatus, ivInfoRechazo;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFolio=itemView.findViewById(R.id.tvFolioPago);
            tvFechaRegistro=itemView.findViewById(R.id.tvFechaRegistroPago);
            tvFechaPago=itemView.findViewById(R.id.tvFechaPago);
            tvEstatus=itemView.findViewById(R.id.tvEstatusPago);
            tvReferencia=itemView.findViewById(R.id.tvNumeroOperacionPago);
            tvPaquete=itemView.findViewById(R.id.tvPaqPago);
            btnMostrarInfo=itemView.findViewById(R.id.btnMostrarInfo);
            imgViewEstatus=itemView.findViewById(R.id.imgEstatusPago);
            tvMensaje=itemView.findViewById(R.id.tvMensajePago);
            ivInfoRechazo=itemView.findViewById(R.id.ivInfoRechazo);
        }
    }
}
