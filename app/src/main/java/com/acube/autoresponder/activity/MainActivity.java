package com.acube.autoresponder.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
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
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.acube.autoresponder.Config;
import com.acube.autoresponder.R;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.Messages;
import com.acube.autoresponder.services.WhatsAppAccessbilityService;
import com.acube.autoresponder.utils.SharedPreferenceUtils;
import com.acube.autoresponder.utils.Utils;
import com.acube.autoresponder.utils.ui.ViewProxy;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_CAMERA = 22;
    private static final int REQUEST_CODE_RESOLVE_ERR = 11;
    private static final int SELECT_FILE = 33 ;
    TableLayout tab;
    MessageDatabase messageDatabase;
    private ListView list;
    List<String> incomingChats = new ArrayList<>();
    ArrayAdapter<String> adapter;
    Toolbar toolbar;
    private GoogleApiClient mGoogleApiClient;
    ConnectionResult mConnectionResult;
    private String userChoosenTask="";
    private ImageView imageView1,imageView2;
    private int selectedImage = 1;
    Bitmap bmpImage1,bmpImage2;

   /*
    private TextView recordTimeText;
    private ImageButton audioSendButton;
    private View recordPanel;
    private View slideText;
    private float startedDraggingX = -1;
    private float distCanMove = dp(80);
    private Timer timer;
    private long startTime = 0L;
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L; */

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initToolbar();
        initUI();


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
/*
    private boolean setAudioRecordOptions(View view, MotionEvent motionEvent) {

        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                    .getLayoutParams();
            params.leftMargin = dp(30);
            slideText.setLayoutParams(params);
            ViewProxy.setAlpha(slideText, 1);
            startedDraggingX = -1;
            // startRecording();
            startrecord();
            audioSendButton.getParent()
                    .requestDisallowInterceptTouchEvent(true);
            recordPanel.setVisibility(View.VISIBLE);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_UP
                || motionEvent.getAction() == MotionEvent.ACTION_CANCEL) {
            startedDraggingX = -1;
            stoprecord();
            // stopRecording(true);
        } else if (motionEvent.getAction() == MotionEvent.ACTION_MOVE) {
            float x = motionEvent.getX();
            if (x < -distCanMove) {
                stoprecord();
                // stopRecording(false);
            }
            x = x + ViewProxy.getX(audioSendButton);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) slideText
                    .getLayoutParams();
            if (startedDraggingX != -1) {
                float dist = (x - startedDraggingX);
                params.leftMargin = dp(30) + (int) dist;
                slideText.setLayoutParams(params);
                float alpha = 1.0f + dist / distCanMove;
                if (alpha > 1) {
                    alpha = 1;
                } else if (alpha < 0) {
                    alpha = 0;
                }
                ViewProxy.setAlpha(slideText, alpha);
            }
            if (x <= ViewProxy.getX(slideText) + slideText.getWidth()
                    + dp(30)) {
                if (startedDraggingX == -1) {
                    startedDraggingX = x;
                    distCanMove = (recordPanel.getMeasuredWidth()
                            - slideText.getMeasuredWidth() - dp(48)) / 2.0f;
                    if (distCanMove <= 0) {
                        distCanMove = dp(80);
                    } else if (distCanMove > dp(80)) {
                        distCanMove = dp(80);
                    }
                }
            }
            if (params.leftMargin > dp(30)) {
                params.leftMargin = dp(30);
                slideText.setLayoutParams(params);
                ViewProxy.setAlpha(slideText, 1);
                startedDraggingX = -1;
            }
        }
        view.onTouchEvent(motionEvent);
        return true;
    }

    private void stoprecord() {
        if (timer != null) {
            timer.cancel();
        }
        if (recordTimeText.getText().toString().equals("00:00")) {
            return;
        }
        recordTimeText.setText("00:00");
        vibrate();

    }
    class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            final String hms = String.format(
                    "%02d:%02d",
                    TimeUnit.MILLISECONDS.toMinutes(updatedTime)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(updatedTime)),
                    TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(updatedTime)));
            long lastsec = TimeUnit.MILLISECONDS.toSeconds(updatedTime)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                    .toMinutes(updatedTime));
            System.out.println(lastsec + " hms " + hms);
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    try {
                        if (recordTimeText != null)
                            recordTimeText.setText(hms);
                    } catch (Exception e) {
                        // TODO: handle exception
                    }

                }
            });
        }
    }
    private void vibrate() {
        try {
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(200);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void startrecord() {
        startTime = SystemClock.uptimeMillis();
        timer = new Timer();
        MyTimerTask myTimerTask = new MyTimerTask();
        timer.schedule(myTimerTask, 1000, 1000);
        vibrate();

    }

    public static int dp(float value) {
        return (int) Math.ceil(1 * value);
    } */

    private void initUI() {
        list = (ListView)findViewById(R.id.list);
        imageView1 = (ImageView)findViewById(R.id.ivImage1);
        imageView2 = (ImageView)findViewById(R.id.ivImage2);
/*
        recordPanel = findViewById(R.id.record_panel);
        recordTimeText = (TextView) findViewById(R.id.recording_time_text);
        slideText = findViewById(R.id.slideText);
        audioSendButton = (ImageButton) findViewById(R.id.chat_audio_send_button);
        TextView textView = (TextView) findViewById(R.id.slideToCancelTextView);
        textView.setText("SlideToCancel");  */

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

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        Utils.showToast(this,"Connection Suspended "+i);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        if (!result.hasResolution()) {

            // show the localized error dialog.
            GoogleApiAvailability.getInstance().getErrorDialog(this, result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  The failure has a resolution. Resolve it.
         *  Called typically when the app is not yet authorized, and an  authorization
         *  dialog is displayed to the user.
         */

        try {

            result.startResolutionForResult(this, REQUEST_CODE_RESOLVE_ERR);

        } catch (IntentSender.SendIntentException e) {

            Log.e(Config.TAG, "Exception while starting resolution activity", e);
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

       // new DatabaseAsync().execute();
        String path1 = SharedPreferenceUtils.getStringData(this,Config.Image1Path);
        String path2 = SharedPreferenceUtils.getStringData(this,Config.Image2Path);
//        Utils.sendMultipleWhatsappImage(this,path1,path2);
    }

    public void sendTestMessage()
    {

        if(mGoogleApiClient==null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mGoogleApiClient==null)
        {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
//        mGoogleApiClient.connect();

        if(!WhatsAppAccessbilityService.isAccessibilityOn(this,WhatsAppAccessbilityService.class))
        {
            Intent intent = new Intent (Settings.ACTION_ACCESSIBILITY_SETTINGS);
            startActivity (intent);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mGoogleApiClient!=null)
        {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
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



}
