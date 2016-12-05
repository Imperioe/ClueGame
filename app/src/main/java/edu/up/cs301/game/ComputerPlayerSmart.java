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

/**
 * Created by Noah on 11/8/2016.
 */

public class ComputerPlayerSmart extends GameComputerPlayer {
    //does smart AI stuff
    private boolean[] checkBoxVals = new boolean[21];
    private Card[] allCards;
    private ClueState myState;
    private ClueSuggestionAction prevSuggestion = null;
    private int[][] doorCoordinates = {{5,10}, {7,12}, {7,13}, {6,18}, {10,18}, {13,17}, {19,20},
            {21,16}, {18,15}, {18,10}, {21,9}, {20,5}, {16,6}, {13,2}, {9,7}, {4,7}};
    private Card[] doorRooms = {Card.HALL, Card.HALL, Card.HALL, Card.LOUNGE, Card.DINING_ROOM,
            Card.DINING_ROOM,Card.KITCHEN, Card.BALLROOM, Card.BALLROOM, Card.BALLROOM, Card.BALLROOM,
            Card.CONSERVATORY, Card.BILLIARD_ROOM, Card.BILLIARD_ROOM, Card.LIBRARY, Card.STUDY};


    public ComputerPlayerSmart(String name) {
        super(name);
    }

    public int getPlayerID() {
        return playerNum;
    }

    public void setPlayerID(int newPlayerID) {
        playerNum = newPlayerID;
    }

    @Override
    protected void receiveInfo(GameInfo info) {
        if (info instanceof ClueState) {
            myState = (ClueState) info; //cast it
            if(myState.getCheckCardToSend()[playerNum]) {
                Log.i("Computer Player "+playerNum,"Showing Card");
                String[] temp = myState.getSuggestCards();
                Hand tempHand = myState.getCards(playerNum);
                Card[] tempCards = tempHand.getCards();
                String[] cardNames = new String[tempHand.getArrayListLength()];
                ArrayList<String> cards = new ArrayList<String>();
                for(int i = 0; i < tempHand.getArrayListLength(); i++) {
                    cardNames[i] = tempCards[i].getName();
                }

                for(int i = 0; i < 3; i++) {
                    for(int j = 0; j < cardNames.length; j++) {
                        if(cardNames[j].equals(temp[i])) {
                            cards.add(temp[i]);
                        }
                    }
                }
                String[] validCards = new String[cards.size()];
                cards.toArray(validCards);
                if(validCards.length == 0) {
                    game.sendAction(new ClueShowCardAction(this));
                }else {
                    Random rand1 = new Random();
                    int c = rand1.nextInt(validCards.length);
                    ClueShowCardAction s = new ClueShowCardAction(this);
                    s.setCardToShow(validCards[c]);
                    game.sendAction(s);
                }
            }
            boolean oneChecked = false;
            for(int i = 0; i < 21; i++) {
                boolean tempChecked = myState.getCheckBox(playerNum, i);
                if(tempChecked) {
                    oneChecked = true;
                }
            }
            if(!oneChecked) {
                allCards = new Card[21];
                int j = 0;
                for(Card card: Card.values()) {
                    allCards[j] = card;
                    j++;
                }
                j = 0;
                for(Card c: Card.values()) {
                    for(int i = 0; i < myState.getCards(playerNum).getArrayListLength(); i++) {
                        if(c.equals(myState.getCards(playerNum).getCards()[i])) {
                            checkBoxVals[j] = true;
                        }
                    }
                    j++;
                }
            }
            if (myState.getTurnId() == playerNum && myState.getPlayerStillInGame(playerNum)) {
                if (myState.getCanRoll(this.playerNum)) {
                    Log.i("Computer Player"+playerNum, "Rolling");
                    game.sendAction(new ClueRollAction(this));
                    return;
                }else if(myState.getCardToShow(this.playerNum).equals("No card shown.")) {
                    ClueAccuseAction caa = new ClueAccuseAction(this);
                    caa.room = prevSuggestion.room;
                    caa.suspect = prevSuggestion.suspect;
                    caa.weapon = prevSuggestion.weapon;
                    game.sendAction(caa);

                }else if (myState.getNewToRoom(this.playerNum)) {
                    //make suggestion

                    Card[] suspects = {Card.COL_MUSTARD, Card.PROF_PLUM, Card.MR_GREEN,
                            Card.MRS_PEACOCK, Card.MISS_SCARLET, Card.MRS_WHITE};
                    Card[] weapons = {Card.KNIFE, Card.CANDLESTICK, Card.REVOLVER, Card.ROPE,
                            Card.LEAD_PIPE, Card.WRENCH};
                    Random rand = new Random();
                    ArrayList<Card> availableSuspects = new ArrayList<Card>();
                    ArrayList<Card> availableWeapons = new ArrayList<Card>();

                    //add suspects not already known to not be the murderer
                    for(int i = 0; i < 6; i++) {
                        if(!checkBoxVals[i]) {
                            availableSuspects.add(suspects[i]);
                        }
                    }

                    //add weapons not already known to not be the murder weapon
                    for(int i = 6; i < 12; i++) {
                        if(!checkBoxVals[i]) {
                            availableWeapons.add(weapons[i-6]);
                        }
                    }


                    ClueSuggestionAction csa = new ClueSuggestionAction(this);
                    for (int i = 0; i < 26; i++) {
                        for (int j = 0; j < 26; j++) {
                            if (myState.getBoard().getPlayerBoard()[j][i] == playerNum) {
                                csa.room = myState.getBoard().getBoardArr()[j][i].getRoom().getName();
                                break;
                            }
                        }
                    }

                    csa.suspect = availableSuspects.get(rand.nextInt(availableSuspects.size())).getName();
                    csa.weapon = availableWeapons.get(rand.nextInt(availableWeapons.size())).getName();

                    Log.i("Computer Player "+playerNum,"Suggesting");
                    prevSuggestion = csa;
                    game.sendAction(csa);

                } else if (myState.getDieValue() != myState.getSpacesMoved()) {

                    try {
                        Thread.sleep(100);
                    }catch(Exception e) {}
                    Card[] rooms = {Card.HALL, Card.LOUNGE, Card.DINING_ROOM, Card.KITCHEN, Card.BALLROOM,
                            Card.CONSERVATORY, Card.BILLIARD_ROOM, Card.LIBRARY, Card.STUDY};
                    int curX = -1;
                    int curY = -1;
                    int closestX = 100;
                    int closestY = 100;

                    Card currentRoom = null; //null if not in a room, set to room if in room

                    for (int i = 0; i < 26; i++) {
                        for (int j = 0; j < 26; j++) {
                            if (myState.getBoard().getPlayerBoard()[j][i] == playerNum) {
                                curX = j;
                                curY = i;
                                currentRoom = myState.getBoard().getBoardArr()[j][i].getRoom();
                                break;
                            }
                        }
                    }

                    for(int i = 0; i < 16; i++) {
                        if (!doorRooms[i].equals(currentRoom)) {
                            boolean roomChecked = true;
                            for(int k = 0; k < 9; k++) {
                                if(rooms[k].equals(doorRooms[i]) && !checkBoxVals[12+k]) {
                                    roomChecked = false;
                                }
                            }

                            //update to dX dY
                            if ((((closestX- curX) * (closestX - curX)) + ((closestY - curY) * (closestY - curY))) >
                                    (((doorCoordinates[i][0] - curX) * (doorCoordinates[i][0] - curX)) + ((doorCoordinates[i][1] - curY)
                                            * (doorCoordinates[i][1] - curY))) && !roomChecked) {
                                closestX = doorCoordinates[i][0];
                                closestY = doorCoordinates[i][1];
                            }
                        }
                    }

                    int dX = closestX - curX;
                    int dY = closestY - curY;
                    boolean dXNegative = false;
                    boolean dYNegative = false;

                    if(dX < 0) {dX *= -1; dXNegative = true;}
                    if(dY < 0) {dY *= -1; dYNegative = true;}

                    if(dX <  dY) {

                        if(dXNegative && checkIfAvailableTile(curX - 1, curY, 3)){
                            game.sendAction(new ClueMoveUpAction((this)));
                            Log.i("Moved", "Up");
                            return;
                        }
                        if(!dXNegative && checkIfAvailableTile(curX + 1, curY, 4)) {
                            game.sendAction(new ClueMoveDownAction(this));
                            Log.i("Moved", "Down");
                            return;
                        }
                        if(dYNegative && checkIfAvailableTile(curX, curY - 1, 1)) {
                            game.sendAction(new ClueMoveLeftAction((this)));
                            Log.i("Moved", "Left");
                            return;
                        }
                        if(dYNegative && checkIfAvailableTile(curX, curY + 1, 2)) {
                            game.sendAction(new ClueMoveRightAction((this)));
                            Log.i("Moved", "Right");
                            return;
                        }
                    }else {
                        if(dXNegative && checkIfAvailableTile(curX, curY - 1, 1)) {
                            game.sendAction(new ClueMoveLeftAction((this)));
                            Log.i("Moved", "Left");
                            return;
                        }
                        if(dXNegative && checkIfAvailableTile(curX, curY + 1, 2)) {
                            game.sendAction(new ClueMoveRightAction((this)));
                            Log.i("Moved", "Right");
                            return;
                        }
                        if(dYNegative && checkIfAvailableTile(curX - 1, curY, 3)){
                            game.sendAction(new ClueMoveUpAction((this)));
                            Log.i("Moved", "Up");
                            return;
                        }
                        if(!dYNegative && checkIfAvailableTile(curX + 1, curY, 4)) {
                            game.sendAction(new ClueMoveDownAction(this));
                            Log.i("Moved", "Down");
                            return;
                        }

                    }


                        //check room coordinates to figure out closest room not checked in checkbox
                        //array to set roomToMoveTo to most closest room not current room
                        //also consider passageways
                } else {
                    Log.i("Computer Player "+playerNum, "End Turn");
                    game.sendAction(new ClueEndTurnAction(this));
                }
            }
        } else {
            return;
        }

        //if it enters a room, suggest random
        //if it uses all its moves and does not enter a room, end turn
    }

    //direction 1 = up, 2 = down, 3 = left, 4 = right
    private boolean checkIfAvailableTile(int x, int y, int direction) {
        if(myState.getBoard().getBoardArr()[x][y] != null) {
            if(direction == 1) {
                if(!myState.getBoard().getBoardArr()[x][y].getBottomWall()) {
                    if(myState.getBoard().getPlayerBoard()[x][y] == -1) {
                        if(!myState.getBoard().getBoardArr()[x][y + 1].getTopWall()) {
                            return true;
                        }
                    }
                }
            } else if(direction == 2) {
                if(!myState.getBoard().getBoardArr()[x][y].getTopWall()) {
                    if(myState.getBoard().getPlayerBoard()[x][y] == -1) {
                        if(!myState.getBoard().getBoardArr()[x][y - 1].getBottomWall()) {
                            return true;
                        }
                    }
                }
            }else if(direction == 3) {
                if(!myState.getBoard().getBoardArr()[x][y].getRightWall()) {
                    if(myState.getBoard().getPlayerBoard()[x][y] == -1) {
                        if(!myState.getBoard().getBoardArr()[x + 1][y].getLeftWall()) {
                            return true;
                        }
                    }
                }
            }else if(direction == 4) {
                if(!myState.getBoard().getBoardArr()[x][y].getLeftWall()) {
                    if(myState.getBoard().getPlayerBoard()[x][y] == -1) {
                        if(!myState.getBoard().getBoardArr()[x - 1][y].getRightWall()) {
                            return true;
                        }
                    }
                }
            }
        }else {
            return false;
        }
        return false;
    }

}
