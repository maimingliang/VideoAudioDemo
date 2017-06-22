package com.elk.wxplayer.record;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * 类       名:
 * 说       明:
 * 修 改 记 录:
 * 版 权 所 有:   Copyright © 2017
 * 公       司:   深圳市旅联网络科技有限公司
 * version   0.1
 * date   2017/6/22
 * author   maimingliang
 */


public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Context mContext;
    private SurfaceHolder mSurfaceHolder;
    private Camera mCamera;
    private boolean isBackCameraOn = true;
    private boolean mIsPortrait = true;
    private int mSurfaceWidth;
    private int mSurfaceHeight;
    public CameraSurfaceView(Context context) {
        this(context,null);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CameraSurfaceView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();

    }

    private void init() {
        mSurfaceHolder = getHolder();
        mSurfaceHolder.addCallback(this);
    }


    public void start(){
        if(mCamera == null && checkCameraHardware(mContext)){
            mCamera = getCamera();
            setStartPreview(mCamera,mSurfaceHolder);
        }
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }


    public void switchCamera() {
        int cameraCount;
        Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
        cameraCount = Camera.getNumberOfCameras();

        //遍历可用摄像头
        for(int i = 0 ;i < cameraCount;i++){

            Camera.getCameraInfo(i, cameraInfo);
            if(isBackCameraOn){
                if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    releaseCamera();
                    mCamera = Camera.open(i);
                    setStartPreview(mCamera, mSurfaceHolder);
                    isBackCameraOn = false;
                    break;
                }
            }else{
                if(cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK){
                    releaseCamera();
                    mCamera = Camera.open(i);
                    setStartPreview(mCamera, mSurfaceHolder);
                    isBackCameraOn = true;
                    break;
                }
            }
        }
    }


    public void autoFocus(){
        if (mCamera != null) {
            mCamera.autoFocus(null);
        }
    }

    public void capture(View view) {




        //自动对焦
        mCamera.autoFocus(new Camera.AutoFocusCallback() {
            @Override
            public void onAutoFocus(boolean success, Camera camera) {

                Log.e("autoFocus", "----- success = " + success);
                if (success) {
                    mCamera.takePicture(null,null,null,mPictureCallback);
                }
            }
        });
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            ////竖屏显示
            mIsPortrait = true;
        } else if (this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //横屏显示
            mIsPortrait = false;
        }
    }

    public void setCameraParams(int width, int height){
//        mSurfaceHolder.setFixedSize(width, height);
        Camera.Parameters parameters = mCamera.getParameters();

        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();

        Camera.Size closelyPreSize = getCloselyPreSize(width, height, supportedPictureSizes);
        //        parameters.setPreviewFpsRange(4,10); //fps
        //        parameters.setJpegQuality(100);


        parameters.setPictureFormat(ImageFormat.JPEG);
//        parameters.setPreviewSize(closelyPreSize.width, closelyPreSize.height);
        parameters.setPreviewSize(height, width);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
        mCamera.setParameters(parameters);
    }
    /**
     * 通过对比得到与宽高比最接近的尺寸（如果有相同尺寸，优先选择）
     *
     * @param surfaceWidth
     *            需要被进行对比的原宽
     * @param surfaceHeight
     *            需要被进行对比的原高
     * @param preSizeList
     *            需要对比的预览尺寸列表
     * @return 得到与原宽高比例最接近的尺寸
     */
    protected Camera.Size getCloselyPreSize(int surfaceWidth, int surfaceHeight,
                                            List<Camera.Size> preSizeList) {

        int ReqTmpWidth;
        int ReqTmpHeight;
        // 当屏幕为垂直的时候需要把宽高值进行调换，保证宽大于高
        if (mIsPortrait) {
            ReqTmpWidth = surfaceHeight;
            ReqTmpHeight = surfaceWidth;
        } else {
            ReqTmpWidth = surfaceWidth;
            ReqTmpHeight = surfaceHeight;
        }

        Log.e("getCloselyPreSize", " ReqTmpWidth = " + ReqTmpWidth + " ReqTmpHeight = " + ReqTmpHeight);
        //先查找preview中是否存在与surfaceview相同宽高的尺寸
        for(Camera.Size size : preSizeList){
            if((size.width == ReqTmpWidth) && (size.height == ReqTmpHeight)){
                return size;
            }
        }

        // 得到与传入的宽高比最接近的size
        float reqRatio = ((float) ReqTmpWidth) / ReqTmpHeight;
        float curRatio, deltaRatio;
        float deltaRatioMin = Float.MAX_VALUE;
        Camera.Size retSize = null;
        for (Camera.Size size : preSizeList) {
            curRatio = ((float) size.width) / size.height;
            deltaRatio = Math.abs(reqRatio - curRatio);
            if (deltaRatio < deltaRatioMin) {
                deltaRatioMin = deltaRatio;
                retSize = size;
            }
        }

        return retSize;
    }
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File file = getOutputMediaFile();
            if (file == null) {
                return;
            }

            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(data);
                fos.close();
                Log.e("onPictureTaken", " ----- fos -=== " + file.getAbsolutePath());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };


    /**
     * 获取图片保持路径
     *
     * @return pic Path
     */
    private File getOutputMediaFile() {
        File mediaStorageDir = new File(Environment
                .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                return null;
            }
        }
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "temp.png");
        return mediaFile;
    }
    /**
     * 使用Camera.open（）时始终检查异常。如果相机正在使用或不存在，则无法检查异常，将导致您的应用程序被系统关闭。
     * @return
     */
    public Camera getCamera(){
        Camera camera = null;

        try{
            camera = Camera.open();
        }catch (Exception e){
            camera = null;
        }

        return camera;
    }

    /**
     * 释放相机
     */
    public void releaseCamera(){

        if(mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();
            mCamera = null;
        }
    }


    public void setStartPreview(Camera camera, SurfaceHolder surfaceHolder) {
        try {
            camera.setPreviewDisplay(surfaceHolder);
            if(mIsPortrait){
                camera.setDisplayOrientation(90);
            }else{
                camera.setDisplayOrientation(0);
            }
            camera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        setStartPreview(mCamera, holder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


        Log.e("surfaceChanged", " ---- width = " + width + "   height = " + height);

        Log.e("surfaceChanged", "-----> getDeviceHeight = " + getDeviceHeight());

        mSurfaceWidth = width;
        mSurfaceHeight = height;
        if (mSurfaceHolder.getSurface() == null || mCamera == null) {
            return;
        }

        try {
            mCamera.stopPreview();
        }catch (Exception e){
            e.printStackTrace();
        }
//        Camera.Parameters parameters = mCamera.getParameters();
        //        List<Camera.Size> supportedPictureSizes = parameters.getSupportedPictureSizes();
        //        Camera.Size size = getCloselyPreSize(width, height, supportedPictureSizes);
        //        setCameraParams(mIsPortrait == true ? size.height : size.width, mIsPortrait == true ? size.width : size.height);

        setStartPreview(mCamera,mSurfaceHolder);
        setCameraParams(width, height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        releaseCamera();
    }

    public  int getDeviceHeight(){
        return mContext.getResources().getDisplayMetrics().heightPixels;
    }
}
