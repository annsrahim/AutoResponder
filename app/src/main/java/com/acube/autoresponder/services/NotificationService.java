package com.acube.autoresponder.services;

import android.app.Notification;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;
import com.robj.notificationhelperlibrary.utils.NotificationUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import models.Action;

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
        messageDatabase = Room.databaseBuilder(context,MessageDatabase.class,"auto-respond").allowMainThreadQueries().build();
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

    private void parseNotification(final StatusBarNotification sbn) {
        Messages messages = new Messages();
        messages.setContact_number(Utils.getMobileNumber(sbn.getTag()));
        Bundle extras = sbn.getNotification().extras;
        String text = extras.getCharSequence("android.text").toString();
        messages.setMessage_text(text);
        messages.setMessage_time(sbn.getNotification().when);
        try
        {
            Messages check =  messageDatabase.daoAcess().checkMessageExists(sbn.getNotification().when);
            if(check == null)
            {
                Messages isNumberAvailable = messageDatabase.daoAcess().getMessage(Utils.getMobileNumber(sbn.getTag()));
                if(isNumberAvailable==null)
                    messageDatabase.daoAcess().insertOnlySingleRecord(messages);
                else
                {
                    isNumberAvailable.setMessage_text(text);
                    isNumberAvailable.setMessage_time(sbn.getNotification().when);
                    messageDatabase.daoAcess().updateRecord(isNumberAvailable);
                }
            }

            else
            {
                Log.i("Record Status","Record Already inserted");
                return;
            }

        }
        catch (Exception ae)
        {
            Log.d("Excep",ae.getLocalizedMessage());
        }


        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(5);

        ScheduledFuture scheduledFuture =
                scheduledExecutorService.schedule(new Callable() {
                                                      public Object call() throws Exception {
                                                          replyTest(sbn,Utils.getMobileNumber(sbn.getTag()));
                                                          return "Called!";
                                                      }
                                                  },
                        0,
                        TimeUnit.SECONDS);



    }

    private void replyTest(StatusBarNotification sn, String mobileNumber) {
        String replyMessage = "Hi Hello";
        Messages lastMessage = messageDatabase.daoAcess().getMessage(mobileNumber);
        if(lastMessage!=null)
        {
            int status = lastMessage.getStatus();
            switch (status)
            {
                case 0:
                    replyMessage = "Hi Hello";
                    NotificationReplyService.sendReply(sn.getNotification(),getApplicationContext(),replyMessage);
                    lastMessage.setStatus(1);
                     messageDatabase.daoAcess().updateRecord(lastMessage);
                    break;
                case 1:
                    replyMessage = "All Good,Do u want to exchange photos?";
                    NotificationReplyService.sendReply(sn.getNotification(),getApplicationContext(),replyMessage);
                    lastMessage.setStatus(2);
                    messageDatabase.daoAcess().updateRecord(lastMessage);
                    break;
                case 2:
                    replyMessage = "https://c.tadst.com/gfx/1200x630/tree-winter-solstice.jpg?1";
                    for(int j=0;j<3;j++)
                         NotificationReplyService.sendReply(sn.getNotification(),getApplicationContext(),replyMessage);
                    lastMessage.setStatus(3);
                    messageDatabase.daoAcess().updateRecord(lastMessage);
                    break;

            }
        }


//      Action action = NotificationUtils.getQuickReplyAction(sn.getNotification(),sn.getPackageName());
//        try {
//            action.sendReply(getApplicationContext(),"Hello Hai");
//        } catch (PendingIntent.CanceledException e) {
//            e.printStackTrace();
//        }
    }



    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }
}
