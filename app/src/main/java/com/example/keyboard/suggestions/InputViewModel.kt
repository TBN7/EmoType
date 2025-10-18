package com.example.keyboard.suggestions

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class InputViewModel: ViewModel() {
    private val _currentInput = MutableStateFlow("")
    val currentInput: StateFlow<String> = _currentInput.asStateFlow()

    fun updateInput(letter: String) {
        _currentInput.value += letter
    }

    fun onSpacePressed() {
        _currentInput.value += " "
    }
}