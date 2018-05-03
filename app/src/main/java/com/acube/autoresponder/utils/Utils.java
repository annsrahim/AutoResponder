package com.acube.autoresponder.utils;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.persistence.room.Room;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.telephony.PhoneNumberUtils;
import android.widget.Toast;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.services.CustomNotificaionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

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

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static boolean checkPermission(final Context context)
    {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                    alertBuilder.setCancelable(true);
                    alertBuilder.setTitle("Permission necessary");
                    alertBuilder.setMessage("External storage permission is necessary");
                    alertBuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                        }
                    });
                    AlertDialog alert = alertBuilder.create();
                    alert.show();
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public static void sendWhatsappImage(Context context,String path)
    {
        Intent sendIntent = new Intent("android.intent.action.SEND");
        sendIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        File f=new File("path to the file");
        Uri uri = Uri.parse(path);
        sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.ContactPicker"));
        sendIntent.setType("image");
        sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("918197713454")+"@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT,"sample text you want to send along with the image");
        context.startActivity(sendIntent);
    }
    public static void sendMultipleWhatsappImage(Context context, String mobNumber, CustomNotificaionUtils customNotificaionUtils, StatusBarNotification sbn, boolean isNextMessageAvailable)
    {

        String path1 = SharedPreferenceUtils.getStringData(context,Config.Image1Path);
        String path2 = SharedPreferenceUtils.getStringData(context,Config.Image2Path);
        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_SEND_MULTIPLE);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Here are some files.");
        intent.setType("text/plain");
        intent.setType("image/jpeg"); /* This example is sharing jpeg images. */
        intent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.ContactPicker"));
        ArrayList<Uri> files = new ArrayList<Uri>();
        Uri uri1 = Uri.parse(path1);
        Uri uri2 = Uri.parse(path2);
        files.add(uri1);
        files.add(uri2);

//        intent.putExtra(Intent.EXTRA_TEXT, "Text caption message!!");
        intent.setType("text/plain");
        intent.setType("image/jpeg");
        intent.putExtra("jid", PhoneNumberUtils.stripSeparators(mobNumber)+"@s.whatsapp.net");
        intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM,files);


        int messageDelay  = SharedPreferenceUtils.getIntData(context,Config.MESSAGE_DELAY);
        messageDelay+=messageDelay;
        PendingIntent pendingIntent = PendingIntent.getActivity(context,444,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager am = (AlarmManager) context.getSystemService(Activity.ALARM_SERVICE);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE,messageDelay);
        am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        if(isNextMessageAvailable)
        {
            customNotificaionUtils.scheduleReplyAfterImage(sbn);
        }


    }


    public static int getIndexForImage(Context context)
    {
        SharedPreferences myPrefs;
        myPrefs = context.getSharedPreferences(Config.MyPREFERENCES, Context.MODE_PRIVATE);
        MessageDatabase messageDatabase = Utils.getMessageDatabase(context);
        int templateCount = messageDatabase.daoAcess().getTemplateMessageCount();
        return myPrefs.getInt(Config.IMAGE_INDEX, templateCount-1);
    }




}

