package com.carloshoil.waaljanal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.carloshoil.waaljanal.Adapter.CategoriasAdapter;
import com.carloshoil.waaljanal.Adapter.SuscripcionAdapter;
import com.carloshoil.waaljanal.DTO.Suscripcion;
import com.carloshoil.waaljanal.Utils.Global;
import com.carloshoil.waaljanal.Utils.Values;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentSuscripcion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSuscripcion extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclePaquetes;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private SuscripcionAdapter suscripcionAdapter;
    private ProgressBar pbCarga;
    private Spinner spOpcionesMenu;


    public FragmentSuscripcion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentSuscripcion.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSuscripcion newInstance(String param1, String param2) {
        FragmentSuscripcion fragment = new FragmentSuscripcion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void Init(View view)
    {
        recyclePaquetes=view.findViewById(R.id.recyclePaquetes);
        pbCarga=view.findViewById(R.id.pbCarga);
        spOpcionesMenu=view.findViewById(R.id.spOpcionesMenu);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();
        spOpcionesMenu.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ObtenerPaquetes(ObtenerStringTipo(i));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        InicializarAdapter();
        ObtenerPaquetes("1menu");

    }
    private void InicializarAdapter()
    {

        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclePaquetes.setLayoutManager(linearLayoutManager);
        suscripcionAdapter= new SuscripcionAdapter(getActivity(), new ArrayList<>());
        recyclePaquetes.setAdapter(suscripcionAdapter);
    }
    private void ObtenerPaquetes(String cIdTipo)
    {
        suscripcionAdapter.LimpiarLista();
        pbCarga.setVisibility(View.VISIBLE);
        List<Suscripcion> lstSuscripcion= new ArrayList<>();
        databaseReference.child("paquetes").child(cIdTipo).get()
                .addOnCompleteListener(task -> {
                    pbCarga.setVisibility(View.GONE);
                    if(task.isSuccessful())
                    {
                        if(task.getResult().hasChildren())
                        {
                            Suscripcion suscripcion;
                            for(DataSnapshot dataSnapshot:task.getResult().getChildren())
                            {
                                suscripcion= new Suscripcion(
                                        dataSnapshot.getKey(),
                                        dataSnapshot.child("cNombre").getValue()==null?"":dataSnapshot.child("cNombre").getValue(String.class),
                                        dataSnapshot.child("cPrecio").getValue()==null?"0.00MX": dataSnapshot.child("cPrecio").getValue(String.class),
                                        dataSnapshot.child("cUrl").getValue()==null?"": dataSnapshot.child("cUrl").getValue(String.class),
                                        dataSnapshot.child("cAhorro").getValue()==null?"0": dataSnapshot.child("cAhorro").getValue(String.class)
                                );
                                lstSuscripcion.add(suscripcion);

                            }
                            suscripcionAdapter.AgregaLista(lstSuscripcion);
                        }
                        else {
                            Global.MostrarMensaje(getActivity(), "No existen paquetes", "No" +
                                    " se ha encontrado paquetes, intentalo más tarde");
                        }
                    }
                    else{
                        Global.MostrarMensaje(getActivity(), "Error al cargar", "Se " +
                                "ha producido un erro al cargar" + task.getException());
                    }
                });

    }
    private String ObtenerStringTipo(int iPosition)
    {
        String cRespuesta="1menu";
        switch (iPosition)
        {
            case 0:
                cRespuesta="1menu";
                break;
            case 1:
                cRespuesta="2menu";
                break;
            case 2:
                cRespuesta="3menu";
                break;
            case 3:
                cRespuesta="5menu";
                break;

        }
        return cRespuesta;
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
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_suscripcion, container, false);
        Init(view);
        return view;
    }
}