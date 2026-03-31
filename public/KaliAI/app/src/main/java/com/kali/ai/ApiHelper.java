package com.kali.ai;

import android.content.Context;
import android.util.Log;
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

    private static final String TAG = "KaliAI_API";
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
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(120, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
    }

    public void getAIResponse(String userMessage, ApiCallback callback) {
        String apiProvider = prefsManager.getApiProvider();
        String apiKey = prefsManager.getApiKey();
        
        Log.d(TAG, "API Provider: " + apiProvider);
        Log.d(TAG, "API Key exists: " + (apiKey != null && !apiKey.isEmpty()));
        Log.d(TAG, "User Message: " + userMessage);
        
        if (apiKey == null || apiKey.isEmpty()) {
            String error = "API key not set. Settings mein API key enter karo.";
            Log.e(TAG, error);
            callback.onError(error);
            return;
        }

        try {
            switch (apiProvider.toLowerCase()) {
                case "deepseek":
                    callDeepSeek(userMessage, apiKey, callback);
                    break;
                case "openai":
                case "gpt":
                case "chatgpt":
                    callOpenAI(userMessage, apiKey, callback);
                    break;
                case "gemini":
                case "google":
                    callGemini(userMessage, apiKey, callback);
                    break;
                case "grok":
                case "xai":
                    callGrok(userMessage, apiKey, callback);
                    break;
                default:
                    Log.d(TAG, "Default to DeepSeek");
                    callDeepSeek(userMessage, apiKey, callback);
            }
        } catch (Exception e) {
            Log.e(TAG, "Exception in getAIResponse: " + e.getMessage());
            callback.onError("Error: " + e.getMessage());
        }
    }

    private void callDeepSeek(String message, String apiKey, ApiCallback callback) {
        Log.d(TAG, "Calling DeepSeek API...");
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
            requestBody.put("stream", false);

            Log.d(TAG, "DeepSeek Request: " + requestBody.toString());

            Request request = new Request.Builder()
                    .url("https://api.deepseek.com/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("Accept", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "deepseek");
        } catch (Exception e) {
            Log.e(TAG, "DeepSeek Error: " + e.getMessage());
            callback.onError("DeepSeek Error: " + e.getMessage());
        }
    }

    private void callOpenAI(String message, String apiKey, ApiCallback callback) {
        Log.d(TAG, "Calling OpenAI API...");
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

            Log.d(TAG, "OpenAI Request: " + requestBody.toString());

            Request request = new Request.Builder()
                    .url("https://api.openai.com/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "openai");
        } catch (Exception e) {
            Log.e(TAG, "OpenAI Error: " + e.getMessage());
            callback.onError("OpenAI Error: " + e.getMessage());
        }
    }

    private void callGemini(String message, String apiKey, ApiCallback callback) {
        Log.d(TAG, "Calling Gemini API...");
        try {
            JSONObject requestBody = new JSONObject();
            JSONArray contents = new JSONArray();
            
            // System instruction
            JSONObject systemInstruction = new JSONObject();
            JSONArray systemParts = new JSONArray();
            JSONObject systemPart = new JSONObject();
            systemPart.put("text", getSystemPrompt());
            systemParts.put(systemPart);
            systemInstruction.put("parts", systemParts);
            requestBody.put("systemInstruction", systemInstruction);
            
            // User message
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
            generationConfig.put("topP", 0.95);
            requestBody.put("generationConfig", generationConfig);

            String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;
            
            Log.d(TAG, "Gemini Request URL: " + url);
            Log.d(TAG, "Gemini Request: " + requestBody.toString());

            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "gemini");
        } catch (Exception e) {
            Log.e(TAG, "Gemini Error: " + e.getMessage());
            callback.onError("Gemini Error: " + e.getMessage());
        }
    }

    private void callGrok(String message, String apiKey, ApiCallback callback) {
        Log.d(TAG, "Calling Grok API...");
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
            requestBody.put("stream", false);

            Log.d(TAG, "Grok Request: " + requestBody.toString());

            Request request = new Request.Builder()
                    .url("https://api.x.ai/v1/chat/completions")
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody.toString(), JSON))
                    .build();

            executeRequest(request, callback, "grok");
        } catch (Exception e) {
            Log.e(TAG, "Grok Error: " + e.getMessage());
            callback.onError("Grok Error: " + e.getMessage());
        }
    }

    private void executeRequest(Request request, ApiCallback callback, String provider) {
        Log.d(TAG, "Executing request for provider: " + provider);
        
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                String error = "Network error: " + e.getMessage();
                Log.e(TAG, error);
                Log.e(TAG, "URL: " + call.request().url());
                callback.onError(error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String responseBody = response.body() != null ? response.body().string() : "";
                    
                    Log.d(TAG, "Response Code: " + response.code());
                    Log.d(TAG, "Response Body: " + responseBody);
                    
                    if (!response.isSuccessful()) {
                        String error = "API Error " + response.code() + ": " + parseErrorMessage(responseBody, provider);
                        Log.e(TAG, error);
                        callback.onError(error);
                        return;
                    }
                    
                    String aiResponse = parseResponse(responseBody, provider);
                    Log.d(TAG, "Parsed Response: " + aiResponse);
                    callback.onSuccess(aiResponse);
                } catch (Exception e) {
                    String error = "Parse error: " + e.getMessage();
                    Log.e(TAG, error);
                    callback.onError(error);
                }
            }
        });
    }

    private String parseErrorMessage(String responseBody, String provider) {
        try {
            JSONObject json = new JSONObject(responseBody);
            if (json.has("error")) {
                JSONObject error = json.getJSONObject("error");
                if (error.has("message")) {
                    return error.getString("message");
                }
            }
            return responseBody;
        } catch (Exception e) {
            return responseBody;
        }
    }

    private String parseResponse(String responseBody, String provider) throws Exception {
        JSONObject json = new JSONObject(responseBody);
        
        Log.d(TAG, "Parsing response for provider: " + provider);
        
        switch (provider) {
            case "gemini":
                if (json.has("candidates") && json.getJSONArray("candidates").length() > 0) {
                    return json.getJSONArray("candidates")
                            .getJSONObject(0)
                            .getJSONObject("content")
                            .getJSONArray("parts")
                            .getJSONObject(0)
                            .getString("text");
                }
                throw new Exception("No response from Gemini");
                
            case "deepseek":
            case "openai":
            case "grok":
            default:
                if (json.has("choices") && json.getJSONArray("choices").length() > 0) {
                    return json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content");
                }
                throw new Exception("No response from " + provider);
        }
    }

    private String getSystemPrompt() {
        String customPrompt = prefsManager.getCustomPrompt();
        if (customPrompt != null && !customPrompt.isEmpty()) {
            return customPrompt;
        }
        
        String language = prefsManager.getLanguage();
        
        if (language.startsWith("mr")) {
            return "तू काली आहेस, एक helpful AI assistant जी Marathi मध्ये बोलते. " +
                   "तुझं नाव काली आहे आणि तू user ला मदत करायला नेहमी तयार आहेस. " +
                   "Short आणि helpful answers दे. Friendly आणि respectful रहा.";
        } else if (language.startsWith("en")) {
            return "You are Kali, a helpful AI assistant. " +
                   "Your name is Kali and you are always ready to help the user. " +
                   "Give short and helpful answers. Be friendly and respectful.";
        } else {
            return "Tum Kali ho, ek helpful AI assistant jo Hindi mein baat karti hai. " +
                   "Tumhara naam Kali hai aur tum user ki madad karne ke liye hamesha taiyaar ho. " +
                   "Short aur helpful answers do. Friendly aur respectful raho. " +
                   "Tum Hindi, English aur Marathi teeno languages samajh sakti ho.";
        }
    }
    
    // Test API connection
    public void testApiConnection(ApiCallback callback) {
        Log.d(TAG, "Testing API connection...");
        getAIResponse("Hello, reply with just 'API working!' if you can hear me.", callback);
    }
}
