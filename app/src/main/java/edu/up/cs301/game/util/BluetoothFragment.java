package edu.up.cs301.game.util;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanRecord;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.ParcelUuid;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import edu.up.cs301.game.R;

import static edu.up.cs301.game.util.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static edu.up.cs301.game.util.BluetoothLeService.ACTION_GATT_CONNECTED;
import static edu.up.cs301.game.util.BluetoothLeService.ACTION_GATT_DISCONNECTED;
import static edu.up.cs301.game.util.BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED;

public class BluetoothFragment extends ListFragment {

    //Bluetooth API
    private BluetoothFragment.LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;


    //TODO: Delete unused
    private boolean mScanning;

    private static final int REQUEST_ENABLE_BT = 3;

    /**
     * Member object for the Game services
     */
    public BluetoothGameService mGameService = null;


    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    //Connected Stuff
    //private TextView mConnectionState;
    HashMap<String, ViewHolder> viewHolderList = new HashMap<>();
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;
    private boolean mConnected = false;
    private Handler mHandler;
    //public RFCOMMServer rfcommServer;
    //UUID SERVICE_UUID = new UUID((0x0000000000001000L | ((0x180D & 0xFFFFFFFF) << 32)),0x800000805f9b34fbL);
    //private final String LIST_NAME = "NAME";
    //private final String LIST_UUID = "UUID";

    //A Tag for logging
    private static final String TAG = "BluetoothFragment";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bluetooth_layout, container, false);
    }

    //Uses onActivityCreated instead of onCreate the ensure GameMainActivity is created first
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        Log.i(TAG,"Activity Created");
        super.onActivityCreated(savedInstanceState);
        //setListAdapter(mLeDeviceListAdapter);
        //getActionBar().setTitle(R.string.title_devices);
        mHandler = new Handler();

        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        getActivity().registerReceiver(mReceiver, filter);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // Checks if Bluetooth is supported on the device.
        if (mBluetoothAdapter == null) {
            Toast.makeText(getActivity(), R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
            return;
        }

        Log.i("Bluetooth Fragment","Adding Adapter");
        mLeDeviceListAdapter = new BluetoothFragment.LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mGameService == null) {
            setupGame();
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        /*if(mConnected){
            //Can send 20 bytes over Bluetooth
            byte[] data = (new String("01")).getBytes();//TimeProfile.getLocalTimeInfo(System.currentTimeMillis());//(new String("H")).getBytes();//
            Log.i("Bluetooth Fragment", new String(data)+"");
            mBluetoothLeService.writeCustomCharacteristic(data, DataTransferProfile.TRANSFER_DESC01);
            return;
        }*/

        final BluetoothDevice device = mLeDeviceListAdapter.getDevice(position);
        if (device == null) return;
        //final Intent intent = new Intent(getActivity(), DeviceControlActivity.class);
        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_NAME, device.getName());
        //intent.putExtra(DeviceControlActivity.EXTRAS_DEVICE_ADDRESS, device.getAddress());
        if (mScanning) {
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
            mScanning = false;
        }
        if(!mConnected) {
            //TODO: Make this RFCOMM
            Log.i(TAG, "starting");
            while(mGameService == null) {
                Log.i(TAG, "Game Service is Null");
                Thread.yield();
            }
            while(mGameService.getState() != mGameService.STATE_LISTEN){
                Thread.yield();
            }
            Log.i(TAG, "Connecting");
            mGameService.connect(device, true);
            while(mGameService.getState() != mGameService.STATE_CONNECTED){
                Thread.yield();
            }
            Log.i(TAG, "Connected");
            //BluetoothService bs = new BluetoothService(connectThread.mmSocket);
            //Log.i(TAG, "Sending Hello Message");
            //bs.write("Hello Bluetooth".getBytes());
            //Log.i(TAG, "Done");

            //This is where GATT connection starts
            //startActivity(intent);
            //Intent gattServiceIntent = new Intent(this.getActivity(), BluetoothLeService.class);
            //mDeviceAddress = device.getAddress(); //might be able to remove
            //getActivity().bindService(gattServiceIntent, mServiceConnection, this.getActivity().BIND_AUTO_CREATE);
            //getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
    }

    private void setupGame(){
        Log.i(TAG, "Setting Up Game Service");

        mGameService = new BluetoothGameService(getActivity(), bluetoothHandler);

    }

    /**
     * Makes this device discoverable for 300 seconds (5 minutes).
     */
    public void ensureDiscoverable() {
        //BluetoothAdapter.getDefaultAdapter().setName("ClueGameHost");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mGameService == null) {
            setupGame();
        }

        mGameService.start();
    }

    private void updateConnectionState(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            //@Override
            public void run() {
                ViewHolder vh = viewHolderList.get(mDeviceAddress);
                vh.connectionStatus.setText(resourceId);
            }
        });
    }

    public void scan(){
        if (mBluetoothAdapter.isDiscovering()) {
            mBluetoothAdapter.cancelDiscovery();
        }

        mLeDeviceListAdapter.clear();

        // Stops scanning after a pre-defined scan period.
        mHandler.postDelayed(new Runnable() {
            //@Override
            public void run() {
                mScanning = false;
                mBluetoothAdapter.cancelDiscovery();
                //invalidateOptionsMenu();
            }
        }, SCAN_PERIOD);

        mScanning = true;
        mBluetoothAdapter.startDiscovery();
    }

    public void scanLeDevice(final boolean enable) {
        if (enable) {
            mLeDeviceListAdapter.clear();
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                //@Override
                public void run() {
                    mScanning = false;
                    mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
                    //invalidateOptionsMenu();
                }
            }, SCAN_PERIOD);

            mScanning = true;
            mBluetoothAdapter.getBluetoothLeScanner().startScan(mLeScanCallback);
        } else {
            mScanning = false;
            mBluetoothAdapter.getBluetoothLeScanner().stopScan(mLeScanCallback);
        }
        //invalidateOptionsMenu();
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private ArrayList<BluetoothDevice> mLeDevices;
        private LayoutInflater mInflator;

        public LeDeviceListAdapter() {
            super();
            mLeDevices = new ArrayList<BluetoothDevice>();
            mInflator = BluetoothFragment.this.getActivity().getLayoutInflater();
        }

        public void addDevice(BluetoothDevice device) {
            if (!mLeDevices.contains(device)) {
                mLeDevices.add(device);
            }
        }

        public BluetoothDevice getDevice(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        //@Override
        public int getCount() {
            return mLeDevices.size();
        }

        //@Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        //@Override
        public long getItemId(int i) {
            return i;
        }

        //@Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            BluetoothFragment.ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = mInflator.inflate(R.layout.bluetooth_layout, null);
                viewHolder = new BluetoothFragment.ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.device_address);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.device_name);
                viewHolder.connectionStatus = (TextView) view.findViewById(R.id.connection_state);
                view.setTag(viewHolder);
            } else {
                viewHolder = (BluetoothFragment.ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i);
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0) {
                viewHolder.deviceName.setText(deviceName);
            }
            else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            viewHolder.connectionStatus.setText("Disconnected");
            viewHolderList.put(device.getAddress(), viewHolder);
            return view;
        }
    }

    /**
     * The BroadcastReceiver that listens for discovered devices and changes the title when
     * discovery is finished
     */
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Log.i(TAG, device.getAddress()); //B0:6E:BF:19:b8:af
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    //TODO: Game Name
                    if (device.getName() != null && device.getName().contains("Tablet")) {
                        //Log.i("Device Found", new String(scanRecord));
                        mLeDeviceListAdapter.addDevice(device);
                        mLeDeviceListAdapter.notifyDataSetChanged();
                    }
                    //mNewDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                }
                // When discovery is finished, change the Activity title
            }
        }
    };

    // Device scan callback.
    private ScanCallback mLeScanCallback =
            new ScanCallback() {

                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    final ScanResult res = result;
                    getActivity().runOnUiThread(new Runnable() {
                        BluetoothDevice device = res.getDevice();
                        //@Override
                        public void run() {
                            ScanRecord sr = res.getScanRecord();
                            if (device.getName() != null && sr != null
                                        && new String(sr.getBytes()).contains(getResources().getString(R.string.app_name))) {
                                //Log.i("Device Found", new String(scanRecord));
                                mLeDeviceListAdapter.addDevice(device);
                                mLeDeviceListAdapter.notifyDataSetChanged();
                            }
                        }
                    });
                }
            };

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView connectionStatus;
    }

    public BluetoothLeService getBluetoothLeService(){
        return mBluetoothLeService;
    }

    /**
     * The Handler that gets information back from the BluetoothGameService
     */
    public final Handler bluetoothHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothGameService.STATE_CONNECTED:
                            updateConnectionState(R.string.connected);
                            //mConversationArrayAdapter.clear();
                            break;
                        case BluetoothGameService.STATE_CONNECTING:
                            updateConnectionState(R.string.connecting);
                            break;
                        case BluetoothGameService.STATE_LISTEN:
                        case BluetoothGameService.STATE_NONE:
                            updateConnectionState(R.string.disconnected);
                            break;
                    }
                    break;
                case Constants.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    //mConversationArrayAdapter.add("Me:  " + writeMessage);
                    break;
                case Constants.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);
                    //mConversationArrayAdapter.add(mConnectedDeviceName + ":  " + readMessage);
                    break;
                case Constants.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    //mConnectedDeviceName = msg.getData().getString(Constants.DEVICE_NAME);
                    //if (null != activity) {
                    //    Toast.makeText(activity, "Connected to "
                    //            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    //}
                    break;
                case Constants.MESSAGE_TOAST:
                    //if (null != activity) {
                    //    Toast.makeText(activity, msg.getData().getString(Constants.TOAST),
                    //            Toast.LENGTH_SHORT).show();
                    //}
                    break;
            }
        }
    };
}