package com.example.sportscompiler;

public class Match
{
    private String matchName;
    private String matchScore;
    private String matchPosition;
    private String matchDate;

    public Match(String matchName, String matchScore, String matchPosition, String matchDate)
    {
        this.matchName = matchName;
        this.matchScore = matchScore;
        this.matchPosition = matchPosition;
        this.matchDate = matchDate;
    }

    public String getMatchName()
    {
        return matchName;
    }

    public String getMatchScore()
    {
        return matchScore;
    }

    public String getMatchPosition()
    {
        return matchPosition;
    }

    public String getMatchDate()
    {
        return matchDate;
    }

    public void setMatchName(String newMatchName)
    {
        matchName = newMatchName;
    }

    public void setMatchScore(String newMatchScore)
    {
        matchScore = newMatchScore;
    }

    public  void setMatchPosition(String newMatchPosition)
    {
        matchPosition = newMatchPosition;
    }

    public void setMatchDate(String newMatchDate)
    {
        matchDate = newMatchDate;
    }
}
