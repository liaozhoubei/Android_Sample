# 使用Google Protobuf 要点

本项目使用的 backup.proto 协议是 android 4.4(大概) 的 Launcher3 源码需要的协议文件

1. 在项目的 build.gradle 中添加插件 protobuf-gradle-plugin

在项目 build.gradle 的 buildscript 块下的 dependencies 添加插件，如下：

    buildscript {
        repositories {
            ...
        }
        dependencies {
            ...
            classpath 'com.google.protobuf:protobuf-gradle-plugin:0.8.8'
        }
    }

此处需要注意 protobuf-gradle-plugin 插件版本也许会因 android studio 版本而导致编译失败，若出现此现象需要考虑更改插件版本

2.在 app 的 build.gradle 开头配置插件：

    apply plugin: 'com.google.protobuf'

3.在 app 的 build.gradle 的 dependencies 中添加依赖库

    implementation 'com.google.protobuf.nano:protobuf-javanano:3.0.0-alpha-2'

4. 在 app 的 build.gradle 中配置protoc编译器

    protobuf {
        //这里配置protoc编译器
        protoc {
            artifact = 'com.google.protobuf:protoc:3.0.0-alpha-3'
             //这里配置生成目录，编译后会在build的目录下生成对应的java文件
            generateProtoTasks {
                all().each { task ->
                    task.builtins {
                        remove java
                        javanano {
                            option "java_package=launcher_log.proto|com.android.launcher3.userevent.nano"
                            option "enum_style=java"
                        }
                    }
                }
            }
        }
    }

5. 此时就可以在与src/main/java同位置的proto目录写协议文件*.proto了

6. 重新编译

编译成功后会在 build/generated/source 中生成 proto 目录，里面有使用 protobuf 协议的 java 文件

需要谨慎注意，不同的 protobuf 版本生成的 java 文件是不一样的，会有不同的方法

PS: 本目录下的 TestProtol.zip 是 launcher3 使用 protobuf 生成 java 文件的正确案例 

> Android Stduio中配置和使用Google Protobuf
> https://zhangphil.blog.csdn.net/article/details/89418594

> 旧机宝开发笔记之：Android Studio集成protobuf
> https://blog.csdn.net/zfan93/article/details/102918872
