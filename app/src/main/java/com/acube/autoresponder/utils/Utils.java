package com.acube.autoresponder.utils;

import android.arch.persistence.room.Room;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.acube.autoresponder.R;
import com.acube.autoresponder.database.MessageDatabase;

/**
 * Created by Anns on 23/03/18.
 */

public class Utils {
    public static boolean notificationAccessStatus(Context context)
    {
        String enabledListeners = "";
        enabledListeners = Settings.Secure.getString(context.getContentResolver(),"enabled_notification_listeners");
        if (enabledListeners!=null && enabledListeners.contains("com.acube.autoresponder"))
        {
            return true;
            //service is enabled do something
        } else {
            return false;
            //service is not enabled try to enabled by calling...

        }
    }
    public static void showToast(Context context,String msg)
    {
        Toast.makeText(context,msg, Toast.LENGTH_SHORT).show();
    }
    public static String getMobileNumber(String contact_number)
    {
        return contact_number.substring(0,contact_number.length()-15);
    }
    public static MessageDatabase getMessageDatabase(Context context)
    {
       return Room.databaseBuilder(context,MessageDatabase.class,"auto-respond").allowMainThreadQueries().build();
    }

    public static void showAlert(Context context,String title,String msg)
    {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                    }});

    }
}
