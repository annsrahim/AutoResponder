package com.acube.autoresponder.services;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.acube.autoresponder.Config;

/**
 * Created by Anns on 23/03/18.
 */

public class NotificationService extends NotificationListenerService {
    Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if(sbn.getTag()!=null)
        {

            parseNotification(sbn);
//            String tagSbn = sbn.getTag();
//            Log.d("QQQ",tagSbn+" !!");
//            String pack = sbn.getPackageName();
//            String ticker = sbn.getNotification().tickerText.toString();
//            Bundle extras = sbn.getNotification().extras;
//            String title = extras.getString("android.title");
//            String text = extras.getCharSequence("android.text").toString();
//            Log.i("Package ",pack);
//            Log.i("Ticker ",ticker);
//            Log.i("Title",title);
//            Log.i("Text",text);
//
//
//            Intent msgrcv = new Intent(Config.Msg);
//            msgrcv.putExtra(Config.PACKAGE,pack);
//            msgrcv.putExtra(Config.TICKER,ticker);
//            msgrcv.putExtra(Config.TITLE,title);
//            msgrcv.putExtra(Config.TEXT,text);
//
//            LocalBroadcastManager.getInstance(context).sendBroadcast(msgrcv);

        }


        

    }

    private void parseNotification(StatusBarNotification sbn) {
        Bundle extras = sbn.getNotification().extras;
        String text = extras.getCharSequence("android.text").toString();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }
}
