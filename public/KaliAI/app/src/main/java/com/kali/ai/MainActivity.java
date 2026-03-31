package com.kali.ai;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        initHelpers();
        checkPermissions();
        setupRecyclerView();
        setupListeners();
        initSpeechRecognizer();
        
        loadRecentChats();
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
            Manifest.permission.READ_CONTACTS
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
    }

    private void initSpeechRecognizer() {
        if (SpeechRecognizer.isRecognitionAvailable(this)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    statusText.setText("Listening...");
                }

                @Override
                public void onBeginningOfSpeech() {}

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    isListening = false;
                    micButton.setImageResource(R.drawable.ic_mic);
                }

                @Override
                public void onError(int error) {
                    isListening = false;
                    micButton.setImageResource(R.drawable.ic_mic);
                    statusText.setText("Say 'Kali' to activate");
                    
                    if (isWakeWordMode) {
                        startWakeWordDetection();
                    }
                }

                @Override
                public void onResults(Bundle results) {
                    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                    if (matches != null && !matches.isEmpty()) {
                        String spokenText = matches.get(0).toLowerCase();
                        processVoiceInput(spokenText);
                    }
                    
                    if (isWakeWordMode) {
                        startWakeWordDetection();
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {}

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
            
            if (prefsManager.isWakeWordEnabled()) {
                startWakeWordDetection();
            }
        }
    }

    private void processVoiceInput(String text) {
        if (isWakeWordMode) {
            if (text.contains("kali") || text.contains("काली") || text.contains("cali")) {
                isWakeWordMode = false;
                speak("Haan, main sun rahi hoon");
                statusText.setText("Kali is listening...");
                startListening();
            }
        } else {
            sendMessage(text);
            isWakeWordMode = true;
        }
    }

    private void startWakeWordDetection() {
        if (prefsManager.isWakeWordEnabled()) {
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
            intent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);
            
            try {
                speechRecognizer.startListening(intent);
                statusText.setText("Say 'Kali' to activate");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void startListening() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            isListening = true;
            micButton.setImageResource(R.drawable.ic_mic_active);
            
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN");
            intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
            
            speechRecognizer.startListening(intent);
        } else {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopListening() {
        isListening = false;
        micButton.setImageResource(R.drawable.ic_mic);
        speechRecognizer.stopListening();
    }

    private void sendMessage(String message) {
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
                    ChatMessage aiMessage = new ChatMessage(response, false, System.currentTimeMillis());
                    chatMessages.add(aiMessage);
                    chatAdapter.notifyItemInserted(chatMessages.size() - 1);
                    chatRecyclerView.scrollToPosition(chatMessages.size() - 1);
                    
                    dbHelper.saveMessage(aiMessage);
                    statusText.setText("Say 'Kali' to activate");
                    
                    if (prefsManager.isTTSEnabled()) {
                        speak(response);
                    }
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                    statusText.setText("Say 'Kali' to activate");
                });
            }
        });
    }

    private void speak(String text) {
        if (textToSpeech != null) {
            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
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
            Locale hindi = new Locale("hi", "IN");
            int result = textToSpeech.setLanguage(hindi);
            
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech.setLanguage(Locale.getDefault());
            }
            
            textToSpeech.setPitch(1.0f);
            textToSpeech.setSpeechRate(0.9f);
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
            
            if (allGranted && prefsManager.isWakeWordEnabled()) {
                startWakeWordDetection();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefsManager.isWakeWordEnabled() && !isListening) {
            startWakeWordDetection();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
