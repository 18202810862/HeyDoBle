package com.iloof.heydoblelibrary.util;

import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;

/**
 * Created by chentao on 2017/9/14.
 */

public class BleTransfer {

    /**
     * The Size(Bite) Of Each Byte
     */
    private final static int BITE_OF_BYTE = 8;

    private static String TAG = "BleTransfer";


    /**
     * 遍历命令编码。依下列各项: 0X54 → 0X52 0X00; 0X53 → 0X52
     * 0X01; 0X52 → 0X52 0X02;
     *
     * @param command The command to encode.
     * @return The command what Complete encode. Or return null if the command
     * is null or the length of command is zero.
     */
    public static byte[] encoding(byte[] command) {
        byte[] retCmd = null;

        if ((command == null) || (command.length == 0)
                || (command.length > BleConstant.CMD_MAX_SIZE)) {
            Log.i(TAG, "The command for encode is null");
            return retCmd;
        }

        byte[] tempCmd = new byte[BleConstant.CMD_MAX_SIZE];
        int tempLength = 0;
        // Traverse the command for encode
        for (byte everyByte : command) {
            switch (everyByte) {
                case 0x52:
                    tempCmd[tempLength++] = 0x52;
                    tempCmd[tempLength++] = 0x02;
                    break;
                case 0x53:
                    tempCmd[tempLength++] = 0x52;
                    tempCmd[tempLength++] = 0x01;
                    break;
                case 0x54:
                    tempCmd[tempLength++] = 0x52;
                    tempCmd[tempLength++] = 0x00;
                    break;
                default:
                    tempCmd[tempLength++] = everyByte;
                    break;
            }
        }

        retCmd = new byte[tempLength];
        System.arraycopy(tempCmd, 0, retCmd, 0, tempLength);

        return retCmd;
    }

    /**
     * 解码后从BLE接收到的信息忽略头部和结束代码. 0X52
     * 0X00 → 0X54; 0X52 0X01 → 0X53; 0X52 0X02 → 0X52;
     *
     * @param message The message received from BLE
     * @return The message what Complete decode.
     */
    public static byte[] decoding(final byte[] message) {
        byte[] retMsg = null;
        int msgOffset = 0;
        int retOffset = 0;

        if ((message == null) || (message.length == 0)
                || (message.length >= BleConstant.CMD_MAX_SIZE)) {
            Log.w(TAG, "Something is wrong with the message");
            return retMsg;
        }

        if ((message[msgOffset] != BleConstant.HEAD_CODE)
                || (message[message.length - 1] != BleConstant.END_CODE)) {
            Log.w(TAG, "The head code Or the end code is wrong");
            return null;
        }

        byte[] tempMsg = new byte[BleConstant.CMD_MAX_SIZE];

        for (; msgOffset < message.length; ++msgOffset) {
            if (message[msgOffset] == 0x52) {
                if (msgOffset == (message.length - 1)) {
                    Log.w(TAG, "Something is wrong with the command");
                    break;
                }

                switch (message[++msgOffset]) {
                    case 0x00:
                        tempMsg[retOffset++] = 0x54;
                        break;
                    case 0x01:
                        tempMsg[retOffset++] = 0x53;
                        break;
                    case 0x02:
                        tempMsg[retOffset++] = 0x52;
                        break;
                    default:
                        Log.w(TAG, "Something is wrong with the message");
                        return null;
                }
            } else {
                tempMsg[retOffset++] = message[msgOffset];
            }
        }

        retMsg = new byte[retOffset];

        System.arraycopy(tempMsg, 0, retMsg, 0, retOffset);

        return retMsg;
    }


    /**
     * 十六进制转换为整数
     *
     * @param singleByte The byte to convert.
     * @param isSigned   True if the integer is signed, Or false.
     * @return Integer corresponding with byteArray.
     */
    public static int Hex2Deci(final byte singleByte, boolean isSigned) {
        int integer = BleConstant.ERROR_INTEGER;
        integer = (int) singleByte;
        if (!isSigned && (integer < 0)) {
            integer += 256;
        }

        return integer;
    }

    /**
     * 十六进制转换为整数
     *
     * @param bytes    The bytes array to convert.
     * @param isSigned True if the integer is signed, Or false.
     * @return Integer corresponding with byteArray.
     */
    public static int Hex2Deci(final byte[] bytes, boolean isSigned) {
        if ((null == bytes) || (bytes.length <= 0)
                || (bytes.length > BleConstant.TS_SIZE)) {
            Log.e(TAG, "Hex2Deci:	The length of parameter out of range");
            return BleConstant.ERROR_INTEGER;
        }
        int integer = 0x00000000;
        int offset = 0;

        byte mask = (byte) 0x80;
        isSigned = (isSigned && (mask == (mask & bytes[offset])));

        byte temp = bytes[offset++];
        int iMask = 0x00000001;
        for (int i = 0; i < BITE_OF_BYTE; ++i) {
            integer <<= 1;
            if (mask == (mask & temp)) {
                integer |= iMask;
            }
            temp <<= 1;
        }
        while (offset < bytes.length) {
            temp = bytes[offset++];
            for (int i = 0; i < BITE_OF_BYTE; ++i) {
                integer <<= 1;
                if (mask == (mask & temp)) {
                    integer |= iMask;
                }
                temp <<= 1;
            }
        }

        if (isSigned && (BleConstant.TS_SIZE != bytes.length)) { // minus
            int[] carry = {1, 256, 65536, 16777216};
            integer -= carry[bytes.length];
        }

        return integer;
    }

    /**
     * 整数转换为十六进制（转换的是有符号的整形）
     *
     * @param deciNum  The Integer to convert.
     * @param byteSize The length of byte array.
     * @return The byte array corresponding with Integer
     */
    public static byte[] Deci2Hex(final int deciNum, int byteSize) {
        switch (byteSize) {
            case 1:
                if ((deciNum < -128) || (deciNum > 127)) {
                    Log.e(TAG, "convertToHex:	the size of byte array out of range");
                    return null;
                }
                break;
            case 2:
                if ((deciNum < -32768) || (deciNum > 32767)) {
                    Log.e(TAG,
                            "convertToHex:	the size of byte array out of range deciNum:"
                                    + deciNum);
                    return null;
                }
                break;
            case 3:
                if ((deciNum < -8388608) || (deciNum > 8388607)) {
                    Log.e(TAG, "convertToHex:	the size of byte array out of range");
                    return null;
                }
                break;
            case 4:
                if ((deciNum < -2147483648) || (deciNum > 2147483647)) {
                    Log.e(TAG, "convertToHex:	the size of byte array out of range");
                    return null;
                }
                break;
            default:
                Log.e(TAG, "convertToHex:	the size of byte array out of range");
                return null;
        }

        byte[] hexInteger = new byte[byteSize];
        int tempInt = deciNum;
        int mask_4b = 0x80000000;
        byte mask = 0x01;

        tempInt <<= ((4 - byteSize) * BITE_OF_BYTE);
        for (int offset = 0; offset < byteSize; ++offset) {
            for (int i = 0; i < BITE_OF_BYTE; ++i) {
                hexInteger[offset] <<= 1;
                if (mask_4b == (mask_4b & tempInt)) {
                    hexInteger[offset] |= mask;
                }
                tempInt <<= 1;
            }
        }

        return hexInteger;
    }

    /**
     * 整数转换为十六进制
     *
     * @param deciNum The Integer to convert.
     * @return The byte array corresponding with Integer.Equal to
     * Deci2Hex(deciNum, 1)
     */
    public static byte Deci2Hex(final int deciNum) {
        int byteSize = 1;
        byte hexInteger = BleConstant.sinByteInvalid;

//		byte hexInteger = -1;
        if ((deciNum < -128) || (deciNum > 127)) {
            Log.e(TAG, "convertToHex:	the size of byte array out of range");
            return hexInteger;
        }

        int tempInt = deciNum;
        int mask_4b = 0x80000000;
        byte mask = 0x01;

        tempInt <<= ((4 - byteSize) * BITE_OF_BYTE);
        for (int i = 0; i < BITE_OF_BYTE; ++i) {
            hexInteger <<= 1;
            if (mask_4b == (mask_4b & tempInt)) {
                hexInteger |= mask;
            }
            tempInt <<= 1;
        }

        return hexInteger;
    }

}
