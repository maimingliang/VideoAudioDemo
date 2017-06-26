package com.elk.wxplayer.play;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.VideoView;

import com.elk.wxplayer.R;


public class VideoPlayByVVActivity extends AppCompatActivity {

    private VideoView mVideoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);  //去掉 title
//        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); //设置全屏
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);   //设置屏幕常亮
        setContentView(R.layout.activity_video_play_by_vv);
        mVideoView = (VideoView) findViewById(R.id.video_view);

        init();
    }

    private void init() {

      String path = "/storage/emulated/0/DCIM/Camera/20170521_200117.mp4";

        Uri uri = Uri.parse(path);

        mVideoView.setVideoPath(path);

        mVideoView.start();
        mVideoView.requestFocus();

    }
}
