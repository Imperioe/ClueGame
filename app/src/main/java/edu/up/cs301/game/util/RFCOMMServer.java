package edu.up.cs301.game.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.Context;
import android.os.ParcelUuid;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

import edu.up.cs301.game.R;

public class RFCOMMServer {
    private static final String TAG = "RFCOMMServer";
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private AcceptThread acceptThread;
    private BluetoothManager bluetoothManager;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;

    public boolean startRFCOMMServer(Context context){
        this.context = context;
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        bluetoothManager =
                (BluetoothManager) this.context.getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(this.context, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            //this.context.finish();
            return false;
        }

        //Other Device knows what device to connect to
        startAdvertising();
        //Start the server to connect to
        acceptThread = (new AcceptThread());
        Log.i(TAG, "Starting Accept Thread");
        acceptThread.start();
        return true;
    }

    public BluetoothSocket getSocket(){
        return acceptThread.mmSocket;
    }

    public void stopRFCOMMServer(){
        acceptThread.cancel();
    }

    //Needs to tell the other device it exists
    private void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        mBluetoothLeAdvertiser = bluetoothAdapter.getBluetoothLeAdvertiser();
        if (mBluetoothLeAdvertiser == null) {
            Log.w(TAG, "Failed to create advertiser");
            return;
        }

        //Set up Advertising Settings
        AdvertiseSettings settings = new AdvertiseSettings.Builder()
                .setAdvertiseMode(AdvertiseSettings.ADVERTISE_MODE_BALANCED)
                .setConnectable(true)
                .setTimeout(0)
                .setTxPowerLevel(AdvertiseSettings.ADVERTISE_TX_POWER_MEDIUM)
                .build();

        //Other side indirectly accesses this
        ParcelUuid p = new ParcelUuid(UUID.fromString("4e414d45-0000-1000-8000-00805f9b34fb"));
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceData(p, context.getResources().getString(R.string.app_name).getBytes())
                .build();


        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    /**
     * Callback to receive information about the advertisement process.
     */
    private AdvertiseCallback mAdvertiseCallback = new AdvertiseCallback() {
        @Override
        public void onStartSuccess(AdvertiseSettings settingsInEffect) {
            Log.i(TAG, "LE Advertise Started.");
        }

        @Override
        public void onStartFailure(int errorCode) {
            Log.w(TAG, "LE Advertise Failed: "+errorCode);
        }
    };

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        private BluetoothSocket mmSocket;

        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket
            // because mmServerSocket is final.
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code.
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(context.getString(R.string.app_name), UUID.fromString("00009999-0000-1000-8000-00805f9b34fb"));
                Log.i(TAG, "Socket's listen() was successful");
            } catch (IOException e) {
                Log.e(TAG, "Socket's listen() method failed", e);
            }
            mmServerSocket = tmp;
        }

        public void run() {
            Log.i(TAG, "Running Accept");
            BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned.
            while (true) {
                try {
                    socket = mmServerSocket.accept();
                    Log.i(TAG, "Socket was accepted");
                } catch (IOException e) {
                    Log.e(TAG, "Socket's accept() method failed", e);
                    break;
                }

                if (socket != null) {
                    // A connection was accepted. Perform work associated with
                    // the connection in a separate thread.
                    //manageMyConnectedSocket(socket);
                    //mmServerSocket.close();
                    mmSocket = socket;
                    //TODO: Do something with this socket
                    break;
                }
            }
        }

        // Closes the connect socket and causes the thread to finish.
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}
