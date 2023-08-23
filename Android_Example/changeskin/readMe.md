# 关于 android-skin-support 的使用

 - 若要使用插件化换肤，记得要申请读取存储卡权限
 - 应用中使用的 androidx.appcompat:appcompat 依赖包版本要与 android-skin-support 的版本一致，此处为 1.1.0 ,这是因为 android-skin-support 中有对此依赖包的强引用，不保持一致会报空指针
 - 加载 SD 卡中的插件首要要自定义 CustomSDCardLoader 这个类，重写此类中的 getSkinPath() 方法来自定义路径
 - 对于资源包要将所有的依赖去除，否则会造成依赖包过大的问题
 - 在 android 29 以上无法使用sd卡更新资源，因为 PackageManager.getPackageArchiveInfo 返回为空,
 这是因为存储权限问题，需要将文件保存在专门路径即可
 - 注意，无论是从 asset 目录或者 sd 卡中加载插件资源，都要注意文件有没有放错位置，路径有没有写错。
        - 使用 asset 则要放在 asset/skins 目录中
        - 使用 sd 卡则要自己选择目录