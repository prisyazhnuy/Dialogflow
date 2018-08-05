package com.prisyazhnuy.dialogflow

import android.Manifest
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer

class MainActivity : AppCompatActivity() {

    private val rxPermission by lazy { RxPermissions(this) }

    private val cameraConsumer = Consumer<Boolean> {
        if (it == true) {
            //permission granted
        } else {
            //permission denied
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun checkAudioPermission() {
        rxPermission.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(cameraConsumer)
    }
}
