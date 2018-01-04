package com.iloof.heydoblelibrary.heydos1new;

import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.util.BleFileSend;

/**
 * Created by chentao on 2017/9/14.
 */

public class BleFileSendS1S extends BleFileSend {

    public BleFileSendS1S(int index, byte[] data) {
        super(index, data);

        /** S1-S水杯发送 发送*/
        tolPackage = data.length % Const.S1S_SEND_FILE_LENGTH == 0 ? data.length / Const.S1S_SEND_FILE_LENGTH
                : data.length / Const.S1S_SEND_FILE_LENGTH + 1;
    }

    @Override
    protected int getSizeOfPackage(int dataLen, int index) {

        int sizeOfPackage;
        if (dataLen - index * Const.S1S_SEND_FILE_LENGTH < Const.S1S_SEND_FILE_LENGTH) {
                sizeOfPackage = dataLen - index * Const.S1S_SEND_FILE_LENGTH;
            } else {
                sizeOfPackage = Const.S1S_SEND_FILE_LENGTH;
            }
        return sizeOfPackage;
    }

    @Override
    protected byte[] getRePackage(int index, int sizeOfPackage) {

        byte[] rePackage = new byte[sizeOfPackage];
        System.arraycopy(dataOfImg, index * Const.S1S_SEND_FILE_LENGTH, rePackage, 0,
                sizeOfPackage);
        return rePackage;
    }


}
