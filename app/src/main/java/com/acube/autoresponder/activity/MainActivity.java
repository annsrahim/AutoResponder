package com.acube.autoresponder.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.KeyguardManager;
import android.app.NotificationManager;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.R;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.services.NotificationService;
import com.acube.autoresponder.services.ReplyTask;
import com.acube.autoresponder.services.WhatsAppAccessbilityService;
import com.acube.autoresponder.utils.SharedPreferenceUtils;
import com.acube.autoresponder.utils.Utils;
import com.acube.autoresponder.utils.ui.ViewProxy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_CODE_RESOLVE_ERR = 11;
    private static final int SELECT_FILE = 33 ;


    MessageDatabase messageDatabase;
    private ListView list;
    List<String> incomingChats = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Toolbar toolbar;

    private String userChoosenTask="";
    private ImageView imageView1,imageView2;
    private int selectedImage = 1;
    Bitmap bmpImage1,bmpImage2;
    TextView tvMessageDelay,tvLastMessageDelay,tvTemplateLastMessage;
    ImageButton ibAddMessageDelay,ibSubMessageDelay,ibLastAddMessageDelay,ibLastSubMessageDelay,ibTemplateLastMessage;
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor  = new ScheduledThreadPoolExecutor(2);


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initUI();
        disableScreenLock();
        setMessageinterval();
        setLastMessageTemplate();
        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImage = 1;
                selectImage();
            }
        });
        imageView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedImage = 2;
                selectImage();
            }
        });
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

    private void setLastMessageTemplate() {
        String lastMessageTemplate = SharedPreferenceUtils.getStringData(this,Config.LAST_MESSAGE_TEMPLATE);
        tvTemplateLastMessage.setText(lastMessageTemplate);
    }

    private void setMessageinterval() {
        int messageDelay = SharedPreferenceUtils.getIntData(this,Config.MESSAGE_DELAY);
        int lastMessageDelay = SharedPreferenceUtils.getIntData(this,Config.LAST_MESSAGE_DELAY);
        tvMessageDelay.setText(String.valueOf(messageDelay));
        tvLastMessageDelay.setText(String.valueOf(lastMessageDelay));

    }

    private void disableScreenLock() {
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }



    private void initUI() {
        list = (ListView)findViewById(R.id.list);
        imageView1 = (ImageView)findViewById(R.id.ivImage1);
        imageView2 = (ImageView)findViewById(R.id.ivImage2);
        tvMessageDelay = (TextView) findViewById(R.id.tv_message_delay);
        tvMessageDelay.setText(String.valueOf(Config.MessageDelay));
        tvLastMessageDelay = (TextView)findViewById(R.id.tv_last_message_delay);
        ibAddMessageDelay  = (ImageButton)findViewById(R.id.ib_add_message_delay);
        ibSubMessageDelay  = (ImageButton)findViewById(R.id.ib_sub_message_delay);
        ibLastAddMessageDelay = (ImageButton)findViewById(R.id.ib_add_last_message_delay);
        ibLastSubMessageDelay = (ImageButton)findViewById(R.id.ib_sub_last_message_delay);
        tvTemplateLastMessage = (TextView)findViewById(R.id.tv_template_last_message);
        ibTemplateLastMessage = (ImageButton)findViewById(R.id.ib_template_last_message);
        ibTemplateLastMessage.setOnClickListener(this);
        ibLastAddMessageDelay.setOnClickListener(this);
        ibLastSubMessageDelay.setOnClickListener(this);
        ibAddMessageDelay.setOnClickListener(this);
        ibSubMessageDelay.setOnClickListener(this);


    }

    private void initToolbar() {
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.app_name);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.WHITE));
    }

    public void clearDB(View view) {
        try{
            messageDatabase.daoAcess().clearDatabase();
            Utils.notificationLogs.clear();
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

    @Override
    public void onClick(View view) {
        int id= view.getId();
        switch (id)
        {
            case R.id.ib_add_message_delay:
                    addMessageTime();
                break;

            case R.id.ib_sub_message_delay:
                    subMessageTime();
                break;
            case R.id.ib_add_last_message_delay:
                    addLastMessageTime();
                break;
            case R.id.ib_sub_last_message_delay:
                    subLastMessageTime();
                break;
            case R.id.ib_template_last_message:
                    editLastMessageTemplate();
                break;
        }


    }

    private void editLastMessageTemplate() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("EDIT");
        alertDialog.setMessage("Edit the template");

        final EditText input = new EditText(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(tvTemplateLastMessage.getText());
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_mode_edit_deep_purple);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(!input.getText().toString().equals(""))
                        {
                            SharedPreferenceUtils.setStringData(MainActivity.this,Config.LAST_MESSAGE_TEMPLATE,input.getText().toString());
                            tvTemplateLastMessage.setText(input.getText().toString());
                            dialog.cancel();
                        }
                        else
                            Utils.showToast(MainActivity.this,"Please enter the template");

                    }
                });

        alertDialog.setNegativeButton("NO",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }

    private void subLastMessageTime() {
        int messageDelay = SharedPreferenceUtils.getIntData(this,Config.LAST_MESSAGE_DELAY);
        messageDelay--;
        if(messageDelay<1)
            return;
        SharedPreferenceUtils.setIntData(this,Config.LAST_MESSAGE_DELAY,messageDelay);
        tvLastMessageDelay.setText(String.valueOf(messageDelay));
    }

    private void addLastMessageTime() {
        int messageDelay = SharedPreferenceUtils.getIntData(this,Config.LAST_MESSAGE_DELAY);
        messageDelay++;
        if(messageDelay>48)
            return;
        SharedPreferenceUtils.setIntData(this,Config.LAST_MESSAGE_DELAY,messageDelay);
        tvLastMessageDelay.setText(String.valueOf(messageDelay));
    }

    private void subMessageTime() {
        int messageDelay = SharedPreferenceUtils.getIntData(this,Config.MESSAGE_DELAY);
        messageDelay--;
        if(messageDelay<1)
            return;
        SharedPreferenceUtils.setIntData(this,Config.MESSAGE_DELAY,messageDelay);
        tvMessageDelay.setText(String.valueOf(messageDelay));
    }

    private void addMessageTime() {

        int messageDelay = SharedPreferenceUtils.getIntData(this,Config.MESSAGE_DELAY);
        messageDelay++;
        if(messageDelay>5)
            return;
        SharedPreferenceUtils.setIntData(this,Config.MESSAGE_DELAY,messageDelay);
        tvMessageDelay.setText(String.valueOf(messageDelay));
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


    }



    @Override
    protected void onResume() {
        super.onResume();
        scheduledThreadPoolExecutor.scheduleWithFixedDelay(checkAndReply,2,5,TimeUnit.SECONDS);
        if(!WhatsAppAccessbilityService.isAccessibilityOn(this,WhatsAppAccessbilityService.class))
        {
            Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity (intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }
    private void selectImage() {
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                boolean result= Utils.checkPermission(MainActivity.this);
                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
                    if(result)
                        cameraIntent();
                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
                    if(result)
                        galleryIntent();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    public void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
    public void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utils.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Take Photo"))
                        cameraIntent();
                    else if(userChoosenTask.equals("Choose from Library"))
                        galleryIntent();
                } else {
                    //code for deny
                }
                break;
        }
    }



    @SuppressWarnings("deprecation")
    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm=null;
        if (data != null) {
            try {
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(selectedImage==1)
        {
            imageView1.setImageBitmap(bm);
            bmpImage1 = bm;
            SharedPreferenceUtils.setStringData(this,Config.Image1Path,Utils.getImageUri(this,bm).toString());
        }
        else
        {
            imageView2.setImageBitmap(bm);
            bmpImage2 = bm;
            SharedPreferenceUtils.setStringData(this,Config.Image2Path,Utils.getImageUri(this,bm).toString());
        }
        
    }

    private void sendViaWhatsapp(Bitmap bm) {

        Intent sendIntent = new Intent("android.intent.action.SEND");
        File f=new File("path to the file");
//        Uri uri = Utils.getImageUri(this,bm);
        String path = SharedPreferenceUtils.getStringData(this,Config.Image1Path);
        Uri uri = Uri.parse(path);
        sendIntent.setComponent(new ComponentName("com.whatsapp","com.whatsapp.ContactPicker"));
        sendIntent.setType("image");
        sendIntent.putExtra(Intent.EXTRA_STREAM,uri);
        sendIntent.putExtra("jid", PhoneNumberUtils.stripSeparators("918197713454")+"@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_TEXT,"sample text you want to send along with the image");
        startActivity(sendIntent);
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(selectedImage==1)
        {
            imageView1.setImageBitmap(thumbnail);
            bmpImage1 = thumbnail;
        }
        else
        {
            imageView2.setImageBitmap(thumbnail);
            bmpImage2 = thumbnail;
        }

    }

    public Runnable checkAndReply = new Runnable() {
        @Override
        public void run() {
            checkForReplyMessages();
        }
    };


    public void checkForReplyMessages()
    {
        List<Messages> messagesList = messageDatabase.daoAcess().getAllMessages();
        int templateMessagesCount = messageDatabase.daoAcess().getTemplateMessageCount();
        for(Messages message: messagesList)
        {
            if(message.getQueue()==0)
            {
                if(message.getStatus()<=templateMessagesCount)
                {
                    StatusBarNotification sbn = Utils.getStatusNotification(message.getContact_number());
                    int replyStatus = Utils.getReplyStatus(message.getContact_number());
                    int imageIndex = Utils.getIndexForImage(getApplicationContext())+1;
                    if(replyStatus==0)
                    {
                        if(message.isImageStatus())
                        {
                            Log.d("Sending Image","True");
                            message.setImageStatus(false);
                            message.setQueue(1);
                            messageDatabase.daoAcess().updateRecord(message);
                            sendImageReply(message,templateMessagesCount);
                            return;
                        }
                        else
                        {
                            Log.d("Sending Image","False");
                            if(message.getStatus()==imageIndex)
                                message.setImageStatus(true);
                            ReplyTask replyTask = new ReplyTask(this,sbn);
                            message.setQueue(1);
                            messageDatabase.daoAcess().updateRecord(message);
                            Utils.updateReplyStatus(this,message.getContact_number());
                            scheduledThreadPoolExecutor.schedule(replyTask,10, TimeUnit.SECONDS);
                        }


                    }

                }
            }
            Log.d("T_____",message.getContact_number()+" "+message.getStatus());
        }
    }

    private void sendImageReply(Messages message, int templateMessagesCount) {
        String mobileNumber = message.getContact_number();
        final boolean isNextMessageAvailbale;
        isNextMessageAvailbale = message.getStatus() != templateMessagesCount;
    }


}
