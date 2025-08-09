package com.example.keyboard.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.BottomRowKeys
import com.example.keyboard.model.English
import com.example.keyboard.model.Key
import com.example.keyboard.model.KeyboardLanguageConfig
import com.example.keyboard.model.KeyboardLanguageManager

@Composable
fun KeyboardLayout(
    languageManager: KeyboardLanguageManager,
    emojiSuggestions: List<String>,
    onEmojiClick: (String) -> Unit,
    onKeyPress: (Key) -> Unit,
    isShiftEnabled: Boolean
) {
    val currentLanguage = languageManager.currentLanguage
    Column (
        modifier = Modifier.fillMaxWidth()
            .background(color = Color.White)
    ) {

        if (emojiSuggestions.isNotEmpty()) {
            EmojisBar(
                emojis = emojiSuggestions,
                onEmojiClick = onEmojiClick
            )
        }

        AlphabetRows(
            language = currentLanguage,
            onKeyPress = onKeyPress,
            isShiftEnabled = isShiftEnabled
        )
        BottomRow(
            languageManager = languageManager,
            onKeyPress = onKeyPress
        )
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
fun KeyButton(
    modifier: Modifier = Modifier,
    key: Key,
    onKeyPress: (Key) -> Unit
) {
    Box(
        modifier = modifier
            .padding(2.dp)
            .height(48.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.LightGray)
            .clickable {
                onKeyPress(key)
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when(key) {
                is Key.Character -> key.value
                is Key.Shift -> "⇧"
                is Key.Delete -> "⌫"
                is Key.NumberToggle -> "?123"
                is Key.Enter -> "⏎"
                is Key.LanguageToggle -> "\uD83C\uDF10"
                else -> ""
            },
            color = Color.DarkGray,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
fun EmojisBar (
    modifier: Modifier = Modifier,
    emojis: List<String>,
    onEmojiClick: (String) -> Unit
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
    }
}