package com.iloof.heydoblelibrary.thread;

import android.content.Intent;
import android.util.Log;

import com.iloof.heydoblelibrary.BleHelper;
import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.app.HdApplication;
import com.iloof.heydoblelibrary.heydoc1.BleCmdSetC1;
import com.iloof.heydoblelibrary.heydos1.BleCmdSetS1;
import com.iloof.heydoblelibrary.heydos1new.BleCmdSetS1S;
import com.iloof.heydoblelibrary.util.BleFileSend;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.iloof.heydoblelibrary.BleConstant.D;

/**
 * Created by chentao on 2017/9/11.
 */


public class BleUpdateFileThread extends Thread {
    private final static String TAG = "BleWriteImg";

    private BleFileSend mFile4Send;
    private boolean isRun = true;
    private boolean isWrong = false;

    /**
     * The inner lock
     */
    private Lock mInnerLock = new ReentrantLock();
    /**
     * The inner condition corresponding to the inner lock
     */
    private Condition mInnerCondition = mInnerLock.newCondition();
    /**
     * 是否停止发送
     */
    private boolean isStop2send = false;

    BleHelper helper;

    SendThread mSendThread;

    int style;

    /**
     * The constructor function.
     *
     * @param file4Send
     */
    public BleUpdateFileThread(BleFileSend file4Send, BleHelper helper, SendThread mSendThread, int style) {
        this.helper = helper;
        this.mSendThread = mSendThread;
        this.style = style;

        if (!addFile(file4Send)) {
            isRun = false;
            isWrong = true;

        }
    }

    /**
     * Add the image into the list.
     *
     * @param file4Send The image ready to send.
     * @return True if success.
     */
    public boolean addFile(BleFileSend file4Send) {
        if (file4Send.isValid()) {
            isStop2send = false;
            mFile4Send = file4Send;
            // fileList.add(file4Send);
            isRun = true;
            isWrong = false;

            mInnerLock.lock();
            mInnerCondition.signalAll();
            mInnerLock.unlock();

            return true;
        }
        return false;
    }

    /**
     * We want to suspend the thread.
     */
    public void pause() {
        Log.w("updateImageTest", "线程暂停");
        mInnerLock.lock();
        try {
            if (D)
                Log.i(TAG, "The BleSendImgThread is paused.");
            mInnerCondition.await();
        } catch (Exception e) {
            e.printStackTrace();
            if (D)
                Log.i(TAG, "BleSendImgThread: await() Fails.");
        }
        mInnerLock.unlock();
    }

    /**
     * We want to cancel the thread.
     */
    public void cancel() {
        Log.w("updateImageTest", "线程取消");
        isRun = false;
        isWrong = true;
        mFile4Send = null;
        stopCurrentSend();
        if (mSendThread != null)
            mSendThread.clear();
        if (D)
            Log.i(TAG, "The BleSendImgThread is cancelled.");
    }

    @Override
    public void run() {
        while (isRun) {
            if (mFile4Send == null) {
                pause();
            }

            // for (int i = 0; i < fileList.size(); ++i) {
            if (writeFile(mFile4Send)) { // send the picture until
                // it
                // broadcastUpdate(what)
                // has send.
                Intent intent = new Intent(
                        Const.NOTIFY_BLUETOOTH_SENDPIC_FIN);
                HdApplication.getInstance().sendBroadcast(intent);
                mFile4Send = null; // delete the picture after the
                // picture has send or wrong.
            }

            // }

        }
    }

    /**
     * 停止当前发送
     */
    public void stopCurrentSend() {
        isStop2send = true;
    }

    /**
     * Return false if something is wrong.
     *
     * @param file The data of image ready to send.
     * @return Return false if something is wrong.
     */
    private boolean writeFile(BleFileSend file) {
        Log.d(TAG, "call--writeFile");
        int curPackage = 0;
        int tolPackage = file.getTolPackNum();
        while (curPackage != tolPackage) {
            if (!isStop2send) {

                if (style == 0) {
                    if (curPackage == 0) {
                        // 发送文件首包 要数据地址,其余包不带
                        helper.sendCmdToBle(BleCmdSetS1.getFirstPackageCmdOfWriteFile(
                                file.getIndex(), file.getPackage(curPackage)));
                    } else {

                        Log.i("writeFile", "curPackage =" + curPackage);
                        helper.sendCmdToBle(BleCmdSetS1.getOtherPackageCmdOfWriteFile(
                                curPackage, file.getPackage(curPackage)));
                    }
                } else if (style == 1) {
                    /** 儿童水杯*/
                    Log.i("writeFile", "curPackage =" + curPackage);
                    helper.sendCmdToBle(BleCmdSetC1.getPackageCmdOfWriteFile(
                            file.getIndex() + curPackage * Const.C1_SEND_FILE_LENGTH, curPackage, file.getPackage(curPackage)));

                } else {
                    helper.sendCmdToBle(BleCmdSetS1S.getPackageCmdOfWriteFile(
                            file.getIndex() + curPackage * Const.S1S_SEND_FILE_LENGTH, curPackage, file.getPackage(curPackage)));
                }


                ++curPackage;

                if (D)
                    Log.i(TAG, "WritePicture: " + curPackage + "/"
                            + tolPackage);
                if (isWrong) {
                    isWrong = false;
                    if (D)
                        Log.i(TAG, "WritePicture: UnDone!");

                    return false;
                }
            }
        }

        if (D)
            Log.i(TAG, "WritePicture: Done!");
        return true;
    }

}
