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
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list = (ListView)findViewById(R.id.list);
         adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1,incomingChats);
        list.setAdapter(adapter);
        messageDatabase = Utils.getMessageDatabase(this);
        new DatabaseAsync().execute();
        String enabledListeners = Settings.Secure.getString(this.getContentResolver(),
                "enabled_notification_listeners");

        if(!enabledListeners.contains("services.NotificationService"))
        {
            Utils.showToast(this,"Please enable notification access to avail the service");
            Intent intent = new Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS");
            startActivity(intent);
        }


    }


    private  class DatabaseAsync extends AsyncTask<Void,Void,Void>
    {

        @Override
        protected Void doInBackground(Void... voids) {
            List<Messages> messages = messageDatabase.daoAcess().getAllMessages();
            incomingChats.clear();
            for(Messages message: messages)
            {
                incomingChats.add(message.getContact_number()+" : "+message.getMessage_text());
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
//
        new DatabaseAsync().execute();
    }
}
