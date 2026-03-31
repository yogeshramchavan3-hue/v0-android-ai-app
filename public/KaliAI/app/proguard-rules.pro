# Kali AI ProGuard Rules

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }

# JSON
-keep class org.json.** { *; }

# Keep model classes
-keep class com.kali.ai.ChatMessage { *; }
