package edu.up.cs301.game.util;

import java.util.UUID;

//Created by: Eric Imperio
//This class is an Object is designed to be added to the Gatt Server Queue in order for the Host to send the Clients Game info.
public class QueueObject {
    private UUID myUUID;
    private byte[] data;

    public QueueObject(byte[] bytes,UUID uuid){
        myUUID = uuid;
        data = bytes;
    }

    public UUID getUUID(){
        return myUUID;
    }

    public byte[] getData(){
        return data;
    }
}
