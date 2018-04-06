package com.acube.autoresponder.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

/**
 * Created by Anns on 30/03/18.
 */
@Dao
public interface DaoAcess {

    @Insert
    void insertOnlySingleRecord(Messages messages);

    @Insert
    void insertTemplate(TemplateMessages templateMessages);

    @Query("SELECT * FROM Messages WHERE message_time =:msgTime")
    Messages checkMessageExists(long msgTime);

    @Query("SELECT * FROM MESSAGES ORDER BY id DESC")
    List<Messages> getAllMessages();

    @Query("SELECT * FROM Messages WHERE contact_number =:cNumber ORDER BY id DESC LIMIT 1")
    Messages getMessage(String cNumber);

    @Query("DELETE FROM Messages")
    void clearDatabase();

    @Update
    void updateRecord(Messages messages);

}
