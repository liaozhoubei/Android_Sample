# Android_Example

一个简单的 android 示例工程

项目运行环境：

    android 30
    android studio 3.4
    androidx 依赖包

## 模块介绍

 - app     此模块是一般技术演示

 - taskdispatcher  是延迟加载技术的演示

 - changeSkin      是动态换肤的演示(更换背景色及布局)
     - nightSkin   是夜间模式，可将此文件生成的 skin 放置于自定义目录，此处为sd卡根目录
     - dayskin     是白天模式，生成的文件手动放在 asset/skins 中

 - makeJar         演示在 Android studio 中如何生成 jar 包

 - buildprotobuf   演示 google protobuf 的用法


## 源码介绍

 - bg_splash.xml       用于闪屏页时避免 app 冷启动时白屏

 - FileProvideActivity 演示如何使用 FileProvide 从相册、相机、裁剪中获取图片

 - FileListActivity    演示一个简单的文件浏览器，添加 Android 11 示例

 - InstallApkActivity  演示如何兼容 android 4.4 及 7.0 以后的应用内更新


    使用 appcompat 1.3.0 中的 Activity Result API 替代 startActivityForResult() ,ActivityResultLauncher 是一个回调处理一个结果，隐藏没有 requestCode 区分结果
    以下是目前所支持的 Contract 方法
    StartActivityForResult()
    StartIntentSenderForResult()
    RequestMultiplePermissions()
    RequestPermission()
    TakePicturePreview()
    TakePicture()
    TakeVideo()
    PickContact()
    GetContent()
    GetMultipleContents()
    OpenDocument()
    OpenMultipleDocuments()
    OpenDocumentTree()
    CreateDocument()

关于 xml/file_paths.xml 的路径说明


    <files-path name="*name*" path="*path*" />   
    对应的是：Context.getFileDir()的路径地址

    对应路径:Context.getFileDir()+"/${path}/"
    得到路径:content://${applicationId}/&{name}/

    <cache-path name="*name*" path="*path*" />  
    对应路径:Context.getCacheFir()+"/${path}/"
    得到路径:content://${applicationId}/&{name}/


    <external-path name="*name*" path="*path*" />  
    对应路径:Environment.getExternalStorageDirectory()+"/${path}/"
    得到路径:content://${applicationId}/&{name}/

    <external-files-path name="*name*" path="*path*" />  
    对应路径:Context.getExternalStorageDirectory()+"/${path}/"
    得到路径:content://${applicationId}/&{name}/

    <external-cache-path name="*name*" path="*path*" />   
    对应路径: Context.getExternalCacheDir()+"/${path}/"
    得到路径：content://${applicationId}/&{name}/

 > https://developer.android.google.cn/reference/androidx/core/content/FileProvider

 - CameraActivity      演示如何使用 Camera 1

    关于自动对焦的问题，如果相机设置对焦参数为 Camera.Parameters.FOCUS_MODE_AUTO , 那么在每次
    调用 Camera.autoFocus(AutoFocusCallback) 的时候就会对焦一次，因此要持续对焦则需要不断的调用 autoFocus 方法，否则后面就不在对焦
    
    Camera的对焦模式：
   
       + FOCUS_MODE_AUTO : 自动对焦模式，应用需要调用autoFocus(AutoFocusCallback)开始对焦，只会对焦一次，对焦成功会有回调。

       + FOCUS_MODE_INFINITY : 无穷对焦模式，应用很少，不能调用autoFocus(AutoFocusCallback)方法。

       +  FOCUS_MODE_MACRO : 特写镜头对焦模式，应用需要调用autoFocus(AutoFocusCallback)开始对焦

       +  FOCUS_MODE_FIXED : 定焦点模式，焦点不可调用时都是在这种模式，如果Camera能够自动对焦，这种模式会固定焦点，
    通常应用于超焦距对焦。这种模式不能调用autoFocus(AutoFocusCallback)。

       +  FOCUS_MODE_EDOF : 扩展景深模式

       +  FOCUS_MODE_CONTINUOUS_VIDEO : 连续自动对焦模式，主要用于录制视频过程中，Camera会不断地尝试聚焦，这是录制视频时
    对焦模式的最好选择，在设置了Camera的参数后就开始自动对焦，但是调用takePicture时不一定已经对焦完成。

       +  FOCUS_MODE_CONTINUOUS_PICTURE : 这种模式是对 FOCUS_MODE_CONTINUOUS_VIDEO连续自动对焦应用于拍照的扩展。
    Camera会不停的尝试连续对焦，对焦频率会比FOCUS_MODE_CONTINUOUS_VIDEO频繁，当设置了camera参数后开始对焦。
    注意如果想要重新开始自动聚焦，需要首先调用cancelAutoFocus，然后设置自动对焦模式，在调用autoFocus（AutoFocusCallback）
    该模式下可调用autoFocus(AutoFocusCallback)，如果当前正在对焦扫描，focus回调函数将在它完成对焦是回调；如果没有正在
    对焦扫描，将立即放回。autoFocus函数调用后对焦区域是固定的，如果应用想要重新开启自动连续对焦，需要首先调用cancelAutoFocus，
    重新开始预览无法开启自动连续对焦，需要重新调用autoFocus，如果想要停止自动连续对焦，应用可以修改对焦模式。
    FOCUS_MODE_AUTO，FOCUS_MODE_CONTINUOUS_VIDEO，FOCUS_MODE_CONTINUOUS_PICTURE通常较为常用。
    对焦的意义就是在手机晃动，移动或者改变位置时，拍摄画面依然清晰，如果不进行对焦则画面会很模糊


 - CameraXActivity     演示如何使用 CameraX , 使用 CameraX 必须是 android 5.1 以上，同时对与 Gradle classpath 插件会有要求
 
        unexpected element ＜queries＞ found in ＜manifest＞解决方案
        https://blog.csdn.net/xuling_1987/article/details/112631111

 - LiveDataActivity    演示jetpack 库中的 LiveData 的使用

 - PickerViewActivity  演示第三方库  Android-PickerView  的使用

 - RetrofitActivity    演示使用 Retrofit + 转换器 网络请求框架

    - 下载上传文件出现 OOM
    使用 Retrofit 下载文件时，切记要在接口方法中添加 @Streaming  注解，否则下载大文件会出现 OOM
    其次使用 okhttp3 下载文件时，添加的日志拦截器 HttpLoggingInterceptor 要将其级别设置为 HttpLoggingInterceptor.Level.NONE，
    因为使用 Level.BODY 级别，那么它会将请求的 body 保存在内存中而引起 OOM

 > When you use HttpLoggingInterceptor.Level.BODY , then you try to download large file , will save all body in memory for log.
That's easy let to OOM .

 >https://stackoverflow.com/questions/36643280/fatal-exception-java-lang-outofmemoryerror-using-okhttp3-okio-for-download-file

    关于 network_security_config
    google在 android P 为了安全起见,已经明确规定禁止http协议额,但是之前很多接口都是http协议，可新建 xml/network_security_config.xml 文件解决,
    这是因为应用程序的SDK高于或等于24，则只有系统证书才会被信任

    - 关于混淆
   使用 retrofit 请求网络的时候，若开启 minifyEnabled 进行代码压缩混淆就会出现网络请求失败的问题。这大概是因为 retrofit 和 gson 注解混淆之后，解析失败的问题，因此要在 proguard-rules.pro 中配置混淆规则，如下：


        # Retrofit
        -dontwarn retrofit2.**
        -keep class retrofit2.** { *; }
        -keepattributes Signature
        -keepattributes Exceptions

        # Gson
        -keep class com.google.gson.stream.** { *; }
        -keepattributes EnclosingMethod

        # Gson
        -keep class com.example.example.retrofit.**{*;} # 自定义数据模型的bean目录
   若想在 debug 下测试，需要将 minifyEnabled 设置为 true 即可，在不添加  proguard-rules.pro 情况下会发现请求网络信息没有数据返回


 - WebViewActivity     演示 android 调用 js 方法 以及 js 调用 android 方法

 - RichEditTextActivity    演示一个简单的富文本编辑器, 以及富文本如何点击

 - MediaStoreActivity  演示如何使用 MediaStore 获取图片

 - studytouch.StudyTouchActivity   演示 Android 触摸事件传递

 - multilanguage.MultiLanguage     演示如何动态更改国际语言

 - RecyclerviewActivity    演示 recyclerView 如何更改item间隔

 - MessengerActivity       演示如何 activity 与 service 使用 Messenger 通信的案例

 - SignalListenerActivity  演示如何获取手机信号强度，wifi信号强度，蓝牙连接状态的案例

    获取蓝牙连接状态需要权限：
            <uses-permission android:name="android.permission.BLUETOOTH" />
            <uses-permission android:name="android.permission.BLUETOOTH_ADMIN" />
    获取wifi信号强度需要权限：
            <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
            <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />

## 去除重复依赖

打包时若出现重复依赖，可以使用　double shift　快捷键　search everywhere，这时所有相关信息就会出现了，然后在相关的依赖中设置　exclude 即可

如：

    implementation ('com.squareup.retrofit2:converter-simplexml:2.7.1'){
        // 不包含　xpp3 依赖，否则会有重复引用的错误，导致无法编译 release 包
        exclude group: 'xpp3', module: 'xpp3'
    }

exclude 中 group 和 module 的区别如下：

maven格式是：

    <groupID>:<artifactID>:<version>

module就是artifactID

    exclude module: "cardview-v7"

意思就是排除所有artifactID为cardview-v7的库

不管是 com.support.android:cardview-v7:1.0.0 还是别的什么 com.meitu:cardview-v7:2.1.1 都排除

    exclude group:"com.google.zxing", modul:"core"

就是排除 com.google.zxing:core:任何版本号 的库

但是可能有个 com.meitu:zxing:1.11.0 就不会被排除

## 常用路径

    Context.getFilesDir()=/data/data/com.example.example/files
    Context.getCacheDir()=/data/data/com.example.example/cache
    Environment.getExternalStorageDirectory()=/storage/emulated/0
    getExternalFilesDir(Environment.DIRECTORY_PICTURES)=/storage/emulated/0/Android/data/com.example.example/files/Pictures
    Context.getExternalFilesDir(null)=/storage/emulated/0/Android/data/com.example.example/files
    Context.getExternalCacheDir()=/storage/emulated/0/Android/data/com.example.example/cache


