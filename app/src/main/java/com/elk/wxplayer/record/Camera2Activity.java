package com.elk.wxplayer.record;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.elk.wxplayer.R;

public class Camera2Activity extends AppCompatActivity {
    private Button mSwitchCamera;
    private Button mCapture;
    private CameraSurfaceView mSurfaceView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        mSurfaceView = (CameraSurfaceView) findViewById(R.id.sv_camera);
        mSwitchCamera = (Button) findViewById(R.id.btn_switch_camera);
        mCapture = (Button) findViewById(R.id.btn_capture);

        mSurfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.autoFocus();
            }
        });


        mSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.switchCamera();
            }
        });

        mCapture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSurfaceView.capture(v);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSurfaceView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSurfaceView.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mSurfaceView != null){
            mSurfaceView.releaseCamera();
        }
    }
}
