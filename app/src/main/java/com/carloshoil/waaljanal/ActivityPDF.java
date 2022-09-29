package com.carloshoil.waaljanal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class ActivityPDF extends AppCompatActivity {

    EditText edNombreRestaurante, edTelefonoContacto, edTextoPersonalizado;
    Button btnGenerarPDF;
    TextView tvCarateresRest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        edNombreRestaurante=findViewById(R.id.edRestauranteQR);
        edTelefonoContacto=findViewById(R.id.edTelefonoContactoQR);
        edTextoPersonalizado=findViewById(R.id.edTextoPersonalizadoQR);
        btnGenerarPDF=findViewById(R.id.btnGenerarPDFQR);
        tvCarateresRest=findViewById(R.id.tvCaracteresRestantes);
        edTextoPersonalizado.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                RestarCarateres(edTextoPersonalizado.getText().toString().length());

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        RevisaPermisos();
    }

    private void RestarCarateres(int length) {
        tvCarateresRest.setText(length+"/60");
    }

    private void RevisaPermisos() {
    }

    private void GenerarPDF()
    {

    }
}