package com.example.keyboard.emotiondetection

import android.graphics.Bitmap
import android.os.SystemClock
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.utils.MatrixExt.postRotate
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.ui.graphics.Matrix
import androidx.core.graphics.createBitmap
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.mediapipe.framework.image.BitmapImageBuilder
import com.google.mediapipe.framework.image.MPImage
import com.google.mediapipe.tasks.core.BaseOptions
import com.google.mediapipe.tasks.core.Delegate
import com.google.mediapipe.tasks.vision.core.RunningMode
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarker
import com.google.mediapipe.tasks.vision.facelandmarker.FaceLandmarkerResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

class CameraViewModel: ViewModel() {
    private var cameraProvider: ProcessCameraProvider? = null
    private var faceLandMarker: FaceLandmarker? = null

    private var backgroundExecutor = Executors.newSingleThreadExecutor()

    fun setupCamera(
        previewView: PreviewView,
        overlayView: OverlayView,
        lifecycleOwner: LifecycleOwner
    ) {
        viewModelScope.launch {
            cameraProvider =
                ProcessCameraProvider.getInstance(previewView.context).get()

            withContext(Dispatchers.IO) {
                setupFaceLandmarker(overlayView)
            }

            bindCamera(previewView,lifecycleOwner)
        }
    }

    private fun setupFaceLandmarker(
        overlayView: OverlayView
    ) {
        try {
            faceLandMarker?.close()

            val baseOptionalBuilder = BaseOptions.builder()
                .setDelegate(Delegate.GPU)
                .setModelAssetPath("face_landmarker.task")

            val optionsBuilder = FaceLandmarker.FaceLandmarkerOptions.builder()
                .setBaseOptions(baseOptionalBuilder.build())
                .setMinFaceDetectionConfidence(0.5f)
                .setMinTrackingConfidence(0.5f)
                .setMinFacePresenceConfidence(0.5f)
                .setOutputFaceBlendshapes(true)
                .setNumFaces(1)
                .setRunningMode(RunningMode.LIVE_STREAM)
                .setResultListener { result, input ->
                    handleResult(result, input, overlayView)
                }

            val options = optionsBuilder.build()
            faceLandMarker = FaceLandmarker.createFromOptions(
                overlayView.context, options
            )
        } catch (e: Exception) {

        }
    }

    private fun bindCamera(
        previewView: PreviewView,
        lifecycleOwner: LifecycleOwner
    ) {
        val cameraProvider = cameraProvider ?: return

        val preview = Preview.Builder().build()
        preview.surfaceProvider = previewView.surfaceProvider

        val imageAnalysis = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .build()

        imageAnalysis.setAnalyzer(backgroundExecutor) { imageProxy ->
            detectFace(imageProxy)
        }

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
            .build()

        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageAnalysis
        )
    }

    private fun detectFace(
        imageProxy: ImageProxy
    ) {
        val frameTime = SystemClock.uptimeMillis()
        val bitmapBuffer = createBitmap(
            imageProxy.width, imageProxy.height
        )
        imageProxy.use {
            bitmapBuffer.copyPixelsFromBuffer(imageProxy.planes[0].buffer)
        }

        val matrix = Matrix().apply {
            postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            postScale(-1f,1f,imageProxy.width.toFloat(),imageProxy.height.toFloat())
        }

        val rotatedBitmap = Bitmap.createBitmap(
            bitmapBuffer, 0, 0, bitmapBuffer.width, bitmapBuffer.height,
            matrix, true
        )

        faceLandMarker?.detectAsync(BitmapImageBuilder(rotatedBitmap).build(),frameTime)

        imageProxy.close()
    }

    private fun handleResult(
        result: FaceLandmarkerResult,
        input: MPImage,
        overlayView: OverlayView
    ) {
        if (result.faceLandmarks().isEmpty()) {
            overlayView.clear()
            return
        }

        viewModelScope.launch {
            overlayView.setResults(result, input)
            overlayView.invalidate()
        }
    }
}