package edu.up.cs301.game;

import android.content.pm.ActivityInfo;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.ArrayList;

import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import edu.up.cs301.game.util.Logger;

/**
 * Created by Noah on 11/13/2016.
 */

public class ClueMainActivity extends GameMainActivity {
    //Tag for logging
    private static final String TAG = "ClueMainActivity";

    public static final int PORT_NUMBER = 6732;
    private int numberOfaccuse = 1;

    //An array list created for the player's types
    private ArrayList<GamePlayerType> gamePlayerTypes;

    public ClueMainActivity() {
        super();
    }

    @Override
    public GameConfig createDefaultConfig() {

        //locks screen to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //Create array list for the player types
        ArrayList<GamePlayerType> playerTypes = new ArrayList<GamePlayerType>();

        /*
        Types of players declared: Human, dumb computer player and smart computer player
         */
        //Human player
        playerTypes.add(new GamePlayerType(getString(R.string.Human_Player_Name)) {
            public GamePlayer createPlayer(String name) {
                return new ClueHumanPlayer(name, R.layout.game_clue_gui);
            }
        });
        //Dumb Computer player
        playerTypes.add(new GamePlayerType(getString(R.string.Computer_Dumb_Name)) {
            public GamePlayer createPlayer(String name) {
                return new ClueComputerPlayerDumb(name);
            }
        });
        //Smart Computer Player
        playerTypes.add(new GamePlayerType(getString(R.string.Computer_Smart_Name)) {
            public GamePlayer createPlayer(String name) {
                return new ClueComputerPlayerSmart(name);
            }
        });


        // Default Game config is created, taking in the playerTypes  minimum (3) players, maximum (6) players, and port number
        GameConfig defaultConfig = new GameConfig(playerTypes, 3, 6, "Clue", PORT_NUMBER);

        //Add default players with their player name and type
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Dumb Computer", 1);
        defaultConfig.addPlayer("Smart Computer", 2);

        //Set the remote data with the remote player as a type 0 player
        defaultConfig.setRemoteData("Remote Player", "", 0);

        //Set the game player types
        gamePlayerTypes = playerTypes;

        //Return the default configuration for setup
        return defaultConfig;
    }

    @Override
    protected void initSettingsTab() {
        super.initSettingsTab();
        //Laying out seeker bar so number of Accusations can be changed
        SeekBar sk = (SeekBar) findViewById(R.id.accuseNumberSeeker);
        sk.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                numberOfaccuse = i+1;
                TextView t=(TextView)findViewById(R.id.accuseNumberText);
                t.setText(getString(R.string.accuseNumberString)+" "+String.valueOf(i+1));
            }

            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        //Text view to display the number of Accusations allowed
        TextView t=(TextView)findViewById(R.id.accuseNumberText);
        t.setText(getString(R.string.accuseNumberString)+" "+1);
    }

    @Override
    public LocalGame createLocalGame()
    {
        //Return a new local game with the number of players desired in the game
        //The number of players is determined by the size of the table rows created
        //The number of table rows (on the host tablet for network) is equal to the number of players that will be in the game
        return new ClueLocalGame(tableRows.size(), numberOfaccuse);
    }
}
