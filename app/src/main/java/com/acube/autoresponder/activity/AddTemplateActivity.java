package com.acube.autoresponder.activity;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.acube.autoresponder.Config;
import com.acube.autoresponder.R;
import com.acube.autoresponder.database.MessageDatabase;
import com.acube.autoresponder.database.TemplateMessages;
import com.acube.autoresponder.utils.SharedPreferenceUtils;
import com.acube.autoresponder.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class AddTemplateActivity extends AppCompatActivity implements ITemplateListItemClickListener {

    ListView listView;
    Toolbar toolbar;
    TemplateListAdapter templateListAdapter;
    List<TemplateMessages> templateMessagesList = new ArrayList<>();
    MessageDatabase messageDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        initToolbar();
        init();
    }

    private void init() {
        listView = (ListView)findViewById(R.id.list);
        messageDatabase = Utils.getMessageDatabase(this);


        getTemplateMessages();



    }

    private void getTemplateMessages() {
        templateMessagesList = messageDatabase.daoAcess().getTemplateMessages();
        templateListAdapter = new TemplateListAdapter(templateMessagesList,this,this);
        listView.setAdapter(templateListAdapter);
    }

    private void initToolbar() {
        toolbar = (android.support.v7.widget.Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.template_list);
        toolbar.setTitleTextColor(ContextCompat.getColor(this,R.color.WHITE));
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.template_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.add_menu) {
            openAddTemplateDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void addTemplate(String templateMsg)
    {
        messageDatabase.daoAcess().insertTemplate(addTemplateMessage(templateMsg));
        getTemplateMessages();
    }



    private void openAddTemplateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddTemplateActivity.this);
        alertDialog.setTitle("ADD");
        alertDialog.setMessage("Add the template");

        final EditText input = new EditText(AddTemplateActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_mode_edit_deep_purple);

        alertDialog.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(!input.getText().toString().equals(""))
                {
                    addTemplate(input.getText().toString());

                }
                else
                    Utils.showToast(AddTemplateActivity.this,"Please enter the template");
                    }
                });

        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();

    }

    private void editTemplateDialog(final int itemId, String text, final int orderNo) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(AddTemplateActivity.this);
        alertDialog.setTitle("EDIT");
        alertDialog.setMessage("Edit the template");

        final EditText input = new EditText(AddTemplateActivity.this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        input.setText(text);
        alertDialog.setView(input);
        alertDialog.setIcon(R.drawable.ic_mode_edit_deep_purple);

        alertDialog.setPositiveButton("YES",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        if(!input.getText().toString().equals(""))
                        {
                           TemplateMessages messages = new TemplateMessages();
                           messages.setId(itemId);
                           messages.setOrderNo(orderNo);
                           messages.setTemplates(input.getText().toString());
                           messageDatabase.daoAcess().updateTemplateRecord(messages);
                           getTemplateMessages();
                            dialog.cancel();
                        }
                        else
                            Utils.showToast(AddTemplateActivity.this,"Please enter the template");

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





    public TemplateMessages addTemplateMessage(String msg)
    {
        int orderNo = messageDatabase.daoAcess().getTemplateMessageCount()+1;
        TemplateMessages templateMessages = new TemplateMessages();
        templateMessages.setTemplates(msg);
        templateMessages.setOrderNo(orderNo);
        return templateMessages;
    }


    @Override
    public void onEditItemClick(int itemId,String text,int orderNo) {
        editTemplateDialog(itemId,text,orderNo);
    }

    @Override
    public void onDeleteItemClick(final int itemId) {
        new AlertDialog.Builder(this)
                .setTitle("Delete")
                .setMessage("Are you sure to delete this item?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                      TemplateMessages templateMessages = new TemplateMessages();
                      templateMessages.setId(itemId);
                      messageDatabase.daoAcess().deleteTemplateMessages(templateMessages);

                      getTemplateMessages();
                        dialog.dismiss();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                    }
                }).show();

    }
    private void refreshListView() {
        templateListAdapter = new TemplateListAdapter(templateMessagesList,this,this);
        listView.setAdapter(templateListAdapter);
    }
    @Override
    public void updateImageIndex(final  int pos) {
        new AlertDialog.Builder(this)
                .setTitle("Image Schedule")
                .setMessage("Images will be sent after the selected message")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferenceUtils.setIntData(AddTemplateActivity.this,Config.IMAGE_INDEX,pos);
                        refreshListView();
                        dialog.dismiss();
                    }})
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                }).show();

    }

    private void resetPrimaryKeyInSequence() {
        List<TemplateMessages> templateMessageList = messageDatabase.daoAcess().getTemplateMessages();
        for(int i=0;i<templateMessageList.size();i++)
        {
            TemplateMessages messages = templateMessageList.get(i);
            messages.setOrderNo(i+1);
            messageDatabase.daoAcess().updateTemplateRecord(messages);
        }
    }
}
