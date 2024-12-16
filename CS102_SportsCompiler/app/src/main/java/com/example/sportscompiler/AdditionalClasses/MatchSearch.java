package com.example.sportscompiler.AdditionalClasses;

import java.util.List;

public class MatchSearch
{
    public static boolean doesContainMatch(List<Match> matches, Match matchToSearch )
    {
        for(Match match : matches)
        {
            if(match != null && matchToSearch != null)
            {
                if(match.getMatchID().equals(matchToSearch.getMatchID()))
                {
                    return true;
                }
            }
        }

        return false;
    }
}
