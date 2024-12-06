package com.example.sportscompiler.AdditionalClasses;

import com.example.sportscompiler.AdditionalClasses.User;
import com.google.firebase.Timestamp;

public class Message {
    private User user;
    private Timestamp time;
    private String content;
    private String messageID;

    public Message(User user, Timestamp time, String content, String messageID) {
        this.user = user;
        this.time = time;
        this.content = content;
        this.messageID = messageID;
    }




    public Timestamp getTime() {
        return time;
    }

    public void setTime(Timestamp time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }
}
