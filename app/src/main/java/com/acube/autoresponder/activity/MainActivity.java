package com.acube.autoresponder.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.R;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    TableLayout tab;
    MessageDatabase messageDatabase;
    private ListView list;
    List<String> incomingChats = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        list = (ListView)findViewById(R.id.list);

         adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,incomingChats);
        list.setAdapter(adapter);
        messageDatabase = Utils.getMessageDatabase(this);
        new DatabaseAsync().execute();
        String enabledListeners = Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");

        if(enabledListeners==null || !enabledListeners.contains("services.NotificationService"))
        {
            Utils.showToast(this,"Please enable notification access to avail the service");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if(currentAPIVersion>=android.os.Build.VERSION_CODES.M)
        {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
                    int REQUEST_INTERNET = 11;
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET,Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_INTERNET);
                }
        }


    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.WHITE));
    }

    public void clearDB(View view) {
        try{
            messageDatabase.daoAcess().clearDatabase();
        }
        catch (Exception e)
        {
            Log.e("Error DB",e.getLocalizedMessage());
        }
        new DatabaseAsync().execute();
    }

    public void goToAddTemplate(View view) {

        Intent intent = new Intent(this,AddTemplateActivity.class);
        startActivity(intent);
    }


    private  class DatabaseAsync extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            List<Messages> messages = messageDatabase.daoAcess().getAllMessages();
            incomingChats.clear();
            for(Messages message: messages)
            {
                incomingChats.add(message.getContact_number()+" : "+message.getMessage_text()+" : "+message.getStatus()+"----"+message.getQueue());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            adapter.notifyDataSetChanged();
            super.onPostExecute(aVoid);
        }
    }


    public void doTestFunction(View view) {

        new DatabaseAsync().execute();
        sendTestMessage();

    }

    public void sendTestMessage()
    {


    }
}
