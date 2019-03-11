package edu.up.cs301.game;

import android.nfc.Tag;
import android.util.Log;

import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.BindGameInfo;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.BluetoothLeService;
import edu.up.cs301.game.util.BluetoothPasser;
import edu.up.cs301.game.util.GattServer;

//@author Eric Imperio

public class BluetoothPlayer implements GamePlayer  {
    private static String TAG = "BluetoothPlayer";
    // the game object
    private Game game;
    private BluetoothPasser bluetoothPasser;

    /**
     * BluetoothPlayer constructor.
     *
     *
     * 		the port number through which we connect to our client
     */
    public BluetoothPlayer(GattServer gattServer, BluetoothLeService bluetoothLeService) { //TODO: Maybe indicate which tablet

        Log.i(TAG, "creating Bluetooth Player");

        // set instance variables to their initial values
        game = null; // the game
        //isReady = false; // whether we are ready

        // create our network-connection object, connecting as a server
        bluetoothPasser =
                new BluetoothPasser(gattServer, bluetoothLeService) { //TODO: Maybe indicate which table

                    // callback method, called whenever we receive an object
                    // that has come across the network
                    public void onReceiveObject(Object obj) {
                        if (obj instanceof GameAction) {
                            // if it's a game action (which it should be), send
                            // the action to the game
                            GameAction action = (GameAction)obj;
                            action.setPlayer(BluetoothPlayer.this);
                            game.sendAction(action);
                        }
                    }
                };
    }

    /**
     * Used by the game to send a GameInfo object to this player
     *
     * @param state
     * 		The state to send
     */
    public void sendInfo(GameInfo state) {
        if (game == null && state instanceof BindGameInfo) {
            // If we're just starting (so we don't know who
            // game is), then it had better be a BindGameInfo
            // message. Get the game from the BindGameInfo
            // object so that we have the connection for
            // future messages.
            game = ((BindGameInfo)state).getGame();
        }

        // Null out the game from the GameInfo object (if present),
        // so that the entire game does not get passed across Bluetooth
        state.setGame(null);

        // send the state across the network
        bluetoothPasser.sendObject(state);
    }

    /**
     * Starts the game. (In this case the constructor has already done
     * all the work.)
     */
    public void start() {
    }

    /**
     * Set this game as a GUI. (Should never be called because the
     * 'supportsGui' method returns false.)
     */
    public final void gameSetAsGui(GameMainActivity a) {
    }

    /**
     * Set this game as a GUI. (Should never be called because the
     * 'supportsGui' method returns false.)
     */
    public void setAsGui(GameMainActivity a) {
    }

    /**
     * Tells whether the this player requires a GUI.
     *
     * @return
     * 		false, since this player does not require a GUI
     */
    public boolean requiresGui() {
        return false;
    }

    /**
     * Tells whether the this player support a GUI.
     *
     * @return
     * 		false, since this player does not support a GUI
     */
    public boolean supportsGui() {
        return false;
    }
}
