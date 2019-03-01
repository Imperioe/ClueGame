package edu.up.cs301.game.util;

import android.app.ListFragment;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import edu.up.cs301.game.R;

import static edu.up.cs301.game.util.BluetoothLeService.ACTION_DATA_AVAILABLE;
import static edu.up.cs301.game.util.BluetoothLeService.ACTION_GATT_CONNECTED;
import static edu.up.cs301.game.util.BluetoothLeService.ACTION_GATT_DISCONNECTED;
import static edu.up.cs301.game.util.BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED;

public class BluetoothFragment extends ListFragment {

    //Bluetooth API
    private BluetoothFragment.LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mScanning;
    private Handler mHandler;

    //private static final int REQUEST_ENABLE_BT = 1;

    // Stops scanning after 10 seconds.
    private static final long SCAN_PERIOD = 10000;

    //Connected Stuff
    //private TextView mConnectionState;
    HashMap<String, ViewHolder> viewHolderList = new HashMap<>();
    private BluetoothLeService mBluetoothLeService;
    private String mDeviceAddress;
    private boolean mConnected = false;
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

        /*TODO: If still works remove
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(getActivity(), R.string.ble_not_supported, Toast.LENGTH_SHORT).show();
            getActivity().finish();
        }
        */

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        if(bluetoothManager != null) {
            mBluetoothAdapter = bluetoothManager.getAdapter();
        }

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

    //region Commented Out
    /*public boolean onCreateOptionsMenu(Menu menu) {
        getActivity().getMenuInflater().inflate(R.menu.bluetooth_main, menu);
        if (!mScanning) {
            menu.findItem(R.id.menu_stop).setVisible(false);
            menu.findItem(R.id.menu_scan).setVisible(true);
            menu.findItem(R.id.menu_refresh).setActionView(null);
        } else {
            menu.findItem(R.id.menu_stop).setVisible(true);
            menu.findItem(R.id.menu_scan).setVisible(false);
            menu.findItem(R.id.menu_refresh).setActionView(
                    R.layout.actionbar_indeterminate_progress);
        }
        return true;
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_scan:
                mLeDeviceListAdapter.clear();
                scanLeDevice(true);
                break;
            case R.id.menu_stop:
                scanLeDevice(false);
                break;
        }
        return true;
    }*/

    /*@Override
    public void onResume() {
        super.onResume();

        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            }
        }

        // Initializes list view adapter.
        mLeDeviceListAdapter = new BluetoothFragment.LeDeviceListAdapter();
        setListAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }*/

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            getActivity().finish();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }*/

    /*@Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
    }*/
    //endregion

    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        //@Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e("", "Unable to initialize Bluetooth");
                getActivity().finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
        }

        //@Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                Log.i(TAG,"Connected to GATT");
                updateConnectionState(R.string.connected);
                //invalidateOptionsMenu();
            } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                Log.i(TAG,"Disconnected from GATT");
                updateConnectionState(R.string.disconnected);
                //invalidateOptionsMenu();
                //clearUI();
            } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                displayGattServices(mBluetoothLeService.getSupportedGattServices());
            } else if (ACTION_DATA_AVAILABLE.equals(action)) {
                Log.i(TAG,intent.getDataString());
                //TODO: May need to add code here
                //displayData(intent.getStringExtra(BluetoothLeService.EXTRA_DATA));
            }
        }
    };

    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;
        String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();

        //region Hidden
        //Loops through available GATT Services.
        /*for (BluetoothGattService gattService : gattServices) {
            HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, SampleGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();
            // Loops through available Characteristics.
            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, SampleGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                getActivity(),
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        Log.i("Gatt Services", gattServiceAdapter.getChild(0,0).toString());
        //mGattServicesList.setAdapter(gattServiceAdapter);*/
            //endregion

        //Trying to send data across Gatt
        //BluetoothGattService bgs = gattServices.add(new BluetoothGattService(Service_UUID,));

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
            //startActivity(intent);
            Intent gattServiceIntent = new Intent(this.getActivity(), BluetoothLeService.class);
            mDeviceAddress = device.getAddress(); //might be able to remove
            getActivity().bindService(gattServiceIntent, mServiceConnection, this.getActivity().BIND_AUTO_CREATE);
            getActivity().registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        }
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

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
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
                            //TODO: Check for game name match
                            if(device.getName() != null && device.getName().contains("Tablet")) {
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
}
