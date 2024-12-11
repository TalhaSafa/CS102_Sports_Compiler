package com.example.sportscompiler.AdditionalClasses;

import com.google.firebase.Timestamp;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Match
{
    private String adminID;
    private String adminName;
    private String matchName;
    private Timestamp date;
    private MatchFields field;
    private Map<String, Player > playersA;
    private Map<String, Player > playersB;
    private String adminPosition;
    private String notes;
    private String matchID;
    private ArrayList<Application> applications;

    public Match(){}

    public Match(String adminID1, String adminName, String matchName, Timestamp date1,
                 MatchFields field1, Map<String, Player > playersA1, Map<String, Player > playersB1,
                 String adminPosition, String notes, String matchID)
    {
        adminID = adminID1;
        this.adminName = adminName;
        date = date1;
        field = field1;
        playersA= playersA1;
        playersB= playersB1;
        this.matchName = matchName;
        this.adminPosition = adminPosition;
        this.notes = notes;
        this.matchID = matchID;
        applications = new ArrayList<>();
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
            return Integer.toString(playersA.size());
        }
    }

    public String getAdminPosition() {
        return adminPosition;
    }

    public void setAdminPosition(String adminPosition) {
        this.adminPosition = adminPosition;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }
    public int countTeam(Map<String, Player> players){
        int count = 0;
        ArrayList<String> positionList = new ArrayList<>();
        if(getMatchType().equals("matches5"))
        {
            positionList.add(Positions.GK1.getAction());
            positionList.add(Positions.CB1.getAction());
            positionList.add(Positions.CB2.getAction());
            positionList.add(Positions.CB3.getAction());
            positionList.add(Positions.FW3.getAction());
        }
        else if(getMatchType().equals("matches6"))
        {
            positionList.add(Positions.GK1.getAction());
            positionList.add(Positions.CB1.getAction());
            positionList.add(Positions.CB2.getAction());
            positionList.add(Positions.CB3.getAction());
            positionList.add(Positions.MO3.getAction());
            positionList.add(Positions.FW3.getAction());
        }
        else
        {
            throw new NullPointerException("Not determined match size");
        }
        for(int i = 0; i< players.size() ; i++){
            if(players.get(positionList.get(i)) != null)
            {
                count++;
            }
        }
        return count;
    }


    public boolean isFull(){
        if(getMatchType().equals("matches5"))
        {
            if(countTeam(playersA) == 5 && countTeam(playersB) == 5)
            {
                return true;
            }
            else
            {
                return false;
            }
        }

        else if(getMatchType().equals("matches6"))
        {
            if(countTeam(playersA) == 6 && countTeam(playersB) == 6)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        else{
            throw new NullPointerException("Not determined match type");
        }
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }

    public ArrayList<Application> getApplications() {
        return applications;
    }

    public void setApplications(ArrayList<Application> applications) {
        this.applications = applications;
    }

    public void addApplication(Application application)
    {
        applications.add(application);
    }
}
