package com.yueproject.example

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInParent
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.yueproject.example.ui.theme.Android_ExampleTheme
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import kotlin.math.abs
import androidx.exifinterface.media.ExifInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 裁剪图片的Activity
 * 需要传入图片的 uri 路径
 */
class CropActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val inputUri = intent.getParcelableExtra<Uri>(EXTRA_INPUT_URI)
        if (inputUri == null) {
            Toast.makeText(this, "图片路径错误", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        setContent {
            Android_ExampleTheme {
                CropScreen(
                    inputUri = inputUri,
                    onCropSuccess = { croppedUri ->
                        val resultIntent = Intent().apply {
                            putExtra(EXTRA_OUTPUT_URI, croppedUri)
                        }
                        setResult(RESULT_OK, resultIntent)
                        finish()
                    },
                    onCancel = {
                        setResult(RESULT_CANCELED)
                        finish()
                    }
                )
            }
        }
    }

    companion object {
        const val EXTRA_INPUT_URI = "extra_input_uri"
        const val EXTRA_OUTPUT_URI = "extra_output_uri"

        fun createIntent(context: Context, inputUri: Uri): Intent {
            return Intent(context, CropActivity::class.java).apply {
                putExtra(EXTRA_INPUT_URI, inputUri)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
        }
    }
}

// 拖拽手势触控区域枚举
private enum class TouchArea {
    NONE, CENTER, TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT, TOP, BOTTOM, LEFT, RIGHT
}

@Composable
fun CropScreen(
    inputUri: Uri,
    onCropSuccess: (Uri) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val density = LocalDensity.current

    // 1. 状态管理
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    // 裁剪框 (屏幕 View 坐标)
    var cropRect by remember { mutableStateOf(Rect.Zero) }
    var currentTouchArea by remember { mutableStateOf(TouchArea.NONE) }

    val handleSizePx = with(density) { 32.dp.toPx() }
    val minRectSizePx = with(density) { 60.dp.toPx() }

    // 加载原始高分辨率 Bitmap 并自动校正 EXIF 旋转角度
    LaunchedEffect(inputUri) {
        withContext(Dispatchers.IO) {
            try {
                // 1. 读取图片的 EXIF 旋转角度
                var rotationDegrees = 0
                context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                    val exifInterface = ExifInterface(inputStream)
                    val orientation = exifInterface.getAttributeInt(
                        ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_NORMAL
                    )
                    rotationDegrees = when (orientation) {
                        ExifInterface.ORIENTATION_ROTATE_90 -> 90
                        ExifInterface.ORIENTATION_ROTATE_180 -> 180
                        ExifInterface.ORIENTATION_ROTATE_270 -> 270
                        else -> 0
                    }
                }

                // 2. 解码得到原始 Bitmap
                var rawBitmap: Bitmap? = null
                context.contentResolver.openInputStream(inputUri)?.use { inputStream ->
                    rawBitmap = BitmapFactory.decodeStream(inputStream)
                }

                // 3. 校正方向
                if (rawBitmap != null) {
                    if (rotationDegrees != 0) {
                        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
                        bitmap = Bitmap.createBitmap(
                            rawBitmap!!,
                            0,
                            0,
                            rawBitmap!!.width,
                            rawBitmap!!.height,
                            matrix,
                            true
                        )
                        rawBitmap!!.recycle()
                    } else {
                        bitmap = rawBitmap
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    // 计算图片在容器中按 ContentScale.Fit 适配后的实际绘制区域 Rect
    val displayedImageRect: Rect? = remember(bitmap, containerSize) {
        val bm = bitmap ?: return@remember null
        if (containerSize.width == 0 || containerSize.height == 0) return@remember null

        val containerWidth = containerSize.width.toFloat()
        val containerHeight = containerSize.height.toFloat()
        val containerAspect = containerWidth / containerHeight
        val bitmapAspect = bm.width.toFloat() / bm.height

        val drawWidth: Float
        val drawHeight: Float

        if (bitmapAspect > containerAspect) {
            drawWidth = containerWidth
            drawHeight = containerWidth / bitmapAspect
        } else {
            drawHeight = containerHeight
            drawWidth = containerHeight * bitmapAspect
        }

        val left = (containerWidth - drawWidth) / 2f
        val top = (containerHeight - drawHeight) / 2f
        Rect(left, top, left + drawWidth, top + drawHeight)
    }

    // 当获取到正确的图片绘制区域时，初始化裁剪框为图片区域内缩 10%
    LaunchedEffect(displayedImageRect) {
        if (displayedImageRect != null && cropRect == Rect.Zero) {
            val insetX = displayedImageRect.width * 0.1f
            val insetY = displayedImageRect.height * 0.1f
            cropRect = Rect(
                left = displayedImageRect.left + insetX,
                top = displayedImageRect.top + insetY,
                right = displayedImageRect.right - insetX,
                bottom = displayedImageRect.bottom - insetY
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        val loadedBitmap = bitmap
        if (loadedBitmap != null) {
            // 2. 图片展示与容器大小监听
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        containerSize = coordinates.size
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    bitmap = loadedBitmap.asImageBitmap(),
                    contentDescription = "Crop Target",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )

                // 3. 裁剪蒙版与手势绘制交互层
                if (displayedImageRect != null && cropRect != Rect.Zero) {
                    Canvas(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(displayedImageRect) {
                                detectDragGestures(
                                    onDragStart = { offset ->
                                        currentTouchArea = calculateTouchArea(offset, cropRect, handleSizePx)
                                    },
                                    onDrag = { change, dragAmount ->
                                        change.consume()
                                        var newRect = cropRect

                                        newRect = when (currentTouchArea) {
                                            // 1. 整体平移（中心拖拽）
                                            TouchArea.CENTER -> newRect.translate(dragAmount.x, dragAmount.y)

                                            // 2. 四个角落拖拽（对应对角线锚点保持固定）
                                            TouchArea.TOP_LEFT -> Rect(newRect.left + dragAmount.x, newRect.top + dragAmount.y, newRect.right, newRect.bottom)
                                            TouchArea.TOP_RIGHT -> Rect(newRect.left, newRect.top + dragAmount.y, newRect.right + dragAmount.x, newRect.bottom)

                                            // 核心修复点：左下角拖拽只改变 left 和 bottom，右边 (right) 和顶边 (top) 保持不变
                                            TouchArea.BOTTOM_LEFT -> Rect(newRect.left + dragAmount.x, newRect.top, newRect.right, newRect.bottom + dragAmount.y)

                                            TouchArea.BOTTOM_RIGHT -> Rect(newRect.left, newRect.top, newRect.right + dragAmount.x, newRect.bottom + dragAmount.y)

                                            // 3. 四条边线拖拽（只改变单方向）
                                            TouchArea.TOP -> Rect(newRect.left, newRect.top + dragAmount.y, newRect.right, newRect.bottom)
                                            TouchArea.BOTTOM -> Rect(newRect.left, newRect.top, newRect.right, newRect.bottom + dragAmount.y)
                                            TouchArea.LEFT -> Rect(newRect.left + dragAmount.x, newRect.top, newRect.right, newRect.bottom)
                                            TouchArea.RIGHT -> Rect(newRect.left, newRect.top, newRect.right + dragAmount.x, newRect.bottom)

                                            TouchArea.NONE -> newRect
                                        }

                                        // 尺寸与边界约束 (绝不出图片有效区域)
                                        if (abs(newRect.width) >= minRectSizePx && abs(newRect.height) >= minRectSizePx) {
                                            val img = displayedImageRect
                                            val cLeft = newRect.left.coerceIn(img.left, img.right - minRectSizePx)
                                            val cTop = newRect.top.coerceIn(img.top, img.bottom - minRectSizePx)
                                            val cRight = newRect.right.coerceIn(cLeft + minRectSizePx, img.right)
                                            val cBottom = newRect.bottom.coerceIn(cTop + minRectSizePx, img.bottom)

                                            cropRect = if (currentTouchArea == TouchArea.CENTER) {
                                                val w = cropRect.width
                                                val h = cropRect.height
                                                val maxLeft = img.right - w
                                                val maxTop = img.bottom - h
                                                val constrainedL = newRect.left.coerceIn(img.left, maxLeft)
                                                val constrainedT = newRect.top.coerceIn(img.top, maxTop)
                                                Rect(constrainedL, constrainedT, constrainedL + w, constrainedT + h)
                                            } else {
                                                Rect(cLeft, cTop, cRight, cBottom)
                                            }
                                        }
                                    },
                                    onDragEnd = { currentTouchArea = TouchArea.NONE }
                                )
                            }
                    ) {
                        // 使用 Path Operation 计算全屏与裁剪框差集（框外画黑色半透明蒙版，框内透出照片原色）
                        val fullScreenPath = Path().apply { addRect(Rect(0f, 0f, size.width, size.height)) }
                        val cropPath = Path().apply { addRect(cropRect) }
                        val maskPath = Path.combine(PathOperation.Difference, fullScreenPath, cropPath)

                        drawPath(path = maskPath, color = Color.Black.copy(alpha = 0.6f))

                        // 绘制白色外边框
                        drawRect(
                            color = Color.White,
                            topLeft = Offset(cropRect.left, cropRect.top),
                            size = Size(cropRect.width, cropRect.height),
                            style = Stroke(width = 1.5.dp.toPx())
                        )

                        // 绘制四个角的手柄指示线
                        val cornerLen = 20.dp.toPx()
                        val cornerWidth = 3.5f.dp.toPx()
                        val strokeCap = StrokeCap.Round

                        // 左上
                        drawLine(Color.White, Offset(cropRect.left, cropRect.top + cornerLen), Offset(cropRect.left, cropRect.top), cornerWidth, strokeCap)
                        drawLine(Color.White, Offset(cropRect.left, cropRect.top), Offset(cropRect.left + cornerLen, cropRect.top), cornerWidth, strokeCap)
                        // 右上
                        drawLine(Color.White, Offset(cropRect.right - cornerLen, cropRect.top), Offset(cropRect.right, cropRect.top), cornerWidth, strokeCap)
                        drawLine(Color.White, Offset(cropRect.right, cropRect.top), Offset(cropRect.right, cropRect.top + cornerLen), cornerWidth, strokeCap)
                        // 左下
                        drawLine(Color.White, Offset(cropRect.left, cropRect.bottom - cornerLen), Offset(cropRect.left, cropRect.bottom), cornerWidth, strokeCap)
                        drawLine(Color.White, Offset(cropRect.left, cropRect.bottom), Offset(cropRect.left + cornerLen, cropRect.bottom), cornerWidth, strokeCap)
                        // 右下
                        drawLine(Color.White, Offset(cropRect.right - cornerLen, cropRect.bottom), Offset(cropRect.right, cropRect.bottom), cornerWidth, strokeCap)
                        drawLine(Color.White, Offset(cropRect.right, cropRect.bottom), Offset(cropRect.right, cropRect.bottom - cornerLen), cornerWidth, strokeCap)
                    }
                }
            }
        } else {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }

        // 4. 底部控制栏
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(onClick = onCancel) {
                Text("取消", color = Color.White)
            }

            Button(
                onClick = {
                    val currentBitmap = bitmap
                    val imgRect = displayedImageRect
                    if (currentBitmap != null && imgRect != null) {
                        coroutineScope.launch {
                            val resultUri = performCropAndSave(context, currentBitmap, cropRect, imgRect)
                            if (resultUri != null) {
                                onCropSuccess(resultUri)
                            } else {
                                Toast.makeText(context, "裁剪失败", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            ) {
                Text("确认裁剪")
            }
        }
    }
}

// 计算触摸点击区域逻辑
private fun calculateTouchArea(touch: Offset, rect: Rect, handleSize: Float): TouchArea {
    return when {
        Offset(rect.left, rect.top).distanceTo(touch) < handleSize -> TouchArea.TOP_LEFT
        Offset(rect.right, rect.top).distanceTo(touch) < handleSize -> TouchArea.TOP_RIGHT
        Offset(rect.left, rect.bottom).distanceTo(touch) < handleSize -> TouchArea.BOTTOM_LEFT
        Offset(rect.right, rect.bottom).distanceTo(touch) < handleSize -> TouchArea.BOTTOM_RIGHT
        abs(touch.x - rect.left) < handleSize && touch.y in rect.top..rect.bottom -> TouchArea.LEFT
        abs(touch.x - rect.right) < handleSize && touch.y in rect.top..rect.bottom -> TouchArea.RIGHT
        abs(touch.y - rect.top) < handleSize && touch.x in rect.left..rect.right -> TouchArea.TOP
        abs(touch.y - rect.bottom) < handleSize && touch.x in rect.left..rect.right -> TouchArea.BOTTOM
        rect.contains(touch) -> TouchArea.CENTER
        else -> TouchArea.NONE
    }
}

private fun Offset.distanceTo(other: Offset): Float {
    return kotlin.math.sqrt((x - other.x) * (x - other.x) + (y - other.y) * (y - other.y))
}

// 核心：屏幕坐标到原始 Bitmap 像素坐标转换与裁剪保存
private suspend fun performCropAndSave(
    context: Context,
    sourceBitmap: Bitmap,
    cropRect: Rect,
    displayedImageRect: Rect
): Uri? = withContext(Dispatchers.IO) {
    try {
        // 1. 换算 X/Y 轴的缩放比例 (原图像素 / 屏幕显示像素)
        val scaleX = sourceBitmap.width.toFloat() / displayedImageRect.width
        val scaleY = sourceBitmap.height.toFloat() / displayedImageRect.height

        // 2. 屏幕坐标转为相对图片左上角的偏移量
        val relativeLeft = cropRect.left - displayedImageRect.left
        val relativeTop = cropRect.top - displayedImageRect.top

        // 3. 精确映射至原图上的真实像素点
        val realX = (relativeLeft * scaleX).toInt().coerceIn(0, sourceBitmap.width - 1)
        val realY = (relativeTop * scaleY).toInt().coerceIn(0, sourceBitmap.height - 1)

        val realWidth = (cropRect.width * scaleX).toInt().coerceAtMost(sourceBitmap.width - realX)
        val realHeight = (cropRect.height * scaleY).toInt().coerceAtMost(sourceBitmap.height - realY)

        if (realWidth <= 0 || realHeight <= 0) return@withContext null

        // 4. 截取原始 Bitmap 对应区域
        val croppedBitmap = Bitmap.createBitmap(
            sourceBitmap,
            realX,
            realY,
            realWidth,
            realHeight
        )

        // 5. 写入缓存临时文件并生成 Content Uri 返回
        val outputFile = File(context.cacheDir, "cropped_${System.currentTimeMillis()}.jpg")
        FileOutputStream(outputFile).use { out ->
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
        }

        FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            outputFile
        )
    } catch (e: Exception) {
        e.printStackTrace()
                null
    }
}