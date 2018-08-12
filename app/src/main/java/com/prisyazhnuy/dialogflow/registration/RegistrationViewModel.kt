package com.prisyazhnuy.dialogflow.registration

import ai.api.AIConfiguration
import ai.api.AIDataService
import ai.api.android.AIService
import ai.api.model.AIError
import ai.api.model.AIRequest
import ai.api.model.AIResponse
import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import com.prisyazhnuy.dialogflow.BuildConfig
import com.prisyazhnuy.dialogflow.DialogflowIntent
import com.prisyazhnuy.dialogflow.SimpleAIListener
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers

class RegistrationViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private const val EMAIL_PARAM = "email"
        private const val PHOME_PARAM = "phone-number"
    }

    private val TAG = RegistrationViewModel::class.java.simpleName

    val emailLiveData = MutableLiveData<String>()
    val phoneLiveData = MutableLiveData<String>()


    val speechLiveData = MutableLiveData<String>()
    val unknownSpeechLiveData = MutableLiveData<String>()

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
                    speechLiveData.value = result?.result?.resolvedQuery
                    val aiRequest = AIRequest().apply { setQuery(result?.result?.resolvedQuery) }
                    Single.just(Unit)
                            .map { aiDataService.request(aiRequest) }
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(aiDataConsumer, aiErrorConsumer)
                }

                override fun onError(error: AIError?) {
                    Log.e(TAG, "Speech error result: ${error?.message}")
                }
            })
        }
    }

    private val aiDataService by lazy { AIDataService(config) }

    private val aiDataConsumer = Consumer<AIResponse> {
        with(it.status) {
            Log.i(TAG, "Code: $code, type: $errorType")
        }
        with(it.result) {
            Log.i(TAG, "Resolved query: $resolvedQuery")
            Log.i(TAG, "Action: $action")
            Log.i(TAG, "Speech: ${fulfillment.speech}")
            metadata?.let { it ->
                Log.i(TAG, "Metadata intent id: ${it.intentId}")
                Log.i(TAG, "Intent name: ${it.intentName}")
                when (it.intentName) {
                    DialogflowIntent.EMAIL.intent -> emailLiveData.value = parameters[EMAIL_PARAM]?.asString
                    DialogflowIntent.PHONE_NUMBER.intent -> phoneLiveData.value = parameters[PHOME_PARAM]?.asString
                    else -> unknownSpeechLiveData.value = fulfillment.speech
                }
            }
        }
    }

    private val aiErrorConsumer = Consumer<Throwable> {
        Log.e(TAG, "Error: ${it.message}")
    }

    fun startListening() {
        aiService.startListening()
    }

    fun stopListening() {
        aiService.stopListening()
    }
}