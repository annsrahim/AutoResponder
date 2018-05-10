package com.acube.autoresponder.services;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.SharedPreferenceUtils;
import com.acube.autoresponder.utils.Utils;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anns on 23/03/18.
 */

public class NotificationService extends NotificationListenerService {
    Context context;
    MessageDatabase messageDatabase;
    CustomNotificaionUtils customNotificaionUtils;
    Handler handler = new Handler();
    ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(5);
    int messageDelay;
    int lastMessageDelay;



    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        messageDatabase = Room.databaseBuilder(context,MessageDatabase.class,"auto-respond").allowMainThreadQueries().build();
        customNotificaionUtils = new CustomNotificaionUtils(messageDatabase,context);
        messageDelay = SharedPreferenceUtils.getIntData(context,Config.MESSAGE_DELAY);
        lastMessageDelay = SharedPreferenceUtils.getIntData(context,Config.LAST_MESSAGE_DELAY);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if(sbn.getTag()!=null && sbn.getPackageName().equalsIgnoreCase("com.whatsapp"))
        {
//            if(customNotificaionUtils.isNotificationRepeated(sbn)) //Check Whether old notification is repeating
//            {
//                addNotificationInDb(sbn);
//            }
//           Log.d("Noti Count ====",Utils.notificationLogs.size()+" Count");
           parseNotification(sbn);
        }


    }
    private void addNotificationInDb(StatusBarNotification sbn)
    {
        MessageDatabase msDb = Utils.getMessageDatabase(this);


        Messages isNumberAvailable = msDb.daoAcess().getMessage(Utils.getMobileNumber(sbn.getTag()));
        if(isNumberAvailable==null)
        {
            Messages messages = new Messages();
            messages.setContact_number(Utils.getMobileNumber(sbn.getTag()));
            Bundle extras = sbn.getNotification().extras;
            String text = extras.getCharSequence("android.text").toString();
            messages.setMessage_text(text);
            messages.setMessage_time(sbn.getNotification().when);
            messages.setWaitingQueue(1);
            messages.setQueue(0);
            messages.setStatus(1);
            messageDatabase.daoAcess().insertOnlySingleRecord(messages);
            NotificationLog log = new NotificationLog(Utils.getMobileNumber(sbn.getTag()),sbn,0);
            Utils.notificationLogs.add(log);

        }
        else
        {
            if(isNumberAvailable.getQueue()==0)
            {
                isNumberAvailable.setWaitingQueue(1);
                messageDatabase.daoAcess().updateRecord(isNumberAvailable);
                Utils.updateStatusBarNotification(isNumberAvailable.getContact_number(),sbn);
            }

        }


//
//        Messages messages = new Messages();
//        messages.setContact_number(Utils.getMobileNumber(sbn.getTag()));
//        Bundle extras = sbn.getNotification().extras;
//        String text = extras.getCharSequence("android.text").toString();
//        messages.setMessage_text(text);
//        messages.setMessage_time(sbn.getNotification().when);
//        messages.setQueue(0);
//        messages.setStatus(1);
//        final Messages isNumberAvailable = msDb.daoAcess().getMessage(Utils.getMobileNumber(sbn.getTag()));
//        if(isNumberAvailable==null)
//        {
//
//            msDb.daoAcess().insertOnlySingleRecord(messages);
//            NotificationLog log = new NotificationLog(Utils.getMobileNumber(sbn.getTag()),sbn,0);
//            Utils.notificationLogs.add(log);
//        }
//        else
//        {
//            Utils.updateReplyStatus(context,isNumberAvailable.getContact_number());
//        }
//        msDb.close();

    }


    private void parseNotification(final StatusBarNotification sbn) {
        Log.d("t______",sbn.getId()+" : ID");
        Messages messages = new Messages();
        messages.setContact_number(Utils.getMobileNumber(sbn.getTag()));
        Bundle extras = sbn.getNotification().extras;
        String text = extras.getCharSequence("android.text").toString();
        messages.setMessage_text(text);
        messages.setMessage_time(sbn.getNotification().when);
        messages.setQueue(1);
        messages.setStatus(1);

        try
        {

            if(customNotificaionUtils.isNotificationRepeated(sbn)) //Check Whether old notification is repeating
            {
                final Messages isNumberAvailable = messageDatabase.daoAcess().getMessage(Utils.getMobileNumber(sbn.getTag()));
                if(isNumberAvailable==null)
                {
                    messages.setLast_reply_time(System.currentTimeMillis());
                    messageDatabase.daoAcess().insertOnlySingleRecord(messages);

                    ReplyTask replyTask = new ReplyTask(context,sbn);
                    scheduledExecutorService.schedule(replyTask,messageDelay, TimeUnit.MINUTES);
                    NotificationLog log = new NotificationLog(Utils.getMobileNumber(sbn.getTag()),sbn,0);
                    Utils.notificationLogs.add(log);


                }
                else
                {
                    long timeDifferenceForLastRep = Utils.timeDifferenceOfLastReply(isNumberAvailable.getLast_reply_time());
                    Log.i("Time",isNumberAvailable.getContact_number()+"---"+timeDifferenceForLastRep);

                    if(isNumberAvailable.getQueue()==0 && timeDifferenceForLastRep>5)
                    {

                        int templateMessageCount = messageDatabase.daoAcess().getTemplateMessageCount();
                        Utils.updateStatusBarNotification(isNumberAvailable.getContact_number(),sbn);
                        if(isNumberAvailable.getStatus()<=templateMessageCount)
                        {
                            if(isNumberAvailable.getImageStatus()==0)
                            {

                                int imageIndex = Utils.getIndexForImage(getApplicationContext())+1;

                                    ReplyTask replyTask = new ReplyTask(this,sbn);
                                    if(isNumberAvailable.getStatus()==imageIndex)
                                        isNumberAvailable.setImageStatus(1);
                                    isNumberAvailable.setQueue(1);
                                    isNumberAvailable.setLast_reply_time(System.currentTimeMillis());
                                    messageDatabase.daoAcess().updateRecord(isNumberAvailable);
                                    scheduledExecutorService.schedule(replyTask,messageDelay, TimeUnit.MINUTES);
                            }
                            else if(isNumberAvailable.getImageStatus()==1)
                            {

                                Utils.updateReplyStatus(context,isNumberAvailable.getContact_number(),1);
                            }

                        }
                        else
                        {

                            if(isNumberAvailable.getWaitingQueue()==0)
                            {
                                Log.i("DOne","Last message");
                                isNumberAvailable.setWaitingQueue(1);
                                messageDatabase.daoAcess().updateRecord(isNumberAvailable);
                               scheduledExecutorService.schedule(new Runnable() {
                                   @Override
                                   public void run() {
                                       String replyMessage = SharedPreferenceUtils.getStringData(context,Config.LAST_MESSAGE_TEMPLATE);
                                       NotificationReplyService.sendReply(sbn.getNotification(), context, replyMessage);
                                   }
                               },lastMessageDelay,TimeUnit.HOURS);

                            }
                            else
                            {
                                Log.i("DOne","All Messages Sent");
                            }
                        }


                    }


                }
//                else if(isNumberAvailable.getImageStatus()==1)
//                {
//
//                    int templateMessageCount = messageDatabase.daoAcess().getTemplateMessageCount();
//                    isNumberAvailable.setImageStatus(2);
//                    isNumberAvailable.setQueue(1);
//                    messageDatabase.daoAcess().updateRecord(isNumberAvailable);
//                    String mobileNumber = isNumberAvailable.getContact_number();
//                    final boolean isNextMessageAvailbale;
//                    isNextMessageAvailbale = isNumberAvailable.getStatus() != templateMessageCount;
////                    SendImages sendImages  = new SendImages(context,customNotificaionUtils,sbn,isNextMessageAvailbale,mobileNumber);
////                    handler.postDelayed(sendImages,5000);
////                    final Runnable afterExe = new Runnable() {
////                        @Override
////                        public void run() {
////                            if(isNextMessageAvailbale)
////                            {
////                                ReplyTask replyTask = new ReplyTask(context,sbn);
////                                scheduledExecutorService.schedule(replyTask,2, TimeUnit.SECONDS);
////                            }
////
////                        }
////                    };
////                    handler.postDelayed(new Runnable() {
////                              }
////                    },5000); @Override
////                        public void run() {
////                            Utils.sendWhatsappImage(context,isNumberAvailable.getContact_number(),customNotificaionUtils,sbn,isNextMessageAvailbale);
//////                            handler.postDelayed(afterExe,0);
////                        }
////                    },5000);
//
//
//
//
//                }

//                else
//                {
//                    isNumberAvailable.setMessage_text(text);
//                    isNumberAvailable.setMessage_time(sbn.getNotification().when);
//
//                    if(isNumberAvailable.getQueue()==0)
//                    {
//                        int templateMessageCount = messageDatabase.daoAcess().getTemplateMessageCount();
//                        if(isNumberAvailable.getStatus()<=templateMessageCount)
//                        {
//
//                            int imageIndex = Utils.getIndexForImage(getApplicationContext())+1;
//                            if(isNumberAvailable.getStatus()==imageIndex)
//                            {
//                                isNumberAvailable.setQueue(1);
//                                isNumberAvailable.setImageStatus(1);
//                                ReplyTask replyTask = new ReplyTask(context,sbn);
//                                scheduledExecutorService.schedule(replyTask,5, TimeUnit.SECONDS);
//
////                                customNotificaionUtils.isImageTaskAvailable = true;
////                                customNotificaionUtils.scheduledReply(sbn);
//                                String mobileNumber = isNumberAvailable.getContact_number();
//                                boolean isNextMessageAvailbale;
////                                    Utils.sendMultipleWhatsappImage(context,mobileNumber,customNotificaionUtils,sbn,false);
//                                isNextMessageAvailbale = isNumberAvailable.getStatus() != templateMessageCount;
////                                SendImages sendImages  = new SendImages(context,customNotificaionUtils,sbn,isNextMessageAvailbale,mobileNumber);
////                                handler.postDelayed(sendImages,5000);
//
//                            }
//                            else
//                            {
//                                isNumberAvailable.setQueue(1);
//                                isNumberAvailable.setImageStatus(0);
//                                ReplyTask replyTask = new ReplyTask(context,sbn);
//                                scheduledExecutorService.schedule(replyTask,5, TimeUnit.SECONDS);
//                            }
//
//                        }
//                        else
//                        {
//                            if(isNumberAvailable.getStatus()==templateMessageCount+1)
//                            {
//                                Log.d(Config.TAG, SharedPreferenceUtils.getStringData(context,Config.Image1Path));
//
////                                String mobileNumber = isNumberAvailable.getContact_number();
////                                Utils.sendMultipleWhatsappImage(context,mobileNumber);
//                                customNotificaionUtils.scheduledLastReply(sbn);
//                                isNumberAvailable.setStatus(templateMessageCount+3);
//                            }
//
//
//                            isNumberAvailable.setQueue(0);
//                        }
//
//
//                    }
//
//                    messageDatabase.daoAcess().updateRecord(isNumberAvailable);
//
//                }



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

    }





    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

    }
}
