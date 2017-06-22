package com.elk.wxplayer.record;

import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.elk.wxplayer.R;

import java.io.File;
import java.io.IOException;

/**
 * 使用MediaRecorder录制声音的步骤：
 1) 创建 MediaRecorder 对象。
 2) 调用MediaRecorder对象的setAudioSource()方法设置声音来源，一般传入 MediaRecorder. AudioSource.MIC参数指定录制来自麦克风的声音。
 3) 调用MediaRecorder对象的setOutputFormat()设置所录制的音频文件的格式。
 4) 调用MediaRecorder 对象的setAudioEncoder()、setAudioEncodingBitRate(intbitRate)、 setAudioSamplingRate(int samplingRate)设置所录制的声音的编码格式、编码位率、采样率等， 这些参数将可以控制所录制的声音的品质、文件的大小。一般来说，声音品质越好，声音文件越大。
 5) 调用MediaRecorder的setOutputFile(Stringpath)方法设置录制的音频文件的保存位置。
 6) 调用MediaRecorder的prepare()方法准备录制。
 7) 调用MediaRecorder对象的start()方法开始录制。
 8) 录制完成，调用MediaRecorder对象的stop()方法停止录制，并调用release()方法释放资源。
 提示：1.上面的步骤中第3和第4两个步骤千万不能搞反，否则程序将会抛出lllegalStateException 异常。
 2. 设置声音编码格式要和声音的输出格式相对应，不然录制的音频文件不标准。如果编码格式和输出格式不对应，录制出的音频文件虽然可以播放，但是将多个这类音频文件合并之后，会出现只播放合并文件中的部分文件。
 */
public class RecordSoundActivity extends AppCompatActivity {

    private Button mRecord;
    private Button mStop;
    private File mSoundFile;
    private MediaRecorder mMediaRecorder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_sound);

        mRecord = (Button) findViewById(R.id.btn_record);
        mStop = (Button) findViewById(R.id.btn_stop);

        mRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });
        mStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

    }

    private void stop() {
        if(mSoundFile != null && mSoundFile.exists()){
            mMediaRecorder.stop();
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    private void record() {
        if (!Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED)) {
            Toast.makeText(this, "SD卡不存在，请插入SD卡！",
                    Toast.LENGTH_SHORT).show();
            return;
        }


        // 创建保存录音的音频文件
        try {
            mSoundFile = new File(Environment
                     .getExternalStorageDirectory().getCanonicalFile()
                     + "/sound.amr");

            mMediaRecorder = new MediaRecorder();

            //set sound
            mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
            mMediaRecorder.setOutputFile(mSoundFile.getAbsolutePath());

            mMediaRecorder.prepare();
            mMediaRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
