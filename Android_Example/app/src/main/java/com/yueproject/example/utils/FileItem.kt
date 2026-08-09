package com.yueproject.example.utils

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val size: Long,
    val lastModified: Long,
    val file: File
) {
    // 获取文件的安全 Uri (支持传递与上传)
    fun getUri(context: Context): Uri {
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
        } catch (e: Exception) {
            // 如果未配置 FileProvider，回退至文件 Uri
            Uri.fromFile(file)
        }
    }
}