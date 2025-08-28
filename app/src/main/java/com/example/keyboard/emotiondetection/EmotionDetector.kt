package com.example.keyboard.emotiondetection

import com.example.keyboard.model.Emotion
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import java.util.Locale

import android.util.Log
import com.google.mediapipe.tasks.components.containers.Category


class EmotionDetector {

    fun detectAmotion(result: FaceLandmarkerResult): Emotion {
        if (!result.faceBlendshapes().isPresent || result.faceBlendshapes().get().isEmpty()) {
            return Emotion.NEUTRAL
        }

        val blendshapes = result.faceBlendshapes().get()[0]
        val emotionScores = calculateEmotionScores(blendshapes)

        return getHighestEmotion(emotionScores)
    }

    private fun calculateEmotionScores(
        blendshapes: List<Category>
    ): Map<Emotion, Float> {
        val blendshapeMap = blendshapes.associateBy { it.categoryName() }

        fun getBlendshapeValue(name: String): Float =
            blendshapeMap[name]?.score() ?: 0.0f

        val happinessScore = calculateHappinessScore(::getBlendshapeValue)
        val sadnessScore = calculateSadnessScore(::getBlendshapeValue)
        val surpriseScore = calculateSurpriseScore(::getBlendshapeValue)
        val angerScore = calculateAngerScore(::getBlendshapeValue)

        val maxEmotion = maxOf(happinessScore, sadnessScore, surpriseScore, angerScore)
        val neutralScore = maxOf(0f, 0.8f - (maxEmotion * 2f))

        return mapOf(
            Emotion.HAPPY to happinessScore,
            Emotion.SAD to sadnessScore,
            Emotion.SURPRISED to surpriseScore,
            Emotion.ANGRY to angerScore,
            Emotion.NEUTRAL to neutralScore
        )
    }

    private fun calculateHappinessScore(
        getBlendshape: (String) -> Float
    ): Float {
        val mouthSmileLeft = getBlendshape("mouthSmileLeft")
        val mouthSmileRight = getBlendshape("mouthSmileRight")
        val cheekSquintLeft = getBlendshape("cheekSquintLeft")
        val cheekSquintRight = getBlendshape("cheekSquintRight")
        val eyeSquintLeft = getBlendshape("eyeSquintLeft")
        val eyeSquintRight = getBlendshape("eyeSquintRight")

        return (mouthSmileLeft + mouthSmileRight) * 0.5f +
                (cheekSquintLeft + cheekSquintRight) * 0.3f +
                (eyeSquintLeft + eyeSquintRight) * 0.2f
    }

    private fun calculateSadnessScore(
        getBlendshape: (String) -> Float
    ): Float {
        val mouthFrownLeft = getBlendshape("mouthFrownLeft")
        val mouthFrownRight = getBlendshape("mouthFrownRight")

        val browInnerUp = getBlendshape("browInnerUp")

        val mouthLowerDownLeft = getBlendshape("mouthLowerDownLeft")
        val mouthLowerDownRight = getBlendshape("mouthLowerDownRight")

        val mouthLeft = getBlendshape("mouthLeft")
        val mouthRight = getBlendshape("mouthRight")

        val eyeLookDownLeft = getBlendshape("eyeLookDownLeft")
        val eyeLookDownRight = getBlendshape("eyeLookDownRight")

        val frownIntensity = (mouthFrownLeft + mouthFrownRight) / 2f
        val mouthDownIntensity = (mouthLowerDownLeft + mouthLowerDownRight) / 2f
        val eyeDownwardIntensity = (eyeLookDownLeft + eyeLookDownRight) / 2f
        val mouthIntensity = (mouthLeft + mouthRight) / 2f

        val sadScore = frownIntensity * 0.35f +
                browInnerUp * 0.3f +
                mouthDownIntensity * 0.15f +
                mouthIntensity * 0.15f +
                eyeDownwardIntensity * 0.05f

        return minOf(1f, sadScore * 1.3f)
    }

    private fun calculateSurpriseScore(
        getBlendshape: (String) -> Float
    ): Float {
        val browInnerUp = getBlendshape("browInnerUp")

        val browOuterUpLeft = getBlendshape("browOuterUpLeft")
        val browOuterUpRight = getBlendshape("browOuterUpRight")

        val eyeWideLeft = getBlendshape("eyeWideLeft")
        val eyeWideRight = getBlendshape("eyeWideRight")

        val jawOpen = getBlendshape("jawOpen")

        val browRaise = (browInnerUp + browOuterUpLeft + browOuterUpRight) / 3f
        val eyeWiden = (eyeWideLeft + eyeWideRight) / 2f

        return minOf(1f, browRaise * 0.45f + eyeWiden * 0.3f + jawOpen * 0.15f)
    }

    private fun calculateAngerScore(
        getBlendshape: (String) -> Float
    ): Float {
        val browDownLeft = getBlendshape("browDownLeft")
        val browDownRight = getBlendshape("browDownRight")

        val eyeSquintLeft = getBlendshape("eyeSquintLeft")
        val eyeSquintRight = getBlendshape("eyeSquintRight")

        val mouthPressLeft = getBlendshape("mouthPressLeft")
        val mouthPressRight = getBlendshape("mouthPressRight")

        val noseSneerLeft = getBlendshape("noseSneerLeft")
        val noseSneerRight = getBlendshape("noseSneerRight")

        return (browDownLeft + browDownRight) * 0.35f +
                (eyeSquintLeft + eyeSquintRight) * 0.25f +
                (mouthPressLeft + mouthPressRight) * 0.2f +
                (noseSneerLeft + noseSneerRight) * 0.2f
    }

    private fun getHighestEmotion(emotionScores: Map<Emotion, Float>): Emotion {
        val generalThreshold = 0.25f
        val sadnessThreshold = 0.15f

        val maxEmotion = emotionScores.maxByOrNull { it.value }

        Log.d("taaag", emotionScores.toString())

        return when {
            maxEmotion == null -> Emotion.NEUTRAL
            maxEmotion.key == Emotion.SAD && maxEmotion.value >= sadnessThreshold -> Emotion.SAD
            maxEmotion.value >= generalThreshold -> maxEmotion.key
            else -> Emotion.NEUTRAL
        }
    }
}