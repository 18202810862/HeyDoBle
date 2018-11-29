package com.iloof.heydoblelibrary.thread;

import android.content.Intent;
import android.util.Log;

import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.app.HdApplication;
import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.BleHelper;
import com.iloof.heydoblelibrary.heydos1.BleCmdSetS1;
import com.iloof.heydoblelibrary.util.BleImg4Send;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import static com.iloof.heydoblelibrary.BleConstant.D;

/**
 * 发送图片线程
 *
 * @author UESTC-PRMI-Burial
 * @date 2015-02-05
 */
//@Deprecated
public class BleUpdateImgThread extends Thread {
    private final static String TAG = "BleWriteImg";

    private List<BleImg4Send> imgList = new ArrayList<BleImg4Send>();
    private int curIndexOfImg = BleConstant.ERROR_INTEGER;

    private boolean isRun = true;
    private boolean isWrong = false;

    private BleHelper helper;

    /**
     * The inner lock
     */
    private Lock mInnerLock = new ReentrantLock();
    /**
     * The inner condition corresponding to the inner lock
     */
    private Condition mInnerCondition = mInnerLock.newCondition();

    /**
     * The constructor function.
     *
     * @param img4Send
     */
    public BleUpdateImgThread(BleImg4Send img4Send, BleHelper helper) {
        if (!addImg(img4Send)) {
            isRun = false;
            isWrong = true;
            this.helper = helper;
        }
    }

    /**
     * Add the image into the list.
     *
     * @param img4Send The image ready to send.
     * @return True if success.
     * @see BleImg4Send
     */
    public boolean addImg(BleImg4Send img4Send) {
        if (img4Send.isValid()) {
            imgList.add(img4Send);
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
     * Delete the image with the special index
     *
     * @param indexOfImg The index of image
     */
    public void delImg(int indexOfImg) {
        if ((curIndexOfImg == indexOfImg) || (-1 == indexOfImg)) {
            isWrong = true;
            indexOfImg = curIndexOfImg;
        }
        for (int i = 0; i < imgList.size(); ++i) {
            if (indexOfImg == imgList.get(i).getIndex()) {
                imgList.remove(i);
                if (D)
                    Log.i(TAG, "The image " + indexOfImg + " is deleted.");
                return;
            }
        }
    }

    /**
     * We want to alter the value of isWrong.
     *
     * @param indexOfImg The value.
     */
    public void setWrongFlag(int indexOfImg) {
        if ((curIndexOfImg == indexOfImg) || (-1 == indexOfImg)) {
            isWrong = true;
        }
    }

    /**
     * We want to suspend the thread.
     */
    public void pause() {

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
        isRun = false;
        isWrong = true;

        imgList.clear();
        imgList = null;
        if (D)
            Log.i(TAG, "The BleSendImgThread is cancelled.");
    }

    @Override
    public void run() {
        while (isRun) {
            if (imgList.isEmpty()) {
                pause();
            }

            for (int i = 0; i < imgList.size(); ++i) {
                curIndexOfImg = imgList.get(i).getIndex();
                if (writeImg(imgList.get(i))) { // send the picture until it
                    // broadcastUpdate(what)
                    // has send.
                    Intent intent = new Intent(
                            Const.NOTIFY_BLUETOOTH_SENDPIC_FIN);
                    HdApplication.getInstance().sendBroadcast(intent);
                    imgList.remove(i); // delete the picture after the
                    // picture has send or wrong.
                }

            }

            curIndexOfImg = BleConstant.ERROR_INTEGER;
        }
    }

    /**
     * Return false if something is wrong.
     *
     * @param img The data of image ready to send.
     * @return Return false if something is wrong.
     */
    private boolean writeImg(BleImg4Send img) {

        int curPackage = 0;
        int tolPackage = img.getTolPackNum();
        while (curPackage != tolPackage) {

            helper.sendCmdToBle(BleCmdSetS1.getCmdOfWrtPic(img.getIndex(), curPackage,
                    img.getPackage(curPackage)));
            // if (curPackage == 0) {
            // sendCmdToBle(BleCmdSet.getFirstPackageCmdOfWriteFile(
            // img.getIndex(), img.getPackage(curPackage)));
            // } else {
            // sendCmdToBle(BleCmdSet.getOtherPackageCmdOfWriteFile(
            // img.getIndex(), curPackage,
            // img.getPackage(curPackage)));
            // }

            ++curPackage;
            // Intent intent = new
            // Intent(Const.NOTIFY_BLUETOOTH_SENDPICING);
            // intent.putExtra("progress", curPackage);
            // sendBroadcast(intent);
            if (D)
                Log.i(TAG, "WritePicture: " + curPackage + "/" + tolPackage);
            if (isWrong) {
                isWrong = false;
                if (D)
                    Log.i(TAG, "WritePicture: UnDone!");
                return false;
            }
        }

        if (D)
            Log.i(TAG, "WritePicture: Done!");
        return true;
    }
}
