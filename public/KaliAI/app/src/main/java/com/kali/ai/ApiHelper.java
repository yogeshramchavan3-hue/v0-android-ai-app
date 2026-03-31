package com.kali.ai;

import android.content.Context;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class ApiHelper {

    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    
    private final Context context;
    private final PreferencesManager prefsManager;
    private final OkHttpClient client;

    public interface ApiCallback {
        void onSuccess(String response);
        void onError(String error);
    }

    public ApiHelper(Context context, PreferencesManager prefsManager) {
        this.context = context;
        this.prefsManager = prefsManager;
        this.client = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
    }

    public void getAIResponse(String userMessage, ApiCallback callback) {
        String apiProvider = prefsManager.getApiProvider();
        String apiKey = prefsManager.getApiKey();
        
        if (apiKey == null || apiKey.isEmpty()) {
            callback.onError("API key not set. Please configure in settings.");
            return;
        }

        switch (apiProvider) {
            case "deepseek":
                callDeepSeek(userMessage, apiKey, callback);
                break;
            case "openai":
                callOpenAI(userMessage, apiKey, callback);
                break;
            case "gemini":
                callGemini(userMessage, apiKey, callback);
                break;
            case "grok":
                callGrok(userMessage, apiKey, callback);
                break;
            default:
                callDeepSeek(userMessage, apiKey, callback);
        }
    }

    private void callDeepSeek(String message, String apiKey, ApiCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "deepseek-chat");
            
            JSONArray messages = new JSONArray();
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", getSystemPrompt());
            messages.put(systemMsg);
            
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            messages.put(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);

            Request request = new Request.Builder()
                    .url("https://api.deepseek.com/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "deepseek");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private void callOpenAI(String message, String apiKey, ApiCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "gpt-4o-mini");
            
            JSONArray messages = new JSONArray();
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", getSystemPrompt());
            messages.put(systemMsg);
            
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            messages.put(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);
            requestBody.put("max_tokens", 2048);

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "openai");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private void callGemini(String message, String apiKey, ApiCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            
            JSONObject systemContent = new JSONObject();
            systemContent.put("role", "user");
            JSONArray systemParts = new JSONArray();
            JSONObject systemPart = new JSONObject();
            systemPart.put("text", getSystemPrompt());
            systemParts.put(systemPart);
            systemContent.put("parts", systemParts);
            contents.put(systemContent);
            
            JSONObject userContent = new JSONObject();
            userContent.put("role", "user");
            JSONArray userParts = new JSONArray();
            JSONObject userPart = new JSONObject();
            userPart.put("text", message);
            userParts.put(userPart);
            userContent.put("parts", userParts);
            contents.put(userContent);
            
            requestBody.put("contents", contents);
            
            JSONObject generationConfig = new JSONObject();
            generationConfig.put("temperature", 0.7);
            generationConfig.put("maxOutputTokens", 2048);
            requestBody.put("generationConfig", generationConfig);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "gemini");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private void callGrok(String message, String apiKey, ApiCallback callback) {
        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("model", "grok-beta");
            
            JSONArray messages = new JSONArray();
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content", getSystemPrompt());
            messages.put(systemMsg);
            
            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", message);
            messages.put(userMsg);
            
            requestBody.put("messages", messages);
            requestBody.put("temperature", 0.7);

            Request request = new Request.Builder()
                    .url("https://api.x.ai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "grok");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    private void executeRequest(Request request, ApiCallback callback, String provider) {
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError("Network error: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body().string();
                    
                    if (!response.isSuccessful()) {
                        callback.onError("API error: " + response.code());
                        return;
                    }
                    
                    String aiResponse = parseResponse(responseBody, provider);
                    callback.onSuccess(aiResponse);
                } catch (Exception e) {
                    callback.onError("Parse error: " + e.getMessage());
                }
            }
        });
    }

    private String parseResponse(String responseBody, String provider) throws Exception {
        JSONObject json = new JSONObject(responseBody);
        
        switch (provider) {
            case "gemini":
                return json.getJSONArray("candidates")
                        .getJSONObject(0)
                        .getJSONObject("content")
                        .getJSONArray("parts")
                        .getJSONObject(0)
                        .getString("text");
            default:
                return json.getJSONArray("choices")
                        .getJSONObject(0)
                        .getJSONObject("message")
                        .getString("content");
        }
    }

    private String getSystemPrompt() {
        String customPrompt = prefsManager.getCustomPrompt();
        if (customPrompt != null && !customPrompt.isEmpty()) {
            return customPrompt;
        }
        
        return "Tum Kali ho, ek helpful AI assistant jo Hindi mein baat karti hai. " +
               "Tumhara naam Kali hai aur tum user ki madad karne ke liye hamesha taiyaar ho. " +
               "Short aur helpful answers do. Friendly aur respectful raho.";
    }
}
