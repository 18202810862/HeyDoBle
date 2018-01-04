package com.iloof.heydoblelibrary.util;

import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;

import static com.iloof.heydoblelibrary.util.BleCheckUtil.getCrc16;
import static com.iloof.heydoblelibrary.util.BleTransfer.Hex2Deci;
import static com.iloof.heydoblelibrary.util.BleTransfer.decoding;

/**
 * 解析APP发送到水杯命令
 *
 * @author UESTC-PRMI-Burial
 * @date 2014-12-19
 */
public class BleCommand {

    private static String TAG = "BleCommand";

    /**
     * The whole command bytes after decode
     */
    private byte[] cmdBytes = null;
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
     * The Index Of Command
     */
    public int index = BleConstant.ERROR_INTEGER;
    /**
     * The Ack Code
     */
    public byte ackCode = BleConstant.sinByteInvalid;
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

    public static int reSendCount = 0;

    /**
     * The Constructor for BleCommand
     *
     * @param command The whole command. You should to be careful that the
     *                command must be the whole command.Or Something will be
     *                wrong.
     */
    public BleCommand(byte[] command) {
        int offset = 0;
        byte[] temp = null;

        cmdBytes = command;

        command = decoding(command);

        // head
        head = command[offset++];
        // equipment code
        System.arraycopy(command, offset, equCode, 0, BleConstant.EQ_SIZE);
        offset += BleConstant.EQ_SIZE;
        // package sn
        temp = new byte[BleConstant.SN_SIZE];
        System.arraycopy(command, offset, temp, 0, BleConstant.SN_SIZE);
        totalPackages = temp[0];
        if (totalPackages < 0) {
            totalPackages += 256;
        }
        currentPackage = temp[1];
        if (currentPackage < 0) {
            currentPackage += 256;
        }
        offset += BleConstant.SN_SIZE;
        // Index
        index = BleConstant.ERROR_INTEGER;
        temp = new byte[BleConstant.ID_SIZE];
        System.arraycopy(command, offset, temp, 0, BleConstant.ID_SIZE);
        for (int i = 0; i < BleConstant.INDEX_ARRAY.length; ++i) {
            if ((BleConstant.INDEX_ARRAY[i][0] == temp[0])
                    && (BleConstant.INDEX_ARRAY[i][1] == temp[1])) {
                index = i;
                break;
            }
        }


        offset += BleConstant.ID_SIZE;
        // ack code
        ackCode = command[offset++];
        // data length
        temp = new byte[BleConstant.DL_SIZE];
        System.arraycopy(command, offset, temp, 0, BleConstant.DL_SIZE);
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
            System.arraycopy(command, offset, data, 0, dataLength);
            offset += dataLength;
        }

        // crc
        temp = getCrc16(command, BleConstant.HD_SIZE, command.length - 1
                - BleConstant.CK_SIZE - BleConstant.ED_SIZE);

        // is right
        isRight = ((null != temp) && (temp[0] == command[offset++])
                && (temp[1] == command[offset++])
                && (index != BleConstant.ERROR_INTEGER) && (dataLength != BleConstant.ERROR_INTEGER));

        // end
        end = command[offset];
    }

    /**
     * The copy constructor for BleCommand
     *
     * @param cmd The BleCommand instance to copy
     */
    public BleCommand(BleCommand cmd) {
        if (cmd == null) {
            Log.w(TAG, "The command is null!");
            return;
        }
        cmdBytes = cmd.cmdBytes;
        head = cmd.head; // head
        System.arraycopy(cmd.equCode, 0, equCode, 0, BleConstant.EQ_SIZE); // equipment
        // code
        totalPackages = cmd.totalPackages;
        currentPackage = cmd.currentPackage;
        index = cmd.index;
        ackCode = cmd.ackCode;
        if ((null == cmd.data) || (0 == cmd.data.length)) {
            data = null;
        } else {
            data = new byte[cmd.data.length];
            System.arraycopy(cmd.data, 0, data, 0, data.length);
        }
        end = cmd.end;
        isRight = cmd.isRight;
    }

    /**
     * Return the bytes about command
     *
     * @return Return the array of bytes
     */
    public byte[] getBytes() {
        // byte[] bytes = null;
            /*
             * if (cmdBytes != null) { bytes = encoding(cmdBytesAfDecode); }
			 */
        ++reSendCount;
        return cmdBytes;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder(
                cmdBytes.length);
        for (byte byteChar : cmdBytes)
            stringBuilder.append(String.format("%02X ", byteChar));

        return stringBuilder.toString();
    }

}
