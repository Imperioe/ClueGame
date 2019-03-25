package edu.up.cs301.game.util;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

public abstract class BluetoothPasser {
    private static String TAG = "BluetoothPasser";

    // the streams for Serializing objects
    //reading and writing objects from/to the network
    private ObjectOutputStream out = null;
    ByteArrayOutputStream bos = null;

    // the handler the objects "sending thread"
    private Handler sendHandler;

    private BluetoothLeService bluetoothLeService;
    private BluetoothSocket bluetoothSocket;
    private boolean imHost;

    // a queue for collecting objects that "sent" to this object
    // before the network connection is established
    private Queue<Object> objQueue = new LinkedList<Object>();


    public BluetoothPasser(BluetoothSocket bluetoothSocket, BluetoothLeService bles, boolean host){
        this.bluetoothSocket = bluetoothSocket;
        bluetoothLeService = bles;
        imHost = host;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            out = new ObjectOutputStream(bos);
            out.flush();
        }catch(IOException ioe){
            Log.e(TAG, ioe.getMessage());
            return;
        }
        // Create thread that waits for the connection to be
        // established from the network. This will ultimately become
        // the thread that is reading objects from the network.
        BluetoothPasser.CreateRunner createRunner = new BluetoothPasser.CreateRunner();
        Thread thread2 = new Thread(createRunner);
        thread2.setName("Network receive-handler");
        thread2.start();

        // Loop, waiting for the status (from the just-created thread)
        // to not be "waiting". This will occur when the connection
        // is established.
        while (createRunner.getStatus() == BluetoothPasser.RunnerStatus.WAITING) {
            Thread.yield();
        }

        // create/run a thread and handler for sending objects
        // via the network
        Runnable runnable = new Runnable() {
            public void run() {
                Looper.prepare();
                sendHandler = new Handler();
                Looper.loop();
            }
        };
        Thread thread = new Thread(runnable);
        thread.setName("Network send-handler");
        thread.start();

        // wait for sendHandler to be set
        while (sendHandler == null) {
            Thread.yield();
        }
    }

    public void stop(){
        try {
            bos.close();
        }catch(IOException ioe){
            //TODO: Handle
            return;
        }
    }

    public void sendObject(Object obj) {
        // schedule the "send" in the object's "sending" thread
        Runnable run = new BluetoothPasser.MsgRunnable(obj);
        sendHandler.post(run);
    }

    public void writeObject(Object obj)throws IOException{
        out.writeObject(obj);
        out.flush();
        while(bos == null){
            Thread.yield();
        }
        byte[] serialized = bos.toByteArray();
        int packets = serialized.length/20;
        if(imHost){
            while(bluetoothSocket == null){
                Log.i(TAG, "Gatt Server is null");
                Thread.yield();
            }
            Log.i(TAG, "Writing Bluetooth Packets as Host");
            //gattServer.addToSendingQueue(new QueueObject((packets+"").getBytes(),DataTransferProfile.TRANSFER_DESC01));
            for(int i = 0;i<packets;i++) {
                int start = i * 20;
                int stop = (i * 20) + 20;
                if (stop > serialized.length) {
                    stop = serialized.length;
                }

                //gattServer.addToSendingQueue(new QueueObject(Arrays.copyOfRange(serialized, start, stop), DataTransferProfile.TRANSFER_DESC_LIST[i]));
            }
            Log.i(TAG, "Finished Writing Bluetooth Packets as Host");
            return;
        }

        while(bluetoothLeService == null){
            Thread.yield();
        }
        bluetoothLeService.writeCustomCharacteristic((new String(packets+"")).getBytes(),DataTransferProfile.TRANSFER_DESC01);
        for(int i = 0;i<packets;i++) {
            int start = i * 20;
            int stop = (i * 20) + 20;
            if (stop > serialized.length) {
                stop = serialized.length;
            }

            bluetoothLeService.writeCustomCharacteristic(Arrays.copyOfRange(serialized, start, stop), DataTransferProfile.TRANSFER_DESC_LIST[i]);
        }
    }

    public abstract void onReceiveObject(Object obj);

    private enum RunnerStatus {
        WAITING, READY, FAILED;
    }

    private class CreateRunner implements Runnable {
        private String TAG = "CreateRunner";
        private String ipAddress; // the ipAddress of server (or null if we are server)
        private int port; // the port
        private RunnerStatus status;

        // constructor
        public CreateRunner() {
            status = RunnerStatus.WAITING;
        }

        public RunnerStatus getStatus() {
            return status;
        }

        // run-method, which runs in the separate thread
        public void run() {
            Log.i(TAG, "starting run method at bottom"); //TODO: This is vague

            // create the input and output streams; also send already queued objects
            synchronized (this) {
                status = RunnerStatus.READY;
                try {
                    out.flush();
                }
                catch (IOException e) {
                    // if exception, return
                    status = RunnerStatus.FAILED;
                    return;
                }

                // send out all queued-up objects
                while (!objQueue.isEmpty()) {
                    Object obj = objQueue.remove();
                    try {
                        writeObject(obj);
                    } catch (IOException e) {
                        Log.e(TAG, "could not write object");
                    }
                }
            }

            // go into our read-object loop, passing the object to our user by
            // invoking the user's 'onReceiveObject' method on each object
            for (;;) {
                try {
                    Log.i(TAG, "ready to read object");
                    Object obj;
                    if(imHost) {
                        //obj = gattServer.getReceivedObject();
                    }else{
                        String data = "";
                        String size = bluetoothLeService.readCustomCharacteristic(DataTransferProfile.TRANSFER_DESC01);
                        if(size == null){
                            continue;
                        }

                        for(int i = 0; i < Integer.getInteger(size); i++){
                            String result = bluetoothLeService.readCustomCharacteristic(DataTransferProfile.TRANSFER_DESC_LIST[i]);
                            if(result == null){
                                Log.e(TAG, "Something is wrong with the Bluetooth data");
                            }
                            data+=result;
                        }

                        obj = data;
                    }
                    //Log.i(TAG, "object read ("+obj.getClass()+")");
                    //onReceiveObject(obj);
                }
                catch (Exception x) {
                    break;
                }
            }
        }
    }

    /**
     * helper class for sending objects using a dedicated thread
     * @author Steven R. Vegdahl
     * @version July 2013
     *
     */
    private class MsgRunnable implements Runnable {
        private String TAG = "MsgRunnable";

        // the object we're going to send
        private Object obj;

        // constructor
        public MsgRunnable(Object obj) {
            this.obj = obj;
        }

        // run method, which writes out the object or, if unsuccessful,
        // queues the object up for sending later
        public void run() {
            synchronized(this) {
                boolean success = false;
                if (out != null) {
                    try {
                        // write object
                        writeObject(obj);
                        success = true;
                    } catch (IOException e) {
                        Log.e(TAG, "could not write object");
                    }
                }
                if (!success) {
                    // could not write object, so queue it up
                    objQueue.add(obj);
                }
            }
        }
    }
}