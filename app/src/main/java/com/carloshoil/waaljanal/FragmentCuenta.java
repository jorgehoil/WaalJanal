package com.carloshoil.waaljanal;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.carloshoil.waaljanal.Dialog.DialogoCarga;
import com.carloshoil.waaljanal.Utils.Global;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentCuenta#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCuenta extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    Button btnGuardar, btnActualizarCon;
    EditText edCorreo, edNombre, edContrasenaNue, edContrasenaAnt;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth firebaseAuth;
    DialogoCarga dialogoCarga;


    public FragmentCuenta() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentCuenta.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCuenta newInstance(String param1, String param2) {
        FragmentCuenta fragment = new FragmentCuenta();
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

    private void Init(View view)
    {
        firebaseAuth=FirebaseAuth.getInstance();
        firebaseDatabase=FirebaseDatabase.getInstance();
        btnActualizarCon=view.findViewById(R.id.btnActualizarContConfig);
        btnGuardar=view.findViewById(R.id.btnGuardarDatosConfig);
        edContrasenaAnt=view.findViewById(R.id.edContrasenaAntConfig);
        edContrasenaNue=view.findViewById(R.id.edContrasenaNueConfig);
        edCorreo=view.findViewById(R.id.edCorreoConfig);
        edNombre=view.findViewById(R.id.edNombreConfig);
        btnGuardar.setOnClickListener(v->{
            GuardaDatos();
        });
        btnActualizarCon.setOnClickListener(v -> ActualizaContrasena());
        CargarDatos();
    }


    private void ActualizaContrasena()
    {
        if(firebaseAuth.getCurrentUser()!=null)
        {
            String cContrasenaAnt=edContrasenaAnt.getText().toString().trim();
            String cContrasenaNue=edContrasenaNue.getText().toString().trim();
            if(ValidaCamposContrasena(cContrasenaAnt, cContrasenaNue))
            {
                btnActualizarCon.setEnabled(false);
                AuthCredential credential= EmailAuthProvider.getCredential(firebaseAuth.getCurrentUser().getEmail(),cContrasenaAnt);
                firebaseAuth.getCurrentUser().reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            firebaseAuth.getCurrentUser()
                                    .updatePassword(cContrasenaNue)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            btnActualizarCon.setEnabled(true);
                                            if(task.isSuccessful())
                                            {
                                                edContrasenaAnt.setText("");
                                                edContrasenaNue.setText("");
                                                Toast.makeText(getActivity(), "Contraseña actualizada", Toast.LENGTH_SHORT).show();
                                            }
                                            else {
                                                Global.MostrarMensaje(getActivity(), "Error", "Ocurrió un error al actualizar");
                                            }

                                        }
                            });

                        }
                        else {
                            btnActualizarCon.setEnabled(true);
                            Global.MostrarMensaje(getActivity(), "Error",
                                    "Verifica tu contraseña anterior");
                        }
                    }
                });
            }
        }


    }
    private boolean ValidaCamposContrasena(String cContrasenaAnt, String cContrasenaNue)
    {
        if(cContrasenaAnt.isEmpty())
        {
            edContrasenaAnt.setError("Ingresa tu contraseña actual");
            return false;
        }
        if(cContrasenaNue.isEmpty())
        {
            edContrasenaNue.setError("Debes ingresar una nueva contrasena");
            return false;
        }
        return true;
    }
    private void GuardaDatos()
    {
        if(!edNombre.getText().toString().isEmpty())
        {
            if(firebaseAuth.getUid()!=null)
            {
                btnGuardar.setEnabled(false);
                firebaseDatabase.getReference()
                        .child("usuarios")
                        .child(firebaseAuth.getUid())
                        .child("cNombre")
                        .setValue(edNombre.getText().toString().trim())
                        .addOnCompleteListener(task -> {
                            btnGuardar.setEnabled(true);
                            if(task.isSuccessful())
                            {
                                Global.GuardarPreferencias("cNombreUsuario", edNombre.getText().toString(),getActivity());
                                Toast.makeText(getActivity(), "Se ha guardado los datos", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                Global.MostrarMensaje(getActivity(), "Error al guardar",
                                        "Se ha presentado un error al guardar, intente de nuevo");
                            }

                        });
            }
        }
        else
        {
            edNombre.setError("Este campo es obligatorio");
        }


    }
    private void CargarDatos()
    {
        edCorreo.setText(Global.RecuperaPreferencia("cEmailId", getActivity()));
        edNombre.setText(Global.RecuperaPreferencia("cNombreUsuario", getActivity()));

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_cuenta, container, false);
        Init(view);
        return view;
    }
}