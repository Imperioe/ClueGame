package edu.up.cs301.game.util;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattServerCallback;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.AdvertiseCallback;
import android.bluetooth.le.AdvertiseData;
import android.bluetooth.le.AdvertiseSettings;
import android.bluetooth.le.BluetoothLeAdvertiser;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.LinkedTransferQueue;

import edu.up.cs301.game.R;

import static android.content.Context.BLUETOOTH_SERVICE;

public class GattServer {
    //A Tag for logging
    private static final String TAG = "GattServer";

    //Context for Server
    Context context;
    /* Bluetooth API */
    private BluetoothManager mBluetoothManager;
    private BluetoothGattServer mBluetoothGattServer;
    private BluetoothLeAdvertiser mBluetoothLeAdvertiser;
    /* Collection of notification subscribers */
    private Set<BluetoothDevice> mRegisteredDevices = new HashSet<BluetoothDevice>();

    //Received Object
    private boolean hasWrite = false;
    private Object receievedObject = null;
    private char[] transferString = null;
    private int transferCount = 0;
    private int writeCount = 0;

    //Sending Object
    private boolean canRead = false;
    private boolean sending = false;
    private int transferSendCount = 0;
    private int sendCount = 0;
    private LinkedTransferQueue<QueueObject> toSendQueue = new LinkedTransferQueue<>();

    public boolean startGattServer(Context context){
        this.context = context;
        //Set up Gatt Server for Bluetooth games
        mBluetoothManager = (BluetoothManager) this.context.getSystemService(BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();

        // Register for system Bluetooth events
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        this.context.registerReceiver(mBluetoothReceiver, filter);

        //Start the Gatt Server
        if (!bluetoothAdapter.isEnabled()) {
            Log.d(TAG, "Bluetooth is currently disabled...enabling");
            bluetoothAdapter.enable();
            //Double check the Adapter started
            if(!bluetoothAdapter.isEnabled()){
                Log.e(TAG, "BLE Adapter Failed to start.");
                return false;
            }
        }
        Log.d(TAG, "Bluetooth enabled...starting services");
        startAdvertising();
        startServer();
        Log.i("Gatt Server", "Gatt Server started");
        return true;
    }

    private BroadcastReceiver mBluetoothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    startAdvertising();
                    startServer();
                    break;
                case BluetoothAdapter.STATE_OFF:
                    stopServer();
                    stopAdvertising();
                    break;
                default:
                    // Do nothing
            }

        }
    };

    //Bluetooth Methods
    //region Bluetooth
    /**
     * Begin advertising over Bluetooth that this device is connectible
     * and supports the Current Time Service.
     */
    private void startAdvertising() {
        BluetoothAdapter bluetoothAdapter = mBluetoothManager.getAdapter();
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

        //TODO: Check This Make this get displayed on the other tablet
        ParcelUuid p = new ParcelUuid(UUID.fromString("4e414d45-0000-1000-8000-00805f9b34fb"));//GameAdvertisingProfile.NAME_SERVICE);
        AdvertiseData data = new AdvertiseData.Builder()
                .setIncludeDeviceName(true)
                .setIncludeTxPowerLevel(false)
                .addServiceData(p, context.getResources().getString(R.string.app_name).getBytes())
                .build();


        mBluetoothLeAdvertiser.startAdvertising(settings, data, mAdvertiseCallback);
    }

    /**
     * Stop Bluetooth advertisements.
     */
    private void stopAdvertising() {
        if (mBluetoothLeAdvertiser == null) return;

        mBluetoothLeAdvertiser.stopAdvertising(mAdvertiseCallback);
    }

    /**
     * Initialize the GATT server instance with the services/characteristics
     * from the Time Profile.
     */
    private void startServer() {
        mBluetoothGattServer = mBluetoothManager.openGattServer(context, mGattServerCallback);
        if (mBluetoothGattServer == null) {
            Log.w(TAG, "Unable to create GATT server");
            return;
        }

        mBluetoothGattServer.addService(DataTransferProfile.createDataTransferService());
        //TODO: Remove
        //mBluetoothGattServer.addService(TimeProfile.createTimeService());

        // Initialize the local UI
        //updateLocalUi(System.currentTimeMillis());
    }

    /**
     * Shut down the GATT server.
     */
    private void stopServer() {
        if (mBluetoothGattServer == null) return;

        mBluetoothGattServer.close();
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

    /**
     * Callback to handle incoming requests to the GATT server.
     * All read/write requests for characteristics and descriptors are handled here.
     */
    private BluetoothGattServerCallback mGattServerCallback = new BluetoothGattServerCallback() {

        @Override
        public void onConnectionStateChange(BluetoothDevice device, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.i(TAG, "BluetoothDevice CONNECTED: " + device);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.i(TAG, "BluetoothDevice DISCONNECTED: " + device);
                //Remove device from any active subscriptions
                mRegisteredDevices.remove(device);
            }
        }

        @Override
        public void onCharacteristicWriteRequest(BluetoothDevice device, int requestId,
                                                 BluetoothGattCharacteristic characteristic,
                                                 boolean preparedWrite, boolean responseNeeded,
                                                 int offset, byte[] value){
            Log.i(TAG, "Write Requested");
        }

        @Override
        public void onCharacteristicReadRequest(BluetoothDevice device, int requestId, int offset,
                                                BluetoothGattCharacteristic characteristic) {
                // Invalid characteristic
                Log.w(TAG, "Invalid Characteristic Read: " + characteristic.getUuid());
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
        }

        @Override
        public void onDescriptorReadRequest(BluetoothDevice device, int requestId, int offset,
                                            BluetoothGattDescriptor descriptor) {
            //TODO: The Client must request an update
            if (DataTransferProfile.TRANSFER_DESC01.equals(descriptor.getUuid())) {
                Log.d(TAG, "Transfer Descriptor read");
                if(!canRead){
                    Log.w(TAG, "Waiting for Updated State from Client");
                    mBluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0,
                            "Waiting for Update".getBytes());
                    return;
                }

                if(sending){
                    Log.w(TAG, "Already Sending");
                    mBluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0,
                            "Sending".getBytes());
                    return;
                }

                sending = true;
                //Get the next item to send, tell the Client how many packets to request.

                QueueObject qo = toSendQueue.peek();
                if(qo == null){
                    mBluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0,
                            "No Update".getBytes());
                    sending = false;
                    return;
                }else if(qo.getUUID() != descriptor.getUuid()){
                    mBluetoothGattServer.sendResponse(device,
                            requestId,
                            BluetoothGatt.GATT_FAILURE,
                            0,
                            "Wrong UUID Request".getBytes());
                    return;
                }

                toSendQueue.poll();
                //Record the expected packet transfer
                transferSendCount = Integer.getInteger(new String(qo.getData()));
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_SUCCESS,
                        0,
                        qo.getData());
                return;
            }
            if(canRead) {
                for (UUID transferDesc : DataTransferProfile.TRANSFER_DESC_LIST) {
                    if (transferDesc.equals(descriptor.getUuid())) {
                        if (sending) {
                            QueueObject qo = toSendQueue.peek();
                            if(qo == null){
                                mBluetoothGattServer.sendResponse(device,
                                        requestId,
                                        BluetoothGatt.GATT_FAILURE,
                                        0,
                                        "No Update".getBytes());
                                sending = false;
                                return;
                            }else if(qo.getUUID() != descriptor.getUuid()){
                                mBluetoothGattServer.sendResponse(device,
                                        requestId,
                                        BluetoothGatt.GATT_FAILURE,
                                        0,
                                        "Wrong UUID Request".getBytes());
                                return;
                            }

                            toSendQueue.poll();
                            //Update the count before sending
                            sendCount++;
                            mBluetoothGattServer.sendResponse(device,
                                    requestId,
                                    BluetoothGatt.GATT_SUCCESS,
                                    0,
                                    qo.getData());
                            if(transferSendCount == sendCount){
                                transferSendCount = 0;
                                sendCount = 0;
                                sending = false;
                                canRead = false; //It now excepts a response
                            }
                            return;
                        } else {
                            Log.w(TAG, "Not Sending");
                            mBluetoothGattServer.sendResponse(device,
                                    requestId,
                                    BluetoothGatt.GATT_FAILURE,
                                    0,
                                    "Not Sending".getBytes());
                            return;
                        }
                    }

                }
            }else{
                Log.w(TAG, "Waiting for Updated State from Client");
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        "Waiting for Update".getBytes());
                return;
            }

            Log.w(TAG, "Unknown descriptor read request");
            mBluetoothGattServer.sendResponse(device,
                    requestId,
                    BluetoothGatt.GATT_FAILURE,
                    0,
                    "Unknown descriptor".getBytes());
        }

        @Override
        public void onDescriptorWriteRequest(BluetoothDevice device, int requestId,
                                             BluetoothGattDescriptor descriptor,
                                             boolean preparedWrite, boolean responseNeeded,
                                             int offset, byte[] value) {
            //TODO: NEEDs to make sure hasWrite is not happening
            Log.i(TAG, "Descriptor Write Request");
            Log.i(TAG, new String(value) + "");
            if(DataTransferProfile.TRANSFER_DESC01.equals(descriptor.getUuid())){
                //TODO: Decide on how each device is determined, maybe by different Characteristics
                try {
                    transferCount = Integer.getInteger(new String(value));
                    Log.i(TAG, "There is " + transferCount + "Descriptors Edited");
                    while(transferString != null) {
                        Thread.yield();
                    }
                    transferString = new char[transferCount*20];
                    //descriptor.setValue("".getBytes());
                }catch(Exception e){
                    Log.i(TAG, "Descriptor 1 did not transfer a number");
                }
            }
            for (UUID transferDesc : DataTransferProfile.TRANSFER_DESC_LIST) {
                if (transferDesc.equals(descriptor.getUuid())) {
                    int location = -1;
                    try {
                        location = Integer.getInteger(descriptor.getUuid().toString().substring(4, 8))-2;
                    }catch(Exception e){
                        Log.e(TAG, descriptor.getUuid() + "Contains a non numeric value in indices 4-8.");
                    }

                    String val = new String(value);
                    Log.i(TAG, val);
                    synchronized(this) {

                        for (int i = 0; i < val.length(); i++) {
                            transferString[i + 20 * location] = val.charAt(i);
                        }
                        writeCount++;
                        if(transferCount > 1 && writeCount == transferCount){
                            writeCount = 0;
                            transferCount = 0;
                            ByteArrayInputStream bis = new ByteArrayInputStream(transferString.toString().getBytes());
                            ObjectInput in = null;
                            try {
                                in = new ObjectInputStream(bis);
                                receievedObject = in.readObject();
                                hasWrite = true;
                                canRead = true;
                            } catch(IOException ioe) {
                                Log.e(TAG, ioe.getMessage());
                            }catch (ClassNotFoundException cnfe) {
                                Log.e(TAG, cnfe.getMessage());
                            }finally{
                                try {
                                    if (in != null) {
                                        in.close();
                                    }
                                } catch (IOException ioe) {
                                    // ignore close exception
                                }
                            }
                        }
                    }

                    if (responseNeeded) {
                        mBluetoothGattServer.sendResponse(device,
                                requestId,
                                BluetoothGatt.GATT_SUCCESS,
                                0,
                                null);
                    }
                    return;
                }
            }

            Log.w(TAG, "Unknown descriptor write request");
            if (responseNeeded) {
                mBluetoothGattServer.sendResponse(device,
                        requestId,
                        BluetoothGatt.GATT_FAILURE,
                        0,
                        null);
            }
        }
    };

    public Object getReceivedObject(){
        while(!hasWrite){
            Thread.yield();
        }
        hasWrite = false;
        Object retObj = receievedObject;
        receievedObject = null;
        transferString = null;
        return retObj;
    }

    public boolean addToSendingQueue(QueueObject queueObject){
        toSendQueue.add(queueObject);
        return true;
    }
    //endregion Bluetooth
}