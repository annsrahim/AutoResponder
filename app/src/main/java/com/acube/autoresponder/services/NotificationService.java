package com.acube.autoresponder.services;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.SharedPreferenceUtils;
import com.acube.autoresponder.utils.Utils;

/**
 * Created by Anns on 23/03/18.
 */

public class NotificationService extends NotificationListenerService {
    Context context;
    MessageDatabase messageDatabase;
    CustomNotificaionUtils customNotificaionUtils;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        messageDatabase = Room.databaseBuilder(context,MessageDatabase.class,"auto-respond").allowMainThreadQueries().build();
        customNotificaionUtils = new CustomNotificaionUtils(messageDatabase,context);
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        if(sbn.getTag()!=null && sbn.getPackageName().equalsIgnoreCase("com.whatsapp"))
        {
            parseNotification(sbn);
        }


    }

    private void parseNotification(final StatusBarNotification sbn) {
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
                Messages isNumberAvailable = messageDatabase.daoAcess().getMessage(Utils.getMobileNumber(sbn.getTag()));
                if(isNumberAvailable==null)
                {

                    messageDatabase.daoAcess().insertOnlySingleRecord(messages);
                    customNotificaionUtils.scheduledReply(sbn);
                }
                else
                {
                    isNumberAvailable.setMessage_text(text);
                    isNumberAvailable.setMessage_time(sbn.getNotification().when);

                    if(isNumberAvailable.getQueue()==0)
                    {
                        int templateMessageCount = messageDatabase.daoAcess().getTemplateMessageCount();
                        if(isNumberAvailable.getStatus()<=templateMessageCount)
                        {

                            int imageIndex = Utils.getIndexForImage(getApplicationContext())+1;
                            if(isNumberAvailable.getStatus()==imageIndex)
                            {
                                isNumberAvailable.setQueue(1);
                                customNotificaionUtils.isImageTaskAvailable = true;
                                customNotificaionUtils.scheduledReply(sbn);
                                String mobileNumber = isNumberAvailable.getContact_number();

                                if(isNumberAvailable.getStatus()==templateMessageCount)
                                    Utils.sendMultipleWhatsappImage(context,mobileNumber,customNotificaionUtils,sbn,false);
                                else
                                    Utils.sendMultipleWhatsappImage(context,mobileNumber,customNotificaionUtils,sbn,true);
                            }
                            else
                            {
                                isNumberAvailable.setQueue(1);
                                customNotificaionUtils.isImageTaskAvailable = false;
                                customNotificaionUtils.scheduledReply(sbn);
                            }

                        }
                        else
                        {
                            if(isNumberAvailable.getStatus()==templateMessageCount+1)
                            {
                                Log.d(Config.TAG, SharedPreferenceUtils.getStringData(context,Config.Image1Path));

//                                String mobileNumber = isNumberAvailable.getContact_number();
//                                Utils.sendMultipleWhatsappImage(context,mobileNumber);
                                customNotificaionUtils.scheduledLastReply(sbn);
                                isNumberAvailable.setStatus(templateMessageCount+3);
                            }


                            isNumberAvailable.setQueue(0);
                        }


                    }

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

    }





    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        Log.i("Msg","Notification Removed");
    }
}
