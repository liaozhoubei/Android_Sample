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
import androidx.compose.ui.tooling.preview.Preview
import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yueproject.example.ui.theme.Android_ExampleTheme

/**
 * Android 12+ 官方推行的新 API（SplashScreen）的定位仅仅是“背景色 + App Logo Icon”的静态展示：
 * 无法进行 UI 交互：系统 Splash 界面只是一个遮罩层，你无法在上面放置按钮（比如“同意协议”、“点击跳过”）、无法放置可滑动的引导页（ViewPager2）。
 * 无法拦截触摸事件：它只负责在进程冷启动时垫场，加载完后就会被强行移除。
 * 因此，像首次安装的引导页（Onboarding）、隐私政策授权框、开屏广告这类强交互业务，最佳选择依然是保留独立的 SplashActivity，
 * 并加上 tools:ignore="SplashScreenAttribute" 规避 Lint 警告
 */
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // 适配 Android 12+：当系统 Splash 准备退出时，直接将其移除，避免和你的引导页 UI 发生动画重叠/闪烁
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            splashScreen.setOnExitAnimationListener { splashScreenView ->
                splashScreenView.remove()
            }
        }
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
//            Android_ExampleTheme {
                SplashScreenContent(
                    activity = this,
                    onPermissionsGranted = { navigateToMain() }
                )
//            }
        }
    }
    private fun navigateToMain() {
        startActivity(Intent(this, MainActivity::class.java))
        finish() // 销毁 SplashActivity，防止按返回键回到启动页
    }
}
@Composable
private fun SplashScreenContent(
    activity: ComponentActivity,
    onPermissionsGranted: () -> Unit
) {
    val context = LocalContext.current

    // 需要申请的权限列表
    val requiredPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA,
            Manifest.permission.POST_NOTIFICATIONS
        )
    } else {
        arrayOf(
            Manifest.permission.CAMERA
        )
    }

    // 状态控制：是否显示拒绝提示框，以及是否为“不再询问”模式
    var showDialog by remember { mutableStateOf(false) }
    var isPermanentlyDenied by remember { mutableStateOf(false) }

    // 注册权限申请 Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { resultMap ->
        val allGranted = resultMap.values.all { it }
        if (allGranted) {
            onPermissionsGranted()
        } else {
            // 找出被拒绝的权限
            val deniedPermissions = resultMap.filter { !it.value }.keys

            // 判断被拒绝的权限中，是否有“不再询问”的项
            // shouldShowRequestPermissionRationale 返回 false 说明用户勾选了“不再询问”（或系统禁止再弹）
            val permanentlyDenied = deniedPermissions.any { permission ->
                !activity.shouldShowRequestPermissionRationale(permission)
            }

            isPermanentlyDenied = permanentlyDenied
            showDialog = true
        }
    }

    // 页面初次加载时触发申请
    LaunchedEffect(Unit) {
        permissionLauncher.launch(requiredPermissions)
    }

    // 主 UI 界面
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "My App",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(24.dp))
            CircularProgressIndicator()
        }

        // 权限拒绝弹窗
        if (showDialog) {
            PermissionDialog(
                isPermanentlyDenied = isPermanentlyDenied,
                onRetry = {
                    showDialog = false
                    permissionLauncher.launch(requiredPermissions)
                },
                onOpenSettings = {
                    showDialog = false
                    openAppSettings(context)
                }
            )
        }
    }
}

/**
 * 权限拒绝/彻底拒绝弹窗
 */
@Composable
private fun PermissionDialog(
    isPermanentlyDenied: Boolean,
    onRetry: () -> Unit,
    onOpenSettings: () -> Unit
) {
    AlertDialog(
        onDismissRequest = {},
        title = {
            Text(if (isPermanentlyDenied) "需要手动开启权限" else "权限请求")
        },
        text = {
            Text(
                if (isPermanentlyDenied) {
                    "您已禁用核心权限且勾选了“不再询问”。请前往系统“设置”->“权限”中手动开启相关权限，否则应用无法正常运行。"
                } else {
                    "应用需要相机等核心权限才能正常运行，请授权。"
                }
            )
        },
        confirmButton = {
            if (isPermanentlyDenied) {
                Button(onClick = onOpenSettings) {
                    Text("前往设置")
                }
            } else {
                Button(onClick = onRetry) {
                    Text("重新授权")
                }
            }
        },
        dismissButton = {
            if (!isPermanentlyDenied) {
                TextButton(onClick = onOpenSettings) {
                    Text("手动设置")
                }
            }
        }
    )
}

/**
 * 跳转到当前应用的系统设置页面
 */
private fun openAppSettings(context: Context) {
    val intent = Intent(
        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
        Uri.fromParts("package", context.packageName, null)
    ).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    context.startActivity(intent)
}