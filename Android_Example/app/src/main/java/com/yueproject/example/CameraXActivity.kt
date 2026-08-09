package com.yueproject.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import coil.compose.rememberAsyncImagePainter
import java.util.concurrent.Executor
import com.yueproject.example.ui.theme.Android_ExampleTheme
import com.yueproject.example.utils.CameraUtils
import java.io.File

class CameraXActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_ExampleTheme {
                CameraXComposeScreen()
            }
        }
    }
}
@Composable
fun CameraXComposeScreen() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    // 1. 状态管理
    var imageUri by remember { mutableStateOf<Uri?>(null) } // 最终裁切后显示的图片 URI
    var cameraLensFacing by remember { mutableIntStateOf(CameraSelector.LENS_FACING_BACK) } // 镜头方向
    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
        )
    }

    // CameraX 绑定的组件
    val imageCapture = remember { ImageCapture.Builder().build() }
    var tempCapturedUri by remember { mutableStateOf<Uri?>(null) } // 裁剪前的临时文件地址
    var tempCropOutputUri by remember { mutableStateOf<Uri?>(null) }

    // 2. Activity Result Launchers
    // (1) 权限请求 Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasCameraPermission = isGranted
    }

    // (2) 裁剪 Intent 返回 Launcher
    val cropActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = tempCropOutputUri
        } else {
            Toast.makeText(context, "裁剪已取消", Toast.LENGTH_SHORT).show()
        }
    }

    // 启动裁剪的通用闭包
    val startCrop = { inputUri: Uri ->
        val outUri = CameraUtils.createTempImageUri(context)
        tempCropOutputUri = outUri
        val cropIntent = CameraUtils.buildCropIntent(context, inputUri, outUri)
        cropActivityResultLauncher.launch(cropIntent)
    }


    // 1. 注册自定义 CropActivity Launcher
    val customCropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = result.data?.getParcelableExtra<Uri>(CropActivity.EXTRA_OUTPUT_URI)
            if (croppedUri != null) {
                imageUri = croppedUri // 在预览框中更新裁剪后的图片
            }
        } else {
            Toast.makeText(context, "已取消裁剪", Toast.LENGTH_SHORT).show()
        }
    }

    // 调起自定义的的闭包
    val startCustomerCrop = { inputUri: Uri ->
        val intent = CropActivity.createIntent(context, inputUri)
        customCropLauncher.launch(intent)
    }

    // (3) 打开系统相机 Launcher
    val systemCameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success && tempCapturedUri != null) {
            startCrop(tempCapturedUri!!)
        }
    }

    // 检查权限
    LaunchedEffect(Unit) {
        if (!hasCameraPermission) {
            permissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    // UI 布局
    Box(modifier = Modifier.fillMaxSize()) {

        // ================= 控件 2: 自定义 CameraX 预览界面 =================
        if (hasCameraPermission) {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { ctx ->
                    PreviewView(ctx).apply {
                        scaleType = PreviewView.ScaleType.FILL_CENTER
                    }
                },
                update = { previewView ->
                    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(cameraLensFacing)
                            .build()

                        try {
                            cameraProvider.unbindAll()
                            cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                cameraSelector,
                                preview,
                                imageCapture
                            )
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }, ContextCompat.getMainExecutor(context))
                }
            )
        } else {
            Box(
                modifier = Modifier.fillMaxSize().background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "未授予相机权限", color = Color.White)
            }
        }

        // ================= 控件 1: 显示最后一张照片的预览图 =================
        Box(
            modifier = Modifier
                .statusBarsPadding()
                .padding(16.dp)
                .size(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.DarkGray)
                .border(2.dp, Color.White, RoundedCornerShape(8.dp))
                .align(Alignment.TopStart)
        ) {
            if (imageUri != null) {
                Image(
                    painter = rememberAsyncImagePainter(imageUri),
                    contentDescription = "裁剪后预览图",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            } else {
                Text(
                    text = "无预览图",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // 底部控制按钮容器
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // ================= 控件 3: 打开系统摄像机按钮 =================
                Button(onClick = {
                    val uri = CameraUtils.createTempImageUri(context)
                    tempCapturedUri = uri
                    systemCameraLauncher.launch(uri)
                }) {
                    Text("系统相机")
                }

                // ================= 控件 4: 切换前后摄像头按钮 =================
                Button(onClick = {
                    cameraLensFacing = if (cameraLensFacing == CameraSelector.LENS_FACING_BACK) {
                        CameraSelector.LENS_FACING_FRONT
                    } else {
                        CameraSelector.LENS_FACING_BACK
                    }
                }) {
                    Text("切换摄像头")
                }
            }

            // 自定义 CameraX 拍照按钮
            Button(
                onClick = {
                    if (hasCameraPermission) {
                        takePhotoWithCameraX(
                            context = context,
                            imageCapture = imageCapture,
                            executor = ContextCompat.getMainExecutor(context),
                            onPhotoSaved = { uri ->
//                                startCrop(uri)
                                startCustomerCrop(uri)
                            }
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) {
                Text("CameraX 拍照并裁剪")
            }
        }
    }
}

// CameraX 拍照实现
private fun takePhotoWithCameraX(
    context: Context,
    imageCapture: ImageCapture,
    executor: Executor,
    onPhotoSaved: (Uri) -> Unit
) {
    val photoUri = CameraUtils.createTempImageUri(context)
    val outputOptions = ImageCapture.OutputFileOptions.Builder(
        context.contentResolver,
        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
        android.content.ContentValues()
    ).build()

    // 存储在本地 cache 文件的 OutputFileOptions
    val file = File(context.cacheDir, "camerax_${System.currentTimeMillis()}.jpg")
    val fileOptions = ImageCapture.OutputFileOptions.Builder(file).build()

    imageCapture.takePicture(
        fileOptions,
        executor,
        object : ImageCapture.OnImageSavedCallback {
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                // Android 6.0 及更早可以使用 savedUri ，后续版本则要使用 providerUri
                val savedUri = Uri.fromFile(file)
                val providerUri = FileProvider.getUriForFile(
                    context,
                    "${context.packageName}.fileprovider",
                    file
                )
                onPhotoSaved(providerUri)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(context, "拍照失败: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
        }
    )
}