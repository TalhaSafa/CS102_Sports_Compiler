package com.example.sportscompiler.AdditionalClasses;

import com.example.sportscompiler.AdditionalClasses.User;

public class Message {
    private final User user;
    private String content;

    public Message(User user, String content) {
        this.user = user;
        this.content = content;
    }

    public User getUser() {
        return user;
    }

    public String getContent() {
        return content;
    }
}
