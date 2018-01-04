package com.iloof.heydoblelibrary.util;

import com.iloof.heydoblelibrary.BleConstant;

/**
 * 打包发送文件数据
 *
 * @author lbc1234
 */
public abstract class BleFileSend {
    public byte[] dataOfImg;
    public int tolPackage;
    public int index;

    public BleFileSend(int index, byte[] data) {
        if (null == data) {
            dataOfImg = null;
            this.index = BleConstant.ERROR_INTEGER;
            tolPackage = BleConstant.ERROR_INTEGER;
            return;
        }
        this.index = index;

        dataOfImg = new byte[data.length];
        System.arraycopy(data, 0, dataOfImg, 0, data.length);

//        if (preference.getInt(Const.STYLE) == 0) {
//            /** ST-1 发送*/
//            tolPackage = data.length % 1024 == 0 ? data.length / 1024
//                    : data.length / 1024 + 1;
//        } else {
//            /** 儿童水杯发送 发送*/
//            tolPackage = data.length % 192 == 0 ? data.length / 192
//                    : data.length / 192 + 1;
//        }


    }

    public int getTolPackNum() {
        return tolPackage;
    }

    public int getIndex() {
        return index;
    }

    public boolean isValid() {
        return (null != dataOfImg);
    }

    public byte[] getPackage(int index) {
        byte[] rePackage = null;

        if ((null == dataOfImg) || (index < 0) || (index >= tolPackage)) {
            return rePackage;
        }

        int dataLen = dataOfImg.length;
        // 第index包数据长度
        int sizeOfPackage = 0;


        sizeOfPackage = getSizeOfPackage(dataLen,index);


        rePackage = getRePackage(index,sizeOfPackage);

        return rePackage;
    }

    protected abstract int getSizeOfPackage( int dataLen,int index);

    protected abstract byte[] getRePackage( int index,int sizeOfPackage);

}

