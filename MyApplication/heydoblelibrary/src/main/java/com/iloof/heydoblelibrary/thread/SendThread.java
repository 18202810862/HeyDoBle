package com.iloof.heydoblelibrary.thread;

import android.annotation.TargetApi;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.BleUtil;
import com.iloof.heydoblelibrary.HdUtil;
import com.iloof.heydoblelibrary.LockObject;
import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.util.BleCommand;
import com.iloof.heydoblelibrary.util.BleQueue;

import static com.iloof.heydoblelibrary.BleConstant.D;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
public class SendThread extends Thread {

    private final static String TAG = "SendThread";

//    private int tag = 0;

    /**
     * Generic Attribute Profile (GATT)
     */
    private BluetoothGatt mBluetoothGatt;
    /**
     * The special characteristic with UUID_WRITE for write operation
     */
    private BluetoothGattCharacteristic mWriteCharacteristic;
    /**
     * False if you want to cancel this thread.
     */
    private boolean mIsRun;
    /**
     * What the Handler is traced to BleHandler is an input parameter when you
     * get the instance of BleParseMessageThread
     */
    private Handler mHandler;

    private boolean isReSend;
    /**
     * mBluetoothGatt.writeCharacteristic(mWriteCharacteristic) 发送数据失败次数
     **/
    int sendValuesFailTimes = 0;
    /**
     * The queue for command.
     *
     * @see BleQueue
     */
    private BleQueue mQueue;
    /**
     * The instance of the class of BleCommand are used as the temporary place
     * for saving the last command
     */
    private BleCommand mLastCommand;
    /**
     * 发送接收蓝牙数据的共享锁
     */
    LockObject mLockObj;

    Context mContext;

    public SendThread(BluetoothGatt btGatt,
                      BluetoothGattCharacteristic btChactacteristic, Handler handler,
                      LockObject lockObj, Context context) {

        // Initialization
        mBluetoothGatt = btGatt;
        mWriteCharacteristic = btChactacteristic;
        mHandler = handler;
        mLockObj = lockObj;
        mContext = context;
        mQueue = new BleQueue(BleConstant.QUEUE_SIZE);
        mIsRun = true;
        isReSend = false;

    }

    /**
     * Writing the command into the command queue.
     *
     * @param command the command to write.
     */
    public void addCommandPackage(byte[] command) {
        if (null == command) {
            if (D)
                Log.i(TAG, "The message is null!");
            return;
        }

        mQueue.addElement(command);

        if (D)
            Log.i(TAG, "Add the command!");

    }

    @Override
    public void run() {
        byte[] command;
        while (mIsRun) {
            command = getCommandFromBuffer();

            if (null != command) {
                sendCmdToBle(command);
            } else {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 清除命令队列
     */
    public void clear() {
        mQueue.clear();
    }

    /**
     * If you attempt to close the APP or when the connection has disconnected,
     * you should cancel the thread through calling this function.
     */
    public void cancel() {
        mIsRun = false;
        mQueue.destroy();
        Log.i(TAG, "We have stoped the BleSendCmd thread.");
    }

    /**
     * Getting command
     *
     * @return The command included in the buffer.
     */
    private byte[] getCommandFromBuffer() {
        byte[] reCommand = null;

        if ((isReSend) && (mLastCommand != null)) {
            if (BleCommand.reSendCount < 10) {
                byte[] temp = mLastCommand.getBytes();
                int length = temp.length;
                reCommand = new byte[length];
                System.arraycopy(temp, 0, reCommand, 0, length);
                isReSend = false;
                if (D)
                    Log.i(TAG, "Return the last command!");
                return reCommand;
            } else {

                BleCommand.reSendCount = 0;
                isReSend = false;
//                 broadCastSendFail();
                broadcastUpdate(BleConstant.HM_MSG_SENDFAIL, mLastCommand);
                HdUtil.broadcast(mContext,Const.NOTIFY_RETRY_CONNECT_TEN,null);
                mQueue.clear();
                if (D)
                    Log.i(TAG, "Return the last command more than 15 times!");
            }
        }
        BleCommand.reSendCount = 0;
        byte[] buffer = mQueue.getAllBytes();
        if (null == buffer) {
            // if (D)
            // Log.i(TAG, "There is no data in the command queue.");

            return reCommand;
        }

        int posStart = 0;
        int posEnd = 0;
        boolean isStart = false;
        boolean isEnd = false;

        for (int offset = 0; offset < buffer.length; ++offset) {
            if (BleConstant.HEAD_CODE == buffer[offset]) {
                posStart = offset;
                isStart = true;
            }
            if (isStart) { // Now we are finding the end code of command.
                if (BleConstant.END_CODE == buffer[offset]) {
                    posEnd = offset;
                    isEnd = true;
                    break;
                }
            }
        }

        if (!isEnd) {
            // if (D)
            // Log.i(TAG, "We can't found the command from the buffer.");
            return reCommand;
        } else { // Now We are sure the fact that we have found a command.
            int length = posEnd - posStart + 1;
            reCommand = new byte[length];
            System.arraycopy(buffer, posStart, reCommand, 0, length);
            mQueue.removeElement(posEnd);
            if (D)
                Log.i(TAG, "Get the command successful!");

            return reCommand;
        }

    }

    /**
     * The function is the only interface which we are allowed to send command
     * to BLE. Request a write on a given {@code BluetoothGattCharacteristic}.
     * The write result is reported asynchronously through the
     * {@code BluetoothGattCallback#onCharacteristicWrite(BluetoothGatt, BluetoothGattCharacteristic, int)}
     * callback.
     *
     * @param cmd The command to send.
     */
    private void sendCmdToBle(byte[] cmd) {

//        tag++;
//        Log.i(TAG, " tag == " + tag);
        if ((mBluetoothGatt == null) || (mWriteCharacteristic == null)) {
            if (D)
                Log.i(TAG, "BluetoothAdapter not initialized");
            return;
        }
        Log.d(TAG, "发送的命令:" + BleUtil.DebugMsgBytes2String(cmd));

        if ((cmd == null) || (cmd.length == 0)) {
            if (D)
                Log.i(TAG, "sendMessageToBle:	Command is null!");
            return;
        }
        // save the command
        mLastCommand = new BleCommand(cmd);
        synchronized (mLockObj) {
            mLockObj.tag = -1;
        }

        int length = cmd.length;
        if (length <= BleConstant.MSG_MAX_SIZE) {
            mWriteCharacteristic.setValue(cmd);
            boolean result = mBluetoothGatt
                    .writeCharacteristic(mWriteCharacteristic);
            if (!result) {
                isReSend = true;
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
                return;
            }
        } else { // Every times send 20 bytes
            byte[] subCmd = new byte[BleConstant.MSG_MAX_SIZE];
            int count = length / BleConstant.MSG_MAX_SIZE;
            int remainder = length % BleConstant.MSG_MAX_SIZE;
            sendValuesFailTimes = 0;
            for (int i = 0; i < count; ++i) {
                System.arraycopy(cmd, i * BleConstant.MSG_MAX_SIZE, subCmd, 0,
                        BleConstant.MSG_MAX_SIZE);
                mWriteCharacteristic.setValue(subCmd);
                Log.i(TAG, "i =" + i + "count = " + count + "thread = " + Thread.currentThread().getId());
                Log.i(TAG, "subCmd =  " + BleUtil.DebugMsgBytes2String(subCmd));
                boolean result = mBluetoothGatt
                        .writeCharacteristic(mWriteCharacteristic);
                try {
                    if (!result) {
                        if (sendValuesFailTimes > 10) {
                            sendValuesFailTimes = 0;
                            isReSend = true;
                            return;
                        }
                        --i;
                        Log.d(TAG, "重发==========>" + i);
                        sendValuesFailTimes++;
                        Thread.sleep(sendValuesFailTimes * 600);
                    } else {
                        sendValuesFailTimes = 0;
                        Thread.sleep(30);//30
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (remainder != 0) {

                byte[] remaindCmd = new byte[remainder];
                System.arraycopy(cmd, count * BleConstant.MSG_MAX_SIZE, remaindCmd,
                        0, remainder);
                mWriteCharacteristic.setValue(remaindCmd);
                mBluetoothGatt.writeCharacteristic(mWriteCharacteristic);

            }
        }

//		}

        if (D) { // The debug code

            broadCastSendProgress();
            Log.i(TAG, "test--" + mLastCommand.index + "=index");
            Log.i(TAG, "test--" + mLastCommand.currentPackage
                    + "=currentPackage");
        }

        // 等待3s,如果还未收到消息则重发
        try {

            boolean isNeedResend = false;
            int i = 0;
//            int time;
//            if (preference.getBoolean(Const.IS_UPDATE_HEAD)){
//                time = 150;
//            }else {
//                time = 110;
//            }

//            Log.i(TAG,"time ------>" + time);
            for (; i < 10; i++) {
                Thread.sleep(200);
                synchronized (mLockObj) {
                    // 未收到消息或收到到消息与当前命令不符则重发
                    if (mLockObj.tag == -1
                            || mLastCommand.index != mLockObj.tag) {
                        Log.i(TAG, "mLastCommand.index--" + mLastCommand.index);
                        Log.i(TAG, "mLockObj.tag--" + mLockObj.tag);
                        if (mLastCommand.index == BleConstant.ID_UPDATEHEYDO){
                            isReSend = false;
                            isNeedResend = false;
                            break;
                        }else {
                            Log.i(TAG, "isNeedResend = true;------>");
                            isNeedResend = true;

                        }
                    } else {
                        isReSend = false;
                        isNeedResend = false;
                        break;
                    }
                }
            }
            if (isNeedResend) {
                Log.d(TAG, "重发=====>");
                isReSend = true;
            } else {

                Log.d(TAG, i + "===========>");
            }
        } catch (InterruptedException e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }

    }

    /**
     * The messaging function
     *
     * @param what    The handler code defined in the Constants
     * @param command The command readily to send
     */
    private void broadcastUpdate(final int what, Object command) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = command;
        mHandler.sendMessage(msg);
        // BleCommand bleCommand=new BleCommand(command);
        if (D)
            Log.d(TAG, "broadcastUpdate---" + what);
    }

    /**
     * 广播发送失败
     */
    @Deprecated
    private void broadCastSendFail() {
        // 通知发送文件失败
        Intent intent2 = new Intent(Const.NOTIFY_BLUETOOTH_SENDPIC_FAIL);
        /** 发送命令索引 */
        intent2.putExtra("commandIndex", mLastCommand == null ? -1
                : mLastCommand.index);
        mContext.sendBroadcast(intent2);
    }

    /**
     * 广播发送进度
     */
    @Deprecated
    private void broadCastSendProgress() {
        Intent intent2 = new Intent(Const.NOTIFY_BLUETOOTH_SENDPICING);
        /** 发送命令索引 */
        intent2.putExtra("commandIndex", mLastCommand == null ? -1
                : mLastCommand.index);
        /** 发送进度 */
        intent2.putExtra("progress", mLastCommand == null ? 0
                : mLastCommand.currentPackage);
        mContext.sendBroadcast(intent2);
    }

    /**
     * 更新解析线程 handler引用
     *
     * @param mHandler
     */
    public void setHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

}
