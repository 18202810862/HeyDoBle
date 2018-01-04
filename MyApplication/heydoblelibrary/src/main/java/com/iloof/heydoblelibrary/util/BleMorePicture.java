package com.iloof.heydoblelibrary.util;


import com.iloof.heydoblelibrary.BleConstant;

import static com.iloof.heydoblelibrary.util.BleTransfer.Deci2Hex;

/**
 * We have packaged the more picture for display or LOGO.
 *
 * @author UESTC-PRMI-Burial
 * @date 2015-01-13
 */
public class BleMorePicture {
    private byte[] displayInformation = null;
    private final int perSize = 3;
    private final int maxSize = 57;
    private final int S1SPerSize = 2;
    private final int S1SMaxSize = 16;
    private int style;
    private int length;

    public BleMorePicture(int style,int length) {

        this.style = style;
        this.length = length;
        if (style == 0){
            displayInformation = new byte[maxSize];
            for (int i = 0; i < maxSize; ++i) {
                displayInformation[i] = BleConstant.sinByteInvalid;
            }
        }else {
            displayInformation = new byte[S1SMaxSize];
        }

    }

    /**
     * Set the picture ready to display.
     *
     * @param order       The order for display
     * @param index       The index of picture
     * @param millisecond The timespan of display (millsecond)
     * @return True if successful, Or false
     */
    public boolean setPicture(int order, int index, int millisecond) {
        if ((order >= 20) || (index < 0) || (millisecond < 0)) {
            return false;
        }

        if (style == 0){
            int offset = order * perSize;
            displayInformation[offset++] = Deci2Hex(index);
            byte[] temp = Deci2Hex(millisecond, 2);
            if (null != temp)
                System.arraycopy(temp, 0, displayInformation, offset, 2);
        }else {
            int offset = order * S1SPerSize;
            displayInformation[offset] = Deci2Hex(index);
            displayInformation[offset+1] = Deci2Hex(millisecond);
        }



        return true;
    }

    /**
     * Set the picture ready to display with no specific timespan.
     *
     * @param order The order for display
     * @param index The index of picture
     * @return True if successful, Or false
     */
    public boolean setPictureIndex(int order, int index) {
        if ((order >= 20) || (index < 0)) {
            return false;
        }

        int offset = order * perSize;
        displayInformation[offset++] = Deci2Hex(index);

        return true;
    }

    /**
     * Set the picture ready to display.
     *
     * @param order       The order for display
     * @param millisecond The timespan of display (millsecond)
     * @return True if successful, Or false
     */
    public boolean setPictureTimespan(int order, int millisecond) {
        if ((order >= 20) || (millisecond < 0)) {
            return false;
        }

        int offset = order * perSize;
        if (BleConstant.sinByteInvalid == displayInformation[offset++]) {
            return false;
        }

        byte[] temp = Deci2Hex(millisecond, 2);
        if (null != temp)
            System.arraycopy(temp, 0, displayInformation, offset, 2);

        return true;
    }

    /**
     * Add a picture ready to display.
     *
     * @param index       The index of picture
     * @param millisecond The timespan of display (millsecond)
     * @return True if successful, Or false
     */
    public boolean addPicture(int index, int millisecond) {
        if ((index < 0) || (millisecond < 0)) {
            return false;
        }

        int offset = 0;
        for (int i = 0; i < maxSize; ++i) {
            offset = i * perSize;
            if (BleConstant.sinByteInvalid == displayInformation[offset]) {
                displayInformation[offset++] = Deci2Hex(index);
                byte[] temp = Deci2Hex(millisecond, 2);
                if (null != temp)
                    System.arraycopy(temp, 0, displayInformation, offset, 2);

                return true;
            }
        }

        return false;
    }

    /**
     * Return the information about picture
     *
     * @return Return the valid data of {@link #displayInformation}
     */
    public byte[] getBytes() {
            /*
             * int setNum = maxSize / perSize; int length = 0; int offset = 0;
			 * for (int i = 0; i < setNum; ++i) { offset = i * perSize; if
			 * (displayInformation[offset++] == BleConstant.sinByteInvalid) {
			 * break; }
			 *
			 * if (displayInformation[offset++] == BleConstant.sinByteInvalid) {
			 * if (displayInformation[offset++] == BleConstant.sinByteInvalid) {
			 * break; } }
			 *
			 * length = offset; } byte[] info = new byte[length];
			 * System.arraycopy(displayInformation, 0, info, 0, length);
			 */

        return displayInformation;
    }

    public byte[] getS1SBytes(){
        for (int i= length+2;i<=S1SMaxSize-1;i++){
            displayInformation[i] = (byte)0xff;
        }

        return displayInformation;
    }
}

