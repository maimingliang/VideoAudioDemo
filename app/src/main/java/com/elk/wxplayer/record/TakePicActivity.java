package com.elk.wxplayer.record;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.elk.wxplayer.R;

import java.io.File;

public class TakePicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_pic);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePic();
            }
        });

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCarmer();
            }
        });
    }

    private void takeCarmer() {

        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        startActivityForResult(intent,102);

    }

    private void takePic() {
       String imgPath = "/sdcard/test/img.jpg";

        File file = new File(imgPath);

        if(!file.exists()){

            File parentFile = file.getParentFile();
            parentFile.mkdirs();
        }

        Uri uri = Uri.fromFile(file);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        startActivityForResult(intent,100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK){
            if(requestCode == 100){

                Log.e("onActivityResult", " -- data = " + data.getData().toString());
            }else if(requestCode == 102){
                Log.e("onActivityResult", " -- data = " + data.getData().toString());

            }

        }


    }
}
