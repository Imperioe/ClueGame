package edu.up.cs301.game.util;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;

import java.util.UUID;

public class DataTransferProfile {
    private static final String TAG = DataTransferProfile.class.getSimpleName();

    /* Data Transfer Service UUID */
    public static UUID TRANSFER_SERVICE = UUID.fromString("00009000-0000-1000-8000-00805f9b34fb");
    /* Data Transfer Characteristic */
    public static UUID TRANSFER_CHAR    = UUID.fromString("00009001-0000-1000-8000-00805f9b34fb");
    /* Data Transfer Descriptors */
    //region TRANSFER DESC UUID 01-64
    public static UUID TRANSFER_DESC01 = UUID.fromString("00000001-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC02 = UUID.fromString("00000002-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC03 = UUID.fromString("00000003-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC04 = UUID.fromString("00000004-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC05 = UUID.fromString("00000005-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC06 = UUID.fromString("00000006-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC07 = UUID.fromString("00000007-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC08 = UUID.fromString("00000008-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC09 = UUID.fromString("00000009-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC10 = UUID.fromString("00000010-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC11 = UUID.fromString("00000011-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC12 = UUID.fromString("00000012-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC13 = UUID.fromString("00000013-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC14 = UUID.fromString("00000014-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC15 = UUID.fromString("00000015-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC16 = UUID.fromString("00000016-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC17 = UUID.fromString("00000017-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC18 = UUID.fromString("00000018-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC19 = UUID.fromString("00000019-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC20 = UUID.fromString("00000020-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC21 = UUID.fromString("00000021-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC22 = UUID.fromString("00000022-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC23 = UUID.fromString("00000023-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC24 = UUID.fromString("00000024-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC25 = UUID.fromString("00000025-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC26 = UUID.fromString("00000026-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC27 = UUID.fromString("00000027-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC28 = UUID.fromString("00000028-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC29 = UUID.fromString("00000029-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC30 = UUID.fromString("00000030-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC31 = UUID.fromString("00000031-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC32 = UUID.fromString("00000032-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC33 = UUID.fromString("00000033-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC34 = UUID.fromString("00000034-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC35 = UUID.fromString("00000035-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC36 = UUID.fromString("00000036-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC37 = UUID.fromString("00000037-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC38 = UUID.fromString("00000038-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC39 = UUID.fromString("00000039-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC40 = UUID.fromString("00000040-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC41 = UUID.fromString("00000041-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC42 = UUID.fromString("00000042-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC43 = UUID.fromString("00000043-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC44 = UUID.fromString("00000044-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC45 = UUID.fromString("00000045-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC46 = UUID.fromString("00000046-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC47 = UUID.fromString("00000047-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC48 = UUID.fromString("00000048-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC49 = UUID.fromString("00000049-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC50 = UUID.fromString("00000050-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC51 = UUID.fromString("00000051-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC52 = UUID.fromString("00000052-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC53 = UUID.fromString("00000053-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC54 = UUID.fromString("00000054-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC55 = UUID.fromString("00000055-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC56 = UUID.fromString("00000056-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC57 = UUID.fromString("00000057-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC58 = UUID.fromString("00000058-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC59 = UUID.fromString("00000059-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC60 = UUID.fromString("00000060-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC61 = UUID.fromString("00000061-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC62 = UUID.fromString("00000062-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC63 = UUID.fromString("00000063-0000-1000-8000-00805f9b34fb");
    public static UUID TRANSFER_DESC64 = UUID.fromString("00000064-0000-1000-8000-00805f9b34fb");
    //endregion
    
    /*Data Transfer Descriptors List */
    //Transfer Desc 01 is intentionally left out.
    //region TRANSFER DESC LIST
    public static UUID[]  TRANSFER_DESC_LIST = {
      TRANSFER_DESC02,
      TRANSFER_DESC03,
      TRANSFER_DESC04,
      TRANSFER_DESC05,
      TRANSFER_DESC06,
      TRANSFER_DESC07,
      TRANSFER_DESC08,
      TRANSFER_DESC09,
      TRANSFER_DESC10,
      TRANSFER_DESC11,
      TRANSFER_DESC12,
      TRANSFER_DESC13,
      TRANSFER_DESC14,
      TRANSFER_DESC15,
      TRANSFER_DESC16,
      TRANSFER_DESC17,
      TRANSFER_DESC18,
      TRANSFER_DESC19,
      TRANSFER_DESC20,
      TRANSFER_DESC21,
      TRANSFER_DESC22,
      TRANSFER_DESC23,
      TRANSFER_DESC24,
      TRANSFER_DESC25,
      TRANSFER_DESC26,
      TRANSFER_DESC27,
      TRANSFER_DESC28,
      TRANSFER_DESC29,
      TRANSFER_DESC30,
      TRANSFER_DESC31,
      TRANSFER_DESC32,
      TRANSFER_DESC33,
      TRANSFER_DESC34,
      TRANSFER_DESC35,
      TRANSFER_DESC36,
      TRANSFER_DESC37,
      TRANSFER_DESC38,
      TRANSFER_DESC39,
      TRANSFER_DESC40,
      TRANSFER_DESC41,
      TRANSFER_DESC42,
      TRANSFER_DESC43,
      TRANSFER_DESC44,
      TRANSFER_DESC45,
      TRANSFER_DESC46,
      TRANSFER_DESC47,
      TRANSFER_DESC48,
      TRANSFER_DESC49,
      TRANSFER_DESC50,
      TRANSFER_DESC51,
      TRANSFER_DESC52,
      TRANSFER_DESC53,
      TRANSFER_DESC54,
      TRANSFER_DESC55,
      TRANSFER_DESC56,
      TRANSFER_DESC57,
      TRANSFER_DESC58,
      TRANSFER_DESC59,
      TRANSFER_DESC60,
      TRANSFER_DESC61,
      TRANSFER_DESC62,
      TRANSFER_DESC63,
      TRANSFER_DESC64};
    //endregion
    
    /**
     * Return a configured {@link BluetoothGattService} instance for the
     * Data Transfer Service.
     */
    public static BluetoothGattService createDataTransferService() {
        BluetoothGattService service = new BluetoothGattService(TRANSFER_SERVICE,
                BluetoothGattService.SERVICE_TYPE_PRIMARY);

        // Current Time characteristic
        BluetoothGattCharacteristic characteristic = new BluetoothGattCharacteristic(TRANSFER_CHAR,
                //Read-only characteristic, supports notifications
                BluetoothGattCharacteristic.PROPERTY_READ | BluetoothGattCharacteristic.PROPERTY_NOTIFY,
                BluetoothGattCharacteristic.PERMISSION_READ);
        //region Creating Transfer desc 01-64
        BluetoothGattDescriptor transferDescriptor01 = new BluetoothGattDescriptor(TRANSFER_DESC01,
                //Read/write descriptor
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor02 = new BluetoothGattDescriptor(TRANSFER_DESC02,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor03 = new BluetoothGattDescriptor(TRANSFER_DESC03,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor04 = new BluetoothGattDescriptor(TRANSFER_DESC04,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor05 = new BluetoothGattDescriptor(TRANSFER_DESC05,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor06 = new BluetoothGattDescriptor(TRANSFER_DESC06,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor07 = new BluetoothGattDescriptor(TRANSFER_DESC07,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor08 = new BluetoothGattDescriptor(TRANSFER_DESC08,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor09 = new BluetoothGattDescriptor(TRANSFER_DESC09,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor10 = new BluetoothGattDescriptor(TRANSFER_DESC10,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor11 = new BluetoothGattDescriptor(TRANSFER_DESC11,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor12 = new BluetoothGattDescriptor(TRANSFER_DESC12,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor13 = new BluetoothGattDescriptor(TRANSFER_DESC13,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor14 = new BluetoothGattDescriptor(TRANSFER_DESC14,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor15 = new BluetoothGattDescriptor(TRANSFER_DESC15,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor16 = new BluetoothGattDescriptor(TRANSFER_DESC16,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor17 = new BluetoothGattDescriptor(TRANSFER_DESC17,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor18 = new BluetoothGattDescriptor(TRANSFER_DESC18,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor19 = new BluetoothGattDescriptor(TRANSFER_DESC19,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor20 = new BluetoothGattDescriptor(TRANSFER_DESC20,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor21 = new BluetoothGattDescriptor(TRANSFER_DESC21,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor22 = new BluetoothGattDescriptor(TRANSFER_DESC22,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor23 = new BluetoothGattDescriptor(TRANSFER_DESC23,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor24 = new BluetoothGattDescriptor(TRANSFER_DESC24,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor25 = new BluetoothGattDescriptor(TRANSFER_DESC25,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor26 = new BluetoothGattDescriptor(TRANSFER_DESC26,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor27 = new BluetoothGattDescriptor(TRANSFER_DESC27,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor28 = new BluetoothGattDescriptor(TRANSFER_DESC28,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor29 = new BluetoothGattDescriptor(TRANSFER_DESC29,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor30 = new BluetoothGattDescriptor(TRANSFER_DESC30,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor31 = new BluetoothGattDescriptor(TRANSFER_DESC31,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor32 = new BluetoothGattDescriptor(TRANSFER_DESC32,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor33 = new BluetoothGattDescriptor(TRANSFER_DESC33,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor34 = new BluetoothGattDescriptor(TRANSFER_DESC34,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor35 = new BluetoothGattDescriptor(TRANSFER_DESC35,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor36 = new BluetoothGattDescriptor(TRANSFER_DESC36,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor37 = new BluetoothGattDescriptor(TRANSFER_DESC37,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor38 = new BluetoothGattDescriptor(TRANSFER_DESC38,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor39 = new BluetoothGattDescriptor(TRANSFER_DESC39,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor40 = new BluetoothGattDescriptor(TRANSFER_DESC40,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor41 = new BluetoothGattDescriptor(TRANSFER_DESC41,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor42 = new BluetoothGattDescriptor(TRANSFER_DESC42,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor43 = new BluetoothGattDescriptor(TRANSFER_DESC43,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor44 = new BluetoothGattDescriptor(TRANSFER_DESC44,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor45 = new BluetoothGattDescriptor(TRANSFER_DESC45,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor46 = new BluetoothGattDescriptor(TRANSFER_DESC46,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor47 = new BluetoothGattDescriptor(TRANSFER_DESC47,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor48 = new BluetoothGattDescriptor(TRANSFER_DESC48,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor49 = new BluetoothGattDescriptor(TRANSFER_DESC49,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor50 = new BluetoothGattDescriptor(TRANSFER_DESC50,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor51 = new BluetoothGattDescriptor(TRANSFER_DESC51,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor52 = new BluetoothGattDescriptor(TRANSFER_DESC52,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor53 = new BluetoothGattDescriptor(TRANSFER_DESC53,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor54 = new BluetoothGattDescriptor(TRANSFER_DESC54,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor55 = new BluetoothGattDescriptor(TRANSFER_DESC55,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor56 = new BluetoothGattDescriptor(TRANSFER_DESC56,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor57 = new BluetoothGattDescriptor(TRANSFER_DESC57,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor58 = new BluetoothGattDescriptor(TRANSFER_DESC58,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor59 = new BluetoothGattDescriptor(TRANSFER_DESC59,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor60 = new BluetoothGattDescriptor(TRANSFER_DESC60,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor61 = new BluetoothGattDescriptor(TRANSFER_DESC61,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor62 = new BluetoothGattDescriptor(TRANSFER_DESC62,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor63 = new BluetoothGattDescriptor(TRANSFER_DESC63,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        BluetoothGattDescriptor transferDescriptor64 = new BluetoothGattDescriptor(TRANSFER_DESC64,
                BluetoothGattDescriptor.PERMISSION_READ | BluetoothGattDescriptor.PERMISSION_WRITE);
        //endregion

        //region Adding Transfer Descriptors 01-64 to characteristics
        characteristic.addDescriptor(transferDescriptor01);
        characteristic.addDescriptor(transferDescriptor02);
        characteristic.addDescriptor(transferDescriptor03);
        characteristic.addDescriptor(transferDescriptor04);
        characteristic.addDescriptor(transferDescriptor05);
        characteristic.addDescriptor(transferDescriptor06);
        characteristic.addDescriptor(transferDescriptor07);
        characteristic.addDescriptor(transferDescriptor08);
        characteristic.addDescriptor(transferDescriptor09);
        characteristic.addDescriptor(transferDescriptor10);
        characteristic.addDescriptor(transferDescriptor11);
        characteristic.addDescriptor(transferDescriptor12);
        characteristic.addDescriptor(transferDescriptor13);
        characteristic.addDescriptor(transferDescriptor14);
        characteristic.addDescriptor(transferDescriptor15);
        characteristic.addDescriptor(transferDescriptor16);
        characteristic.addDescriptor(transferDescriptor17);
        characteristic.addDescriptor(transferDescriptor18);
        characteristic.addDescriptor(transferDescriptor19);
        characteristic.addDescriptor(transferDescriptor20);
        characteristic.addDescriptor(transferDescriptor21);
        characteristic.addDescriptor(transferDescriptor22);
        characteristic.addDescriptor(transferDescriptor23);
        characteristic.addDescriptor(transferDescriptor24);
        characteristic.addDescriptor(transferDescriptor25);
        characteristic.addDescriptor(transferDescriptor26);
        characteristic.addDescriptor(transferDescriptor27);
        characteristic.addDescriptor(transferDescriptor28);
        characteristic.addDescriptor(transferDescriptor29);
        characteristic.addDescriptor(transferDescriptor30);
        characteristic.addDescriptor(transferDescriptor31);
        characteristic.addDescriptor(transferDescriptor32);
        characteristic.addDescriptor(transferDescriptor33);
        characteristic.addDescriptor(transferDescriptor34);
        characteristic.addDescriptor(transferDescriptor35);
        characteristic.addDescriptor(transferDescriptor36);
        characteristic.addDescriptor(transferDescriptor37);
        characteristic.addDescriptor(transferDescriptor38);
        characteristic.addDescriptor(transferDescriptor39);
        characteristic.addDescriptor(transferDescriptor40);
        characteristic.addDescriptor(transferDescriptor41);
        characteristic.addDescriptor(transferDescriptor42);
        characteristic.addDescriptor(transferDescriptor43);
        characteristic.addDescriptor(transferDescriptor44);
        characteristic.addDescriptor(transferDescriptor45);
        characteristic.addDescriptor(transferDescriptor46);
        characteristic.addDescriptor(transferDescriptor47);
        characteristic.addDescriptor(transferDescriptor48);
        characteristic.addDescriptor(transferDescriptor49);
        characteristic.addDescriptor(transferDescriptor50);
        characteristic.addDescriptor(transferDescriptor51);
        characteristic.addDescriptor(transferDescriptor52);
        characteristic.addDescriptor(transferDescriptor53);
        characteristic.addDescriptor(transferDescriptor54);
        characteristic.addDescriptor(transferDescriptor55);
        characteristic.addDescriptor(transferDescriptor56);
        characteristic.addDescriptor(transferDescriptor57);
        characteristic.addDescriptor(transferDescriptor58);
        characteristic.addDescriptor(transferDescriptor59);
        characteristic.addDescriptor(transferDescriptor60);
        characteristic.addDescriptor(transferDescriptor61);
        characteristic.addDescriptor(transferDescriptor62);
        characteristic.addDescriptor(transferDescriptor63);
        characteristic.addDescriptor(transferDescriptor64);
        //endregion

        service.addCharacteristic(characteristic);

        return service;
    }

}
