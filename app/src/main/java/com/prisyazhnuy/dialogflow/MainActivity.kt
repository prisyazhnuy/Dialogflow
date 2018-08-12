package com.prisyazhnuy.dialogflow

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.os.Bundle
import com.prisyazhnuy.dialogflow.registration.RegistrationActivity
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.functions.Consumer
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(MainViewModel::class.java)
    }

    private val rxPermission by lazy { RxPermissions(this) }

    private val cameraConsumer = Consumer<Boolean> {
        if (it == true) {
            //permission granted
            viewModel.startListening()
        } else {
            //permission denied
            finish()
        }
    }

    private val speechObserver = Observer<String> {
        it?.let {
            tvResult.apply {
                if (text.isNotEmpty()) append("\n")
                append(it)
            }
        }
    }

    private val intentObserver = Observer<String> {
        it?.let {
            when (it) {
                DialogflowIntent.REGISTRATION.intent -> openRegistration()
                else -> showErrorDialog()
            }
        }
    }

    private fun openRegistration() {
        startActivity(Intent(this, RegistrationActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        observeLiveData()
        btnRecord.setOnClickListener { checkAudioPermission() }
        btnStopRecord.setOnClickListener { viewModel.stopListening() }
    }

    private fun observeLiveData() {
        with(viewModel) {
            speechLiveData.observe(this@MainActivity, speechObserver)
            dataLiveData.observe(this@MainActivity, speechObserver)
            intentLiveData.observe(this@MainActivity, intentObserver)
        }
    }

    private fun checkAudioPermission() {
        rxPermission.request(Manifest.permission.RECORD_AUDIO)
                .subscribe(cameraConsumer)
    }
}
