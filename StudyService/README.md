# StudyService
学习android service以及aidl中跨进程回调activity

博文：

- 浅析Android服务中的startService和bindService

https://www.jianshu.com/p/0dfc6e9d82b3 

- AIDL跨进程通信的使用

https://www.jianshu.com/p/5789c59095cb

- AIDL之service回调Activity

https://www.jianshu.com/p/c7407e532307

 - StartAndBindServiceActivity

演示如何使用 startService 和 BindService 两种打开服务的方法

 - SecondActiivty

演示 Service 如何回调 activity ,多个 activity 如何绑定一个 Service

 - AIDLClientActivity

演示 Service 如何跨进程传输对象，使用此功能时先编译安装 aidlservice 模块

 - MessengerActivity

演示 Messenger 如何在跨进程中使用。

注意 Message.obj 在跨进程中只支持经过 Parcelable 序列化的对象，其他对象无法传递(基础数据类型也一样)