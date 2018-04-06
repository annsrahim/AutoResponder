package com.acube.autoresponder.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Anns on 06/04/18.
 */
@Entity
public class TemplateMessages {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String templates;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTemplates() {
        return templates;
    }

    public void setTemplates(String templates) {
        this.templates = templates;
    }
}
