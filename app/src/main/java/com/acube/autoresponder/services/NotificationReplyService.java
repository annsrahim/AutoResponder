package com.acube.autoresponder.services;

import android.app.AlarmManager;
import android.app.Notification;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.RemoteInput;

import java.util.ArrayList;
import java.util.List;

import models.RemoteInputParcel;

/**
 * Created by Anns on 04/04/18.
 */

public class NotificationReplyService {


    public static void sendReply(Notification n, Context context, String replyMessage)
    {
        PendingIntent p=null;
        ArrayList<RemoteInput> actualInputs = new ArrayList<>();
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        NotificationCompat.WearableExtender wearableExtender = new NotificationCompat.WearableExtender(n);
        List<NotificationCompat.Action> actions = wearableExtender.getActions();
           for(int i=0;i<actions.size();i++)
           {
//               CharSequence actionTitle = actions.get(i).title;
//               String notificationTitle = "Reply to "+n.extras.getString("android.title");
//                if(!notificationTitle.equals(actionTitle))
//                    continue;
               p = actions.get(i).actionIntent;
               RemoteInput[] remoteInput = actions.get(i).getRemoteInputs();
               RemoteInputParcel remoteInputParcel = new RemoteInputParcel(remoteInput[i]);
               RemoteInput.Builder builder = new RemoteInput.Builder(remoteInputParcel.getResultKey());



               bundle.putCharSequence(remoteInputParcel.getResultKey(),replyMessage);
               builder.setLabel(remoteInputParcel.getLabel());
               builder.setChoices(remoteInputParcel.getChoices());
               builder.setAllowFreeFormInput(remoteInputParcel.isAllowFreeFormInput());
               builder.addExtras(remoteInputParcel.getExtras());
               actualInputs.add(builder.build());
               RemoteInput[] inputs = actualInputs.toArray(new RemoteInput[actualInputs.size()]);
               RemoteInput.addResultsToIntent(inputs, intent, bundle);

               try {


                        p.send(context, 0, intent);
                   actions.clear();
               } catch (Exception e) {
                   e.printStackTrace();
               }
           }



    }
}
