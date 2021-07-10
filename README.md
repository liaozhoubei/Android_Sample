# Android_Example

一个简单的 android 示例工程

项目运行环境：

    android 28
    android studio 3.4
    androidx 依赖包

## 模块介绍

app     此模块是一般技术演示
taskdispatcher  是延迟加载技术的演示
changeSkin      是动态换肤的演示(更换背景色及布局)
    nightSkin   是夜间模式，可将此文件生成的 skin 放置于自定义目录，此处为sd卡根目录
    dayskin     是白天模式，生成的文件手动放在 asset/skins 中
makeJar         演示在 Android studio 中如何生成 jar 包
buildprotobuf   演示 google protobuf 的用法


## 源码介绍

bg_splash.xml       用于闪屏页时避免 app 冷启动时白屏

FileProvideActivity 演示如何使用 FileProvide 从相册、相机、裁剪中获取图片

FileListActivity    演示一个简单的文件浏览器

InstallApkActivity  演示如何兼容 android 4.4 及 7.0 以后的应用内更新

LiveDataActivity    演示jetpack 库中的 LiveData 的使用

PickerViewActivity  演示第三方库  Android-PickerView  的使用

RetrofitActivity    演示使用 Retrofit + 转换器 网络请求框架

    使用 Retrofit 下载文件时，切记要在接口方法中添加 @Streaming  注解，否则下载大文件会出现 OOM
    其次使用 okhttp3 下载文件时，添加的日志拦截器 HttpLoggingInterceptor 要将其级别设置为 HttpLoggingInterceptor.Level.NONE，
    因为使用 Level.BODY 级别，那么它会将请求的 body 保存在内存中而引起 OOM

 > When you use HttpLoggingInterceptor.Level.BODY , then you try to download large file , will save all body in memory for log.
That's easy let to OOM .

 >https://stackoverflow.com/questions/36643280/fatal-exception-java-lang-outofmemoryerror-using-okhttp3-okio-for-download-file

    关于 network_security_config
    google在 android P 为了安全起见,已经明确规定禁止http协议额,但是之前很多接口都是http协议，可新建 xml/network_security_config.xml 文件解决,
    这是因为应用程序的SDK高于或等于24，则只有系统证书才会被信任

WebViewActivity     演示 android 调用 js 方法 以及 js 调用 android 方法

RichEditTextActivity    演示一个简单的富文本编辑器, 以及富文本如何点击

MediaStoreActivity  演示如何使用 MediaStore 获取图片

studytouch.StudyTouchActivity   演示 Android 触摸事件传递

multilanguage.MultiLanguage     演示如何动态更改国际语言

RecyclerviewActivity    演示 recyclerView 如何更改item间隔

MessengerActivity       演示如何 activity 与 service 使用 Messenger 通信的案例

SignalListenerActivity  演示如何获取手机信号强度，wifi信号强度，蓝牙连接状态的案例

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


