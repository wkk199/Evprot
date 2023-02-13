# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

-keep class com.powercore.eviecharge.eviechargeljx.data.been.**{*;}
-keep class com.powercore.eviecharge.eviechargeljx.data.config.**{*;}


-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**


-keepattributes *Annotation*
-keepattributes Exceptions
-keepattributes InnerClasses
-keepattributes Signature
-keepattributes SourceFile,LineNumberTable


-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public class * extends com.bumptech.glide.module.AppGlideModule
-keep public enum com.bumptech.glide.load.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}


-keep class com.google.zxing.{*; }
-dontwarn com.google.zxing.
-dontwarn cn.bingoogolapple.**
-keep class cn.bingoogolapple.*{*;}

-keep class com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout.**{*;}
-dontwarn com.guanaj.**
-keep  class com.guanaj.**{*;}

-keepclassmembers class com.guanaj.easyswipemenulibrary.EasySwipeMenuLayout {
    #这个是具体方法
    private void handlerSwipeMenu(State);

}
