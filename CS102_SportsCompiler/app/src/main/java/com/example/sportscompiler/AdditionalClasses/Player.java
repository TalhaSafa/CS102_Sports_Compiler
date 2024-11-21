package com.example.sportscompiler.AdditionalClasses;

public class Player
{
    private Positions position;
    private String userID;
    private int rating;
    private TeamType team;
    private boolean isAdmin;
    private String matchID;

    public Player(){}

    public Player(String userID1, int rating1, TeamType team1, Positions position1, boolean isAdmin1, String matchID1  )
    {
        userID = userID1;
        rating = rating1;
        team = team1;
        position = position1;
        isAdmin = isAdmin1;
        matchID = matchID1;

    }
    public Positions getPosition() {
        return position;
    }

    public void setPosition(Positions position) {
        this.position = position;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public TeamType getTeam() {
        return team;
    }

    public void setTeam(TeamType team) {
        this.team = team;
    }

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }

    public String getMatchID() {
        return matchID;
    }

    public void setMatchID(String matchID) {
        this.matchID = matchID;
    }
}
