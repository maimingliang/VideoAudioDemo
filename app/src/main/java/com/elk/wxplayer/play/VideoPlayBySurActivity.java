package com.elk.wxplayer.play;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.elk.wxplayer.R;

import java.io.IOException;

public class VideoPlayBySurActivity extends AppCompatActivity implements View.OnClickListener {

    private SurfaceView mSurfaceView;
    private MediaPlayer mMediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_by_sur);
        mSurfaceView = (SurfaceView) findViewById(R.id.surface_view);
        findViewById(R.id.stop).setOnClickListener(this);
        findViewById(R.id.pasue).setOnClickListener(this);

        findViewById(R.id.play).setOnClickListener(this);

        init();
    }

    private void init() {
        mMediaPlayer = new MediaPlayer();

        mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                play();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.stop:
                stop();
                break;
            case R.id.play:
                if(!mMediaPlayer.isPlaying()){
                    play();
                }
                break;
            case R.id.pasue:
                pasue();
                break;

        }
    }


    public void stop(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.stop();
        }
    }

    public void pasue(){
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }else{
            mMediaPlayer.start();
        }
    }

    public void play(){

        String path = "/storage/emulated/0/DCIM/Camera/20170521_200117.mp4";
        try {
            mMediaPlayer.reset();
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            //设置需要播放的视频
            mMediaPlayer.setDataSource(this, Uri.parse(path));
            //把视频画面输出到SurfaceView
            mMediaPlayer.setDisplay(mSurfaceView.getHolder());
            mMediaPlayer.prepare();
            mMediaPlayer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
