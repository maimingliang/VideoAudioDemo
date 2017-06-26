package com.elk.wxplayer.play;

import android.graphics.SurfaceTexture;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.widget.ImageView;

import com.elk.wxplayer.R;

import java.io.IOException;

public class VideoPlayByTextrueViewActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener, MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

    private TextureView mTextureView;
    private ImageView mImageVideo;
    private Surface mSurface;
    private MediaPlayer mMediaPlayer;
    private static String path = "/storage/emulated/0/DCIM/Camera/20170521_200117.mp4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_play_by_textrue_view);

        mTextureView = (TextureView) findViewById(R.id.texture_view);

        mImageVideo = (ImageView) findViewById(R.id.video_image);

        init();
    }

    private void init() {

        mTextureView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
            @Override
            public void onSurfaceTextureAvailable(SurfaceTexture surfaceTexture, int width, int height) {
                mSurface = new Surface(surfaceTexture);

                Log.e("tag", "---- onSurfaceTextureAvailable");

                play();
            }

            @Override
            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
                Log.e("tag", "---- onSurfaceTextureSizeChanged");
            }

            @Override
            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                mTextureView=null;
                mSurface=null;
                mMediaPlayer.stop();
                mMediaPlayer.release();
                return false;
            }

            @Override
            public void onSurfaceTextureUpdated(SurfaceTexture surface) {

            }
        });


    }



    public void play(){


        mMediaPlayer = new MediaPlayer();

        try {
            mMediaPlayer.setDataSource(getApplicationContext(), Uri.parse(path));
            mMediaPlayer.setSurface(mSurface);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.setOnInfoListener(this);
            mMediaPlayer.setOnBufferingUpdateListener(this);

            mMediaPlayer.prepareAsync();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onPrepared(MediaPlayer mp) {

        mImageVideo.setVisibility(View.GONE);
        mMediaPlayer.start();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

    }
}
