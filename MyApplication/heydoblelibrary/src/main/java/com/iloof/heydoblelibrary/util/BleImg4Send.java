package com.iloof.heydoblelibrary.util;

import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.BleUtil;

/**
 * 打包要发送的图片数据
 *
 * @author UESTC-PRMI-Burial
 * @date 2015-02-05
 */
public class BleImg4Send {
    private byte[] dataOfImg;
    private int tolPackage;
    private int sizeOfPackage;
    private int indexOfImg;

    public BleImg4Send(BleUtil.WriteType type, int index, byte[] data) {
        if ((index < 0) || (null == data)
                || (data.length != BleConstant.PICTURE_SIZE)) {
            dataOfImg = null;
            tolPackage = BleConstant.ERROR_INTEGER;
            sizeOfPackage = BleConstant.ERROR_INTEGER;
            indexOfImg = BleConstant.ERROR_INTEGER;

            return;
        }

        switch (type) {
            case Type_32:
                sizeOfPackage = 32;
                tolPackage = 1024; // The size of picture is 32K.
                break;
            case Type_64:
                sizeOfPackage = 64;
                tolPackage = 512;
                break;
            case Type_128:
                sizeOfPackage = 128;
                tolPackage = 256;
                break;
            case Type_256:
                sizeOfPackage = 256;
                tolPackage = 128;
                break;
            case Type_512:
                sizeOfPackage = 512;
                tolPackage = 64;
                break;
            case Type_1024:
                sizeOfPackage = 1024;
                tolPackage = 32;
                break;
        }
        indexOfImg = index;
        // tolPackage = BleConstant.PICTURE_SIZE / perSize;

        dataOfImg = new byte[data.length];
        System.arraycopy(data, 0, dataOfImg, 0, data.length);
    }

    public int getTolPackNum() {
        if (!isValid()) {
            return BleConstant.ERROR_INTEGER;
        }

        return tolPackage;
    }

    /**
     * Return the index of the image
     *
     * @return Return the index of the image,or ERROR_INTEGER if the image
     * is valid.
     * @see BleConstant#ERROR_INTEGER
     */
    public int getIndex() {
        if (!isValid()) {
            return BleConstant.ERROR_INTEGER;
        }

        return indexOfImg;
    }

    public boolean isValid() {
        return (null != dataOfImg);
    }

    public byte[] getPackage(int index) {
        byte[] rePackage = null;

        if ((null == dataOfImg) || (index < 0) || (index >= tolPackage)) {
            return rePackage;
        }
        rePackage = new byte[sizeOfPackage];
        System.arraycopy(dataOfImg, index * sizeOfPackage, rePackage, 0,
                sizeOfPackage);
        return rePackage;
    }

}