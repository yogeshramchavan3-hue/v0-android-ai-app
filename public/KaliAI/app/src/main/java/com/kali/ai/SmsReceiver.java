package com.kali.ai;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!SMS_RECEIVED.equals(intent.getAction())) return;

        PreferencesManager prefsManager = new PreferencesManager(context);
        if (!prefsManager.isSmsAutoReplyEnabled()) return;

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        ApiHelper apiHelper = new ApiHelper(context, prefsManager);

        Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        Object[] pdus = (Object[]) bundle.get("pdus");
        if (pdus == null) return;

        String format = bundle.getString("format");
        
        for (Object pdu : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);
            String sender = smsMessage.getDisplayOriginatingAddress();
            String messageBody = smsMessage.getMessageBody();

            if (sender == null || messageBody == null) continue;

            if (!dbHelper.isAutoReplyEnabled(sender)) {
                continue;
            }

            apiHelper.getAIResponse(messageBody, new ApiHelper.ApiCallback() {
                @Override
                public void onSuccess(String response) {
                    sendSmsReply(context, sender, response);
                }

                @Override
                public void onError(String error) {
                    // Silent fail
                }
            });
        }
    }

    private void sendSmsReply(Context context, String phoneNumber, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, message, null, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
