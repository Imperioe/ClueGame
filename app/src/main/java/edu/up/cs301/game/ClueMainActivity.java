package edu.up.cs301.game;

import android.content.pm.ActivityInfo;

import java.util.ArrayList;

import edu.up.cs301.game.config.GameConfig;
import edu.up.cs301.game.config.GamePlayerType;
import edu.up.cs301.game.util.GattServer;

/**
 * Created by Noah on 11/13/2016.
 */

public class ClueMainActivity extends GameMainActivity {

    public static final int PORT_NUMBER = 6732;

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
        playerTypes.add(new GamePlayerType("Human Player") {
            public GamePlayer createPlayer(String name) {
                return new ClueHumanPlayer(name, R.layout.game_clue_gui);
            }
        });
        //Dumb Computer player
        playerTypes.add(new GamePlayerType("Computer Player (dumb)") {
            public GamePlayer createPlayer(String name) {
                return new ClueComputerPlayerDumb(name);
            }
        });
        //Smart Computer Player
        playerTypes.add(new GamePlayerType("Computer Player (smart)") {
            public GamePlayer createPlayer(String name) {
                return new ClueComputerPlayerSmart(name);
            }
        });

        // Default Game config is created, taking in the playerTypes  minimum (3) players, maximum (6) players, and port number
        GameConfig defaultConfig = new GameConfig(playerTypes, 3, 6, "Clue", PORT_NUMBER, gattServer, bluetoothLeService);

        //Add default players with their player name and type
        defaultConfig.addPlayer("Human", 0);
        defaultConfig.addPlayer("Dumb Computer", 1);
        defaultConfig.addPlayer("Smart Computer", 2);

        //Set the remote data with the remote player as a type 0 player
        defaultConfig.setRemoteData("Remote Player", "", 0);
        defaultConfig.setBluetoothData("Bluetooth Player",1);

        //Set the game player types
        gamePlayerTypes = playerTypes;

        //Return the default configuration for setup
        return defaultConfig;
    }

    @Override
    public LocalGame createLocalGame()
    {
        //Return a new local game with the number of players desired in the game
        //The number of players is determined by the size of the table rows created
        //The number of table rows (on the host tablet for network) is equal to the number of players that will be in the game
        return new ClueLocalGame(tableRows.size());
    }
}
