package com.kali.ai;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesManager {

    private static final String PREFS_NAME = "kali_ai_prefs";
    
    private static final String KEY_API_PROVIDER = "api_provider";
    private static final String KEY_API_KEY = "api_key";
    private static final String KEY_WAKE_WORD_ENABLED = "wake_word_enabled";
    private static final String KEY_TTS_ENABLED = "tts_enabled";
    private static final String KEY_AUTO_REPLY_WHATSAPP = "auto_reply_whatsapp";
    private static final String KEY_AUTO_REPLY_TELEGRAM = "auto_reply_telegram";
    private static final String KEY_AUTO_REPLY_INSTAGRAM = "auto_reply_instagram";
    private static final String KEY_AUTO_REPLY_SMS = "auto_reply_sms";
    private static final String KEY_CUSTOM_PROMPT = "custom_prompt";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_TTS_LANGUAGE = "tts_language";
    private static final String KEY_SPEECH_LANGUAGE = "speech_language";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    // API Settings
    public String getApiProvider() {
        return prefs.getString(KEY_API_PROVIDER, "deepseek");
    }

    public void setApiProvider(String provider) {
        prefs.edit().putString(KEY_API_PROVIDER, provider).apply();
    }

    public String getApiKey() {
        return prefs.getString(KEY_API_KEY, "");
    }

    public void setApiKey(String apiKey) {
        prefs.edit().putString(KEY_API_KEY, apiKey).apply();
    }

    // Wake Word Settings
    public boolean isWakeWordEnabled() {
        return prefs.getBoolean(KEY_WAKE_WORD_ENABLED, true);
    }

    public void setWakeWordEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_WAKE_WORD_ENABLED, enabled).apply();
    }

    // TTS Settings
    public boolean isTTSEnabled() {
        return prefs.getBoolean(KEY_TTS_ENABLED, true);
    }

    public void setTTSEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply();
    }

    // Auto Reply Settings - WhatsApp
    public boolean isWhatsAppAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_WHATSAPP, false);
    }

    public void setWhatsAppAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_WHATSAPP, enabled).apply();
    }

    // Auto Reply Settings - Telegram
    public boolean isTelegramAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_TELEGRAM, false);
    }

    public void setTelegramAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_TELEGRAM, enabled).apply();
    }

    // Auto Reply Settings - Instagram
    public boolean isInstagramAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_INSTAGRAM, false);
    }

    public void setInstagramAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_INSTAGRAM, enabled).apply();
    }

    // Auto Reply Settings - SMS
    public boolean isSmsAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_SMS, false);
    }

    public void setSmsAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_SMS, enabled).apply();
    }

    // Custom Prompt
    public String getCustomPrompt() {
        return prefs.getString(KEY_CUSTOM_PROMPT, "");
    }

    public void setCustomPrompt(String prompt) {
        prefs.edit().putString(KEY_CUSTOM_PROMPT, prompt).apply();
    }

    // Language Settings
    // Supported: hi-IN (Hindi), en-US (English), mr-IN (Marathi)
    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "hi-IN");
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }
    
    // TTS Language (separate from speech recognition)
    public String getTTSLanguage() {
        return prefs.getString(KEY_TTS_LANGUAGE, "hi-IN");
    }

    public void setTTSLanguage(String language) {
        prefs.edit().putString(KEY_TTS_LANGUAGE, language).apply();
    }
    
    // Speech Recognition Language
    public String getSpeechLanguage() {
        return prefs.getString(KEY_SPEECH_LANGUAGE, "hi-IN");
    }

    public void setSpeechLanguage(String language) {
        prefs.edit().putString(KEY_SPEECH_LANGUAGE, language).apply();
    }
    
    // Get display name for language code
    public static String getLanguageDisplayName(String code) {
        switch (code) {
            case "hi-IN":
                return "Hindi (हिंदी)";
            case "en-US":
            case "en-IN":
                return "English";
            case "mr-IN":
                return "Marathi (मराठी)";
            default:
                return code;
        }
    }
}
