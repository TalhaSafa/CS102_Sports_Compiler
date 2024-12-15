package com.example.sportscompiler.AdditionalClasses;

public class SearchForPlayer {
    public static boolean doesMatchIncludePlayer(Match match, Player player)
    {
        if(match.getPlayersA().containsValue(player))
        {
            return true;
        }
        else if(match.getPlayersB().containsValue(player))
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
