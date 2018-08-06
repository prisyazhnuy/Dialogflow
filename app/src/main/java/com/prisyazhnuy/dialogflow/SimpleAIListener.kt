package com.prisyazhnuy.dialogflow

import ai.api.AIListener
import ai.api.model.AIError
import ai.api.model.AIResponse

open class SimpleAIListener : AIListener {

    override fun onResult(result: AIResponse?) {
        // here process response
    }

    override fun onError(error: AIError?) {
        // here process error
    }

    override fun onAudioLevel(level: Float) {
        // callback for sound level visualization
    }

    override fun onListeningStarted() {
        // indicate start listening here
    }

    override fun onListeningCanceled() {
        // indicate stop listening here
    }

    override fun onListeningFinished() {
        // indicate stop listening here
    }
}