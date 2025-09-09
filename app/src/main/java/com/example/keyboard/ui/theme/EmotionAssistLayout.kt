package com.example.keyboard.ui.theme

import android.graphics.drawable.Icon
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.keyboard.emotionassist.EmotionAssistViewModel
import com.example.keyboard.model.Emotion
import com.example.keyboard.model.Tone
import com.example.keyboard.model.promptFor

@Composable
fun EmotionAssistLayout(
    modifier: Modifier = Modifier,
    currentInput: String,
    currentEmotion: Emotion,
    emotionAssistViewModel: EmotionAssistViewModel,
    onTextApply: (String) -> Unit,
    onBackToKeyboardPressed: () -> Unit
) {
    var selectedTone by remember { mutableStateOf<Tone?>(null) }
    var loadingMessage by remember { mutableStateOf("") }

    val isLoading by emotionAssistViewModel.isLoading.collectAsState()
    val response by emotionAssistViewModel.response.collectAsState()

    LaunchedEffect(selectedTone) {
        if (selectedTone != null) {
            loadingMessage = selectedTone?.loadingMessages?.random() ?: "Loading..."
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Emotion Assist",
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        ToneButtonsGrid(
            onToneSelected = { tone ->
                selectedTone = tone
                val prompt = promptFor(tone)
                    .replace("{text}", currentInput)
                    .replace("{emotion}", currentEmotion.name)
                    .replace("{allow_emojis}", "true")

                emotionAssistViewModel.generateResponse(prompt)
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            modifier = Modifier.align(Alignment.Start),
            text = "Your text",
            fontSize = 14.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier
                .height(96.dp)
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        ) {
            Text(
                modifier = Modifier.padding(8.dp),
                text = when {
                    isLoading && selectedTone != null -> loadingMessage
                    response.isNotEmpty() && selectedTone != null -> response
                    else -> currentInput
                }
            )
        }

        if (response.isNotEmpty() && selectedTone != null && !isLoading) {
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Card(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    onClick = {
                        onTextApply(response)
                        onBackToKeyboardPressed()
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Check, ""
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Card(
                    modifier = Modifier.size(36.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    onClick = {
                        selectedTone = null
                    }
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Clear, ""
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 1.dp),
            onClick = { onBackToKeyboardPressed() }
        ) {
            Text(
                text = "Back to keyboard",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@Composable
fun ToneButtonsGrid(
    onToneSelected: (Tone) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Tone.entries.chunked(2).forEach { rowTones ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowTones.forEach { tone ->
                    Card(
                        modifier = Modifier
                            .height(48.dp)
                            .weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        onClick = {
                            onToneSelected(tone)
                        }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxSize()
                                .padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = tone.label,
                                modifier = Modifier.padding(horizontal = 4.dp),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}