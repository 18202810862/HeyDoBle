package com.iloof.heydoblelibrary.heydos1;

import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.util.BleFileSend;

/**
 * Created by chentao on 2017/9/14.
 */

public class BleFileSendS1 extends BleFileSend {


    public BleFileSendS1(int index, byte[] data) {
        super(index, data);
        tolPackage = data.length % Const.S1_SEND_FILE_LENGTH == 0 ? data.length / Const.S1_SEND_FILE_LENGTH
                : data.length / Const.S1_SEND_FILE_LENGTH + 1;
    }

    @Override
    protected int getSizeOfPackage(int dataLen, int index) {
        int sizeOfPackage;
        if (dataLen - index * Const.S1_SEND_FILE_LENGTH < Const.S1_SEND_FILE_LENGTH) {
            sizeOfPackage = dataLen - index * Const.S1_SEND_FILE_LENGTH;
        } else {
            sizeOfPackage = Const.S1_SEND_FILE_LENGTH;
        }
        return sizeOfPackage;
    }

    @Override
    protected byte[] getRePackage(int index, int sizeOfPackage) {
        byte[] rePackage = new byte[sizeOfPackage];
        System.arraycopy(dataOfImg, index * Const.S1_SEND_FILE_LENGTH, rePackage, 0,
                sizeOfPackage);
        return rePackage;
    }
}
