# CrashMonitor
Debug监听程序崩溃日志,直接页面展示崩溃日志列表，方便自己平时调试。

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
   	     compile 'com.github.maning0303:CrashMonitor:V1.0.0'
   	}
   ```

## 使用方法:
### 1:Application 的 onCreate()方法 初始化：

``` java

    /** 初始化
     *  context : 上下文
     *  isDebug : 是不是Debug状态,true:使用库,false:不会拦截崩溃
     */
    MCrashMonitor.init(this, true);

```

### 2:文件的位置:
``` java

    /Android/data/包名/cache/crashLogs/

```


### 喜欢就Star一下吧!


## 感谢:
### 内部使用了一些三方库文件:
##### [StatusBarUtil](https://github.com/laobie/StatusBarUtil)
##### [NotifyUtil](https://github.com/wenmingvs/NotifyUtil)

