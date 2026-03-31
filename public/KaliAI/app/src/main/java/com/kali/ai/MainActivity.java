package com.kali.ai;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final String TAG = "KaliAI_Main";
    private static final int PERMISSION_REQUEST_CODE = 100;
    
    private RecyclerView chatRecyclerView;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> chatMessages;
    private EditText messageInput;
    private ImageButton sendButton, micButton, settingsButton, historyButton;
    private TextView statusText;
    
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private ApiHelper apiHelper;
    private DatabaseHelper dbHelper;
    private PreferencesManager prefsManager;
    
    private boolean isListening = false;
    private boolean isWakeWordMode = true;
    private boolean isTTSReady = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        Log.d(TAG, "onCreate started");
        
        initViews();
        initHelpers();
        checkPermissions();
        setupRecyclerView();
        setupListeners();
        initSpeechRecognizer();
        checkNotificationAccess();
        
        loadRecentChats();
        
        Log.d(TAG, "onCreate completed");
    }

    private void initViews() {
        chatRecyclerView = findViewById(R.id.chatRecyclerView);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        micButton = findViewById(R.id.micButton);
        settingsButton = findViewById(R.id.settingsButton);
        historyButton = findViewById(R.id.historyButton);
        statusText = findViewById(R.id.statusText);
    }

    private void initHelpers() {
        dbHelper = new DatabaseHelper(this);
        prefsManager = new PreferencesManager(this);
        apiHelper = new ApiHelper(this, prefsManager);
        textToSpeech = new TextToSpeech(this, this);
        chatMessages = new ArrayList<>();
    }

    private void checkPermissions() {
        String[] permissions = {
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.POST_NOTIFICATIONS
        };
        
        List<String> neededPermissions = new ArrayList<>();
        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                neededPermissions.add(permission);
            }
        }
        
        if (!neededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(this, neededPermissions.toArray(new String[0]), PERMISSION_REQUEST_CODE);
        }
    }
    
    private void checkNotificationAccess() {
        String enabledListeners = Settings.Secure.getString(
            getContentResolver(), 
            "enabled_notification_listeners"
        );
        
        String packageName = getPackageName();
        
        if (enabledListeners == null || !enabledListeners.contains(packageName)) {
            // Show dialog to enable notification access
            new AlertDialog.Builder(this)
                .setTitle("Notification Access Required")
                .setMessage("Auto-reply feature ke liye Notification Access chahiye. Enable karoge?")
                .setPositiveButton("Enable", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Later", null)
                .show();
        } else {
            Log.d(TAG, "Notification access already granted");
        }
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter(chatMessages, this);
        chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        chatRecyclerView.setAdapter(chatAdapter);
    }

    private void setupListeners() {
        sendButton.setOnClickListener(v -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
                messageInput.setText("");
            }
        });

        micButton.setOnClickListener(v -> {
            if (isListening) {
                stopListening();
            } else {
                startListening();
            }
        });

        settingsButton.setOnClickListener(v -> {
            startActivity(new Intent(this, SettingsActivity.class));
        });

        historyButton.setOnClickListener(v -> {
            startActivity(new Intent(this, ChatHistoryActivity.class));
        });
        
        // Long press on mic for API test
        micButton.setOnLongClickListener(v -> {
            testApiConnection();
            return true;
        });
    }
    
    private void testApiConnection() {
        Toast.makeText(this, "Testing API connection...", Toast.LENGTH_SHORT).show();
        statusText.setText("Testing API...");
        
        apiHelper.testApiConnection(new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "API Working! Response: " + response, Toast.LENGTH_LONG).show();
                    statusText.setText("API Test Success!");
                    Log.d(TAG, "API Test Success: " + response);
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "API Error: " + error, Toast.LENGTH_LONG).show();
                    statusText.setText("API Test Failed!");
                    Log.e(TAG, "API Test Failed: " + error);
                });
            }
        });
    }

    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            Log.d(TAG, "Speech recognition available");
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    Log.d(TAG, "Ready for speech");
                    statusText.setText("Listening...");
                }

                @Override
                public void onBeginningOfSpeech() {
                    Log.d(TAG, "Speech started");
                }

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    Log.d(TAG, "Speech ended");
                    isListening = false;
                    micButton.setImageResource(R.drawable.ic_mic);
                }

                @Override
                public void onError(int error) {
                    Log.e(TAG, "Speech error: " + error);
                    isListening = false;
                    micButton.setImageResource(R.drawable.ic_mic);
                    statusText.setText("Say 'Kali' to activate");
                    
                    String errorMsg = getSpeechErrorMessage(error);
                    if (error != SpeechRecognizer.ERROR_NO_MATCH && error != SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                        Toast.makeText(MainActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
                    
                    if (isWakeWordMode && prefsManager.isWakeWordEnabled()) {
                        // Restart wake word detection after delay
                        messageInput.postDelayed(() -> startWakeWordDetection(), 1000);
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String spokenText = matches.get(0).toLowerCase();
                        Log.d(TAG, "Speech result: " + spokenText);
                        processVoiceInput(spokenText);
                    }
                    
                    if (isWakeWordMode && prefsManager.isWakeWordEnabled()) {
                        messageInput.postDelayed(() -> startWakeWordDetection(), 500);
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {
                    ArrayList<String> matches = partialResults.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        Log.d(TAG, "Partial result: " + matches.get(0));
                    }
                }

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
            
            if (prefsManager.isWakeWordEnabled()) {
                messageInput.postDelayed(() -> startWakeWordDetection(), 1000);
            }
        } else {
            Log.e(TAG, "Speech recognition NOT available");
            Toast.makeText(this, "Speech recognition not available on this device", Toast.LENGTH_LONG).show();
        }
    }
    
    private String getSpeechErrorMessage(int error) {
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                return "Audio recording error";
            case SpeechRecognizer.ERROR_CLIENT:
                return "Client side error";
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                return "Insufficient permissions";
            case SpeechRecognizer.ERROR_NETWORK:
                return "Network error";
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case SpeechRecognizer.ERROR_NO_MATCH:
                return "No speech detected";
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                return "Recognition service busy";
            case SpeechRecognizer.ERROR_SERVER:
                return "Server error";
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                return "No speech input";
            default:
                return "Unknown error: " + error;
        }
    }

    private void processVoiceInput(String text) {
        Log.d(TAG, "Processing voice input: " + text + ", isWakeWordMode: " + isWakeWordMode);
        
        if (isWakeWordMode) {
            // Check for wake word in multiple languages
            if (text.contains("kali") || text.contains("काली") || text.contains("cali") || 
                text.contains("kelly") || text.contains("कली") || text.contains("कालि")) {
                isWakeWordMode = false;
                speak("Haan, main sun rahi hoon");
                statusText.setText("Kali is listening...");
                
                // Start listening for actual command after TTS
                messageInput.postDelayed(() -> {
                    if (!isWakeWordMode) {
                        startListening();
                    }
                }, 1500);
            }
        } else {
            // Process as message
            if (!text.isEmpty()) {
                sendMessage(text);
            }
            isWakeWordMode = true;
        }
    }

    private void startWakeWordDetection() {
        if (prefsManager.isWakeWordEnabled() && !isListening) {
            Log.d(TAG, "Starting wake word detection");
            
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            
            // Support multiple languages for wake word detection
            String speechLang = prefsManager.getSpeechLanguage();
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLang);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, speechLang);
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"hi-IN", "en-IN", "mr-IN"});
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 3);
            
            try {
                speechRecognizer.startListening(intent);
                statusText.setText("Say 'Kali' to activate");
            } catch (Exception e) {
                Log.e(TAG, "Error starting wake word detection: " + e.getMessage());
            }
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            isListening = true;
            micButton.setImageResource(R.drawable.ic_mic_active);
            
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            
            // Use selected language
            String speechLang = prefsManager.getSpeechLanguage();
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, speechLang);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, speechLang);
            // Also add support for other languages
            intent.putExtra("android.speech.extra.EXTRA_ADDITIONAL_LANGUAGES", new String[]{"hi-IN", "en-US", "mr-IN"});
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            
            Log.d(TAG, "Starting speech recognition with language: " + speechLang);
            speechRecognizer.startListening(intent);
        } else {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show();
            checkPermissions();
        }
    }

    private void stopListening() {
        isListening = false;
        micButton.setImageResource(R.drawable.ic_mic);
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    private void sendMessage(String message) {
        Log.d(TAG, "Sending message: " + message);
        
        ChatMessage userMessage = new ChatMessage(message, true, System.currentTimeMillis());
        chatMessages.add(userMessage);
        chatAdapter.notifyItemInserted(chatMessages.size() - 1);
        chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        
        dbHelper.saveMessage(userMessage);
        statusText.setText("Thinking...");
        
        apiHelper.getAIResponse(message, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Log.d(TAG, "AI Response: " + response);
                    
                    ChatMessage aiMessage = new ChatMessage(response, false, System.currentTimeMillis());
                    chatMessages.add(aiMessage);
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    
                    dbHelper.saveMessage(aiMessage);
                    statusText.setText("Say 'Kali' to activate");
                    
                    if (prefsManager.isTTSEnabled() && isTTSReady) {
                        speak(response);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Log.e(TAG, "API Error: " + error);
                    Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_LONG).show();
                    statusText.setText("Error - Check API settings");
                });
            }
        });
    }

    private void speak(String text) {
        if (textToSpeech != null && isTTSReady) {
            Log.d(TAG, "Speaking: " + text);
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "kali_response");
        }
    }

    private void loadRecentChats() {
        List<ChatMessage> recentMessages = dbHelper.getRecentMessages(50);
        chatMessages.addAll(recentMessages);
        chatAdapter.notifyDataSetChanged();
        
        if (!chatMessages.isEmpty()) {
            chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
        }
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            Log.d(TAG, "TTS initialized successfully");
            
            String ttsLang = prefsManager.getTTSLanguage();
            Locale locale;
            
            switch (ttsLang) {
                case "en-US":
                case "en-IN":
                    locale = new Locale("en", "IN");
                    break;
                case "mr-IN":
                    locale = new Locale("mr", "IN");
                    break;
                case "hi-IN":
                default:
                    locale = new Locale("hi", "IN");
                    break;
            }
            
            int result = textToSpeech.setLanguage(locale);
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w(TAG, "TTS language not supported, trying default");
                // Try English as fallback
                result = textToSpeech.setLanguage(Locale.US);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Log.e(TAG, "Default TTS language also not supported");
                    Toast.makeText(this, "TTS language not available", Toast.LENGTH_SHORT).show();
                }
            }
            
            textToSpeech.setPitch(1.0f);
            textToSpeech.setSpeechRate(0.9f);
            isTTSReady = true;
            
            Log.d(TAG, "TTS ready with language: " + locale);
        } else {
            Log.e(TAG, "TTS initialization failed: " + status);
            Toast.makeText(this, "Text-to-Speech not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            boolean allGranted = true;
            for (int result : grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allGranted = false;
                    break;
                }
            }
            
            if (allGranted) {
                Log.d(TAG, "All permissions granted");
                if (prefsManager.isWakeWordEnabled()) {
                    startWakeWordDetection();
                }
            } else {
                Toast.makeText(this, "Some permissions denied. Features may not work.", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        
        // Refresh TTS language in case changed in settings
        if (textToSpeech != null && isTTSReady) {
            String ttsLang = prefsManager.getTTSLanguage();
            Locale locale;
            switch (ttsLang) {
                case "en-US":
                case "en-IN":
                    locale = new Locale("en", "IN");
                    break;
                case "mr-IN":
                    locale = new Locale("mr", "IN");
                    break;
                default:
                    locale = new Locale("hi", "IN");
            }
            textToSpeech.setLanguage(locale);
        }
        
        if (prefsManager.isWakeWordEnabled() && !isListening) {
            messageInput.postDelayed(() -> startWakeWordDetection(), 1000);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
