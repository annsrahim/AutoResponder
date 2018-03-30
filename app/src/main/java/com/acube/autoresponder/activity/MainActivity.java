package com.acube.autoresponder.activity;

import android.Manifest;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.R;
import com.acube.autoresponder.utils.Utils;

import java.io.DataOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    TableLayout tab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tab = (TableLayout)findViewById(R.id.tab);
        sendWhatsAppMessage();
//       if(Utils.notificationAccessStatus(this))
//            broadCastLocal();
//       else
//       {
//           Utils.showToast(this,"Please Enable Notification Access");
//           startActivity(new Intent(
//                   "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS"));
//       }


    }

    private void sendWhatsAppMessage() {

    }

    public void broadCastLocal()
    {
        LocalBroadcastManager.getInstance(this).registerReceiver(onNotice,new IntentFilter(Config.Msg));
    }

    private BroadcastReceiver onNotice = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String pack = intent.getStringExtra(Config.PACKAGE);
            String title = intent.getStringExtra(Config.TITLE);
            String text = intent.getStringExtra(Config.TEXT);

            TableRow tr = new TableRow(getApplicationContext());
            tr.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,TableRow.LayoutParams.WRAP_CONTENT));
            TextView textview = new TextView(getApplicationContext());
            textview.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT,1.0f));
            textview.setTextSize(20);
            textview.setTextColor(Color.parseColor("#0B0719"));
            textview.setText(Html.fromHtml(pack +"<br><b>" + title + " : </b>" + text));
            tr.addView(textview);
            tab.addView(tr);


        }
    };


    public void doTestFunction(View view) {

        NotificationManager notificationManager = (NotificationManager)
                getSystemService(NOTIFICATION_SERVICE);

    }
}
