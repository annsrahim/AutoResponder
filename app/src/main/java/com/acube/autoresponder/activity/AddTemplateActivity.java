package com.acube.autoresponder.activity;

import android.content.DialogInterface;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;


import com.acube.autoresponder.R;
import com.acube.autoresponder.utils.Utils;

public class AddTemplateActivity extends AppCompatActivity {

    ListView listView;
    Toolbar toolbar;

    TemplateListAdapter templateListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_template);
        initToolbar();
        init();
    }

    private void init() {
        listView = (ListView)findViewById(R.id.list);

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

    private void openAddTemplateDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_template, null);
        dialogBuilder.setView(dialogView);

        EditText editText = (EditText) dialogView.findViewById(R.id.ed_template);
        editText.setText("test label");

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }


}
