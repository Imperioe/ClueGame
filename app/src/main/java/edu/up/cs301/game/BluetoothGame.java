package edu.up.cs301.game;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.util.LinkedList;
import java.util.Queue;

import edu.up.cs301.game.actionMsg.GameAction;
import edu.up.cs301.game.infoMsg.GameInfo;
import edu.up.cs301.game.util.BluetoothLeService;
import edu.up.cs301.game.util.BluetoothPasser;
import edu.up.cs301.game.util.GattServer;

public class BluetoothGame implements Game {
    private static String TAG = "BluetoothGame";

    // the player associated with this game
    private GamePlayer player;

    // a queue of objects that are collected, which might have been sent over the
    // bluetooth before we are connected to a player
    private Queue<GameInfo> queuedObjectsForPlayer = new LinkedList<GameInfo>();

    //the bluetooth connection object
    private BluetoothPasser bluetoothPasser;

    /**
     * Static method used instead of a constructor, so that null can be returned if
     * the creation was unsuccessful.
     *
     * @return
     */
    public static BluetoothGame create(/*BluetoothSocket bluetoothSocket,*/ BluetoothLeService bluetoothLeService, boolean host) {
        // create the game object
        BluetoothGame rtnVal = new BluetoothGame(/*bluetoothSocket,*/ bluetoothLeService, host);

        // see if a connection becomes established; if so, return
        // the object, otherwise null
        boolean isReady = true;//rtnVal.bluetoothPasser.isReady();//TODO: Maybe read
        if (isReady) {
            return rtnVal;
        }
        else {
            return null;
        }
    }

    //BluetoothGame Constructor (Private)
    private BluetoothGame(/*BluetoothSocket bluetoothSocket,*/ BluetoothLeService bluetoothLeService, boolean host){

        player = null;

        bluetoothPasser = new BluetoothPasser(/*bluetoothSocket,*/ bluetoothLeService, host){
            // callback method, called whenever an object is sent to us from
            // across bluetooth
            public void onReceiveObject(Object obj) {
                Log.i(TAG, "received object ("+obj.getClass()+")");
                try {
                    if (obj instanceof GameInfo) {
                        // object is a GameState object
                        GameInfo gs = (GameInfo)obj;
                        gs.setGame(BluetoothGame.this);
                        synchronized(this) {
                            if (player == null) {
                                // if the player has not been connected, save the
                                // object in a queue
                                Log.i(TAG, "adding object to queue");
                                queuedObjectsForPlayer.add(gs);
                            }
                            else {
                                // if the player has been connected, send the object
                                // directly to the player
                                Log.i(TAG, "about to send state to player");
                                player.sendInfo(gs);
                                Log.i(TAG, "... done sending state");
                            }
                        }
                    }else {
                        // ignore if the object is not a GameInfo object
                        Log.i(TAG, "object NOT being sent to player");
                    }
                }catch(Exception e){
                    Log.e(e.getClass().toString(), e.getMessage());
                }
            }
        };

    }

    /**
     * Method used by player to send an action to this Game object.
     *
     * @param action  the action object to apply
     */
    public final void sendAction(GameAction action) {
        // Send the action across the socket, nulling out the player in
        // the action so that the entire player is not serialized.
        if (action != null) {
            action.setPlayer(null);
            bluetoothPasser.sendObject(action);
        }
    }

    /**
     * Starts the game. In this context, we know that the array will
     * contain exactly one player.
     */
    public void start(GamePlayer[] players) {
        Log.i(TAG, "start() called");

        // if player has already been bound, ignore
        if (player != null) return;

        // if the player array somehow something other than
        // a single element, ignore
        if (players.length != 1) return;

        // start the player
        if (players[0] != null) {
            players[0].start(); // start our player
        }

        // loop through and empty (and send) the objects that might have
        // accumulated in the queue before the player was bound
        for (;;) {
            GameInfo unqueuedObject;
            synchronized (this) {
                if (queuedObjectsForPlayer.isEmpty()) {
                    // queue is finally empty: bind player and return
                    player = players[0];
                    return;
                }
                else {
                    // queue not empty, so remove an object from the queue
                    unqueuedObject = queuedObjectsForPlayer.remove();
                }
            }

            // send the just=unqueued object to the player
            Log.i(TAG, "sending queued object to player ("+unqueuedObject.getClass()+")");
            players[0].sendInfo(unqueuedObject);
        }
    }
}