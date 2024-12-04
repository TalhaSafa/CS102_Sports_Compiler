package com.example.sportscompiler.AdditionalClasses;

import com.google.firebase.Timestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class Match
{
    private String adminID;
    private String matchName;
    private Timestamp date;
    private MatchFields field;
    private Map<String, Player > playersA;
    private Map<String, Player > playersB;
    private String adminPosition;

    public Match(){}

    public Match(String adminID1, String matchName, Timestamp date1, MatchFields field1, Map<String, Player > playersA1, Map<String, Player > playersB1, String adminPosition )
    {
        adminID = adminID1;
        date = date1;
        field = field1;
        playersA= playersA1;
        playersB= playersB1;
        this.matchName = matchName;
        this.adminPosition = adminPosition;
    }


    public String getAdminID() {
        return adminID;
    }

    public void setAdminID(String adminID) {
        this.adminID = adminID;
    }

    public String getMatchName() { return matchName;}

    public void setMatchName(String matchName) {this.matchName = matchName;}

    public Timestamp getDate() {
        return date;
    }

    public void setDate(Timestamp date) {
        this.date = date;
    }

    public MatchFields getField() {
        return field;
    }

    public void setField(MatchFields field) {
        this.field = field;
    }

    public Map<String, Player>  getPlayersA() {
        return playersA;
    }

    public void setPlayersA(Map<String, Player> playersA) {
        this.playersA = playersA;
    }

    public Map<String, Player> getPlayersB() {
        return playersB;
    }

    public void setPlayersB(Map<String, Player> playersB) {
        this.playersB = playersB;
    }

    public String getMatchType()
    {
        if(playersA.size() == 5)
        {
            return "matches5";
        }
        else if(playersA.size() == 6)
        {
            return "matches6";
        }
        else
        {
            return "outOfBond";
        }
    }

    public String getAdminPosition() {
        return adminPosition;
    }

    public void setAdminPosition(String adminPosition) {
        this.adminPosition = adminPosition;
    }
}
