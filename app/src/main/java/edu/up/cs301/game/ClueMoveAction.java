package edu.up.cs301.game;

/**
 * Created by Paige on 11/8/16.
 */
public class ClueMoveAction
{
    public int playerID;

    public ClueMoveAction(GamePlayer player)
    {
        playerID = player.getID();
    }
}
