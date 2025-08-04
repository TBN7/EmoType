package com.example.keyboard.model

sealed class Key {
    data class Character(val value: String): Key()
    data object Shift: Key()
    data object Space: Key()
    data object Delete: Key()
    data object Enter: Key()
    data object LanguageToggle: Key()
    data object NumberToggle: Key()
    data object EmojiToggle: Key()
}