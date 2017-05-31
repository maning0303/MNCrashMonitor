# CrashMonitor
Debug监听程序崩溃日志,直接页面展示崩溃日志列表，方便自己平时调试。
[![](https://jitpack.io/v/maning0303/CrashMonitor.svg)](https://jitpack.io/#maning0303/CrashMonitor)

## 截图

![](https://github.com/maning0303/CrashMonitor/raw/master/screenshots/crash_001.png)
![](https://github.com/maning0303/CrashMonitor/raw/master/screenshots/crash_002.png)
![](https://github.com/maning0303/CrashMonitor/raw/master/screenshots/crash_003.png)
![](https://github.com/maning0303/CrashMonitor/raw/master/screenshots/crash_004.png)
![](https://github.com/maning0303/CrashMonitor/raw/master/screenshots/crash_005.png)


## 如何添加
   ### Gradle添加：
   #### 1.在Project的build.gradle中添加仓库地址

   ``` gradle
   	allprojects {
   		repositories {
   			...
   			maven { url "https://jitpack.io" }
   		}
   	}
   ```

   #### 2.在app目录下的build.gradle中添加依赖
   ``` gradle
   	dependencies {
   	     compile 'com.github.maning0303:CrashMonitor:V1.0.2'
   	}
   ```

## 使用方法:
### 1:Application 的 onCreate()方法 初始化：

``` java

     /**
      * 初始化日志系统
      * context :    上下文
      * isDebug :    是不是Debug模式,true:崩溃后显示自定义崩溃页面 ;false:关闭应用,不跳转奔溃页面(默认)
      */
    MCrashMonitor.init(this, true);

```

### 2:文件的位置:
``` java

    /Android/data/包名/cache/crashLogs/

```


### 喜欢就Star一下吧!

### 注意:
当应用已启动就崩溃的无法打开页面,直接看通知或者去文件夹里面查看:/Android/data/包名/cache/crashLogs/

## 感谢:
#### 内部使用了一些三方库文件:
##### [StatusBarUtil](https://github.com/laobie/StatusBarUtil)
##### [NotifyUtil](https://github.com/wenmingvs/NotifyUtil)

