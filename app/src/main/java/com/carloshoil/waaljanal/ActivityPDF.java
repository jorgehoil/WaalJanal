package com.carloshoil.waaljanal;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;
import com.rajat.pdfviewer.PdfViewerActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class ActivityPDF extends AppCompatActivity {


    TextView tvMensajeInfo;
    String cIdMenu="", cNombreG;
    private ActivityResultLauncher<String[]> requestPermissionLaucher=
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), hashMapGranted->
            {
               if(!hashMapGranted.containsValue(false))
               {
                    GenerarPDF();
               }
               else
               {
                   Toast.makeText(this, "Es necesario otorgar los permisos para continuar" +
                           "", Toast.LENGTH_SHORT).show();
                   finish();
               }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        tvMensajeInfo=findViewById(R.id.tvMensaje);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        ActivityPDF.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        cIdMenu=getIntent().getStringExtra("cIdMenu")==null?"":getIntent().getStringExtra("cIdMenu");
        cNombreG=getIntent().getStringExtra("cNombre")==null?"":getIntent().getStringExtra("cNombre");
        RevisaPermisos();
    }



    private void RevisaPermisos() {
        if(!checkPermiso())
        {
            solicitaPermiso();
        }
        else
        {
            new Handler().postDelayed(() -> GenerarPDF(), 1500);
        }
    }
    private void solicitaPermiso() {
        requestPermissionLaucher.launch(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE});
    }

    private boolean checkPermiso()
    {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission2 == PackageManager.PERMISSION_GRANTED&& permission1 == PackageManager.PERMISSION_GRANTED;
    }
    private String limpiaNombre(String cNombre)
    {
        String cRetorno="";
        cRetorno=cNombre.toLowerCase(Locale.ROOT);
        cRetorno=cRetorno.replace("á", "a")
                .replace("é", "e")
                .replace("í", "i")
                .replace("ó", "o")
                .replace("ú", "u")
                .replace(".","")
                .replace("ñ", "n")
                .replace(" ", "")
                .replace("'", "");
        return "34893478";

    }
    private void GenerarPDF()
    {
        int iHeight=1120;
        int iWidth=792;
        Bitmap bitmapQr=ObtenerQR();
        String cNombre= limpiaNombre(cNombreG);
        if(bitmapQr!=null)
        {
            String cPath;
            File document=null;
            float iX=230,iY=280;
            PdfDocument pdfDocument= new PdfDocument();
            Paint paint= new Paint();
            Paint texto= new Paint();
            PdfDocument.PageInfo pageInfo=new  PdfDocument.PageInfo.Builder(iWidth, iHeight, 1).create();
            PdfDocument.Page myPage= pdfDocument.startPage(pageInfo);
            Canvas canvas= myPage.getCanvas();

            canvas.drawBitmap(bitmapQr, 77, 100, paint);
            texto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            texto.setColor(ContextCompat.getColor(this, R.color.black));
            for(int iColumn=0; iColumn<3; iColumn++)
            {
                for(int iFila=0; iFila<3;iFila++)
                {
                    texto.setTextSize(14);
                    canvas.drawBitmap(bitmapQr, 77+(iFila*iX), 100+(iColumn*iY), paint);
                    canvas.drawText("¡VISITA NUESTRO MENÚ!",73+(iFila*iX), 95+(iColumn*iY), texto);
                    texto.setTextSize(13);
                    canvas.drawText("¿Sin escaner?", 110+(iFila*iX), 257+(iColumn*iY), texto);
                    texto.setTextSize(12);
                    canvas.drawText("Visita:", 135+(iFila*iX),270+(iColumn*iY),texto);
                    canvas.drawText("waaljanal.web.app",100+(iFila*iX),285+(iColumn*iY),texto);
                    canvas.drawText("e ingresa el código:",98+(iFila*iX),301+(iColumn*iY),texto);
                    texto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    texto.setTextSize(12);
                    texto.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(cIdMenu, 150+(iFila*iX),321+(iColumn*iY),texto);
                    texto.setTextAlign(Paint.Align.LEFT);
                    texto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                }
            }

            cPath=this.getFilesDir().getPath()+"/"+ java.util.UUID.randomUUID().toString().replace("-", "").substring(0,8)+ ".pdf";

            document= new File(cPath);
            pdfDocument.finishPage(myPage);
            try {
                pdfDocument.writeTo(new FileOutputStream(document));
                open_File(cPath);

            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "¡ERROR AL GENERAR ARCHIVO!" +e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ERROR", e.getMessage()+e.getCause());
            }
            finish();
            pdfDocument.close();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 400) {
            if (grantResults.length > 0) {
                boolean writeStorage = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                boolean readStorage = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                if (!writeStorage && !readStorage) {
                    Toast.makeText(this, "Es necesario otorgar todos los permisos para continuar", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
        }
    }

    public void open_File(String cPath) {
        File file = new File(cPath);
        Log.d("pdfFIle", "" + file);

        // Get the URI Path of file.
        Uri uriPdfPath = FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        Log.d("pdfPath", "" + uriPdfPath);

        // Start Intent to View PDF from the Installed Applications.
        Intent pdfOpenIntent = new Intent(Intent.ACTION_VIEW);
        pdfOpenIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        pdfOpenIntent.setClipData(ClipData.newRawUri("", uriPdfPath));
        pdfOpenIntent.setDataAndType(uriPdfPath, "application/pdf");
        pdfOpenIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION |  Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

        try {
            startActivity(pdfOpenIntent);
        } catch (ActivityNotFoundException activityNotFoundException) {
            Toast.makeText(this,"No hay ninguna app para abrir PDF",Toast.LENGTH_LONG).show();

        }

    }

    private Bitmap ObtenerQR()
    {
        MultiFormatWriter mWriter= new MultiFormatWriter();
        BitMatrix matrix;
        Bitmap bitmapQR;
        String cUrl="waaljanal.web.app/menu.html?menu=";
        if(!cIdMenu.isEmpty())
        {
               try {
                   cUrl=cUrl+cIdMenu;
                   matrix= mWriter.encode(cUrl, BarcodeFormat.QR_CODE, 150,150);
                   BarcodeEncoder barcodeEncoder= new BarcodeEncoder();
                   bitmapQR=barcodeEncoder.createBitmap(matrix);
                   return bitmapQR;

               }catch (Exception e)
               {
                   return null;
               }
        }
        else
        {
           return null;
        }

    }
}