package com.example.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.annotation.CallSuper
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.keyboard.emotiondetection.CameraLayout
import com.example.keyboard.emotiondetection.EmotionDetectorViewModel
import com.example.keyboard.model.Emotion
import com.example.keyboard.model.Key
import com.example.keyboard.model.KeyboardLanguageManager
import com.example.keyboard.ui.theme.KeyboardLayout
import com.example.keyboard.ui.theme.KeyboardTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch

class KeyboardService : InputMethodService(),
    LifecycleOwner,
    SavedStateRegistryOwner, ViewModelStoreOwner {
    private val dispatcher = ServiceLifecycleDispatcher(this)
    override val lifecycle: Lifecycle = dispatcher.lifecycle

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    private val store = ViewModelStore()
    override val viewModelStore: ViewModelStore
        get() = store

    private var isShiftEnabled by mutableStateOf(false)
    private var emojiSuggestions by mutableStateOf(emptyList<String>())
    private var currentInput by mutableStateOf("")
    private var currentEmotion by mutableStateOf(Emotion.NEUTRAL)

    private lateinit var keyboardLanguageManager: KeyboardLanguageManager

    private val emotionDetectorViewModel: EmotionDetectorViewModel by lazy {
        ViewModelProvider(this)[EmotionDetectorViewModel::class]
    }

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        keyboardLanguageManager = KeyboardLanguageManager(this)
        savedStateRegistryController.performRestore(null)

        updateSuggestions()
    }

    @CallSuper
    override fun onBindInput() {
        super.onBindInput()
        dispatcher.onServicePreSuperOnBind()
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnDetachedFromWindow)
            setContent {
                KeyboardTheme {
                    Column {
                        CameraLayout()
                        KeyboardLayout (
                            languageManager = keyboardLanguageManager,
                            currentInput = currentInput,
                            currentEmotion = currentEmotion,
                            emojiSuggestions = emojiSuggestions,
                            isShiftEnabled = isShiftEnabled,
                            onKeyPress = { key ->
                                when(key) {
                                    is Key.Character -> handleLetterKeyPress(key.value)
                                    Key.Shift -> handleShiftPress()
                                    Key.Delete -> handleDelete()
                                    Key.Space -> handleSpace()
                                    else -> { }
                                }
                            },
                            onEmojiClick = { emoji ->
                                handleEmojiSuggestionClick(emoji)
                            },
                            onTextApply = { text ->
                                handleTextApplied(text)
                            }
                        )
                    }
                }
            }
        }

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
        }

        return composeView
    }

    private fun handleLetterKeyPress(letter: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(letter, 1)

        currentInput += letter
    }

    private fun handleDelete() {
        val inputConnection = currentInputConnection ?: return
        inputConnection.deleteSurroundingTextInCodePoints(1,0)

        if (currentInput.isNotEmpty()) {
            currentInput = currentInput.dropLast(1)
        }
    }

    private fun handleShiftPress() {
        isShiftEnabled = !isShiftEnabled
    }

    private fun handleSpace() {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(" ", 1)

        currentInput += " "
    }

    private fun handleEmojiSuggestionClick(emoji: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(" $emoji", 1)
        currentInput += " $emoji"
    }

    private fun handleTextApplied(text: String) {
        val inputConnection = currentInputConnection ?: return
        if (currentInput.isNotEmpty()) {
            inputConnection.deleteSurroundingTextInCodePoints(currentInput.length, 0)
        }

        inputConnection.commitText(text, 1)
        currentInput = text
    }

    private fun updateSuggestions() {
        lifecycleScope.launch {
            emotionDetectorViewModel.detectedEmotion
                .collectLatest { emotion ->
                    emojiSuggestions = SuggestionsProvider.getEmojiFromEmotion(emotion)
                    currentEmotion = emotion
                }
        }
    }


}