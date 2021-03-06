package com.lyw.live;

/**
 * 功能描述:视频参数
 * Created on 2021/7/5.
 *
 * @author lyw
 */
public class VideoParam {
    private int width;
    private int height;
    private int cameraId;
    private int bitRate;
    private int frameRate;


    public VideoParam(int width, int height, int cameraId, int bitRate, int frameRate) {
        this.width = width;
        this.height = height;
        this.cameraId = cameraId;
        this.bitRate = bitRate;
        this.frameRate = frameRate;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getCameraId() {
        return cameraId;
    }

    public void setCameraId(int cameraId) {
        this.cameraId = cameraId;
    }

    public int getBitRate() {
        return bitRate;
    }

    public void setBitRate(int bitRate) {
        this.bitRate = bitRate;
    }

    public int getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(int frameRate) {
        this.frameRate = frameRate;
    }
}
