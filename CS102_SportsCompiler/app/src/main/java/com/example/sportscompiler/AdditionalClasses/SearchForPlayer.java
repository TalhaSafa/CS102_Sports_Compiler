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

    public static boolean doesMatchContainUser(Match match, String userID )
    {
        for(Player player: match.getPlayersA().values())
        {
            if(player != null && player.getUserID().equals(userID))
            {
                return true;
            }
        }

        for(Player player: match.getPlayersB().values())
        {
            if(player != null && player.getUserID().equals(userID))
            {
                return true;
            }
        }

        return false;
    }

    public static Player findPlayerWithID(Match match , String userID)
    {
        for(Player player: match.getPlayersA().values())
        {
            if(player != null && player.getUserID().equals(userID))
            {
                return player;
            }
        }

        for(Player player: match.getPlayersB().values())
        {
            if(player != null && player.getUserID().equals(userID))
            {
                return player;
            }
        }

        return null;
    }
}
