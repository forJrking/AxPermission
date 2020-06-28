# AxPermission
一个轻量级的AOP(Android)应用框架

## 特点

* 动态申请权限切片`@Permission`可以在任意类的方法中发起权限请求
* 添加`@PermissionDenied`拦截拒绝权限后回调（反射）
* 兼容Kotlin语法 注意使用新版插件 aspectjx。

## 2、如何使用
目前支持主流开发工具AndroidStudio的使用，直接配置build.gradle，增加依赖即可.

### 2.1、Android Studio导入方法，添加Gradle依赖

1.先在项目根目录的 build.gradle 的 repositories 添加:
```groovy
allprojects {
    repositories {
        ...
        maven { url "http://" }
    }
}
```

2.再在项目根目录的 build.gradle 的 dependencies 添加aop插件：

```groovy
buildscript {
    ···
    dependencies {
        ··· 支持kotlin和字节码 打包时编织
        //classpath 'com.hujiang.aspectjx:gradle-android-plugin-aspectjx:2.0.10'
    }
}
```

3.在项目的 build.gradle 中增加依赖并引用xaop插件

```groovy
apply plugin: 'aspectjx' //引用aop插件

dependencies {
    ···
    //仅支持androidx，如需support自行修改
    //implementation 'com.forjrking.permission:permission:1.0.0'
}
```


### 2.2、在Application中进行初始化

```java

//AxPermission.init(this); //初始化插件
AxPermission.setIsdebug(true); //日志打印切片开启
//设置动态申请权限切片 申请权限被拒绝的事件全局响应监听 可以不申请
AxPermission.setListener(new RequestPermListener() {
    @Override
    public void onPermissionsDenied(String... perms) {
        //部分成功
    }

    @Override
    public void onAllPermissionsGranted() {
        //成功
    }

    @Override
    public void onPermissionsGranted(String... perms) {
     //拒绝的权限
    }
});
```

## 3、动态申请权限使用

1.使用`@Permission`标注需要申请权限执行的方法。可设置申请一个或多个权限,支持 Constant 提供的权限组和 Manifest.permission.xxx。

```
@Permission({Constant.G_CAMERA,Constant.G_CALENDAR, Manifest.permission.READ_EXTERNAL_STORAGE})
private void handleRequestPermission() {
    Toast.makeText(this, "权限同意", Toast.LENGTH_SHORT).show();
}
```
2.选用（可以不使用）使用`@PermDenied`标注的方法，会回调被拒绝权限，支持过滤器，默认全部都支持，如下只接受 CAMERA或者SMS 拒绝的权限。
```
@PermissionDenied({Constant.G_CAMERA,Constant.G_SMS})
private void permDenied(String[] strings) {
    Toast.makeText(this, "拒绝" + Arrays.toString(strings), Toast.LENGTH_SHORT).show();
}
```


## 4、混淆配置

```
引入后自带混淆配置
```
