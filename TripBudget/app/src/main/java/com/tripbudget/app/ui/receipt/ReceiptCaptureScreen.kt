package com.tripbudget.app.ui.receipt

import android.Manifest
import android.content.pm.PackageManager
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat

/**
 * Camera preview + shutter for photographing a receipt. Actual text
 * extraction (merchant, total, date) is intentionally not implemented here —
 * see `ReceiptTextExtractor` below for the seam. The mockup's "auto-capture
 * when steady" behavior also isn't implemented; this scaffold captures on
 * tap only, which is the safer default until that heuristic is tuned.
 */
@Composable
fun ReceiptCaptureScreen(onClose: () -> Unit, onCaptured: (imagePath: String) -> Unit) {
    val context = LocalContext.current
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED,
        )
    }
    val permissionLauncher = androidx.activity.compose.rememberLauncherForActivityResult(
        androidx.activity.result.contract.ActivityResultContracts.RequestPermission(),
    ) { granted -> hasPermission = granted }

    LaunchedEffect(Unit) {
        if (!hasPermission) permissionLauncher.launch(Manifest.permission.CAMERA)
    }

    Box(Modifier.fillMaxSize().background(Color.Black)) {
        if (hasPermission) {
            CameraPreview(onCaptureRequested = { imageCapture ->
                // TODO: wire up ImageCapture.takePicture(...) to a file in
                // context.filesDir, then call ReceiptTextExtractor on it and
                // pass the resulting path (and parsed fields, once that
                // exists) to onCaptured.
                onCaptured("")
            })
        } else {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Camera permission is needed to photograph receipts.", color = Color.White)
            }
        }

        IconButton(onClick = onClose, modifier = Modifier.align(Alignment.TopStart).padding(20.dp)) {
            Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
        }
    }
}

@Composable
private fun CameraPreview(onCaptureRequested: (androidx.camera.core.ImageCapture) -> Unit) {
    val context = LocalContext.current
    val lifecycleOwner = androidx.lifecycle.compose.LocalLifecycleOwner.current
    val imageCapture = remember { androidx.camera.core.ImageCapture.Builder().build() }

    Box(Modifier.fillMaxSize()) {
        AndroidView(
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    runCatching {
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageCapture,
                        )
                    }
                }, ContextCompat.getMainExecutor(ctx))
                previewView
            },
            modifier = Modifier.fillMaxSize(),
        )

        Button(
            onClick = { onCaptureRequested(imageCapture) },
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
        ) {
            Text("Capture", color = Color.Black)
        }
    }
}

/**
 * Seam for on-device receipt OCR. Not implemented in this scaffold —
 * plugging in Google ML Kit Text Recognition (on-device, offline, free)
 * is the natural choice: add `com.google.mlkit:text-recognition` to
 * app/build.gradle.kts, run it on the captured Bitmap, then apply the same
 * `ExpenseParser`-style heuristics to the recognized text to pull out a
 * total and merchant name.
 */
object ReceiptTextExtractor {
    data class ExtractedReceipt(val merchant: String?, val totalMinorUnits: Long?, val rawText: String)

    suspend fun extract(imagePath: String): ExtractedReceipt {
        throw NotImplementedError("Wire up ML Kit Text Recognition here — see class doc.")
    }
}
