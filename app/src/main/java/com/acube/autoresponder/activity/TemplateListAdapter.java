package com.acube.autoresponder.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.acube.autoresponder.R;
import com.acube.autoresponder.database.TemplateMessages;

import java.util.List;

/**
 * Created by Anns on 06/04/18.
 */

public class TemplateListAdapter extends BaseAdapter {

    List<TemplateMessages> templateMessagesList;
    Context context;

    public TemplateListAdapter(List<TemplateMessages> templateMessagesList, Context context) {
        this.templateMessagesList = templateMessagesList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return templateMessagesList.size();
    }

    @Override
    public Object getItem(int i) {
        return templateMessagesList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return templateMessagesList.get(i).getId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup parent) {
        TemplateMessages template  = templateMessagesList.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_template, parent, false);
        TextView tv_list = (TextView)convertView.findViewById(R.id.list_id);
        TextView tv_template = (TextView)convertView.findViewById(R.id.list_message);
        tv_list.setText(template.getId());
        tv_template.setText(template.getTemplates());

        return convertView;
    }
}
