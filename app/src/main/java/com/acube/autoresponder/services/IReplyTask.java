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

public class IReplyTask implements Callable<Messages> {

    private MessageDatabase messageDatabase;
    private Context context;
    private StatusBarNotification statusBarNotification;

    public IReplyTask( Context context, MessageDatabase messageDatabase,StatusBarNotification statusBarNotification) {
        this.messageDatabase = messageDatabase;
        this.context = context;
        this.statusBarNotification = statusBarNotification;
    }

    @Override
    public Messages call() throws Exception {
        String mobileNumber = Utils.getMobileNumber(statusBarNotification.getTag());
        String replyMessage;
        Messages lastMessage = messageDatabase.daoAcess().getMessage(mobileNumber);
        if(lastMessage !=null)

        {
            int status = lastMessage.getStatus();
            replyMessage = messageDatabase.daoAcess().getTemplateByOrderNo(status);
            NotificationReplyService.sendReply(statusBarNotification.getNotification(), context, replyMessage);
            lastMessage.setStatus(status + 1);
            lastMessage.setQueue(0);
            messageDatabase.daoAcess().updateRecord(lastMessage);
        }
        return lastMessage;
    }
}
