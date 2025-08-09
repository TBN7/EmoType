package com.example.keyboard

import com.example.keyboard.model.Emotion

object SuggestionProvider {
    private val emojiSuggestion = mapOf(
        Emotion.HAPPY to listOf("🙂", "😊", "😄", "😆", "🤩"),
        Emotion.SAD to listOf("🙁", "😟", "😢", "😫", "😭"),
        Emotion.SURPRISED to listOf("😯", "😮", "😲", "🤯", "😱")
    )

    fun getEmojiFromEmotion(emotion: Emotion): List<String> =
        emojiSuggestion[emotion] ?: emptyList()
}