package com.carloshoil.waaljanal;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

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
        cSourceUri=getIntent().getStringExtra("imageData")==null?"":getIntent().getStringExtra("imageData");
        if(!cSourceUri.isEmpty())
        {
            uri=Uri.parse(cSourceUri);
        }
        cDestinationUri= new StringBuilder(UUID.randomUUID().toString()).append(".jpg").toString();
        UCrop.Options options= new UCrop.Options();
        UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), cDestinationUri)))
                .withOptions(options)
                .withAspectRatio(16,9)
                .withMaxResultSize(1000, 1000)
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
            finish();

        }
        else if(resultCode==UCrop.RESULT_ERROR)
        {
            final  Throwable error= UCrop.getError(data);
        }
    }
}