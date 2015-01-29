# DynamicLoaderDEX
DynamicLoaderDEX项目希望能帮助到广大的Android开发者运用到自己钟爱的项目中。

#背景
1. Android中每个dex有最多65536方法数的限制；
2. 在Android的新版本中已经做了改善，但是兼容老版本需要做处理；
3. 项目的功能不断增加，方法数，以及jar的数量增加；

#解决方案
1. 使用ProGuard；
2. 使用多个dex，动态反射加载；
3. 通过JNI替换记录方法信息的缓存（Facebook），这个是从根本上解决问题，但是需要兼容各大手机厂商，难度很大；

#dex动态加载
dx命令(dex) --- DexClassLoader加载dex --- 反射调用

#dx命令使用
1. 配置环境变量：Androidsdk中的sdk\build-tools\19.0.2中的dx.bat
2. dx --dex --output="地址一" “地址二”：
	a. 地址一是输出的文件;b. 地址二是源jar包地址 可以用*.jar这样的形式处理目录里的所有文件;

#DexClassLoader加载dex
1. com.frank.dynamicloaderdex.proxy包中ExlibAsyncLoader、Module、ModuleLoader、MultiDex是动态加载的框架；
2. com.frank.dynamicloaderdex.proxy.gson是需要通过dex动态加载反射的方法和类；
3. BaseApplication中attachBaseContext()方法中进行同步加载dex，也可以在这里进行异步加载dex；
4. 在AndroidManifest.xml中Application中的指定自己的application，这样动态的dex才可以被加载；


#统计项目中方法数工具
https://github.com/mihaip/dex-method-counts

#背景参考文献
1. https://medium.com/@rotxed/dex-skys-the-limit-no-65k-methods-is-28e6cb40cf71
2. http://blog.csdn.net/ddaitest/article/details/21020511
3. http://viila.info/2014/04/android-2-3-dex-max-function-problem/

#动态加载参考文献
http://android-developers.blogspot.co.il/2011/07/custom-class-loading-in-dalvik.html?m=1

#dx命令参考文献
http://www.cnblogs.com/wujd/archive/2012/01/10/wujiandong-android-1.html




