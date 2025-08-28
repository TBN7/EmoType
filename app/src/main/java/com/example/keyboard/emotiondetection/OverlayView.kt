package com.example.keyboard.emotiondetection

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.components.containers.NormalizedLandmark
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlin.math.max

class OverlayView(
    context: Context?, attrs: AttributeSet?
) : View(context, attrs) {

    private var results: FaceLandmarkerResult? = null
    private var pointPaint = Paint()
    private var linePaint = Paint()

    private var imageWidth = 1
    private var imageHeight = 1

    private var scaleFactor = 1f

    init {
        initPaint()
    }

    private fun initPaint() {
        pointPaint.color = Color.RED
        pointPaint.strokeWidth = 5f
        pointPaint.style = Paint.Style.FILL

        linePaint.color = Color.MAGENTA
        linePaint.strokeWidth = 5f
        linePaint.style = Paint.Style.FILL
    }

    override fun draw(canvas: Canvas) {
        super.draw(canvas)

        if (results?.faceLandmarks().isNullOrEmpty()) {
            return
        }

        results?.let { landmarkerResult ->
            val scaledImageWidth = imageWidth * scaleFactor
            val scaledImageHeight = imageHeight * scaleFactor

            val offsetX = (width - scaledImageWidth) / 2f
            val offsetY = (height - scaledImageHeight) / 2f

            landmarkerResult.faceLandmarks().forEach { landmarks ->
                drawFaceLandmarks(canvas, landmarks, offsetX, offsetY)
                drawFaceConnectors(canvas, landmarks, offsetX, offsetY)
            }
        }
    }

    private fun drawFaceLandmarks(
        canvas: Canvas,
        faceLandMarks: List<NormalizedLandmark>,
        offsetX: Float,
        offsetY: Float,
    ) {
        faceLandMarks.forEach { landmark ->
            val x = landmark.x() * imageWidth * scaleFactor + offsetX
            val y = landmark.y() * imageHeight * scaleFactor + offsetY
            canvas.drawPoint(x, y, pointPaint)
        }
    }

    private fun drawFaceConnectors(
        canvas: Canvas,
        faceLandMarks: List<NormalizedLandmark>,
        offsetX: Float,
        offsetY: Float,
    ) {
        FaceLandmarker.FACE_LANDMARKS_CONNECTORS.filterNotNull().forEach { connector ->
            val start = faceLandMarks.getOrNull(connector.start())
            val end = faceLandMarks.getOrNull(connector.end())

            if (start != null && end != null) {
                val startX = start.x() * imageWidth * scaleFactor + offsetX
                val startY = start.y() * imageHeight * scaleFactor + offsetY

                val endX = end.x() * imageWidth * scaleFactor + offsetX
                val endY = end.y() * imageHeight * scaleFactor + offsetY

                canvas.drawLine(startX, startY, endX, endY, linePaint)
            }
        }
    }

    fun setResults(
        faceLandmarkerResult: FaceLandmarkerResult,
        input: MPImage
    ) {
        results = faceLandmarkerResult

        imageWidth = input.width
        imageHeight = input.height

        scaleFactor = max(width * 1f / imageWidth, height * 1f / imageHeight)

        invalidate()
    }

    fun clear() {
        results = null
        pointPaint.reset()
        invalidate()
        initPaint()
    }
}