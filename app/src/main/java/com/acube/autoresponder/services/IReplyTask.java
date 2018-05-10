package com.acube.autoresponder.services;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;

import java.util.concurrent.Callable;

/**
 * Created by Anns on 04/05/18.
 */

public class IReplyTask implements Runnable {

    private MessageDatabase messageDatabase;
    private Context context;
    private StatusBarNotification statusBarNotification;
    private Messages messages;


    public IReplyTask(Context context, StatusBarNotification statusBarNotification,Messages mes) {
        this.messageDatabase = Utils.getMessageDatabase(context);
        this.context = context;
        this.statusBarNotification = statusBarNotification;
        this.messages = mes;

    }

    @Override
    public void run() {
        replyNotification();
    }

    public void replyNotification() {

            int imageIndex = Utils.getIndexForImage(context)+1;
            int status = messages.getStatus();
            String replyMessage = messageDatabase.daoAcess().getTemplateByOrderNo(status);
            NotificationReplyService.sendReply(statusBarNotification.getNotification(), context, replyMessage);
            if(messages.getStatus()==imageIndex)
                messages.setImageStatus(1);
            messages.setStatus(status+1);
            messages.setQueue(0);
            messageDatabase.daoAcess().updateRecord(messages);
    }
}
