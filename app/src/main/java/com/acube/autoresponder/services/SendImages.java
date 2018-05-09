package com.acube.autoresponder.services;

import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;

/**
 * Created by Anns on 03/05/18.
 */

public class SendImages implements Runnable {


    private Context context;

    private String mobileNumber;
    Messages messages;
    private boolean isNextMessageAvailable;

   public SendImages(Context context, String mobileNumber, Messages messages, boolean isNextMessageAvailable) {
        this.context = context;
        this.mobileNumber = mobileNumber;
        this.messages = messages;
        this.isNextMessageAvailable = isNextMessageAvailable;
    }

    @Override
    public void run() {
        MessageDatabase messageDatabase = Utils.getMessageDatabase(context);
        messages.setQueue(0);
        if(isNextMessageAvailable)
            messages.setImageStatus(2);
        else
            messages.setImageStatus(0);
        messageDatabase.daoAcess().updateRecord(messages);
        Utils.sendWhatsappImage(context,mobileNumber);
    }


}
