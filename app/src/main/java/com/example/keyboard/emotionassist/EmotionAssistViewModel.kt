package com.example.keyboard.emotionassist

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.tasks.genai.llminference.LlmInference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class EmotionAssistViewModel : ViewModel() {

    private var llmInference: LlmInference? = null

    private val _response = MutableStateFlow("")
    val response: StateFlow<String> = _response

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun initModel(
        context: Context
    ) {
        if (llmInference != null) return

        viewModelScope.launch {
            val path = prepareModelFromAssets(
                context, "gemma.task"
            )

            val options = LlmInference.LlmInferenceOptions.builder()
                .setModelPath(path)
                .setMaxTokens(1024)
                .setMaxTopK(64)
                .build()

            llmInference = LlmInference.createFromOptions(context, options)

            Log.d("taaag", "Model is initialized")
        }
    }

    private suspend fun prepareModelFromAssets(
        context: Context,
        fileName: String
    ): String = withContext(Dispatchers.IO) {
        val outputFile = File(context.filesDir, fileName)

        if (!outputFile.exists()) {
            context.assets.open(fileName).use { inputStream ->
                outputFile.outputStream().use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
            }
        }

        outputFile.absolutePath
    }

    fun generateResponse(
        prompt: String
    ) {
        _isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {
            val result = llmInference?.generateResponse(prompt)
            if (result != null) {
                _response.value = result
            }

            _isLoading.value = false
        }
    }
}