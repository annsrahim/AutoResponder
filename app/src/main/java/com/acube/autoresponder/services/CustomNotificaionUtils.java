package com.acube.autoresponder.services;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;

import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * Created by Anns on 09/04/18.
 */

public class CustomNotificaionUtils {

    MessageDatabase messageDatabase;
    Context context;

    public CustomNotificaionUtils(MessageDatabase messageDatabase, Context context) {
        this.messageDatabase = messageDatabase;
        this.context = context;

    }

    public boolean isNotificationRepeated(StatusBarNotification sbn) {
        boolean isNewNotification;
        MessageDatabase messageDatabase = Utils.getMessageDatabase(context);
        Messages check = messageDatabase.daoAcess().checkMessageExists(sbn.getNotification().when);
        isNewNotification = check == null;
        return isNewNotification;
    }



    public void scheduledReply(final StatusBarNotification scheduledSbn)
    {
        ScheduledExecutorService scheduledExecutorService =
                Executors.newScheduledThreadPool(5);

        ScheduledFuture scheduledFuture =
                scheduledExecutorService.schedule(new Callable() {
                                                      public Object call() throws Exception {
                                                           replyNotification(scheduledSbn,Utils.getMobileNumber(scheduledSbn.getTag()));
                                                          return "Called!";
                                                      }
                                                  },
                        0,
                        TimeUnit.SECONDS);
    }


    public void replyNotification(StatusBarNotification sn, String mobileNumber) {
        String replyMessage = "Hi Hello";
        Messages lastMessage = messageDatabase.daoAcess().getMessage(mobileNumber);
        if (lastMessage != null) {
            int status = lastMessage.getStatus();
            replyMessage = messageDatabase.daoAcess().getTemplateByOrderNo(status);
            NotificationReplyService.sendReply(sn.getNotification(), context, replyMessage);
            lastMessage.setStatus(status+1);
            lastMessage.setQueue(0);
            messageDatabase.daoAcess().updateRecord(lastMessage);

        }


    }
}
