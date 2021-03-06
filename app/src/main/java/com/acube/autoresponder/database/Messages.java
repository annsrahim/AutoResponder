package com.acube.autoresponder.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * Created by Anns on 30/03/18.
 */

@Entity
public class Messages {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String contact_name;
    private String contact_number;

    public int getWaitingQueue() {
        return waitingQueue;
    }

    public void setWaitingQueue(int waitingQueue) {
        this.waitingQueue = waitingQueue;
    }

    private int waitingQueue = 0;

    public String getMessage_text() {
        return message_text;
    }

    public void setMessage_text(String message_text) {
        this.message_text = message_text;
    }

    private String message_text;
    private long message_time;

    public long getLast_reply_time() {
        return last_reply_time;
    }

    public void setLast_reply_time(long last_reply_time) {
        this.last_reply_time = last_reply_time;
    }

    private long last_reply_time;
    private int status;
    private int imageStatus = 0;

    public int getQueue() {
        return queue;
    }

    public void setQueue(int queue) {
        this.queue = queue;
    }

    private int queue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getContact_name() {
        return contact_name;
    }

    public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }

    public String getContact_number() {
        return contact_number;
    }

    public void setContact_number(String contact_number) {
        this.contact_number = contact_number;
    }

    public long getMessage_time() {
        return message_time;
    }

    public void setMessage_time(long message_time) {
        this.message_time = message_time;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getImageStatus() {
        return imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }
}
