package com.kali.ai;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class SettingsActivity extends AppCompatActivity {

    private Spinner apiProviderSpinner;
    private EditText apiKeyInput;
    private EditText customPromptInput;
    private SwitchMaterial wakeWordSwitch;
    private SwitchMaterial ttsSwitch;
    private SwitchMaterial whatsappSwitch;
    private SwitchMaterial telegramSwitch;
    private SwitchMaterial smsSwitch;
    
    private PreferencesManager prefsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Settings");
        }

        prefsManager = new PreferencesManager(this);
        initViews();
        loadSettings();
        setupListeners();
    }

    private void initViews() {
        apiProviderSpinner = findViewById(R.id.apiProviderSpinner);
        apiKeyInput = findViewById(R.id.apiKeyInput);
        customPromptInput = findViewById(R.id.customPromptInput);
        wakeWordSwitch = findViewById(R.id.wakeWordSwitch);
        ttsSwitch = findViewById(R.id.ttsSwitch);
        whatsappSwitch = findViewById(R.id.whatsappSwitch);
        telegramSwitch = findViewById(R.id.telegramSwitch);
        smsSwitch = findViewById(R.id.smsSwitch);

        String[] providers = {"DeepSeek", "OpenAI (GPT)", "Google Gemini", "xAI Grok"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, 
            android.R.layout.simple_spinner_dropdown_item, providers);
        apiProviderSpinner.setAdapter(adapter);
    }

    private void loadSettings() {
        String provider = prefsManager.getApiProvider();
        switch (provider) {
            case "deepseek":
                apiProviderSpinner.setSelection(0);
                break;
            case "openai":
                apiProviderSpinner.setSelection(1);
                break;
            case "gemini":
                apiProviderSpinner.setSelection(2);
                break;
            case "grok":
                apiProviderSpinner.setSelection(3);
                break;
        }

        apiKeyInput.setText(prefsManager.getApiKey());
        customPromptInput.setText(prefsManager.getCustomPrompt());
        wakeWordSwitch.setChecked(prefsManager.isWakeWordEnabled());
        ttsSwitch.setChecked(prefsManager.isTTSEnabled());
        whatsappSwitch.setChecked(prefsManager.isWhatsAppAutoReplyEnabled());
        telegramSwitch.setChecked(prefsManager.isTelegramAutoReplyEnabled());
        smsSwitch.setChecked(prefsManager.isSmsAutoReplyEnabled());
    }

    private void setupListeners() {
        findViewById(R.id.saveButton).setOnClickListener(v -> saveSettings());
    }

    private void saveSettings() {
        int providerPosition = apiProviderSpinner.getSelectedItemPosition();
        String provider;
        switch (providerPosition) {
            case 0:
                provider = "deepseek";
                break;
            case 1:
                provider = "openai";
                break;
            case 2:
                provider = "gemini";
                break;
            case 3:
                provider = "grok";
                break;
            default:
                provider = "deepseek";
        }

        prefsManager.setApiProvider(provider);
        prefsManager.setApiKey(apiKeyInput.getText().toString().trim());
        prefsManager.setCustomPrompt(customPromptInput.getText().toString().trim());
        prefsManager.setWakeWordEnabled(wakeWordSwitch.isChecked());
        prefsManager.setTTSEnabled(ttsSwitch.isChecked());
        prefsManager.setWhatsAppAutoReplyEnabled(whatsappSwitch.isChecked());
        prefsManager.setTelegramAutoReplyEnabled(telegramSwitch.isChecked());
        prefsManager.setSmsAutoReplyEnabled(smsSwitch.isChecked());

        Toast.makeText(this, "Settings saved!", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
