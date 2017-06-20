package com.elk.wxplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WxPlayer mWxPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWxPlayer = (WxPlayer) findViewById(R.id.wx_player);

       String path = "http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-33-30.mp4";
//       String path = "/storage/emulated/0/DCIM/Camera/20170521_200117.mp4";

        mWxPlayer.setVideoPath(path);

        WxMediaController controller = new WxMediaController(this);
        controller.setImage("http://tanzi27niu.cdsb.mobi/wps/wp-content/uploads/2017/05/2017-05-17_17-30-43.jpg");
        mWxPlayer.setMediaController(controller);
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mWxPlayer != null){
            mWxPlayer.release();
        }
    }
}
