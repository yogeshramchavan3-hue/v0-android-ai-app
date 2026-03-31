package com.kali.ai;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import java.util.HashSet;
import java.util.Set;

public class KaliListenerService extends NotificationListenerService {

    private static final String TAG = "KaliAI_Listener";
    private static final String CHANNEL_ID = "kali_listener_channel";
    
    // Package names for supported apps
    private static final String WHATSAPP_PACKAGE = "com.whatsapp";
    private static final String WHATSAPP_BUSINESS_PACKAGE = "com.whatsapp.w4b";
    private static final String TELEGRAM_PACKAGE = "org.telegram.messenger";
    private static final String TELEGRAM_X_PACKAGE = "org.thunderdog.challegram";
    private static final String INSTAGRAM_PACKAGE = "com.instagram.android";
    
    private PreferencesManager prefsManager;
    private ApiHelper apiHelper;
    private DatabaseHelper dbHelper;
    private Handler mainHandler;
    
    // Track processed notifications to avoid duplicates
    private Set<String> processedNotifications = new HashSet<>();

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "KaliListenerService onCreate");
        prefsManager = new PreferencesManager(this);
        apiHelper = new ApiHelper(this, prefsManager);
        dbHelper = new DatabaseHelper(this);
        mainHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
        showServiceStartedToast();
    }
    
    private void showServiceStartedToast() {
        mainHandler.post(() -> {
            Toast.makeText(this, "Kali Auto-Reply Service Started", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        String notificationKey = sbn.getKey();
        
        Log.d(TAG, "Notification received from: " + packageName);
        
        // Skip if already processed
        if (processedNotifications.contains(notificationKey)) {
            Log.d(TAG, "Notification already processed, skipping");
            return;
        }
        
        // Check if this app should be processed
        boolean shouldProcess = false;
        String appName = "";
        
        if ((packageName.equals(WHATSAPP_PACKAGE) || packageName.equals(WHATSAPP_BUSINESS_PACKAGE)) 
                && prefsManager.isWhatsAppAutoReplyEnabled()) {
            shouldProcess = true;
            appName = "WhatsApp";
        } else if ((packageName.equals(TELEGRAM_PACKAGE) || packageName.equals(TELEGRAM_X_PACKAGE)) 
                && prefsManager.isTelegramAutoReplyEnabled()) {
            shouldProcess = true;
            appName = "Telegram";
        } else if (packageName.equals(INSTAGRAM_PACKAGE) && prefsManager.isInstagramAutoReplyEnabled()) {
            shouldProcess = true;
            appName = "Instagram";
        }
        
        if (!shouldProcess) {
            Log.d(TAG, "Auto-reply not enabled for: " + packageName);
            return;
        }
        
        Log.d(TAG, "Processing notification from: " + appName);
        
        // Extract notification details
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        CharSequence textCs = extras.getCharSequence(Notification.EXTRA_TEXT);
        String text = textCs != null ? textCs.toString() : "";
        
        Log.d(TAG, "Title: " + title);
        Log.d(TAG, "Text: " + text);
        
        if (text.isEmpty()) {
            Log.d(TAG, "Empty message, skipping");
            return;
        }
        
        // Skip group messages (optional)
        if (title.contains("messages") || title.contains("@")) {
            Log.d(TAG, "Possibly group message, skipping");
            return;
        }
        
        // Check contact filter
        String senderNumber = extractNumber(title);
        if (senderNumber != null && !senderNumber.isEmpty()) {
            if (!dbHelper.isAutoReplyEnabled(senderNumber)) {
                Log.d(TAG, "Auto-reply disabled for: " + senderNumber);
                return;
            }
        }
        
        // Find reply action
        Notification.Action replyAction = findReplyAction(sbn.getNotification());
        
        if (replyAction != null) {
            // Mark as processed
            processedNotifications.add(notificationKey);
            
            // Show processing toast
            final String finalAppName = appName;
            mainHandler.post(() -> {
                Toast.makeText(this, "Kali processing " + finalAppName + " message...", Toast.LENGTH_SHORT).show();
            });
            
            processAndReply(text, replyAction, sbn, appName);
        } else {
            Log.d(TAG, "No reply action found");
        }
    }
    
    private Notification.Action findReplyAction(Notification notification) {
        if (notification.actions == null) {
            Log.d(TAG, "No actions in notification");
            return null;
        }
        
        for (Notification.Action action : notification.actions) {
            if (action.getRemoteInputs() != null && action.getRemoteInputs().length > 0) {
                Log.d(TAG, "Found reply action: " + action.title);
                return action;
            }
        }
        
        // Also check for wearable extender actions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            Notification.WearableExtender wearableExtender = new Notification.WearableExtender(notification);
            for (Notification.Action action : wearableExtender.getActions()) {
                if (action.getRemoteInputs() != null && action.getRemoteInputs().length > 0) {
                    Log.d(TAG, "Found wearable reply action: " + action.title);
                    return action;
                }
            }
        }
        
        return null;
    }

    private String extractNumber(String title) {
        if (title == null) return null;
        return title.replaceAll("[^0-9+]", "");
    }

    private void processAndReply(String message, Notification.Action action, StatusBarNotification sbn, String appName) {
        Log.d(TAG, "Getting AI response for: " + message);
        
        apiHelper.getAIResponse(message, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                Log.d(TAG, "AI Response received: " + response);
                sendReply(action, response, sbn.getKey(), appName);
            }

            @Override
            public void onError(String error) {
                Log.e(TAG, "API Error: " + error);
                // Remove from processed so it can retry
                processedNotifications.remove(sbn.getKey());
                
                mainHandler.post(() -> {
                    Toast.makeText(KaliListenerService.this, "Kali Error: " + error, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void sendReply(Notification.Action action, String reply, String notificationKey, String appName) {
        try {
            Log.d(TAG, "Sending reply: " + reply);
            
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            
            for (RemoteInput remoteInput : action.getRemoteInputs()) {
                bundle.putCharSequence(remoteInput.getResultKey(), reply);
                Log.d(TAG, "RemoteInput key: " + remoteInput.getResultKey());
            }
            
            RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
            
            int flags = PendingIntent.FLAG_UPDATE_CURRENT;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                flags |= PendingIntent.FLAG_MUTABLE;
            }
            
            action.actionIntent.send(this, 0, intent);
            
            Log.d(TAG, "Reply sent successfully to " + appName);
            
            // Save to database
            dbHelper.saveAutoReplyLog(appName, reply, System.currentTimeMillis());
            
            // Show success toast
            mainHandler.post(() -> {
                Toast.makeText(this, "Kali replied on " + appName, Toast.LENGTH_SHORT).show();
            });
            
            // Clean up old processed notifications
            if (processedNotifications.size() > 100) {
                processedNotifications.clear();
            }
            
        } catch (Exception e) {
            Log.e(TAG, "Failed to send reply: " + e.getMessage());
            e.printStackTrace();
            processedNotifications.remove(notificationKey);
            
            mainHandler.post(() -> {
                Toast.makeText(this, "Failed to reply: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID,
                "Kali Listener Service",
                NotificationManager.IMPORTANCE_LOW
            );
            channel.setDescription("Background service for auto-reply");
            
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        // Clean up processed notification
        processedNotifications.remove(sbn.getKey());
    }
    
    @Override
    public void onListenerConnected() {
        super.onListenerConnected();
        Log.d(TAG, "Notification Listener Connected");
        mainHandler.post(() -> {
            Toast.makeText(this, "Kali Notification Access Granted", Toast.LENGTH_SHORT).show();
        });
    }
    
    @Override
    public void onListenerDisconnected() {
        super.onListenerDisconnected();
        Log.d(TAG, "Notification Listener Disconnected");
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "KaliListenerService destroyed");
        processedNotifications.clear();
    }
}
