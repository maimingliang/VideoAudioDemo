package com.elk.wxplayer;

import android.content.Context;
import android.os.Handler;
import android.support.annotation.DrawableRes;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

/**
 * 类       名:
 * 说       明:
 * 修 改 记 录:
 * 版 权 所 有:   Copyright © 2017
 * 公       司:   深圳市旅联网络科技有限公司
 * version   0.1
 * date   2017/6/19
 * author   maimingliang
 */


public class WxMediaController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private Context mContext;
    private ImageView mImage;
    private ImageView mBack;
    private LinearLayout mBottom;
    private TextView mPosition;
    private TextView mDuration;
    private SeekBar mSeek;
    private ImageView mCenterStart;

    private WxMediaControll mControll;
    private ImageView mPasue;
    private Handler mHandler = new Handler();
     private LinearLayout mTop;
    private boolean mTopBottomVisible;
    private LinearLayout mLoading;

    public WxMediaController(Context context) {
        super(context);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.video_palyer_controller, this, true);
        mImage = (ImageView) findViewById(R.id.image);
        mBack = (ImageView) findViewById(R.id.back);
        mBottom = (LinearLayout) findViewById(R.id.bottom);
        mPosition = ((TextView) findViewById(R.id.position));
        mDuration = (TextView) findViewById(R.id.duration);
        mSeek = (SeekBar) findViewById(R.id.seek);
        mCenterStart = (ImageView) findViewById(R.id.center_start);
        mPasue = (ImageView) findViewById(R.id.restart_or_pause);
        mTop = (LinearLayout) findViewById(R.id.top);
        mLoading = (LinearLayout) findViewById(R.id.loading);

        mCenterStart.setOnClickListener(this);
        mPasue.setOnClickListener(this);
        mSeek.setOnSeekBarChangeListener(this);
        mBack.setOnClickListener(this);
        this.setOnClickListener(this);
        setTopBottomVisible(false);
    }

    public void setImage(String imageUrl) {
        Glide.with(mContext)
                .load(imageUrl)
                .placeholder(R.drawable.all_darkbackground)
                .crossFade()
                .into(mImage);
    }

    public void setImage(@DrawableRes int resId) {
        mImage.setImageResource(resId);
    }

    public void setWxPlayer(WxMediaControll controll){
        mControll =controll;
    }

    public void setControllerState(int currState){

        Log.e("tag", "------ currState = " + currState);
        switch (currState){
            case WxPlayer.STATE_IDLE:
                break;
            case WxPlayer.STATE_ERROR:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mBottom.setVisibility(GONE);
                mLoading.setVisibility(GONE);
                mTop.setVisibility(GONE);
                removeCallback();
                setTopBottomVisible(false);
                break;
            case WxPlayer.STATE_COMPLETED:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(VISIBLE);
                mBottom.setVisibility(VISIBLE);
                mTop.setVisibility(VISIBLE);
                mLoading.setVisibility(GONE);
                removeCallback();
                setTopBottomVisible(false);
                break;
            case WxPlayer.STATE_PREPARING:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mBottom.setVisibility(VISIBLE);
                mTop.setVisibility(VISIBLE);
                mLoading.setVisibility(VISIBLE);
                mPasue.setImageResource(R.drawable.ic_player_start);
                break;
            case WxPlayer.STATE_PREPARED:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mBottom.setVisibility(VISIBLE);
                mTop.setVisibility(VISIBLE);
                mPasue.setImageResource(R.drawable.ic_player_start);
                mLoading.setVisibility(VISIBLE);
                startUpdateProgress();
                break;
            case WxPlayer.STATE_PLAYING:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mBottom.setVisibility(VISIBLE);
                mTop.setVisibility(VISIBLE);
                mLoading.setVisibility(GONE);
                mPasue.setImageResource(R.drawable.ic_player_pause);
                 break;
            case WxPlayer.STATE_PAUSED:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mBottom.setVisibility(VISIBLE);
                mTop.setVisibility(VISIBLE);
                mLoading.setVisibility(GONE);
                mPasue.setImageResource(R.drawable.ic_player_start);
                break;
            case WxPlayer.STATE_BUFFERING_PAUSED:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mLoading.setVisibility(VISIBLE);
                mPasue.setImageResource(R.drawable.ic_player_start);
                break;
            case WxPlayer.STATE_BUFFERING_PLAYING:
                mImage.setVisibility(GONE);
                mCenterStart.setVisibility(GONE);
                mLoading.setVisibility(VISIBLE);
                mPasue.setImageResource(R.drawable.ic_player_pause);
                break;
        }

    }


    private Runnable delayRunnable = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            startUpdateProgress();
        }
    };

    private Runnable dismissTopBottomRunnable = new Runnable() {
        @Override
        public void run() {
            setTopBottomVisible(false);
        }
    };

    private void updateProgress() {

        int position = mControll.getCurrentPosition();
        int duration = mControll.getDuration();

        if(duration == 0){
            return;
        }
        int bufferPercentage = mControll.getBufferPercentage();

        mSeek.setSecondaryProgress(bufferPercentage);
        int progress = (int) (100f * position / duration );
        mSeek.setProgress(progress);

        mPosition.setText(NiceUtil.formatTime(position));
        mDuration.setText(NiceUtil.formatTime(duration));
    }

    public void startUpdateProgress(){
        mHandler.postDelayed(delayRunnable, 500);
    }

    public void removeCallback(){
        mHandler.removeCallbacks(delayRunnable);
    }

    private void startDismissTopBottomTimer() {
         mHandler.postDelayed(dismissTopBottomRunnable,3000);
    }

    private void setTopBottomVisible(boolean visible) {

        mTopBottomVisible = visible;
        mTop.setVisibility( visible == true ? VISIBLE : INVISIBLE);
        mBottom.setVisibility( visible == true ? VISIBLE : INVISIBLE);
         if(!visible){
             cancelDismissTopBottomTimer();
         }else{
             startDismissTopBottomTimer();
         }


    }

    private void cancelDismissTopBottomTimer() {
         mHandler.removeCallbacks(dismissTopBottomRunnable);
    }

    @Override
    public void onClick(View v) {
        if(v == mCenterStart){
            if(mControll.isIDLE()){
                mControll.restart();
            }
        }else if(v == mPasue){
            if(mControll.isPlaying()){
                mControll.pause();
            }else{
                mControll.restart();
            }
        }else if(mBack == v){
            mControll.finish();
        }else if(v == this){
            setTopBottomVisible(!mTopBottomVisible);
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        //滑动开始
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        //滑动结束
         if(mControll.isPause()){
             mControll.restart();
         }
       int pos = (int) (mControll.getDuration() * seekBar.getProgress() / 100f); //
        mControll.seekTo(pos);

    }


    public interface WxMediaControll{
        void    release();
        void    start();
        void    restart();
        void    pause();
        int     getDuration();
        int     getCurrentPosition();
        void    seekTo(int pos);
        boolean isPlaying();
        boolean isIDLE();
        boolean isPause();
        int     getBufferPercentage();

        void finish();

    }

}
