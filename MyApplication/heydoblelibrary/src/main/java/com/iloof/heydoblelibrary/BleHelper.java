package com.iloof.heydoblelibrary;

import android.annotation.TargetApi;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.scan.ListScanCallback;
import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.app.HdApplication;
import com.iloof.heydoblelibrary.heydoc1.BleCmdSetC1;
import com.iloof.heydoblelibrary.heydoc1.BleFileSendC1;
import com.iloof.heydoblelibrary.heydos1.BleCmdSetS1;
import com.iloof.heydoblelibrary.heydos1.BleFileSendS1;
import com.iloof.heydoblelibrary.heydos1new.BleCmdSetS1S;
import com.iloof.heydoblelibrary.heydos1new.BleFileSendS1S;
import com.iloof.heydoblelibrary.thread.BleUpdateFileThread;
import com.iloof.heydoblelibrary.thread.BleUpdateImgThread;
import com.iloof.heydoblelibrary.thread.ParseThread;
import com.iloof.heydoblelibrary.thread.SendThread;
import com.iloof.heydoblelibrary.util.BleFileSend;
import com.iloof.heydoblelibrary.util.BleMorePicture;

import java.util.List;

import static com.iloof.heydoblelibrary.BleConstant.D;
import static com.iloof.heydoblelibrary.util.BleTransfer.Hex2Deci;

/**
 * Service for managing connection and data communication with a GATT server
 * hosted on a given BLE device.
 *
 * @author UESTC-PRMI-Burial
 * @date 2014-12-11
 */
@TargetApi(19)
public class BleHelper extends Service {

    private final static String TAG = "BluetoothHelper";

    /**
     * High level manager used to obtain an instance of an BluetoothAdapter and
     * to conduct overall BluetoothAdapter Management
     */
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    /**
     * Generic Attribute Profile (GATT)
     */
    private BluetoothGatt mBluetoothGatt;
    /**
     * The special characteristic with UUID_WRITE for write operation
     */
    private BluetoothGattCharacteristic mWriteCharacteristic;
    /**
     * The special characteristic with UUID_READ for read operation
     */
    private BluetoothGattCharacteristic mReadCharacteristic;
    /**
     * What the thread aims to get message, parse message etc is traced back to
     * {@link Thread}
     */
    private ParseThread mParseThread;
    // private BleParseMsgThread mParseMessageThread;
    /**
     * What the thread aims to send command is traced back to {@link Thread}
     */
    private SendThread mSendThread;
    /**
     * What the thread aims to send image is traced back to {@link Thread}
     */
    private BleUpdateImgThread mUpdateImageThread;
    /**
     * 发送文件到水杯线程
     */
    private BleUpdateFileThread mUpdateFileThread;
    /**
     * The SharedPreferences class aims to save some useful information about
     * communication
     */
//    private SharedPreferences mShardPref;
//    /**
//     * Interface used for modifying values in a SharedPreferences
//     * {@link BleHelper#mShardPref}
//     */
//    private Editor mEditor;

    /**
     * What the Handler is traced to BleHandler is an input parameter when you
     * get the instance of BleHelper
     */
    private Handler mHandler;

    /**
     * The MAC address of the remote device's BLE
     */
    private String mBluetoothDeviceAddress;

    /************************** 水杯连接状态相关 **************************************************/
    /**
     * The Connection State
     */
    public ConnectionState mConnectionState = ConnectionState.STATE_NONE;
    /**
     * 干杯请求是否已经处理
     */
    public static boolean isCheersRequestHandled = false;

    public static int styleFlag;

    /**
     * The Connection State Enum
     */
    public enum ConnectionState {
        /**
         * We're doing nothing
         */
        STATE_NONE,
        /**
         * Now initiating an outgoing connection
         */
        STATE_CONNECTING,
        /**
         * Now connected to a remote device
         */
        STATE_CONNECTED,
        /**
         * connect fail
         */
        STATE_FAIL,
    }

    /**
     * 是否已经连接
     */
    public boolean isConnected() {
        return (mConnectionState == ConnectionState.STATE_CONNECTED);
    }

    /**
     * 是否已经准备好收发消息
     */
    public boolean isReady() {
        return (mWriteCharacteristic != null) && (mReadCharacteristic != null);
    }

    public ConnectionState getCurrentConnectionState() {
        return mConnectionState;
    }

    /**
     * 是否蓝牙相关初始化完成
     */
    private boolean isInited = false;

    /**
     * 停止当前文件发送
     */
    public void stopCurrentSend() {
        if (mUpdateFileThread != null) {
            mUpdateFileThread.cancel();
            mUpdateFileThread = null;
        }

    }

//    /**
//     * 停止发送图片
//     *
//     * @param indexOfImg The index of image ready to stop send.if the index equel to
//     *                   -1, we will stop send the current image.
//     */
//    public void stopSendImg(int indexOfImg) {
//        if (mUpdateImageThread != null) {
//            mUpdateImageThread.delImg(indexOfImg);
//        }
//    }

//    /**
//     * 重新发送图片
//     *
//     * @param indexOfImg What the image is sending happen wrong.
//     */
//    public void reSendImg(int indexOfImg) {
//        if (mUpdateImageThread != null) {
//            mUpdateImageThread.setWrongFlag(indexOfImg);
//        }
//    }

    /*************************
     * 后台与界面交互相关
     *******************************/

    public class LocalBinder extends Binder {

        /**
         * Get BleHelper Service instance
         */
        public BleHelper getService() {

            return BleHelper.this;
        }

        /**
         * Get BleHelper Service instance
         *
         * @param handler The Handler extends from BleHandler
         * @return BleHelper Service instance
         * @see BleHelper
         */
        public BleHelper getService(Handler handler) {
            // Use this check to determine whether BLE is supported on the
            // device. Then
            // you can selectively disable BLE-related features.
            if (null == handler) {
                return null;
            }
            if (!getPackageManager().hasSystemFeature(
                    PackageManager.FEATURE_BLUETOOTH_LE)) {
                final Message msg = new Message();
                msg.what = BleConstant.HM_BLE_NONSUPPORT;
                handler.sendMessage(msg);
            }

            if (mParseThread != null) {
                mParseThread.setHandler(handler);
            }

            if (mSendThread != null)
                mSendThread.setHandler(handler);

            mHandler = handler;
            return BleHelper.this;
        }
    }

    private final IBinder mBinder = new LocalBinder();

    /**
     * Initializes a reference to the local BluetoothAdapter.
     *
     * @return Return true if the initialization is successful.
     */
    public boolean initialize() {
        // For API level 18 and above, get a reference to BluetoothAdapter
        // through
        // BluetoothManager.
        Log.i(TAG, "isInited:" + isInited);
        if (!isInited) {
            if (mBluetoothManager == null) {
                mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                if (mBluetoothManager == null) {
                    Log.e(TAG, "Unable to initialize BluetoothManager.");
                    return false;
                }
            }
            // mBluetoothManager.
            mBluetoothAdapter = mBluetoothManager.getAdapter();
            if (mBluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
                return false;
            }

//            byte[] equCode = readEquipmentCode();
//            if (null != equCode) {
//                BleConstant.setEquipmentCode(equCode);
//            }
            isInited = true;

            blueIsEnabled();
        }
        return true;
    }

    /**
     * 系统蓝牙是否打开
     */
    public void blueIsEnabled() {
        if (mBluetoothAdapter != null && !mBluetoothAdapter.isEnabled()) {
            broadcastUpdate(BleConstant.ID_SYS_BLUETOOTH_NOT_ENABLE);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // 注册 消息监听

        mMsgReceivedBroadcast = new MsgReceivedBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Const.NOTIFY_BLUETOOTH_STOP_SENDPIC);
        intentFilter.addAction(Const.NOTIFY_BLUETOOTH_SENDPIC);
        intentFilter.addAction(Const.NOTIFY_GET_LATEST_DRINK_RECORD);
        intentFilter.addAction(Const.NOTIFY_RETRY_CONNECT_TEN);
        registerReceiver(mMsgReceivedBroadcast, intentFilter);
        Log.i(TAG, "on create is called");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 撤销消息监听
        unregisterReceiver(mMsgReceivedBroadcast);
        close();
    }

    /**
     * We're saving the equipment code of the command
     *
     * @param equCode The equipment code of the command
     */
    public boolean writeEquipmentCode(byte[] equCode) {
        if ((null == equCode) || (equCode.length != BleConstant.EQ_SIZE)) {
            return false;
        }
        int tempInt = 0;
        int length = BleConstant.EQ_SIZE / 2;
        byte[] tempBytes = new byte[length];

        System.arraycopy(equCode, 0, tempBytes, 0, length);
        tempInt = Hex2Deci(tempBytes, false);
        System.arraycopy(equCode, length, tempBytes, 0, length);
        tempInt = Hex2Deci(tempBytes, false);


        BleConstant.setEquipmentCode(equCode);
        return true;
    }

    // 蓝牙连接状态

    public static final int CONNECT_FAIL_NOT_MAC = 0x001;
    public static final int CONNECT_FAIL_NO_MAC = 0x008;
    public static final int CONNECT_FAIL_NOT_INIT = 0x002;
    public static final int CONNECT_FAIL_NOT_ENABLE = 0x003;

    /**
     * 蓝牙正在连接
     */
    public static final int CONNECT_CONNECTING = 0x004;
    public static final int CONNECT_FAIL_CONNECT = 0x005;
    public static final int CONNECT_SUCCESS = 0x006;
    public static final int CONNECT_FAIL_NOT_FOUND = 0x007;
    private static final String MTAG = "MTAG";

//    /**
//     * Connects to the GATT server hosted on the BLE device what are saved in
//     * the {@link BleConstant#BLE_CACHE}, And you should be sure, of course,
//     * that you have save the mac address of the remote device Or you had
//     * connected with a device.
//     *
//     * @return Return true if the connection is initiated successfully. The
//     * connection result is reported asynchronously through the
//     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
//     * callback.
//     */
//    public int connect(String bleMac) {
//        String mac = preference.get(BleConstant.BLE_PREF_MAC);
//        Log.i(TAG, "connect---mac=" + mac);
//        if (null == mac) {
//            return CONNECT_FAIL_NO_MAC;
//        } else {
//            return connect(mac);
//        }
//    }

    /**
     * Disconnects an existing connection or cancel a pending connection. The
     * disconnection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */
    public void disconnect() {

        Log.i(TAG, "disconn");
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        // requestDisconnect(true);

        mBluetoothGatt.disconnect();
//        broadcastUpdate(BleConstant.HM_BLE_DISCONNECTED);
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "on Bind is called!");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // After using a given device, you should make sure that
        // BluetoothGatt.close() is called
        // such that resources are cleaned up properly. In this particular
        // example, close() is
        // invoked when the UI is disconnected from the Service.

        return super.onUnbind(intent);
    }


    /**
     * 开始扫描蓝牙
     */
    public void startScanBLE(final String bleMac) {

        BleScanRuleConfig scanRuleConfig = new BleScanRuleConfig.Builder()
                .setScanTimeOut(20000)              // 扫描超时时间，可选，默认10秒；小于等于0表示不限制扫描时间
                .build();
        BleManager.getInstance().initScanRule(scanRuleConfig);

        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                Log.i(TAG, "onScanStarted ---->" + System.currentTimeMillis());

            }

            @Override
            public void onScanning(BleDevice result) {
                Log.i(TAG, "result ---->" + result.getDevice().getAddress() + "===" + result.getDevice().getName());


                if (result.getDevice().getAddress().equals(bleMac) && !TextUtils.isEmpty(result.getDevice().getName())) {
                    byte[] scanRecord = result.getScanRecord();
                    if (new String(scanRecord).contains("HC1-T")) {
                        styleFlag = 1;
                        BleConstant.setIndexArray(1);
                    } else if (new String(scanRecord).contains("HS1-S")) {
                        styleFlag = 2;
                        BleConstant.setIndexArray(2);
                    } else if (scanRecord[11] == 0 && scanRecord[12] == 0 && scanRecord[13] == -1 && scanRecord[14] == -1
                            && scanRecord[15] == -1 && scanRecord[16] == -1 && scanRecord[17] == 100 && scanRecord[18] == 0) {
                        styleFlag = 0;
                        BleConstant.setIndexArray(0);
                    }

                    BleManager.getInstance().cancelScan();
                    connect(bleMac);
                }
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                Log.i(TAG, "onScanFinished ---->" + System.currentTimeMillis());
                int j = 0;
                for (int i = 0; i < scanResultList.size(); i++) {
                    if (scanResultList.get(i).getDevice().getAddress().equals(bleMac)) {
                        j++;
                    }
                }

                if (j < 1) {
                    broadcastUpdate(BleConstant.HM_BLE_DISCONNECTED);
                }
            }
        });

    }

    /**
     * Connects to the GATT server hosted on the BLE device.
     *
     * @param address The device address of the destination device.
     * @return Return true if the connection is initiated successfully. The
     * connection result is reported asynchronously through the
     * {@code BluetoothGattCallback#onConnectionStateChange(android.bluetooth.BluetoothGatt, int, int)}
     * callback.
     */

    private int connect(String address) {
        if (mBluetoothAdapter == null || address == null) {
            if (D)
                Log.i(TAG,
                        "BluetoothAdapter not initialized or unspecified address.");
            return CONNECT_FAIL_NOT_INIT;
        }

        if (mBluetoothDeviceAddress != null && address.equals(mBluetoothDeviceAddress)
                && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mConnectionState = ConnectionState.STATE_CONNECTING;
                return CONNECT_CONNECTING;
            } else {
                return CONNECT_FAIL_NOT_FOUND;
            }
        }

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        if (mBluetoothGatt != null) {

            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        sendBleConnectOutTime();
        mBluetoothGatt = device.connectGatt(HdApplication.getInstance(), false,
                mGattCallback);
        Log.i(TAG, "Trying to create a new connection.");
        mBluetoothDeviceAddress = address;
        mConnectionState = ConnectionState.STATE_CONNECTING;

        return CONNECT_CONNECTING;
    }

    private void sendBleConnectOutTime() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Const.blueRealtimestate) {
                    Log.i(TAG, "sendBleConnectOutTime -------------------->");
                    mHandler.sendEmptyMessage(BleConstant.HM_BLE_DISCONNECTED);
                }
            }
        }, 30 * 1000);
    }

    /**
     * After using a given BLE device, the APP must call this method to ensure
     * resources are released properly.
     */
    public void close() {

        Log.i(TAG, "close -------------------->");

        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
            mBluetoothGatt = null;
        }
        // We're cancelling the running thread for sending image
        if (mUpdateImageThread != null) {
            mUpdateImageThread.cancel();
            mUpdateImageThread = null;
        }
        // We're cancelling the running thread for parsing message
        if (mParseThread != null) {
            mParseThread.cancel();
            mParseThread = null;
        }
        // We're cancelling the running thread for sending command
        if (mSendThread != null) {
            mSendThread.cancel();
            mSendThread = null;
        }
        mReadCharacteristic = null;
        mWriteCharacteristic = null;

        mConnectionState = ConnectionState.STATE_NONE;
        Const.blueRealtimestate = false;
    }

    /**
     * Request a read on a given {@code BluetoothGattCharacteristic}. The read
     * result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicRead(android.bluetooth.BluetoothGatt, android.bluetooth.BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param characteristic The characteristic to read from.
     */
    private void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (characteristic == null) {
            characteristic = mReadCharacteristic;
        }

        mBluetoothGatt.readCharacteristic(characteristic);
    }

    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled        If true, enable notification. False otherwise.
     */
    private void setCharacteristicNotification(
            BluetoothGattCharacteristic characteristic, boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    /**
     * The messaging function
     *
     * @param what The handler code defined in the Constants
     */
    private void broadcastUpdate(final int what) {
        Message msg = new Message();
        msg.what = what;
        mHandler.sendMessage(msg);
        if (D)
            Log.i(TAG, "" + what);
    }

    /**
     * The messaging function (Overload)
     *
     * @param what      The handler code defined in the Constants
     * @param stringMsg The message received form BLE Or the Tip message
     */
    private void broadcastUpdate(final int what, final String stringMsg) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = stringMsg;
        mHandler.sendMessage(msg);
        if (D)
            Log.d(TAG, "" + what);
    }

    /**
     * Implements callback methods for GATT events that the APP cares about. For
     * example, connection change, services discovered, characteristic change
     * and characteristic write. This is the most important function in the
     * Helper.
     */
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {
        /**
         * The callback for the connect attempt, And attempts to discover
         * services if successful connection.
         *
         */
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status,
                                            int newState) {
            if (newState == 2) {

                Log.i(TAG, "bluetoote state_connected");
//                mConnectionState = ConnectionState.STATE_CONNECTED;
                gatt.discoverServices();
                broadcastUpdate(BleConstant.HM_BLE_CONNECTED);

            } else if (newState == 0) {
                Log.i(TAG, "bluetoote state_DisConnected");

                close();
                if (D)
                    Log.i(TAG, "Disconnected from GATT server.");
                // 通知handler
                broadcastUpdate(BleConstant.HM_BLE_DISCONNECTED);
            }
        }

        /**
         * In order to get the write characteristic and the read characteristic,
         * We traverse the service list.
         *
         */
        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i(TAG, "onServicesDiscovered----发现服务执行");
            if (status == BluetoothGatt.GATT_SUCCESS) { // Get service
                Log.i(TAG, "onServicesDiscovered----发现服务执行---GATT_SUCCESS");
                // successful
                List<BluetoothGattService> listGettService = mBluetoothGatt
                        .getServices();
                BluetoothGattCharacteristic characteristic; // The temporary
                // characteristic
                // Traverse the service list for the characteristic with the
                // UUID_WRITE
                Log.i(TAG, "onServicesDiscovered----not equals" + listGettService.size());
                for (BluetoothGattService gattService : listGettService) {
                    Log.i(TAG, "onServicesDiscovered----" + gattService.getUuid());
                    if (null == mWriteCharacteristic) {

                        characteristic = gattService
                                .getCharacteristic(BleConstant.UUID_WRITE);
                        if (characteristic != null) {
                            if (D) {
                                Log.i(TAG,
                                        "The characteristic with the given UUID was found"
                                                + BleConstant.UUID_WRITE
                                                .toString());
                            }

                            mWriteCharacteristic = characteristic;
                            mWriteCharacteristic
                                    .setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE);
//                            mWriteCharacteristic
//                                    .setWriteType(BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT);
                        }
                    }

                    if (null == mReadCharacteristic) {
                        characteristic = gattService
                                .getCharacteristic(BleConstant.UUID_READ);
                        if (characteristic != null) {
                            if (D)
                                Log.i(TAG,
                                        "The characteristic with the given UUID was found"
                                                + BleConstant.UUID_READ
                                                .toString());

                            final int charaProp = characteristic
                                    .getProperties();
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                                if (mReadCharacteristic != null) {
                                    setCharacteristicNotification(
                                            mReadCharacteristic, false);
                                    mReadCharacteristic = null;
                                }
//                                readCharacteristic(characteristic);
                            }
                            if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                mReadCharacteristic = characteristic;
                                setCharacteristicNotification(characteristic,
                                        true);
                            }
                        }
                    }

                    if ((null != mWriteCharacteristic)
                            && (null != mReadCharacteristic)) {
                        LockObject lockObj = new LockObject();
                        if (null == mSendThread) {
                            mSendThread = new SendThread(mBluetoothGatt,
                                    mWriteCharacteristic, mHandler, lockObj,
                                    BleHelper.this);
                            mSendThread.start();
                        }

                        if (null == mParseThread) {
                            mParseThread = new ParseThread(mHandler,
                                    BleHelper.this, lockObj);
                            mParseThread.start();
                        }
                        mConnectionState = ConnectionState.STATE_CONNECTED;
                        broadcastUpdate(BleConstant.HM_BLE_READY);
                        Const.blueRealtimestate = true;
                        break;
                    }
                }

                if ((mWriteCharacteristic == null)
                        || (mReadCharacteristic == null)) {
                    Log.i(MTAG, "ready fail onServicesDiscovered");
                    mConnectionState = ConnectionState.STATE_NONE;
                    broadcastUpdate(BleConstant.HM_BLE_READY_FAIL);
                    Const.blueRealtimestate = false;
                }

            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt,
                                          BluetoothGattCharacteristic characteristic, int status) {
            Log.i(TAG, "onCharacteristicWrite");
            if (status == BluetoothGatt.GATT_SUCCESS) {
                if (characteristic.getValue() != null)
                    Log.i(TAG,
                            "Write values----"
                                    + BleUtil
                                    .DebugMsgBytes2String(characteristic
                                            .getValue()));
                broadcastUpdate(BleConstant.HM_CMD_WRITED, "Write success");
            } else { // The write operation fails
                Log.i(TAG, "Write fails");
                if (D)
                    broadcastUpdate(BleConstant.HM_DEBUG_TOAST, "Write fails");
                broadcastUpdate(BleConstant.HM_CMD_FAILURE, "Write fails");
            }
        }

        @Deprecated
        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic, int status) {
            Log.i(MTAG, "onCharacteristicRead");
            if (status == BluetoothGatt.GATT_SUCCESS) {

                final byte[] data = characteristic.getValue();
                if (data != null && data.length > 0) {
                    if (D)
                        broadcastUpdate(BleConstant.HM_DEBUG_MSG,
                                data.toString());
                    // Create temporary object
                    ParseThread r;
                    // Synchronize a copy of the ConnectedThread
                    synchronized (this) {
                        if (mConnectionState != ConnectionState.STATE_CONNECTED) {
                            return;
                        } else {
                            r = mParseThread;
                        }
                    }
                    // Perform the write unsynchronized
                    r.addMessagePackage(data);
                }
            } else {

            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt,
                                            BluetoothGattCharacteristic characteristic) {
            Log.i(MTAG, "onCharacteristicChanged" + characteristic.getUuid());
            if (mReadCharacteristic.getUuid() == characteristic.getUuid()) {
                if (D)
                    Log.i(TAG, "The read characteristic notification!");
                final byte[] data = characteristic.getValue();
                if ((data != null) && (data.length > 0)) {
                    Log.i(TAG,
                            "readCup values----"
                                    + BleUtil.DebugMsgBytes2String(data));

                    // Create temporary object
                    ParseThread r;
                    // Synchronize a copy of the ConnectedThread
                    synchronized (this) {
                        if (mConnectionState != ConnectionState.STATE_CONNECTED) {
                            return;
                        } else {
                            r = mParseThread;
                        }
                    }
                    // Perform the write unsynchronized
                    r.addMessagePackage(data);
                }
            } else if (mWriteCharacteristic.getUuid() == characteristic
                    .getUuid()) {

                if (D)
                    Log.i(TAG, "The write characteristic notification!");
                final byte[] data = characteristic.getValue();
                if ((data != null) && (data.length > 0))
                    Log.i(TAG,
                            "writeCup values----"
                                    + BleUtil.DebugMsgBytes2String(data));

            }

        }

    };

    /**
     * Writing the command into the command queue.
     *
     * @param command The command to wirte
     */
    public void sendCmdToBle(byte[] command) {
        // Log.d(MTAG, "BleHelper sendCmdToBle is called!");
        if ((mBluetoothAdapter == null) || (mBluetoothGatt == null)) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }

        if (mWriteCharacteristic != null) {
            if ((mSendThread != null) && (command != null)
                    && (command.length > 0)) {
                mSendThread.addCommandPackage(command);
                return;
            }
        }
        if (D)
            Log.i(TAG, "The mWriteCharacteristic is null! Send Fail!");
    }

    protected void gattClose() {
        // TODO Auto-generated method stub
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    /*********************
     * 同步嘿逗表情到水杯
     ************************/
    private MsgReceivedBroadcast mMsgReceivedBroadcast;

    private class MsgReceivedBroadcast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals(
                    Const.NOTIFY_BLUETOOTH_STOP_SENDPIC)) {
                /***************** 停止发送图片 ***************/

//                stopSendImg(-1);
            } else if (intent.getAction()
                    .equals(Const.NOTIFY_BLUETOOTH_SENDPIC)) {
                /***************** 发送图片到水杯 ***************/
                String path = intent.getStringExtra("path");
                Log.i(TAG, "sendPic path=" + path);

            } else if (intent.getAction().equals(
                    Const.NOTIFY_GET_LATEST_DRINK_RECORD)) {
                Log.i("sendCheers", "获取饮水记录广播");
                if (isConnected()) {
                    Log.i("sendCheers", "isConnected获取饮水记录广播");
                    requestLatestDrinkRecord();
                } else {

                }
            } else if (intent.getAction().equals(
                    Const.NOTIFY_RETRY_CONNECT_TEN)) {
                close();
            }

        }
    }

    /**
     * 同步干杯邀请
     *
     * @param fromEn
     * @param toEn
     */
//    public void tipCheers(final String fromEn, final String toEn) {
//        final String from, to;
//        if (fromEn.equals(UserInfo.getUserInfo(BleHelper.this).getUser())) {
//            from = toEn;
//            to = fromEn;
//        } else {
//            from = fromEn;
//            to = toEn;
//        }
//        // 干杯消息
//        if (isConnected()) {
//
//            isCheersRequestHandled = false;
//
//            if (isConnected()){
//                if (mParseThread != null)
//                    mParseThread.setUserNameWaterRemind(from);
//                requestCheers(preference.getInt(Const.STYLE));
//            }
//
//
//            if (mHandler == null)
//                mHandler = new Handler();
//            mHandler.postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    if (!isCheersRequestHandled) {
//                        isCheersRequestHandled = true;
//                        // 干杯超时
//                        HdIMMessage messages = new HdIMMessage();
//                        messages.setBody(getString(R.string.str_cheers_fail));
//                        messages.setType(MsgType.CHESS);
//                        messages.addParam("subtype", "notconn");
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        params.put("to", from);
//                        params.put("msg", messages.toString());
//                        HdUtils.broadcast(BleHelper.this,
//                                Const.NOTIFY_MSG_SEND, params);
//                    } else {
//
//                    }
//                }
//            }, 100 * 1000);
//        } else {
//            HdIMMessage messages = new HdIMMessage();
//            messages.setBody(getString(R.string.str_error_bluetooth_notconnect));
//            messages.setType(MsgType.CHESS);
//            messages.addParam("subtype", "notconn");
//            HashMap<String, String> params = new HashMap<String, String>();
//            params.put("to", from);
//            params.put("msg", messages.toString());
//            HdUtils.broadcast(BleHelper.this, Const.NOTIFY_MSG_SEND, params);
//        }
//    }
//
//    /**
//     * 同步嘿逗表情
//     *
//     * @param from
//     * @param to
//     * @param json
//     */
//    public void tipBigEmotion(final String from, final String to,
//                              JSONObject json) {
//        String bigEmoStr;
//        try {
//            bigEmoStr = json.getString("body").substring(4);
//
//            String pic = bigEmoStr.split(":")[1];
//            String[] picNums = pic.split("@");
//            if (picNums.length > 1) {
//
//                BleMorePicture bmp = new BleMorePicture(preference.getInt(Const.STYLE), picNums.length);
//                HdLog.getInstance().i("BleHelpher ---->picNums.length = " +picNums.length);
//                for (int i = 0; i < picNums.length; i++) {
//                    bmp.setPicture(i, Integer.valueOf(picNums[i]), 10);
//                }
//                if (D)
//                    Log.i(TAG, "requestDisplayMP" + picNums[0] + picNums[1]);
//                if (isConnected()) {
//                    requestBuzzerSound(4);
//                    requestDisplayMP(bmp, preference.getInt(Const.STYLE));
//                }
//
//            } else {
//                if (D)
//                    Log.i(TAG, "requestDisplay1P" + picNums[0]);
//
//                int picNum = 0;
//                try {
//                    picNum = Integer.valueOf(picNums[0]);
//                } catch (NumberFormatException e) {
//                    // TODO Auto-generated catch block
//                    e.printStackTrace();
//                }
//                if (picNum == 21) {
//                    if (isConnected()) {
//                        isWaterRemindHandled = false;
//                        if (mParseThread != null)
//                            mParseThread.setUserNameWaterRemind(from);
//                        // 好友提醒饮水
//                        if (isConnected()){
//                            requestWaterRemind(preference.getInt(Const.STYLE));
//
//                            if (mHandler == null)
//                                mHandler = new Handler();
//                            mHandler.postDelayed(new Runnable() {
//
//                                @Override
//                                public void run() {
//                                    // TODO Auto-generated method stub
//                                    requestLatestDrinkRecord(preference.getInt(Const.STYLE));
//                                }
//                            }, 60 * 1000);
//                        }
//
//                        mHandler.postDelayed(new Runnable() {
//
//                            @Override
//                            public void run() {
//                                if (!isWaterRemindHandled) {
//                                    isWaterRemindHandled = true;
//
//                                    // 饮水提醒未完成
//                                    HashMap<String, String> params = new HashMap<String, String>();
//                                    params.put("to", from);
//                                    params.put(
//                                            "msg",
//                                            getString(R.string.str_waterRemind_fail_tip));
//                                    HdUtils.broadcast(BleHelper.this,
//                                            Const.NOTIFY_MSG_SEND, params);
//                                }
//                            }
//                        }, 100 * 1000);
//                    } else {
//                        HashMap<String, String> params = new HashMap<String, String>();
//                        params.put("to", from);
//                        params.put(
//                                "msg",
//                                getString(R.string.str_error_bluetooth_notconnect));
//                        HdUtils.broadcast(BleHelper.this,
//                                Const.NOTIFY_MSG_SEND, params);
//                    }
//                } else {
//                    // 表情同步
//                    if (isConnected()) {
//                        requestBuzzerSound(4);
//                        requestDisplay1P(picNum, preference.getInt(Const.STYLE));
//                    }
//
//                }
//            }
//        } catch (JSONException e1) {
//            // TODO 自动生成的 catch 块
//            e1.printStackTrace();
//        }
//    }


    /********************************************************************************************
     *
     *
     * 水杯交互
     *
     ***************** *************************************************************************/

    /**
     * 设置高温预警阈值(S1、S1-S)
     *
     * @param temperatrue 温度
     */
    public void requestSetHigthtemperatrue(int temperatrue) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfSetHightemperatrue(temperatrue * 100));

            } else {
                sendCmdToBle(BleCmdSetS1S.getCmdOfSetHightemperatrue(temperatrue));
            }
        }
    }

    /**
     * 获取指定编号定时器(S1、C1、S1-S，C1和S1-S一样)
     *
     * @param num S1水杯共5组定时器，获取时需要传对应的数字，C1和S1-S num就随便传
     */
    public void requestGetTimer(int num) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfTimer(num));

            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfTimer());

            }
        }


    }

    /**
     * 升级固件(S1、C1、S1-S，C1和S1-S一样)
     */
    public void requestUpdateHeydo() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfUpdateHeydo());
            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfUpdateHeydo());
            }
        }


    }

    /**
     * 发送饮水提醒指令到水杯(S1、S1-S)
     */
    public void requestWaterRemind() {
        // TODO 自动生成的方法存根
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfWaterRemind());

            } else if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfWaterRemind());

            }
        }


    }

    /**
     * 发送干杯指令到水杯(S1、S1-S)
     */
    public void requestCheers() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfCheers());

            } else if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfCheers());
            }
        }


    }

    /**
     * 获取水杯软件版本(S1、C1、S1-S ,C1和S1-S一样的)
     */
    public void requestCupSoftVersion() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfCupSoftVersion());
            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfCupSoftVersion());
            }
        }


    }

    /**
     * 获取序列号(S1)
     */
    public void requestSeriousNum() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfSeriousNum());
            }
        }


    }

    /**
     * 矫正杯子重量(S1、C1、S1-S，C1和S1-S一样)
     */
    public void requestAdjustWeight() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfAdjustWeight());
            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfAdjustWeight());
            }
        }


    }

    /**
     * 关闭工厂模式(S1)
     */
    public void requestExtinguishFactory() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfExtinguishFactory());

            }
        }


    }

    /**
     * 系统校时(C1、S1-S,两个命令一样的)
     *
     * @param time 时间戳
     */
    public void requesttiming(int time) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1S_STYLE_FLAG || styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfTiming(time));
            }
        }


    }

    /**
     * 开启亮屏模式(S1)
     * 0为开启,2为关闭
     */
    public void requestLightScreen(int state) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfLightScreen(state));

            }
        }


    }

    /**
     * 设置温度显示格式(S1)
     * 1为设置摄氏度,2为设置华氏度
     */
    public void requestTemperatureStyle(int state) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfTemparatureStyle(state));
            }
        }


    }

    /**
     * 清空记录(S1,测试用不公开)
     */
    public void requestDeleteRecords() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfDeleteRecords());
            }
        }


    }

    /**
     * 蜂鸣器鸣叫(S1、S1-S)
     * count 次数
     */
    public void requestBuzzerSound(int count) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG || styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfBuzzerSound(count));

            }
        }


    }

    /**
     * 水杯单色呼吸灯闪烁(C1)
     *
     * @param count 闪烁次数
     * @param r     对应RGB三原色R
     * @param g     对应RGB三原色G
     * @param b     对应RGB三原色B
     */
    public void requestBreathingLightFlashing(int count, int r, int g, int b) {
        if (Const.blueRealtimestate) {

            if (styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfBreathingLightFlashing(count, r, g, b));
            }
        }


    }

    /**
     * 颜色列表渐变(C1)
     *
     * @param length 颜色列表长度
     * @param color1 对应RGB三原色R
     */
    public void requestBreathingLightsFlashing(int length, int count, List<Integer> color1) {
        if (Const.blueRealtimestate) {

            if (styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfBreathingLightsFlashing(length, count, color1));
            }
        }

    }

    /**
     * 自定义免打扰(S1)
     *
     * @param state           时间段 0x03:全时间段、0x01时间段1、0x02时间段2
     * @param firstTimeStart  时间段1开始时间
     * @param firstTimeEnd    时间段1结束时间
     * @param secondTimeStart 时间段2开始时间
     * @param secondTimeEnd   时间段2结束时间
     */
    public void requestCustomNoDisTurb(int state, int firstTimeStart, int firstTimeEnd, int secondTimeStart, int secondTimeEnd) {
        if (Const.blueRealtimestate) {

            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfCustomNoDisturb(state, firstTimeStart, firstTimeEnd, secondTimeStart, secondTimeEnd));

            }
        }

    }

    /**
     * 自定义界面显示(S1)
     *
     * @param main  主界面，默认有效
     * @param one   顺序1，索引值,0XFF无效，下同
     * @param two   顺序2
     * @param three 顺序3
     */
    public void requestCustomUiShow(int main, int one, int two, int three) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfCustomUiShow(main, one, two, three));

            }
        }


    }

    /**
     * 开水温度降低提醒(S1、S1-S)
     *
     * @param offOn      1开启 0关闭
     * @param temprature 温度值
     */
    public void requestTempratureLow(int offOn, int temprature) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfTempratureLow(offOn, temprature));

            } else if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfTempratureLow(offOn, temprature));
            }
        }


    }

    /**
     * 获取温度提醒（S1-S）
     */
    public void requestGetRemindTemp() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfRemindTemp());

            }
        }


    }


    /**
     * 设置水量计量显示格式(S1)
     *
     * @param state 1为设置 毫升ml,2为设置  盎司oz
     */
    public void requestWeightStyle(int state) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfWeightStyle(state));
            }
        }


    }

    /**
     * 设置免打扰的状态(C1、S1-S)
     *
     * @param disturb 1:开启免打扰      0:关闭免打扰
     * @param remind  1:关闭饮水提醒     0:开启饮水提醒
     * @param light   LED亮度 (4级) 亮度建议设置为 2级别
     * @param volume  系统音量(16级) 音量建议设置为 8级别
     */
    public void requestSetNoDisturbing(int disturb, int remind, int light, int volume) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfSetNoDisturbing(disturb, remind, light, volume));

            } else if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfSetNoDisturbing(disturb, remind, light, volume));
            }
        }


    }

    /**
     * 音量测试(C1)
     *
     * @param volume 测试音量大小
     */
    public void requestSetSoundAjust(int volume) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfSetSoundAjust(volume));
            }
        }


    }

    /**
     * 设置LED亮度(C1)
     *
     * @param second 灯亮时间长度
     * @param light  亮度等级
     */
    public void requestSetLightAjust(int red, int green, int blue, int second, int light) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfSetLightAjust(red, green, blue, second, light));
            }
        }


    }

    /**
     * 获取多条触摸记录(S1)
     *
     * @param number   记录的起始编号
     * @param quantity 记录的数量
     */
    public void requestTouchsRecord(int number, int quantity) {
        if (Const.blueRealtimestate) {

            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfGetTouchsRecord(number, quantity));
            }
        }

    }

    /**
     * 获取最新饮水数据(S1、C1、S1-S，C1和S1-S一样)
     */
    public void requestLatestDrinkRecord() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfDrinkRecord());
            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfDrinkRecord());
            }
        }

    }

    /**
     * 获取多条饮水记录(S1、C1、S1-S，C1和S1-S一样)
     *
     * @param startNum 起始编号
     * @param quantity 获取条数  S1最多请求10条，C1、S1-S最多请求6条
     */
    public void requestDrinksRecord(int startNum, int quantity) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfGetDrinksRecord(startNum, quantity));
            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfGetDrinksRecord(startNum, quantity));
            }
        }


    }


    /**
     * 获取高安全状态(S1、S1-S)
     * 水杯类型为S1时 tempUnit和waterUnit随便填充两个数,S1-S时则都要对应填充
     *
     * @param openHigh  1:开启高安全模式     0:关闭高安全模式
     * @param tempUnit  1:温度单位(华氏度)  0:温度显示单位(℃)
     * @param waterUnit 1:水量单位(盎司)    0:温度显示单位(ml)
     */
    public void requestHeighSafeControl(int openHigh, int tempUnit, int waterUnit) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOftHeighSafeControl(openHigh));
            } else if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOftHeighSafeControl(openHigh, tempUnit, waterUnit));

            }
        }


    }

    /**
     * 设置提醒(S1)
     *
     * @param group      定时器组
     * @param setType    设置方式设置方式 0x01//时间戳（当前系统时间） 0x02//定时秒 0x03//关闭定时
     * @param frequency  定时频率 1:每天/0:一次
     * @param switcher   定时器开关 1:开启0:关闭
     * @param remindType 提醒方式 0x01 咖啡 0x02 吃药 0x03 瘦身 其他 普通闹钟
     * @param seconds    定时的时间
     * @param waterValue （瘦身启用）单次饮水量提醒
     */
    public void requestsetAlarm(int group, int setType, int frequency,
                                int switcher, int remindType, int seconds, int waterValue) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfSetAlarm(group, setType, frequency, switcher,
                        remindType, seconds, waterValue));
            }
        }


    }

    /**
     * 设置水杯定时器组(S1-S,C1)
     *
     * @param context
     * @param style
     */
    public void requestsetChildAlarm(Context context, List<AssisstTimerBean> datas, int style) {
        if (style == 1) {
            sendCmdToBle(BleCmdSetC1.getCmdOfChildSetAlarm(context, datas));

        } else {
            sendCmdToBle(BleCmdSetS1S.getCmdOfS1SSetAlarm(context, datas));

        }
    }

    /**
     * 设置每日饮水目标(S1、C1、S1-S，C1和S1-S一样)
     *
     * @param data 饮水目标
     */
    public void requestDayDrinkGoal(int data) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                if (data * 10 > 32767) {
                    sendCmdToBle(BleCmdSetS1.getCmdOfDayDrinkGoal(-(data * 10 - 32767)));
                } else {
                    sendCmdToBle(BleCmdSetS1.getCmdOfDayDrinkGoal(data * 10));
                }

            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfDayDrinkGoal(data));
            }
        }


    }

    /**
     * 获取每日饮水目标(S1、C1、S1-S，C1和S1-S一样)
     */
    public void requestGetDrinkGoal() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfGetDrinkGoal());

            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfGetDrinkGoal());

            }
        }

    }

    /**
     * 请求水杯状态信息(S1、C1、S1-S，C1和S1-S一样)
     */
    public void requestCurrentStatusOne() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfCurrentStatusOne());

            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfCurrentStatusOne());

            }
        }

    }

    /**
     * 播放一张图片(S1、S1-S)，需要结合“各种参数存储定义”来使用，以下为两个例子图片地址
     * 40		大哭
     * 41		委屈
     * 42		无聊
     * 43		流汗
     * 44		大笑
     * 45		尴尬
     * 46		害羞
     * 47		色迷迷
     * 48		抠鼻
     * 49		装萌
     * 50		献吻
     * 51		装酷
     * 52	    微笑
     *
     * @param index The index of picture
     */
    public void requestDisplay1P(int index) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfDisplay1P(index));

            } else if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfDisplay1P(index));

            }
        }


    }

    /**
     * 播放多张图片(S1、S1-S,暂时不用)
     *
     * @see BleMorePicture
     */
    public void requestDisplayMP(BleMorePicture mpInfo) {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfDisplayMP(mpInfo));

            } else {
                sendCmdToBle(BleCmdSetS1S.getCmdOfDisplayMP(mpInfo));

            }
        }


    }


    /**
     * 发送文件到水杯(S1、C1、S1-S)
     *
     * @param addressIndex 文件存放地址
     * @param data         文件字节数据
     */
    public void requestSendFile(int addressIndex, byte[] data) {
        BleFileSend file;
        if (styleFlag == 0) {
            file = new BleFileSendS1(addressIndex, data);
        } else if (styleFlag == 1) {
            file = new BleFileSendC1(addressIndex, data);
        } else {
            file = new BleFileSendS1S(addressIndex, data);
        }
        if (null == mUpdateFileThread) {
            Log.w("updateImageTest", "新开线程");
            mUpdateFileThread = new BleUpdateFileThread(file, this, mSendThread, styleFlag);
            mUpdateFileThread.start();
        } else {
            Log.w("updateImageTest", "已有线程正在运行");
            mUpdateFileThread.addFile(file);
        }
    }

    /**
     * 重启水杯(S1、C1、S1-S，C1和S1-S一样)
     */
    public void requestResetCup() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1.getCmdOfResetCup());
            } else {
                sendCmdToBle(BleCmdSetC1.getCmdOfResetCup());
            }
        }

    }

    /**
     * 恢复出厂(C1)
     */
    public void requestFactoryReset() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.C1_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetC1.getCmdOfFactoryReset());
            }
        }


    }

    /**
     * 显示JPG头像(S1-S)
     */
    public void requestShowJpgHead() {
        if (Const.blueRealtimestate) {
            if (styleFlag == Const.S1S_STYLE_FLAG) {
                sendCmdToBle(BleCmdSetS1S.getCmdOfShowJpgHead());
            }
        }

    }

}