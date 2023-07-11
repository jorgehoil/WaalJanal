package com.carloshoil.waaljanal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.ProductosAdapter;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.DTO.Variedad;
import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.errorprone.annotations.Var;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
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
    private FirebaseAuth firebaseAuth;
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
    private MenuItem itemPublicar, itemModPrecios, itemCancelarModPre, itemGuardarPre, itemSeleccionarTod;
    private FloatingActionButton fbAgregarProd;
    public String cIdCategoria="";

    private boolean lPrimeraCarga=true;
    ActivityResultLauncher<Intent> mStartForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    Log.d("DEBUGX", "RESULT:"+ result.getResultCode());
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        Bundle bundle = data.getExtras();
                        boolean lActualiza = bundle.getBoolean("lActualiza");
                        boolean lNuevo=bundle.getBoolean("lNuevos");
                        boolean lCambioCat=bundle.getBoolean("lCambioCat");
                        Producto producto= (Producto) bundle.getSerializable("entProd");
                        Log.d("DEBUGX", "lActualiza: "+ lActualiza+ " lNuevos: "+ lNuevo);
                        //SI SE HA ACTUALIZADO EL REGISTRO
                        if(lCambioCat)
                        {
                            productosAdapter.EliminaProductoLista(producto.cLlave);
                        }else if(lActualiza){
                            productosAdapter.ActualizaProducto(producto);
                        }else if(lNuevo)
                        {
                            productosAdapter.LimpiarLista();
                            ConsultaProductos(cIdCategoria);
                        }

                    }
                }
            });

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
    void Init(View view)
    {

        Log.d("DEBUGX", "Init");
        lPrimeraCarga=true;
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth!=null)
        {
            cIdMenuG=Global.RecuperaPreferencia(CCLAVEMENU, getActivity());
            pbCargaProd=view.findViewById(R.id.pbCargaProductos);
            recyclerViewProd=view.findViewById(R.id.rcProductos);
            spCategorias=view.findViewById(R.id.spCategoriasMain);
            lstIdsCategorias=new ArrayList<>();
            fbAgregarProd=view.findViewById(R.id.fbAgregarProducto);
            fbAgregarProd.setOnClickListener(view1 -> {
                if(!cIdMenuG.isEmpty())
                    AbreNuevo();
                else
                {
                    Global.MostrarMensaje(getActivity(),"Información", " Para poder crear " +
                            "productos y/o categorías debes seleccionar y/o crear" +
                            " un menú desde <<Menús>>");
                }
            });
            lstNombresCategorias= new ArrayList<>();

            if(!cIdMenuG.isEmpty())
            {
                firebaseDatabase=FirebaseDatabase.getInstance();
                databaseReferenceMenu=firebaseDatabase.getReference().child("menus").child(cIdMenuG);
                spCategorias.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        CargaOpcionPublicar(false);
                        productosAdapter.LimpiarLista();
                        cIdCategoria=lstIdsCategorias.get(i);
                        ConsultaProductos(cIdCategoria);
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
                        itemCancelarModPre=menu.findItem(R.id.cancelarModPrecios);
                        itemGuardarPre=menu.findItem(R.id.guardarPrecios);
                        itemModPrecios=menu.findItem(R.id.modPrecios);
                        itemSeleccionarTod=menu.findItem(R.id.marcarTodosProd);
                    }

                    @Override
                    public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                        menuInflater.inflate(R.menu.menu_productos, menu);

                    }

                    @Override
                    public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                        int iId=menuItem.getItemId();
                        if(iId==R.id.marcarTodosProd)
                        {
                            MarcarTodos();
                        }else if(iId==R.id.publicarProd)
                        {
                            GuardarMasivo();
                        }
                        else if(iId==R.id.modPrecios)
                        {
                            productosAdapter.CargaModificarPrecios(true);
                            CargaModificarPrecios(true);
                        }
                        else if(iId==R.id.cancelarModPrecios)
                        {
                            EsconderTeclado();
                            CargaModificarPrecios(false);
                            productosAdapter.LimpiarLista();
                            ConsultaProductos(cIdCategoria);

                        } else if(iId==R.id.guardarPrecios)
                        {
                            EsconderTeclado();
                            GuardarMasivo();
                        }
                        return false;
                    }
                },getViewLifecycleOwner());
                IniciarAdapterProductos();
                CargaDatos();
            }
        }
        else
        {
            AbreLogin();
        }


    }

    private void GuardarMasivo()
    {
        MostrarDialogoCarga();
        HashMap<String, Object> hashMapDataPublic= new HashMap<>();
        List<Producto> productos= productosAdapter.getLstProducto();
        for(Producto producto: productos)
        {
            hashMapDataPublic.put("menus/"+cIdMenuG+"/productos/"+producto.cLlave+"/lDisponible", producto.lDisponible);
            hashMapDataPublic.put("menus/"+cIdMenuG+"/productos/"+producto.cLlave+"/cPrecio", producto.cPrecio);
            if(producto.lDisponible) {
                hashMapDataPublic.put("menus/" + cIdMenuG + "/menu_publico/" + producto.cIdCategoria + "/" + producto.cLlave, producto);
            }
            else
            {
                hashMapDataPublic.put("menus/"+cIdMenuG+"/menu_publico/"+producto.cIdCategoria+"/"+producto.cLlave,null);
            }
        }
        firebaseDatabase.getReference().updateChildren(hashMapDataPublic).addOnCompleteListener(task -> {
            OcultarDialogoCarga();
            if(task.isSuccessful())
            {
                Toast.makeText(getActivity(), "¡Proceso exitoso!", Toast.LENGTH_SHORT).show();
                CargaOpcionPublicar(false);
                CargaModificarPrecios(false);
            }
            else
            {
                Global.MostrarMensaje(getActivity(), "Error", "No se ha podido" +
                        " actualizar los datos, intenta de nuevo");
            }
        });
    }
    private void CargaCategoriasAdapter(List<String> lstCategorias)
    {
        Log.d("DEBUGX", "CargaCategorias");
        ArrayAdapter<String> arrayAdapter= new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, lstCategorias);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCategorias.setAdapter(arrayAdapter);
    }
    private void IniciarAdapterProductos()
    {
        List<Producto> lst= new ArrayList<>();
        Log.d("DEBUGX", "IniciarAdapterProductos");
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewProd.setLayoutManager(linearLayoutManager);
        productosAdapter= new ProductosAdapter(getActivity(), lst, this, cIdMenuG, false, mStartForResult);
        recyclerViewProd.setAdapter(productosAdapter);

    }

    public void CargaOpcionPublicar(boolean lMostrar)
    {
        itemPublicar.setVisible(lMostrar);
        itemModPrecios.setVisible(!lMostrar);

    }

    private void ConsultaProductos(String cIdCategoria){
        Log.d("DEBUG", "ConsultaProductos");
        if(cIdCategoria.isEmpty())
        {
            Global.MostrarMensaje(getActivity(), "Información", "No existen categorias registrados");
        }
        else
        {
            pbCargaProd.setVisibility(View.VISIBLE);
            databaseReferenceMenu.child("productos")
                    .orderByChild("cIdCategoria")
                    .equalTo(cIdCategoria)
                    .get()
                    .addOnCompleteListener(task -> {
                        Producto producto;
                        if (task.isSuccessful()) {
                            List<Producto> lstProductos= new ArrayList<>();
                            List<Variedad> lstVariedad;
                            for (DataSnapshot dataSnapshot : task.getResult().getChildren()) {
                                lstVariedad= new ArrayList<>();
                                if (dataSnapshot != null) {
                                    producto = new Producto();
                                    producto.cLlave = dataSnapshot.getKey();
                                    producto.cNombre = dataSnapshot.child("cNombre").getValue() == null ? "" : dataSnapshot.child("cNombre").getValue().toString();
                                    producto.cDescripcion = dataSnapshot.child("cDescripcion").getValue() == null ? "" : dataSnapshot.child("cDescripcion").getValue().toString();
                                    producto.cPrecio = dataSnapshot.child("cPrecio").getValue() == null ? "" : dataSnapshot.child("cPrecio").getValue().toString();
                                    producto.cIdCategoria = dataSnapshot.child("cIdCategoria").getValue() == null ? "" : dataSnapshot.child("cIdCategoria").getValue().toString();
                                    producto.cUrlImagen = dataSnapshot.child("cUrlImagen").getValue() == null ? "" : dataSnapshot.child("cUrlImagen").getValue().toString();
                                    producto.cUrlImagenMin= dataSnapshot.child("cUrlImagenMin").getValue() == null ? "" : dataSnapshot.child("cUrlImagenMin").getValue().toString();
                                    producto.lDisponible = dataSnapshot.child("lDisponible").getValue() == null ? false : dataSnapshot.child("lDisponible").getValue(boolean.class);
                                    for(DataSnapshot dataSnapshot1: dataSnapshot.child("lstVariedad").getChildren())
                                    {
                                        lstVariedad.add(new Variedad(
                                                dataSnapshot1.getKey(),
                                                dataSnapshot1.child("cNombre").getValue(String.class),
                                                dataSnapshot1.child("cPrecio").getValue(String.class),
                                                dataSnapshot1.child("lDisponible").getValue(boolean.class)
                                        ));
                                    }
                                    producto.lstVariedad=lstVariedad;
                                    lstProductos.add(producto);
                                }
                            }
                            if (lstProductos.size() == 0)
                            {
                                Toast.makeText(getActivity(), "No se encontró ningún producto", Toast.LENGTH_SHORT).show();
                            }

                            productosAdapter.Agregar(lstProductos);
                        } else {
                            Global.MostrarMensaje(getActivity(), "Error", "Se ha presentado " +
                                    "un error al carga productos, inténtelo de nuevo" + task.getException());
                        }
                        pbCargaProd.setVisibility(View.GONE);
            });
        }
    }



    private void ConsultaCategorias()
    {
        Log.d("DEBUGX", "ConsultaCategorias");
        pbCargaProd.setVisibility(View.VISIBLE);
        databaseReferenceMenu.child("categorias").get().addOnCompleteListener(task -> {
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
                    Global.MostrarMensaje(getActivity(), "No existen categorías", "Dirígete a la opción <<Categorías>> para iniciar el registro.");
                    fbAgregarProd.setEnabled(false);
                }
                else
                {
                    //cIdCategoria=lstIdsCategorias.get(0);
                    CargaCategoriasAdapter(lstNombresCategorias);
                }
            }
            else
            {
                ConsultaCategorias();
            }
        });
    }



    private void CargaModificarPrecios(boolean lModPrecios) {
        productosAdapter.CargaModificarPrecios(lModPrecios);
        fbAgregarProd.setVisibility(lModPrecios?View.GONE:View.VISIBLE);
        itemCancelarModPre.setVisible(lModPrecios);
        itemGuardarPre.setVisible(lModPrecios);
        itemModPrecios.setVisible(!lModPrecios);
        itemSeleccionarTod.setVisible(!lModPrecios);
    }

    private void AbreLogin() {
        Global.GuardarPreferencias("cEstatusLogin", "0",getActivity());
        Intent i= new Intent(getActivity(), ActivityLogin.class);
        i.putExtra("cData", "USER_NULL");
        startActivity(i);

    }

    private void MarcarTodos() {
        productosAdapter.MarcarTodos();
        CargaOpcionPublicar(true);

    }
    private void EsconderTeclado() {
        View view= this.getActivity().getCurrentFocus();
        if(view!=null)
        {
            InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        i.putExtra("cIdCatSel", cIdCategoria);
        mStartForResult.launch(i);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("DEBUG", "onCreate");
        View view=inflater.inflate(R.layout.fragment_productos, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUGX", "ONDESTROY");
        super.onDestroy();
    }

    private void CargaDatos() {
        if(databaseReferenceMenu!=null||!cIdMenuG.isEmpty())
        {
            if(lPrimeraCarga)
            {
                ConsultaCategorias();
                lPrimeraCarga=false;
            }
        }
        else
        {
            Global.MostrarMensaje(getActivity(), "No tiene seleccionado un menú",
                    "Ingrese a la sección de " +
                    "<<Establecimientos>> para crear o seleccionar");
            fbAgregarProd.setEnabled(false);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("DEBUGX", "onPause");

    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("DEBUGX", "onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("DEBUGX", "onStop");
    }
}