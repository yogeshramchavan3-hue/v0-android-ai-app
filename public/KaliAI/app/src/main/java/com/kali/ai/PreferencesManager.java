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
    private static final String KEY_AUTO_REPLY_SMS = "auto_reply_sms";
    private static final String KEY_CUSTOM_PROMPT = "custom_prompt";
    private static final String KEY_LANGUAGE = "language";

    private final SharedPreferences prefs;

    public PreferencesManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

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

    public boolean isWakeWordEnabled() {
        return prefs.getBoolean(KEY_WAKE_WORD_ENABLED, true);
    }

    public void setWakeWordEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_WAKE_WORD_ENABLED, enabled).apply();
    }

    public boolean isTTSEnabled() {
        return prefs.getBoolean(KEY_TTS_ENABLED, true);
    }

    public void setTTSEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_TTS_ENABLED, enabled).apply();
    }

    public boolean isWhatsAppAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_WHATSAPP, false);
    }

    public void setWhatsAppAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_WHATSAPP, enabled).apply();
    }

    public boolean isTelegramAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_TELEGRAM, false);
    }

    public void setTelegramAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_TELEGRAM, enabled).apply();
    }

    public boolean isSmsAutoReplyEnabled() {
        return prefs.getBoolean(KEY_AUTO_REPLY_SMS, false);
    }

    public void setSmsAutoReplyEnabled(boolean enabled) {
        prefs.edit().putBoolean(KEY_AUTO_REPLY_SMS, enabled).apply();
    }

    public String getCustomPrompt() {
        return prefs.getString(KEY_CUSTOM_PROMPT, "");
    }

    public void setCustomPrompt(String prompt) {
        prefs.edit().putString(KEY_CUSTOM_PROMPT, prompt).apply();
    }

    public String getLanguage() {
        return prefs.getString(KEY_LANGUAGE, "hi-IN");
    }

    public void setLanguage(String language) {
        prefs.edit().putString(KEY_LANGUAGE, language).apply();
    }
}
