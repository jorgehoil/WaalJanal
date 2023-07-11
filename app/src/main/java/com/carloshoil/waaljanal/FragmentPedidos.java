package com.carloshoil.waaljanal;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.view.MenuProvider;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.carloshoil.waaljanal.Adapter.ViewPager2Adapter;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentPedidos#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPedidos extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private ViewPager2Adapter viewPager2Adapter;
    private ViewPager2 viewPager2;
    private TabLayout tabLayoutPedidos;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    ValueEventListener valueEventListener;
    String cIdMenu="";
    public FragmentPedidos() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentPedidos.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentPedidos newInstance(String param1, String param2) {
        FragmentPedidos fragment = new FragmentPedidos();
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
    public void onStop() {
        Log.d("DEBUGX", "FragmentPedidos- OnStop");
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d("DEBUGX", "FragmentPedidos- OnStop");
        super.onDestroy();
    }

    @Override
    public void onResume() {
        AgregaListener();
        Log.d("DEBUGX", "FragmentPedidos- OnResume");
        super.onResume();
    }

    @Override
    public void onPause() {
        RetiraListener();
        super.onPause();
    }
    private void AgregaListener()
    {
        cIdMenu=Global.RecuperaPreferencia("cIdMenu", getActivity());
        if(!cIdMenu.isEmpty())
        {
            databaseReference=firebaseDatabase.getReference().child("pedidos")
                    .child(cIdMenu).child("count");
            databaseReference.addValueEventListener(valueEventListener);
        }
    }

    private void RetiraListener()
    {
        if(databaseReference!=null)
        {
            databaseReference.removeEventListener(valueEventListener);
            tabLayoutPedidos.getTabAt(0).removeBadge();
            tabLayoutPedidos.getTabAt(1).removeBadge();
            tabLayoutPedidos.getTabAt(2).removeBadge();
        }
    }
    private void Init(View view){
        Log.d("DEBUGX", "Init-FragmentPedidos");
        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("DEBUGX", "PEDIDOS!!!!");
                int iComedor=snapshot.child("comedor").child("data").getValue()==null?0:snapshot
                        .child("comedor").child("data").getValue(Integer.class);
                int iDomicilio=snapshot.child("domicilio").child("data").getValue()==null?0:snapshot
                        .child("domicilio").child("data").getValue(Integer.class);
                int iRecoger=snapshot.child("recoger").child("data").getValue()==null?0:snapshot
                        .child("recoger").child("data").getValue(Integer.class);
                if(iComedor>0)
                    tabLayoutPedidos.getTabAt(0).getOrCreateBadge().setNumber(iComedor);
                else
                    tabLayoutPedidos.getTabAt(0).removeBadge();
                if(iDomicilio>0)
                    tabLayoutPedidos.getTabAt(1).getOrCreateBadge().setNumber(iDomicilio);
                else
                    tabLayoutPedidos.getTabAt(1).removeBadge();
                if(iRecoger>0)
                    tabLayoutPedidos.getTabAt(2).getOrCreateBadge().setNumber(iRecoger);
                else
                    tabLayoutPedidos.getTabAt(2).removeBadge();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        viewPager2=view.findViewById(R.id.viewPager2Pedidos);
        tabLayoutPedidos=view.findViewById(R.id.tabLayoutPedidos);
        firebaseDatabase=FirebaseDatabase.getInstance();
        setViewPagerPedidos();
        new TabLayoutMediator(tabLayoutPedidos, viewPager2, (tab, position) -> {
           switch (position)
           {
               case 0:
                   tab.setText("COMEDOR");
                   break;
               case 1:
                   tab.setText("DOMICILIO");
                   break;
               case 2:
                   tab.setText("RECOGER");
                   break;
           }
        }).attach();
        requireActivity().addMenuProvider(new MenuProvider() {
            @Override
            public void onCreateMenu(@NonNull Menu menu, @NonNull MenuInflater menuInflater) {
                menuInflater.inflate(R.menu.menu_pedidos, menu);
            }

            @Override
            public boolean onMenuItemSelected(@NonNull MenuItem menuItem) {
                int iId=menuItem.getItemId();
                if(iId==R.id.buscarPedido)
                {
                    AbrirBuscar();
                }
                return false;
            }
        }, getViewLifecycleOwner());

    }

    private void AbrirBuscar() {
        Intent i= new Intent(getActivity(), BuscarActivity.class);
        startActivity(i);
    }

    private void setViewPagerPedidos() {
        viewPager2Adapter = new ViewPager2Adapter(this);
        List<Fragment> lstFragments= new ArrayList<>();
        lstFragments.add(new FragmentComedor());
        lstFragments.add(new FragmentDomicilio());
        lstFragments.add(new FragmentRecoger());
        viewPager2Adapter.setFragments(lstFragments);
        viewPager2.setAdapter(viewPager2Adapter);
        viewPager2.setOffscreenPageLimit(3);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d("DEBUGX", "onCreateView");
        View view= inflater.inflate(R.layout.fragment_pedidos, container, false);
        Init(view);
        return view;
    }
}