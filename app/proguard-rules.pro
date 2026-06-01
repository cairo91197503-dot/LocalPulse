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

# ---- Regras LocalPulse ----

# Manter ViewModels
-keep class com.example.ui.viewmodel.** { *; }
-keepclassmembers class com.example.ui.viewmodel.** { *; }

# Manter modelos de dados (Room + Serialization)
-keep class com.example.data.models.** { *; }
-keep class com.example.data.db.** { *; }
-keep class com.example.data.repository.** { *; }
-keep class com.example.data.api.** { *; }

# Kotlin Serialization
-keepattributes Signature
-keepattributes *Annotation*
-keep class kotlinx.serialization.** { *; }
-keepclassmembers class kotlinx.serialization.** { *; }
-keepclasseswithmembers class * {
    @kotlinx.serialization.Serializable <fields>;
}

# Coroutines
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}

# OkHttp (usado pelo GeminiApiClient)
-dontwarn okhttp3.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# Compose
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# Manter campos anotados com @Keep
-keep @androidx.annotation.Keep class * { *; }
