package com.example.keyboard.model

data class KeyboardLanguageConfig (
    val code: String,
    val name: String,
    val rows: List<List<Key>>,
    val shiftedRows: List<List<Key>>
)

val English = KeyboardLanguageConfig(
    code = "en",
    name = "EN",
    rows = listOf(
        listOf('q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p')
            .map { Key.Character(it.toString()) },
        listOf('a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l')
            .map { Key.Character(it.toString()) },
        listOf('z', 'x', 'c', 'v', 'b', 'n', 'm')
            .map { Key.Character(it.toString()) }

    ),
    shiftedRows = listOf(
        listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
            .map { Key.Character(it.toString()) },
        listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L')
            .map { Key.Character(it.toString()) },
        listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M')
            .map { Key.Character(it.toString()) }
    )
)