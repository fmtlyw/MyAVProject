package com.lyw.live;

/**
 * 功能描述:音频参数
 * Created on 2021/7/5.
 *
 * @author lyw
 */
public class AudioParam {

    //通道，单声道/多声道
    private int channelConfig;
    //采样率（可以理解为单位时间内录制多少音频），44100是目前的标准
    private int sampleRate;
    //音频数据的格式，  AudioFormat.ENCODING_PCM_16BIT
    private int audioFormat;
    //通道数据
    private int numChannels;

    public AudioParam(int channelConfig, int sampleRate, int audioFormat, int numChannels) {
        this.channelConfig = channelConfig;
        this.sampleRate = sampleRate;
        this.audioFormat = audioFormat;
        this.numChannels = numChannels;
    }

    public int getChannelConfig() {
        return channelConfig;
    }

    public void setChannelConfig(int channelConfig) {
        this.channelConfig = channelConfig;
    }

    public int getSampleRate() {
        return sampleRate;
    }

    public void setSampleRate(int sampleRate) {
        this.sampleRate = sampleRate;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public int getNumChannels() {
        return numChannels;
    }

    public void setNumChannels(int numChannels) {
        this.numChannels = numChannels;
    }
}
