package edu.up.cs301.game.util;

public interface Constants {

        // Message types sent from the BluetoothGameService Handler
        public static final int MESSAGE_STATE_CHANGE = 1;
        public static final int MESSAGE_READ = 2;
        public static final int MESSAGE_WRITE = 3;
        public static final int MESSAGE_DEVICE_NAME = 4;
        public static final int MESSAGE_TOAST = 5;
        // Key names received from the BluetoothGameService Handler
        public static final String DEVICE_NAME = "device_name";
        public static final String TOAST = "toast";
}
