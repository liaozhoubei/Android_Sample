package com.yueproject.example

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.webkit.MimeTypeMap
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.InsertDriveFile
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.yueproject.example.ui.theme.Android_ExampleTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okio.BufferedSink
import okio.source
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 当配置了 <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" /> 权限式，
 * 可以使用绝对路径
 */
class FileExplorerActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_ExampleTheme {
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                FileExplorerApp(widthSizeClass = windowSizeClass.widthSizeClass)
            }
        }
    }
}
// 导航 Tab 界面定义
enum class FileNavigationTab(val title: String, val icon: ImageVector) {
    FILES("文件", Icons.Default.Folder),
    HOME("概览", Icons.Default.Home),
    SETTINGS("设置", Icons.Default.Settings)
}

// 真实文件节点数据模型
data class FileNode(
    val file: File,
    val name: String = file.name,
    val path: String = file.absolutePath,
    val isDirectory: Boolean = file.isDirectory,
    val isImage: Boolean = file.isFile && (
            file.extension.equals("jpg", true) ||
                    file.extension.equals("png", true) ||
                    file.extension.equals("jpeg", true) ||
                    file.extension.equals("webp", true)
            )
)

// ==========================================
// 1. 存储权限与系统设置跳转
// ==========================================

fun hasStoragePermission(context: Context): Boolean {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        Environment.isExternalStorageManager()
    } else {
        val readGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        val writeGranted = ContextCompat.checkSelfPermission(
            context, Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        readGranted && writeGranted
    }
}

@Composable
fun rememberStoragePermissionLauncher(
    onPermissionResult: (Boolean) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val legacyPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        onPermissionResult(result.values.all { it })
    }

    return remember(context) {
        {
            if (hasStoragePermission(context)) {
                onPermissionResult(true)
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION).apply {
                        data = Uri.parse("package:${context.packageName}")
                    }
                    context.startActivity(intent)
                } catch (e: Exception) {
                    val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
                    context.startActivity(intent)
                }
            } else {
                legacyPermissionLauncher.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
            }
        }
    }
}

// ==========================================
// 2. 真实文件系统 IO 工具类
// ==========================================

/**
 * 扫描指定真实路径下的文件列表，按文件夹优先、文件名排序
 */
fun loadRealFilesFromPath(path: String): List<FileNode> {
    val directory = File(path)
    if (!directory.exists() || !directory.isDirectory) return emptyList()

    val fileList = directory.listFiles() ?: return emptyList()

    return fileList.map { FileNode(it) }
        .sortedWith(compareBy({ !it.isDirectory }, { it.name.lowercase() }))
}

// ==========================================
// 3. 主程序与响应式布局架构
// ==========================================

@Composable
fun FileExplorerApp(widthSizeClass: WindowWidthSizeClass) {
    val context = LocalContext.current
    var hasPermission by remember { mutableStateOf(hasStoragePermission(context)) }

    val requestPermission = rememberStoragePermissionLauncher { granted ->
        hasPermission = granted
    }

    // 监控从设置授权页返回的生命周期事件
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                hasPermission = hasStoragePermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        if (!hasPermission) {
            PermissionRequestScreen(onRequestPermission = requestPermission)
        } else {
            MainExplorerScreen(widthSizeClass = widthSizeClass)
        }
    }
}

@Composable
fun PermissionRequestScreen(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "需要存储访问权限", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "需要存储读取与写入权限以浏览真实文件系统及进行图片裁剪保存。",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("授予权限")
        }
    }
}

@Composable
fun MainExplorerScreen(widthSizeClass: WindowWidthSizeClass) {
    var currentTab by remember { mutableStateOf(FileNavigationTab.FILES) }

    // 初始化 SD 卡根目录
    val rootPath = remember { Environment.getExternalStorageDirectory().absolutePath }
    var currentDirectoryPath by remember { mutableStateOf(rootPath) }
    var selectedFileNode by remember { mutableStateOf<FileNode?>(null) }

    // 使用 Map 存储每个路径独立的 LazyListState
    val scrollStates = remember { mutableStateMapOf<String, LazyListState>() }

    // 动态获取/初始化当前目录对应的 LazyListState
    val currentListState = scrollStates.getOrPut(currentDirectoryPath) {
        LazyListState()
    }

        // 读取当前目录下的真实文件列表
    val realFilesList = remember(currentDirectoryPath) {
        loadRealFilesFromPath(currentDirectoryPath)
    }


    // 处理返回上级目录的逻辑
    val canNavigateUp = currentDirectoryPath != rootPath || selectedFileNode != null
    fun handleNavigateUp() {
        if (selectedFileNode != null) {
            selectedFileNode = null // 退出文件详情页（此时 fileListState 会保持退出前的滚动位置）
        } else if (currentDirectoryPath != rootPath) {
            val parent = File(currentDirectoryPath).parentFile
            if (parent != null && parent.exists()) {
                currentDirectoryPath = parent.absolutePath
            }
        }
    }

    // 拦截物理/手势返回键
    BackHandler(enabled = canNavigateUp) {
        handleNavigateUp()
    }

    val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded ||
            widthSizeClass == WindowWidthSizeClass.Medium

    if (isExpandedScreen) {
        // 平板/大屏模式：侧边栏 NavigationRail + 真实文件双栏
        Row(modifier = Modifier.fillMaxSize()) {
            NavigationRail {
                FileNavigationTab.entries.forEach { tab ->
                    NavigationRailItem(
                        selected = currentTab == tab,
                        onClick = {
                            currentTab = tab
                            selectedFileNode = null
                        },
                        icon = { Icon(tab.icon, contentDescription = tab.title) },
                        label = { Text(tab.title) }
                    )
                }
            }
            VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
            Box(modifier = Modifier.weight(1f)) {
                if (currentTab == FileNavigationTab.FILES) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(modifier = Modifier.weight(0.45f)) {
                            FileDirectoryList(
                                currentPath = currentDirectoryPath,
                                fileList = realFilesList,
                                selectedNode = selectedFileNode,
                                listState = currentListState, // 传入当前路径专属的 listState
                                onDirectoryClick = { node -> currentDirectoryPath = node.path },
                                onFileClick = { node -> selectedFileNode = node },
                                onNavigateUp = { handleNavigateUp() },
                                canNavigateUp = currentDirectoryPath != rootPath
                            )
                        }
                        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
                        Box(modifier = Modifier.weight(0.55f)) {
                            FileDetailViewer(
                                node = selectedFileNode,
                                onImageCropped = {
                                    // 刷新文件列表和详情状态
                                    selectedFileNode = selectedFileNode?.copy()
                                }
                            )
                        }
                    }
                } else {
                    SimpleTabScreen(title = currentTab.title)
                }
            }
        }
    } else {
        // 手机模式：内容区 + 底部 NavigationBar
        Scaffold(
            bottomBar = {
                if (selectedFileNode == null) {
                    NavigationBar {
                        FileNavigationTab.entries.forEach { tab ->
                            NavigationBarItem(
                                selected = currentTab == tab,
                                onClick = {
                                    currentTab = tab
                                    selectedFileNode = null
                                },
                                icon = { Icon(tab.icon, contentDescription = tab.title) },
                                label = { Text(tab.title) }
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                if (currentTab == FileNavigationTab.FILES) {
                    if (selectedFileNode == null) {
                        FileDirectoryList(
                            currentPath = currentDirectoryPath,
                            fileList = realFilesList,
                            selectedNode = null,
                            listState = currentListState, // 传入当前路径专属的 listState
                            onDirectoryClick = { node -> currentDirectoryPath = node.path },
                            onFileClick = { node -> selectedFileNode = node },
                            onNavigateUp = { handleNavigateUp() },
                            canNavigateUp = currentDirectoryPath != rootPath
                        )
                    } else {
                        // 处于文件详情/预览页
                        Scaffold(
                            topBar = {
                                @OptIn(ExperimentalMaterial3Api::class)
                                TopAppBar(
                                    title = { Text(selectedFileNode?.name ?: "文件详情") },
                                    navigationIcon = {
                                        IconButton(onClick = { handleNavigateUp() }) {
                                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回")
                                        }
                                    }
                                )
                            }
                        ) { detailPadding ->
                            Box(modifier = Modifier.padding(detailPadding)) {
                                FileDetailViewer(
                                    node = selectedFileNode,
                                    onImageCropped = {
                                        selectedFileNode = selectedFileNode?.copy()
                                    }
                                )
                            }
                        }
                    }
                } else {
                    SimpleTabScreen(title = currentTab.title)
                }
            }
        }
    }
}

// ==========================================
// 4. 真实文件列表 UI 与文件夹深度穿透组件(接入 LazyListState)
// ==========================================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileDirectoryList(
    currentPath: String,
    fileList: List<FileNode>,
    selectedNode: FileNode?,
    listState: LazyListState, // 【新增参数】：绑定滚动状态
    onDirectoryClick: (FileNode) -> Unit,
    onFileClick: (FileNode) -> Unit,
    onNavigateUp: () -> Unit,
    canNavigateUp: Boolean
) {
    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = {
                Text(
                    text = currentPath.substringAfterLast("/"),
                    style = MaterialTheme.typography.titleMedium
                )
            },
            navigationIcon = {
                if (canNavigateUp) {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "返回上一级")
                    }
                }
            }
        )

        LazyColumn(
            state = listState, // 【绑定滚动状态】：保留滚动位置的关键
            modifier = Modifier.fillMaxSize()) {
            items(fileList, key = { it.path }) { node ->
                val isSelected = node.path == selectedNode?.path
                ListItem(
                    headlineContent = {
                        Text(
                            text = node.name,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    },
                    supportingContent = {
                        Text(
                            if (node.isDirectory) {
                                val subItems = node.file.listFiles()?.size ?: 0
                                "$subItems 项"
                            } else {
                                "${formatFileSize(node.file.length())} "
                            }
                        )
                    },
                    leadingContent = {
                        Icon(
                            imageVector = when {
                                node.isDirectory -> Icons.Default.Folder
                                node.isImage -> Icons.Default.Image
                                else -> Icons.Default.InsertDriveFile
                            },
                            contentDescription = null,
                            tint = when {
                                node.isDirectory -> MaterialTheme.colorScheme.primary
                                node.isImage -> MaterialTheme.colorScheme.tertiary
                                else -> MaterialTheme.colorScheme.secondary
                            }
                        )
                    },
                    modifier = Modifier.clickable {
                        if (node.isDirectory) {
                            onDirectoryClick(node)
                        } else {
                            onFileClick(node)
                        }
                    }
                )
                HorizontalDivider(thickness = 0.5.dp)
            }
        }
    }
}

// ==========================================
// 5. 文件详情与真实【图片预览 + 裁剪保存】组件
// ==========================================

@Composable
fun FileDetailViewer(
    node: FileNode?,
    onImageCropped: () -> Unit
) {
    if (node == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("请在左侧选择文件", color = MaterialTheme.colorScheme.outline)
        }
        return
    }

    val context = LocalContext.current
    var refreshKey by remember { mutableIntStateOf(0) } // 控制裁剪完成后刷图的 Key

    // 监听系统 CROP Intent 返回结果
    val cropSystemLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            Toast.makeText(context, "图片裁剪并保存成功！", Toast.LENGTH_SHORT).show()

            val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE).apply {
                data = Uri.fromFile(node.file)
            }
            context.sendBroadcast(mediaScanIntent)

            refreshKey++
            onImageCropped()
        }
    }

    // 2. 裁剪 Activity Launcher
    val cropLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val croppedUri = result.data?.getParcelableExtra<Uri>(CropActivity.EXTRA_OUTPUT_URI)
            if (croppedUri != null) {
                // 保存裁剪后的图片到相册
                saveImageToGallery(context, croppedUri)
                // 核心修复：裁剪保存完成后刷新文件列表
                refreshKey++
            }
        } else {
            Toast.makeText(context, "已取消裁剪", Toast.LENGTH_SHORT).show()
        }
    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = node.name, fontSize = 20.sp, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(12.dp))

        val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }
        val lastModified = remember(node.file, refreshKey) {
            dateFormat.format(Date(node.file.lastModified()))
        }

        Text(text = "路径: ${node.path}", style = MaterialTheme.typography.bodyMedium)
//        Text(text = "大小: ${node.file.length() / 1024} KB", style = MaterialTheme.typography.bodyMedium)
        Text(text = "大小: ${formatFileSize(node.file.length())} ", style = MaterialTheme.typography.bodyMedium)
        Text(text = "修改时间: $lastModified", style = MaterialTheme.typography.bodyMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // 图片专属的真预览与裁剪功能
        if (node.isImage) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                val bitmap = remember(node.path, refreshKey) {
                    BitmapFactory.decodeFile(node.file.absolutePath)
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "图片预览",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("无法读取图片，文件可能损坏或已被移动")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 触发真实的系统图片裁剪 Intent
            Button(
                onClick = {
                    if (!node.file.exists()) {
                        Toast.makeText(context, "真实文件不存在", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    try {
//                        // 1. 获取输入 Uri (通过 FileProvider)
//                        val inputUri = FileProvider.getUriForFile(
//                            context,
//                            "${context.packageName}.fileprovider",
//                            node.file
//                        )
//
//                        // 2. 保存到新文件 Uri
//                        val parentDir = node.file.parentFile ?: context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
//                        val newCroppedFile = File(
//                            parentDir,
//                            "${node.file.nameWithoutExtension}_cropped_${System.currentTimeMillis()}.jpg"
//                        )
//                        val outputUri = Uri.fromFile(newCroppedFile)
//
//                        // 3. 构建系统的 Intent.ACTION_CROP
//                        val cropIntent = Intent("com.android.camera.action.CROP").apply {
//                            setDataAndType(inputUri, "image/*")
//                            putExtra("crop", "true")
//                            putExtra("aspectX", 1) // 1:1 剪裁
//                            putExtra("aspectY", 1)
//                            putExtra("outputX", 600)
//                            putExtra("outputY", 600)
//                            putExtra("scale", true)
//                            putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
//                            putExtra("return-data", false)
//                            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
//                        }
//
//                        cropLauncher.launch(cropIntent)

//                        val uri = fileItem.getUri(context)
                        val inputUri = FileProvider.getUriForFile(
                            context,
                            "${context.packageName}.fileprovider",
                            node.file
                        )
                        val intent = CropActivity.createIntent(context, inputUri)
                        cropLauncher.launch(intent)
                    } catch (e: Exception) {
                        Toast.makeText(context, "当前设备不支持系统裁剪或没有裁剪软件", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Crop, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("裁剪并保存图片")
            }
        }
    }
}

@Composable
fun SimpleTabScreen(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}
// 3. 核心：读取 Uri 数据流模拟网络上传 (例如搭配 OkHttp/Retrofit)
private suspend fun uploadFileByUri(context: Context, uri: Uri, fileName: String) {
    withContext(Dispatchers.IO) {
        try {
            // 通过 ContentResolver 读取 InputStream，避免暴露真实文件路径
            val inputStream = context.contentResolver.openInputStream(uri)
            val mimeType = context.contentResolver.getType(uri)
                ?: getMimeTypeFromExtension(fileName)

            if (inputStream != null) {
                // 模拟读取字节流并上传
                val buffer = ByteArray(1024 * 64) // 64KB 缓冲区
                var bytesRead: Int
                var totalBytesRead = 0L

                while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                    totalBytesRead += bytesRead
                    // TODO: 在此处接入你的网络请求库 (如 OkHttp RequestBody/Retrofit) 将 buffer 写入 Socket
                }
                inputStream.close()

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        context,
                        "文件 $fileName 上传成功！(共 ${formatFileSize(totalBytesRead)}, 类型: $mimeType)",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "上传失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

// 检查 Uri 是否为图片类型
private fun checkIsImage(context: Context, uri: Uri): Boolean {
    val mimeType = context.contentResolver.getType(uri)
    if (mimeType != null && mimeType.startsWith("image/")) {
        return true
    }
    // 备用：根据后缀判断
    val path = uri.path ?: return false
    val extension = path.substringAfterLast('.', "").lowercase()
    return extension in listOf("jpg", "jpeg", "png", "webp", "bmp")
}
// 保存图片到系统相册
private fun saveImageToGallery(context: Context, sourceUri: Uri) {
    try {
        val inputStream = context.contentResolver.openInputStream(sourceUri) ?: return

        // 1. 修正时间格式化逻辑
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "Cropped_$timeStamp.jpg"

        var savedAbsolutePath: String? = null

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val contentValues = android.content.ContentValues().apply {
                put(android.provider.MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(android.provider.MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                put(android.provider.MediaStore.MediaColumns.RELATIVE_PATH, android.os.Environment.DIRECTORY_PICTURES)
            }
            val imageUri = context.contentResolver.insert(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            if (imageUri != null) {
                context.contentResolver.openOutputStream(imageUri)?.use { outputStream ->
                    inputStream.copyTo(outputStream)
                }

                // Android 10+ 拼接相对路径与文件名作为绝对路径提示
                val picturesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
                savedAbsolutePath = File(picturesDir, fileName).absolutePath
            }
        } else {
            val imagesDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_PICTURES)
            val imageFile = File(imagesDir, fileName)

            // 确保目录存在
            if (!imagesDir.exists()) {
                imagesDir.mkdirs()
            }

            FileOutputStream(imageFile).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
            val mediaScanIntent = android.content.Intent(android.content.Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
            mediaScanIntent.data = Uri.fromFile(imageFile)
            context.sendBroadcast(mediaScanIntent)

            // API < 29 直接读取 File 对象的 absolutePath
            savedAbsolutePath = imageFile.absolutePath
        }

        inputStream.close()

        // 2. 弹出包含绝对路径的 Toast 提示
        if (savedAbsolutePath != null) {
            Toast.makeText(context, "已成功保存到: $savedAbsolutePath", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(context, "保存失败，未生成有效路径", Toast.LENGTH_SHORT).show()
        }

    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "保存失败: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}


// 工具函数：计算文件可读大小
/**
 * @param size 传入的单位是 B
 */
private fun formatFileSize(size: Long): String {
    if (size <= 0) return "0 B"
    val units = arrayOf("B", "KB", "MB", "GB", "TB")
    val digitGroups = (Math.log10(size.toDouble()) / Math.log10(1024.0)).toInt()
    return String.format("%.1f %s", size / Math.pow(1024.0, digitGroups.toDouble()), units[digitGroups])
}

// 工具函数：根据文件后缀获取 MimeType
private fun getMimeTypeFromExtension(fileName: String): String {
    val extension = fileName.substringAfterLast('.', "")
    return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.lowercase()) ?: "application/octet-stream"
}

// 将 Uri 转为 OkHttp RequestBody 的扩展方法
fun Uri.toRequestBody(context: Context): RequestBody {
    return object : RequestBody() {
        override fun contentType() = context.contentResolver.getType(this@toRequestBody)?.toMediaTypeOrNull()

        override fun writeTo(sink: BufferedSink) {
            context.contentResolver.openInputStream(this@toRequestBody)?.use { inputStream ->
                sink.writeAll(inputStream.source())
            }
        }
    }
}

// 使用示例：
// val body = MultipartBody.Part.createFormData("file", fileItem.name, uri.toRequestBody(context))
// apiService.uploadFile(body)