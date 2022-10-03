package com.carloshoil.waaljanal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Lifecycle;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.ProductosAdapter;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
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
    private String cIdMenuG="";
    private String CCLAVEMENU="cIdMenu";
    private DialogoCarga dialogoCarga;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private List<String> lstNombresCategorias;
    private List<String> lstIdsCategorias;
    private List<Producto> lstProductosPublicar;
    private MenuItem itemPublicar;
    private FloatingActionButton fbAgregarProd;
    public String cIdCategoriaPub;

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
    private void AgregaProductosPublicar(List<Producto> lstProductosAgregar)
    {
        //for()
    }
    private void Publicar()
    {
        MostrarDialogoCarga();
        HashMap<String, Object> hashMapDataPublic= new HashMap<>();
        List<Producto> productos= productosAdapter.getLstProducto();
        for(Producto producto: productos)
        {
            if(producto.lDisponible)
            {
                hashMapDataPublic.put("menus/"+cIdMenuG+"/productos/"+producto.cLlave, producto);
                hashMapDataPublic.put("menus/"+cIdMenuG+"/menu_publico/"+producto.cIdCategoria+"/"+producto.cLlave, producto);
            }
            else
            {
                hashMapDataPublic.put("menus/"+cIdMenuG+"/productos/"+producto.cLlave, producto);
                hashMapDataPublic.put("menus/"+cIdMenuG+"/menu_publico/"+producto.cIdCategoria+"/"+producto.cLlave,null);
            }
        }
        firebaseDatabase.getReference().updateChildren(hashMapDataPublic).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                OcultarDialogoCarga();
                if(task.isSuccessful())
                {
                    Toast.makeText(getActivity(), "¡Publicación exitosa!", Toast.LENGTH_SHORT).show();
                    MostrarBotonPublicar(false);
                }
                else
                {
                    Global.MostrarMensaje(getActivity(), "Error", "No se ha podido actualizar los datos, intenta de nuevo");
                }
            }});
    }
    private void CargaProductos(List<Producto> lstProd)
    {
        productosAdapter.LimpiarLista();
        productosAdapter.Agregar(lstProd);
    }
    private void CargaCategorias(List<String> lstCategorias)
    {
        Log.d("DEBUG", "CargaCategorias");
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, lstCategorias);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(arrayAdapter);
    }
    private void IniciarAdapter()
    {
        List<Producto> lst= new ArrayList<>();
        Log.d("DEBUG", "IniciarAdapter");
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewProd.setLayoutManager(linearLayoutManager);
        productosAdapter= new ProductosAdapter(getActivity(), lst, this, cIdMenuG);
        recyclerViewProd.setAdapter(productosAdapter);

    }

    public void MostrarBotonPublicar(boolean lMostrar)
    {
        itemPublicar.setVisible(lMostrar);
    }

    private void CargaProductos(String cIdCategoria){
        Log.d("DEBUG", "CargaProductos");
        if(cIdCategoria.isEmpty())
        {
            Global.MostrarMensaje(getActivity(), "Información", "No existen categorias registrados");
        }
        else {

            databaseReferenceMenu.child("productos").orderByChild("cIdCategoria").equalTo(cIdCategoria).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DataSnapshot> task) {
                    List<Producto> lsProducto = new ArrayList<>();
                    Producto producto;
                    if (task.isSuccessful()) {
                        for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                            if (dataSnapshot != null) {
                                producto = new Producto();
                                producto.cLlave = dataSnapshot.getKey();
                                producto.cNombre = dataSnapshot.child("cNombre").getValue() == null ? "" : dataSnapshot.child("cNombre").getValue().toString();
                                producto.cDescripcion = dataSnapshot.child("cDescripcion").getValue() == null ? "" : dataSnapshot.child("cDescripcion").getValue().toString();
                                producto.cPrecio = dataSnapshot.child("cPrecio").getValue() == null ? "" : dataSnapshot.child("cPrecio").getValue().toString();
                                producto.cIdCategoria = dataSnapshot.child("cIdCategoria").getValue() == null ? "" : dataSnapshot.child("cIdCategoria").getValue().toString();
                                producto.cUrlImagen = dataSnapshot.child("cUrlImagen").getValue() == null ? "" : dataSnapshot.child("cUrlImagen").getValue().toString();
                                producto.lDisponible = dataSnapshot.child("lDisponible").getValue() == null ? false : dataSnapshot.child("lDisponible").getValue(boolean.class);
                                lsProducto.add(producto);
                            }
                        }
                        if (lsProducto.size() == 0)
                            Toast.makeText(getActivity(), "No se encontró ningún producto", Toast.LENGTH_SHORT).show();
                        CargaProductos(lsProducto);
                    } else {
                        Global.MostrarMensaje(getActivity(), "Error", "Se ha presentado " +
                                "un error al carga productos, inténtelo de nuevo" + task.getException());
                    }
                    pbCargaProd.setVisibility(View.GONE);
                }
            });
        }
    }
    private void CargaCategorias()
    {
        Log.d("DEBUG", "CargaCategoria");
        pbCargaProd.setVisibility(View.VISIBLE);
        databaseReferenceMenu.child("categorias").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if(task.isSuccessful())
                {
                    String cIdCat;
                    String cCat;
                    for(DataSnapshot dataSnapshot: task.getResult().getChildren())
                    {
                        cIdCat=dataSnapshot.getKey();
                        cCat=dataSnapshot.child("cNombre").getValue().toString();
                        lstIdsCategorias.add(cIdCat);
                        lstNombresCategorias.add(cCat);
                    }
                    if(lstNombresCategorias.size()==0)
                    {
                        pbCargaProd.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), "No existe categorias ni productos, regístrelos en la sección correspondiente", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        String cIdCategoria=lstIdsCategorias.get(0);
                        cIdCategoriaPub=cIdCategoria;
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
        pbCargaProd=view.findViewById(R.id.pbCargaProductos);
        recyclerViewProd=view.findViewById(R.id.rcProductos);
        spCategorias=view.findViewById(R.id.spCategoriasMain);
        lstIdsCategorias=new ArrayList<>();
        fbAgregarProd=view.findViewById(R.id.fbAgregarProducto);
        fbAgregarProd.setOnClickListener(view1 -> AbreNuevo());
        lstNombresCategorias= new ArrayList<>();
        cIdMenuG=Global.RecuperaPreferencia(CCLAVEMENU, getActivity());
        if(!cIdMenuG.isEmpty())
        {
            firebaseDatabase=FirebaseDatabase.getInstance();
            databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cIdMenuG);
            spCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    cIdCategoriaPub=lstIdsCategorias.get(i);
                    CargaProductos(lstIdsCategorias.get(i));
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            requireActivity().addMenuProvider(new MenuProvider() {
                @Override
                public void onPrepareMenu(@NonNull Menu menu) {
                    MenuProvider.super.onPrepareMenu(menu);
                    itemPublicar=menu.findItem(R.id.publicarProd);
                }

                @Override
                public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                    menuInflater.inflate(R.menu.menu_productos, menu);

                }

                @Override
                public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                    switch (menuItem.getItemId())
                    {
                        case R.id.marcarTodosProd:

                            break;
                        case R.id.publicarProd:
                            Publicar();
                            break;
                    }
                    return false;
                }
            },getViewLifecycleOwner(), Lifecycle.State.RESUMED);
            IniciarAdapter();
            CargaCategorias();

        }
    }
    private void MostrarDialogoCarga()
    {
        dialogoCarga= new DialogoCarga(getActivity());
        dialogoCarga.setCancelable(false);
        dialogoCarga.show(getActivity().getSupportFragmentManager(), "dialogoCarga");
    }
    private void OcultarDialogoCarga()
    {
        dialogoCarga.dismiss();
    }
    void AbreNuevo()
    {
        Intent i= new Intent(getActivity(), ABCProductoActivity.class);
        i.putExtra("cIdCatSel", cIdCategoriaPub);
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