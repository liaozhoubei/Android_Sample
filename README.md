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

WebViewActivity     演示 android 调用 js 方法 以及 js 调用 android 方法

RichEditTextActivity    演示一个简单的富文本编辑器, 以及富文本如何点击

MediaStoreActivity  演示如何使用 MediaStore 获取图片

studytouch.StudyTouchActivity   演示 Android 触摸事件传递

multilanguage.MultiLanguage     演示如何动态更改国际语言

RecyclerviewActivity    演示 recyclerView 如何更改item间隔

MessengerActivity       演示如何 activity 与 service 使用 Messenger 通信的案例

