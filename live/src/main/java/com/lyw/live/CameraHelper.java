package com.lyw.live;

import android.app.Activity;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.List;

/**
 * 功能描述:Camera2帮助类
 * Created on 2021/7/5.
 *
 * @author lyw
 */
public class CameraHelper implements SurfaceHolder.Callback, Camera.PreviewCallback {
    private static final String TAG = "CameraHelper";
    private Activity mActivity;
    private int mHeight;
    private int mWidth;

    private byte[] buffer;
    private byte[] bytes;

    private int mRotation;


    public static final String CAMERA_ID_FRONT = "1";
    public static final String CAMERA_ID_BACK = "0";

    private SurfaceHolder mSurfaceHolder;
    private int mCameraId;

    private Camera mCamera;
    private Camera.PreviewCallback mPreviewCallback;

    private OnChangedSizeListener mOnChangedSizeListener;


    public CameraHelper(Activity mActivity, int mHeight, int mWidth,int mCameraId) {
        this.mActivity = mActivity;
        this.mHeight = mHeight;
        this.mWidth = mWidth;
        this.mCameraId = mCameraId;
    }

    public void setPreviewDisplay(SurfaceHolder mHolder) {
        this.mSurfaceHolder = mHolder;
        mSurfaceHolder.addCallback(this);
    }


    void setPreviewCallback(Camera.PreviewCallback previewCallback) {
        mPreviewCallback = previewCallback;
    }


    public void switchCamera() {
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
        } else {
            mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        }

        stopPreview();
        startPreView();
    }

    private void startPreView() {
        try {
            mCamera = Camera.open(mCameraId);
            Camera.Parameters parameters = mCamera.getParameters();

            /**
             * 1、设置视频输出格式
             * 2、设置预览大小
             * 3、设置预览方向
             */

            parameters.setPreviewFormat(ImageFormat.NV21);
            setPreviewSize(parameters);
            setPreviewOrientation(parameters);
            mCamera.setParameters(parameters);


            // FIXME: 2021/7/5 搞不懂为什么他的大小是这个
            buffer = new byte[mWidth * mHeight * 3 / 2];
            bytes = new byte[buffer.length];


            //设置缓存
            mCamera.addCallbackBuffer(buffer);
            mCamera.setPreviewCallbackWithBuffer(this);

            //设置预览画布
            mCamera.setPreviewDisplay(mSurfaceHolder);
            mCamera.startPreview();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 设置预览方向
     *
     * @param parameters
     */
    private void setPreviewOrientation(Camera.Parameters parameters) {
        // FIXME: 2021/7/5 待研究
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(mCameraId, info);
        mRotation = mActivity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (mRotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                mOnChangedSizeListener.onChanged(mHeight, mWidth);
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                mOnChangedSizeListener.onChanged(mWidth, mHeight);
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                mOnChangedSizeListener.onChanged(mWidth, mHeight);
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360; // compensate the mirror
        } else { // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(result);

    }

    /**
     * 设置预览大小
     */
    private void setPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> supportedPreviewSizes = parameters.getSupportedPreviewSizes();
        Camera.Size size = supportedPreviewSizes.get(0);
        //选择最好的大小,最接近传入宽高的大小(传入的宽高不一定就是摄像头支持的宽高)
        int m = Math.abs(size.width * size.height - mWidth * mHeight);
        supportedPreviewSizes.remove(0);

        for (Camera.Size next : supportedPreviewSizes) {
            int n = Math.abs(next.height * next.width - mWidth * mHeight);
            if (n < m) {
                m = n;
                size = next;
            }
        }

        mWidth = size.width;
        mHeight = size.height;
        parameters.setPreviewSize(mWidth, mHeight);
    }

    private void stopPreview() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
            mCamera.release();
            mCamera = null;
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {
        //data的数据方向是相反的
        switch (mRotation) {
            case Surface.ROTATION_0:
                rotation90(data);
                break;
            case Surface.ROTATION_90:
            case Surface.ROTATION_270:
            default:
                break;
        }
        if (mPreviewCallback!= null) {
            Log.d(TAG,"onPreviewFrame");
            mPreviewCallback.onPreviewFrame(bytes,camera);
        }
        mCamera.addCallbackBuffer(buffer);
    }


    // FIXME: 2021/7/5 待研究
    private void rotation90(byte[] data) {
        int index = 0;
        int ySize = mWidth * mHeight;
        int uvHeight = mHeight / 2;
        //back camera rotate 90 deree
        if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {

            for (int i = 0; i < mWidth; i++) {
                for (int j = mHeight - 1; j >= 0; j--) {
                    bytes[index++] = data[mWidth * j + i];
                }
            }

            for (int i = 0; i < mWidth; i += 2) {
                for (int j = uvHeight - 1; j >= 0; j--) {
                    // v
                    bytes[index++] = data[ySize + mWidth * j + i];
                    // u
                    bytes[index++] = data[ySize + mWidth * j + i + 1];
                }
            }
        } else {
            //rotate 90 degree
            for (int i = 0; i < mWidth; i++) {
                int nPos = mWidth - 1;
                for (int j = 0; j < mHeight; j++) {
                    bytes[index++] = data[nPos - i];
                    nPos += mWidth;
                }
            }
            //u v
            for (int i = 0; i < mWidth; i += 2) {
                int nPos = ySize + mWidth - 1;
                for (int j = 0; j < uvHeight; j++) {
                    bytes[index++] = data[nPos - i - 1];
                    bytes[index++] = data[nPos - i];
                    nPos += mWidth;
                }
            }
        }
    }

    @Override
    public void surfaceCreated(@NonNull SurfaceHolder surfaceHolder) {

    }

    @Override
    public void surfaceChanged(@NonNull SurfaceHolder surfaceHolder, int i, int i1, int i2) {
        Log.d(TAG,"画布创建");
        stopPreview();
        startPreView();
    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder surfaceHolder) {
        stopPreview();
    }

    void setOnChangedSizeListener(OnChangedSizeListener listener) {
        mOnChangedSizeListener = listener;
    }

    public interface OnChangedSizeListener {
        void onChanged(int w, int h);
    }

    public void release() {
        mSurfaceHolder.removeCallback(this);
        stopPreview();
    }
}
