package com.prisyazhnuy.dialogflow

import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity

abstract class BaseActivity : AppCompatActivity() {
    protected open fun showErrorDialog() {
        AlertDialog.Builder(this)
                .setTitle(R.string.error_title)
                .setMessage(R.string.unknown_intent_message)
                .setPositiveButton(android.R.string.ok) { dialogInterface, _ -> dialogInterface.dismiss() }
                .show()
    }
}