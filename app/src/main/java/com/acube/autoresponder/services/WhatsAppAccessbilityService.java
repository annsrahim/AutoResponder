package com.acube.autoresponder.services;

import android.accessibilityservice.AccessibilityService;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.R;
import com.acube.autoresponder.activity.MainActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anns on 12/04/18.
 */

public class WhatsAppAccessbilityService extends AccessibilityService {
    int childCount = 0;

    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {
        if(getRootInActiveWindow()==null)
            return;

        AccessibilityNodeInfoCompat rootInactiveWindow = AccessibilityNodeInfoCompat.wrap(getRootInActiveWindow());

//        List<AccessibilityNodeInfoCompat> messageNodeList  = rootInactiveWindow.findAccessibilityNodeInfosByViewId("com.whatsapp:id/ok");
////        if (messageNodeList == null || messageNodeList.isEmpty ()) {
////            return;
////        }
////        AccessibilityNodeInfoCompat messageField = messageNodeList.get (0);
////        if (messageField.getText () == null || messageField.getText ().length () == 0
////                || !messageField.getText ().toString ().equalsIgnoreCase (getApplicationContext ().getString (R.string.whatsapp_suffix))) { // So your service doesn't process any message, but the ones ending your apps suffix
////            return;
////        }
        if(rootInactiveWindow.findAccessibilityNodeInfosByViewId ("com.whatsapp:id/send")==null)
            return;
        List<AccessibilityNodeInfoCompat> sendMessageNodeInfoList = rootInactiveWindow.findAccessibilityNodeInfosByViewId ("com.whatsapp:id/send");
        if (sendMessageNodeInfoList == null || sendMessageNodeInfoList.isEmpty ()) {
            return;
        }
        AccessibilityNodeInfoCompat sendMessageButton = sendMessageNodeInfoList.get (0);
        if (!sendMessageButton.isVisibleToUser ()) {
            return;
        }
        sendMessageButton.performAction (AccessibilityNodeInfo.ACTION_CLICK);
        try {
           // hack for certain devices in which the immediate back click is too fast to handle
            performGlobalAction (GLOBAL_ACTION_BACK);
            // same hack as above
            Thread.sleep (2000);
            performGlobalAction (GLOBAL_ACTION_BACK);
            Thread.sleep (2000);
            performGlobalAction (GLOBAL_ACTION_BACK);
        }
        catch (InterruptedException ignored) {}




    }

    @Override
    public void onInterrupt() {
        Log.d(Config.TAG,"Interrupt");

    }


    public static boolean isAccessibilityOn (Context context, Class<? extends AccessibilityService> clazz) {
        int accessibilityEnabled = 0;
        final String service = context.getPackageName () + "/" + clazz.getCanonicalName ();
        try {
            accessibilityEnabled = Settings.Secure.getInt (context.getApplicationContext ().getContentResolver (), Settings.Secure.ACCESSIBILITY_ENABLED);
        } catch (Settings.SettingNotFoundException ignored) {  }

        String s = ":";

        char c = s.charAt(0);
        TextUtils.SimpleStringSplitter colonSplitter = new TextUtils.SimpleStringSplitter (c);

        if (accessibilityEnabled == 1) {
            String settingValue = Settings.Secure.getString (context.getApplicationContext ().getContentResolver (), Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                colonSplitter.setString (settingValue);
                while (colonSplitter.hasNext ()) {
                    String accessibilityService = colonSplitter.next ();

                    if (accessibilityService.equalsIgnoreCase (service)) {
                        return true;
                    }
                }
            }
        }

        return false;
    }




}
