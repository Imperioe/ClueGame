package edu.up.cs301.game;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import java.util.ArrayList;

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
 * Created by Noah on 11/8/2016.
 */

public class ClueHumanPlayer extends GameHumanPlayer implements CluePlayer, View.OnClickListener{
    //Tag for logging
    private static final String TAG = "ClueHumanPlayer";
    //State
    private ClueState recentState;
    //Booleans
    private boolean checkBoxBool[]; //Boolean array that keeps track of if the checkboxes are checked or not
    private boolean nameSet; //Boolean that determines whether or not the player has been assigned their player name (Which will be displayed on the GUI)
    //Move Buttons
    private Button rollButton;
    private Button upButton;
    private Button downButton;
    private Button leftButton;
    private Button rightButton;
    //Buttons
    private Button endTurnButton;
    private Button cancelButton;
    private Button submitButton;
    private Button secretPassagewayButton;
    //RadioButtons
    private RadioButton suggestR;
    private RadioButton showCardR;
    private RadioButton accuseR;
    //Spinners
    private Spinner roomSpinner;
    private Spinner weaponSpinner;
    private Spinner suspectSpinner;
    //TextViews
    private TextView numberOfMovesLeft; //message view for the number of moves left that the player can use to move their piece
    private TextView messageTextView; //message view for the card that is shown
    private TextView message2TextView; //message view for the who showed the card
    private TextView suggestionTextView; //message view for what cards were in the suggestion
    private TextView playerTextView; //message view to display which character you are
    private TextView turnTextView; //message view to display whose turn it currently is
    //Edit text for notes
    private EditText notesGUI;
    //Check Boxes
    private CheckBox colonelMustardCheck;
    private CheckBox professorPlumCheck;
    private CheckBox mrGreenCheck;
    private CheckBox mrsPeacockCheck;
    private CheckBox missScarletCheck;
    private CheckBox mrsWhiteCheck;
    private CheckBox knifeCheck;
    private CheckBox candlestickCheck;
    private CheckBox revolverCheck;
    private CheckBox ropeCheck;
    private CheckBox leadPipeCheck;
    private CheckBox wrenchCheck;
    private CheckBox hallCheck;
    private CheckBox loungeCheck;
    private CheckBox diningRoomCheck;
    private CheckBox kitchenCheck;
    private CheckBox ballroomCheck;
    private CheckBox conservatoryCheck;
    private CheckBox billiardRoomCheck;
    private CheckBox libraryCheck;
    private CheckBox studyCheck;
    //Board and card views
    private ClueBoardView boardView;
    private ClueCardView cardView;
    //Activity
    private Activity myActivity;



    private int layoutID;
    private boolean notSent = false; //Suggestion boolean for the human player - either a suggestion was sent or not

    public ClueHumanPlayer(String initName, int initID)
    {
        super(initName);

        checkBoxBool = new boolean[21]; //Set boolean array to false initially. When they are checked they will be set to true.
        layoutID = initID;
        nameSet = false; //The player is not assigned a character yet
    }

    @Override
    public View getTopView() {
        return null;
    }

    public int getID()
    {
        return playerNum;
    }

    public void setPlayerID(int newPlayerID) {
        playerNum = newPlayerID;
    }

    public void setAsGui(GameMainActivity g) {



        myActivity = g;
        g.setContentView(layoutID);
        //Declaration for the board view
        boardView = (ClueBoardView) myActivity.findViewById(R.id.boardView);
        //If the recent state is not null, update the board
        if(recentState != null) {
            boardView.updateBoard(recentState.getBoard());
        }
        //Declaration for the card view
        cardView = (ClueCardView) myActivity.findViewById(R.id.playerHandView);

        //GUI button/spinner/radio button declarations, listeners and default configurations
        upButton = (Button)myActivity.findViewById(R.id.upButton);
        upButton.setOnClickListener(this);
        upButton.setEnabled(true);

        downButton = (Button)myActivity.findViewById(R.id.downButton);
        downButton.setOnClickListener(this);
        downButton.setEnabled(true);

        leftButton = (Button)myActivity.findViewById(R.id.leftButton);
        leftButton.setOnClickListener(this);
        leftButton.setEnabled(true);

        rightButton = (Button)myActivity.findViewById(R.id.rightButton);
        rightButton.setOnClickListener(this);
        rightButton.setEnabled(true);

        endTurnButton = (Button)myActivity.findViewById(R.id.endTurnButton);
        endTurnButton.setOnClickListener(this);
        endTurnButton.setEnabled(true);

        suggestR = (RadioButton)myActivity.findViewById(R.id.radioSuggestButton);
        suggestR.setOnClickListener(this);
        suggestR.setEnabled(false);
        suggestR.setChecked(false);

        accuseR = (RadioButton)myActivity.findViewById(R.id.radioAccuseButton);
        accuseR.setOnClickListener(this);
        accuseR.setEnabled(false);
        accuseR.setChecked(false);

        showCardR = (RadioButton)myActivity.findViewById(R.id.radioShowCardButton);
        showCardR.setOnClickListener(this);
        showCardR.setChecked(false);
        showCardR.setEnabled(false);

        rollButton = (Button)myActivity.findViewById(R.id.rollButton);
        rollButton.setOnClickListener(this);
        rollButton.setEnabled(true);

        cancelButton = (Button)myActivity.findViewById(R.id.cancelButton);
        cancelButton.setOnClickListener(this);

        submitButton = (Button)myActivity.findViewById(R.id.submitButton);
        submitButton.setOnClickListener(this);

        secretPassagewayButton = (Button)myActivity.findViewById(R.id.secretPassagewayButton);
        secretPassagewayButton.setOnClickListener(this);
        secretPassagewayButton.setEnabled(false);

        //spinners
        roomSpinner = (Spinner)myActivity.findViewById(R.id.roomSpinner);
        weaponSpinner = (Spinner)myActivity.findViewById(R.id.weaponSpinner);
        suspectSpinner = (Spinner)myActivity.findViewById(R.id.suspectSpinner);

        //Set up the spinners with the default configuration with all of the rooms, weapons and suspects
        setSpinners();

        //CheckBoxes
        colonelMustardCheck = (CheckBox)myActivity.findViewById(R.id.colMustardCheckBox);
        colonelMustardCheck.setOnClickListener(this);

        professorPlumCheck = (CheckBox)myActivity.findViewById(R.id.profPlumCheckBox);
        professorPlumCheck.setOnClickListener(this);

        mrGreenCheck = (CheckBox)myActivity.findViewById(R.id.mrGreenCheckBox);
        mrGreenCheck.setOnClickListener(this);

        mrsPeacockCheck = (CheckBox)myActivity.findViewById(R.id.mrsPeacockCheckBox);
        mrsPeacockCheck.setOnClickListener(this);

        missScarletCheck = (CheckBox)myActivity.findViewById(R.id.missScarletCheckBox);
        missScarletCheck.setOnClickListener(this);

        mrsWhiteCheck = (CheckBox)myActivity.findViewById(R.id.mrsWhiteCheckBox);
        mrsWhiteCheck.setOnClickListener(this);

        knifeCheck = (CheckBox)myActivity.findViewById(R.id.knifeCheckBox);
        knifeCheck.setOnClickListener(this);

        candlestickCheck = (CheckBox)myActivity.findViewById(R.id.candlestickCheckBox);
        candlestickCheck.setOnClickListener(this);

        revolverCheck = (CheckBox)myActivity.findViewById(R.id.revolverCheckbox);
        revolverCheck.setOnClickListener(this);

        ropeCheck = (CheckBox)myActivity.findViewById(R.id.ropeCheckBox);
        ropeCheck.setOnClickListener(this);

        leadPipeCheck = (CheckBox)myActivity.findViewById(R.id.leadPipeCheckBox);
        leadPipeCheck.setOnClickListener(this);

        wrenchCheck = (CheckBox)myActivity.findViewById(R.id.wrenchCheckBox);
        wrenchCheck.setOnClickListener(this);

        hallCheck = (CheckBox)myActivity.findViewById(R.id.hallCheckBox);
        hallCheck.setOnClickListener(this);

        loungeCheck = (CheckBox)myActivity.findViewById(R.id.loungeCheckBox);
        loungeCheck.setOnClickListener(this);

        diningRoomCheck = (CheckBox)myActivity.findViewById(R.id.diningRoomCheckBox);
        diningRoomCheck.setOnClickListener(this);

        kitchenCheck = (CheckBox)myActivity.findViewById(R.id.kitchenCheckBox);
        kitchenCheck.setOnClickListener(this);

        ballroomCheck = (CheckBox)myActivity.findViewById(R.id.ballRoomCheckBox);
        ballroomCheck.setOnClickListener(this);

        conservatoryCheck = (CheckBox)myActivity.findViewById(R.id.conservatoryCheckBox);
        conservatoryCheck.setOnClickListener(this);

        billiardRoomCheck = (CheckBox)myActivity.findViewById(R.id.billiardCheckBox);
        billiardRoomCheck.setOnClickListener(this);

        libraryCheck = (CheckBox)myActivity.findViewById(R.id.libraryCheckBox);
        libraryCheck.setOnClickListener(this);

        studyCheck = (CheckBox)myActivity.findViewById(R.id.studyCheckBox);
        studyCheck.setOnClickListener(this);

        //Text view for moves left, displayed above move arrows
        numberOfMovesLeft = (TextView)myActivity.findViewById(R.id.numberOfMovesTextView);
        numberOfMovesLeft.setText(0+"");

        //Text view that displays what card is shown to the player who made the suggestion above the secret passageway button
        //For other players, it will say that a card was shown, but not say what specific card was shown, but it would say who showed the card
        //If no card is shown, it will say no card was shown
        messageTextView = (TextView)myActivity.findViewById(R.id.messageTextView);
        messageTextView.setText("");

        //Text view that displays who showed the card. The person is displayed for everyone, unless
        //no card was shown. In that case, no name will appear
        message2TextView = (TextView)myActivity.findViewById(R.id.message2TextView);
        message2TextView.setText("");

        //Text view that displays the suggestion that was made. This is visible for all players
        suggestionTextView = (TextView)myActivity.findViewById((R.id.suggestionTextView));
        suggestionTextView.setText("");

        //Text view that says who you are, displayed above the checkboxes
        playerTextView = (TextView)myActivity.findViewById(R.id.playerTextView);
        playerTextView.setText("You are a human\n player with a \nundetermined name.");

        //Text view that displays whose turn it currently is right below the messages
        turnTextView = (TextView)myActivity.findViewById(R.id.turnTextView);
        turnTextView.setText("");

        //Editable text box below the end turn button
        notesGUI = (EditText)myActivity.findViewById(R.id.editText);
        notesGUI.setOnClickListener(this);
    }

    @Override
    public void receiveInfo(GameInfo info)
    {
        //Assign and display the correct character for the human player if the name is not set already
        //This is displayed in the playerTextView
        if(info instanceof ClueState) {
            recentState = new ClueState((ClueState)info);
            if(recentState.getSetupPhase()){
                Logger.log("ERIC IS TACO TWO", "PLAYING THE GAME BUT NOT REALLY");
            }
            else{
                Logger.log("ERIC IS DOUBLE BURRITO", "Playing the real game!");
            }
            if(!nameSet) {
                switch (playerNum) {
                    case 0:
                        playerTextView.setText("You are Miss\n Scarlet.\n");
                        break;
                    case 1:
                        playerTextView.setText("You are Colonel\n Mustard.\n");
                        break;
                    case 2:
                        playerTextView.setText("You are Mrs.\n White.\n");
                        break;
                    case 3:
                        playerTextView.setText("You are Mr.\n Green.\n");
                        break;
                    case 4:
                        playerTextView.setText("You are Mrs.\n Peacock.\n");
                        break;
                    case 5:
                        playerTextView.setText("You are \nProfessor Plum.\n");
                        break;
                }
                nameSet = true;
            }

            //Check to see if the player is NOT in the game
            if(!recentState.getPlayerStillInGame(playerNum)) {
                //the player is out of the game, so disable all non-essential GUI things
                disableActionButtons();
                //disable and uncheck
                disableAndUncheckSuggestAndAccuse();
                //continue disabling
                disableCheckboxes();
                recentState.setCardToShow("\n You Lost!", playerNum); //display You Lost! on the messageTextView
                setSolutionSpinners(recentState.getSolution()); //set the spinners to the solution so the player can see what they should have accused
            }

            //If statement that handles the GUI when a player needs to show a card after a suggestion is made
            //Set notSent variable to true and then disable all the radio buttons except the showCard and set the appropriate spinners
            //that include cards in the player hand that match the cards in the suggestion
            if(recentState.getCheckCardToSend()[playerNum]) {
                notSent = true;
                disableAndUncheckSuggestAndAccuse();
                showCardR.setEnabled(true);
                showCardR.setChecked(true);
                roomSpinner.setEnabled(true);
                weaponSpinner.setEnabled(false);
                suspectSpinner.setEnabled(false);

                setSendCardSpinners();
            }
            //If it is not a player's turn to show a card, enable the regular GUI radio buttons to continue play
            // and disable the showCard radio button, and set the spinners to default with all the possibilities
            else if(!recentState.getCheckCardToSend()[playerNum] && recentState.getPlayerStillInGame(playerNum)) {
                suggestR.setEnabled(true);
                accuseR.setEnabled(true);
                showCardR.setChecked(false);
                showCardR.setEnabled(false);
                setSpinners();
            }
            //If they are not in the game anymore and it is not their turn to show a card, disable everything
            //and set the spinners to default with all the possibilities
            else if(!recentState.getCheckCardToSend()[playerNum] && !recentState.getPlayerStillInGame(playerNum)) {
                disableAndUncheckSuggestAndAccuse();
                showCardR.setChecked(false);
                showCardR.setEnabled(false);
                setSpinners();
            }

            //Boolean arrays from the recent state
            boolean corner[] = recentState.getInCornerRoom();
            boolean usedPassage[] = recentState.getUsedPassageway();
            boolean room[] = recentState.getInRoom();

            //Initial set up for human player's turn
            //Make sure it is the player's turn and they are still in the game
            if(recentState.getTurnId() == playerNum && recentState.getPlayerStillInGame(playerNum))
            {
                //If the player needs to show a card, allow them only to choose the card and submit it
                //Only allow them to submit the card they want to show
                if (showCardR.isEnabled())
                {
                    disableAndUncheckSuggestAndAccuse();
                    submitButton.setEnabled(true); //Only true
                    cancelButton.setEnabled(false);
                    disableActionButtons();
                    roomSpinner.setEnabled(true);
                }
                //If the player does not need to show a card, let them continue regular gameplay with
                //the appropriate buttons enabled
                else {
                    accuseR.setEnabled(true);
                    accuseR.setChecked(false); //only false
                    submitButton.setEnabled(true);
                    cancelButton.setEnabled(true);
                    endTurnButton.setEnabled(true);
                    upButton.setEnabled(true);
                    downButton.setEnabled(true);
                    leftButton.setEnabled(true);
                    rightButton.setEnabled(true);

                    if(recentState.getCanRoll(playerNum)) {
                        rollButton.setEnabled(true);
                    }else {
                        rollButton.setEnabled(false);
                    }
                }

                //If the player is in a corner room and has not used the secret passageway, enable the button
                //If the player has already used the secret passageway, disable the button
                if (corner[playerNum] && !usedPassage[playerNum] && recentState.getPlayerHasSuggested(playerNum) == false)
                {
                    secretPassagewayButton.setEnabled(true);
                }
                else {
                    secretPassagewayButton.setEnabled(false);
                }
                //If the player needs to show a card, disable the secret passageway button
                if(recentState.getCheckCardToSend()[playerNum]) {
                    secretPassagewayButton.setEnabled(false);
                }
            }
            //If it is the player's turn, but they are not in the game, just end their turn (basically skip them)
            else if(recentState.getTurnId() == playerNum && !recentState.getPlayerStillInGame(playerNum) && !notSent){
                disableAndUncheckSuggestAndAccuse();
                weaponSpinner.setEnabled(false);
                suspectSpinner.setEnabled(false);
                cancelButton.setEnabled(false);
                submitButton.setEnabled(false);
                endTurnButton.setEnabled(false);
                game.sendAction(new ClueEndTurnAction(this));
                disableSpinners();
                return;
            }
            //If it is not their turn and they are in the game, disable all GUI features
            else if(recentState.getTurnId() != playerNum && recentState.getPlayerStillInGame(playerNum))
            {
                disableAndUncheckSuggestAndAccuse();
                submitButton.setEnabled(false);
                cancelButton.setEnabled(false);
                disableActionButtons();
                disableSpinners();
            }
            //If a player was in a room, was new to the room and the show card radio wasn't enabled,
            //then set the suggest to true
            if (room[playerNum] && recentState.getNewToRoom(playerNum) && !showCardR.isEnabled() && recentState.getTurnId() == playerNum && !(recentState.getOnDoorTile())[playerNum]) {
                suggestR.setEnabled(true);
                suggestR.setChecked(false);
            }
            //If they are not, then the player cannot suggest
            else {
                suggestR.setEnabled(false);
                suggestR.setChecked(false);
            }

            //Text view that gets the card that was shown and displays it
            messageTextView.setText("Card: " + recentState.getCardToShow(playerNum));
            //Text view that gets the player who showed the card and displays it
            message2TextView.setText("Shown By: " + setPlayerWhoShowedCardName(recentState.getPlayerWhoShowedCard()));
            //Text view that gets the suggestion and displays it
            suggestionTextView.setText(setSuggestionText(recentState.getPlayerIDWhoSuggested()));
            //Text view that gets the current player's ID and displays it

            if (recentState.getTurnId() == 0) {
                turnTextView.setText("Turn: Miss Scarlet");
            }
            else if (recentState.getTurnId() == 1) {
                turnTextView.setText("Turn: Col. Mustard");
            }
            else if (recentState.getTurnId() == 2) {
                turnTextView.setText("Turn: Mrs. White");
            }
            else if (recentState.getTurnId() == 3) {
                turnTextView.setText("Turn: Mr. Green");
            }
            else if (recentState.getTurnId() == 4) {
                turnTextView.setText("Turn: Mrs. Peacock");
            }
            else if (recentState.getTurnId() == 5) {
                turnTextView.setText("Turn: Prof. Plum");
            }
            else {
                turnTextView.setText("Turn: ");
            }

            //updates the board
            boardView.updateBoard(recentState.getBoard());
            boardView.invalidate();
            //Updates the cards
            cardView.updateCards(recentState.getCards(playerNum));
            cardView.invalidate();
            //Updates the remaining moves a player has.
            if (recentState.getInRoom()[playerNum]) {
                //if there are no moves left, set it equal to zero
                if (recentState.getDieValue() - recentState.getSpacesMoved() == 0 ) {
                    numberOfMovesLeft.setText(0 + "");
                }
                //set it equal to 1 when they enter a room so they can continue to move around
                else {
                    numberOfMovesLeft.setText(1 + "");
                }
            }
            //set equal to the number of moves left
            else {
                numberOfMovesLeft.setText(recentState.getDieValue() - recentState.getSpacesMoved() + "");
            }
        }
    }



    private void disableSpinners() {
        roomSpinner.setEnabled(false);
        weaponSpinner.setEnabled(false);
        suspectSpinner.setEnabled(false);
    }

    private void disableActionButtons() {
        endTurnButton.setEnabled(false);
        rollButton.setEnabled(false);
        upButton.setEnabled(false);
        downButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        secretPassagewayButton.setEnabled(false);
    }

    private void disableAndUncheckSuggestAndAccuse() {
        suggestR.setEnabled(false);
        accuseR.setEnabled(false);
        suggestR.setChecked(false);
        accuseR.setChecked(false);
    }

    private void disableCheckboxes() {
        colonelMustardCheck.setEnabled(false);
        professorPlumCheck.setEnabled(false);
        mrGreenCheck.setEnabled(false);
        mrsPeacockCheck.setEnabled(false);
        missScarletCheck.setEnabled(false);
        mrsWhiteCheck.setEnabled(false);
        knifeCheck.setEnabled(false);
        candlestickCheck.setEnabled(false);
        revolverCheck.setEnabled(false);
        ropeCheck.setEnabled(false);
        leadPipeCheck.setEnabled(false);
        wrenchCheck.setEnabled(false);
        hallCheck.setEnabled(false);
        loungeCheck.setEnabled(false);
        diningRoomCheck.setEnabled(false);
        kitchenCheck.setEnabled(false);
        ballroomCheck.setEnabled(false);
        conservatoryCheck.setEnabled(false);
        billiardRoomCheck.setEnabled(false);
        libraryCheck.setEnabled(false);
        studyCheck.setEnabled(false);
    }

    /*
    Method that handles what happens when something on the GUI is clicked
     */
    public void onClick(View view)
    {
        //Check to see if the game is null, if it is, return
        if (game == null) {return;}

        //Send the appropriate actions for whatever is touched on the GUI

        //Roll and move buttons
        if (view == upButton) {
            ClueMoveUpAction up = new ClueMoveUpAction(this);
            game.sendAction(up);
        } else if (view == downButton) {
            ClueMoveDownAction down = new ClueMoveDownAction(this);
            game.sendAction(down);
        } else if (view == leftButton) {
            ClueMoveLeftAction left = new ClueMoveLeftAction(this);
            game.sendAction(left);
        } else if (view == rightButton) {
            ClueMoveRightAction right = new ClueMoveRightAction(this);
            game.sendAction(right);
        } else if (view == rollButton) {
            ClueRollAction roll = new ClueRollAction(this);
            rollButton.setEnabled(false);
            game.sendAction(roll);
        }

        //for radio buttons
        //If it is the accuse radio, uncheck the others
        //Set the spinners for all possibilities
        else if (view.getId() == R.id.radioAccuseButton) {
            suggestR.setChecked(false);
            accuseR.setChecked(true);
            showCardR.setChecked(false);
            roomSpinner.setEnabled(true);
            weaponSpinner.setEnabled(true);
            suspectSpinner.setEnabled(true);
            setSpinners();
        }
        //If it is the suggest radio, make sure the others aren't checked
        //Enable all the spinners for them to choose from
        else if (view.getId() == R.id.radioSuggestButton) {
            suggestR.setChecked(true);
            accuseR.setChecked(false);
            showCardR.setChecked(false);
            setSuggestSpinners();
            roomSpinner.setEnabled(true);
            weaponSpinner.setEnabled(true);
            suspectSpinner.setEnabled(true);

            //If they are on a door tile, do not let them submit a suggestion
            //This will prevent a player from ending their turn on a door tile and blocking the entrance of the room
            if(recentState.getOnDoorTile()[playerNum])
            {
                submitButton.setEnabled(false);
            }
            else
            {
                submitButton.setEnabled(true);
            }


        }
        //If the showCard radio is clicked, set the spinners for the showCard procedure
        else if (view.getId() == R.id.radioShowCardButton) {
            setSpinners();
        }
        //If the cancel button is pressed, uncheck the suggest and accuse radios
        else if (view.getId() == R.id.cancelButton) {
            suggestR.setChecked(false);
            accuseR.setChecked(false);
        }
        //If the submit button is pressed, check three cases: If it was the suggest, accuse or showCard button that was checked
        else if (view.getId() == R.id.submitButton) {
            //If the suggest radio is checked
            if (suggestR.isChecked() == true) {

                //Go through the board and get the position of the player
                int[][] board = recentState.getBoard().getPlayerBoard();
                int x = 0;
                int y = 0;
                for(int i=0;i<27;i++){
                    for(int j=0;j<27;j++){
                        if(board[j][i] == playerNum){
                            x = i;
                            y = j;
                        }
                    }
                }
                if(recentState.getBoard().getBoard()[y][x].getRoom() == null){
                    return;
                }

                //Set the spinners appropriately for a suggestion
                //The room is the current room, the weapon and suspect options are all available
                String roomSelect = recentState.getBoard().getBoard()[y][x].getRoom().getName();
                String weaponSelect = weaponSpinner.getSelectedItem().toString();
                String suspectSelect = suspectSpinner.getSelectedItem().toString();

                //Create new clue suggest action
                ClueSuggestionAction suggest = new ClueSuggestionAction(this);
                //Set the suggested room, weapon and suspect to the strings and send suggest action to the game
                suggest.room = roomSelect;
                suggest.weapon = weaponSelect;
                suggest.suspect = suspectSelect;
                game.sendAction(suggest);

                //Enable only the show card
                roomSpinner.setEnabled(true);
                submitButton.setEnabled(true);
                showCardR.setEnabled(true);
                showCardR.setChecked(false);
                disableAndUncheckSuggestAndAccuse();

                //ClueEndTurnAction end = new ClueEndTurnAction(this);
                endTurnButton.setEnabled(false);
                //game.sendAction(end);
            }
            //If the accuse radio is checked
            else if (accuseR.isChecked() == true) {
                //All spinner options are available for selection
                String roomSelect = roomSpinner.getSelectedItem().toString();
                String weaponSelect = weaponSpinner.getSelectedItem().toString();
                String suspectSelect = suspectSpinner.getSelectedItem().toString();

                //Send clue accuse action and set the accuse room, weapon and suspect strings and send the action to the game
                //Disable the accuse radio so the player can't use it again
                ClueAccuseAction accuse = new ClueAccuseAction(this);
                accuse.room = roomSelect;
                accuse.weapon = weaponSelect;
                accuse.suspect = suspectSelect;
                accuseR.setEnabled(false);
                game.sendAction(accuse);
            }
            //If the show card is checked
            else if (showCardR.isChecked() == true) {
                //Disable the show card and send in a show card
                //Disable the showCard radio so the player can't use it again
                showCardR.setEnabled(false);
                showCardR.setChecked(false);
                ClueShowCardAction showCard = new ClueShowCardAction(this);

                //Use only one spinner (Room spinner, the first spinner) for all the cards that can be shown
                //Send a showCard action to the game
                //Set the notSent to false
                String showCardString = roomSpinner.getSelectedItem().toString();
                showCard.setCardToShow(showCardString);
                game.sendAction(showCard);
                notSent = false;
            }

        }
        //If the secret passageway button is pressed, send in a secretPassagewayAction, send it to the game, and disable it
        //to prevent the player from using it again
        else if (view == secretPassagewayButton) {
            ClueUsePassagewayAction passageway = new ClueUsePassagewayAction(this);
            game.sendAction(passageway);
            secretPassagewayButton.setEnabled(false);
        }

        //If the end turn button is pressed, send in an endTurn action
        //Disable all the GUI buttons and send the endTurn action to the game
        else if (view == endTurnButton) {
            ClueEndTurnAction endTurn = new ClueEndTurnAction(this);
            disableActionButtons();
            suggestR.setEnabled(false);
            accuseR.setEnabled(false);
            submitButton.setEnabled(false);
            cancelButton.setEnabled(false);
            roomSpinner.setEnabled(false);
            weaponSpinner.setEnabled(false);
            suspectSpinner.setEnabled(false);
            game.sendAction(endTurn);
        }

        //CheckBoxes
        //Once a checkbox is checked, set the corresponding boolean array elements to true or false
        else if (view == colonelMustardCheck)
        {
            checkBoxBool[0] = !checkBoxBool[0];
            colonelMustardCheck.setChecked(checkBoxBool[0]);
        }
        else if (view == professorPlumCheck)
        {
            checkBoxBool[1] = !checkBoxBool[1];
            professorPlumCheck.setChecked(checkBoxBool[1]);
        }
        else if (view == mrGreenCheck)
        {
            checkBoxBool[2] = !checkBoxBool[2];
            mrGreenCheck.setChecked(checkBoxBool[2]);
        }
        else if (view == mrsPeacockCheck)
        {
            checkBoxBool[3] = !checkBoxBool[3];
            mrsPeacockCheck.setChecked(checkBoxBool[3]);
        }
        else if (view == missScarletCheck)
        {
            checkBoxBool[4] = !checkBoxBool[4];
            missScarletCheck.setChecked(checkBoxBool[4]);
        }
        else if (view == mrsWhiteCheck)
        {
            checkBoxBool[5] = !checkBoxBool[5];
            mrsWhiteCheck.setChecked(checkBoxBool[5]);
        }
        else if (view == knifeCheck)
        {
            checkBoxBool[6] = !checkBoxBool[6];
            knifeCheck.setChecked(checkBoxBool[6]);
        } else if (view == candlestickCheck) {
            checkBoxBool[7] = !checkBoxBool[7];
            candlestickCheck.setChecked(checkBoxBool[7]);
        } else if (view == revolverCheck) {
            checkBoxBool[8] = !checkBoxBool[8];
            revolverCheck.setChecked(checkBoxBool[8]);
        } else if (view == ropeCheck) {
            checkBoxBool[9] = !checkBoxBool[9];
            ropeCheck.setChecked(checkBoxBool[9]);
        } else if (view == leadPipeCheck) {
            checkBoxBool[10] = !checkBoxBool[10];
            leadPipeCheck.setChecked(checkBoxBool[10]);
        } else if (view == wrenchCheck) {
            checkBoxBool[11] = !checkBoxBool[11];
            wrenchCheck.setChecked(checkBoxBool[11]);
        } else if (view == hallCheck) {
            checkBoxBool[12] = !checkBoxBool[12];
            hallCheck.setChecked(checkBoxBool[12]);
        } else if (view == loungeCheck) {
            checkBoxBool[13] = !checkBoxBool[13];
            loungeCheck.setChecked(checkBoxBool[13]);
        } else if (view == diningRoomCheck) {
            checkBoxBool[14] = !checkBoxBool[14];
            diningRoomCheck.setChecked(checkBoxBool[14]);
        } else if (view == kitchenCheck) {
            checkBoxBool[15] = !checkBoxBool[15];
            kitchenCheck.setChecked(checkBoxBool[15]);
        } else if (view == ballroomCheck) {
            checkBoxBool[16] = !checkBoxBool[16];
            ballroomCheck.setChecked(checkBoxBool[16]);
        } else if (view == conservatoryCheck) {
            checkBoxBool[17] = !checkBoxBool[17];
            conservatoryCheck.setChecked(checkBoxBool[17]);
        } else if (view == billiardRoomCheck) {
            checkBoxBool[18] = !checkBoxBool[18];
            billiardRoomCheck.setChecked(checkBoxBool[18]);
        } else if (view == libraryCheck) {
            checkBoxBool[19] = !checkBoxBool[19];
            libraryCheck.setChecked(checkBoxBool[19]);
        } else if (view == studyCheck) {
            checkBoxBool[20] = !checkBoxBool[20];
            studyCheck.setChecked(checkBoxBool[20]);
        } else {
            return;
        }
    }

    /*
    Getter that gets the player ID
     */
    public int getPlayerID() {
        return playerNum;
    }

    /*
    Getter that gets the boolean array of checkboxes
     */
    public boolean[] getCheckBoxArray() {
        boolean temp[] = new boolean[21];
        for(int i = 0; i < 21; i++) {
            temp[i] = checkBoxBool[i];
        }
        return temp;
    }

    /*
    Method that returns the string of the player who showed the card
     */
    public String setPlayerWhoShowedCardName (int playerID) {
        switch(playerID){
            case 0:
                return "Miss Scarlet";
            case 1:
                return "Col. Mustard";
            case 2:
                return "Mrs. White";
            case 3:
                return "Mr. Green";
            case 4:
                return "Mrs. Peacock";
            case 5:
                return "Prof. Plum";
        }

        return ""; //if it is none of these, return an empty string
    }

    /*
    Method that sets the text to display in the suggestionTextView
     */
    public String setSuggestionText (int playerID) {
        //Need the cards suggested from the state
        String[] suggestCards = recentState.getSuggestCards();
        String suggestCardsText;

        for (int i = 0; i < suggestCards.length-1; i++) {
            if (suggestCards[i] == null) {
                return suggestCardsText = "Suggestion: ";
            }
        }
        //Get the three cards in the suggestion
        String card1 = suggestCards[0];
        String card2 = suggestCards[1];
        String card3 = suggestCards[2];
        //Set the text, returning after each card is printed for spacing
        suggestCardsText = "Suggestion: \n" + "   - " + card1 + "\n" + "   - " + card2 + "\n" + "   - " + card3;
        return suggestCardsText;
    }

    /*
    Method that sets the initial spinners with all of the rooms, weapons, and suspects
    Uses adapters to allow the items to be put into the spinners
     */
    public void setSpinners(){
        String[] roomItems = new String[]{Card.BALLROOM.getName(),Card.BILLIARD_ROOM.getName(), Card.CONSERVATORY.getName(),
                Card.DINING_ROOM.getName(), Card.HALL.getName(), Card.KITCHEN.getName(), Card.LIBRARY.getName(), Card.LOUNGE.getName(), Card.STUDY.getName()};
        String[] weaponItems = new String[]{Card.CANDLESTICK.getName(), Card.KNIFE.getName(), Card.LEAD_PIPE.getName(), Card.REVOLVER.getName(), Card.ROPE.getName(),Card.WRENCH.getName()};
        String[] suspectItem = new String []{Card.MR_GREEN.getName(), Card.COL_MUSTARD.getName(),Card.MRS_PEACOCK.getName(), Card.PROF_PLUM.getName(), Card.MISS_SCARLET.getName(), Card.MRS_WHITE.getName()};
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, roomItems);
        ArrayAdapter<String> weaponAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, weaponItems);
        ArrayAdapter<String> suspectAdapter = new ArrayAdapter<String>(myActivity,android.R.layout.simple_spinner_dropdown_item, suspectItem );

        roomSpinner.setAdapter(roomAdapter);
        weaponSpinner.setAdapter(weaponAdapter);
        suspectSpinner.setAdapter(suspectAdapter);

        submitButton.setEnabled(true);
    }
    /*
    Method that sets the spinners with all of the weapons and suspects and the room spinner is set to the current room
     */
    public void setSuggestSpinners() {
        String[] roomItems = new String[]{"Current Room"};
        String[] weaponItems = new String[]{Card.CANDLESTICK.getName(), Card.KNIFE.getName(), Card.LEAD_PIPE.getName(), Card.REVOLVER.getName(), Card.ROPE.getName(), Card.WRENCH.getName()};
        String[] suspectItem = new String[]{Card.MR_GREEN.getName(), Card.COL_MUSTARD.getName(), Card.MRS_PEACOCK.getName(), Card.PROF_PLUM.getName(), Card.MISS_SCARLET.getName(), Card.MRS_WHITE.getName()};
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, roomItems);
        ArrayAdapter<String> weaponAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, weaponItems);
        ArrayAdapter<String> suspectAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, suspectItem);

        roomSpinner.setAdapter(roomAdapter);
        weaponSpinner.setAdapter(weaponAdapter);
        suspectSpinner.setAdapter(suspectAdapter);
    }

    /*
    Method that sets the spinners to display the solution when a player accuses
     */
    private void setSolutionSpinners(Card[] solution) {
        String[] roomItems = new String[]{Card.BALLROOM.getName(),Card.BILLIARD_ROOM.getName(), Card.CONSERVATORY.getName(),
                Card.DINING_ROOM.getName(), Card.HALL.getName(), Card.KITCHEN.getName(), Card.LIBRARY.getName(), Card.LOUNGE.getName(), Card.STUDY.getName()};
        String[] weaponItems = new String[]{Card.CANDLESTICK.getName(), Card.KNIFE.getName(), Card.LEAD_PIPE.getName(), Card.REVOLVER.getName(), Card.ROPE.getName(),Card.WRENCH.getName()};
        String[] suspectItem = new String []{Card.MR_GREEN.getName(), Card.COL_MUSTARD.getName(),Card.MRS_PEACOCK.getName(), Card.PROF_PLUM.getName(), Card.MISS_SCARLET.getName(), Card.MRS_WHITE.getName()};
        ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, roomItems);
        ArrayAdapter<String> weaponAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, weaponItems);
        ArrayAdapter<String> suspectAdapter = new ArrayAdapter<String>(myActivity,android.R.layout.simple_spinner_dropdown_item, suspectItem );

        int suspect = 0;
        int weapon = 0;
        int room = 0;
        for(int i = 0; i<suspectItem.length;i++) {
            if (solution[0].getName().equals(suspectItem[i])) { //suspect
                suspect = i;
            }

            if (solution[1].getName().equals(weaponItems[i])) { //weapon
                weapon = i;
            }

            if (solution[2].getName().equals(roomItems[i])) { //room
                room = i;
            }
        }

        roomSpinner.setAdapter(roomAdapter);
        weaponSpinner.setAdapter(weaponAdapter);
        suspectSpinner.setAdapter(suspectAdapter);

        roomSpinner.setSelection(room);
        weaponSpinner.setSelection(weapon);
        suspectSpinner.setSelection(suspect);
    }

    /*
    Method that gets the cards from the player's hand and compares them to the suggestion made by another
    player. If the cards match, they will be put into one spinner (the room spinner), for them to choose
    whichever card they want to show to the other player.
     */
    public void setSendCardSpinners() {
        String[] temp = recentState.getSuggestCards();
        Hand tempHand = recentState.getCards(playerNum);
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

        //If there are cards to put into the room spinner, put them into the room spinner and disable the other spinners
        if(validCards.length == 0) {
            game.sendAction(new ClueShowCardAction(this));
            notSent = false;
        }
        else {
            ArrayAdapter<String> roomAdapter = new ArrayAdapter<String>(myActivity, android.R.layout.simple_spinner_dropdown_item, validCards);
            roomSpinner.setAdapter(roomAdapter);
            weaponSpinner.setEnabled(false);
            suspectSpinner.setEnabled(false);
        }
    }
}
