# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/tommytao/adt-bundle-mac-x86_64/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# ---------------------

-dontwarn com.google.android.gms.**
-dontwarn org.apache.**
-dontwarn com.squareup.okhttp.**
-dontwarn sun.misc.Unsafe
-dontwarn rx.**
-dontwarn okio.**
-dontwarn com.google.appengine.api.urlfetch.**
-dontwarn butterknife.internal.**
-dontwarn retrofit.**
-dontwarn com.android.volley.**
-dontwarn com.google.ads.conversiontracking.**
-dontwarn kotlin.**
-dontwarn org.w3c.dom.events.*
-dontwarn com.paypal.android.sdk.**

-keep public class com.google.android.gms.* { public *; }

# EventBus related methods
-keepclassmembers class ** {
    public void onEvent*(**);
}

# Facebook related methods
-keep class com.facebook.** { *; }

# Only required if you use AsyncExecutor
-keepclassmembers class * extends de.greenrobot.event.util.ThrowableFailureEvent {
    public <init>(java.lang.Throwable);
}

-keepclassmembers class com.easyvan.app.activity.history.pickup.fragment.PickupHistorySubDetailFragment$SubpageWebInterface {
   public *;
}

-keepattributes SourceFile,LineNumberTable

# Gson uses generic type information stored in a class file when working with fields. Proguard
# removes such information by default, so configure it to keep all of it.
-keepattributes Signature

# For using GSON @Expose annotation
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keepattributes InnerClasses

# Gson specific classes
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

# Add any classes the interact with gson
-keep class com.easyvan.app.data.schema.** { *; }
-keep class com.easyvan.app.util.location.** {*; }
-keep class com.easyvan.app.data.remote.plugs.** {*; }
-keep class com.easyvan.app.data.remote.annotation.** {*; }
-keep class com.easyvan.ext.library.** {*; }

# Volley
-keep class org.apache.*
-keep class org.apache.commons.logging.**

-keep class com.volley.** { *; }

# Retrofit
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.** *;
}
-keepclassmembers class * {
    @retrofit.** *;
}

#ButterKnife
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }

-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}

-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}

-keep class **.R
-keep class **.R$* {
    <fields>;
}

-keepclassmembers class * {
    @fully.qualified.package.AnnotationType *;
}

-keep class * extends java.util.ListResourceBundle {
    protected Object[][] getContents();
}

#To maintain custom components names that are used on layouts XML:
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
    public void set*(...);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembers class * {
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# Preserve the special static methods that are required in all enumeration classes.
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

-keepnames @com.google.android.gms.common.annotation.KeepName class *
-keepclassmembernames class * {
    @com.google.android.gms.common.annotation.KeepName *;
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# Remove all logs
-assumenosideeffects class android.util.Log {
    public static boolean isLoggable(java.lang.String, int);
    public static *** d(...);
    public static *** v(...);
    public static *** i(...);
    public static *** w(...);
    public static *** e(...);
    public static *** wtf(...);
}
