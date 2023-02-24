package com.carloshoil.waaljanal;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentAcercaDe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentAcercaDe extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvContacto;

    public FragmentAcercaDe() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentAcercaDe.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentAcercaDe newInstance(String param1, String param2) {
        FragmentAcercaDe fragment = new FragmentAcercaDe();
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
        view= inflater.inflate(R.layout.fragment_acerca_de, container, false);
        Init(view);
        return view;
    }

    private void Init(View view) {
        tvContacto= view.findViewById(R.id.tvContactoCorreo);
        tvContacto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CopiarCorreo();
            }
        });

    }

    private void CopiarCorreo() {

        ClipboardManager clipboardManager= (ClipboardManager)getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData;
        String cCorreo="kookaydev@gmail.com";
        clipData=ClipData.newPlainText("text", cCorreo);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(getActivity(), "¡Correo copiado!", Toast.LENGTH_SHORT).show();

    }
}