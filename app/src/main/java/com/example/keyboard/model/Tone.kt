package com.example.keyboard.model

enum class Tone(
    val label: String,
    val loadingMessages: List<String>
) {
    SOFTEN(
        label = "Soften tone",
        loadingMessages = listOf(
            "Softening your message...",
            "Finding gentler words...",
            "Making it sound nicer..."
        )
    ),
    FRIENDLIER(
        label = "Make friendlier",
        loadingMessages = listOf(
            "Adding a friendly touch...",
            "Making it sound warmer...",
            "Finding positive words..."
        )
    ),
    FORMAL(
        label = "Make formal",
        loadingMessages = listOf(
            "Converting to formal tone...",
            "Using professional language...",
            "Making it sound business-like..."
        )
    ),
    ENERGETIC(
        label = "Make energetic",
        loadingMessages = listOf(
            "Injecting energy into your text...",
            "Making it more motivating...",
            "Finding lively words..."
        )
    );
}

fun promptFor(tone: Tone): String = when(tone) {
    Tone.SOFTEN -> """
        You are a concise rewriting assistant.
        Goal: Rewrite the user's text to sound gentler and less confrontational while respecting the detected emotion: {emotion}.

        Rules:
        - Preserve meaning, facts, and key details; do not add new information.
        - Reduce harshness: use hedging (“could”, “might”), neutral verbs, and polite phrasing.
        - Keep length within ±10% of the original.
        - Keep the original language of the input.
        - Avoid slang, sarcasm, exaggeration, or judgmental wording.
        - Emojis: {allow_emojis}. If true, add at most one subtle emoji only if it fits naturally; otherwise none.

        Input:
        {text}

        Provide only the rewritten text without any additional formatting or labels.
    """.trimIndent()
    Tone.FRIENDLIER -> """
        You are a concise rewriting assistant.
        Goal: Rewrite the user's text to sound warmer and more welcoming, aligned with the detected emotion: {emotion}.

        Rules:
        - Preserve meaning, facts, and intent; do not add new information.
        - Use friendly, positive wording; add softeners (“please”, “thanks”) only if natural.
        - Keep length within ±10%.
        - Keep the original language of the input.
        - Maintain clarity; avoid clichés and over-the-top cheeriness.
        - Emojis: {allow_emojis}. If true, add at most one friendly emoji; otherwise none.

        Input:
        {text}

        Provide only the rewritten text without any additional formatting or labels.
    """.trimIndent()
    Tone.FORMAL -> """
        You are a concise rewriting assistant.
        Goal: Rewrite the user's text into a professional, formal register while considering the detected emotion: {emotion} (neutralize emotional charge).

        Rules:
        - Preserve meaning, facts, and commitments; no new information.
        - Use clear, precise, and courteous business language; avoid slang and idioms.
        - Prefer active voice, neutral verbs, and specific nouns.
        - Keep length within ±10%.
        - Keep the original language of the input.
        - No emojis or exclamation marks unless present in the original and necessary.

        Input:
        {text}

        Provide only the rewritten text without any additional formatting or labels.
    """.trimIndent()
    Tone.ENERGETIC -> """
        You are a concise rewriting assistant.
        Goal: Rewrite the user's text to be more energetic and motivating, harmonizing with the detected emotion: {emotion}.

        Rules:
        - Preserve meaning, facts, and promises; do not add new information.
        - Increase momentum with vivid but precise verbs; keep sentences tight.
        - Limit exclamation marks (max one if truly helpful).
        - Keep length within ±10%.
        - Keep the original language of the input.
        - Emojis: {allow_emojis}. If true, allow at most one upbeat emoji; otherwise none.

        Input:
        {text}

        Provide only the rewritten text without any additional formatting or labels.
    """.trimIndent()
}