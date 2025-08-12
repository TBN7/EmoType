package com.example.keyboard.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.R
import com.example.keyboard.model.Key

@Composable
fun KeyButton(
    modifier: Modifier = Modifier,
    key: Key,
    currentKeyboardLayoutType: KeyboardLayoutType = KeyboardLayoutType.Alphabet,
    isShiftEnabled: Boolean = false,
    onKeyPress: (Key) -> Unit
) {
    Card (
        modifier = modifier
            .padding(2.dp)
            .height(48.dp),
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.cardColors(
            contentColor = when(key) {
                is Key.Character, Key.Space, Key.EmojiToggle -> MaterialTheme.colorScheme.surface
                else -> Lavender
            }
        ),
        onClick = {
            onKeyPress(key)
        }
    ) {
        Box (
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            when(key) {
                is Key.Character ->
                    Text(
                        text = key.value,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Normal
                    )
                is Key.NumberToggle ->
                    Text(
                        text = if (currentKeyboardLayoutType is KeyboardLayoutType.Symbol)
                            "?123"
                        else
                            "ABC",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Normal
                    )
                is Key.Enter ->
                    Image(painter = painterResource(R.drawable.ic_enter), "")
                is Key.LanguageToggle ->
                    Image(painter = painterResource(R.drawable.ic_globe), "")
                is Key.Shift ->
                    if (isShiftEnabled) Image(painter = painterResource(R.drawable.ic_emoji),"")
                    else Image(painter = painterResource(R.drawable.ic_shift),"")
                is Key.Delete ->
                    Image(painter = painterResource(R.drawable.ic_delete),"")
                is Key.EmojiToggle ->
                    Image(painter = painterResource(R.drawable.ic_emoji),"")
                else -> {  }
            }
        }
    }
}