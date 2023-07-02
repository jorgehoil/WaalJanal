package com.carloshoil.waaljanal;

import android.graphics.RegionIterator;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.AdapterPedidos;
import com.carloshoil.waaljanal.DTO.Pedido;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentDomicilio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentDomicilio extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReferenceRecibidos, databaseReferenceTomados;
    AdapterPedidos adapterPedidos;
    RecyclerView recyclerView;
    CheckBox ckTomados;
    ChildEventListener childEventListener;
    String cIdMenu;

    public FragmentDomicilio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentDomicilio.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentDomicilio newInstance(String param1, String param2) {
        FragmentDomicilio fragment = new FragmentDomicilio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStop() {
        Log.d("DEBUGX", "FragmentDomicilio- onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        RetiraListener();
        Log.d("DEBUGX", "Fragment Domicilio- onPause");
        super.onPause();
    }

    @Override
    public void onResume() {
        AgregaListener();
        Log.d("DEBUGX", "Fragment Domicilio- onResume");
        super.onResume();
    }

    private void AgregaListener()
    {
        cIdMenu= Global.RecuperaPreferencia("cIdMenu", getActivity());
        if(!cIdMenu.isEmpty())
        {
            databaseReferenceRecibidos= firebaseDatabase.getReference().child("pedidos")
                    .child(cIdMenu).child("domicilio").child("recibidos");
            databaseReferenceRecibidos
                    .orderByKey()
                    .limitToFirst(10)
                    .addChildEventListener(childEventListener);
        }
    }
    private void RetiraListener()
    {
        if(databaseReferenceRecibidos!=null)
        {
            adapterPedidos.LimpiaLista();
            databaseReferenceRecibidos.removeEventListener(childEventListener);
        }
    }
    private void Init(View view)
    {
        recyclerView=view.findViewById(R.id.recycleViewDomicilio);
        ckTomados=view.findViewById(R.id.ckTomadosDomicilio);
        ckTomados.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b)
            {
                //databaseReferenceTomados= firebaseDatabase.getReference().child("pedidos").child(cIdMenu).child("domicilio").child("tomados");
            }
        });
        PreparaAdapter();
        childEventListener= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Pedido pedido= new Pedido(
                        snapshot.getKey(),
                        snapshot.child("cNombreCliente").getValue()==null?"--": snapshot.child("cNombreCliente").getValue().toString(),
                        snapshot.child("cMesa").getValue()==null?"--":snapshot.child("cMesa").getValue().toString() ,
                        snapshot.child("cDireccion").getValue()==null?"--":snapshot.child("cDireccion").getValue().toString(),
                        snapshot.child("cDetallePedido").getValue()==null?"":snapshot.child("cDetallePedido").getValue().toString(),
                        snapshot.child("cTelefono").getValue()==null?"--":snapshot.child("cTelefono").getValue().toString(),
                        snapshot.child("lFechaRegistro").getValue()==null?0:snapshot.child("lFechaRegistro").getValue(Long.class),
                        snapshot.child("cTotal").getValue()==null?"$0":snapshot.child("cTotal").getValue().toString()
                );
                adapterPedidos.AgregaItem(pedido);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String cKey=snapshot.getKey();
                adapterPedidos.EliminaItem(cKey);
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        firebaseDatabase=FirebaseDatabase.getInstance();

    }
    private void PreparaAdapter()
    {
        Log.d("DEBUGX", "IniciarAdapterProductos");
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);
        adapterPedidos = new AdapterPedidos(getActivity(), new ArrayList<>(), "domicilio");
        recyclerView.setAdapter(adapterPedidos);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("DEBUGX", "seCrea- FragmentDomicilio");
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_domicilio, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Log.d("DEBUGX", "onSaveInstance");
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        Log.d("DEBUGX", "onRestoreInstance");
        super.onViewStateRestored(savedInstanceState);
    }
}