package edu.up.cs301.game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import edu.up.cs301.game.infoMsg.GameState;

/**
 * Created by Noah on 10/25/2016.
 */

public class ClueState extends GameState implements Serializable{
    //Tag for logging
    private static final String TAG = "ClueState";
    //instance variables
    private int turnID; //the ID of the player whose turn it is
    private int numPlayers; //the number of players in the game
    private int playerIDs[]; //starts at 0, then 1, 2, 3, 4, 5 if there are enough players
    private int dieValue; //the value of the die after the current players' roll
    private int spacesMoved; //the number of spaces the player whose turn it is moved
    private String notes[]; //stores the notes of the players
    private String playerNames[]; //the names of all the players
    private boolean canSuggest[]; //stores whether or not each players can make a suggestion
    private boolean canRoll[]; //stores whether or not each player can make a suggestion
    private boolean checkboxes[][]; //the checkboxes for each player
    private int cardsPerHand; //the max number of cards a player should be able to have in their hand
    private Hand cards[]; //the cards for each player
    private Card solution[] = new Card[3]; //the murderer, suspect, and weapon the players try to figure out
    private ArrayList<Card> allCards = new ArrayList<Card>(); //all the cards, used in the constructor
    private boolean gameOver; //whether or not the game is over
    private Board board; //the board itself
    private boolean newToRoom[]; //whether or not a player entered a room for the first time the current turn
    private boolean playerStillInGame[]; //whether or not each player is still in the game
    private boolean inCornerRoom[]; //whether or not each player is in a corner room
    private String[] suggestCards; //the cards in the most recent suggestion
    private int playerIDWhoSuggested; //the player that made the most recent suggestion
    private String[] cardToShow; //the most recent card shown to all players
    private boolean[] checkCardToSend; //whether or not a player needs to send a card
    private int playerWhoShowedCard = -1; //the playerID of the player who showed a card most recently
    private boolean usedPassageway[]; //whether or not each player has used a passageway the most recent turn
    private boolean[] inRoom; //whether or not each player is in a room
    private int winnerIndex; //the playerID of the winner
    private boolean[] onDoorTile; //whether or not each player is on a door tile
    private boolean hasSuggested[]; //whether or not each player has suggested the most recent turn
    private int playerInSuggestion; //the player who was suggested to be the murdered most recently
    private boolean[] pulledIn;

    // to satisfy Serializable interface - IDK if necessary
    private static final long serialVersionUID = 7737393762469851826L;


    public ClueState(int initNumPlayers, String initPlayerNames[], int initTurnID) {
        //sets up initial integer values
        turnID = initTurnID;
        dieValue = 0;
        spacesMoved = 0;
        numPlayers = initNumPlayers;
        playerIDWhoSuggested = -1;
        playerInSuggestion = -1;

        //tells the game how many cards to try and give each player
        if(numPlayers == 3) {
            cardsPerHand = 6;
        }else if(numPlayers == 4) {
            cardsPerHand = 5;
        }else if(numPlayers == 5) {
            cardsPerHand = 4;
        }else {
            cardsPerHand = 3;
        }

        //sets up arrays before loops assign values
        playerIDs = new int[numPlayers];
        playerNames = new String[numPlayers];
        canSuggest = new boolean[numPlayers];
        canRoll = new boolean[numPlayers];
        checkboxes = new boolean[numPlayers][21];
        cards = new Hand[numPlayers];
        newToRoom = new boolean[numPlayers];
        suggestCards = new String[3];
        cardToShow = new String[numPlayers];
        checkCardToSend = new boolean[numPlayers];
        playerStillInGame = new boolean[numPlayers];
        inRoom = new boolean[numPlayers];
        onDoorTile = new boolean[numPlayers];
        hasSuggested = new boolean[numPlayers];
        pulledIn = new boolean[numPlayers];

        //says that all players are still in the game
        for(int i=0;i<numPlayers;i++){
            playerStillInGame[i] = true;
        }

        for(int i =0; i < numPlayers;i++){
            cards[i] = new Hand();
        }

        //makes
        notes = new String[numPlayers];

        //makes an ArrayList of all cards
        for(Card c: Card.values()) {
            allCards.add(c);
        }

        //shuffle cards before dealing
        allCards = shuffle(allCards);
        allCards = shuffle(allCards);

        //sets up a solution for the players to figure out
        Card[] rooms = {Card.BALLROOM, Card.BILLIARD_ROOM, Card.CONSERVATORY, Card.DINING_ROOM, Card.HALL, Card.KITCHEN, Card.LOUNGE, Card.LIBRARY, Card.STUDY};
        Card[] suspects = {Card.MISS_SCARLET, Card.COL_MUSTARD, Card.MR_GREEN, Card.MRS_PEACOCK, Card.MRS_WHITE, Card.PROF_PLUM};
        Card[] weapons = {Card.WRENCH, Card.KNIFE, Card.CANDLESTICK, Card.REVOLVER, Card.ROPE, Card.LEAD_PIPE};
        Random rand = new Random();
        solution[0] = suspects[rand.nextInt(6)];
        solution[1] = weapons[rand.nextInt(6)];
        solution[2] = rooms[rand.nextInt(9)];
        allCards.remove(solution[0]);
        allCards.remove(solution[1]);
        allCards.remove(solution[2]);

        //creates board, which stores board tiles and player locations
        board = new Board();

        //puts remaining cards in players' hands
        int k = 0;
        while (allCards.size() != 0)
        {
            int index = allCards.size() - 1;
            cards[k].addCard(allCards.get(index));
            allCards.remove(index);
            if (k == numPlayers-1)
            {
                k = 0;
                continue;
            }
            k++;
        }

        //sets suggestion cards to null, as a suggestion has not ben made yet
        for(int i = 0; i < 3; i++) {
            suggestCards[i] = null;
        }

        //says that the game isn't over
        gameOver = false;

        //sets up boolean arrays
        inCornerRoom = new boolean[numPlayers];
        usedPassageway = new boolean[numPlayers];

        //sets up more arrays, all of length numPlayers
        for(int i = 0; i < numPlayers; i++) {
            playerIDs[i] = i;
            canSuggest[i] = false;
            canRoll[i] = false;
            notes[i] = "";
            cardToShow[i] = "";
            playerNames[i] = initPlayerNames[i]+"";
            Arrays.fill(checkboxes[i], false);
            pulledIn[i] = false;
        }
        //turnID is the same value as the playerID, which is the same as the index in the array
        canRoll[turnID] = true;

        //sets the index of the winner to -1, will be changed when a player wins
        winnerIndex = -1;
    }

    //shuffles all the cards before they are dealt
    private ArrayList<Card> shuffle(ArrayList<Card> cards) {
        Random rand = new Random();
        ArrayList<Card> rtnCards = cards;
        for(int i = 0; i<rtnCards.size();i++){
            int transferToIdx = rand.nextInt(rtnCards.size());
            Card thisCard = rtnCards.get(i);
            Card idxCard = rtnCards.get(transferToIdx);
            rtnCards.set(i, idxCard);
            rtnCards.set(transferToIdx, thisCard);
        }
        return  rtnCards;
    }

    //copy constructor
    public ClueState(ClueState s) {
        //sets variables in new ClueState
        turnID = s.turnID;
        numPlayers = s.getNumPlayers();
        playerIDs = new int[numPlayers];
        dieValue = s.getDieValue();
        spacesMoved = s.getSpacesMoved();
        notes = new String[numPlayers];
        playerNames = new String[numPlayers];
        canSuggest = new boolean[numPlayers];
        canRoll = new boolean[numPlayers];
        checkboxes = new boolean[numPlayers][21];
        cardsPerHand = s.getCardsPerHand();
        cards = new Hand[numPlayers];
        solution = s.getSolution();
        gameOver = s.getGameOver();
        board = new Board();
        newToRoom = new boolean[numPlayers];
        playerStillInGame = new boolean[numPlayers];
        inCornerRoom = new boolean[numPlayers];
        suggestCards = new String[3];
        playerIDWhoSuggested = s.getPlayerIDWhoSuggested();
        cardToShow = new String[numPlayers];
        checkCardToSend = new boolean[numPlayers];
        usedPassageway = new boolean[numPlayers];
        inRoom = new boolean[numPlayers];
        winnerIndex = s.getWinnerIndex();
        onDoorTile = s.getOnDoorTile();
        playerWhoShowedCard = s.getPlayerWhoShowedCard();
        hasSuggested = s.getHasSuggested();
        playerInSuggestion = s.getPlayerInSuggestion();
        pulledIn = new boolean[numPlayers];

        //sets variables in arrays with a length of numPlayers
        for(int i = 0; i < numPlayers; i++) {
            playerIDs[i] = s.getPlayerID(i);
            notes[i] = s.getNotes(i);
            playerNames[i] = s.getPlayerName(i);
            canSuggest[i] = s.getCanSuggest(i);
            canRoll[i] = s.getCanRoll(i);
            newToRoom[i] = s.getNewToRoom(i);
            playerStillInGame[i] = s.getPlayerStillInGame(i);
            inCornerRoom[i] = s.getInCornerRoom(i);
            cardToShow[i] = s.getCardToShow(i);
            checkCardToSend[i] = s.getCheckCardToSend(i);
            usedPassageway[i] = s.getUsedPassageway(i);
            inRoom[i] = s.getInRoom(i);
            hasSuggested[i] = s.getPlayerHasSuggested(i);
            pulledIn[i] = s.getPulledIn(i);
        }

        //copies players' hands
        for(int i = 0; i < numPlayers; i++) {
            cards[i] = new Hand();
            for (int j = 0; j < cardsPerHand; j++) {
                if(s.getCards(i) != null && s.getCards(i).getCards() != null && s.getCards(i).getCards().length > j && s.getCards(i).getCards()[j] != null) {
                    cards[i].addCard(s.getCards(i).getCards()[j]);
                }
            }
        }

        //copies checkboxes
        for(int i = 0; i < numPlayers; i++) {
            for(int j = 0; j < 21; j++) {
                checkboxes[i][j] = s.getCheckBox(i, j);
            }
        }

        //copies suggestion cards
        for(int i =0;i<3;i++){
            suggestCards[i] = s.getSuggestCards(i);
        }

        //copies board
        board.setBoard(s.getBoard().getBoard());
        board.setPlayerBoard(s.getBoard().getPlayerBoard());
    }

    private String getSuggestCards(int i) {
        return suggestCards[i];
    }

    private boolean getInRoom(int i) {
        return inRoom[i];
    }

    private boolean getUsedPassageway(int i) {
        return usedPassageway[i];
    }

    private boolean getCheckCardToSend(int i) {
        return checkCardToSend[i];
    }

    private boolean getInCornerRoom(int i) {
        return inCornerRoom[i];
    }

    //getters
    public int getTurnId() {
        return turnID;
    }

    public int getDieValue() {
        return dieValue;
    }

    public int getSpacesMoved() {
        return spacesMoved;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public int getRoom(int playerID) {
        //flesh out more
        return 1;
    }

    public int getPlayerID(int index) {
        return playerIDs[index];
    }

    public String getNotes(int playerID) {
        return notes[playerID];
    }

    public String getPlayerName(int playerID) {
        return playerNames[playerID];
    }

    public boolean getCanSuggest(int playerID) {
        return canSuggest[playerID];
    }

    public boolean getCanRoll(int playerID) {
        return canRoll[playerID];
    }

    public boolean getCheckBox(int playerID, int index) {
        return checkboxes[playerID][index];
    }

    public boolean getGameOver() {
        return gameOver;
    }

    public Hand getCards(int index) {
        return new Hand(cards[index]);
    }

    public int getCardsPerHand() {
        return cardsPerHand;
    }

    public Board getBoard(){return board;}

    public Card[] getSolution() {
        if(solution != null) {
            Card temp[] = new Card[3];
            temp[0] = solution[0];
            temp[1] = solution[1];
            temp[2] = solution[2];
            return temp;
        }else {
            return null;
        }

    }

    public boolean getNewToRoom(int playerID){ return newToRoom[playerID];}

    public boolean[] getInCornerRoom()
    {
        return inCornerRoom;
    }

    public boolean[] getUsedPassageway()
    {
        return usedPassageway;
    }

    public String[] getSuggestCards() {
        return suggestCards;
    }

    public int getPlayerIDWhoSuggested() {
        return playerIDWhoSuggested;
    }

    public String getCardToShow(int playerID) {
        return cardToShow[playerID];
    }

    public boolean getPlayerStillInGame(int playerID){
        return playerStillInGame[playerID];
    }

    public boolean[] getCheckCardToSend() {
        return checkCardToSend;
    }

    public int getPlayerWhoShowedCard() { return playerWhoShowedCard;}

    public boolean[] getInRoom () { return inRoom; }

    public boolean[] getOnDoorTile() {return onDoorTile; }

    public int getWinnerIndex() {
        return winnerIndex;
    }

    public boolean getPlayerHasSuggested (int playerID) { return hasSuggested[playerID];}

    public boolean[] getHasSuggested () { return hasSuggested;}

    public int getPlayerInSuggestion () {return playerInSuggestion;}


    //setters
    public void setNewToRoom(int playerID, boolean newTo)
    {
        newToRoom[playerID] = newTo;
    }

    public void setTurnID(int newTurnID) {
        turnID = newTurnID;
    }

    public void setNumPlayers(int newNumPlayers) {
        numPlayers = newNumPlayers;
    }

    public void setPlayerID(int newID, int index) {
        playerIDs[index] = newID;
    }

    public void setDieValue(int newDieValue) {
        dieValue = newDieValue;
    }

    public void setSpacesMoved(int newSpacesMoved) {
        spacesMoved = newSpacesMoved;
    }

    public void setNotes(int index, String newNotes) {
        notes[index] = newNotes;
    }

    public void setPlayerName(int index, String newName) {
        playerNames[index] = newName;
    }

    public void setCanSuggest(int index, boolean newCanSuggest) {
        canSuggest[index] = newCanSuggest;
    }

    public void setCanRoll(int index, boolean newCanRoll) {
        canRoll[index] = newCanRoll;
    }

    public void setCheckBox(int playerID, int index, boolean newVal) {
        checkboxes[playerID][index] = newVal;
    }

    public void setCards(int playerID, Hand h) {
        cards[playerID] = h;
    }

    public void setInCornerRoom(int playerID, boolean inRoom)
    {
        inCornerRoom[playerID] = inRoom;
    }

    public void setUsedPassageway(int playerID, boolean used)
    {
        usedPassageway[playerID] = used;
    }

    public void setSuggestCards(String[] newSuggestCards) {
        suggestCards = newSuggestCards;
    }

    public void setPlayerIDWhoSuggested(int newID) {
        playerIDWhoSuggested = newID;
    }

    public void setCardToShow(String newCardToShow, int playerID) {
        cardToShow[playerID] = newCardToShow;
    }

    public void setCheckCardToSend(int playerID, boolean value) {
        checkCardToSend[playerID] = value;
    }

    public void setPlayerWhoShowedCard (int playerID) {playerWhoShowedCard = playerID;}

    public void setGameOver(boolean newGameOver) {
        gameOver = newGameOver;
    }

    public void setPlayerStillInGame(int playerID, boolean b){
        playerStillInGame[playerID] = b;
    }

    public void setInRoom (int playerID, boolean a) {inRoom[playerID] = a;}

    public void setWinnerIndex(int newWinnerIndex) {
        winnerIndex = newWinnerIndex;
    }

    public void setOnDoorTile(int playerID, boolean onDoor) {onDoorTile[playerID] = onDoor; }

    public void setSolution(Card[] newSolution) {
        solution = newSolution;
    }

    public void setHasSuggested (int playerID, boolean suggested) {hasSuggested[playerID] = suggested;}

    public void setPlayerInSuggestion (int playerID) {playerInSuggestion = playerID;}

    public boolean getPulledIn(int playerID) {
        return pulledIn[playerID];
    }

    public void setPulledIn(int playerID, boolean b) {
        pulledIn[playerID] = b;
    }

    //Getters and setters for the setupPhase information
    public void setSetupPhase(boolean phase){inSetupPhase = phase;}
    public boolean getSetupPhase(){return  inSetupPhase;}
}

