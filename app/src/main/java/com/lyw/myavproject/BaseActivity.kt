package com.lyw.myavproject

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

/**
 * 功能描述:
 * Created on 2021/7/7.
 * @author lyw
 */
abstract class BaseActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
            && checkSelfPermission(permissions[0]) != PackageManager.PERMISSION_GRANTED
            && checkSelfPermission(permissions[1]) != PackageManager.PERMISSION_GRANTED) {
            requestPession()
        }
    }

    private fun requestPession() {
        requestPermission(permissions)
    }

    protected fun requestPermission(permissions: Array<String>) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(permissions, REQUEST_CODE)
        }
    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName

        private const val REQUEST_CODE = 1234
        private val permissions = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO)
    }
}