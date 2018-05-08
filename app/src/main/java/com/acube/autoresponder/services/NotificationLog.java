package com.acube.autoresponder.services;

import android.service.notification.StatusBarNotification;

/**
 * Created by Anns on 08/05/18.
 */

public class NotificationLog {
    public String contactNumber;
    public StatusBarNotification statusBarNotification;
    public int replyStatus=0;

    public NotificationLog(String contactNumber, StatusBarNotification statusBarNotification,int replyStatus) {
        this.contactNumber = contactNumber;
        this.statusBarNotification = statusBarNotification;
    }
}
