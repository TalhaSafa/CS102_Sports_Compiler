package com.example.sportscompiler.AdditionalClasses;

public class Team {
    private Player goalkeeper;
    private Player defender1;
    private Player defender2;
    private Player midfielder1;
    private Player midfielder2;
    private Player forward1;

    // Constructor, getters, setters, etc.
    public Team(Player goalkeeper, Player defender1, Player defender2, Player midfielder1, Player midfielder2, Player forward1) {
        this.goalkeeper = goalkeeper;
        this.defender1 = defender1;
        this.defender2 = defender2;
        this.midfielder1 = midfielder1;
        this.midfielder2 = midfielder2;
        this.forward1 = forward1;
    }

    public Player getGoalkeeper() {
        return goalkeeper;
    }

    public Player getDefender1() {
        return defender1;
    }

    public Player getDefender2() {
        return defender2;
    }

    public Player getMidfielder1() {
        return midfielder1;
    }

    public Player getMidfielder2() {
        return midfielder2;
    }

    public Player getForward1() {
        return forward1;
    }
}

