package com.iloof.heydoblelibrary.util;

import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;

import static com.iloof.heydoblelibrary.util.BleCheckUtil.getCrc16;
import static com.iloof.heydoblelibrary.util.BleTransfer.Hex2Deci;

/**
 * 解析从水杯上收到的命令
 *
 * @author UESTC-PRMI-Burial
 * @date 2014-12-16
 */
public class BleMessage {

    private static String TAG = "BleMessage";

    /**
     * The whole message bytes
     */
    public byte[] msgBytes = null;
    /**
     * The Head Code
     */
    public byte head = 0x00;
    /**
     * The Equipment Code
     */
    public byte[] equCode = new byte[BleConstant.EQ_SIZE];
    /**
     * The Total Package
     */
    public int totalPackages = BleConstant.ERROR_INTEGER;
    /**
     * The Current Package
     */
    public int currentPackage = BleConstant.ERROR_INTEGER;
    /**
     * The Ack Code
     */
    public boolean isSuccess = false;
    /**
     * The Index Of Command
     */
    public int index = BleConstant.ERROR_INTEGER;
    /**
     * The Data
     */
    public byte[] data = null;
    /**
     * The End Code
     */
    public byte end = 0x00;
    /**
     * The accuracy of the message
     */
    public boolean isRight = false;

    /**
     * 空构造函数
     */
    public BleMessage() {

    }

    /**
     * The Constructor for BleMessage
     *
     * @param message The whole message. You should to be careful that the
     *                message must be the whole message.Or Something will be
     *                wrong.
     */
    public BleMessage(byte[] message) {
        msgBytes = message;

        int offset = 0;
        byte[] temp = null;
        // head
        head = message[offset++];
        // equipment code
        System.arraycopy(message, offset, equCode, 0, BleConstant.EQ_SIZE);
        offset += BleConstant.EQ_SIZE;
        // package sn
        temp = new byte[BleConstant.SN_SIZE];
        System.arraycopy(message, offset, temp, 0, BleConstant.SN_SIZE);
        totalPackages = temp[0];
        if (totalPackages < 0) {
            totalPackages += 256;
        }
        currentPackage = temp[1];
        if (currentPackage < 0) {
            currentPackage += 256;
        }
        offset += BleConstant.SN_SIZE;
        // ack code
        if (BleConstant.ACK_CODE == message[offset++]) {
            isSuccess = true;
        }
        // Index 判断接收的是什么命令
        index = BleConstant.ERROR_INTEGER;
        temp = new byte[BleConstant.ID_SIZE];
        System.arraycopy(message, offset, temp, 0, BleConstant.ID_SIZE);

        for (int i = 0; i < BleConstant.INDEX_ARRAY.length; ++i) {
            if ((BleConstant.INDEX_ARRAY[i][0] == temp[0])
                    && (BleConstant.INDEX_ARRAY[i][1] == temp[1])) {
                index = i;
                break;
            }
        }


        offset += BleConstant.ID_SIZE;
        // data length
        temp = new byte[BleConstant.DL_SIZE];
        System.arraycopy(message, offset, temp, 0, BleConstant.DL_SIZE);
        int dataLength = Hex2Deci(temp, false);
        offset += BleConstant.DL_SIZE;
        // data
        if ((dataLength < 0) || (dataLength > BleConstant.CMD_MAX_SIZE)) {
            dataLength = BleConstant.ERROR_INTEGER;
            data = null;
        } else if (dataLength == 0) {
            data = null;
        } else {
            data = new byte[dataLength];
            System.arraycopy(message, offset, data, 0, dataLength);
            offset += dataLength;
        }

        // crc
        temp = getCrc16(message, BleConstant.HD_SIZE, message.length - 1
                - BleConstant.CK_SIZE - BleConstant.ED_SIZE);

        // is right
        isRight = ((null != temp) && (temp[0] == message[offset++])
                && (temp[1] == message[offset++])
                && (index != BleConstant.ERROR_INTEGER) && (dataLength != BleConstant.ERROR_INTEGER));

        // end
        end = message[offset];
    }

    /**
     * The copy constructor for BleMessage
     *
     * @param msg The BleMessage's instance to copy
     */
    public BleMessage(BleMessage msg) {
        if (msg == null) {
            Log.w(TAG, "The message is null!");
        }
        msgBytes = msg.msgBytes;
        head = msg.head; // head
        System.arraycopy(msg.equCode, 0, equCode, 0, BleConstant.EQ_SIZE); // equipment
        // code
        totalPackages = msg.totalPackages;
        currentPackage = msg.currentPackage;
        isSuccess = msg.isSuccess;
        index = msg.index;
        if ((null == msg.data) || (0 == msg.data.length)) {
            data = null;
        } else {
            data = new byte[msg.data.length];
            System.arraycopy(msg.data, 0, data, 0, data.length);
        }
        end = msg.end;
        isRight = msg.isRight;
    }

    @Override
    public String toString() {

        final StringBuilder stringBuilder = new StringBuilder(
                msgBytes.length);
        for (byte byteChar : msgBytes)
            stringBuilder.append(String.format("%02X ", byteChar));
        String strMessage = stringBuilder.toString();
        // broadcastUpdate(BleConstant.HM_DEBUG_CMD, strMessage);
        return strMessage;
    }

}
