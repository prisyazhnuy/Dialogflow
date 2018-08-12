package com.prisyazhnuy.dialogflow.registration

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.app.AlertDialog
import com.prisyazhnuy.dialogflow.BaseActivity
import com.prisyazhnuy.dialogflow.R
import kotlinx.android.synthetic.main.activity_registration.*

class RegistrationActivity : BaseActivity() {

    private val viewModel by lazy {
        ViewModelProviders.of(this).get(RegistrationViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)
        etEmail.setOnClickListener { viewModel.startListening() }
        etPhoneNumber.setOnClickListener { viewModel.startListening() }
        observeLiveData()
    }

    override fun showErrorDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.unknown_intent_message)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> viewModel.startListening() }
                .setNegativeButton(android.R.string.cancel) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }

    private fun observeLiveData() {
        with(viewModel) {
            emailLiveData.observe(this@RegistrationActivity, Observer {
                etEmail.setText(it)
                viewModel.startListening()
            })
            phoneLiveData.observe(this@RegistrationActivity, Observer {
                etPhoneNumber.apply {
                    setText(it)
                    setSelection(text.length)
                }
            })
            unknownSpeechLiveData.observe(this@RegistrationActivity, Observer { showErrorDialog() })
        }
    }

}
