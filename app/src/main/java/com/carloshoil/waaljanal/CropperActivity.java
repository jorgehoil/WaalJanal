package com.carloshoil.waaljanal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.carloshoil.waaljanal.Utils.Values;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropperActivity extends AppCompatActivity {

    String cSourceUri, cDestinationUri;
    Uri uri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cropper);
        Init();
    }

    private void Init() {
        int iX;
        int iY;
        int iWidth;
        int iHeight;
        int iPorcentaje;
        File obj= getCacheDir();
        obj.mkdirs();
        cSourceUri=getIntent().getStringExtra("imageData")==null?"":getIntent().getStringExtra("imageData");
        iX=getIntent().getIntExtra("iX", 16);
        iY=getIntent().getIntExtra("iY", 9);
        iWidth=getIntent().getIntExtra("iWidth", 1920);
        iHeight=getIntent().getIntExtra("iHeight", 700);
        iPorcentaje=getIntent().getIntExtra("iPorcentaje", 45);
        if(!cSourceUri.isEmpty())
        {
            uri=Uri.parse(cSourceUri);
        }
        cDestinationUri= UUID.randomUUID().toString()+"jpg";
        UCrop.Options options= new UCrop.Options();
        options.setCompressionQuality(iPorcentaje);
        UCrop.of(uri, Uri.fromFile(new File(obj, cDestinationUri)))
                .withOptions(options)
                .withAspectRatio(iX,iY)
                .withMaxResultSize(iWidth, iHeight)
                .start(CropperActivity.this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK && requestCode==UCrop.REQUEST_CROP)
        {
            final Uri resultUri=UCrop.getOutput(data);
            Intent intent= new Intent();
            intent.putExtra("CROP", resultUri.toString());
            setResult(101, intent);

        }
        else if(resultCode==UCrop.RESULT_ERROR)
        {
            final  Throwable error= UCrop.getError(data);
        }
        finish();
    }
}