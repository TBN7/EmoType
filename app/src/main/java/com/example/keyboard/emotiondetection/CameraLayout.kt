package com.example.keyboard.emotiondetection

import android.widget.FrameLayout
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner

@Composable
fun CameraLayout(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = viewModel()
) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(200.dp)
    ) {
        AndroidView(
            factory = { context ->
                FrameLayout(context.apply {
                    val previewView = PreviewView(context)
                    addView(
                        previewView,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )

                    val overlayView = OverlayView(context = context, null)
                    addView(
                        overlayView,
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )

                    viewModel.setuPCamera(previewView, overlayView, context as LifecycleOwner)

                }
                )
            }
        )
    }
}