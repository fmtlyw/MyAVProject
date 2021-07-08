package com.lyw.live;

import android.app.Activity;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;

/**
 * 功能描述:视频流
 * Created on 2021/7/5.
 *
 * @author lyw
 */
public class VideoStream implements Camera.PreviewCallback, CameraHelper.OnChangedSizeListener {
    private static final String TAG = "VideoStream";
    private CameraHelper mCameraHelper;
    private LivePusherNew mLivePusherNew;
    private Activity mActivity;
    private int mBitRate;
    private int mFprRate;

    private boolean isLiving;


    public VideoStream(LivePusherNew livePusherNew, Activity activity, int width, int height, int bitRate, int fprRate, int cameraId) {
        this.mLivePusherNew = livePusherNew;
        this.mBitRate = bitRate;
        this.mFprRate = fprRate;
        mCameraHelper = new CameraHelper(activity, height, width, cameraId);
        mCameraHelper.setPreviewCallback(this);
        mCameraHelper.setOnChangedSizeListener(this);
    }

    public void setPreviewDisplay(SurfaceHolder mHolder) {
        mCameraHelper.setPreviewDisplay(mHolder);
    }

    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        if (isLiving) {
            Log.d(TAG, "onPreviewFrame");
            mLivePusherNew.pushVideo(bytes);
        }
    }

    public void switchCarema() {
        mCameraHelper.switchCamera();
    }

    @Override
    public void onChanged(int w, int h) {
        mLivePusherNew.setVideoCodecInfo(w, h, mFprRate, mBitRate);
    }

    public void stopLive() {
        isLiving = false;
    }

    public void startLive() {
        isLiving = true;
    }

    public void release() {
        mCameraHelper.release();
    }
}
