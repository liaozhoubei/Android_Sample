package com.yueproject.example.utils

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.File

object CameraUtils {

    // 创建用于保存照片的临时 Uri
    fun createTempImageUri(context: Context): Uri {
        val tempFile = File.createTempFile(
            "JPEG_${System.currentTimeMillis()}_",
            ".jpg",
            context.cacheDir
        ).apply {
            createNewFile()
            deleteOnExit()
        }
        val authority = "${context.packageName}.fileprovider"
        return FileProvider.getUriForFile(context, authority, tempFile)
    }

    /**
     * 构建系统裁剪 Intent (Android 7.0 ~ 16 兼容)
     * Android 4.4+ 处理 Uri 权限的最佳实践
     * @param outputUri 确保你的 outputUri 不要存放在 context.cacheDir（即 internal_cache），
     * 因为部分手机系统裁剪软件（如 OPLUS/Flyme 某些版本）即使拿到了临时 grantUriPermission 授权，
     * 也会在 C 层的 Native 代码中因为 Linux 文件权限拒绝对 data/data/com.xxx/cache/ 目录的写入。
     * 安全的 outputUri 位置：建议使用 getExternalFilesDir(Environment.DIRECTORY_PICTURES)
     * 配合 FileProvider 生成，或者直接使用 Uri.fromFile(file) 存储在 App 的外部私有目录。
     */
    fun buildCropIntent(context: Context, inputUri: Uri, outputUri: Uri): Intent {
        val intent = Intent("com.android.camera.action.CROP").apply {
            setDataAndType(inputUri, "image/*")
            putExtra("crop", "true")
            putExtra("aspectX", 1)
            putExtra("aspectY", 1)
            putExtra("outputX", 600)
            putExtra("outputY", 600)
            putExtra("scale", true)
            putExtra(MediaStore.EXTRA_OUTPUT, outputUri)
            putExtra("return-data", false)
            putExtra("outputFormat", "JPEG")

            // 基础读写 Flag
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)

            // 关键点 1：将 outputUri 放入 ClipData，确保跨进程传递授权（针对 Android 10+ 提升兼容性）
            clipData = android.content.ClipData.newRawUri("crop_output", outputUri)
        }

        // 关键点 2：使用 MATCH_DEFAULT_ONLY 标志进行匹配
        val resInfoList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // 从 Android 11 开始，引入了软件包可见性（Package Visibility）保护。
            //如果你没有在 AndroidManifest.xml 中声明 <queries> 标签，context.packageManager.queryIntentActivities(this, 0) 将会返回空列表（Empty List）！
            //后果：for 循环完全不会执行，grantUriPermission 一次都不会被调用，直接导致 Permission Denial 报错出现。
            context.packageManager.queryIntentActivities(
                intent,
                PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_DEFAULT_ONLY.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY)
        }

        // 显式为找到的所有裁剪 Target 授权
        for (resolveInfo in resInfoList) {
            val packageName = resolveInfo.activityInfo.packageName

            // 授权输入 URI 读权限
            context.grantUriPermission(
                packageName,
                inputUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            // 授权输出 URI 读写权限
            context.grantUriPermission(
                packageName,
                outputUri,
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
        }

        return intent
    }
}