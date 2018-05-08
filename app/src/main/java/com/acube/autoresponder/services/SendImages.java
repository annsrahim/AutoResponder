package com.acube.autoresponder.services;

import android.content.Context;
import android.content.Intent;
import android.service.notification.StatusBarNotification;

import com.acube.autoresponder.utils.Utils;

/**
 * Created by Anns on 03/05/18.
 */

public class SendImages implements Runnable {


    private Context context;
    private CustomNotificaionUtils customNotificaionUtils;
    private StatusBarNotification sbn;
    private boolean isNextMessageAvailable;
    private String mobileNumber;

    SendImages(Context context, CustomNotificaionUtils customNotificaionUtils, StatusBarNotification sbn, boolean isNextMessageAvailable, String mobileNumber) {
        this.context = context;
        this.customNotificaionUtils = customNotificaionUtils;
        this.sbn = sbn;
        this.isNextMessageAvailable = isNextMessageAvailable;
        this.mobileNumber = mobileNumber;
    }

    @Override
    public void run() {
        Utils.sendWhatsappImage(context,mobileNumber,customNotificaionUtils,sbn,isNextMessageAvailable);
    }


}
