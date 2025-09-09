package com.example.keyboard.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.keyboard.R
import com.example.keyboard.emotionassist.EmotionAssistViewModel
import com.example.keyboard.model.BottomRowKeys
import com.example.keyboard.model.EmojiBottomRowKeys
import com.example.keyboard.model.Emotion
import com.example.keyboard.model.English
import com.example.keyboard.model.Key
import com.example.keyboard.model.KeyboardLanguageConfig
import com.example.keyboard.model.KeyboardLanguageManager
import com.example.keyboard.model.symbolKeys

sealed class KeyboardLayoutType {
    data object Alphabet : KeyboardLayoutType()
    data object Symbol : KeyboardLayoutType()
    data object Emoji : KeyboardLayoutType()
    data object EmotionAssist : KeyboardLayoutType()
}

@Composable
fun KeyboardLayout(
    languageManager: KeyboardLanguageManager,
    currentInput: String,
    currentEmotion: Emotion,
    emotionAssistViewModel: EmotionAssistViewModel = viewModel(),
    emojiSuggestions: List<String>,
    isShiftEnabled: Boolean,
    onKeyPress: (Key) -> Unit,
    onEmojiClick: (String) -> Unit,
    onTextApply: (String) -> Unit
) {
    val currentLanguage = languageManager.currentLanguage
    var currentLayoutType by remember {
        mutableStateOf<KeyboardLayoutType>(KeyboardLayoutType.Alphabet)
    }

    val context = LocalContext.current
    LaunchedEffect(Unit) {
        emotionAssistViewModel.initModel(context)
    }

    Column (
        modifier = Modifier.fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(8.dp)
    ) {

        if (currentLayoutType != KeyboardLayoutType.EmotionAssist) {
            EmojisBar(
                emojis = emojiSuggestions,
                onEmojiClick = onEmojiClick,
                onEmotionAssistClick = {
                    currentLayoutType = KeyboardLayoutType.EmotionAssist
                }
            )
        }

        when (currentLayoutType) {
            is KeyboardLayoutType.Alphabet ->
                AlphabetRows(
                    language = currentLanguage,
                    isShiftEnabled = isShiftEnabled,
                    onKeyPress = onKeyPress
                )
            is KeyboardLayoutType.Emoji ->
                EmojiRows(
                    onKeyPress = onKeyPress
                )
            is KeyboardLayoutType.Symbol ->
                SymbolRows(
                    onKeyPress = onKeyPress
                )
            is KeyboardLayoutType.EmotionAssist ->
                EmotionAssistLayout(
                    currentInput = currentInput,
                    currentEmotion = currentEmotion,
                    emotionAssistViewModel = emotionAssistViewModel,
                    onTextApply = onTextApply,
                    onBackToKeyboardPressed = {
                        currentLayoutType = KeyboardLayoutType.Alphabet
                    }
                )
        }

        Spacer(modifier = Modifier.height(4.dp))

        when(currentLayoutType) {
            is KeyboardLayoutType.Alphabet, KeyboardLayoutType.Symbol ->
                BottomRow(
                    languageManager = languageManager,
                    currentKeyboardLayoutType = currentLayoutType,
                    onKeyPress = { key ->
                        when(key) {
                            is Key.NumberToggle -> currentLayoutType = if (currentLayoutType is KeyboardLayoutType.Alphabet)
                                KeyboardLayoutType.Symbol
                            else
                                KeyboardLayoutType.Alphabet

                            is Key.EmojiToggle -> currentLayoutType = if (currentLayoutType is KeyboardLayoutType.Alphabet)
                                KeyboardLayoutType.Emoji
                            else
                                KeyboardLayoutType.Alphabet

                            else -> onKeyPress(key)
                        }
                    }
                )
            is KeyboardLayoutType.Emoji ->
                EmojiBottomRow(
                    currentKeyboardLayoutType = currentLayoutType,
                    onKeyPress = { key ->
                        when (key) {
                            is Key.NumberToggle -> currentLayoutType = if (currentLayoutType is KeyboardLayoutType.Alphabet)
                                KeyboardLayoutType.Symbol
                            else
                                KeyboardLayoutType.Alphabet

                            else -> onKeyPress(key)
                        }
                    }
                )
            else -> { }
        }

    }
}

@Composable
fun AlphabetRows(
    language: KeyboardLanguageConfig,
    isShiftEnabled: Boolean,
    onKeyPress: (Key) -> Unit
) {
    val rowsToUse = if (isShiftEnabled) language.shiftedRows else language.rows

    rowsToUse.forEach { row ->
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row.forEach { key ->
                KeyButton(
                    modifier = Modifier.weight(1f),
                    key = key,
                    isShiftEnabled = isShiftEnabled,
                    onKeyPress = onKeyPress
                )
            }
        }
    }
}

@Composable
fun SymbolRows(
    onKeyPress: (Key) -> Unit
) {
    symbolKeys.forEach { row ->
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            row.forEach { key ->
                KeyButton(
                    modifier = Modifier.weight(1f),
                    key = key,
                    onKeyPress = onKeyPress
                )
            }
        }
    }
}

@Composable
fun BottomRow (
    languageManager: KeyboardLanguageManager,
    currentKeyboardLayoutType: KeyboardLayoutType,
    onKeyPress: (Key) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        BottomRowKeys.forEach { key ->
            KeyButton(
                modifier = Modifier.weight(
                    if (key is Key.Space) 4f else 1f
                ),
                key = key,
                currentKeyboardLayoutType = currentKeyboardLayoutType,
                onKeyPress = {
                    if (it is Key.LanguageToggle) {
                        languageManager.switchToNextLanguage()
                    } else {
                        onKeyPress(it)
                    }
                }
            )
        }
    }
}

@Composable
fun EmojiBottomRow (
    currentKeyboardLayoutType: KeyboardLayoutType,
    onKeyPress: (Key) -> Unit
) {
    Row (
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        EmojiBottomRowKeys.forEach { key ->
            KeyButton(
                modifier = Modifier.weight(
                    if (key is Key.Space) 4f else 1f
                ),
                key = key,
                currentKeyboardLayoutType = currentKeyboardLayoutType,
                onKeyPress = onKeyPress
            )
        }
    }
}

@Composable
fun EmojisBar (
    modifier: Modifier = Modifier,
    emojis: List<String>,
    onEmojiClick: (String) -> Unit,
    onEmotionAssistClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        emojis.forEach { emoji ->
            IconButton(
                modifier = modifier
                    .size(48.dp)
                    .padding(8.dp),
                onClick = { onEmojiClick(emoji) }
            ) {
                Text(
                    text = emoji,
                    fontSize = 28.sp
                )
            }
        }
        IconButton(
            modifier = modifier
                .size(48.dp)
                .padding(8.dp),
            onClick = onEmotionAssistClick
        ) {
            Image(painter = painterResource(R.drawable.magic_wand), "")
        }
    }
}

@Composable
fun EmojiRows(
    modifier: Modifier = Modifier,
    onKeyPress: (Key) -> Unit
) {
    Column(modifier = modifier) {
        AndroidView(
            modifier = Modifier.fillMaxWidth()
                .height(300.dp),
            factory = { context ->
                EmojiPickerView(context).apply {
                    setOnEmojiPickedListener { emoji ->
                        onKeyPress(Key.Character(emoji.emoji))
                    }
                }
            }
        )
    }
}