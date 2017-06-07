# AndroidRequest

  基于retrofit和okhttp封装的网络库，用户不需要了解网络底层实现，通过简单的设置就能实现网络请求。

### 如何使用（How to ues）

第一步 初始化

```jsva
    RequestAgent.showLog();
    RequestAgent.init(getApplicationContext(), "https://api.github.com/");
```

第二步 使用

```java
 new RequestBuilder().path("users/jingle1267/repos")
    .header("head1", "head1Value")
    .param("param1", "param1Value")
    .success(new Success() {
        @Override
        public void onSuccess(String model) {

            Log.e("MainActivity", "111");
            mResponse.setText(model);
        }
    })
    .error(new Error() {
        @Override
        public void onError(int statusCode, String errorMessage, Throwable t) {
            Log.e("MainActivity", "222");
            mResponse.setText(errorMessage);
        }
    })
    .type("get")
    .build();
```

设置公共参数

```java
    RequestAgent.addHeader("Cookie", "123456");
    RequestAgent.addParam("publicKey", "publicValue");
```

### 如何引用 (How to install)

```xml
dependencies {
    compile 'com.ihongqiqu:android-utils:1.0.2'
}
```

```xml
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/ihongqiqu/maven"
        }
    }
```

### 混淆 (Proguard)

  代码混淆只需要在 Proguard 规则文件中添加如下代码即可( Eclipse 下为 proguard.cfg 文件)：

```xml
-keep class com.ihongqiqu.** { *; }
-keepclassmembers class com.ihongqiqu.** { *; }
-dontwarn com.ihongqiqu.**
```
