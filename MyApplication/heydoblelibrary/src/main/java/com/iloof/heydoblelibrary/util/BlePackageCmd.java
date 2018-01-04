package com.iloof.heydoblelibrary.util;

import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;

import static com.iloof.heydoblelibrary.util.BleCheckUtil.getCrc16;
import static com.iloof.heydoblelibrary.util.BleTransfer.Deci2Hex;
import static com.iloof.heydoblelibrary.util.BleTransfer.encoding;

/**
 * Created by chentao on 2017/9/14.
 */

public class BlePackageCmd {

    private static String TAG = "BlePackageCmd";

    /**
     * 打包命令(0x53、0x54要转义)
     *
     * @param packageSN    The pack_sn, 2 bytes. Null is equivalent to {0x00,
     *                     0x00}.Please refer to {@link }.
     * @param indexOfOrder The index of {@link BleConstant#INDEX_ARRAY}
     * @param data         The DATA
     * @param dataLen      The length of the DATA
     * @return Return the command which include the spacial command index, data
     * and pack_sn, Or null if something is wrong.
     */
    public static byte[] packagingCommand(byte[] packageSN, int indexOfOrder,
                                          byte[] data, int dataLen) {
        byte[] command = null;
        Log.i("packagingCommand", "packageSN = null =" + (packageSN == null));


        // Checking the input parameter
        if (packageSN != null){
            if (packageSN.length != BleConstant.SN_SIZE
                    || ((indexOfOrder < 0) || (indexOfOrder >= BleConstant.INDEX_ARRAY.length))){
                return command; // return null.
            }
        }
//        if (((packageSN != null) && (packageSN.length != BleConstant.SN_SIZE))
//                || ((indexOfOrder < 0) || (indexOfOrder >= BleConstant.INDEX_ARRAY.length))) {
//            Log.w(TAG, "Something is wrong with the input parameter!");
//            return command; // return null.
//        }
        if ((data == null) && (dataLen != 0)) {
            Log.w(TAG, "Something is wrong with the input parameter!");
            return command; // return null.
        }
        if ((data != null) && ((dataLen > data.length) || (dataLen < 0))) {
            Log.w(TAG, "Something is wrong with the input parameter!");
            return command; // return null.
        }

        int offset = 0;
        // Getting The Command Length
        int tempCmdLen = dataLen + BleConstant.CMD_MIN_SIZE
                - BleConstant.HD_SIZE - BleConstant.ED_SIZE;

        // Initialize the temporary command array with the special length
        byte[] tempCmd = new byte[tempCmdLen];

        // Equipment Code, Ignore the head code
        if (null == BleConstant.EQU_CODE) {
            Log.w(TAG, "The equipment code is null!");
            return command;
        }
        System.arraycopy(BleConstant.EQU_CODE, 0, tempCmd, offset,
                BleConstant.EQ_SIZE);
        offset += BleConstant.EQ_SIZE;
        // pack_sn Code
        if (packageSN == null) {
            packageSN = new byte[]{0x00, 0x00};
        }
        System.arraycopy(packageSN, 0, tempCmd, offset, BleConstant.SN_SIZE);
        offset += BleConstant.SN_SIZE;

        // Index Of Command


        System.arraycopy(BleConstant.INDEX_ARRAY[indexOfOrder], 0, tempCmd,
                offset, BleConstant.ID_SIZE);

        offset += BleConstant.ID_SIZE;
        // Ack Code
        tempCmd[offset++] = BleConstant.ACK_CODE;
        // Data Length And DATA
        if (null != data) {
            System.arraycopy(Deci2Hex(dataLen, BleConstant.DL_SIZE), 0,
                    tempCmd, offset, BleConstant.DL_SIZE);
            offset += BleConstant.DL_SIZE;

            System.arraycopy(data, 0, tempCmd, offset, data.length);
            offset += data.length;
        } else { // dataLen == 0 And data == null
            tempCmd[offset++] = 0x00;
            tempCmd[offset++] = 0x00;
        }
        // Check
        byte[] crc = getCrc16(tempCmd, offset);
        System.arraycopy(crc, 0, tempCmd, offset, BleConstant.CK_SIZE);
        offset += BleConstant.CK_SIZE;
        // Encode the command
        tempCmd = encoding(tempCmd);
        if (null == tempCmd) {
            Log.w(TAG, "Something is wrong with the process of encode.");
            return command;
        }
        // The length of the command which has encoded.
        tempCmdLen = tempCmd.length;

        int cmdLength = tempCmdLen + BleConstant.HD_SIZE + BleConstant.ED_SIZE;
        command = new byte[cmdLength];
        // Head Code And End Code
        command[0] = BleConstant.HEAD_CODE;
        command[cmdLength - 1] = BleConstant.END_CODE;
        // Copying the command Body ignore the head and end code.
        System.arraycopy(tempCmd, 0, command, BleConstant.HD_SIZE, tempCmdLen);

        return command;
    }
}
