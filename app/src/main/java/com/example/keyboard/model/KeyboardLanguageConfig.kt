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
        listOf(Key.Shift) + listOf('z', 'x', 'c', 'v', 'b', 'n', 'm')
            .map { Key.Character(it.toString()) } + listOf(Key.Delete)

    ),
    shiftedRows = listOf(
        listOf('Q', 'W', 'E', 'R', 'T', 'Y', 'U', 'I', 'O', 'P')
            .map { Key.Character(it.toString()) },
        listOf('A', 'S', 'D', 'F', 'G', 'H', 'J', 'K', 'L')
            .map { Key.Character(it.toString()) },
        listOf(Key.Shift) + listOf('Z', 'X', 'C', 'V', 'B', 'N', 'M')
            .map { Key.Character(it.toString()) } + listOf(Key.Delete)
    )
)

val Russian = KeyboardLanguageConfig(
    code = "ru",
    name = "rU",
    rows = listOf(
        listOf('й','ц','у','к','е','н','г','ш','щ','з','х')
            .map { Key.Character(it.toString()) },
        listOf('ф','ы','в','а','п','р','о','л','д','ж','э')
            .map { Key.Character(it.toString()) },
        listOf(Key.Shift) + listOf('я','ч','с','м','и','т','ь','б','ю')
            .map { Key.Character(it.toString()) } + listOf(Key.Delete)

    ),
    shiftedRows = listOf(
        listOf('Й','Ц','У','К','Е','Н','Г','Ш','Щ','З','Х')
            .map { Key.Character(it.toString()) },
        listOf('Ф','Ы','В','А','П','Р','О','Л','Д','Ж','Э')
            .map { Key.Character(it.toString()) },
        listOf(Key.Shift) + listOf('Я','Ч','С','М','И','Т','Ь','Б','Ю')
            .map { Key.Character(it.toString()) } + listOf(Key.Delete)
    )
)

val BottomRowKeys = listOf(
    Key.NumberToggle, Key.LanguageToggle, Key.Space ,Key.Enter
)