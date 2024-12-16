package com.example.sportscompiler.AdditionalClasses;

import java.util.ArrayList;

public class User {
    private String userID;
    private String name;
    private String birthDate;
    private String department;
    private ArrayList<String> matchIDs;
    private ArrayList<String> pastMatchIDs;
    private double averageRating;
    private String profilePicture;
    private int ratingNumber;

    public User(){}

    public User(String UserID, String Name, String BirthDate, String Department,String profilePicture, double averageRating, int ratingNumber)
    {
        userID = UserID;
        name = Name;
        birthDate = BirthDate;
        department = Department;
        this.averageRating = averageRating;
        this.profilePicture = profilePicture;
        pastMatchIDs = new ArrayList<>();
        matchIDs = new ArrayList<>();
        this.ratingNumber = ratingNumber;

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

    public ArrayList<String> getMatches() {
        return matchIDs;
    }

    public String getProfilePicture(){ return profilePicture; }

    public void setMatches(ArrayList<String> matches) {
        this.matchIDs = matches;
    }

    public void addMatches(String matchID)
    {
        matchIDs.add(matchID);
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public ArrayList<String> getPastMatches() {
        return pastMatchIDs;
    }

    public void setPastMatches(ArrayList<String> pastMatches) {
        this.pastMatchIDs = pastMatches;
    }

    public void addPastMatches(String matchID)
    {
        pastMatchIDs.add(matchID);
    }

    public void removeMatch(String matchID)
    {
        matchIDs.remove(matchID);
    }

    public int getRatingNumber() {
        return ratingNumber;
    }

    public void setRatingNumber(int ratingNumber) {
        this.ratingNumber = ratingNumber;
    }
    public void addRatingNumber()
    {
        ratingNumber++;
    }

    public void decreaseRatingNumber()
    {
        ratingNumber--;
    }
}
