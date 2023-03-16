package com.carloshoil.waaljanal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.PagosAdapter;
import com.carloshoil.waaljanal.Adapter.SuscripcionAdapter;
import com.carloshoil.waaljanal.DTO.RegistroPago;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRegistroPago#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRegistroPago extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FloatingActionButton fbNuevoRegistroPago;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewPagos;
    private PagosAdapter pagosAdapter;
    private ChildEventListener childEventListenerPagos;
    private FirebaseAuth firebaseAuth;
    boolean lExistePendiente=false;
    ProgressBar pbCargaPagos;


    public FragmentRegistroPago() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRegistroPago.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRegistroPago newInstance(String param1, String param2) {
        FragmentRegistroPago fragment = new FragmentRegistroPago();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }
    private void PreparaAdapter()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewPagos.setLayoutManager(linearLayoutManager);
        pagosAdapter= new PagosAdapter(new ArrayList<>(), getActivity());
        recyclerViewPagos.setAdapter(pagosAdapter);
    }
    private void ObtenerPagos()
    {
        List<RegistroPago> lsRegistroPagos= new ArrayList<>();
        databaseReference.child("usuarios")
                .child(firebaseAuth.getUid())
                .child("registropagos")
                .limitToLast(3)
                .orderByChild("datePago")
                .get()
                .addOnCompleteListener(task -> {
                    fbNuevoRegistroPago.setEnabled(true);
                    pbCargaPagos.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {

                        for(DataSnapshot snapshot:task.getResult().getChildren())
                        {
                            RegistroPago registroPago= new RegistroPago();
                            registroPago.cFolio=snapshot.getKey();
                            registroPago.iEstatus=snapshot.child("iEstatus").getValue()==null?0:
                                    snapshot.child("iEstatus").getValue(Integer.class);
                            registroPago.dateRegistro=snapshot.child("dateRegistro").getValue()==null?0:
                                    snapshot.child("dateRegistro").getValue(Long.class);
                            registroPago.cFechaAprobacion=snapshot.child("cFechaPago").getValue()==null?"--/--/----":
                                    snapshot.child("cFechaPago").getValue().toString();
                            registroPago.cReferencia=snapshot.child("cReferencia").getValue()==null?"---":
                                    snapshot.child("cReferencia").getValue().toString();
                            registroPago.cPaquete=snapshot.child("cPaquete").getValue()==null?"--":
                                    snapshot.child("cPaquete").getValue().toString();
                            registroPago.cMensaje=snapshot.child("cMensaje").getValue()==null?"":
                                    snapshot.child("cMensaje").getValue().toString();
                            if(registroPago.iEstatus== Values.PAGO_REVISION)
                            {
                                lExistePendiente=true;
                            }
                           lsRegistroPagos.add(registroPago);
                        }
                       Collections.reverse(lsRegistroPagos);
                        pagosAdapter.CargaDatos(lsRegistroPagos);

                    }
                    else {
                        Toast.makeText(getActivity(), "Error al cargar", Toast.LENGTH_SHORT).show();
                    }
                });

    }
    private void Init(View view)
    {

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        pbCargaPagos=view.findViewById(R.id.pbCargaPagos);
        fbNuevoRegistroPago=view.findViewById(R.id.fbtnNuevoRegistroPago);
        fbNuevoRegistroPago.setOnClickListener(view1 -> {
            if(!lExistePendiente)
            {
                Intent i= new Intent(getActivity(), ActivityRegistroPago.class);
                startActivity(i);
            }
            else {
                Global.MostrarMensaje(getActivity(), "Existe un pago pendiente",
                        "No se puede crear un registro nuevo debido a que " +
                                "existe un pago pendiente por autorizar");
            }

        });
        recyclerViewPagos=view.findViewById(R.id.recyclePagos);
        PreparaAdapter();
        firebaseAuth=FirebaseAuth.getInstance();
        ObtenerPagos();
    }

    @Override
    public void onPause() {
        super.onPause();


    }

    @Override
    public void onResume() {
        super.onResume();
        if(databaseReference!=null&&firebaseAuth!=null)
        {
            ObtenerPagos();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_registro_pago, container, false);
        Init(view);
        return view;
    }
}