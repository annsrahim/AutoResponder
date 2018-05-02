package com.acube.autoresponder.activity;

/**
 * Created by Anns on 10/04/18.
 */

public interface ITemplateListItemClickListener {

    public void onEditItemClick(int itemId,String text,int ordeNo);
    public void onDeleteItemClick(int itemId);
    public void updateImageIndex(int pos);
}
