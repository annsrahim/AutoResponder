package com.acube.autoresponder.database;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
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

    @Query("SELECT * FROM Messages WHERE message_time =:msgTime AND contact_number =:cNumber")
    Messages checkMessageExists(long msgTime,String cNumber);

    @Query("SELECT * FROM MESSAGES ORDER BY id DESC")
    List<Messages> getAllMessages();

    @Query("SELECT * FROM TemplateMessages")
    List<TemplateMessages> getTemplateMessages();

    @Query("SELECT COUNT(id) FROM TemplateMessages")
    int getTemplateMessageCount();

    @Query("SELECT templates FROM TemplateMessages WHERE id=:templateId")
    String getTemplateFromId(int templateId);

    @Query("SELECT templates FROM TemplateMessages WHERE orderNo=:orderNo")
    String getTemplateByOrderNo(int orderNo);

    @Query("SELECT * FROM Messages WHERE contact_number =:cNumber ORDER BY id DESC LIMIT 1")
    Messages getMessage(String cNumber);

    @Query("DELETE FROM Messages")
    void clearDatabase();

    @Update
    void updateRecord(Messages messages);

    @Update
    void updateTemplateRecord(TemplateMessages messages);

    @Delete
    public void deleteTemplateMessages(TemplateMessages templateMessages);

    @Query("UPDATE SQLITE_SEQUENCE SET SEQ=1 WHERE NAME='TemplateMessages'")
    void updateSequence();

}
