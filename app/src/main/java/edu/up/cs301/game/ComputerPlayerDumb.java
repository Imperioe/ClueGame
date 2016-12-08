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
 * Created by Langley on 11/17/2016.
 */

public class ComputerPlayerDumb extends ClueComputerPlayer {

    private boolean[] checkbox = new boolean[21];
    Card[] suspects = {Card.COL_MUSTARD, Card.PROF_PLUM, Card.MR_GREEN, Card.MRS_PEACOCK, Card.MISS_SCARLET, Card.MRS_WHITE};
    Card[] weapons = {Card.KNIFE, Card.CANDLESTICK, Card.REVOLVER, Card.ROPE, Card.LEAD_PIPE, Card.WRENCH};
    Card[] rooms = {Card.HALL,Card.LOUNGE,Card.DINING_ROOM,Card.KITCHEN,Card.BALLROOM,Card.CONSERVATORY,Card.BILLIARD_ROOM,Card.LIBRARY,Card.STUDY};
    private int numTrue;
    private boolean handChecked = false;
    private String lastCard = "";

    public ComputerPlayerDumb(String name){
        super(name);
        for(int i = 0; i < 21; i++){
            checkbox[i] = false;
        }
        numTrue = 0;
    }

    public int getPlayerID() {
        return playerNum;
    }

    public void setPlayerID(int newPlayerID) {
        playerNum = newPlayerID;
    }

    @Override
    protected synchronized void receiveInfo(GameInfo info) {
        synchronized (((ClueLocalGame)game).sync) {
            if (info instanceof ClueState) {
                // I just commented it out to try a couple things.
                ClueState myState = (ClueState) info; //cast it

                if (!handChecked) {
                    Hand myHand = myState.getCards(playerNum);
                    for (Card c : myHand.getCards()) {
                        checkbox(c.getName());
                    }
                    handChecked = true;
                }

                if (!myState.getCardToShow(playerNum).equals(lastCard)) {
                    Log.i("Computer Player " + playerNum, "Looking at Card: "+ myState.getCardToShow(playerNum));
                    lastCard = myState.getCardToShow(playerNum);
                    String card = myState.getCardToShow(playerNum);
                    checkbox(card);
                }

                if (myState.getCheckCardToSend()[playerNum]) {
                    String[] temp = myState.getSuggestCards();
                    Hand tempHand = myState.getCards(playerNum);
                    Card[] tempCards = tempHand.getCards();
                    String[] cardNames = new String[tempHand.getArrayListLength()];
                    ArrayList<String> cards = new ArrayList<String>();
                    for (int i = 0; i < tempHand.getArrayListLength(); i++) {
                        cardNames[i] = tempCards[i].getName();
                    }

                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < cardNames.length; j++) {
                            if (cardNames[j].equals(temp[i])) {
                                cards.add(temp[i]);
                            }
                        }
                    }
                    String[] validCards = new String[cards.size()];
                    cards.toArray(validCards);
                    if (validCards.length == 0) {
                        game.sendAction(new ClueShowCardAction(this));
                        return;
                    } else {
                        Random rand1 = new Random();
                        int c = rand1.nextInt(validCards.length);
                        ClueShowCardAction s = new ClueShowCardAction(this);
                        s.setCardToShow(validCards[c]);
                        Log.i("Computer Player " + playerNum, "Showing Card: " + validCards[c]);
                        game.sendAction(s);
                        return;
                    }
                }else if (myState.getTurnId() == playerNum && myState.getPlayerStillInGame(playerNum)) {
                    Log.i("Computer Player"+ playerNum, "My Turn!");
                    int num = 0;
                    for (int i = 0; i < 21; i++) {
                        if (checkbox[i]) {
                            num++;
                        }
                    }
                    numTrue = num;

                    if (myState.getCanRoll(this.playerNum)) {
                        Log.i("Computer Player" + playerNum, "Rolling");
                        game.sendAction(new ClueRollAction(this));
                        return;
                    } else if (myState.getCanSuggest(this.playerNum) && !myState.getOnDoorTile()[playerNum] && myState.getInRoom()[playerNum]) {
                        //make suggestion
                        Random rand = new Random();
                        ClueSuggestionAction csa = new ClueSuggestionAction(this);
                        loop:
                        for (int i = 0; i < 26; i++) {
                            for (int j = 0; j < 26; j++) {
                                if (myState.getBoard().getPlayerBoard()[j][i] == playerNum) {
                                    csa.room = myState.getBoard().getBoard()[j][i].getRoom().getName();
                                    break loop;
                                }
                            }
                        }

                        csa.suspect = suspects[rand.nextInt(6)].getName();
                        csa.weapon = weapons[rand.nextInt(6)].getName();
                        Log.i("Computer Player " + playerNum, "Suggesting: "+ csa.room+". "+csa.suspect+". "+csa.weapon);
                        for (int i = 0; i < 21; i++) {
                            Log.i("Computer Player " + playerNum, i + ": " + checkbox[i]);
                        }
                        game.sendAction(csa);
                        return;

                    } else if (myState.getDieValue() != myState.getSpacesMoved()) {
                        Random rand = new Random();
                        int move = rand.nextInt(5) + 1;
                        Log.i("Computer Player " + playerNum, "Moving " + move);
                        sleep(300);

                        if (move == 1) {
                            game.sendAction(new ClueMoveLeftAction((this)));
                            return;
                        } else if (move == 2) {
                            game.sendAction(new ClueMoveUpAction((this)));
                            return;
                        } else if (move == 3) {
                            game.sendAction(new ClueMoveRightAction((this)));
                            return;
                        } else if (move == 4) {
                            game.sendAction(new ClueMoveDownAction(this));
                            return;
                        } else if (move == 5) {
                            game.sendAction(new ClueUsePassagewayAction(this));
                            return;
                        }

                        return;
                    } else if (numTrue == 18) {
                        Log.i("Computer Player " + playerNum, "Accusing");

                            for (int i = 0; i < 21; i++) {
                                Log.i("Computer Player " + playerNum, i + ": " + checkbox[i]);
                            }

                        int suspect = 0;
                        int weapon = 0;
                        int room = 0;
                        for (int i = 0; i < 6; i++) {
                            if (!checkbox[i]) {
                                suspect = i;
                            }

                            if (!checkbox[i + 6]) {
                                weapon = i;
                            }
                        }

                        for (int i = 0; i < 9; i++) {
                            if (!checkbox[i + 12]) {
                                room = i;
                            }
                        }

                        ClueAccuseAction caa = new ClueAccuseAction(this);
                        caa.suspect = suspects[suspect].getName();
                        caa.weapon = weapons[weapon].getName();
                        caa.room = rooms[room].getName();
                        game.sendAction(caa);
                        return;
                    } else {//Somehow stop this Write a can end turn method either in endturn action or cluestate variable
                        Log.i("Computer Player " + playerNum, "End Turn");
                        game.sendAction(new ClueEndTurnAction(this));
                        return;
                    }
                }
            } else {
                return;
            }
        }
            //if it enters a room, suggest random
            //if it uses all its moves and does not enter a room, end turn
    }

    private void checkbox(String card) {
        if (card.equals(Card.COL_MUSTARD.getName())) {
            this.checkbox[0] = true;
        } else if (card.equals(Card.PROF_PLUM.getName())) {
            this.checkbox[1] = true;
        } else if (card.equals(Card.MR_GREEN.getName())) {
            this.checkbox[2] = true;
        } else if (card.equals(Card.MRS_PEACOCK.getName())) {
            this.checkbox[3] = true;
        } else if (card.equals(Card.MISS_SCARLET.getName())) {
            this.checkbox[4] = true;
        } else if (card.equals(Card.MRS_WHITE.getName())) {
            this.checkbox[5] = true;
        } else if (card.equals(Card.KNIFE.getName())) {
            this.checkbox[6] = true;
        } else if (card.equals(Card.CANDLESTICK.getName())) {
            this.checkbox[7] = true;
        } else if (card.equals(Card.REVOLVER.getName())) {
            this.checkbox[8] = true;
        } else if (card.equals(Card.ROPE.getName())) {
            this.checkbox[9] = true;
        } else if (card.equals(Card.LEAD_PIPE.getName())) {
            this.checkbox[10] = true;
        } else if (card.equals(Card.WRENCH.getName())) {
            this.checkbox[11] = true;
        } else if (card.equals(Card.HALL.getName())) {
            this.checkbox[12] = true;
        } else if (card.equals(Card.LOUNGE.getName())) {
            this.checkbox[13] = true;
        } else if (card.equals(Card.DINING_ROOM.getName())) {
            this.checkbox[14] = true;
        } else if (card.equals(Card.KITCHEN.getName())) {
            this.checkbox[15] = true;
        } else if (card.equals(Card.BALLROOM.getName())) {
            this.checkbox[16] = true;
        } else if (card.equals(Card.CONSERVATORY.getName())) {
            this.checkbox[17] = true;
        } else if (card.equals(Card.BILLIARD_ROOM.getName())) {
            this.checkbox[18] = true;
        } else if (card.equals(Card.LIBRARY.getName())) {
            this.checkbox[19] = true;
        } else if (card.equals(Card.STUDY.getName())) {
            this.checkbox[20] = true;
        }
    }

    public boolean[] getCheckBoxArray() {
        return checkbox;
    }
}
