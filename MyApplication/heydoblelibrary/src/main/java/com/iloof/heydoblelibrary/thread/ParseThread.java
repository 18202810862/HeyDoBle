package com.iloof.heydoblelibrary.thread;

import android.content.Context;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.BleHandler;
import com.iloof.heydoblelibrary.BleHelper;
import com.iloof.heydoblelibrary.BleUtil;
import com.iloof.heydoblelibrary.BleUtil.BleCurrentStaus1;
import com.iloof.heydoblelibrary.BleUtil.BleFactoryMode;
import com.iloof.heydoblelibrary.BleUtil.BleImgCrc;
import com.iloof.heydoblelibrary.BleUtil.BleWrtImg;
import com.iloof.heydoblelibrary.BleUtil.Cheers;
import com.iloof.heydoblelibrary.BleUtil.DrinkRecord;
import com.iloof.heydoblelibrary.BleUtil.DrinksRecord;
import com.iloof.heydoblelibrary.BleUtil.HardwareVersion;
import com.iloof.heydoblelibrary.BleUtil.SendFile;
import com.iloof.heydoblelibrary.HdUtil;
import com.iloof.heydoblelibrary.LockObject;
import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.util.BleMessage;
import com.iloof.heydoblelibrary.util.BleQueue;

import static com.iloof.heydoblelibrary.BleConstant.D;
import static com.iloof.heydoblelibrary.util.BleTransfer.decoding;

public class ParseThread extends Thread {

    private final static String TAG = "ParseThread";

    /**
     * False if you want to cancel this thread.
     */
    private boolean mIsRun;
    /**
     * What the Handler is traced to BleHandler is an input parameter when you
     * get the instance of BleParseMessageThread
     */
    private Handler mHandler;
    /**
     * What the thread aims to send image is traced back to {@link Thread}
     */

    private Editor mEditor;

    /**
     * 上下文
     */
    private Context mContext;
    /**
     * 饮水提醒好友帐户
     */
    private String mUserNameWaterRemind;
    private String msgEn[], msgEnOz[];

    public void setUserNameWaterRemind(String userNameWaterRemind) {
        this.mUserNameWaterRemind = userNameWaterRemind;
    }

    /**
     * The queue for message.
     *
     * @see BleQueue
     */
    private BleQueue mQueue;

    private LockObject lockObj;

    public ParseThread(Handler handler, Context context, LockObject lockObj) {
        this.mHandler = handler;
        // this.mEditor=editor;
        this.mContext = context;
        this.lockObj = lockObj;
        mQueue = new BleQueue(BleConstant.QUEUE_SIZE);
        // mMessage = null;
        mIsRun = true;

    }

    /**
     * Writing the message into the message queue.
     *
     * @param message the message to write.
     */
    public void addMessagePackage(byte[] message) {
        if (null == message) {
            if (D)
                Log.i(TAG, "The message is null!");
            return;
        }

        mQueue.addElement(message);

        return;
    }

    @Override
    public void run() {

        byte[] message = null;
        while (mIsRun) {
            message = getMessageFromBuffer();

            if (null != message) {
                // Log.i("Test1",
                // "getMessageFromBuffer==message.length="+message.length);

                parseMessage(message);
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
     * Getting message
     *
     * @return The message included in the buffer.
     */
    private byte[] getMessageFromBuffer() {
        byte[] reMessage = null;

        byte[] buffer = mQueue.getAllBytes();
        if (null == buffer) {
            // if (D)
            // Log.i(TAG, "Something is wrong with buffer length");

            return reMessage;
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
            if (isStart) { // Now we are finding the end code of message.
                if (BleConstant.END_CODE == buffer[offset]) {
                    posEnd = offset;
                    isEnd = true;
                    break;
                }
            }
        }

        if (!isEnd) {
            // if (D)
            // Log.i(TAG, "We can't found the message from the buffer.");
            return reMessage;
        } else { // Now We are sure the fact that we have found a message.
            int length = posEnd - posStart + 1;
            reMessage = new byte[length];
            System.arraycopy(buffer, posStart, reMessage, 0, length);
            mQueue.removeElement(posEnd);
            if (D)
                Log.i(TAG, "Getting message successful!");

            return decoding(reMessage);
        }

    }

    private void parseMessage(byte[] msg) {
        if (null == msg) {
            Log.w(TAG, "The message to parse is null");
            return;
        }

        // Log.i("Test1", "parseMessage msg.len="+msg.length);
        BleMessage message = new BleMessage(msg);

        if (!message.isRight) {
            return;
        }

        // release lock
        synchronized (lockObj) {
            lockObj.tag = message.index;
            lockObj.packageSn = message.currentPackage;
        }
        // Now we're parsing the message

        Log.i(TAG, "message.index---->" + message.index);
        switch (message.index) {
            case BleConstant.ID_SERIOUS_NUM:
                broadcastUpdate(BleConstant.ID_SERIOUS_NUM, message);
                break;

            case BleConstant.ID_SET_ALARM:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_SET_ALARM, message);
                break;

            case BleConstant.HM_MSG_DAY_DRINK_GOAL:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.HM_MSG_DAY_DRINK_GOAL, message);
                break;

            case BleConstant.ID_CUP_HARDWARE_VERSION:
                message = new HardwareVersion(message);
                broadcastUpdate(BleConstant.ID_CUP_HARDWARE_VERSION, message);
                break;
            case BleConstant.ID_CURRENT_STATUS_ONE:
                message = new BleCurrentStaus1(message);
                broadcastUpdate(BleConstant.ID_CURRENT_STATUS_ONE, message);
                break;

            case BleConstant.ID_FACTORY_MODE:
                message = new BleFactoryMode(message);
                broadcastUpdate(BleConstant.HM_MSG_FACTORYMODE, message);
                break;
            case BleConstant.ID_WRITE_PICTURE_32:
                BleWrtImg wrtImg32 = new BleWrtImg(message);
                if (!wrtImg32.isSuccess) {
                    // We will pass the message about HM_MSG_WPICTURE32,
                    // Only when something is wrong about the updatedata.
                    broadcastUpdate(BleConstant.HM_MSG_WPICTURE32,
                            wrtImg32.imgIndex);
                }
                break;
            case BleConstant.ID_READ_PICTURE_CRC:
                message = new BleImgCrc(message);
                broadcastUpdate(BleConstant.HM_MSG_PICTURECRC, message);
                break;
            case BleConstant.ID_WRITE_PICTURE_64:
                BleWrtImg wrtImg64 = new BleWrtImg(message);
                if (!wrtImg64.isSuccess) {
                    // We will pass the message about HM_MSG_WPICTURE64,
                    // Only when something is wrong about the updatedata.
                    broadcastUpdate(BleConstant.HM_MSG_WPICTURE64,
                            wrtImg64.imgIndex);
                }
                break;
            case BleConstant.ID_WRITE_PICTURE_1024:
                BleWrtImg wrtImg1024 = new BleWrtImg(message);
                if (!wrtImg1024.isSuccess) {
                    // We will pass the message about HM_MSG_WPICTURE64,
                    // Only when something is wrong about the updatedata.
                    broadcastUpdate(BleConstant.HM_MSG_WPICTURE1024,
                            wrtImg1024.imgIndex);
                }
                break;
            case BleConstant.ID_RESET_PASSWORD:
                if (message.isRight) {
                    broadcastUpdate(BleConstant.HM_MSG_PASSWORD, 1);
                } else {
                    broadcastUpdate(BleConstant.HM_MSG_PASSWORD, 0);
                }
                break;
            case BleConstant.ID_CHANGE_MODE:
                if ((null == message) || (null == message.data)
                        || (message.data.length != 2)) {
                    broadcastUpdate(BleConstant.HM_MSG_CHANGEMODE, 0);
                } else {
                    if ((0x00 == message.data[0]) && (0x00 == message.data[1])) {
                        broadcastUpdate(BleConstant.HM_MSG_CHANGEMODE, 1);
                    } else {
                        broadcastUpdate(BleConstant.HM_MSG_CHANGEMODE, 0);
                    }
                }
                break;

            case BleConstant.ID_ADJUST_WEIGHT:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_ADJUST_WEIGHT, message);
                break;
            case BleConstant.ID_SET_HIGH_TEMPERATURE:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_SET_HIGH_TEMPERATURE, message);
                break;

            case BleConstant.ID_TEMPARATURE_STYLE:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_TEMPARATURE_STYLE, message);
                break;
            case BleConstant.ID_WEIGHT_STYLE:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_WEIGHT_STYLE, message);
                break;
            case BleConstant.ID_DELETE_RECORDS:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_DELETE_RECORDS, message);
                break;

            case BleConstant.ID_CUSTOM_NO_DISTURB:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_CUSTOM_NO_DISTURB, message);
                break;
            case BleConstant.ID_CUSTOM_UI_SHOW:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_CUSTOM_UI_SHOW, message);
                break;
            case BleConstant.ID_WATER_TEMPRATURE_LOW:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_WATER_TEMPRATURE_LOW, message);
                break;
            case BleConstant.ID_CUP_CONSTANT_TEMPRATURE:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_CUP_CONSTANT_TEMPRATURE, message);
                break;

            case BleConstant.ID_EXTINGUISH_FACTORY:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_EXTINGUISH_FACTORY, message);
                break;

            case BleConstant.ID_FACTORY_PATTERN:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_FACTORY_PATTERN, message);
                break;

            case BleConstant.ID_SET_NO_DISTURBING:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_SET_NO_DISTURBING, message);
                break;

            case BleConstant.ID_LIGTH_SCREEN:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_LIGTH_SCREEN, message);
                break;

            case BleConstant.ID_CHANGE_NAME:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.HM_MSG_RENAME, message);
                break;
            case BleConstant.ID_HIGH_LEVEL:
                if ((null == message) || (null == message.data)
                        || (message.data.length != 2)) {
                    broadcastUpdate(BleConstant.HM_MSG_HIGHLEVEL, 0);
                } else {
                    if ((0x00 == message.data[0]) && (0x00 == message.data[1])) {
                        broadcastUpdate(BleConstant.HM_MSG_HIGHLEVEL, 1);
                    } else {
                        broadcastUpdate(BleConstant.HM_MSG_HIGHLEVEL, 0);
                    }
                }
                break;
            case BleConstant.ID_WORDS:
                if ((null == message) || (null == message.data)
                        || (message.data.length != 2)) {
                    broadcastUpdate(BleConstant.HM_MSG_WORDS, 0);
                } else {
                    if ((0x00 == message.data[0]) && (0x00 == message.data[1])) {
                        broadcastUpdate(BleConstant.HM_MSG_WORDS, 1);
                    } else {
                        broadcastUpdate(BleConstant.HM_MSG_WORDS, 0);
                    }
                }
                break;
            case BleConstant.ID_DRINKING_RECORD:
                message = new DrinkRecord(message);
                Log.d("recordTime", ((DrinkRecord) message).recordType + "");
                broadcastUpdate(BleConstant.ID_DRINKING_RECORD, message);
                DrinkRecord record = (DrinkRecord) message;
                if (BleHelper.styleFlag == Const.S1_STYLE_FLAG) {
                    if (((DrinkRecord) message).recordType == 32) {
                        //老款S1水杯提醒饮水成功
                    } else if (((DrinkRecord) message).recordType == 64) {
                        //老款S1水杯干杯成功
                    }
                } else if (BleHelper.styleFlag == Const.S1S_STYLE_FLAG) {
                    if (((DrinkRecord) message).recordType == 64) {
                        //新款S1-S水杯提醒饮水成功
                    } else if (((DrinkRecord) message).recordType == 128) {
                        //新款S1-S水杯干杯成功
                    }
                }

                break;
            case BleConstant.ID_DRINKING_RECORDS:

                message = new DrinksRecord(message);
                broadcastUpdate(BleConstant.ID_DRINKING_RECORDS, message);
                break;
            case BleConstant.ID_SEND_FILE:

                message = new SendFile(message);
                broadcastUpdate(BleConstant.ID_SEND_FILE, message);
                break;
            case BleConstant.ID_CHEERS:
                Log.d("cheerResponse", "收到水杯干杯回应");
                message = new Cheers(message);
                broadcastUpdate(BleConstant.ID_CHEERS, message);
                int cheersResult = ((Cheers) message).result;
                // 干杯消息反馈
                if (cheersResult == 0) {
                    Log.d("cheerResponse", " 干杯成功");
                    if (!BleHelper.isCheersRequestHandled) {
                        // 干杯成功
                        sendCheersSuccessTip();
                        // sendCheersReply(mContext
                        // .getString(R.string.str_cheers_success));
                    }
                } else {
                    Log.d("cheerResponse", " 对方已经离开");
                    if (!BleHelper.isCheersRequestHandled) {
                        BleHelper.isCheersRequestHandled = true;
                    }
                }
                break;
            case BleConstant.ID_UPDATEHEYDO:
                message = new BleUtil.UpdateHeydo(message);
                broadcastUpdate(BleConstant.ID_UPDATEHEYDO, message);
                break;
            case BleConstant.ID_CUP_SOFT_VERSION:
                message = new BleUtil.CupSoftVersion(message);
                broadcastUpdate(BleConstant.ID_CUP_SOFT_VERSION, message);
                break;
            case BleConstant.ID_WATERREMIND:
                // 饮水提醒

                break;
            case BleConstant.ID_GET_FACTORY_STATUS:
                message = new BleUtil.BleFactoryModeStatus(message);
                broadcastUpdate(BleConstant.ID_GET_FACTORY_STATUS, message);
                break;
            case BleConstant.ID_GET_ALL_TIMER:
                message = new BleUtil.BleAllTimers(message);
                broadcastUpdate(BleConstant.ID_GET_ALL_TIMER, message);
                break;
            case BleConstant.ID_GET_ALL_TIMER_TIME:
                message = new BleUtil.BleAllTimersTime(message);
                broadcastUpdate(BleConstant.ID_GET_ALL_TIMER_TIME, message);
                break;
            case BleConstant.ID_GET_TIMER:
                message = new BleUtil.BleTimerWithTime(message);
                broadcastUpdate(BleConstant.ID_GET_TIMER, message);

                break;
            case BleConstant.ID_GET_DAY_DRINK_GOAL:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_GET_DAY_DRINK_GOAL, message);
                break;
            case BleConstant.ID_TIMING:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_TIMING, message);

                break;
            case BleConstant.ID_SOUND_AJUST:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_SOUND_AJUST, message);

                break;
            case BleConstant.ID_LIGHT_AJUST:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_LIGHT_AJUST, message);

                break;
            case BleConstant.ID_MEMORY_VERSION:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_MEMORY_VERSION, message);

                break;
            case BleConstant.ID_VOICE_MODE:
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_VOICE_MODE, message);

                break;
            case BleConstant.ID_HEIGH_SAFE_CONTROL:
                Log.i(TAG, "ID_HEIGH_SAFE_CONTROL--------->");
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_HEIGH_SAFE_CONTROL, message);

                break;
            case BleConstant.ID_GET_REMIND_TEMP:
                Log.i(TAG, "ID_HEIGH_SAFE_CONTROL--------->");
                message = new BleMessage(message);
                broadcastUpdate(BleConstant.ID_GET_REMIND_TEMP, message);

                break;
            default:
                Log.i(TAG, "Out of Range!");
                break;
        }

    }

    /**
     * 干杯成功后获取最新的饮水记录
     */
    private void sendCheersSuccessTip() {
        Log.d("sendCheers", "发送获取饮水记录广播");
        HdUtil.broadcast(mContext, Const.NOTIFY_GET_LATEST_DRINK_RECORD, null);
    }

    /**
     * If you attempt to close the APP or when the connection has disconnected,
     * you should cancel the thread through calling this function.
     */
    public void cancel() {
        mIsRun = false;
        mQueue.destroy();
        Log.i(TAG, "We have stoped the BleParseMsg thread.");
    }

    /**
     * The messaging function
     *
     * @param what    The handler code defined in the Constants
     * @param message The message received form BLE
     */
    private void broadcastUpdate(final int what, Object message) {
        Message msg = new Message();
        msg.what = what;
        msg.obj = message;
        mHandler.sendMessage(msg);
        Log.i(TAG, "broadcastUpdate: " + what + "---->"+((BleHandler)mHandler).getActivity());
    }

    /**
     * The messaging function
     *
     * @param what The handler code defined in the Constants
     * @param para The integer what you want to transfer
     */
    private void broadcastUpdate(final int what, int para) {
        Message msg = new Message();
        msg.what = what;
        msg.arg1 = para;
        mHandler.sendMessage(msg);
        if (D)
            Log.i(TAG, "broadcastUpdate: " + what);
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
