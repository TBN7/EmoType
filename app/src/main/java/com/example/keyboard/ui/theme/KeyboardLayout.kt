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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.model.English
import com.example.keyboard.model.Key

@Composable
fun KeyboardLayout(
    onKeyPress: (Key) -> Unit
) {
    Column (
        modifier = Modifier.fillMaxWidth()
            .background(color = Color.White)
    ) {
        AlphabetRows(
            onKeyPress = onKeyPress
        )
    }
}

@Composable
fun AlphabetRows(
    onKeyPress: (Key) -> Unit
) {
    English.rows.forEach { row ->
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
                else -> ""
            },
            color = Color.DarkGray,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}