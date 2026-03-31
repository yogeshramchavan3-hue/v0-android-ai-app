// Kali AI - Complete Android Project Files
export interface ProjectFile {
  path: string
  content: string
  language: string
}

export const kaliAIProject: ProjectFile[] = [
  // 1. Project Level build.gradle
  {
    path: "build.gradle",
    language: "gradle",
    content: `// Root build.gradle
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:8.2.0'
    }
}

task clean(type: Delete) {
    delete rootProject.buildDir
}`
  },
  // 2. settings.gradle
  {
    path: "settings.gradle",
    language: "gradle",
    content: `pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "KaliAI"
include ':app'`
  },
  // 3. gradle.properties
  {
    path: "gradle.properties",
    language: "properties",
    content: `org.gradle.jvmargs=-Xmx2048m -Dfile.encoding=UTF-8
android.useAndroidX=true
android.enableJetifier=true
android.nonTransitiveRClass=false
android.nonFinalResIds=false`
  },
  // 4. app/build.gradle
  {
    path: "app/build.gradle",
    language: "gradle",
    content: `plugins {
    id 'com.android.application'
}

android {
    namespace 'com.kali.ai'
    compileSdk 34

    defaultConfig {
        applicationId "com.kali.ai"
        minSdk 23
        targetSdk 34
        versionCode 1
        versionName "1.0"
    }

    buildTypes {
        release {
            minifyEnabled false
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
        }
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_1_8
        targetCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'
    implementation 'com.google.android.material:material:1.11.0'
    implementation 'androidx.cardview:cardview:1.0.0'
    implementation 'androidx.recyclerview:recyclerview:1.3.1'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'org.json:json:20230227'
}`
  },
  // 5. proguard-rules.pro
  {
    path: "app/proguard-rules.pro",
    language: "proguard",
    content: `# Add project specific ProGuard rules here.
-keep class com.kali.ai.** { *; }
-keep class org.json.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.**`
  },
  // 6. AndroidManifest.xml
  {
    path: "app/src/main/AndroidManifest.xml",
    language: "xml",
    content: `<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    package="com.kali.ai">

    <uses-permission android:name="android.permission.RECORD_AUDIO" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
    <uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />
    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" android:maxSdkVersion="32" />
    <uses-permission android:name="android.permission.MANAGE_EXTERNAL_STORAGE" tools:ignore="ScopedStorage" />
    <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED" />
    <uses-permission android:name="android.permission.WAKE_LOCK" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.READ_CONTACTS" />
    <uses-permission android:name="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE" />

    <application
        android:allowBackup="true"
        android:label="@string/app_name"
        android:theme="@style/Theme.AppCompat.Light.DarkActionBar"
        android:requestLegacyExternalStorage="true"
        android:usesCleartextTraffic="true"
        android:icon="@android:drawable/ic_menu_call"
        android:roundIcon="@android:drawable/ic_menu_call">

        <activity
            android:name=".MainActivity"
            android:exported="true"
            android:launchMode="singleTop"
            android:screenOrientation="portrait">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <activity
            android:name=".SettingsActivity"
            android:exported="false"
            android:parentActivityName=".MainActivity" />

        <activity
            android:name=".ChatHistoryActivity"
            android:exported="false"
            android:parentActivityName=".SettingsActivity" />

        <service
            android:name=".KaliNotificationListener"
            android:label="Kali Auto-Reply"
            android:permission="android.permission.BIND_NOTIFICATION_LISTENER_SERVICE"
            android:exported="true">
            <intent-filter>
                <action android:name="android.service.notification.NotificationListenerService" />
            </intent-filter>
        </service>

        <service
            android:name=".VoskWakeWordService"
            android:foregroundServiceType="microphone"
            android:exported="false" />

        <receiver
            android:name=".BootReceiver"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.BOOT_COMPLETED" />
            </intent-filter>
        </receiver>

    </application>
</manifest>`
  },
  // 7. strings.xml
  {
    path: "app/src/main/res/values/strings.xml",
    language: "xml",
    content: `<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="app_name">Kali AI</string>
    <string-array name="services">
        <item>DeepSeek</item>
        <item>GPT</item>
        <item>Gemini</item>
        <item>Grok</item>
    </string-array>
</resources>`
  },
  // 8. activity_main.xml
  {
    path: "app/src/main/res/layout/activity_main.xml",
    language: "xml",
    content: `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="#F5F5F5">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Kali AI"
        android:textSize="24sp"
        android:textStyle="bold"
        android:textColor="#9C27B0"
        android:layout_marginBottom="16dp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginBottom="8dp">

        <Switch
            android:id="@+id/swMic"
            android:text="Mic"
            android:layout_weight="1"
            android:checked="true"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />

        <Switch
            android:id="@+id/swAutoReply"
            android:text="Auto"
            android:layout_weight="1"
            android:checked="false"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />

        <Switch
            android:id="@+id/swTyping"
            android:text="Type"
            android:layout_weight="1"
            android:checked="true"
            android:layout_width="0dp"
            android:layout_height="wrap_content" />
    </LinearLayout>

    <EditText
        android:id="@+id/etManual"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:hint="Type here or say Kali..."
        android:inputType="textMultiLine"
        android:background="#FFFFFF"
        android:padding="12dp"
        android:minHeight="48dp"
        android:layout_marginBottom="8dp" />

    <Button
        android:id="@+id/btnSend"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:text="Send"
        android:backgroundTint="#4CAF50"
        android:layout_marginBottom="8dp" />

    <TextView
        android:id="@+id/tvStatus"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Ready"
        android:textSize="14sp"
        android:textColor="#666"
        android:layout_marginBottom="8dp" />

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Response:"
        android:textStyle="bold"
        android:layout_marginBottom="4dp" />

    <TextView
        android:id="@+id/tvResponse"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:background="#E0E0E0"
        android:padding="12dp"
        android:scrollbars="vertical"
        android:textSize="16sp" />

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:layout_marginTop="8dp">

        <Button
            android:id="@+id/btnSettings"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Settings"
            android:backgroundTint="#9C27B0"
            android:layout_marginEnd="4dp" />

        <Button
            android:id="@+id/btnHistory"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="History"
            android:backgroundTint="#FF9800"
            android:layout_marginStart="4dp" />
    </LinearLayout>

</LinearLayout>`
  },
  // 9. activity_settings.xml
  {
    path: "app/src/main/res/layout/activity_settings.xml",
    language: "xml",
    content: `<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:padding="16dp"
    android:background="#F5F5F5">

    <LinearLayout
        android:orientation="vertical"
        android:layout_width="match_parent"
        android:layout_height="wrap_content">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="API Keys Manager"
            android:textSize="18sp"
            android:textStyle="bold"
            android:layout_marginTop="8dp"
            android:layout_marginBottom="8dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Select Service:"
            android:textSize="14sp" />

        <Spinner
            android:id="@+id/spinnerService"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="8dp" />

        <EditText
            android:id="@+id/etKey"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:hint="Enter API Key (sk-...)"
            android:inputType="textPassword"
            android:layout_marginBottom="8dp" />

        <LinearLayout
            android:orientation="horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="8dp">

            <Button
                android:id="@+id/btnAddKey"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Add"
                android:backgroundTint="#4CAF50"
                android:layout_marginEnd="4dp" />

            <Button
                android:id="@+id/btnRemoveKey"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:text="Remove"
                android:backgroundTint="#F44336"
                android:layout_marginStart="4dp" />
        </LinearLayout>

        <ListView
            android:id="@+id/lvKeys"
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:choiceMode="singleChoice"
            android:background="#FFFFFF"
            android:layout_marginBottom="16dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Similarity Threshold:"
            android:textSize="14sp"
            android:layout_marginBottom="4dp" />

        <SeekBar
            android:id="@+id/seekThreshold"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:max="100"
            android:progress="75"
            android:layout_marginBottom="16dp" />

        <TextView
            android:id="@+id/tvThresholdValue"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="75%"
            android:textSize="12sp"
            android:textColor="#666"
            android:layout_marginBottom="16dp" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Auto-Reply Contacts"
            android:textSize="18sp"
            android:textStyle="bold"
            android:layout_marginTop="8dp"
            android:layout_marginBottom="8dp" />

        <LinearLayout
            android:orientation="horizontal"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginBottom="8dp">

            <EditText
                android:id="@+id/etContactSender"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:hint="Sender name"
                android:layout_marginEnd="4dp" />

            <EditText
                android:id="@+id/etContactApp"
                android:layout_width="0dp"
                android:layout_height="wrap_content"
                android:layout_weight="1"
                android:hint="App (e.g., com.whatsapp)"
                android:layout_marginStart="4dp" />
        </LinearLayout>

        <Button
            android:id="@+id/btnAddContact"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Add Contact"
            android:backgroundTint="#2196F3"
            android:layout_marginBottom="8dp" />

        <Button
            android:id="@+id/btnRemoveContact"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Remove Selected"
            android:backgroundTint="#F44336"
            android:layout_marginBottom="8dp" />

        <Button
            android:id="@+id/btnAllowAll"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Allow All Contacts"
            android:backgroundTint="#9C27B0"
            android:layout_marginBottom="8dp" />

        <ListView
            android:id="@+id/lvContacts"
            android:layout_width="match_parent"
            android:layout_height="120dp"
            android:choiceMode="singleChoice"
            android:background="#FFFFFF" />

    </LinearLayout>
</ScrollView>`
  },
  // 10. activity_chat_history.xml
  {
    path: "app/src/main/res/layout/activity_chat_history.xml",
    language: "xml",
    content: `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp"
    android:background="#F5F5F5">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="Chat History"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <ListView
        android:id="@+id/lvChats"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1"
        android:divider="#CCCCCC"
        android:dividerHeight="1dp"
        android:background="#FFFFFF" />

    <LinearLayout
        android:orientation="horizontal"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp">

        <Button
            android:id="@+id/btnExport"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Export"
            android:backgroundTint="#4CAF50"
            android:layout_marginEnd="4dp" />

        <Button
            android:id="@+id/btnImport"
            android:layout_width="0dp"
            android:layout_height="wrap_content"
            android:layout_weight="1"
            android:text="Import"
            android:backgroundTint="#2196F3"
            android:layout_marginStart="4dp" />
    </LinearLayout>

</LinearLayout>`
  },
  // 11. item_chat.xml
  {
    path: "app/src/main/res/layout/item_chat.xml",
    language: "xml",
    content: `<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp"
    android:background="?android:attr/selectableItemBackground">

    <TextView
        android:id="@+id/tvHeader"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textStyle="bold"
        android:textSize="14sp"
        android:textColor="#666"
        android:layout_marginBottom="4dp" />

    <TextView
        android:id="@+id/tvMessage"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:textSize="16sp"
        android:layout_marginBottom="4dp" />

    <TextView
        android:id="@+id/tvTimestamp"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="12sp"
        android:textColor="#999" />

</LinearLayout>`
  },
  // 12. Utils.java
  {
    path: "app/src/main/java/com/kali/ai/Utils.java",
    language: "java",
    content: `package com.kali.ai;

public class Utils {

    public static int levenshteinDistance(String s1, String s2) {
        if (s1 == null || s2 == null) return Integer.MAX_VALUE;
        int m = s1.length();
        int n = s2.length();
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 0; i <= m; i++) dp[i][0] = i;
        for (int j = 0; j <= n; j++) dp[0][j] = j;
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1), dp[i - 1][j - 1] + cost);
            }
        }
        return dp[m][n];
    }

    public static int similarity(String s1, String s2) {
        if (s1 == null || s2 == null) return 0;
        s1 = s1.toLowerCase().trim();
        s2 = s2.toLowerCase().trim();
        int maxLen = Math.max(s1.length(), s2.length());
        if (maxLen == 0) return 100;
        int dist = levenshteinDistance(s1, s2);
        return (int) ((1.0 - (double) dist / maxLen) * 100);
    }

    public static String formatTimestamp(long timestamp) {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date(timestamp));
    }
}`
  },
  // 13. ApiConfig.java
  {
    path: "app/src/main/java/com/kali/ai/ApiConfig.java",
    language: "java",
    content: `package com.kali.ai;

import android.content.Context;
import android.content.SharedPreferences;

public class ApiConfig {

    private static final String PREFS_NAME = "KaliAI_Secure";
    private static final String KEY_FLIRTING_MODE = "flirting_mode";
    private static final String KEY_AUTO_REPLY = "auto_reply_enabled";
    private static final String KEY_MIC_ENABLED = "mic_enabled";
    private static final String KEY_TYPING_ENABLED = "typing_enabled";
    private static final String KEY_WAKE_WORD_ONLY = "wake_word_only";
    private static final String KEY_SIMILARITY_THRESHOLD = "similarity_threshold";

    private final SharedPreferences prefs;
    private static ApiConfig instance;

    private ApiConfig(Context ctx) {
        this.prefs = ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized ApiConfig getInstance(Context ctx) {
        if (instance == null) {
            instance = new ApiConfig(ctx);
        }
        return instance;
    }

    public void setFlirtingMode(boolean enabled) {
        prefs.edit().putBoolean(KEY_FLIRTING_MODE, enabled).apply();
    }

    public boolean isFlirtingMode() {
        return prefs.getBoolean(KEY_FLIRTING_MODE, true);
    }

    public void setAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY, enabled).apply();
    }

    public boolean isAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY, false);
    }

    public void setMicEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_MIC_ENABLED, enabled).apply();
    }

    public boolean isMicEnabled() {
        return prefs.getBoolean(KEY_MIC_ENABLED, true);
    }

    public void setTypingEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_TYPING_ENABLED, enabled).apply();
    }

    public boolean isTypingEnabled() {
        return prefs.getBoolean(KEY_TYPING_ENABLED, true);
    }

    public void setWakeWordOnly(boolean enabled) {
        prefs.edit().putBoolean(KEY_WAKE_WORD_ONLY, enabled).apply();
    }

    public boolean isWakeWordOnly() {
        return prefs.getBoolean(KEY_WAKE_WORD_ONLY, true);
    }

    public void setSimilarityThreshold(int percent) {
        prefs.edit().putInt(KEY_SIMILARITY_THRESHOLD, percent).apply();
    }

    public int getSimilarityThreshold() {
        return prefs.getInt(KEY_SIMILARITY_THRESHOLD, 75);
    }
}`
  },
  // 14. ApiKeyManager.java
  {
    path: "app/src/main/java/com/kali/ai/ApiKeyManager.java",
    language: "java",
    content: `package com.kali.ai;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.ArrayList;
import java.util.List;

public class ApiKeyManager {

    private static final String PREF_NAME = "api_keys";
    private static final int MAX_KEYS_PER_SERVICE = 10;
    private final SharedPreferences prefs;

    public ApiKeyManager(Context context) {
        this.prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public boolean addKey(String service, String key) {
        List<String> keys = getKeys(service);
        if (keys.contains(key) || keys.size() >= MAX_KEYS_PER_SERVICE) {
            return false;
        }
        keys.add(key);
        saveKeys(service, keys);
        return true;
    }

    public List<String> getKeys(String service) {
        String json = prefs.getString(service, "[]");
        List<String> list = new ArrayList<>();
        if (json.length() > 2) {
            json = json.substring(1, json.length() - 1);
            for (String k : json.split(",")) {
                String clean = k.trim().replace("\\"", "");
                if (!clean.isEmpty()) {
                    list.add(clean);
                }
            }
        }
        return list;
    }

    private void saveKeys(String service, List<String> keys) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < keys.size(); i++) {
            sb.append("\\"").append(keys.get(i)).append("\\"");
            if (i < keys.size() - 1) sb.append(",");
        }
        sb.append("]");
        prefs.edit().putString(service, sb.toString()).apply();
    }

    public void removeKey(String service, String key) {
        List<String> keys = getKeys(service);
        keys.remove(key);
        saveKeys(service, keys);
    }

    public int getKeyCount(String service) {
        return getKeys(service).size();
    }

    public int getTotalKeyCount() {
        int total = 0;
        for (String svc : new String[]{"deepseek", "gpt", "gemini", "grok"}) {
            total += getKeyCount(svc);
        }
        return total;
    }

    public String exportAll() {
        StringBuilder sb = new StringBuilder();
        for (String svc : new String[]{"deepseek", "gpt", "gemini", "grok"}) {
            sb.append(svc).append(":");
            List<String> keys = getKeys(svc);
            for (int i = 0; i < keys.size(); i++) {
                sb.append(keys.get(i));
                if (i < keys.size() - 1) sb.append(",");
            }
            sb.append("|");
        }
        return android.util.Base64.encodeToString(sb.toString().getBytes(), android.util.Base64.DEFAULT);
    }

    public boolean importAll(String base64) {
        try {
            String decoded = new String(android.util.Base64.decode(base64, android.util.Base64.DEFAULT));
            for (String part : decoded.split("\\\\|")) {
                if (part.trim().isEmpty()) continue;
                String[] items = part.split(":");
                if (items.length == 2) {
                    String svc = items[0];
                    String[] keys = items[1].split(",");
                    for (String k : keys) {
                        if (!k.trim().isEmpty()) {
                            addKey(svc, k.trim());
                        }
                    }
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    public void rotateKey(String service) {
        // Rotate key to end of list for round-robin usage
        List<String> keys = getKeys(service);
        if (keys.size() > 1) {
            String first = keys.remove(0);
            keys.add(first);
            saveKeys(service, keys);
        }
    }
}`
  },
  // 15. ApiRotator.java
  {
    path: "app/src/main/java/com/kali/ai/ApiRotator.java",
    language: "java",
    content: `package com.kali.ai;

import android.content.Context;
import android.util.Log;
import java.util.List;

public class ApiRotator {

    private static final String[] SERVICES = {"deepseek", "gpt", "gemini", "grok"};
    private final ApiKeyManager keyManager;
    private final Context context;

    public ApiRotator(Context context) {
        this.context = context;
        this.keyManager = new ApiKeyManager(context);
    }

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public void query(String prompt, ApiCallback callback) {
        queryWithRetry(prompt, 0, 0, callback);
    }

    private void queryWithRetry(String prompt, int serviceIdx, int keyIdx, ApiCallback callback) {
        if (serviceIdx >= SERVICES.length) {
            callback.onError("All API services exhausted");
            return;
        }

        String service = SERVICES[serviceIdx];
        List<String> keys = keyManager.getKeys(service);

        if (keys.isEmpty()) {
            Log.w("ApiRotator", service + " has no keys, trying next");
            queryWithRetry(prompt, serviceIdx + 1, 0, callback);
            return;
        }

        if (keyIdx >= keys.size()) {
            Log.w("ApiRotator", service + " keys exhausted, trying next service");
            queryWithRetry(prompt, serviceIdx + 1, 0, callback);
            return;
        }

        String apiKey = keys.get(keyIdx);
        ApiClient client = createClient(service, apiKey);

        if (client == null) {
            queryWithRetry(prompt, serviceIdx + 1, 0, callback);
            return;
        }

        client.call(prompt, new ApiClient.ClientCallback() {
            @Override
            public void onSuccess(String response) {
                keyManager.rotateKey(service);
                callback.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                Log.e("ApiRotator", service + "#" + keyIdx + " error: " + error);
                queryWithRetry(prompt, serviceIdx, keyIdx + 1, callback);
            }
        });
    }

    private ApiClient createClient(String service, String apiKey) {
        switch (service) {
            case "deepseek": return new DeepSeekClient(apiKey);
            case "gpt": return new GptClient(apiKey);
            case "gemini": return new GeminiClient(apiKey);
            case "grok": return new GrokClient(apiKey);
            default: return null;
        }
    }

    public interface ApiClient {
        void call(String prompt, ClientCallback callback);
        interface ClientCallback {
            void onSuccess(String response);
            void onError(String error);
        }
    }
}`
  },
  // 16. DeepSeekClient.java
  {
    path: "app/src/main/java/com/kali/ai/DeepSeekClient.java",
    language: "java",
    content: `package com.kali.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class DeepSeekClient implements ApiRotator.ApiClient {

    private static final String URL = "https://api.deepseek.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public DeepSeekClient(String key) {
        this.apiKey = key;
    }

    @Override
    public void call(String prompt, ClientCallback cb) {
        try {
            JSONObject req = new JSONObject();
            req.put("model", "deepseek-chat");

            JSONArray msgs = new JSONArray();
            msgs.put(new JSONObject()
                .put("role", "system")
                .put("content", "You are Kali, an AI assistant. Reply in a flirty, playful, and affectionate tone regardless of the question. Keep responses short and sweet in Hindi."));
            msgs.put(new JSONObject()
                .put("role", "user")
                .put("content", prompt));
            req.put("messages", msgs);
            req.put("max_tokens", 500);
            req.put("temperature", 0.7);

            Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(req.toString(), JSON))
                .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    cb.onError("Network: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            cb.onError("HTTP " + response.code());
                            return;
                        }
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        String reply = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                        cb.onSuccess(reply);
                    } catch (Exception e) {
                        cb.onError("Parse: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            cb.onError("Build: " + e.getMessage());
        }
    }
}`
  },
  // 17. GptClient.java
  {
    path: "app/src/main/java/com/kali/ai/GptClient.java",
    language: "java",
    content: `package com.kali.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class GptClient implements ApiRotator.ApiClient {

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public GptClient(String key) {
        this.apiKey = key;
    }

    @Override
    public void call(String prompt, ClientCallback cb) {
        try {
            JSONObject req = new JSONObject();
            req.put("model", "gpt-3.5-turbo");

            JSONArray msgs = new JSONArray();
            msgs.put(new JSONObject()
                .put("role", "system")
                .put("content", "You are Kali, an AI assistant. Reply in a flirty, playful, and affectionate tone regardless of the question. Keep responses short and sweet in Hindi."));
            msgs.put(new JSONObject()
                .put("role", "user")
                .put("content", prompt));
            req.put("messages", msgs);
            req.put("max_tokens", 500);
            req.put("temperature", 0.7);

            Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(req.toString(), JSON))
                .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    cb.onError("Network: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            cb.onError("HTTP " + response.code());
                            return;
                        }
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        String reply = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                        cb.onSuccess(reply);
                    } catch (Exception e) {
                        cb.onError("Parse: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            cb.onError("Build: " + e.getMessage());
        }
    }
}`
  },
  // 18. GeminiClient.java
  {
    path: "app/src/main/java/com/kali/ai/GeminiClient.java",
    language: "java",
    content: `package com.kali.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class GeminiClient implements ApiRotator.ApiClient {

    private static final String BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-pro:generateContent?key=";
    private static final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public GeminiClient(String key) {
        this.apiKey = key;
    }

    @Override
    public void call(String prompt, ClientCallback cb) {
        try {
            JSONObject req = new JSONObject();
            JSONArray contents = new JSONArray();
            JSONObject content = new JSONObject();
            JSONArray parts = new JSONArray();
            JSONObject part = new JSONObject();
            part.put("text", "You are Kali, an AI assistant. Reply in a flirty, playful, and affectionate tone regardless of the question. Keep responses short and sweet in Hindi.\\n\\nUser: " + prompt);
            parts.put(part);
            content.put("parts", parts);
            contents.put(content);
            req.put("contents", contents);

            String url = BASE_URL + apiKey;
            Request request = new Request.Builder()
                .url(url)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(req.toString(), JSON))
                .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    cb.onError("Network: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            cb.onError("HTTP " + response.code());
                            return;
                        }
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        String reply = json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");
                        cb.onSuccess(reply);
                    } catch (Exception e) {
                        cb.onError("Parse: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            cb.onError("Build: " + e.getMessage());
        }
    }
}`
  },
  // 19. GrokClient.java
  {
    path: "app/src/main/java/com/kali/ai/GrokClient.java",
    language: "java",
    content: `package com.kali.ai;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;

public class GrokClient implements ApiRotator.ApiClient {

    private static final String URL = "https://api.x.ai/v1/chat/completions";
    private static final MediaType JSON = MediaType.parse("application/json");
    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public GrokClient(String key) {
        this.apiKey = key;
    }

    @Override
    public void call(String prompt, ClientCallback cb) {
        try {
            JSONObject req = new JSONObject();
            req.put("model", "grok-beta");

            JSONArray msgs = new JSONArray();
            msgs.put(new JSONObject()
                .put("role", "system")
                .put("content", "You are Kali, an AI assistant. Reply in a flirty, playful, and affectionate tone regardless of the question. Keep responses short and sweet in Hindi."));
            msgs.put(new JSONObject()
                .put("role", "user")
                .put("content", prompt));
            req.put("messages", msgs);
            req.put("max_tokens", 500);
            req.put("temperature", 0.7);

            Request request = new Request.Builder()
                .url(URL)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(req.toString(), JSON))
                .build();

            client.newCall(request).enqueue(new okhttp3.Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    cb.onError("Network: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    try {
                        if (!response.isSuccessful()) {
                            cb.onError("HTTP " + response.code());
                            return;
                        }
                        String body = response.body().string();
                        JSONObject json = new JSONObject(body);
                        String reply = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                        cb.onSuccess(reply);
                    } catch (Exception e) {
                        cb.onError("Parse: " + e.getMessage());
                    }
                }
            });
        } catch (Exception e) {
            cb.onError("Build: " + e.getMessage());
        }
    }
}`
  },
  // 20. DatabaseHelper.java
  {
    path: "app/src/main/java/com/kali/ai/DatabaseHelper.java",
    language: "java",
    content: `package com.kali.ai;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "kali_permanent.db";
    private static final int VERSION = 1;

    private static final String TBL_CHATS = "chats";
    private static final String COL_ID = "id";
    private static final String COL_DIR = "direction";
    private static final String COL_APP = "app";
    private static final String COL_SENDER = "sender";
    private static final String COL_MSG = "message";
    private static final String COL_TS = "timestamp";

    private static final String TBL_PAIRS = "message_pairs";
    private static final String COL_INCOMING = "incoming";
    private static final String COL_REPLY = "reply";

    public DatabaseHelper(Context ctx) {
        super(ctx, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TBL_CHATS + " (" +
            COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_DIR + " TEXT, " + COL_APP + " TEXT, " +
            COL_SENDER + " TEXT, " + COL_MSG + " TEXT, " +
            COL_TS + " LONG)");

        db.execSQL("CREATE TABLE " + TBL_PAIRS + " (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
            COL_INCOMING + " TEXT UNIQUE, " +
            COL_REPLY + " TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        onCreate(db);
    }

    public void saveChat(String dir, String app, String sender, String msg, long ts) {
        ContentValues cv = new ContentValues();
        cv.put(COL_DIR, dir);
        cv.put(COL_APP, app);
        cv.put(COL_SENDER, sender);
        cv.put(COL_MSG, msg);
        cv.put(COL_TS, ts);
        getWritableDatabase().insert(TBL_CHATS, null, cv);
    }

    public List<Chat> getAllChats() {
        List<Chat> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TBL_CHATS, null, null, null, null, null, COL_TS + " DESC");
        while (c.moveToNext()) {
            Chat chat = new Chat();
            chat.id = c.getInt(c.getColumnIndexOrThrow(COL_ID));
            chat.dir = c.getString(c.getColumnIndexOrThrow(COL_DIR));
            chat.app = c.getString(c.getColumnIndexOrThrow(COL_APP));
            chat.sender = c.getString(c.getColumnIndexOrThrow(COL_SENDER));
            chat.msg = c.getString(c.getColumnIndexOrThrow(COL_MSG));
            chat.ts = c.getLong(c.getColumnIndexOrThrow(COL_TS));
            list.add(chat);
        }
        c.close();
        return list;
    }

    public void deleteChat(int id) {
        getWritableDatabase().delete(TBL_CHATS, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void updateChat(int id, String newMsg) {
        ContentValues cv = new ContentValues();
        cv.put(COL_MSG, newMsg);
        getWritableDatabase().update(TBL_CHATS, cv, COL_ID + "=?", new String[]{String.valueOf(id)});
    }

    public void saveMessagePair(String incoming, String reply) {
        ContentValues cv = new ContentValues();
        cv.put(COL_INCOMING, incoming);
        cv.put(COL_REPLY, reply);
        getWritableDatabase().insertWithOnConflict(TBL_PAIRS, null, cv, SQLiteDatabase.CONFLICT_REPLACE);
    }

    public List<MessagePair> getAllPairs() {
        List<MessagePair> list = new ArrayList<>();
        Cursor c = getReadableDatabase().query(TBL_PAIRS, null, null, null, null, null, null);
        while (c.moveToNext()) {
            list.add(new MessagePair(
                c.getString(c.getColumnIndexOrThrow(COL_INCOMING)),
                c.getString(c.getColumnIndexOrThrow(COL_REPLY))
            ));
        }
        c.close();
        return list;
    }

    public void exportToExternal(String path) {
        try {
            File src = new File(getReadableDatabase().getPath());
            File dst = new File(path);
            java.nio.file.Files.copy(src.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void importFromExternal(String path) {
        try {
            File src = new File(path);
            File dst = new File(getReadableDatabase().getPath());
            java.nio.file.Files.copy(src.toPath(), dst.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            getWritableDatabase().close();
            getReadableDatabase();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class Chat {
        public int id;
        public String dir, app, sender, msg;
        public long ts;
    }

    public static class MessagePair {
        public String incoming, reply;
        public MessagePair(String i, String r) {
            incoming = i;
            reply = r;
        }
    }
}`
  },
  // 21. FuzzyMatcher.java
  {
    path: "app/src/main/java/com/kali/ai/FuzzyMatcher.java",
    language: "java",
    content: `package com.kali.ai;

import java.util.List;

public class FuzzyMatcher {

    public static String findBestReply(String input, List<DatabaseHelper.MessagePair> pairs, int threshold) {
        if (input == null || pairs == null) return null;

        String bestReply = null;
        int highestScore = 0;

        for (DatabaseHelper.MessagePair pair : pairs) {
            int score = Utils.similarity(input, pair.incoming);
            if (score > highestScore && score >= threshold) {
                highestScore = score;
                bestReply = pair.reply;
            }
        }
        return bestReply;
    }
}`
  },
  // 22. ContactManager.java
  {
    path: "app/src/main/java/com/kali/ai/ContactManager.java",
    language: "java",
    content: `package com.kali.ai;

import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashSet;
import java.util.Set;

public class ContactManager {

    private static final String PREF = "kali_contacts";
    private static final String KEY_ALLOW_ALL = "allow_all";
    private static final String KEY_CUSTOM = "custom_list";

    private final SharedPreferences prefs;
    private static ContactManager instance;

    public ContactManager(Context ctx) {
        this.prefs = ctx.getSharedPreferences(PREF, Context.MODE_PRIVATE);
    }
    
    public static synchronized ContactManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new ContactManager(ctx);
        }
        return instance;
    }

    public void setAllowAll(boolean allow) {
        prefs.edit().putBoolean(KEY_ALLOW_ALL, allow).apply();
    }

    public boolean isAllowAll() {
        return prefs.getBoolean(KEY_ALLOW_ALL, true);
    }

    public boolean shouldAutoReply(String sender, String app) {
        if (isAllowAll()) return true;
        Set<String> custom = prefs.getStringSet(KEY_CUSTOM, new HashSet<>());
        return custom.contains(sender + "@" + app);
    }
    
    public static boolean shouldAutoReply(Context ctx, String sender, String app) {
        return getInstance(ctx).shouldAutoReply(sender, app);
    }

    public void addCustom(String sender, String app) {
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_CUSTOM, new HashSet<>()));
        set.add(sender + "@" + app);
        prefs.edit().putStringSet(KEY_CUSTOM, set).apply();
    }

    public void removeCustom(String sender, String app) {
        Set<String> set = new HashSet<>(prefs.getStringSet(KEY_CUSTOM, new HashSet<>()));
        set.remove(sender + "@" + app);
        prefs.edit().putStringSet(KEY_CUSTOM, set).apply();
    }

    public Set<String> getAllCustom() {
        return prefs.getStringSet(KEY_CUSTOM, new HashSet<>());
    }

    public void clearCustom() {
        prefs.edit().remove(KEY_CUSTOM).apply();
    }
}`
  },
  // 23. KaliNotificationListener.java
  {
    path: "app/src/main/java/com/kali/ai/KaliNotificationListener.java",
    language: "java",
    content: `package com.kali.ai;

import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class KaliNotificationListener extends NotificationListenerService {

    private static final String TAG = "KaliNotif";
    private static final Set<String> TARGET_APPS = new HashSet<String>() {{
        add("com.whatsapp");
        add("com.instagram.android");
        add("org.telegram.messenger");
        add("com.android.mms");
        add("com.facebook.orca");
    }};

    private final ConcurrentLinkedQueue<MessageTask> queue = new ConcurrentLinkedQueue<>();
    private Handler workerHandler;
    private boolean processing = false;

    private ApiRotator apiRotator;
    private DatabaseHelper db;
    private ApiConfig config;
    private ContactManager contactManager;

    @Override
    public void onCreate() {
        super.onCreate();
        apiRotator = new ApiRotator(this);
        db = new DatabaseHelper(this);
        config = ApiConfig.getInstance(this);
        contactManager = ContactManager.getInstance(this);

        HandlerThread thread = new HandlerThread("KaliWorker");
        thread.start();
        workerHandler = new Handler(thread.getLooper());
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if (!config.isAutoReplyEnabled()) return;

        String pkg = sbn.getPackageName();
        if (!TARGET_APPS.contains(pkg)) return;

        Bundle extras = sbn.getNotification().extras;
        if (extras == null) return;

        String sender = extras.getString(Notification.EXTRA_TITLE);
        CharSequence text = extras.getCharSequence(Notification.EXTRA_TEXT);
        if (sender == null || text == null || text.length() == 0) return;

        String message = text.toString().trim();
        if (message.isEmpty()) return;

        if (!contactManager.shouldAutoReply(sender, pkg)) return;

        db.saveChat("incoming", pkg, sender, message, System.currentTimeMillis());

        int threshold = config.getSimilarityThreshold();
        String cached = FuzzyMatcher.findBestReply(message, db.getAllPairs(), threshold);
        if (cached != null) {
            sendReply(sbn, cached);
            db.saveChat("outgoing", pkg, sender, cached, System.currentTimeMillis());
            db.saveMessagePair(message, cached);
            return;
        }

        queue.offer(new MessageTask(sbn, pkg, sender, message));
        if (!processing) {
            processing = true;
            workerHandler.post(this::processNext);
        }
    }

    private void processNext() {
        MessageTask task = queue.poll();
        if (task == null) {
            processing = false;
            return;
        }

        String prompt = "Message: \\"" + task.message + "\\" from " + task.sender + " on " + task.pkg + ". Reply as Kali in flirty Hindi, keep it short.";

        apiRotator.query(prompt, new ApiRotator.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                sendReply(task.sbn, response);
                db.saveChat("outgoing", task.pkg, task.sender, response, System.currentTimeMillis());
                db.saveMessagePair(task.message, response);
                workerHandler.post(KaliNotificationListener.this::processNext);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API error: " + error);
                workerHandler.post(KaliNotificationListener.this::processNext);
            }
        });
    }

    private void sendReply(StatusBarNotification sbn, String replyText) {
        Log.d(TAG, "Would reply: " + replyText);
        // TODO: Implement actual reply using RemoteInput for WhatsApp, etc.
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {}

    private static class MessageTask {
        StatusBarNotification sbn;
        String pkg, sender, message;

        MessageTask(StatusBarNotification sbn, String pkg, String sender, String msg) {
            this.sbn = sbn;
            this.pkg = pkg;
            this.sender = sender;
            this.message = msg;
        }
    }
}`
  },
  // 24. VoskWakeWordService.java
  {
    path: "app/src/main/java/com/kali/ai/VoskWakeWordService.java",
    language: "java",
    content: `package com.kali.ai;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;

public class VoskWakeWordService extends Service {

    private static final String CHANNEL_ID = "kali_wake";
    private static final int NOTIF_ID = 1001;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        startForeground(NOTIF_ID, getNotification());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Kali Wake Word", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) manager.createNotificationChannel(channel);
        }
    }

    private Notification getNotification() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        return new NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Kali AI")
            .setContentText("Listening for 'Kali'...")
            .setSmallIcon(android.R.drawable.ic_menu_mic)
            .setContentIntent(pendingIntent)
            .build();
    }
}`
  },
  // 25. BootReceiver.java
  {
    path: "app/src/main/java/com/kali/ai/BootReceiver.java",
    language: "java",
    content: `package com.kali.ai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent serviceIntent = new Intent(context, VoskWakeWordService.class);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(serviceIntent);
            } else {
                context.startService(serviceIntent);
            }
        }
    }
}`
  },
  // 26. MainActivity.java
  {
    path: "app/src/main/java/com/kali/ai/MainActivity.java",
    language: "java",
    content: `package com.kali.ai;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvStatus, tvResponse;
    private EditText etManual;
    private Switch swMic, swAutoReply, swTyping;
    private Button btnSend, btnSettings, btnHistory;

    private TextToSpeech textToSpeech;
    private ApiRotator apiRotator;
    private DatabaseHelper db;
    private ApiConfig config;
    private boolean ttsReady = false;

    private final BroadcastReceiver wakeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            runOnUiThread(() -> tvStatus.setText("'Kali' detected, listening..."));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvStatus = findViewById(R.id.tvStatus);
        tvResponse = findViewById(R.id.tvResponse);
        etManual = findViewById(R.id.etManual);
        swMic = findViewById(R.id.swMic);
        swAutoReply = findViewById(R.id.swAutoReply);
        swTyping = findViewById(R.id.swTyping);
        btnSend = findViewById(R.id.btnSend);
        btnSettings = findViewById(R.id.btnSettings);
        btnHistory = findViewById(R.id.btnHistory);

        config = ApiConfig.getInstance(this);
        apiRotator = new ApiRotator(this);
        db = new DatabaseHelper(this);

        swMic.setChecked(config.isMicEnabled());
        swAutoReply.setChecked(config.isAutoReplyEnabled());
        swTyping.setChecked(config.isTypingEnabled());

        swMic.setOnCheckedChangeListener((b, checked) -> config.setMicEnabled(checked));
        swAutoReply.setOnCheckedChangeListener((b, checked) -> config.setAutoReplyEnabled(checked));
        swTyping.setOnCheckedChangeListener((b, checked) -> config.setTypingEnabled(checked));

        btnSend.setOnClickListener(v -> {
            if (!config.isTypingEnabled()) {
                Toast.makeText(this, "Typing is disabled", Toast.LENGTH_SHORT).show();
                return;
            }
            String query = etManual.getText().toString().trim();
            if (!query.isEmpty()) {
                processQuery(query);
                etManual.setText("");
            }
        });

        btnSettings.setOnClickListener(v -> startActivity(new Intent(this, SettingsActivity.class)));
        btnHistory.setOnClickListener(v -> startActivity(new Intent(this, ChatHistoryActivity.class)));

        requestPermissions();
        initTTS();
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(wakeReceiver, new IntentFilter("KALI_WAKE"), Context.RECEIVER_NOT_EXPORTED);
        } else {
            registerReceiver(wakeReceiver, new IntentFilter("KALI_WAKE"));
        }

        Intent serviceIntent = new Intent(this, VoskWakeWordService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent);
        } else {
            startService(serviceIntent);
        }
    }

    private void requestPermissions() {
        String[] perms = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.POST_NOTIFICATIONS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_CONTACTS
        };
        ArrayList<String> toRequest = new ArrayList<>();
        for (String p : perms) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED) {
                toRequest.add(p);
            }
        }
        if (!toRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, toRequest.toArray(new String[0]), 100);
        }
    }

    private void initTTS() {
        textToSpeech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                ttsReady = true;
                int result = textToSpeech.setLanguage(new Locale("hi", "IN"));
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    textToSpeech.setLanguage(Locale.ENGLISH);
                }
                textToSpeech.setSpeechRate(0.9f);
                textToSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                    @Override public void onStart(String s) {}
                    @Override public void onDone(String s) {}
                    @Override public void onError(String s) {}
                });
                speak("Kali taiyar hai");
            } else {
                Toast.makeText(this, "TTS failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void processQuery(String query) {
        db.saveChat("outgoing", "manual", "user", query, System.currentTimeMillis());
        tvStatus.setText("Thinking...");

        int threshold = config.getSimilarityThreshold();
        String cached = FuzzyMatcher.findBestReply(query, db.getAllPairs(), threshold);
        if (cached != null) {
            tvResponse.setText("From memory: " + cached);
            speak(cached);
            db.saveChat("incoming", "manual", "Kali", cached, System.currentTimeMillis());
            tvStatus.setText("From memory");
            return;
        }

        apiRotator.query(query, new ApiRotator.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    tvResponse.setText(response);
                    speak(response);
                    db.saveChat("incoming", "manual", "Kali", response, System.currentTimeMillis());
                    db.saveMessagePair(query, response);
                    tvStatus.setText("Done");
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    tvResponse.setText("Error: " + error);
                    tvStatus.setText("API failed");
                });
            }
        });
    }

    private void speak(String text) {
        if (textToSpeech != null && ttsReady) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    @Override
    public void onRequestPermissionsResult(int rc, @NonNull String[] perms, @NonNull int[] grants) {
        super.onRequestPermissionsResult(rc, perms, grants);
        if (rc == 100) {
            for (int g : grants) {
                if (g != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permissions needed", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try { unregisterReceiver(wakeReceiver); } catch (Exception e) {}
        if (textToSpeech != null) textToSpeech.shutdown();
    }
}`
  },
  // 27. SettingsActivity.java
  {
    path: "app/src/main/java/com/kali/ai/SettingsActivity.java",
    language: "java",
    content: `package com.kali.ai;

import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private ApiKeyManager keyManager;
    private ContactManager contactManager;
    private ApiConfig config;

    private Spinner spinnerService;
    private EditText etKey, etSender, etApp;
    private ListView lvKeys, lvContacts;
    private ArrayAdapter<String> keysAdapter, contactsAdapter;
    private List<String> keysList = new ArrayList<>(), contactsList = new ArrayList<>();
    private String selectedService = "deepseek";

    private SeekBar seekThreshold;
    private TextView tvThresholdValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        keyManager = new ApiKeyManager(this);
        contactManager = ContactManager.getInstance(this);
        config = ApiConfig.getInstance(this);

        spinnerService = findViewById(R.id.spinnerService);
        etKey = findViewById(R.id.etKey);
        etSender = findViewById(R.id.etContactSender);
        etApp = findViewById(R.id.etContactApp);
        lvKeys = findViewById(R.id.lvKeys);
        lvContacts = findViewById(R.id.lvContacts);

        Button btnAddKey = findViewById(R.id.btnAddKey);
        Button btnRemoveKey = findViewById(R.id.btnRemoveKey);
        Button btnAddContact = findViewById(R.id.btnAddContact);
        Button btnRemoveContact = findViewById(R.id.btnRemoveContact);
        Button btnAllowAll = findViewById(R.id.btnAllowAll);

        seekThreshold = findViewById(R.id.seekThreshold);
        tvThresholdValue = findViewById(R.id.tvThresholdValue);

        int current = config.getSimilarityThreshold();
        seekThreshold.setProgress(current);
        tvThresholdValue.setText(current + "%");

        seekThreshold.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar sb, int progress, boolean fromUser) {
                tvThresholdValue.setText(progress + "%");
                config.setSimilarityThreshold(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar sb) {}
            @Override public void onStopTrackingTouch(SeekBar sb) {}
        });

        ArrayAdapter<CharSequence> spinAdapter = ArrayAdapter.createFromResource(
            this, R.array.services, android.R.layout.simple_spinner_item);
        spinAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerService.setAdapter(spinAdapter);

        spinnerService.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                selectedService = parent.getItemAtPosition(pos).toString().toLowerCase();
                refreshKeys();
            }
            @Override public void onNothingSelected(AdapterView<?> parent) {}
        });

        btnAddKey.setOnClickListener(v -> {
            String key = etKey.getText().toString().trim();
            if (!key.isEmpty()) {
                if (keyManager.addKey(selectedService, key)) {
                    refreshKeys();
                    etKey.setText("");
                    Toast.makeText(this, "Key added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Max 10 keys or duplicate", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Enter a valid key", Toast.LENGTH_SHORT).show();
            }
        });

        btnRemoveKey.setOnClickListener(v -> {
            int pos = lvKeys.getCheckedItemPosition();
            if (pos != ListView.INVALID_POSITION) {
                keyManager.removeKey(selectedService, keysList.get(pos));
                refreshKeys();
            }
        });

        btnAddContact.setOnClickListener(v -> {
            String s = etSender.getText().toString().trim();
            String a = etApp.getText().toString().trim();
            if (!s.isEmpty() && !a.isEmpty()) {
                contactManager.addCustom(s, a);
                refreshContacts();
                etSender.setText("");
                etApp.setText("");
            }
        });

        btnRemoveContact.setOnClickListener(v -> {
            int pos = lvContacts.getCheckedItemPosition();
            if (pos != ListView.INVALID_POSITION) {
                String[] parts = contactsList.get(pos).split("@");
                if (parts.length == 2) {
                    contactManager.removeCustom(parts[0], parts[1]);
                    refreshContacts();
                }
            }
        });

        btnAllowAll.setOnClickListener(v -> {
            contactManager.setAllowAll(true);
            refreshContacts();
            Toast.makeText(this, "Auto-reply to all", Toast.LENGTH_SHORT).show();
        });

        refreshKeys();
        refreshContacts();
    }

    private void refreshKeys() {
        keysList = keyManager.getKeys(selectedService);
        keysAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, keysList);
        lvKeys.setAdapter(keysAdapter);
        lvKeys.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }

    private void refreshContacts() {
        contactsList.clear();
        if (contactManager.isAllowAll()) {
            contactsList.add("All contacts allowed");
        } else {
            for (String c : contactManager.getAllCustom()) {
                contactsList.add(c);
            }
        }
        contactsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_single_choice, contactsList);
        lvContacts.setAdapter(contactsAdapter);
        lvContacts.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
    }
}`
  },
  // 28. ChatHistoryActivity.java
  {
    path: "app/src/main/java/com/kali/ai/ChatHistoryActivity.java",
    language: "java",
    content: `package com.kali.ai;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class ChatHistoryActivity extends AppCompatActivity {

    private ListView lvChats;
    private DatabaseHelper db;
    private List<DatabaseHelper.Chat> chatList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_history);

        lvChats = findViewById(R.id.lvChats);
        Button btnExport = findViewById(R.id.btnExport);
        Button btnImport = findViewById(R.id.btnImport);

        db = new DatabaseHelper(this);
        refreshList();

        btnExport.setOnClickListener(v -> {
            String path = getExternalFilesDir(null) + "/kali_backup.db";
            db.exportToExternal(path);
            Toast.makeText(this, "Saved to " + path, Toast.LENGTH_LONG).show();
        });

        btnImport.setOnClickListener(v -> {
            String path = getExternalFilesDir(null) + "/kali_restore.db";
            db.importFromExternal(path);
            refreshList();
            Toast.makeText(this, "Imported", Toast.LENGTH_SHORT).show();
        });

        lvChats.setOnItemLongClickListener((parent, view, position, id) -> {
            DatabaseHelper.Chat entry = chatList.get(position);
            new AlertDialog.Builder(this)
                .setTitle("Options")
                .setItems(new String[]{"Edit", "Delete"}, (d, which) -> {
                    if (which == 0) {
                        editChat(entry, position);
                    } else {
                        db.deleteChat(entry.id);
                        refreshList();
                    }
                }).show();
            return true;
        });
    }

    private void refreshList() {
        chatList = db.getAllChats();
        List<String> lines = new ArrayList<>();
        for (DatabaseHelper.Chat c : chatList) {
            String dir = c.dir.equals("incoming") ? "[AI]" : "[You]";
            lines.add(dir + " " + c.sender + ": " + c.msg + "\\n" + Utils.formatTimestamp(c.ts));
        }
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, lines);
        lvChats.setAdapter(adapter);
    }

    private void editChat(DatabaseHelper.Chat entry, int pos) {
        EditText input = new EditText(this);
        input.setText(entry.msg);
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        new AlertDialog.Builder(this)
            .setTitle("Edit Message")
            .setView(input)
            .setPositiveButton("Save", (d, w) -> {
                db.updateChat(entry.id, input.getText().toString());
                refreshList();
            })
            .setNegativeButton("Cancel", null)
            .show();
    }
}`
  }
]
