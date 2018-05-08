package com.acube.autoresponder.services;

import android.content.Context;
import android.service.notification.StatusBarNotification;

import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;

import javax.security.auth.callback.Callback;

/**
 * Created by Anns on 03/05/18.
 */

public class ReplyTask implements Runnable {


    private MessageDatabase messageDatabase;
    private Context context;
    private StatusBarNotification statusBarNotification;


    public ReplyTask(Context context, StatusBarNotification statusBarNotification) {
        this.messageDatabase = Utils.getMessageDatabase(context);
        this.context = context;
        this.statusBarNotification = statusBarNotification;
       
    }

    @Override
    public void run() {
            replyNotification();
    }

    public void replyNotification() {
        String mobileNumber = Utils.getMobileNumber(statusBarNotification.getTag());
        String replyMessage = "Hi Hello";
        Messages lastMessage = messageDatabase.daoAcess().getMessage(mobileNumber);
        if(lastMessage !=null)

        {
            int status = lastMessage.getStatus();
            replyMessage = messageDatabase.daoAcess().getTemplateByOrderNo(status)+mobileNumber;
            NotificationReplyService.sendReply(statusBarNotification.getNotification(), context, replyMessage);
            lastMessage.setStatus(status + 1);
            lastMessage.setQueue(0);
            messageDatabase.daoAcess().updateRecord(lastMessage);

        }


    }


}



