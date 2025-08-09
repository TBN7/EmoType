package com.example.keyboard.model

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit

class KeyboardLanguageManager(
    private val context: Context
) {
    private val sharedPrefs = context.getSharedPreferences("keyboard_prefs", Context.MODE_PRIVATE)
    private val allLanguages = listOf(English, Russian)

    private var currentLanguageIndex by mutableIntStateOf(0)
    val currentLanguage: KeyboardLanguageConfig
        get() = allLanguages[currentLanguageIndex]

    init {
        val savedLanguageCode = sharedPrefs.getString("selected_language", English.code)
        currentLanguageIndex = allLanguages.indexOfFirst {
            it.code == savedLanguageCode
        }.takeIf { it >= 0 } ?: 0
    }

    fun switchToNextLanguage() {
        currentLanguageIndex = (currentLanguageIndex + 1) % allLanguages.size
        sharedPrefs.edit {
            putString("selected_language", currentLanguage.code)
        }
    }
}