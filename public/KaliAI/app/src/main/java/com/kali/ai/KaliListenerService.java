package com.kali.ai;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;

import androidx.core.app.NotificationCompat;

public class KaliListenerService extends NotificationListenerService {

    private static final String CHANNEL_ID = "kali_listener_channel";
    private PreferencesManager prefsManager;
    private ApiHelper apiHelper;
    private DatabaseHelper dbHelper;

    @Override
    public void onCreate() {
        super.onCreate();
        prefsManager = new PreferencesManager(this);
        apiHelper = new ApiHelper(this, prefsManager);
        dbHelper = new DatabaseHelper(this);
        createNotificationChannel();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        String packageName = sbn.getPackageName();
        
        boolean shouldProcess = false;
        
        if (packageName.equals("com.whatsapp") && prefsManager.isWhatsAppAutoReplyEnabled()) {
            shouldProcess = true;
        } else if (packageName.equals("org.telegram.messenger") && prefsManager.isTelegramAutoReplyEnabled()) {
            shouldProcess = true;
        }
        
        if (!shouldProcess) return;
        
        Bundle extras = sbn.getNotification().extras;
        String title = extras.getString(Notification.EXTRA_TITLE, "");
        String text = extras.getString(Notification.EXTRA_TEXT, "");
        
        if (text == null || text.isEmpty()) return;
        
        String senderNumber = extractNumber(title);
        if (senderNumber != null && !dbHelper.isAutoReplyEnabled(senderNumber)) {
            return;
        }
        
        Notification.Action[] actions = sbn.getNotification().actions;
        if (actions != null) {
            for (Notification.Action action : actions) {
                if (action.getRemoteInputs() != null && action.getRemoteInputs().length > 0) {
                    processAndReply(text, action, sbn);
                    break;
                }
            }
        }
    }

    private String extractNumber(String title) {
        if (title == null) return null;
        return title.replaceAll("[^0-9+]", "");
    }

    private void processAndReply(String message, Notification.Action action, StatusBarNotification sbn) {
        apiHelper.getAIResponse(message, new ApiHelper.ApiCallback() {
            @Override
            public void onSuccess(String response) {
                sendReply(action, response);
            }

            @Override
            public void onError(String error) {
                // Silent fail for auto-reply
            }
        });
    }

    private void sendReply(Notification.Action action, String reply) {
        try {
            Intent intent = new Intent();
            Bundle bundle = new Bundle();
            
            for (RemoteInput remoteInput : action.getRemoteInputs()) {
                bundle.putCharSequence(remoteInput.getResultKey(), reply);
            }
            
            RemoteInput.addResultsToIntent(action.getRemoteInputs(), intent, bundle);
            action.actionIntent.send(this, 0, intent);
        } catch (Exception e) {
            e.printStackTrace();
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
        // Not needed
    }
}
