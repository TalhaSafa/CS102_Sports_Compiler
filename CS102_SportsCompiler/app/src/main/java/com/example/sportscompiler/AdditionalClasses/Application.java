package com.example.sportscompiler.AdditionalClasses;

public class Application {
    private String firstName;
    private String position;
    private String applicationNote;
    private String teamInfo; // Field for team information

    public Application(String firstName, String position, String applicationNote, String teamInfo) {
        this.firstName = firstName;
        this.position = position;
        this.applicationNote = applicationNote;
        this.teamInfo = teamInfo; // Initialize team info
    }

    // Getters and setters
    public String getName() { return firstName; }
    public String getPosition() { return position; }
    public String getNote() { return applicationNote; }
    public String getTeamInfo() { return teamInfo; }

    public void setTeamInfo(String teamInfo) { this.teamInfo = teamInfo; }
}


