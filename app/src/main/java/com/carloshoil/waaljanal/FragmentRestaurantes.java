package com.carloshoil.waaljanal;

import android.content.Intent;
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

import com.carloshoil.waaljanal.Adapter.ProductosAdapter;
import com.carloshoil.waaljanal.Adapter.RestaurantesAdapter;
import com.carloshoil.waaljanal.DTO.Producto;
import com.carloshoil.waaljanal.DTO.Restaurante;
import com.carloshoil.waaljanal.Dialog.DialogoABCRestaurante;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentRestaurantes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRestaurantes extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private DatabaseReference databaseReferenceRestaurantes;
    private FirebaseDatabase firebaseDatabase;
    private RecyclerView recyclerViewRestaurantes;
    private RestaurantesAdapter restaurantesAdapter;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private ChildEventListener childEventListenerRes;
    private FloatingActionButton fbNuevoRestaurant;


    public FragmentRestaurantes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRestaurantes.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRestaurantes newInstance(String param1, String param2) {
        FragmentRestaurantes fragment = new FragmentRestaurantes();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view;
        view= inflater.inflate(R.layout.fragment_restaurantes, container, false);
        Init(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(firebaseUser!=null&&databaseReferenceRestaurantes!=null)
        {
            databaseReferenceRestaurantes.addChildEventListener(childEventListenerRes);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(databaseReferenceRestaurantes!=null)
        {
            LimpiaRecycle();
            databaseReferenceRestaurantes.removeEventListener(childEventListenerRes);
        }

    }

    private void Init(View view) {
        firebaseAuth=FirebaseAuth.getInstance();
        if(firebaseAuth!=null)
        {
            fbNuevoRestaurant=view.findViewById(R.id.fbCrearRestaurantMenu);
            recyclerViewRestaurantes=view.findViewById(R.id.recycleRestaurantes);
            firebaseDatabase=FirebaseDatabase.getInstance();

            firebaseUser=firebaseAuth.getCurrentUser();
            fbNuevoRestaurant.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ValidaCreacionMenu();


                }
            });
            childEventListenerRes= new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    if(snapshot.exists())
                    {
                        Restaurante restaurante= new Restaurante(
                                snapshot.getKey(),
                                snapshot.child("cNombre").getValue()==null?"":snapshot.child("cNombre").getValue(String.class),
                                snapshot.getKey(),
                                snapshot.child("lDisponible").getValue()==null?true:snapshot.child("lDisponible").getValue(boolean.class));
                        AgregaRestaurante(restaurante);
                    }
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    Restaurante restaurante= new Restaurante(
                            snapshot.getKey(),
                            snapshot.child("cNombre").getValue()==null?"":snapshot.child("cNombre").getValue(String.class),
                            snapshot.getKey(),
                            snapshot.child("lDisponible").getValue()==null?true:snapshot.child("lDisponible").getValue(boolean.class));
                    ActualizaRestaurante(restaurante);
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    if(snapshot.exists())
                    {
                        Restaurante restaurante= new Restaurante(
                                snapshot.getKey(),
                                snapshot.child("cNombre").getValue()==null?"":snapshot.child("cNombre").getValue(String.class),
                                snapshot.getKey(),
                                snapshot.child("lDisponible").getValue()==null?true:snapshot.child("lDisponible").getValue(boolean.class));
                        EliminaRestaurante(restaurante);
                    }
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
            if(firebaseUser!=null) {
                databaseReferenceRestaurantes = firebaseDatabase.getReference().child("usuarios").child(firebaseUser.getUid()).child("adminlugares");
            }
            IniciaAdapter();

        }
        else
        {
            IniciaLogin();
        }

    }

    private void ValidaCreacionMenu() {
        firebaseDatabase.getReference().child("usuarios")
                .child(firebaseAuth.getUid())
                .child("dataInfoUsoMenu")
                .get()
                .addOnCompleteListener(task -> {
                    int iLimite=0, iTotalActual=0;
                    String cTitulo="";
                    String cMensaje="";
                    iLimite=task.getResult().child("iLimiteMenus").getValue()==null?0:
                            task.getResult().child("iLimiteMenus").getValue(Integer.class);
                    iTotalActual=task.getResult().child("iTotalMenus").getValue()==null?0:
                            task.getResult().child("iTotalMenus").getValue(Integer.class);
                    if(iTotalActual==0&&iLimite==0)
                    {
                       cTitulo="Error al crear menu";
                       cMensaje="Ha ocurrido un error al crear el menú, si el problema persiste" +
                               "envianos un correo a la dirección de contacto describiendo el problema";
                       Global.MostrarMensaje(getActivity(), cTitulo, cMensaje);
                    }
                    else if(iTotalActual<iLimite)
                    {
                        AbreDialogoABC();
                    }
                    else {
                        cTitulo="Límite alcanzado";
                        cMensaje="Con tu plan actual solo puedes crear hasta " + iLimite +" menús. Puedes" +
                                " crear más menús suscribiéndote al plan que mejor se adapte a tus necesidades." +
                                "Consulta más información en la sección de Suscripciones";
                        Global.MostrarMensaje(getActivity(), cTitulo, cMensaje);
                    }
                });
    }

    private void IniciaLogin() {
        Global.GuardarPreferencias("cEstatusLogin", "0",getActivity());
        Intent i= new Intent(getActivity(), ActivityLogin.class);
        i.putExtra("cData", "USER_NULL");
        startActivity(i);
    }

    public void AbreDialogoABC() {
       Intent i= new Intent(getActivity(), ActivityConfiguracion.class);
       startActivity(i);
    }

    private void AgregaRestaurante(Restaurante restaurante)
    {
        restaurantesAdapter.AgregaRestaurante(restaurante);
    }
    private void EliminaRestaurante(Restaurante restaurante)
    {
        restaurantesAdapter.EliminaRestaurante(restaurante.cLlave);
    }
    private void ActualizaRestaurante(Restaurante restaurante)
    {
        restaurantesAdapter.ActualizaRestaurante(restaurante);
    }
    private void LimpiaRecycle()
    {
        restaurantesAdapter.LimpiaLista();
    }
    private void IniciaAdapter()
    {
        List<Producto> lst= new ArrayList<>();
        Log.d("DEBUG", "IniciarAdapter");
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(linearLayoutManager.VERTICAL);
        recyclerViewRestaurantes.setLayoutManager(linearLayoutManager);
        restaurantesAdapter= new RestaurantesAdapter(getActivity(), new ArrayList<>());
        recyclerViewRestaurantes.setAdapter(restaurantesAdapter);

    }
}