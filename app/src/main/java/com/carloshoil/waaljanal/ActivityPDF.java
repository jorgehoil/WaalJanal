package com.carloshoil.waaljanal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Locale;

public class ActivityPDF extends AppCompatActivity {


    TextView tvMensajeInfo;
    String cIdMenu="", cNombreG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        tvMensajeInfo=findViewById(R.id.tvMensaje);
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
            new Handler().postDelayed(() -> GenerarPDF(), 2500);
        }
    }

    private boolean checkPermiso()
    {
        int permission1 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        return permission1 == PackageManager.PERMISSION_GRANTED && permission2 == PackageManager.PERMISSION_GRANTED;
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
                .replace(" ", "_")
                .replace("'", "");
        return cRetorno;

    }
    private void GenerarPDF()
    {
        int iHeight=1120;
        int iWidth=792;
        Bitmap bitmapQr=ObtenerQR();
        String cNombre= limpiaNombre(cNombreG);
        if(bitmapQr!=null)
        {
            File document=null;
            float iX=180,iY=230;
            PdfDocument pdfDocument= new PdfDocument();
            Paint paint= new Paint();
            Paint texto= new Paint();
            PdfDocument.PageInfo pageInfo=new  PdfDocument.PageInfo.Builder(iWidth, iHeight, 1).create();
            PdfDocument.Page myPage= pdfDocument.startPage(pageInfo);
            Canvas canvas= myPage.getCanvas();

            canvas.drawBitmap(bitmapQr, 77, 100, paint);
            texto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            texto.setColor(ContextCompat.getColor(this, R.color.black));
            for(int iColumn=0; iColumn<4; iColumn++)
            {
                for(int iFila=0; iFila<4;iFila++)
                {
                    texto.setTextSize(14);
                    canvas.drawBitmap(bitmapQr, 77+(iFila*iX), 100+(iColumn*iY), paint);
                    canvas.drawText("¡VISITA NUESTRO MENÚ!",50+(iFila*iX), 95+(iColumn*iY), texto);
                    texto.setTextSize(13);
                    canvas.drawText("¿Sin escaner?", 85+(iFila*iX), 207+(iColumn*iY), texto);
                    texto.setTextSize(12);
                    canvas.drawText("Visita:", 110+(iFila*iX),220+(iColumn*iY),texto);
                    canvas.drawText("waaljanal.web.app",77+(iFila*iX),235+(iColumn*iY),texto);
                    canvas.drawText("e ingresa el código:",74+(iFila*iX),251+(iColumn*iY),texto);
                    texto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                    texto.setTextSize(12);
                    texto.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(cIdMenu, 125+(iFila*iX),271+(iColumn*iY),texto);
                    texto.setTextAlign(Paint.Align.LEFT);
                    texto.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
                }
            }
            document= new File(Environment.getExternalStorageDirectory(), "wj_"+cNombre+".pdf");
            pdfDocument.finishPage(myPage);
            try {
                pdfDocument.writeTo(new FileOutputStream(document));
                Toast.makeText(this, "Se ha generado correctamente el archivo "+ "wj_"+cNombre+".pdf" , Toast.LENGTH_LONG).show();
                finish();

            } catch (IOException e) {
                e.printStackTrace();
            }
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

    private void solicitaPermiso() {
        ActivityCompat.requestPermissions(this, new String[]{
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 400);
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
                   matrix= mWriter.encode(cUrl, BarcodeFormat.QR_CODE, 100,100);
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