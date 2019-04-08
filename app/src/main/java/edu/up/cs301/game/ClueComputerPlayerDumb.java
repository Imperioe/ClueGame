package edu.up.cs301.game;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

import edu.up.cs301.game.actionMsg.ClueAccuseAction;
import edu.up.cs301.game.actionMsg.ClueEndTurnAction;
import edu.up.cs301.game.actionMsg.ClueMoveDownAction;
import edu.up.cs301.game.actionMsg.ClueMoveLeftAction;
import edu.up.cs301.game.actionMsg.ClueMoveRightAction;
import edu.up.cs301.game.actionMsg.ClueMoveUpAction;
import edu.up.cs301.game.actionMsg.ClueRollAction;
import edu.up.cs301.game.actionMsg.ClueShowCardAction;
import edu.up.cs301.game.actionMsg.ClueSuggestionAction;
import edu.up.cs301.game.actionMsg.ClueUsePassagewayAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.Logger;

/**
 * Created by Langley on 11/17/2016.
 */
/*AI that is able to perform all moves necessary to play a Clue game
 *Does these moves randomly
 * eg. randomly shows cards, moves, and suggestions
 * Only Accuses when it knows the accusation is write
 * Ends Turn only when It must
 */
public class ClueComputerPlayerDumb extends ClueComputerPlayer
{
    //Tag for logging
    private static final String TAG = "ClueComputerPlayerDumb";
    private boolean[] checkbox = new boolean[21]; //Used to know what cards have been seen, true= seen(checked), false = never seen(unchecked)
    Card[] suspects = {Card.COL_MUSTARD, Card.PROF_PLUM, Card.MR_GREEN, Card.MRS_PEACOCK, Card.MISS_SCARLET, Card.MRS_WHITE}; //array of all the suspects
    Card[] weapons = {Card.KNIFE, Card.CANDLESTICK, Card.REVOLVER, Card.ROPE, Card.LEAD_PIPE, Card.WRENCH}; //array of all the weapons
    Card[] rooms = {Card.HALL,Card.LOUNGE,Card.DINING_ROOM,Card.KITCHEN,Card.BALLROOM,Card.CONSERVATORY,Card.BILLIARD_ROOM,Card.LIBRARY,Card.STUDY}; //array of all the rooms
    private int numTrue; //num of cards checked
    private boolean handChecked = false; //Used to know if the hand was checked or not
    private String lastCard = ""; //Used to know the last card checked
    private static final int LEFT = 1, UP = 2, RIGHT = 3, DOWN = 4, USEPASSAGEWAY = 5;
    private int numMoves; //used to prevent player from being stuck and trying to move forever

    public ClueComputerPlayerDumb(String name)
    {
        super(name);
        numTrue = 0;
        numMoves = 0;
    }

    public int getPlayerID()
    {
        return playerNum;
    }

    public void setPlayerID(int newPlayerID)
    {
        playerNum = newPlayerID;
    }

    @Override
    protected synchronized void receiveInfo(GameInfo info)
    {
            if (info instanceof ClueState)
            { //Make sure the info is Clue State information
                ClueState myState = (ClueState) info; //cast it

                if (!handChecked)
                { //Were the boxes for the cards in had already checked?
                    Hand myHand = myState.getCards(playerNum); //Get the hand of the AI
                    for (Card c : myHand.getCards())
                    {
                        checkbox(c.getName()); //Check the boxes for the cards
                    }
                    handChecked = true; //Do not check the hand boxes again
                }

                if (!myState.getCardToShow(playerNum).equals(lastCard))
                { //If the lastCard shown was not checked
                    Logger.log(TAG, " " + playerNum+"Looking at Card: " + myState.getCardToShow(playerNum));
                    String card = myState.getCardToShow(playerNum); //get the card shown to the AI
                    lastCard = card; //set last card to the card being checked
                    checkbox(card);
                }

                if (myState.getCheckCardToSend()[playerNum])
                { //Is it the AI's turn to show a card?
                    String[] temp = myState.getSuggestCards(); //Get suggested cards
                    Hand tempHand = myState.getCards(playerNum); //get the hand of the AI
                    Card[] tempCards = tempHand.getCards(); //get the cards in the hand
                    String[] cardNames = new String[tempHand.getArrayListLength()]; //Array to store the name of the cards in the hand
                    ArrayList<String> cards = new ArrayList<String>(); //Arraylist to store the cards that can be shown

                    for (int i = 0; i < tempHand.getArrayListLength(); i++)
                    { //Set cardNames to the name of cards in the hand
                        cardNames[i] = tempCards[i].getName();
                    }

                    for (String str: temp)
                    {
                        for (int j = 0; j < cardNames.length; j++)
                        {
                            if (cardNames[j].equals(str))
                            {
                                cards.add(str); //Add cards that can be shown
                            }
                        }
                    }

                    if (cards.size() == 0)
                    { //If there is no showable card send a ClueShowCardAction to the game with a null string for the card
                        game.sendAction(new ClueShowCardAction(this));
                        return;
                    }
                    else
                    { //Randomly pick a card to show
                        Random rand1 = new Random();
                        int c = rand1.nextInt(cards.size()); //randomly pick a card
                        ClueShowCardAction s = new ClueShowCardAction(this);
                        s.setCardToShow(cards.get(c)); //Set the string to the card picked
                        Logger.log(TAG,  " " + playerNum + "Showing Card: " +cards.get(c));
                        game.sendAction(s);
                        return;
                    }
                }
                else if (myState.getTurnId() == playerNum && myState.getPlayerStillInGame(playerNum))
                { //If its the AI's turn and they are still in the game
                    Logger.log(TAG,  " " + playerNum + "My Turn!");

                    //Get the number of boxes checked
                    numTrue = 0;
                    for(boolean b: checkbox){
                        if(b){
                            numTrue++;
                        }
                    }

                    if (myState.getCanRoll(this.playerNum))
                    { //If the AI needs to roll then roll
                        Logger.log(TAG,  " " + playerNum + "Rolling");
                        numMoves = 0;
                        game.sendAction(new ClueRollAction(this));
                        return;
                    }
                    else if (myState.getCanSuggest(this.playerNum) && !myState.getOnDoorTile()[playerNum] && myState.getInRoom()[playerNum])
                    { //If the AI can make a suggestion then randomly make a suggestion
                        Random rand = new Random();
                        ClueSuggestionAction csa = new ClueSuggestionAction(this);

                        FindPlayerBoard:
                        for (int i = 0; i < 26; i++)
                        {
                            for (int j = 0; j < 26; j++)
                            {
                                if (myState.getBoard().getPlayerBoard()[j][i] == playerNum)
                                {
                                    csa.room = myState.getBoard().getBoard()[j][i].getRoom().getName(); //Suggest the Room the AI is in
                                    break FindPlayerBoard;
                                }
                            }
                        }

                        //Randomly suggestion the Suspect and Weapon
                        csa.suspect = suspects[rand.nextInt(6)].getName();
                        csa.weapon = weapons[rand.nextInt(6)].getName();
                        Logger.log(TAG,  " " + playerNum + "Suggesting: "+ csa.room+". "+csa.suspect+". "+csa.weapon);
                        game.sendAction(csa);
                        return;

                    }
                    else if (myState.getDieValue() != myState.getSpacesMoved() && numMoves < 20)
                    { //There are spaces left to move then move randomly
                        Random rand = new Random();
                        int move = rand.nextInt(5) + 1; //Pick move: 1-5, left = 1, up = 2, right = 3, down = 4, use passageway = 5
                        Logger.log(TAG,  " " + playerNum + "Moving " + move);
                        sleep(300); //Wait so the Human player can see where the AI is moving

                        switch(move)
                        {
                            case LEFT:
                                game.sendAction(new ClueMoveLeftAction((this)));
                                //numMoves++;
                                return;
                            case UP:
                                game.sendAction(new ClueMoveUpAction((this)));
                                //numMoves++;
                                return;
                            case RIGHT:
                                game.sendAction(new ClueMoveRightAction((this)));
                                //numMoves++;
                                return;
                            case DOWN:
                                game.sendAction(new ClueMoveDownAction(this));
                                //numMoves++;
                                return;
                            case USEPASSAGEWAY:
                                game.sendAction(new ClueUsePassagewayAction(this));
                                //numMoves++;
                                return;
                        }

                        return;
                    }
                    else if (numTrue == 18)
                    { //If only 3 boxes are unchecked then accuse
                        Logger.log(TAG,  " " + playerNum + "Accusing");

                        int suspect = 0; //person index
                        int weapon = 0; //weapon index
                        int room = 0; //room index
                        for (int i = 0; i < 6; i++)
                        { //Find the checkboxes of the person and weapon unchecked
                            if (!checkbox[i])
                            { //Find the checkbox of the person unchecked
                                suspect = i;
                            }

                            if (!checkbox[i + 6])
                            { //Find the checkbox of the weapon unchecked
                                weapon = i;
                            }
                        }

                        for (int i = 0; i < 9; i++)
                        { //Find the checkbox of the room unchecked
                            if (!checkbox[i + 12])
                            {
                                room = i;
                            }
                        }

                        ClueAccuseAction caa = new ClueAccuseAction(this);
                        caa.suspect = suspects[suspect].getName();
                        caa.weapon = weapons[weapon].getName();
                        caa.room = rooms[room].getName();
                        game.sendAction(caa);
                        return;
                    }
                    else
                    { //Ends the AI's turn
                        Logger.log(TAG,  " " + playerNum + "End Turn");
                        game.sendAction(new ClueEndTurnAction(this));
                        return;
                    }
                }
            }
            else
            { //GameInfo was not ClueState information do nothing with it
                return;
            }
    }

    //Checks the box of the name of a card passed in
    private void checkbox(String card)
    {
        if (card.equals(Card.COL_MUSTARD.getName()))
        {
            this.checkbox[0] = true;
        }
        else if (card.equals(Card.PROF_PLUM.getName()))
        {
            this.checkbox[1] = true;
        }
        else if (card.equals(Card.MR_GREEN.getName()))
        {
            this.checkbox[2] = true;
        }
        else if (card.equals(Card.MRS_PEACOCK.getName()))
        {
            this.checkbox[3] = true;
        }
        else if (card.equals(Card.MISS_SCARLET.getName()))
        {
            this.checkbox[4] = true;
        }
        else if (card.equals(Card.MRS_WHITE.getName()))
        {
            this.checkbox[5] = true;
        }
        else if (card.equals(Card.KNIFE.getName()))
        {
            this.checkbox[6] = true;
        }
        else if (card.equals(Card.CANDLESTICK.getName()))
        {
            this.checkbox[7] = true;
        }
        else if (card.equals(Card.REVOLVER.getName()))
        {
            this.checkbox[8] = true;
        }
        else if (card.equals(Card.ROPE.getName()))
        {
            this.checkbox[9] = true;
        }
        else if (card.equals(Card.LEAD_PIPE.getName()))
        {
            this.checkbox[10] = true;
        }
        else if (card.equals(Card.WRENCH.getName()))
        {
            this.checkbox[11] = true;
        }
        else if (card.equals(Card.HALL.getName()))
        {
            this.checkbox[12] = true;
        }
        else if (card.equals(Card.LOUNGE.getName()))
        {
            this.checkbox[13] = true;
        }
        else if (card.equals(Card.DINING_ROOM.getName()))
        {
            this.checkbox[14] = true;
        }
        else if (card.equals(Card.KITCHEN.getName()))
        {
            this.checkbox[15] = true;
        }
        else if (card.equals(Card.BALLROOM.getName()))
        {
            this.checkbox[16] = true;
        }
        else if (card.equals(Card.CONSERVATORY.getName()))
        {
            this.checkbox[17] = true;
        }
        else if (card.equals(Card.BILLIARD_ROOM.getName()))
        {
            this.checkbox[18] = true;
        }
        else if (card.equals(Card.LIBRARY.getName()))
        {
            this.checkbox[19] = true;
        }
        else if (card.equals(Card.STUDY.getName()))
        {
            this.checkbox[20] = true;
        }
    }

    //return array of the cards checked
    public boolean[] getCheckBoxArray()
    {
        return checkbox;
    }
}
