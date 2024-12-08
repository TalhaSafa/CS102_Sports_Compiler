package com.example.sportscompiler.AdditionalClasses;

public enum MatchFields
{
    MAIN1("Main Campus 1"),
    MAIN2("Main Campus 2"),
    EAST("East Campus");

    private String action;
    public String getAction()
    {
        return this.action;
    }

    private MatchFields(String action)
    {
        this.action = action;
    }

    public static MatchFields fromString(String field) {
        for (MatchFields matchField : MatchFields.values()) {
            if (matchField.name().equals(field)) {
                return matchField;
            }
        }
        throw new IllegalArgumentException("Invalid MatchFields value: " + field);
    }

}
