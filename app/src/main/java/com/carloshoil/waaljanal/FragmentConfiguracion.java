package com.carloshoil.waaljanal;

import android.app.TimePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.TimePicker;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FragmentConfiguracion#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentConfiguracion extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private TextView tvTimeLunesInicio,tvTimeLunesFinal, tvTimeMartesInicio,tvTimeMartesFinal,
            tvTimeMierInicio, tvTimeMierFinal, tvTimeJueInicio, tvTimeJueFinal, tvTimeVierInicio,
            tvTimeVierFinal, tvTimeSabInicio, tvTimeSabFin, tvTimeDomInicio, tvTimeDomFin;


    public FragmentConfiguracion() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentConfiguracion.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentConfiguracion newInstance(String param1, String param2) {
        FragmentConfiguracion fragment = new FragmentConfiguracion();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    private void AbrirTimePicker(TextView tvSelected){
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int iHour, int iMinute) {
                String AM_PM;
                    if (iHour>=0&&iHour<12){
                        AM_PM=" AM";
                    }else {
                    AM_PM=" PM";
                    if(iHour>12)
                    {
                        iHour=iHour-12;
                    }
                   }
             tvSelected.setText ( iHour + ":" + (iMinute<10?"0"+iMinute:iMinute)+""+AM_PM );
            }
        },12,0,false);
        timePickerDialog.show();
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
        tvTimeLunesInicio=view.findViewById(R.id.tvLunesTimeInicio);
        tvTimeLunesFinal=view.findViewById(R.id.tvLunesTimeFinal);
        tvTimeMartesInicio=view.findViewById(R.id.tvMartesTimeInicio);
        tvTimeMartesFinal=view.findViewById(R.id.tvMartesTimeFinal);
        tvTimeMierInicio=view.findViewById(R.id.tvMierTimeInicio);
        tvTimeMierFinal=view.findViewById(R.id.tvMierTimeFinal);
        tvTimeJueInicio=view.findViewById(R.id.tvJueTimeInicio);
        tvTimeJueFinal=view.findViewById(R.id.tvJueTimeFinal);
        tvTimeVierInicio=view.findViewById(R.id.tvVieTimeInicio);
        tvTimeVierFinal=view.findViewById(R.id.tvVieTimeFinal);
        tvTimeSabInicio=view.findViewById(R.id.tvSabTimeInicio);
        tvTimeSabFin=view.findViewById(R.id.tvSabTimeFinal);
        tvTimeDomInicio=view.findViewById(R.id.tvDomTimeInicio);
        tvTimeDomFin=view.findViewById(R.id.tvDomTimeFinal);
        tvTimeLunesInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeLunesInicio);
            }
        });
        tvTimeLunesFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeLunesFinal);
            }
        });
        tvTimeMartesInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeMartesInicio);
            }
        });
        tvTimeMartesFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeMartesFinal);
            }
        });
        tvTimeMierInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeMierInicio);
            }
        });
        tvTimeMierFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeMierFinal);
            }
        });
        tvTimeJueInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeJueInicio);
            }
        });
        tvTimeJueFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeJueFinal);
            }
        });
        tvTimeVierInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeVierInicio);
            }
        });
        tvTimeVierFinal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeVierFinal);
            }
        });
        tvTimeSabInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeSabInicio);
            }
        });
        tvTimeSabFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeSabFin);
            }
        });
        tvTimeDomInicio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeDomInicio);

            }
        });
        tvTimeDomFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbrirTimePicker(tvTimeDomFin);
            }
        });
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_configuracion, container, false);
        Init(view);
        return view;
    }
}