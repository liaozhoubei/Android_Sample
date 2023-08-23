# android studio 生成 jar 包

 ## 创建 android library 的 module
 ### 修改 module 包的 build.gradle
 
#### 添加 afterEvaluate 块
用于编译时不生成 BuildConfig.java 文件，这样打包时没有 BuildConfig.class
    

    afterEvaluate {
        generateReleaseBuildConfig.enabled = false
        generateDebugBuildConfig.enabled = false
    }
    

#### 设置打包时需要添加的其他文件
jar包可能需要一些资源文件，如 assets 目录下的文件，在这里添加进入，并重打包， 将以下代码添加至 module 的 build.gradle 中与 android 块平级的地方
    

    def SDK_BASENAME = "myjar";
    def SDK_VERSION = "_v1.0.0";
    def sdkDestinationPath = "build/outputs/jar/";
    // 文件目录不固定(不同 android studio 版本不一样)，需要从 build 中查找(需要先 assemble )
    //def zipFile = file('build/intermediates/bundles/debug/classes.jar')
    def zipFile = file('build/intermediates/packaged-classes/release/classes.jar')

    task deleteBuild(type: Delete) {
        delete sdkDestinationPath + SDK_BASENAME + SDK_VERSION + ".jar"
    }
    // 从 build 目录中取出 jar 包，然后添加 assets 文件重新打包
    task makeJar(type: Jar) {
        from zipTree(zipFile)
        from fileTree(dir: 'src/main', includes: ['assets/**']) // 打包assets目录下的所有文件
        baseName = SDK_BASENAME + SDK_VERSION
        destinationDir = file(sdkDestinationPath)
    }

    makeJar.dependsOn(deleteBuild, build)    

## 总结
实际上 android studio 在编译的时候会把 src 目录下的源码打成 jar 包，如果不需要其他的文件，那么直接就可以拿来用，只是这个文件一般在 build/intermediates 目录下，需要仔细查找一番，而且不同 android studio / gradle 生成的目录还不一样！这要重点关注！
