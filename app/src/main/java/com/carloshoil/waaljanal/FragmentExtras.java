package com.carloshoil.waaljanal;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.carloshoil.waaljanal.Adapter.IngredientesAdapter;
import com.carloshoil.waaljanal.Adapter.VariedadesAdapter;
import com.carloshoil.waaljanal.DTO.Ingrediente;
import com.carloshoil.waaljanal.Dialog.DialogABCIngrediente;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentExtras#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentExtras extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    RecyclerView recyclerViewIng;

    IngredientesAdapter ingredientesAdapter;
    public FragmentExtras() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentExtras.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentExtras newInstance(String param1, String param2) {
        FragmentExtras fragment = new FragmentExtras();
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
    public void MuestraMensaje()
    {
        Toast.makeText(getActivity(), "Correcto Variedades", Toast.LENGTH_SHORT).show();
    }

    public void cargaIngredientes(List<Ingrediente> lstIngredientes)
    {
        ingredientesAdapter.cargaIngredientes(lstIngredientes);
    }
    public List<Ingrediente> ObtenerIngreDientes()
    {
        return ingredientesAdapter.ObtenerIngredientes();
    }
    public int ObtenerTotalActivos()
    {
        int iTotal=0;
        for (Ingrediente ingrediente: ingredientesAdapter.ObtenerIngredientes())
        {
            if(ingrediente.lDisponible)
            {
                iTotal++;
            }
        }
        return iTotal;
    }
    public void agregaIngrediente(Ingrediente ingrediente)
    {
        ingredientesAdapter.AgregaIngrediente(ingrediente);
    }
    public void AbreDialogo(Ingrediente ingrediente)
    {
        DialogABCIngrediente dialogABCIngrediente= new DialogABCIngrediente(getActivity(), ingrediente, "M", this);
        dialogABCIngrediente.show(getActivity().getSupportFragmentManager(), "dialgo_ingredientes");
        dialogABCIngrediente.setCancelable(false);
    }
    private void PreparaAdapter()
    {
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewIng.setLayoutManager(linearLayoutManager);
        ingredientesAdapter= new IngredientesAdapter(getActivity(), new ArrayList<>(), this);
        recyclerViewIng.setAdapter(ingredientesAdapter);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_extras, container, false);
        Init(view);
        return view;
    }

    private void Init(View view) {
        recyclerViewIng=view.findViewById(R.id.recycleIngredientes);
        PreparaAdapter();
    }
}