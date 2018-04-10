package com.acube.autoresponder.activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.acube.autoresponder.R;
import com.acube.autoresponder.database.TemplateMessages;
import com.acube.autoresponder.utils.Utils;

import java.util.List;

/**
 * Created by Anns on 06/04/18.
 */

public class TemplateListAdapter extends BaseAdapter {

    private List<TemplateMessages> templateMessagesList;
    private Context context;
    private ITemplateListItemClickListener iTemplateListItemClickListener;

    public TemplateListAdapter(List<TemplateMessages> templateMessagesList, Context context,ITemplateListItemClickListener iTemplateListItemClickListener) {
        this.templateMessagesList = templateMessagesList;
        this.context = context;
        this.iTemplateListItemClickListener = iTemplateListItemClickListener;
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
    public View getView(int i, View convertView, final ViewGroup parent) {
        final int position = i;
        final TemplateMessages template  = templateMessagesList.get(i);
        LayoutInflater inflater = LayoutInflater.from(context);
        convertView = inflater.inflate(R.layout.list_template, parent, false);
        TextView tv_list = (TextView)convertView.findViewById(R.id.list_id);
        TextView tv_template = (TextView)convertView.findViewById(R.id.list_message);

        ImageButton imgBtnEdit = (ImageButton)convertView.findViewById(R.id.list_iv_edit);
        ImageButton imgBtnDel = (ImageButton)convertView.findViewById(R.id.list_iv_delete);
        imgBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iTemplateListItemClickListener.onEditItemClick(template.getId(),template.getTemplates(),template.getOrderNo());
            }
        });
        imgBtnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iTemplateListItemClickListener.onDeleteItemClick(template.getId());
            }
        });

        tv_list.setText(String.valueOf(template.getOrderNo()+") "));
        tv_template.setText(template.getTemplates());
        return convertView;
    }
}
