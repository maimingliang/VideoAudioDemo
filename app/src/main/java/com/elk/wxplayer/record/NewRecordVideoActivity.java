package com.elk.wxplayer.record;

import android.hardware.Camera;
import android.media.CamcorderProfile;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;

import com.elk.wxplayer.CameraHelper;
import com.elk.wxplayer.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class NewRecordVideoActivity extends AppCompatActivity implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView mSurfaceView;
    private Button mBtnRecord;
    private Button mBtnStop;
    private SurfaceHolder mSurfaceHolder;

    private Camera mCamera;
    private int mPreviewWidth;
    private int mPreviewHeight;
    private CamcorderProfile mCamcorderProfile;
    private MediaRecorder mMediaRecorder;
    private File mTargetFile;
    private boolean isRecording;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_record_video);

        mSurfaceView = (SurfaceView) findViewById(R.id.sv_camera);
        mBtnRecord = (Button) findViewById(R.id.btn_record);
        mBtnStop = (Button) findViewById(R.id.btn_stop);

        mBtnRecord.setOnClickListener(this);
        mBtnStop.setOnClickListener(this);

        init();
    }

    private void init() {

        Log.e("record", "init");
        mSurfaceHolder = mSurfaceView.getHolder();
        mSurfaceHolder.addCallback(this);
        mSurfaceHolder.setFixedSize(mPreviewWidth, mPreviewHeight);


    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_record:
                record();
                break;
            case R.id.btn_stop:
                save();
                break;
        }
    }

    private void save() {
        Log.e("save","---- isRecording = " + isRecording);
        if(isRecording){
            mMediaRecorder.stop();
            isRecording = false;
            Log.e("tag", "-----> flie = " + mTargetFile.getAbsolutePath());
        }
    }


    private void startPreView(SurfaceHolder holder) {

        if (mCamera == null) {

            try {
                mCamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
            }catch (Exception e){
                Log.e("startPreView", " --- 打开相机失败");
            }

        }

        if (mCamera != null) {
            try {
                mCamera.setDisplayOrientation(90); //竖屏显示

                mCamera.setPreviewDisplay(holder);
                Camera.Parameters parameters = mCamera.getParameters();
                List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
                List<Camera.Size> supportedVideoSizes = parameters.getSupportedVideoSizes();

                Camera.Size optimalVideoSize = CameraHelper.getOptimalVideoSize(supportedVideoSizes, supportedPreviewSizes,
                        mSurfaceView.getWidth(), mSurfaceView.getHeight());

                mPreviewWidth = optimalVideoSize.width;
                mPreviewHeight = optimalVideoSize.height;

                parameters.setPreviewSize(mPreviewWidth, mPreviewHeight);
                mCamcorderProfile = CamcorderProfile.get(CamcorderProfile.QUALITY_HIGH);
                // 这里是重点，分辨率和比特率
                // 分辨率越大视频大小越大，比特率越大视频越清晰
                // 清晰度由比特率决定，视频尺寸和像素量由分辨率决定
                // 比特率越高越清晰（前提是分辨率保持不变），分辨率越大视频尺寸越大。
                mCamcorderProfile.videoFrameWidth = optimalVideoSize.width;
                mCamcorderProfile.videoFrameHeight = optimalVideoSize.height;
                mCamcorderProfile.videoBitRate = 2 * optimalVideoSize.width * optimalVideoSize.height;

                List<String> focusModes = parameters.getSupportedFocusModes();
                if (focusModes != null) {
                    for (String mode : focusModes) {
                        if(mode.contains("continuous-video")){
                            parameters.setFocusMode("continuous-video");
                        }
                    }
                }

                mCamera.setParameters(parameters);
                mCamera.startPreview();

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("startPreView", "----- e " + e.getMessage());
            }
        }
    }

    private void record(){

        Log.e("record", " ---- isRecording = " + isRecording);
        if(isRecording){
            if (mMediaRecorder != null) {
                mMediaRecorder.stop();
                releaseRecord();
                mCamera.lock();
                isRecording = false;
            }
        }else{
            if (prepareRecord()) {
                mMediaRecorder.start();
                isRecording = true;
            }
        }

    }

    private boolean prepareRecord() {
        try {

            mMediaRecorder = new MediaRecorder();

            mCamera.unlock();
            mMediaRecorder.setCamera(mCamera);
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mMediaRecorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);
            mMediaRecorder.setProfile(mCamcorderProfile);
            mMediaRecorder.setOrientationHint(90);

            mTargetFile = new File("/sdcard/test.mp4");
            mMediaRecorder.setOutputFile(mTargetFile.getAbsolutePath());
            mMediaRecorder.prepare();

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("prepareRecord", "IOException = " + e.getMessage());
            return false;
        }catch (IllegalStateException e){
            e.printStackTrace();
            Log.e("prepareRecord", "IllegalStateException = " + e.getMessage());
            return false;
        }
        return true;
    }

    private void releaseRecord() {

        if (mMediaRecorder != null) {
            mMediaRecorder.reset();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }
    private void releaseCamear() {

        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }
    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.e("record", "surfaceCreated");
        mSurfaceHolder = holder;
        startPreView(holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamear();
        releaseRecord();
    }
}
