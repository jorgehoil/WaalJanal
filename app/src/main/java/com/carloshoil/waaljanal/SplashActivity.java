package com.carloshoil.waaljanal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import com.carloshoil.waaljanal.Utils.Global;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        SplashActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
        Init();
    }
    private void Init()
    {
        new Handler().postDelayed(() -> {
            Intent i;
            String cEstatus= Global.RecuperaPreferencia("cEstatusLogin", SplashActivity.this);
            if(!cEstatus.equals("1")){
                i= new Intent(SplashActivity.this, ActivityLogin.class);
            }
            else
            {
                i= new Intent(SplashActivity.this, ActivityInicioSeleccion.class);
            }
            startActivity(i);
            finish();
        }, 1000);

    }
}