package com.example.sportscompiler.AdditionalClasses;

public enum Positions {
    GK1("Goalkeeper"), CB1("CB Left"), CB2("CB Right"), CB3("CB Center"),
    MO1("MO Left"), MO2("MO Right"), MO3("MO Center"),
    FW1("FW Left"), FW2("FW Right"), FW3("FW Center");

    private String action;
    public String getAction()
    {
        return this.action;
    }

    private Positions(String action)
    {
        this.action = action;
    }
}
