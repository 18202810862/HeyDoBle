package com.iloof.heydoblelibrary.heydoc1;

import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.util.BleFileSend;

/**
 * Created by chentao on 2017/9/14.
 */

public class BleFileSendC1 extends BleFileSend {

    public BleFileSendC1(int index, byte[] data) {
        super(index, data);

        /** 儿童水杯发送 发送*/
        tolPackage = data.length % Const.C1_SEND_FILE_LENGTH == 0 ? data.length / Const.C1_SEND_FILE_LENGTH
                : data.length / Const.C1_SEND_FILE_LENGTH + 1;
    }

    @Override
    protected int getSizeOfPackage(int dataLen, int index) {

        int sizeOfPackage;
        if (dataLen - index * Const.C1_SEND_FILE_LENGTH < Const.C1_SEND_FILE_LENGTH) {
                sizeOfPackage = dataLen - index * Const.C1_SEND_FILE_LENGTH;
            } else {
                sizeOfPackage = Const.C1_SEND_FILE_LENGTH;
            }
        return sizeOfPackage;
    }

    @Override
    protected byte[] getRePackage(int index, int sizeOfPackage) {

        byte[] rePackage = new byte[sizeOfPackage];
        System.arraycopy(dataOfImg, index * Const.C1_SEND_FILE_LENGTH, rePackage, 0,
                sizeOfPackage);
        return rePackage;
    }


}
