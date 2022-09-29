package com.carloshoil.waaljanal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.carloshoil.waaljanal.Adapter.CategoriasAdapter;
import com.carloshoil.waaljanal.Adapter.ProductosAdapter;
import com.carloshoil.waaljanal.DTO.Categoria;
import com.carloshoil.waaljanal.Dialog.DialogoCategoria;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
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
    private DialogoCategoria dialogoCategoria;
    private ChildEventListener childEventListenerCat;

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
        floatingActionButtonAgregarCat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                abrirDialogo(null, "wjag1");
            }
        });
        childEventListenerCat= new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    Categoria categoria= new Categoria(
                            snapshot.getKey(),
                            snapshot.child("cNombre")==null?"":snapshot.child("cNombre").getValue(String.class)
                    );
                    AgregaCategoriaLista(categoria);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists())
                {
                    Categoria categoria= new Categoria(
                            snapshot.getKey(),
                            snapshot.child("cNombre")==null?"":snapshot.child("cNombre").getValue(String.class)
                    );
                    ModificaCategoriaLista(categoria);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        preparaAdapter();

    }

    private void abrirDialogo(Categoria categoria, String cIdMenu) {
        dialogoCategoria= new DialogoCategoria(getActivity(), categoria,cIdMenu);
        dialogoCategoria.show(getActivity().getSupportFragmentManager(), "dialogoCat");

    }

    private void AgregaCategoriaLista(Categoria categoria)
    {
        categoriasAdapter.AgregaCategoriaLista(categoria);
    }
    private void ModificaCategoriaLista(Categoria categoria)
    {
        categoriasAdapter.ModificaCategoriaLista(categoria);
    }
    private void preparaAdapter()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewCategorias.setLayoutManager(linearLayoutManager);
        categoriasAdapter= new CategoriasAdapter(getActivity(), new ArrayList<>(), cTemporalMenu);
        recyclerViewCategorias.setAdapter(categoriasAdapter);
    }


    @Override
    public void onPause() {
        super.onPause();
        databaseReference.removeEventListener(childEventListenerCat);
    }

    @Override
    public void onResume() {
        super.onResume();
        databaseReference.addChildEventListener(childEventListenerCat);
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