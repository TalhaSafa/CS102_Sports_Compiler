package com.example.sportscompiler.AdditionalClasses;

import java.util.ArrayList;

public class User {
    private String userID;
    private String name;
    private String birthDate;
    private String department;
    private ArrayList<Match> matches = new ArrayList<Match>();
    private double averageRating;
    private String profilePicture;

    public User(){}

    public User(String UserID, String Name, String BirthDate, String Department,String profilePicture, double averageRating)
    {
        userID = UserID;
        name = Name;
        birthDate = BirthDate;
        department = Department;
        this.averageRating = averageRating;
        this.profilePicture = profilePicture;

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public ArrayList<Match> getMatches() {
        return matches;
    }

    public String getProfilePicture(){ return profilePicture; }

    public void setMatches(ArrayList<Match> matches) {
        this.matches = matches;
    }

    public void addMatches(Match match)
    {
        matches.add(match);
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }
}
