package edu.up.cs301.game;

import android.util.Log;

import java.util.Random;

import edu.up.cs301.game.actionMsg.ClueAccuseAction;
import edu.up.cs301.game.actionMsg.ClueEndTurnAction;
import edu.up.cs301.game.actionMsg.ClueMoveAction;
import edu.up.cs301.game.actionMsg.ClueMoveDownAction;
import edu.up.cs301.game.actionMsg.ClueMoveLeftAction;
import edu.up.cs301.game.actionMsg.ClueMoveRightAction;
import edu.up.cs301.game.actionMsg.ClueMoveUpAction;
import edu.up.cs301.game.actionMsg.ClueRollAction;
import edu.up.cs301.game.actionMsg.ClueShowCardAction;
import edu.up.cs301.game.actionMsg.ClueSuggestionAction;
import edu.up.cs301.game.actionMsg.ClueUsePassagewayAction;
import edu.up.cs301.game.actionMsg.GameAction;

import static edu.up.cs301.game.Card.COL_MUSTARD;
import static edu.up.cs301.game.Card.CONSERVATORY;
import static edu.up.cs301.game.Card.KITCHEN;
import static edu.up.cs301.game.Card.LOUNGE;
import static edu.up.cs301.game.Card.MISS_SCARLET;
import static edu.up.cs301.game.Card.MRS_PEACOCK;
import static edu.up.cs301.game.Card.MRS_WHITE;
import static edu.up.cs301.game.Card.MR_GREEN;
import static edu.up.cs301.game.Card.PROF_PLUM;
import static edu.up.cs301.game.Card.STUDY;
import static edu.up.cs301.game.Type.ROOM;
import static edu.up.cs301.game.Type.WEAPON;
import edu.up.cs301.game.util.Logger;

/**
 * Created by Noah on 10/25/2016.
 *
 * This class is like the rule book keeper for the game.  It checks if a player
 * can make a legal move, and if so performs that move.  It also checks to see
 * if the game is over and if so, who has won.
 *
 */

public class ClueLocalGame extends LocalGame
{
    //Tag for logging 
    private static final String TAG = "ClueLocalGame";
    ClueMoveAction moveAction;
    ClueState state;
    private Random rand;
    private int numberOfAccuses = 1;
    public static String sync = "V";

    //Constructor
    public ClueLocalGame(int numPlayerFromTableRows, int numberOfAccuses)
    {
        super();
        String[] str = new String[numPlayerFromTableRows];
        state = new ClueState(numPlayerFromTableRows,str, 0);
        rand = new Random();
        this.numberOfAccuses = numberOfAccuses;
    }

    /**
     * This method will always return true
     * @param playerID
     * @return boolean
     */
    public boolean canMove(int playerID)
    {
        return true;
    } //always returns true

    /**
     * This method checks if the player can move their piece.
     * They will not be able to move if they have moved the number
     * of spaces they have rolled.
     * @param playerID
     * @return boolean
     */
    public boolean movePlayer(int playerID)
    {
        if (state.getSpacesMoved() < state.getDieValue())
        {
            return true;
        }
        else
        {
            //The players have moved the max number of times and their turn will be ended.
            return false;
        }
    }

    /**
     * This method checks to see if the player is in a corner room,
     * and if so sets the inCornerRoom variable to true
     * @param x players x position
     * @param y players y position
     * @param board the board of tiles that is passed in
     * @param playerID the current playerID
     */
    public void inCornerRoom(int x, int y, Tile[][] board, int playerID)
    {
        //Separate if statements that will check to see if they are in a corner room!
        if (board[x][y].getRoom() == LOUNGE)
        {
            state.setInCornerRoom(playerID, true);
        }
        else if (board[x][y].getRoom() == CONSERVATORY)
        {
            state.setInCornerRoom(playerID, true);
        }
        else if (board[x][y].getRoom() == STUDY)
        {
            state.setInCornerRoom(playerID, true);
        }
        else if (board[x][y].getRoom() == KITCHEN)
        {
            state.setInCornerRoom(playerID, true);
        }
        else
        {
            state.setInCornerRoom(playerID, false);
        }
    }

    @Override
    /**
     * This method checks a game action that is passed in and checks
     * what type of move action it is.
     * @param a A GameAction that is passed in
     */
    public boolean makeMove(GameAction a)
    {
        int[][] playBoard;
        Tile[][] curBoard;

        if(a instanceof ClueMoveAction)
        {
            moveAction = (ClueMoveAction)a;

            //Instance variables that will be used for some of the actions
            int x = 0; //Create current position variables
            int y = 0;
            int curPlayerID = ((ClueMoveAction) a).playerID; //The ID of the player who made the action
            playBoard = state.getBoard().getPlayerBoard(); //Get the current player board so we know where all the players are
            curBoard = (state.getBoard()).getBoard(); //Get the current board w/tiles

            //Set x and y:
            for (int i = 0; i < 27; i++) //Find the current position of the player
            {
                for (int j = 0; j< 27; j++)
                {
                    if (playBoard[i][j] == curPlayerID)
                    {
                        //Set the current position of the player.
                        x = i;
                        y = j;
                    }
                }
            }

            //Check to make sure it's actually the player's turn
            if (state.getTurnId() == curPlayerID)
            {
                if (moveAction instanceof ClueRollAction)
                {
                    //What to do if a certain action is made
                    if (state.getCanRoll(curPlayerID))
                    {
                        int numRolled = rand.nextInt(6) + 1; //Will produce a random number between 1 and 6.
                        state.setDieValue(numRolled);
                        state.setCanRoll(curPlayerID, false);
                        return true;
                    }
                }
                else if (movePlayer(curPlayerID))
                {
                    if (moveAction instanceof ClueMoveUpAction)
                    {
                        //Makes sure the player's next move would not be to another player's current
                        //position on the board.  Also checks to make sure their next position is in the hallway
                        //or through a door.
                        if(curBoard[x-1][y] != null)  //Checks to make sure the tile the player wants to move to is not null
                        {
                            if ((playBoard[x-1][y] == -1 && (curBoard[x-1][y].getTileType() == 0 || curBoard[x-1][y].getIsDoor() || curBoard[x-1][y].getTileType() == 1)) && !curBoard[x-1][y].getBottomWall()
                                    && !curBoard[x][y].getTopWall())
                            {
                                if (curBoard[x-1][y].getIsDoor() && curBoard[x][y].getTileType() == 0) //If the player is moving into a room
                                {
                                    setStateVariablesMove(curPlayerID, x-1, y, x, y,false, true, true, true, true);
                                    inCornerRoom(x-1, y, curBoard, curPlayerID);
                                    //Makes it so that the player is able to move around in a room even if they technically have no moves left.
                                    if (state.getSpacesMoved() == state.getDieValue())
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() - 2);
                                    }
                                    return true;
                                }
                                else if (curBoard[x][y].getTileType() == 1 && curBoard[x-1][y].getTileType() == 1) //If the player is in and will move within a room
                                {
                                    //If the player is moving around within a room, it will set their new position but will
                                    //not increment the spaces moved.
                                    state.getBoard().setPlayerOnBoard(x - 1, y, x, y, curPlayerID);

                                    //In order to prevent the dumb AI from moving around in a room indefinitely,
                                    //increment their spaces moved.
                                    if (a.getPlayer() instanceof ClueComputerPlayerDumb)
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() + 1);
                                    }

                                    //Check to see if the player is on a door tile
                                    if (curBoard[x-1][y].getIsDoor())
                                    {
                                        state.setOnDoorTile(curPlayerID, true);
                                    }
                                    else
                                    {
                                        state.setOnDoorTile(curPlayerID, false);
                                    }
                                    Logger.log("New to room="+state.getNewToRoom(curPlayerID), " ");
                                    return true;
                                }
                                else //Otherwise they're moving to a hallway.
                                {
                                    //If they're new to a room, currently in a room, and they did not get pulled in, they cannot leave the room
                                    if (state.getNewToRoom(curPlayerID) && curBoard[x][y].getTileType() == 1 && !state.getPulledIn(curPlayerID))
                                    {
                                        return true;
                                    }

                                    inCornerRoom(x-1, y, curBoard, curPlayerID);
                                    state.setPulledIn(curPlayerID, false);
                                    setStateVariablesMove(curPlayerID, x-1, y, x, y,false, false, false, false, false);
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (moveAction instanceof ClueMoveDownAction)
                    {
                        if(curBoard[x+1][y] != null) //Checks to make sure the tile the player wants to move to is not null
                        {
                            if ((playBoard[x+1][y] == -1 && (curBoard[x+1][y].getTileType() == 0 || curBoard[x+1][y].getIsDoor() || curBoard[x+1][y].getTileType() == 1)) && !curBoard[x+1][y].getTopWall()
                                    && !curBoard[x][y].getBottomWall())
                            {
                                if (curBoard[x+1][y].getIsDoor() && curBoard[x][y].getTileType() == 0) //If the player is moving into a room
                                {
                                    inCornerRoom(x+1, y, curBoard, curPlayerID);
                                    setStateVariablesMove(curPlayerID, x+1, y, x, y, false, true, true, true, true);
                                    //Makes it so that the player is able to move around in a room even if they technically have no moves left
                                    if (state.getSpacesMoved() == state.getDieValue())
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() -2);
                                    }

                                    return true;
                                }
                                else if (curBoard[x][y].getTileType() == 1 && curBoard[x+1][y].getTileType() == 1) //If the player is in and will move within a room
                                {
                                    //If the player is moving around within a room, it will set their new position but will
                                    //not increment the spaces moved.
                                    state.getBoard().setPlayerOnBoard(x + 1, y, x, y, curPlayerID);


                                    //In order to prevent the dumb AI from moving around in a room indefinitely,
                                    //increment their spaces moved.
                                    if (a.getPlayer() instanceof ClueComputerPlayerDumb)
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() + 1);
                                    }

                                    //Check to see if the player is on a door tile
                                    if (curBoard[x+1][y].getIsDoor())
                                    {
                                        state.setOnDoorTile(curPlayerID, true);
                                    }
                                    else
                                    {
                                        state.setOnDoorTile(curPlayerID, false);
                                    }
                                    return true;
                                }
                                else //Otherwise they're moving to a hallway.
                                {
                                    //If they're new to a room, currently in a room, and they did not get pulled in, they cannot leave the room
                                    if (state.getNewToRoom(curPlayerID) && curBoard[x][y].getTileType() == 1 && !state.getPulledIn(curPlayerID))
                                    {
                                        return true;
                                    }

                                    inCornerRoom(x+1, y, curBoard, curPlayerID);

                                    state.setPulledIn(curPlayerID, false);
                                    setStateVariablesMove(curPlayerID, x+1, y, x, y, false, false, false, false, false);
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (moveAction instanceof ClueMoveRightAction)
                    {
                        if(curBoard[x][y+1] != null) //Checks to make sure the tile the player wants to move to is not null
                        {
                            if((playBoard[x][y+1] == -1 && (curBoard[x][y+1].getTileType() == 0 || curBoard[x][y+1].getIsDoor() || curBoard[x][y+1].getTileType() == 1)) && !curBoard[x][y+1].getLeftWall()
                                    && !curBoard[x][y].getRightWall())
                            {
                                if (curBoard[x][y+1].getIsDoor() && curBoard[x][y].getTileType() == 0) //If the player is moving into a room
                                {
                                    inCornerRoom(x, y+1, curBoard, curPlayerID);

                                    setStateVariablesMove(curPlayerID, x, y+1, x, y, false, true, true, true, true);
                                    //Makes it so that the player is able to move around in a room even if they technically have no moves left
                                    if (state.getSpacesMoved() == state.getDieValue())
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() - 2);
                                    }

                                    return true;
                                }
                                else if (curBoard[x][y].getTileType() == 1 && curBoard[x][y+1].getTileType() == 1) //If the player is in and will move within a room
                                {
                                    //If the player is moving around within a room, it will set their new position but will
                                    //not increment the spaces moved.
                                    state.getBoard().setPlayerOnBoard(x, y + 1, x, y, curPlayerID);

                                    //In order to prevent the dumb AI from moving around in a room indefinitely,
                                    //increment their spaces moved.
                                    if (a.getPlayer() instanceof ClueComputerPlayerDumb)
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() + 1);
                                    }

                                    //Check to see if the player is on a door tile
                                    if (curBoard[x][y+1].getIsDoor())
                                    {
                                        state.setOnDoorTile(curPlayerID, true);
                                    }
                                    else
                                    {
                                        state.setOnDoorTile(curPlayerID, false);
                                    }
                                    return true;
                                }
                                else //Otherwise they're moving to a hallway.
                                {
                                    //If they're new to a room, currently in a room, and they did not get pulled in, they cannot leave the room
                                    if (state.getNewToRoom(curPlayerID) && curBoard[x][y].getTileType() == 1 && !state.getPulledIn(curPlayerID))
                                    {
                                        return true;
                                    }

                                    inCornerRoom(x, y+1, curBoard, curPlayerID);

                                    state.setPulledIn(curPlayerID, false);
                                    setStateVariablesMove(curPlayerID, x, y+1, x, y, false, false, false, false, false);
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }
                    else if (moveAction instanceof ClueMoveLeftAction)
                    {
                        if(x >= 0 && y >= 1 && curBoard[x][y-1] != null) //Checks to make sure the tile the player wants to move to is not null
                        {
                            if ((playBoard[x][y-1] == -1 && (curBoard[x][y-1].getTileType() == 0 || curBoard[x][y-1].getIsDoor() || curBoard[x][y-1].getTileType() == 1)) && !curBoard[x][y-1].getRightWall()
                                    && !curBoard[x][y].getLeftWall())
                            {
                                if (curBoard[x][y-1].getIsDoor() && curBoard[x][y].getTileType() == 0) //If the player is moving into a room
                                {
                                    inCornerRoom(x, y-1, curBoard, curPlayerID);

                                    setStateVariablesMove(curPlayerID, x, y-1, x, y, false, true, true, true, true);
                                    //Makes it so that the player is able to move around in a room even if they technically have no moves left
                                    if (state.getSpacesMoved() == state.getDieValue())
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() -2);
                                    }

                                    return true;
                                }
                                else if (curBoard[x][y].getTileType() == 1 && curBoard[x][y-1].getTileType() == 1) //If the player is in and will move within a room
                                {
                                    //If the player is moving around within a room, it will set their new position but will
                                    //not increment the spaces moved.
                                    state.getBoard().setPlayerOnBoard(x, y - 1, x, y, curPlayerID);

                                    //In order to prevent the dumb AI from moving around in a room indefinitely,
                                    //increment their spaces moved.
                                    if (a.getPlayer() instanceof ClueComputerPlayerDumb)
                                    {
                                        state.setSpacesMoved(state.getSpacesMoved() + 1);
                                    }

                                    //Check to see if the player is on a door tile
                                    if (curBoard[x][y-1].getIsDoor())
                                    {
                                        state.setOnDoorTile(curPlayerID, true);
                                    }
                                    else
                                    {
                                        state.setOnDoorTile(curPlayerID, false);
                                    }

                                    return true;
                                }
                                else //Otherwise they're moving to a hallway.
                                {
                                    //If they're new to a room, currently in a room, and they did not get pulled in, they cannot leave the room
                                    if (state.getNewToRoom(curPlayerID) && curBoard[x][y].getTileType() == 1 && !state.getPulledIn(curPlayerID))
                                    {
                                        return true;
                                    }

                                    inCornerRoom(x, y-1, curBoard, curPlayerID);

                                    state.setPulledIn(curPlayerID,false);
                                    setStateVariablesMove(curPlayerID, x, y-1, x, y, false, false, false, false, false);
                                    return true;
                                }
                            }
                        }
                        else
                        {
                            return false;
                        }
                    }
                }

                if (moveAction instanceof ClueAccuseAction)
                {
                    boolean solved = true;
                    Card solution[] = state.getSolution();
                    ClueAccuseAction moveActionAcc = (ClueAccuseAction)moveAction;

                    //Check to see if the players guess matches the solution.  If one is incorrect, then
                    //solved will be set to false and the player
                    for (int i = 0; i < 3; i++)
                    {
                        if (solution[i].getType() == ROOM) //Checks the room cards
                        {
                            if (!solution[i].getName().equals(moveActionAcc.room))
                            {
                                solved = false;
                            }
                        }
                        else if (solution[i].getType() == WEAPON) //Checks the weapon cards
                        {
                            if (!solution[i].getName().equals(moveActionAcc.weapon))
                            {
                                solved = false;
                            }
                        }
                        else //Checks the suspect cards
                        {
                            if (!solution[i].getName().equals(moveActionAcc.suspect))
                            {
                                solved = false;
                            }
                        }
                    }

                    if (!solved) //The player lost
                    {
                        if(numberOfAccuses-1==0) {
                            //End the game for that player
                            state.setPlayerStillInGame(curPlayerID, false);

                            //Move the player back to their "home position"

                            switch (curPlayerID) {
                                case 0:
                                    state.getBoard().setPlayerOnBoard(1, 17, x, y, curPlayerID);
                                    break;
                                case 1:
                                    state.getBoard().setPlayerOnBoard(8, 24, x, y, curPlayerID);
                                    break;
                                case 2:
                                    state.getBoard().setPlayerOnBoard(25, 15, x, y, curPlayerID);
                                    break;
                                case 3:
                                    state.getBoard().setPlayerOnBoard(25, 10, x, y, curPlayerID);
                                    break;
                                case 4:
                                    state.getBoard().setPlayerOnBoard(19, 1, x, y, curPlayerID);
                                    break;
                                case 5:
                                    state.getBoard().setPlayerOnBoard(6, 1, x, y, curPlayerID);
                            }

                            return true;
                        }else{
                            numberOfAccuses-=1;
                            return true;
                        }
                    }
                    else //The player won the game
                    {
                        state.setGameOver(true); //End the game for all the players
                        state.setWinnerIndex(curPlayerID); //Set the winners index
                        return true;
                    }
                }

                else if (moveAction instanceof ClueSuggestionAction)
                {
                    //Make sure they are not on a door tile when they suggest to prevent the player
                    //from ending their turn on a door tile
                    if (!curBoard[x][y].getIsDoor())
                    {
                        //Check if they are on a room tile and they are new to room
                        if (curBoard[x][y].getTileType() == 1 && state.getNewToRoom(curPlayerID))
                        {
                            ClueSuggestionAction b = (ClueSuggestionAction) a;
                            String[] suggestionCards = new String[3]; //To store a players suggestions in

                            //Set the cards that the player suggests
                            suggestionCards[0] = b.room;
                            suggestionCards[1] = b.suspect;
                            suggestionCards[2] = b.weapon;

                            //Set variables
                            state.setSuggestCards(suggestionCards);
                            state.setPlayerIDWhoSuggested(b.playerID);
                            state.setNewToRoom(b.playerID, false);
                            state.setCanSuggest(b.playerID, false);
                            state.setCanRoll(b.playerID, false);
                            state.setHasSuggested(b.playerID, true);

                            //move the player in the suggestion into the room as well next to the player
                            //only if they are still in the game
                            if (state.getPlayerStillInGame(b.playerID))
                            {
                                String[] tempCards = state.getSuggestCards();

                                //check if the player to move is still in the game
                                if (tempCards[1].equals(MISS_SCARLET.getName()))
                                {
                                    state.setPlayerInSuggestion(0);
                                }
                                else if (tempCards[1].equals(COL_MUSTARD.getName()))
                                {
                                    state.setPlayerInSuggestion(1);
                                }
                                else if (tempCards[1].equals(MRS_WHITE.getName()))
                                {
                                    state.setPlayerInSuggestion(2);
                                }
                                else if (tempCards[1].equals(MR_GREEN.getName()))
                                {
                                    state.setPlayerInSuggestion(3);
                                }
                                else if (tempCards[1].equals(MRS_PEACOCK.getName()))
                                {
                                    state.setPlayerInSuggestion(4);
                                }
                                else if (tempCards[1].equals(PROF_PLUM.getName())) {

                                    state.setPlayerInSuggestion(5);
                                }
                                else
                                {
                                    state.setPlayerInSuggestion(-1);
                                }

                                //if a player was in the suggestion, pull them into the room
                                if (state.getPlayerInSuggestion() != -1)
                                {
                                    String moveToRoom = " ";
                                    int currentX = 1;
                                    int currentY = 1;

                                    //Find the room to place the player in and get the player who is in the suggestions position
                                    for (int i = 0; i < 26; i++)
                                    {
                                        for (int j = 0; j < 26; j++)
                                        {
                                            if (state.getBoard().getPlayerBoard()[j][i] == b.playerID)
                                            {
                                                moveToRoom = state.getBoard().getBoard()[j][i].getRoom().getName();
                                            }
                                            if (state.getBoard().getPlayerBoard()[j][i] == state.getPlayerInSuggestion())
                                            {
                                                currentX = i;
                                                currentY = j;
                                            }
                                        }
                                     }


                                    //Move the player
                                    loop1:
                                    for (int i = 0; i < 26; i++) {
                                        for (int j = 0; j < 26; j++) {
                                            //find first available tile in the room to place the player
                                            if (state.getBoard().getBoard()[j][i] != null && state.getBoard().getBoard()[j][i].getRoom() != null && state.getBoard().getBoard()[j][i].getRoom().getName().equals(moveToRoom) && state.getBoard().getPlayerBoard()[j][i] == - 1 && !state.getBoard().getBoard()[j][i].getIsDoor())
                                            {
                                                //Pulls the player that was included in the suggestion into the room where the suggestion was made
                                                state.getBoard().setPlayerOnBoard(j, i,currentY, currentX, state.getPlayerInSuggestion());

                                                //Checks to make sure the player is actually playing the game.
                                                if(state.getPlayerInSuggestion() < state.getNumPlayers())
                                                {
                                                    state.setPulledIn(state.getPlayerInSuggestion(), true);
                                                    inCornerRoom(j, i, curBoard, state.getPlayerInSuggestion());
                                                    state.setCanSuggest(state.getPlayerInSuggestion(), true);
                                                    state.setUsedPassageway(state.getPlayerInSuggestion(), false);
                                                    state.setOnDoorTile(state.getPlayerInSuggestion(), false);

                                                    //This if statement checks to see if the player included themselves in the suggestion.
                                                    //If they did, setting newToRoom to false will prevent them from making a suggestion again
                                                    //in the same turn
                                                    if (state.getPlayerInSuggestion() == state.getPlayerIDWhoSuggested())
                                                    {
                                                        state.setNewToRoom(state.getPlayerInSuggestion(), false);
                                                    }
                                                    else
                                                    {
                                                        state.setNewToRoom(state.getPlayerInSuggestion(), true);
                                                    }
                                                }
                                                break loop1;
                                            }
                                        }
                                    }
                                }
                             }

                            //Send a card to the player
                            if (b.playerID == state.getNumPlayers() - 1) //If the player who suggested is the last player
                            {
                                //Send the card to the player who suggested.
                                state.setCheckCardToSend(0, true);
                            }
                            else //If the player who suggested is not the last player
                            {
                                //Send the card to the player who suggested.
                                state.setCheckCardToSend(b.playerID + 1, true);
                            }

                            //Set some variables in the state after card has been sent
                            if (state.getTurnId() == (state.getNumPlayers() - 1)) //If the player who showed the card is the last player
                            {
                                //Make it so that the player who showed the card can't move
                                state.setCanRoll(0, false);
                                state.setCanSuggest(0, false);
                                state.setTurnID(0);
                                state.setSpacesMoved(0);
                                state.setDieValue(0);
                                return true;
                            }
                            else //If the player who showed the card is not the last player
                            {
                                //Make it so that the player who showed the card can't move
                                state.setCanRoll(state.getTurnId() + 1, false);
                                state.setCanSuggest(state.getTurnId()+1,false);
                                state.setTurnID(state.getTurnId() + 1);
                                state.setSpacesMoved(0);
                                state.setDieValue(0);
                                return true;
                            }
                        }
                        return false;
                    }
                    return false;
                }

                else if (moveAction instanceof ClueUsePassagewayAction)
                {
                    //These if statements will see where the player is at and will move them to the corner room
                    //diagonal to them.
                    boolean inCorner[] = state.getInCornerRoom(); //Get all of the players that are in a corner room
                    boolean usedPass[] = state.getUsedPassageway(); //Get all of the players that have used a passageway in this turn

                    if (inCorner[curPlayerID] && !usedPass[curPlayerID]) //If the player is in a corner room and hasn't used a passageway
                    {
                        if (curBoard[x][y].getRoom() == LOUNGE) //If they are in the lounge, move them to the conservatory
                        {
                            Logger.log("Got to lounge if", " ");
                            setStateVariablesSuggestion(curPlayerID, false, true);
                            setStateVariablesMove(curPlayerID, 22, 1+curPlayerID, x, y, true, false, true, true, true);
                            return true;
                        }
                        else if (curBoard[x][y].getRoom() == CONSERVATORY) //If they are in the conservatory, move them to the lounge
                        {
                            int sum = 18+curPlayerID; //Number calculated for players spot in new room
                            if(sum > 24)
                            {
                                sum = 24;
                            }

                            setStateVariablesSuggestion(curPlayerID, false, true);
                            setStateVariablesMove(curPlayerID, 2, sum, x, y, true, false, true, true, true);
                            return true;
                        }
                        else if (curBoard[x][y].getRoom() == STUDY) //If the player is in the study move them to the kitchen
                        {
                            int sum = 19+curPlayerID; //Number calculated for players spot in new room
                            if(sum > 24)
                            {
                                sum = 24;
                            }

                            setStateVariablesSuggestion(curPlayerID, false, true);
                            setStateVariablesMove(curPlayerID, 22, sum, x, y, true, false, true, true, true);
                            return true;
                        }
                        else if (curBoard[x][y].getRoom() == KITCHEN) //If the player is in the kitchen move them into the study
                        {

                            setStateVariablesSuggestion(curPlayerID, false, true);
                            setStateVariablesMove(curPlayerID, 3, 1+curPlayerID, x, y, true, false, true, true, true);

                            return true;
                        }
                    }
                }
                else if (moveAction instanceof ClueEndTurnAction)
                {
                    if(state.getTurnId() != moveAction.playerID) //If it is not their turn then they can't end it
                    {
                        return false;
                    }

                    Logger.log("Player Still in Game" + state.getPlayerStillInGame(curPlayerID), " ");
                    //If the player has lost the game but other players are still playing, just
                    //continue the turn rotation
                    if (!state.getPlayerStillInGame(curPlayerID))
                    {
                        return endTurn(state);
                    }
                    else if (!curBoard[x][y].getIsDoor() && state.getPlayerStillInGame(curPlayerID)) //Make sure the player is not on a door tile and is still in the game
                    {
                        //Change the turnID to the next player and lets the next player roll
                        state.setNewToRoom(curPlayerID, false); //Once they've ended their turn, they are no longer new to a room.
                        state.setCanRoll(curPlayerID, false);
                        state.setCanSuggest(curPlayerID, false);
                        state.setHasSuggested(curPlayerID, false);

                        return endTurn(state);
                    }
                    else if(curBoard[x][y].getIsDoor()) {
                        onDoorTile(moveAction);
                        return  endTurn(state);
                    }else if(players[curPlayerID] instanceof ClueComputerPlayerDumb){  //If it's a dumb computer player, make it so that they end their turn properly
                        state.setSpacesMoved(state.getSpacesMoved()-1); //Keep moving till no moves left
                        return false;
                    }

                }
                else if (moveAction instanceof ClueShowCardAction)
                {
                    ClueShowCardAction b = (ClueShowCardAction)a; //Cast the action passed
                    state.setPlayerWhoShowedCard(b.playerID); //sent the id of the player who suggested
                    if(b.getCardToShow() == null) //If there is no card to show
                    {
                        if(b.playerID == state.getNumPlayers() - 1 && state.getPlayerIDWhoSuggested() != 0)
                        {
                            state.setCheckCardToSend(b.playerID, false);
                            state.setCheckCardToSend(0, true);
                            setStateVariablesShowCard(0, 0, false, false);
                            return true;
                        }
                        else if(b.playerID == state.getNumPlayers() - 1 && state.getPlayerIDWhoSuggested() == 0)
                        {
                            //Prints "No card Shown" to all players', except the one who suggested, screen
                            for (int i = 0; i < state.getNumPlayers(); i++)
                            {
                                state.setCardToShow("No card shown.", i);
                            }

                            state.setCheckCardToSend(b.playerID, false);

                            //Displays "No card shown" on the player who suggested screen
                            state.setCardToShow("No card shown.", state.getPlayerIDWhoSuggested());

                            //If no card was shown, no player showed it, so changed "Showed By" to an empty string
                            state.setPlayerWhoShowedCard(-1); //print empty string
                            state.setNewToRoom(state.getPlayerIDWhoSuggested(), false); //Once they've ended their turn, they are no longer new to a room.
                            setStateVariablesShowCard(state.getPlayerIDWhoSuggested(), state.getPlayerIDWhoSuggested(), false, false);
                            return true;
                        }
                        else if(state.getPlayerIDWhoSuggested() != b.playerID + 1)
                        {
                            state.setCheckCardToSend(b.playerID, false);
                            state.setCheckCardToSend(b.playerID + 1, true);
                            setStateVariablesShowCard(b.playerID+1, b.playerID+1, false, false);
                            return true;
                        }
                        else if(b.playerID == state.getPlayerIDWhoSuggested()-1)
                        {
                            //Prints "No card Shown" to all players', except the one who suggested, screen
                            for (int i = 0; i < state.getNumPlayers(); i++)
                            {
                                state.setCardToShow("No card shown.", i);
                            }

                            state.setCheckCardToSend(b.playerID, false);

                            //Displays "No card shown" on the player who suggested screen
                            state.setCardToShow("No card shown.", state.getPlayerIDWhoSuggested());

                            //If no card was shown, no player showed it, so changed "Showed By" to an empty string
                            state.setPlayerWhoShowedCard(-1); //print empty string
                            state.setNewToRoom(state.getPlayerIDWhoSuggested(), false); //Once they've ended their turn, they are no longer new to a room.
                            setStateVariablesShowCard(state.getPlayerIDWhoSuggested(), b.playerID+1, false, false);
                            return true;
                        }
                    }
                    else //A player has chosen a card to show
                    {
                        //Display "Card was shown" on all players' screens
                        for (int i = 0; i < state.getNumPlayers(); i++)
                        {
                            state.setCardToShow("Card was shown.", i);
                        }

                        state.setCheckCardToSend(b.playerID, false);

                        //Set the "Card:" to the card that was shown on the player who suggested screen
                        state.setCardToShow(b.getCardToShow(), state.getPlayerIDWhoSuggested());
                        state.setNewToRoom(state.getPlayerIDWhoSuggested(), false); //Once they've ended their turn, they are no longer new to a room.
                        setStateVariablesShowCard(state.getPlayerIDWhoSuggested(), state.getPlayerIDWhoSuggested(), false, false);
                        return true;
                    }
                    return false;
                }
            }
            return false;
        }
        return false;
    }

    @Override
    /**
     * This method will send an updated state to a specified player
     * @param p GamePlayer the state is being sent to
     */
    public void sendUpdatedStateTo(GamePlayer p)
    {
        ClueState sendState = new ClueState(state);
        if(p instanceof ClueHumanPlayer) //If the player is a human player
        {
            ClueHumanPlayer player = (ClueHumanPlayer)p;
            int playerCount = sendState.getNumPlayers(); //get number of players
            for(int i = 0; i < playerCount; i++)
            {
                if(i != player.getPlayerID()) //If the player the state is being sent to is not the current player
                {
                    sendState.setCards(i, null); //Set the cards in other players hands to null so the current player does not have access to them
                }
            }
        }
        else if(p instanceof ClueComputerPlayerDumb)
        {
            ClueComputerPlayerDumb player = (ClueComputerPlayerDumb)p;
            int playerCount = sendState.getNumPlayers(); //get number of players
            for(int i = 0; i < playerCount; i++)
            {
                if(i != player.getPlayerID()) //If the player the state is being sent to is not the current player
                {
                    sendState.setCards(i, null); //Set the cards in other players hands to null so the current player does not have access to them
                }
            }
        }
        else if(p instanceof ClueComputerPlayerSmart)
        {
            ClueComputerPlayerSmart player = (ClueComputerPlayerSmart)p;
            int playerCount = sendState.getNumPlayers(); //get number of players
            for(int i = 0; i < playerCount; i++)
            {
                if(i != player.getPlayerID()) //If the player the state is being sent to is not the current player
                {
                    sendState.setCards(i, null); //Set the cards in other players hands to null so the current player does not have access to them
                }
            }
        }

        p.sendInfo(sendState); //Send the state to the player
    }

    @Override
    /**
     * This method will send an updated state to a specified player
     * @param p GamePlayer the state is being sent to
     */
    public void sendUpdatedStateToInitial(GamePlayer p)
    {
        ClueState sendState = new ClueState(state);
        if(p instanceof ClueHumanPlayer) //If the player is a human player
        {
            ClueHumanPlayer player = (ClueHumanPlayer)p;
            int playerCount = sendState.getNumPlayers(); //get number of players
            for(int i = 0; i < playerCount; i++)
            {
                if(i != player.getPlayerID()) //If the player the state is being sent to is not the current player
                {
                    sendState.setCards(i, null); //Set the cards in other players hands to null so the current player does not have access to them
                }
            }

            ((ClueHumanPlayer) p).sendInfoInitial(sendState); //Send the state to the player
        }
        else if(p instanceof ClueComputerPlayerDumb)
        {
            ClueComputerPlayerDumb player = (ClueComputerPlayerDumb)p;
            int playerCount = sendState.getNumPlayers(); //get number of players
            for(int i = 0; i < playerCount; i++)
            {
                if(i != player.getPlayerID()) //If the player the state is being sent to is not the current player
                {
                    sendState.setCards(i, null); //Set the cards in other players hands to null so the current player does not have access to them
                }
            }

            p.sendInfo(sendState); //Send the state to the player
        }
        else if(p instanceof ClueComputerPlayerSmart)
        {
            ClueComputerPlayerSmart player = (ClueComputerPlayerSmart)p;
            int playerCount = sendState.getNumPlayers(); //get number of players
            for(int i = 0; i < playerCount; i++)
            {
                if(i != player.getPlayerID()) //If the player the state is being sent to is not the current player
                {
                    sendState.setCards(i, null); //Set the cards in other players hands to null so the current player does not have access to them
                }
            }

            p.sendInfo(sendState); //Send the state to the player
        }
    }





    @Override
    /**
     * This method will check if a player has won the game, either because they have made a
     * correct accusation or because they are the only player left in the game.
     */
    public String checkIfGameOver()
    {
        int numPlayersLeft = 0;

        //Count the number of players left in the game
        for(int i = 0; i < state.getNumPlayers(); i++)
        {
            if(state.getPlayerStillInGame(i))
            {
                numPlayersLeft++;
            }
        }
        //If there are multiple players left in the game and no one has won, do not do anything
        if(numPlayersLeft != 1 && !state.getGameOver())
        {
            return null;
        }
        else if(numPlayersLeft == 1) //If there is only one player left, they have won the game
        {
            for(int i = 0; i < state.getNumPlayers(); i++) //This loop will look for the player that is still in the game
            {
                if(state.getPlayerStillInGame(i)) //Make sure player left is still in the game
                {
                    state.setWinnerIndex(i); //Set the winners index
                }
            }
            state.setGameOver(true); //Set the game to over

            String gameOverMessage = "Game Over";

            //Match the players index with what character they are so correct message is printed to screen
            switch (state.getWinnerIndex())
            {
                case 0: gameOverMessage = "Miss Scarlet won.";
                    break;
                case 1: gameOverMessage = "Colonel Mustard won.";
                    break;
                case 2: gameOverMessage = "Mrs. White won.";
                    break;
                case 3: gameOverMessage = "Mr. Green won";
                    break;
                case 4: gameOverMessage = "Mrs. Peacock won.";
                    break;
                case 5: gameOverMessage = "Professor Plum won.";
                    break;
            }
            return gameOverMessage;
        }
        else //Otherwise a player has won by making a correct accusation
        {
            Logger.log("Game", "Over");
            String gameOverMessage = "Game Over";

            //Match the players index with what character they are so correct message is printed to screen
            switch (state.getWinnerIndex())
            {
                case 0: gameOverMessage = "Miss Scarlet won.";
                    break;
                case 1: gameOverMessage = "Colonel Mustard won.";
                    break;
                case 2: gameOverMessage = "Mrs. White won.";
                    break;
                case 3: gameOverMessage = "Mr. Green won";
                    break;
                case 4: gameOverMessage = "Mrs. Peacock won.";
                    break;
                case 5: gameOverMessage = "Professor Plum won.";
                    break;
            }
            return gameOverMessage;
        }
    }


    /**
     * This method sets variables in the state when a player ends their turn.
     * It continues the turn rotation
     *
     * @param state A ClueState that is passed through whose variables
     * will be set and will be used to get the number of players
     */
    public boolean endTurn (ClueState state)
    {
        if (state.getTurnId() == (state.getNumPlayers() - 1)) //If the player has the last turn ID, go to the first player
        {
            state.setCanRoll(0, true); //Set the first player so that they can roll
            state.setTurnID(0); //Set the turn ID to the first player
            state.setSpacesMoved(0); //Set the spaces moved to 0
            state.setDieValue(0); //Set the die value back to 0
            return true;
        }
        else
        {
            state.setCanRoll(state.getTurnId() + 1, true); //Make it so that the next player can roll
            state.setTurnID(state.getTurnId() + 1); //Make it so that it's the next players turn
            state.setSpacesMoved(0); //Set the spaces moved to 0
            state.setDieValue(0); //Set the die value back to 0
            return true;
        }
    }

    /**
     * This method will set variables in the state when a player makes a move(up, down, left, right)
     *
     * @param playerID int that will be passed through to setters
     * @param newX  set the new x position for the player
     * @param newY  set the new y position for the player
     * @param curX  will move the player from cur x position
     * @param curY  will move the player from cur y position
     * @param usedPassageway boolean that will set usedPassageway variable
     * @param onDoor boolean that will set onDoor variable
     * @param newToRoom boolean that will set newToRoom variable
     * @param canSuggest boolean that will set canSuggest variable
     * @param inRoom boolean that will set inRoom variable
     */
    public void setStateVariablesMove(int playerID, int newX, int newY, int curX, int curY, boolean usedPassageway, boolean onDoor, boolean newToRoom, boolean canSuggest, boolean inRoom)
    {
        state.getBoard().setPlayerOnBoard(newX, newY, curX, curY, playerID); //Set the new position of the player and set the old position to -1.
        state.setSpacesMoved(state.getSpacesMoved() + 1); //Increment spaces moved by one
        state.setUsedPassageway(playerID, usedPassageway); //Sets whether they used a passageway or not
        state.setOnDoorTile(playerID, onDoor); //Sets whether they are on a door tile or not
        state.setNewToRoom(playerID, newToRoom); //Set the new to room in array to true.
        state.setCanSuggest(playerID, canSuggest); //Sets whether they can suggest or not
        state.setInRoom(playerID, inRoom); //Sets whether they are in a room or not
    }

    /**
     * This method sets some variables in the state when a player makes a suggestion
     * @param playerID Whose turn it is
     * @param canRoll If a player can roll
     * @param usedPassageway If a player used a passageway
     */
    public void setStateVariablesSuggestion(int playerID, boolean canRoll, boolean usedPassageway)
    {
        state.setCanRoll(playerID, canRoll); //The player can no longer roll
        state.setUsedPassageway(playerID, usedPassageway); //The player can not use the passageway again since they already used it
        state.setSpacesMoved(1); //Sets the spaces moved to 1 so they can still move(within a room) after a suggestion has been made
    }

    /**
     * This method sets some variables in the state when a player shows a card during a suggestion
     * @param playerID PlayerID whose canRoll variable will be set
     * @param playerTurnID The player whose turn it will be
     * @param canRoll If a player can roll
     * @param canSuggest If a player can suggest
     */
    public void setStateVariablesShowCard(int playerID, int playerTurnID, boolean canRoll, boolean canSuggest)
    {
        state.setCanRoll(playerID, canRoll); //Sets the canRoll variable in the state for a certain player
        state.setCanSuggest(playerID, canSuggest); //Sets the canSuggest variable in the state for a certain player
        state.setSpacesMoved(0); //Sets the spaces moved back to 0
        state.setDieValue(0); //Sets the die value back to 0
        state.setTurnID(playerTurnID); //Sets the turnID
    }

    private void onDoorTile(ClueMoveAction b) {
        int currentX = 1;
        int currentY = 1;
        String moveToRoom = " ";

        //Find the room to place the player in and get the player who is in the suggestions position
        theLoop:
        for (int i = 0; i < 26; i++)
        {
            for (int j = 0; j < 26; j++)
            {
                if (state.getBoard().getPlayerBoard()[j][i] == b.playerID)
                {
                    currentX = i;
                    currentY = j;
                    moveToRoom = state.getBoard().getBoard()[j][i].getRoom().getName();
                    break theLoop;
                }
            }
        }


        //Move the player
        loop1:
        for (int i = 0; i < 26; i++) {
            for (int j = 0; j < 26; j++) {
                //find first available tile in the room to place the player
                if (state.getBoard().getBoard()[j][i] != null && state.getBoard().getBoard()[j][i].getRoom() != null && state.getBoard().getBoard()[j][i].getRoom().getName().equals(moveToRoom) && state.getBoard().getPlayerBoard()[j][i] == - 1 && !state.getBoard().getBoard()[j][i].getIsDoor())
                {
                    //Pulls the player that was included in the suggestion into the room where the suggestion was made
                    state.getBoard().setPlayerOnBoard(j, i,currentY, currentX, b.playerID);
                    break loop1;
                }
            }
        }
    }
}
