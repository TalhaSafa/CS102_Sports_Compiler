package com.example.sportscompiler.AdditionalClasses;

import com.google.firebase.Timestamp;

public class adminHelpMessage {

    private String message;
    private String userID;
    private String userMail;
    private String userName;
    private Timestamp sendTime;

    public adminHelpMessage(){}

    public adminHelpMessage(String message, String userID, String userMail, String userName, Timestamp sendTime) {
        this.message = message;
        this.userID = userID;
        this.userMail = userMail;
        this.userName = userName;
        this.sendTime = sendTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getUserMail() {
        return userMail;
    }

    public void setUserMail(String userMail) {
        this.userMail = userMail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Timestamp getSendTime() {
        return sendTime;
    }

    public void setSendTime(Timestamp sendTime) {
        this.sendTime = sendTime;
    }
}
