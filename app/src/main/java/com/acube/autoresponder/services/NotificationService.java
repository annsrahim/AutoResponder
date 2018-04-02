package com.acube.autoresponder.services;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;

/**
 * Created by Anns on 23/03/18.
 */

public class NotificationService extends NotificationListenerService {
    Context context;
    MessageDatabase messageDatabase;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        messageDatabase = Room.databaseBuilder(context,MessageDatabase.class,"auto-respond").build();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if(sbn.getTag()!=null && sbn.getPackageName().equalsIgnoreCase("com.whatsapp"))
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
        Messages messages = new Messages();
        messages.setContact_number(Utils.getMobileNumber(sbn.getTag()));
        Bundle extras = sbn.getNotification().extras;
        String text = extras.getCharSequence("android.text").toString();
        messages.setMessage_text(text);
        messages.setMessage_time(sbn.getNotification().when);

       Messages check =  messageDatabase.daoAcess().checkMessageExists(sbn.getNotification().when);
       if(check == null)
           messageDatabase.daoAcess().insertOnlySingleRecord(messages);
        else
            Log.i("Record Status","Record Already inserted");
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }
}
