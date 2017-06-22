package com.elk.wxplayer.record;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.elk.wxplayer.R;

import java.io.File;
import java.io.IOException;

public class RecordVideoActivity extends AppCompatActivity {

    private SurfaceView mSurfaceView;
    private Button mRecord;
    private Button mStop;
    private File mVideoFile;
    private MediaRecorder mMediaRecorder;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 去掉标题栏 ,必须放在setContentView之前
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record_video);

        mSurfaceView = (SurfaceView) findViewById(R.id.sv_camera);
        mStop = (Button) findViewById(R.id.btn_stop);
        mRecord = (Button) findViewById(R.id.btn_record);

        mStop.setEnabled(false);

        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });


    }

    private void record() {

        if (!Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED))
        {
            Toast.makeText(this
                    , "SD卡不存在，请插入SD卡！"
                    , Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mVideoFile = new File(Environment
                    .getExternalStorageDirectory()
                    .getCanonicalFile() + "/testvideo.3gp");

            mMediaRecorder = new MediaRecorder();

            mMediaRecorder.reset();
            // 设置从麦克风采集声音(或来自录像机的声音AudioSource.CAMCORDER)
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            // 设置从摄像头采集图像
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            // 设置视频文件的输出格式
            // 必须在设置声音编码格式、图像编码格式之前设置
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            // 设置声音编码的格式
            mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            // 设置图像编码的格式
            mMediaRecorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);
            mMediaRecorder.setVideoSize(1280,720);
            //每秒 4帧 视频的帧率和视频大小是需要硬件支持的，如果设置的帧率和视频大小,如果硬件不支持就会出现错误。
//            mMediaRecorder.setVideoFrameRate(20);

            mMediaRecorder.setOutputFile(mVideoFile.getAbsolutePath());

            // 指定使用SurfaceView来预览视频
            mMediaRecorder.setPreviewDisplay(mSurfaceView.getHolder().getSurface());

            mMediaRecorder.prepare();
            mMediaRecorder.start();

            System.out.println("---recording---");
            // 让record按钮不可用。
            mRecord.setEnabled(false);
            // 让stop按钮可用。
            mStop.setEnabled(true);
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void stop() {

        if(isRecording){
            release();
        }
    }

    private void release() {
        if(mMediaRecorder != null){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
            mRecord.setEnabled(true);
            mStop.setEnabled(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        release();
    }
}
