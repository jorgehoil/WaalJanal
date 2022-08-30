package com.carloshoil.waaljanal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.ProductosAdapter;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.Utils.Global;
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
 * Use the {@link FragmentProductos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentProductos extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReferenceMenu;
    private ProductosAdapter productosAdapter;
    private Spinner spCategorias;
    private RecyclerView recyclerViewProd;
    private ProgressBar pbCargaProd;
    String cTemporal="wjag1";

    FloatingActionButton floatingActionButtonAgregar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> lstNombresCategorias;
    private List<String> lstIdsCategorias;

    public FragmentProductos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentProductos.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentProductos newInstance(String param1, String param2) {
        FragmentProductos fragment = new FragmentProductos();
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
    private void CargaProductos(List<Producto> lstProd)
    {
        productosAdapter= new ProductosAdapter(getActivity(), lstProd);
        recyclerViewProd.setAdapter(productosAdapter);

    }
    private void CargaCategorias(List<String> lstCategorias)
    {
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, lstCategorias);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(arrayAdapter);
    }
    private void IniciarAdapter()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewProd.setLayoutManager(linearLayoutManager);
        recyclerViewProd.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));

    }

    private void CargaProductos(String cIdCategoria){
        databaseReferenceMenu.child("productos").equalTo(cIdCategoria,"cIdCategoria").orderByChild("cNombre").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                List<Producto> lsProducto= new ArrayList<>();
                Producto producto;
                if(task.isSuccessful())
                {
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        if(dataSnapshot!=null)
                        {
                            producto= new Producto();
                            producto.cLlave= dataSnapshot.getKey();
                            producto.cNombre=dataSnapshot.child("cNombre")==null?"":dataSnapshot.child("cNombre").toString();
                            producto.cDescripcion=dataSnapshot.child("cDescripcion")==null?"": dataSnapshot.child("cDescripcion").toString();
                            producto.cPrecio=dataSnapshot.child("cPrecio")==null?"":dataSnapshot.child("cPrecio").toString();
                            producto.cIdCategoria=dataSnapshot.child("cIdCategoria")==null?"":dataSnapshot.child("cIdCategoria").toString();
                            producto.cUrlImagen=dataSnapshot.child("cUrlImagen")==null?"":dataSnapshot.child("cUrlImagen").toString();
                            producto.lDisponible=dataSnapshot.child("lDisponible")==null?false:dataSnapshot.child("lDisponible").getValue(boolean.class);
                            lsProducto.add(producto);
                        }

                    }
                    if(lsProducto.size()>0)
                    {
                        CargaProductos(lsProducto);
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "No se encontró ningún producto", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    Global.MostrarMensaje(getActivity(), "Error", "Se ha presentado " +
                            "un error al carga productos, inténtelo de nuevo"+ task.getException());
                }
                pbCargaProd.setVisibility(View.GONE);
            }
        });
    }
    private void CargaCategorias()
    {
        pbCargaProd.setVisibility(View.VISIBLE);
        databaseReferenceMenu.child("categorias").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    String cIdCat;
                    String cCat;
                    lstIdsCategorias.add("0");
                    lstNombresCategorias.add("Todos");
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        cIdCat=dataSnapshot.getKey();
                        cCat=dataSnapshot.child("cNombre").getValue().toString();
                        lstIdsCategorias.add(cIdCat);
                        lstNombresCategorias.add(cCat);
                    }
                    if(lstNombresCategorias.size()==1)
                    {
                        Toast.makeText(getActivity(), "No existe categorias ni productos, regístrelos en la sección correspondiente", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String cIdCategoria=lstIdsCategorias.get(1);
                        CargaCategorias(lstNombresCategorias);
                        CargaProductos(cIdCategoria);
                    }
                }
                else
                {
                    CargaCategorias();
                }
            }
        });
    }
    void Init(View view)
    {
        lstIdsCategorias=new ArrayList<>();
        lstNombresCategorias= new ArrayList<>();
        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cTemporal);
        floatingActionButtonAgregar= view.findViewById(R.id.fbAgregarProducto);
        floatingActionButtonAgregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbreNuevo();
            }
        });
        pbCargaProd=view.findViewById(R.id.pbCargaProductos);
        recyclerViewProd=view.findViewById(R.id.rcProductos);
        spCategorias=view.findViewById(R.id.spCategoriasMain);
        IniciarAdapter();
        CargaCategorias();

    }
    void AbreNuevo()
    {
        Intent i= new Intent(getActivity(), ABCProductoActivity.class);
        startActivity(i);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_productos, container, false);
        Init(view);
        return view;
    }
}