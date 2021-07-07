package com.lyw.live;

import android.app.Activity;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * 功能描述:
 * Created on 2021/7/5.
 *
 * @author lyw
 */
public class LivePusherNew {
    private static final String TAG = "LivePusherNew";

    static {
        System.loadLibrary("live");
    }

    private AudioStream mAudioStream;
    private VideoStream mVideoStream;

    private VideoParam mVideoParam;
    private AudioParam mAudioParam;

    private Activity mActivity;

    public LivePusherNew(Activity mActivity, VideoParam mVideoParam, AudioParam mAudioParam) {
        this.mVideoParam = mVideoParam;
        this.mAudioParam = mAudioParam;
        this.mActivity = mActivity;
        native_init();
        mVideoStream = new VideoStream(this,mActivity,mVideoParam.getWidth(),mVideoParam.getHeight(),mVideoParam.getBitRate(),mVideoParam.getFrameRate(),mVideoParam.getCameraId());
    }

    /**
     * 设置画布
     *
     * @param surfaceHolder
     */
    public void setPreviewDisplay(SurfaceHolder surfaceHolder) {
        mVideoStream.setPreviewDisplay(surfaceHolder);
    }

    /**
     * 开始推送
     */
    public void startPush(String url) {
        native_start(url);
        mVideoStream.startLive();
    }

    /**
     * 停止推送
     */
    public void stopPush() {
        mVideoStream.stopLive();
        native_stop();
    }

    /**
     * 释放资源
     */
    public void release(){
        native_release();
    }


    public void switchCamera(){
        mVideoStream.switchCarema();
    }


    public void pushVideo(byte[] data) {
        native_pushVideo(data);
    }

    public void pushAudio(){

    }


    public void setVideoCodecInfo(int width, int height, int fps, int bitrate){
        native_setVideoCodecInfo(width, height, fps, bitrate);
    }


    /**
     * Callback this method, when native occurring error
     *
     * @param errCode errCode
     */
    public void errorFromNative(int errCode) {
        //stop pushing stream
        stopPush();
    }


    private native void native_init();

    private native void native_start(String path);

    private native void native_setVideoCodecInfo(int width, int height, int fps, int bitrate);

    private native void native_setAudioCodecInfo(int sampleRateInHz, int channels);

    private native int getInputSamples();

    private native void native_pushAudio(byte[] data);

    private native void native_pushVideo(byte[] data);

    private native void native_pushVideoNew(byte[] y, byte[] u, byte[] v);

    private native void native_stop();

    private native void native_release();
}
