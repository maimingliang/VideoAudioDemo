package com.elk.wxplayer;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.elk.wxplayer.play.PlayerActivity;
import com.elk.wxplayer.play.WxPlayer;

public class MainActivity extends AppCompatActivity {

    private WxPlayer mWxPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,PlayerActivity.class));
            }
        });
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
