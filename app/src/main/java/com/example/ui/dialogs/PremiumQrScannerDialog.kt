package com.example.ui.dialogs

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FlashOff
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ui.theme.EmeraldDark
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.EmeraldLight
import com.example.ui.theme.ObsidianBlack
import com.example.ui.theme.ObsidianBorder
import com.example.ui.theme.ObsidianCard
import com.example.ui.theme.ObsidianCardHighlight
import com.example.ui.theme.ObsidianDark
import com.example.ui.theme.PureBlack
import com.example.ui.theme.PureWhite
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.google.zxing.BarcodeFormat
import com.google.zxing.BinaryBitmap
import com.google.zxing.DecodeHintType
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import org.json.JSONObject
import java.util.concurrent.Executors
import kotlin.math.roundToInt

data class ScannedWebhookConfig(
    val name: String,
    val url: String,
    val token: String,
    val senderFilter: String,
    val simSlot: Int
)

@Composable
fun PremiumQrScannerDialog(
    onDismiss: () -> Unit,
    onConfigScanned: (ScannedWebhookConfig) -> Unit
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        hasCameraPermission = granted
        if (!granted) {
            Toast.makeText(context, "Camera permission needed for live scanning", Toast.LENGTH_SHORT).show()
        }
    }

    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    var isFlashlightOn by remember { mutableStateOf(false) }
    var selectedMode by remember { mutableStateOf("viewfinder") } // "viewfinder" or "manual"
    var manualInputText by remember { mutableStateOf("") }
    var detectedSuccess by remember { mutableStateOf(false) }

    // Laser sweep animation
    val infiniteTransition = rememberInfiniteTransition(label = "LaserSweep")
    val laserYPercent by infiniteTransition.animateFloat(
        initialValue = 0.08f,
        targetValue = 0.92f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "LaserPosition"
    )

    fun handleScannedText(rawText: String) {
        if (detectedSuccess) return
        val trimmed = rawText.trim()
        try {
            if (trimmed.startsWith("{") && trimmed.endsWith("}")) {
                val obj = JSONObject(trimmed)
                val name = obj.optString("name", "Scanned Webhook")
                val url = obj.optString("url", "")
                val token = obj.optString("token", "")
                val senderFilter = obj.optString("senderFilter", "")
                val simSlot = obj.optInt("simSlot", 0)

                if (url.isBlank()) {
                    Toast.makeText(context, "QR is missing a webhook URL", Toast.LENGTH_SHORT).show()
                    return
                }

                detectedSuccess = true
                Toast.makeText(context, "QR Verified: $name", Toast.LENGTH_SHORT).show()
                onConfigScanned(
                    ScannedWebhookConfig(
                        name = name,
                        url = url,
                        token = token,
                        senderFilter = senderFilter,
                        simSlot = simSlot
                    )
                )
            } else if (trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) || trimmed.contains(".")) {
                detectedSuccess = true
                val finalUrl = when {
                    trimmed.startsWith("http://", ignoreCase = true) || trimmed.startsWith("https://", ignoreCase = true) -> trimmed
                    else -> "https://$trimmed"
                }
                Toast.makeText(context, "Webhook URL Detected!", Toast.LENGTH_SHORT).show()
                onConfigScanned(
                    ScannedWebhookConfig(
                        name = "Scanned Endpoint",
                        url = finalUrl,
                        token = "",
                        senderFilter = "",
                        simSlot = 0
                    )
                )
            } else {
                Toast.makeText(context, "Unrecognized QR content", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Error reading QR: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    // Photo picker launcher for scanning from saved QR images
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                if (bitmap != null) {
                    val width = bitmap.width
                    val height = bitmap.height
                    val pixels = IntArray(width * height)
                    bitmap.getPixels(pixels, 0, width, 0, 0, width, height)
                    val source = RGBLuminanceSource(width, height, pixels)
                    val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                    val reader = MultiFormatReader().apply {
                        setHints(
                            mapOf(
                                DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                                DecodeHintType.TRY_HARDER to true
                            )
                        )
                    }
                    val result = reader.decodeWithState(binaryBitmap)
                    if (result != null && result.text.isNotBlank()) {
                        handleScannedText(result.text)
                    } else {
                        Toast.makeText(context, "No QR Code found in selected image", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Could not decode QR from image: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = PureBlack
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                // Top Header Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianBorder, CircleShape)
                            .clickable(onClick = onDismiss)
                            .testTag("scanner_close_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(9999.dp))
                            .background(EmeraldLight)
                            .border(1.dp, EmeraldGreen.copy(alpha = 0.4f), RoundedCornerShape(9999.dp))
                            .padding(horizontal = 12.dp, vertical = 5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(EmeraldGreen)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (hasCameraPermission) "LIVE SCANNER" else "CAMERA STANDBY",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = EmeraldDark
                        )
                    }

                    // Flashlight Toggle
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(if (isFlashlightOn) EmeraldLight else ObsidianCard)
                            .border(1.dp, if (isFlashlightOn) EmeraldGreen else ObsidianBorder, CircleShape)
                            .clickable {
                                isFlashlightOn = !isFlashlightOn
                                cameraControl?.enableTorch(isFlashlightOn)
                            }
                            .testTag("scanner_flashlight_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isFlashlightOn) Icons.Default.FlashOn else Icons.Default.FlashOff,
                            contentDescription = "Torch",
                            tint = if (isFlashlightOn) EmeraldDark else TextPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title Section
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Scan Webhook QR",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = PureWhite
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Point camera at webhook QR code from your website or pick an image",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Mode Selector: Viewfinder vs Manual JSON
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(9999.dp))
                        .background(ObsidianCard)
                        .border(1.dp, ObsidianBorder, RoundedCornerShape(9999.dp))
                        .padding(4.dp)
                ) {
                    val isViewfinder = selectedMode == "viewfinder"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(if (isViewfinder) ObsidianCardHighlight else Color.Transparent)
                            .clickable { selectedMode = "viewfinder" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = null,
                                tint = if (isViewfinder) EmeraldGreen else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Camera Viewfinder",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (isViewfinder) TextPrimary else TextSecondary
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(9999.dp))
                            .background(if (!isViewfinder) ObsidianCardHighlight else Color.Transparent)
                            .clickable { selectedMode = "manual" }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = if (!isViewfinder) EmeraldGreen else TextSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Manual / Paste",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = if (!isViewfinder) TextPrimary else TextSecondary
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                if (selectedMode == "viewfinder") {
                    // Futuristic Cyber Viewfinder Box with Live Camera Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF0F172A))
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(20.dp))
                            .clipToBounds(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (hasCameraPermission) {
                            AndroidView(
                                factory = { ctx ->
                                    val previewView = PreviewView(ctx).apply {
                                        implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                                    }
                                    val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                                    val cameraExecutor = Executors.newSingleThreadExecutor()

                                    cameraProviderFuture.addListener({
                                        try {
                                            val cameraProvider = cameraProviderFuture.get()
                                            val preview = Preview.Builder().build().also {
                                                it.setSurfaceProvider(previewView.surfaceProvider)
                                            }

                                            val imageAnalysis = ImageAnalysis.Builder()
                                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                                .build()

                                            val multiFormatReader = MultiFormatReader().apply {
                                                setHints(
                                                    mapOf(
                                                        DecodeHintType.POSSIBLE_FORMATS to listOf(BarcodeFormat.QR_CODE),
                                                        DecodeHintType.TRY_HARDER to true,
                                                        DecodeHintType.CHARACTER_SET to "UTF-8"
                                                    )
                                                )
                                            }

                                            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                                                if (!detectedSuccess) {
                                                    try {
                                                        val plane = imageProxy.planes[0]
                                                        val buffer = plane.buffer
                                                        val rowStride = plane.rowStride
                                                        val width = imageProxy.width
                                                        val height = imageProxy.height

                                                        val yBytes: ByteArray
                                                        if (rowStride == width) {
                                                            yBytes = ByteArray(buffer.remaining())
                                                            buffer.get(yBytes)
                                                        } else {
                                                            yBytes = ByteArray(width * height)
                                                            val rowData = ByteArray(rowStride)
                                                            for (row in 0 until height) {
                                                                val bytesToRead = minOf(rowStride, buffer.remaining())
                                                                buffer.get(rowData, 0, bytesToRead)
                                                                System.arraycopy(rowData, 0, yBytes, row * width, width)
                                                            }
                                                        }

                                                        // Handle rotation
                                                        val rotation = imageProxy.imageInfo.rotationDegrees
                                                        val rotatedBytes: ByteArray
                                                        val finalWidth: Int
                                                        val finalHeight: Int

                                                        if (rotation == 90) {
                                                            rotatedBytes = ByteArray(yBytes.size)
                                                            for (y in 0 until height) {
                                                                for (x in 0 until width) {
                                                                    rotatedBytes[x * height + (height - y - 1)] = yBytes[y * width + x]
                                                                }
                                                            }
                                                            finalWidth = height
                                                            finalHeight = width
                                                        } else if (rotation == 270) {
                                                            rotatedBytes = ByteArray(yBytes.size)
                                                            for (y in 0 until height) {
                                                                for (x in 0 until width) {
                                                                    rotatedBytes[(width - x - 1) * height + y] = yBytes[y * width + x]
                                                                }
                                                            }
                                                            finalWidth = height
                                                            finalHeight = width
                                                        } else {
                                                            rotatedBytes = yBytes
                                                            finalWidth = width
                                                            finalHeight = height
                                                        }

                                                        val source = PlanarYUVLuminanceSource(
                                                            rotatedBytes, finalWidth, finalHeight, 0, 0, finalWidth, finalHeight, false
                                                        )
                                                        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))
                                                        val result = multiFormatReader.decodeWithState(binaryBitmap)

                                                        if (result != null && result.text.isNotBlank()) {
                                                            previewView.post {
                                                                handleScannedText(result.text)
                                                            }
                                                        }
                                                    } catch (_: NotFoundException) {
                                                        // Scanning frames
                                                    } catch (e: Exception) {
                                                        Log.e("WisfyScanner", "Decode error: ${e.message}")
                                                    } finally {
                                                        imageProxy.close()
                                                    }
                                                } else {
                                                    imageProxy.close()
                                                }
                                            }

                                            val hasBack = cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA)
                                            val cameraSelector = if (hasBack) CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

                                            cameraProvider.unbindAll()
                                            val cam = cameraProvider.bindToLifecycle(
                                                lifecycleOwner,
                                                cameraSelector,
                                                preview,
                                                imageAnalysis
                                            )
                                            cameraControl = cam.cameraControl
                                        } catch (exc: Exception) {
                                            Log.e("WisfyScanner", "Camera bind failed: ${exc.message}")
                                        }
                                    }, ContextCompat.getMainExecutor(ctx))

                                    previewView
                                },
                                modifier = Modifier.fillMaxSize()
                            )
                        } else {
                            // Camera Permission Missing UI Prompt
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(44.dp)
                                )
                                Spacer(modifier = Modifier.height(10.dp))
                                Text(
                                    text = "Camera Permission Required",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PureWhite
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Grant camera permission to point your device at your web pairing QR code.",
                                    fontSize = 12.sp,
                                    color = TextSecondary,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                                Button(
                                    onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = EmeraldGreen,
                                        contentColor = PureBlack
                                    ),
                                    shape = RoundedCornerShape(9999.dp)
                                ) {
                                    Text("Grant Camera Access", fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Ambient Cyber Grid / Torch Effect
                        if (isFlashlightOn) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.radialGradient(
                                            colors = listOf(Color(0x3310B981), Color.Transparent)
                                        )
                                    )
                            )
                        }

                        // Reticle Center Box (220dp x 220dp)
                        Box(
                            modifier = Modifier
                                .size(220.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(1.dp, Color(0x4410B981), RoundedCornerShape(16.dp))
                        ) {
                            // 4 Glowing Corner Reticles
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val strokeW = 4.dp.toPx()
                                val cornerL = 26.dp.toPx()
                                val cornerColor = if (detectedSuccess) Color(0xFF34D399) else Color(0xFF10B981)

                                // Top-Left
                                drawLine(cornerColor, Offset(0f, 0f), Offset(cornerL, 0f), strokeWidth = strokeW)
                                drawLine(cornerColor, Offset(0f, 0f), Offset(0f, cornerL), strokeWidth = strokeW)

                                // Top-Right
                                drawLine(cornerColor, Offset(size.width, 0f), Offset(size.width - cornerL, 0f), strokeWidth = strokeW)
                                drawLine(cornerColor, Offset(size.width, 0f), Offset(size.width, cornerL), strokeWidth = strokeW)

                                // Bottom-Left
                                drawLine(cornerColor, Offset(0f, size.height), Offset(cornerL, size.height), strokeWidth = strokeW)
                                drawLine(cornerColor, Offset(0f, size.height), Offset(0f, size.height - cornerL), strokeWidth = strokeW)

                                // Bottom-Right
                                drawLine(cornerColor, Offset(size.width, size.height), Offset(size.width - cornerL, size.height), strokeWidth = strokeW)
                                drawLine(cornerColor, Offset(size.width, size.height), Offset(size.width, size.height - cornerL), strokeWidth = strokeW)
                            }

                            // Moving Laser Beam Animation
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(3.dp)
                                    .offset { IntOffset(0, (laserYPercent * 220.dp.toPx()).roundToInt()) }
                                    .background(
                                        Brush.horizontalGradient(
                                            listOf(
                                                Color.Transparent,
                                                EmeraldDark,
                                                EmeraldGreen,
                                                EmeraldDark,
                                                Color.Transparent
                                            )
                                        )
                                    )
                            )

                            if (!hasCameraPermission) {
                                Icon(
                                    imageVector = Icons.Default.QrCode,
                                    contentDescription = null,
                                    tint = Color(0x2210B981),
                                    modifier = Modifier
                                        .size(100.dp)
                                        .align(Alignment.Center)
                                )
                            }
                        }

                        // Subtitle overlay
                        Text(
                            text = if (hasCameraPermission) "ALIGN QR CODE WITHIN BOUNDS" else "CAMERA ACCESS STANDBY",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = PureWhite.copy(alpha = 0.85f),
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Scan from Gallery Button
                    OutlinedButton(
                        onClick = {
                            photoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("scan_from_gallery_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = ObsidianCard,
                            contentColor = TextPrimary
                        ),
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(ObsidianBorder)
                        )
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Image,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Scan QR from Image / Gallery",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 1-Tap Quick Sample Payloads
                    Text(
                        text = "INSTANT TEST PAIRING PRESETS",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    PresetCard(
                        title = "Production WooCommerce Store",
                        url = "https://store.wisfy-merchant.com/api/v1/sms",
                        sim = "Both SIMs",
                        onClick = {
                            handleScannedText(
                                """
                                {
                                  "name": "Production WooCommerce Store",
                                  "url": "https://store.wisfy-merchant.com/api/v1/sms",
                                  "token": "tok_live_wisfy_e9124019ab",
                                  "senderFilter": "bKash, NAGAD, Rocket, Upay",
                                  "simSlot": 0
                                }
                                """.trimIndent()
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    PresetCard(
                        title = "Webhook.site Public Inspector",
                        url = "https://webhook.site/wisfy-sample-probe",
                        sim = "SIM 1 Only",
                        onClick = {
                            handleScannedText(
                                """
                                {
                                  "name": "Webhook.site Public Inspector",
                                  "url": "https://webhook.site/wisfy-sample-probe",
                                  "token": "",
                                  "senderFilter": "",
                                  "simSlot": 1
                                }
                                """.trimIndent()
                            )
                        }
                    )
                } else {
                    // Manual JSON / URL Payload Mode
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(ObsidianCard)
                            .border(1.dp, ObsidianBorder, RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Paste Webhook URL or JSON Config",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Paste a raw webhook URL or full pairing JSON block",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        OutlinedTextField(
                            value = manualInputText,
                            onValueChange = { manualInputText = it },
                            placeholder = {
                                Text(
                                    text = "https://webhook.site/...\nor\n{\n  \"name\": \"Store API\",\n  \"url\": \"https://...\"\n}",
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = TextSecondary
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .testTag("manual_qr_input"),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = ObsidianDark,
                                unfocusedContainerColor = ObsidianDark,
                                focusedBorderColor = EmeraldGreen,
                                unfocusedBorderColor = ObsidianBorder,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = { handleScannedText(manualInputText) },
                            enabled = manualInputText.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("import_manual_qr_button"),
                            shape = RoundedCornerShape(9999.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = EmeraldGreen,
                                contentColor = PureBlack
                            )
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Import Webhook Config", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PresetCard(
    title: String,
    url: String,
    sim: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(ObsidianCard)
            .border(1.dp, ObsidianBorder, RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = url,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Config: $sim",
                    fontSize = 10.sp,
                    color = EmeraldDark,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(9999.dp))
                    .background(EmeraldLight)
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "1-Tap Pair",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldDark
                )
            }
        }
    }
}
