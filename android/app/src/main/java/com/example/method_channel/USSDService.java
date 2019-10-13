package com.example.method_channel;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.Collections;
import java.util.List;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

@TargetApi(Build.VERSION_CODES.DONUT)
public class USSDService extends AccessibilityService {

    public static String TAG = USSDService.class.getSimpleName();


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        SharedPreferences prefs = getApplicationContext().getSharedPreferences("nativePrefs", MODE_PRIVATE);

        if(prefs.getBoolean("serviceEnabled", false)) {

            AccessibilityNodeInfo source = event.getSource();
            /* if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && !event.getClassName().equals("android.app.AlertDialog")) { // android.app.AlertDialog is the standard but not for all phones  */
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
                    !String.valueOf(event.getClassName()).contains("AlertDialog")) {
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
                    (source == null || !source.getClassName().equals("android.widget.TextView"))) {
                return;
            }
            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED &&
                    TextUtils.isEmpty(source.getText())) {
            }

            List<CharSequence> eventText;

            if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
                eventText = event.getText();
            } else {
                eventText = Collections.singletonList(source.getText());
            }

            String ussdText = processUSSDText(eventText);

            Intent intent = new Intent("transaction-complete");
            intent.putExtra("ussdText", ussdText);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);

            if (TextUtils.isEmpty(ussdText)) return;

            // Close dialog
            performGlobalAction(GLOBAL_ACTION_BACK); // This works on 4.1+ only
            // Handle USSD response here
        }

    }

    private String processUSSDText(List<CharSequence> eventText) {
        for (CharSequence s : eventText) {
            String text = String.valueOf(s);
            // Return text if text is the expected ussd response
            if( true ) {
                return text;
            }
        }
        return null;
    }

    @Override
    public void onInterrupt() {

    }



    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        Log.d(TAG, "onServiceConnected");
        AccessibilityServiceInfo info = new AccessibilityServiceInfo();
        info.flags = AccessibilityServiceInfo.DEFAULT;
        info.packageNames = new String[]{"com.android.phone"};
        info.eventTypes = AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED | AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED;
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        setServiceInfo(info);
    }
}