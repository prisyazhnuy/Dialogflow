package com.prisyazhnuy.dialogflow

import ai.api.AIConfiguration
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIResponse
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import io.reactivex.functions.Consumer

class MainViewModel(app: Application) : AndroidViewModel(app) {

    private val TAG = MainViewModel::class.java.simpleName

    val speechLiveData = MutableLiveData<String>()
    val dataLiveData = MutableLiveData<String>()
    val intentLiveData = MutableLiveData<String>()

    private val config by lazy {
        ai.api.android.AIConfiguration(BuildConfig.CLIENT_ACCESS_TOKEN,
                AIConfiguration.SupportedLanguages.English,
                ai.api.android.AIConfiguration.RecognitionEngine.System)
    }

    private val aiService by lazy {
        AIService.getService(app, config).apply {
            setListener(object : SimpleAIListener() {
                override fun onResult(result: AIResponse?) {
                    Log.i(TAG, "Speech result: ${result?.result}")
                    aiDataConsumer.accept(result)
                }

                override fun onError(error: AIError?) {
                    Log.e(TAG, "Speech error result: ${error?.message}")
                }
            })
        }
    }

    private val aiDataConsumer = Consumer<AIResponse> {
        with(it.status) {
            Log.i(TAG, "Code: $code, type: $errorType")
        }
        with(it.result) {
            dataLiveData.value = fulfillment.speech
            Log.i(TAG, "Resolved query: $resolvedQuery")
            Log.i(TAG, "Action: $action")
            Log.i(TAG, "Speech: ${fulfillment.speech}")
            Log.i(TAG, "Parameters: $parameters")
            metadata?.let { it ->
                Log.i(TAG, "Metadata intent id: ${it.intentId}")
                Log.i(TAG, "Intent name: ${it.intentName}")
                intentLiveData.value = it.intentName
            }
        }
    }

    fun startListening() {
        aiService.startListening()
    }

    fun stopListening() {
        aiService.stopListening()
    }
}