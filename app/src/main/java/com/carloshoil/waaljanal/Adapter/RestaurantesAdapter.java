package com.carloshoil.waaljanal.Adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.carloshoil.waaljanal.ActivityConfiguracion;
import com.carloshoil.waaljanal.ActivityImagenes;
import com.carloshoil.waaljanal.ActivityLogin;
import com.carloshoil.waaljanal.ActivityMenuOnline;
import com.carloshoil.waaljanal.ActivityPDF;
import com.carloshoil.waaljanal.ActivityResponsable;
import com.carloshoil.waaljanal.DTO.Restaurante;
import com.carloshoil.waaljanal.Dialog.DialogoABCRestaurante;
import com.carloshoil.waaljanal.R;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.storage.FirebaseStorage;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.HashMap;
import java.util.List;

public class RestaurantesAdapter  extends RecyclerView.Adapter<RestaurantesAdapter.ViewHolder> {
    Context context;
    List<Restaurante> lstRestaurante;
    private String CCLAVEMENU="cIdMenu";
    String cIdMenu="";
    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseStorage firebaseStorage;
    public RestaurantesAdapter(Context context, List<Restaurante> lstRestaurante)
    {
        this.context=context;
        this.lstRestaurante=lstRestaurante;
        cIdMenu=Global.RecuperaPreferencia(CCLAVEMENU, context);
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        firebaseStorage=FirebaseStorage.getInstance();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_restaurante,parent, false);
        return new RestaurantesAdapter.ViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Restaurante restaurante= lstRestaurante.get(position);
        if(restaurante!=null){
            holder.tvCodigoMenu.setText(restaurante.cIdMenu);
            holder.tvNombreRestaurante.setText(restaurante.cNombre);
            //holder.cardView.setCardBackgroundColor(cIdMenu.equals(restaurante.cIdMenu)?context.getColor(R.color.color2):Color.WHITE);
            holder.ivSeleccionado.setVisibility(cIdMenu.equals(restaurante.cIdMenu)?View.VISIBLE:View.INVISIBLE);
            holder.btnOpciones.setOnClickListener(view -> {
                PopupMenu popupMenu= new PopupMenu(context, holder.btnOpciones);
                popupMenu.inflate(R.menu.menu_options_restaurant);
                popupMenu.setOnMenuItemClickListener(menuItem -> {
                    int iIdItem=menuItem.getItemId();

                    if(iIdItem==R.id.administrarRes)
                    {
                        MarcarRestaurante(restaurante.cIdMenu);
                    } else if(iIdItem==R.id.informacionRes){
                        AbrirInformacion(restaurante.cIdMenu, restaurante.cLlave);
                    }
                    else if(iIdItem==R.id.generarQRRes)
                    {
                        AbrirQRGen(restaurante);
                    } else if(iIdItem==R.id.menuLinea){
                        AbrirMenuLinea(restaurante.cIdMenu);
                    } else if(iIdItem==R.id.eliminarMenu)
                    {
                        ConfirmaEliminar(restaurante);
                    } else if(iIdItem==R.id.copiarURL){
                        CopiarUrl(restaurante);
                    }else if(iIdItem==R.id.abrirImg){
                        abrirImagenes(restaurante.cIdMenu);
                    }
                    return false;
                });
                popupMenu.show();
            });
            holder.swDisponible.setChecked(restaurante.lDisponible);
            holder.swDisponible.setOnTouchListener(new View.OnTouchListener() {
                @SuppressLint("ClickableViewAccessibility")
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    ConfirmarAbrirCerrar(!restaurante.lDisponible,restaurante);
                    return false;

                }
            });


        }


    }

    private void CopiarUrl(Restaurante restaurante) {
        ClipboardManager clipboardManager= (ClipboardManager)context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData;
        String cCodigo= "waaljanal.web.app/menu.html?menu="+ restaurante.cIdMenu;
        if(!cCodigo.isEmpty())
        {
            clipData=ClipData.newPlainText("text", cCodigo);
            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(context, "¡Copiado correctamente!", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(context, "Error, no existe código de usuario", Toast.LENGTH_SHORT).show();
        }

    }

    private void ConfirmaEliminar(Restaurante restaurante) {
        String cIdMenuAdmin=Global.RecuperaPreferencia("cIdMenu", context);
        String cMensaje="";
        AlertDialog.Builder alertDialog= new AlertDialog.Builder(context);
        alertDialog.setTitle("¿Eliminar menú?");
        alertDialog.setIcon(R.drawable.ic_warning);
        alertDialog.setMessage(cMensaje+" Al eliminar el menú se eliminarán todos los productos y categtorías registradas en él");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) ->{
            if(cIdMenuAdmin.equals(restaurante.cIdMenu))
            {
                Global.GuardarPreferencias("cIdMenu","", context);
            }
            EliminaMenu(restaurante);
        });
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();

    }
    private void ConfirmarAbrirCerrar(boolean lDisponible, Restaurante restaurante){

        AlertDialog.Builder alertDialog= new AlertDialog.Builder(context);
        alertDialog.setTitle("¿"+(restaurante.lDisponible?"Cerrar":"Abrir")+"?");
        alertDialog.setIcon(R.drawable.ic_info);
        alertDialog.setMessage("El menú"+(lDisponible?"":" ya no")+" podrá ser visible ahora");
        alertDialog.setPositiveButton("SI", (dialogInterface, i) ->{
           MarcaDisponible(lDisponible,restaurante);
        });
        alertDialog.setNegativeButton("NO", (dialogInterface, i) -> dialogInterface.dismiss());
        alertDialog.show();
    }

    private void EliminaMenu(Restaurante restaurante) {
            if(restaurante!=null)
            {
                HashMap<String, Object> hashMapDelete= new HashMap<>();
                hashMapDelete.put("usuarios/"+firebaseAuth.getUid()+"/adminlugares/"+restaurante.cLlave, null);
                hashMapDelete.put("usuarios/"+firebaseAuth.getUid()+"/dataInfoUso/iTotalMenus", ServerValue.increment(-1));
                hashMapDelete.put("menus/"+restaurante.cIdMenu, null);

                firebaseDatabase.getReference().updateChildren(hashMapDelete).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(context, "¡Se ha eliminado el menú correctamente!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
    }

    private void AbrirMenuLinea(String cIdMenu) {
        Intent i= new Intent(context, ActivityMenuOnline.class);
        i.putExtra("cIdMenu", cIdMenu);

        context.startActivity(i);
    }
    private void MarcarRestaurante(String cIdMenuP) {
        Global.GuardarPreferencias(CCLAVEMENU,cIdMenuP, context);
        cIdMenu=cIdMenuP;
        notifyDataSetChanged();
    }

    private void AbrirQRGen(Restaurante restaurante) {
        Intent i= new Intent(context, ActivityPDF.class);
        i.putExtra("cIdMenu", restaurante.cIdMenu);
        i.putExtra("cNombre", restaurante.cNombre);
        context.startActivity(i);
    }

    private void AbrirInformacion(String cIdMenu, String cKey) {
        Intent i= new Intent(context, ActivityConfiguracion.class);
        i.putExtra("cIdMenu", cIdMenu);
        i.putExtra("cKeyRestaurante", cKey);
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
            if(rest.cLlave.equals(restaurante.cLlave))
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

    private void abrirImagenes(String cIdMenu)
    {
        Intent i= new Intent(context, ActivityImagenes.class);
        i.putExtra("cIdMenu", cIdMenu);
        context.startActivity(i);

    }
    public void MarcaDisponible(boolean lDisponible, Restaurante restaurante)
    {
        HashMap<String, Object> hashMap= new HashMap<>();
        hashMap.put("usuarios/"+firebaseAuth.getUid()+"/adminlugares/"+restaurante.cLlave+"/lDisponible",lDisponible);
        hashMap.put("menus/"+restaurante.cIdMenu+"/info/lDisponible", lDisponible);
        firebaseDatabase.getReference().updateChildren(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful())
                {
                    Toast.makeText(context, "¡Se ha "+(lDisponible?"abierto":"cerrado")+" el menú de "+restaurante.cNombre+"!", Toast.LENGTH_SHORT).show();
                }

            }
        });
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
        TextView tvNombreRestaurante, tvCodigoMenu;
        Button btnOpciones;
        ImageView ivSeleccionado;
        Switch swDisponible;
        CardView cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreRestaurante=itemView.findViewById(R.id.tvNombreRestauranteRow);
            ivSeleccionado=itemView.findViewById(R.id.ivRestauranteSeleccionado);
            btnOpciones=itemView.findViewById(R.id.btnOpcionesRes);
            tvCodigoMenu=itemView.findViewById(R.id.tvCodigoMenu);
            swDisponible=itemView.findViewById(R.id.swDisponible);
            cardView=itemView.findViewById(R.id.cardRestaurant);

        }
    }


}
