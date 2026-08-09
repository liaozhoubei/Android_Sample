package com.yueproject.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.yueproject.example.ui.theme.Android_ExampleTheme

/**
 * 在手机模式下，底部导航栏常驻在屏幕下方；在平板模式下，则可以自动适配为左侧的侧边导航栏（NavigationRail），这是 Android 官方大屏适配规范（Material Design 3）的标准做法
 */
class AdaptiveLayoutActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_ExampleTheme {
                val windowSizeClass = calculateWindowSizeClass(activity = this)
                AdaptiveApp(widthSizeClass = windowSizeClass.widthSizeClass)
            }
        }
    }
}
// 导航菜单项枚举
enum class NavigationTab(val title: String, val icon: ImageVector) {
    HOME("首页", Icons.Default.Home),
    FILES("文件", Icons.Default.Folder),
    SETTINGS("设置", Icons.Default.Settings)
}

// 模拟数据模型
data class ItemData(val id: Int, val title: String, val content: String)

val sampleItems = (1..20).map { id ->
    ItemData(
        id = id,
        title = "项目列表条目 #$id",
        content = "这是第 $id 项的详细内容数据。在平板上，点击项目后会在右侧直接刷新显示；在手机上，点击后会打开详情界面。"
    )
}

@Composable
fun AdaptiveApp(widthSizeClass: WindowWidthSizeClass) {
    // 当前选中的底部菜单 tab
    var currentTab by remember { mutableStateOf(NavigationTab.HOME) }
    // 选中的列表项
    var selectedItem by remember { mutableStateOf<ItemData?>(null) }

    // 判断是否是大屏设备（平板/桌面端）
    val isExpandedScreen = widthSizeClass == WindowWidthSizeClass.Expanded ||
            widthSizeClass == WindowWidthSizeClass.Medium

    Surface(modifier = Modifier.fillMaxSize()) {
        if (isExpandedScreen) {
            // ================= 平板模式：左侧侧边导航栏 (NavigationRail) + 右侧双栏 =================
            Row(modifier = Modifier.fillMaxSize()) {
                NavigationRail {
                    NavigationTab.entries.forEach { tab ->
                        NavigationRailItem(
                            selected = currentTab == tab,
                            onClick = {
                                currentTab = tab
                                selectedItem = null
                            },
                            icon = { Icon(tab.icon, contentDescription = tab.title) },
                            label = { Text(tab.title) }
                        )
                    }
                }

                VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)

                Box(modifier = Modifier.weight(1f)) {
                    TabContent(
                        tab = currentTab,
                        isExpandedScreen = true,
                        selectedItem = selectedItem ?: sampleItems.first(),
                        onItemSelect = { selectedItem = it },
                        onBack = { selectedItem = null }
                    )
                }
            }
        } else {
            // ================= 手机模式：内容区 + 底部导航栏 (NavigationBar) =================
            Scaffold(
                bottomBar = {
                    // 仅在主页面时显示底部导航栏，进入层级详情页时可自动隐藏
                    if (selectedItem == null) {
                        NavigationBar {
                            NavigationTab.entries.forEach { tab ->
                                NavigationBarItem(
                                    selected = currentTab == tab,
                                    onClick = {
                                        currentTab = tab
                                        selectedItem = null
                                    },
                                    icon = { Icon(tab.icon, contentDescription = tab.title) },
                                    label = { Text(tab.title) }
                                )
                            }
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    TabContent(
                        tab = currentTab,
                        isExpandedScreen = false,
                        selectedItem = selectedItem,
                        onItemSelect = { selectedItem = it },
                        onBack = { selectedItem = null }
                    )
                }
            }
        }
    }
}

/**
 * 根据当前选择的 Tab 分发显示不同的页面内容
 */
@Composable
fun TabContent(
    tab: NavigationTab,
    isExpandedScreen: Boolean,
    selectedItem: ItemData?,
    onItemSelect: (ItemData) -> Unit,
    onBack: () -> Unit
) {
    when (tab) {
        NavigationTab.HOME -> {
            if (isExpandedScreen) {
                // 平板模式：双栏
                TabletTwoPaneLayout(
                    items = sampleItems,
                    selectedItem = selectedItem ?: sampleItems.first(),
                    onItemSelect = onItemSelect
                )
            } else {
                // 手机模式：单栏
                PhoneSinglePaneLayout(
                    items = sampleItems,
                    selectedItem = selectedItem,
                    onItemSelect = onItemSelect,
                    onBack = onBack
                )
            }
        }
        NavigationTab.FILES -> SimplePage(title = "文件管理页面")
        NavigationTab.SETTINGS -> SimplePage(title = "应用设置页面")
    }
}

/**
 * 平板双栏界面
 */
@Composable
fun TabletTwoPaneLayout(
    items: List<ItemData>,
    selectedItem: ItemData,
    onItemSelect: (ItemData) -> Unit
) {
    Row(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.weight(0.35f)) {
            ItemList(items = items, selectedItem = selectedItem, onItemSelect = onItemSelect)
        }
        VerticalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.outlineVariant)
        Box(modifier = Modifier.weight(0.65f)) {
            ItemDetail(item = selectedItem)
        }
    }
}

/**
 * 手机单栏界面
 */
@Composable
fun PhoneSinglePaneLayout(
    items: List<ItemData>,
    selectedItem: ItemData?,
    onItemSelect: (ItemData) -> Unit,
    onBack: () -> Unit
) {
    if (selectedItem == null) {
        ItemList(items = items, selectedItem = null, onItemSelect = onItemSelect)
    } else {
        Scaffold(
            topBar = {
                @OptIn(ExperimentalMaterial3Api::class)
                TopAppBar(
                    title = { Text(selectedItem.title) },
                    navigationIcon = {
                        IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                )
            }
        ) { paddingValues ->
            Box(modifier = Modifier.padding(paddingValues)) {
                ItemDetail(item = selectedItem)
            }
        }
    }
}

@Composable
fun ItemList(
    items: List<ItemData>,
    selectedItem: ItemData?,
    onItemSelect: (ItemData) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(items, key = { it.id }) { item ->
            val isSelected = item.id == selectedItem?.id
            ListItem(
                headlineContent = {
                    Text(
                        text = item.title,
                        color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
                },
                modifier = Modifier.clickable { onItemSelect(item) }
            )
            HorizontalDivider(thickness = 0.5.dp)
        }
    }
}

@Composable
fun ItemDetail(item: ItemData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(text = item.title, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = item.content, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SimplePage(title: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(text = title, style = MaterialTheme.typography.headlineMedium)
    }
}