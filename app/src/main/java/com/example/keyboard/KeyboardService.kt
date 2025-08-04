package com.example.keyboard

import android.inputmethodservice.InputMethodService
import android.view.View
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ServiceLifecycleDispatcher
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.SavedStateRegistry
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.example.keyboard.model.Key
import com.example.keyboard.ui.theme.KeyboardLayout
import com.example.keyboard.ui.theme.KeyboardTheme

class KeyboardService : InputMethodService(), LifecycleOwner, SavedStateRegistryOwner {
    private val dispatcher = ServiceLifecycleDispatcher(this)
    override val lifecycle: Lifecycle
        get() = dispatcher.lifecycle

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry: SavedStateRegistry
        get() = savedStateRegistryController.savedStateRegistry

    override fun onCreate() {
        dispatcher.onServicePreSuperOnCreate()
        super.onCreate()
        savedStateRegistryController.performRestore(null)
    }

    override fun onDestroy() {
        dispatcher.onServicePreSuperOnDestroy()
        super.onDestroy()
    }

    override fun onCreateInputView(): View {
        val composeView = ComposeView(this).apply {
            setContent {
                KeyboardTheme {
                    KeyboardLayout(
                        onKeyPress = { key ->
                            when(key) {
                                is Key.Character -> handleLetterKeyPress(key.value)
                                else -> { }
                            }
                        }
                    )
                }
            }
        }

        window?.window?.decorView?.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }

        return composeView
    }

    private fun handleLetterKeyPress(letter: String) {
        val inputConnection = currentInputConnection ?: return
        inputConnection.commitText(letter, 1)
    }


}