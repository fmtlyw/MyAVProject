package com.lyw.myavproject

import android.media.AudioFormat
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.CompoundButton
import android.widget.ToggleButton
import com.lyw.live.AudioParam
import com.lyw.live.CameraHelper
import com.lyw.live.LivePusherNew
import com.lyw.live.VideoParam

class MainActivity : BaseActivity(), CompoundButton.OnCheckedChangeListener {

    private var surfaceView: SurfaceView? = null
    private var mLivePusher : LivePusherNew? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        initPushNew()
    }

    private fun initView() {
        surfaceView = findViewById<SurfaceView>(R.id.surface_camera)

        findViewById<View>(R.id.btn_swap).setOnClickListener {
            mLivePusher!!.switchCamera()
        }

        (findViewById<View>(R.id.btn_live) as ToggleButton).setOnCheckedChangeListener(this)
        (findViewById<View>(R.id.btn_mute) as ToggleButton).setOnCheckedChangeListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mLivePusher!!.release()
    }


    private fun initPushNew() {
        //视频参数
        val width = 640
        val height = 480
        val bitRate = 800000
        val frameRate = 10
        val videoParam = VideoParam(width,height,Integer.valueOf(CameraHelper.CAMERA_ID_BACK),bitRate, frameRate)

        //音频参数
        val channelconfig = AudioFormat.CHANNEL_IN_STEREO
        val simpleRate = 44100
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val numChannels = 2
        val audioParam = AudioParam(channelconfig,simpleRate,audioFormat, numChannels)


        mLivePusher = LivePusherNew(this,videoParam,audioParam)
        mLivePusher!!.setPreviewDisplay(surfaceView!!.holder)
    }

    override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
        when (buttonView.id) {
            R.id.btn_live ->
                mLivePusher!!.let {
                    if(isChecked) it.startPush(URL) else it.stopPush()
                }
            R.id.btn_mute->mLivePusher!!.setMute(isChecked)
        }
    }

    companion object{

        private val TAG = MainActivity::class.java.simpleName
        //const val 可见性为public final static，可以直接访问。
        private const val URL = "rtmp://live-push.bilivideo.com/live-bvc/?streamname=live_1792239063_87501789&key=b9a0bfebde52671720a2b933f05973b5&schedule=rtmp&pflag=1"

    }
}
