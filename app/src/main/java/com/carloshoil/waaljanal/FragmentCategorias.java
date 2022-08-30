package com.carloshoil.waaljanal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.carloshoil.waaljanal.Adapter.CategoriasAdapter;
import com.carloshoil.waaljanal.Adapter.ProductosAdapter;
import com.carloshoil.waaljanal.DTO.Categoria;
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
 * Use the {@link FragmentCategorias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCategorias extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerViewCategorias;
    private FloatingActionButton floatingActionButtonAgregarCat;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private String cTemporalMenu="wjag1";
    private CategoriasAdapter categoriasAdapter;

    public FragmentCategorias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCategorias.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCategorias newInstance(String param1, String param2) {
        FragmentCategorias fragment = new FragmentCategorias();
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

    private void Init( View view)
    {
        recyclerViewCategorias=view.findViewById(R.id.rcvCategorias);
        floatingActionButtonAgregarCat=view.findViewById(R.id.fbAgregarCategoria);
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference().child("menus").child(cTemporalMenu).child("categorias");
        preparaAdapter();
        ObtenerCategorias();
    }
    private void ObtenerCategorias()
    {
        List<Categoria> lstCategoria= new ArrayList<>();
        databaseReference.get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    Categoria categoria;
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        categoria= new Categoria();
                        categoria.cLlave=dataSnapshot.getKey();
                        categoria.cNombre=dataSnapshot.child("cNombre")==null?"": dataSnapshot.child("cNombre").getValue().toString();
                        categoria.lDisponible=dataSnapshot.child("lDisponible").getValue()==null?false:dataSnapshot.child("lDisponible").getValue(boolean.class);
                        lstCategoria.add(categoria);
                    }
                    CargaDatosCategoria(lstCategoria);
                }

            }
        });
    }

    private void preparaAdapter()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewCategorias.setLayoutManager(linearLayoutManager);
        recyclerViewCategorias.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
    }
    private void CargaDatosCategoria(List<Categoria> lstCategoria) {
        categoriasAdapter= new CategoriasAdapter(getActivity(), lstCategoria);
        recyclerViewCategorias.setAdapter(categoriasAdapter);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_categorias, container, false);
        Init(view);
        return view;
    }
}