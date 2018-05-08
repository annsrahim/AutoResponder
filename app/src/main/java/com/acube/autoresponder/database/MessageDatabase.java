package com.acube.autoresponder.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

/**
 * Created by Anns on 30/03/18.
 */

@Database(entities = {Messages.class,TemplateMessages.class},version = 2)
public abstract class MessageDatabase extends RoomDatabase
{
    public abstract DaoAcess daoAcess();
}
