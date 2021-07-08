package com.lyw.live;


import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 功能描述:音频流
 * Created on 2021/7/5.
 *
 * @author lyw
 */
public class AudioStream {
    private LivePusherNew mLivePusherNew;
    private Activity mActivity;

    private boolean isLiving;

    private AudioRecord mAudioRecord;
    private int minBufferSize;

    private int inputSamples;

    /**
     * 线程池
     */
    private ExecutorService executor;
    /**
     * 是否静音
     */
    private boolean isMute;


    public AudioStream(LivePusherNew mLivePusherNew, Activity mActivity, int channelConfig, int sampleRate, int audioFormat, int numChannels) {
        this.mLivePusherNew = mLivePusherNew;
        this.mActivity = mActivity;

        executor = Executors.newSingleThreadExecutor();
        mLivePusherNew.setAudioCodecInfo(sampleRate, numChannels);

        if (numChannels == 2) {
            channelConfig = AudioFormat.CHANNEL_IN_STEREO;//立体声
        } else {
            channelConfig = AudioFormat.CHANNEL_IN_MONO;//单声道
        }

        inputSamples = mLivePusherNew.getInputSample() * 2;

        minBufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat);
        int bufferSizeInBytes = minBufferSize > inputSamples ? minBufferSize : inputSamples;
        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, sampleRate, channelConfig, audioFormat, bufferSizeInBytes);
    }

    public void startLive() {
        isLiving = true;
        executor.submit(new AudioTask());
    }

    public void stopLive() {
        isLiving = false;
    }

    public void release() {
        mAudioRecord.release();
    }

    public class AudioTask implements Runnable {
        @Override
        public void run() {
            mAudioRecord.startRecording();
            byte[] audioData = new byte[inputSamples];
            while (isLiving) {
                if (!isMute) {
                    int read = mAudioRecord.read(audioData, 0, audioData.length);
                    //读取成功才推流
                    if (read > 0) {
                        mLivePusherNew.pushAudio(audioData);
                    }
                }
            }
            mAudioRecord.stop();
        }
    }

    /**
     * 设置静音
     * @param mute
     */
    public void setMute(boolean mute) {
        isMute = mute;
    }
}
