package com.acube.autoresponder.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

/**
 * Created by Anns on 30/03/18.
 */
@Dao
public interface DaoAcess {

    @Insert
    void insertOnlySingleRecord(Messages messages);

    @Query("SELECT * FROM Messages WHERE message_time =:msgTime")
    Messages checkMessageExists(long msgTime);

    @Query("SELECT * FROM MESSAGES ORDER BY id DESC")
    List<Messages> getAllMessages();
}
