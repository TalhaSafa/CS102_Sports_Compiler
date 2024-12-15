package com.example.sportscompiler.AdditionalClasses;

public class Application {
    private String firstName;
    private Positions position;
    private String applicationNote;
    private TeamType teamInfo; // Field for team information
    private String userID;
    private double average;

    public Application(String firstName, Positions position, String applicationNote, TeamType teamInfo, String userID, double average) {
        this.firstName = firstName;
        this.position = position;
        this.applicationNote = applicationNote;
        this.teamInfo = teamInfo; // Initialize team info
        this.userID = userID;
        this.average = average;
    }
    public Application()
    {}

    // Getters and setters
    public String getName() { return firstName; }
    public Positions getPosition() { return position; }
    public String getNote() { return applicationNote; }
    public TeamType getTeamInfo() { return teamInfo; }

    public void setName(String firstName)
    {
        this.firstName = firstName;
    }

    public void setPosition(Positions position)
    {
        this.position = position;
    }

    public void setNote(String applicationNote)
    {
        this.applicationNote = applicationNote;
    }

    public void setTeamInfo(TeamType teamInfo) { this.teamInfo = teamInfo; }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public double getAverage() {
        return average;
    }

    public void setAverage(double average) {
        this.average = average;
    }
}


