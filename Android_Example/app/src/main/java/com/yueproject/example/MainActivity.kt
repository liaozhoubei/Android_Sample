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
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.yueproject.example.ui.theme.Android_ExampleTheme



// Activity 信息数据类
data class ActivityInfoModel(
    val simpleName: String,
    val fullName: String
)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_ExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ActivityListScreen(innerPadding)
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityListScreen(innerPadding: PaddingValues) {
    val context = LocalContext.current

    val activityListState = produceState<List<ActivityInfoModel>>(initialValue = emptyList()) {
        value = getActivityList(context)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activity 列表 (${activityListState.value.size})") }
            )
        }
    ) { paddingValues ->
        val activities = activityListState.value

        if (activities.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("正在加载或未找到 Activity")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(activities) { activity ->
                    ActivityItemCard(
                        activity = activity,
                        onClick = {
                            launchActivity(context, activity.fullName)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ActivityItemCard(
    activity: ActivityInfoModel,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onClick() }, // 为卡片添加点击事件
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = activity.simpleName,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = activity.fullName,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 通过类全路径名反射生成 Intent 并启动 Activity
 */
private fun launchActivity(context: Context, fullName: String) {
    try {
        val targetClass = Class.forName(fullName)
        val intent = Intent(context, targetClass)
        context.startActivity(intent)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
        Toast.makeText(context, "未找到该 Activity 类", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "无法启动该 Activity (可能缺失必要参数)", Toast.LENGTH_SHORT).show()
    }
}

private fun getActivityList(context: Context): List<ActivityInfoModel> {
    val pm = context.packageManager
    val packageName = context.packageName

    return try {
        val packageInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            pm.getPackageInfo(
                packageName,
                PackageManager.PackageInfoFlags.of(PackageManager.GET_ACTIVITIES.toLong())
            )
        } else {
            @Suppress("DEPRECATION")
            pm.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
        }

        packageInfo.activities
            ?.filter { activityInfo ->
                val name = activityInfo.name
                // 1. 只保留以应用包名为前缀的 Activity
                // 2. 过滤掉包含 $ 的内部类/匿名类
                // 3. 过滤掉 androidx / google / android 系统的 Activity
                name.startsWith(packageName) &&
                        !name.contains("$") &&
                        !name.contains("MainActivity") &&
                        !name.contains("SplashActivity") &&
                        !name.contains("CropActivity") &&
                        !name.startsWith("androidx.")
            }
            ?.map { activityInfo ->
                ActivityInfoModel(
                    simpleName = activityInfo.name.substringAfterLast('.'),
                    fullName = activityInfo.name
                )
            } ?: emptyList()
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}