package com.carloshoil.waaljanal;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.carloshoil.waaljanal.Utils.Global;

public class ActivityMenuOnline extends AppCompatActivity {
    WebView webView;
    String cIdMenu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_online);
        webView=findViewById(R.id.webView);
        Init();
    }
    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
    @SuppressLint("SetJavaScriptEnabled")
    private void Init()
    {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        String cUrl="waaljanal.web.app/menu.html?IdMenu=";
        webView.clearCache(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        cIdMenu=getIntent().getStringExtra("cIdMenu");
        if(!cIdMenu.isEmpty())
        {
            cUrl=cUrl+cIdMenu;
            webView.loadUrl(cUrl);
        }
        else
        {
            Global.MostrarMensaje(this, "Error", "No se puede mostrar el menú, no se ha recibido un ID");
        }
    }
}