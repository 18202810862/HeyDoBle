package com.iloof.heydoblelibrary.util;

import com.iloof.heydoblelibrary.BleConstant;

/**
 * Created by chentao on 2017/9/14.
 */

public class BleCheckUtil {

    /**
     * 返回与数据对应的循环冗余校验（crc16校验-->蓝牙协议）
     *
     * @param data    The command what ready to check
     * @param dataLen The length of the data what need to calculate.
     * @return Cyclic Redundancy Check
     */
    public static byte[] getCrc16(byte[] data, int dataLen) {
        byte[] byteCrc = null;
        if ((null == data) || (dataLen > data.length) || (dataLen < 0)) {
            return byteCrc;
        }

        /** CRC Redundancy Table */
        short[] crc_ta = {(short) 0x0000, (short) 0x1021, (short) 0x2042,
                (short) 0x3063, (short) 0x4084, (short) 0x50a5, (short) 0x60c6,
                (short) 0x70e7, (short) 0x8108, (short) 0x9129, (short) 0xa14a,
                (short) 0xb16b, (short) 0xc18c, (short) 0xd1ad, (short) 0xe1ce,
                (short) 0xf1ef, (short) 0x1231, (short) 0x0210, (short) 0x3273,
                (short) 0x2252, (short) 0x52b5, (short) 0x4294, (short) 0x72f7,
                (short) 0x62d6, (short) 0x9339, (short) 0x8318, (short) 0xb37b,
                (short) 0xa35a, (short) 0xd3bd, (short) 0xc39c, (short) 0xf3ff,
                (short) 0xe3de, (short) 0x2462, (short) 0x3443, (short) 0x0420,
                (short) 0x1401, (short) 0x64e6, (short) 0x74c7, (short) 0x44a4,
                (short) 0x5485, (short) 0xa56a, (short) 0xb54b, (short) 0x8528,
                (short) 0x9509, (short) 0xe5ee, (short) 0xf5cf, (short) 0xc5ac,
                (short) 0xd58d, (short) 0x3653, (short) 0x2672, (short) 0x1611,
                (short) 0x0630, (short) 0x76d7, (short) 0x66f6, (short) 0x5695,
                (short) 0x46b4, (short) 0xb75b, (short) 0xa77a, (short) 0x9719,
                (short) 0x8738, (short) 0xf7df, (short) 0xe7fe, (short) 0xd79d,
                (short) 0xc7bc, (short) 0x48c4, (short) 0x58e5, (short) 0x6886,
                (short) 0x78a7, (short) 0x0840, (short) 0x1861, (short) 0x2802,
                (short) 0x3823, (short) 0xc9cc, (short) 0xd9ed, (short) 0xe98e,
                (short) 0xf9af, (short) 0x8948, (short) 0x9969, (short) 0xa90a,
                (short) 0xb92b, (short) 0x5af5, (short) 0x4ad4, (short) 0x7ab7,
                (short) 0x6a96, (short) 0x1a71, (short) 0x0a50, (short) 0x3a33,
                (short) 0x2a12, (short) 0xdbfd, (short) 0xcbdc, (short) 0xfbbf,
                (short) 0xeb9e, (short) 0x9b79, (short) 0x8b58, (short) 0xbb3b,
                (short) 0xab1a, (short) 0x6ca6, (short) 0x7c87, (short) 0x4ce4,
                (short) 0x5cc5, (short) 0x2c22, (short) 0x3c03, (short) 0x0c60,
                (short) 0x1c41, (short) 0xedae, (short) 0xfd8f, (short) 0xcdec,
                (short) 0xddcd, (short) 0xad2a, (short) 0xbd0b, (short) 0x8d68,
                (short) 0x9d49, (short) 0x7e97, (short) 0x6eb6, (short) 0x5ed5,
                (short) 0x4ef4, (short) 0x3e13, (short) 0x2e32, (short) 0x1e51,
                (short) 0x0e70, (short) 0xff9f, (short) 0xefbe, (short) 0xdfdd,
                (short) 0xcffc, (short) 0xbf1b, (short) 0xaf3a, (short) 0x9f59,
                (short) 0x8f78, (short) 0x9188, (short) 0x81a9, (short) 0xb1ca,
                (short) 0xa1eb, (short) 0xd10c, (short) 0xc12d, (short) 0xf14e,
                (short) 0xe16f, (short) 0x1080, (short) 0x00a1, (short) 0x30c2,
                (short) 0x20e3, (short) 0x5004, (short) 0x4025, (short) 0x7046,
                (short) 0x6067, (short) 0x83b9, (short) 0x9398, (short) 0xa3fb,
                (short) 0xb3da, (short) 0xc33d, (short) 0xd31c, (short) 0xe37f,
                (short) 0xf35e, (short) 0x02b1, (short) 0x1290, (short) 0x22f3,
                (short) 0x32d2, (short) 0x4235, (short) 0x5214, (short) 0x6277,
                (short) 0x7256, (short) 0xb5ea, (short) 0xa5cb, (short) 0x95a8,
                (short) 0x8589, (short) 0xf56e, (short) 0xe54f, (short) 0xd52c,
                (short) 0xc50d, (short) 0x34e2, (short) 0x24c3, (short) 0x14a0,
                (short) 0x0481, (short) 0x7466, (short) 0x6447, (short) 0x5424,
                (short) 0x4405, (short) 0xa7db, (short) 0xb7fa, (short) 0x8799,
                (short) 0x97b8, (short) 0xe75f, (short) 0xf77e, (short) 0xc71d,
                (short) 0xd73c, (short) 0x26d3, (short) 0x36f2, (short) 0x0691,
                (short) 0x16b0, (short) 0x6657, (short) 0x7676, (short) 0x4615,
                (short) 0x5634, (short) 0xd94c, (short) 0xc96d, (short) 0xf90e,
                (short) 0xe92f, (short) 0x99c8, (short) 0x89e9, (short) 0xb98a,
                (short) 0xa9ab, (short) 0x5844, (short) 0x4865, (short) 0x7806,
                (short) 0x6827, (short) 0x18c0, (short) 0x08e1, (short) 0x3882,
                (short) 0x28a3, (short) 0xcb7d, (short) 0xdb5c, (short) 0xeb3f,
                (short) 0xfb1e, (short) 0x8bf9, (short) 0x9bd8, (short) 0xabbb,
                (short) 0xbb9a, (short) 0x4a75, (short) 0x5a54, (short) 0x6a37,
                (short) 0x7a16, (short) 0x0af1, (short) 0x1ad0, (short) 0x2ab3,
                (short) 0x3a92, (short) 0xfd2e, (short) 0xed0f, (short) 0xdd6c,
                (short) 0xcd4d, (short) 0xbdaa, (short) 0xad8b, (short) 0x9de8,
                (short) 0x8dc9, (short) 0x7c26, (short) 0x6c07, (short) 0x5c64,
                (short) 0x4c45, (short) 0x3ca2, (short) 0x2c83, (short) 0x1ce0,
                (short) 0x0cc1, (short) 0xef1f, (short) 0xff3e, (short) 0xcf5d,
                (short) 0xdf7c, (short) 0xaf9b, (short) 0xbfba, (short) 0x8fd9,
                (short) 0x9ff8, (short) 0x6e17, (short) 0x7e36, (short) 0x4e55,
                (short) 0x5e74, (short) 0x2e93, (short) 0x3eb2, (short) 0x0ed1,
                (short) 0x1ef0};

        short crc = 0x0000;
        byte da = 0x00;
        int index = 0;
        for (int i = 0; i < dataLen; ++i) {
            da = (byte) (crc >> 8);
            crc <<= 8;
            index = (da ^ data[i]);
            if (index < 0) {
                index += 256;
            }
            crc ^= crc_ta[index];
        }

        byteCrc = new byte[]{0x00, 0x00};
        byteCrc[0] = (byte) (crc >> 8);
        byteCrc[1] = (byte) crc;

        return byteCrc;
    }

    /**
     * 返回与数据对应的循环冗余校验（crc16校验-->蓝牙协议）
     *
     * @param data     The command what ready to check
     * @param posStart The start position of the data what need to calculate.
     * @param posEnd   The end position of the data what need to calculate.
     * @return Cyclic Redundancy Check
     */
    public static byte[] getCrc16(byte[] data, int posStart, int posEnd) {
        byte[] byteCrc = null;
        if ((null == data) || (posEnd >= data.length) || (posEnd < 0)
                || (posStart < 0) || (posStart >= posEnd)) {
            return byteCrc;
        }

        /** CRC Redundancy Table */
        short[] crc_ta = {(short) 0x0000, (short) 0x1021, (short) 0x2042,
                (short) 0x3063, (short) 0x4084, (short) 0x50a5, (short) 0x60c6,
                (short) 0x70e7, (short) 0x8108, (short) 0x9129, (short) 0xa14a,
                (short) 0xb16b, (short) 0xc18c, (short) 0xd1ad, (short) 0xe1ce,
                (short) 0xf1ef, (short) 0x1231, (short) 0x0210, (short) 0x3273,
                (short) 0x2252, (short) 0x52b5, (short) 0x4294, (short) 0x72f7,
                (short) 0x62d6, (short) 0x9339, (short) 0x8318, (short) 0xb37b,
                (short) 0xa35a, (short) 0xd3bd, (short) 0xc39c, (short) 0xf3ff,
                (short) 0xe3de, (short) 0x2462, (short) 0x3443, (short) 0x0420,
                (short) 0x1401, (short) 0x64e6, (short) 0x74c7, (short) 0x44a4,
                (short) 0x5485, (short) 0xa56a, (short) 0xb54b, (short) 0x8528,
                (short) 0x9509, (short) 0xe5ee, (short) 0xf5cf, (short) 0xc5ac,
                (short) 0xd58d, (short) 0x3653, (short) 0x2672, (short) 0x1611,
                (short) 0x0630, (short) 0x76d7, (short) 0x66f6, (short) 0x5695,
                (short) 0x46b4, (short) 0xb75b, (short) 0xa77a, (short) 0x9719,
                (short) 0x8738, (short) 0xf7df, (short) 0xe7fe, (short) 0xd79d,
                (short) 0xc7bc, (short) 0x48c4, (short) 0x58e5, (short) 0x6886,
                (short) 0x78a7, (short) 0x0840, (short) 0x1861, (short) 0x2802,
                (short) 0x3823, (short) 0xc9cc, (short) 0xd9ed, (short) 0xe98e,
                (short) 0xf9af, (short) 0x8948, (short) 0x9969, (short) 0xa90a,
                (short) 0xb92b, (short) 0x5af5, (short) 0x4ad4, (short) 0x7ab7,
                (short) 0x6a96, (short) 0x1a71, (short) 0x0a50, (short) 0x3a33,
                (short) 0x2a12, (short) 0xdbfd, (short) 0xcbdc, (short) 0xfbbf,
                (short) 0xeb9e, (short) 0x9b79, (short) 0x8b58, (short) 0xbb3b,
                (short) 0xab1a, (short) 0x6ca6, (short) 0x7c87, (short) 0x4ce4,
                (short) 0x5cc5, (short) 0x2c22, (short) 0x3c03, (short) 0x0c60,
                (short) 0x1c41, (short) 0xedae, (short) 0xfd8f, (short) 0xcdec,
                (short) 0xddcd, (short) 0xad2a, (short) 0xbd0b, (short) 0x8d68,
                (short) 0x9d49, (short) 0x7e97, (short) 0x6eb6, (short) 0x5ed5,
                (short) 0x4ef4, (short) 0x3e13, (short) 0x2e32, (short) 0x1e51,
                (short) 0x0e70, (short) 0xff9f, (short) 0xefbe, (short) 0xdfdd,
                (short) 0xcffc, (short) 0xbf1b, (short) 0xaf3a, (short) 0x9f59,
                (short) 0x8f78, (short) 0x9188, (short) 0x81a9, (short) 0xb1ca,
                (short) 0xa1eb, (short) 0xd10c, (short) 0xc12d, (short) 0xf14e,
                (short) 0xe16f, (short) 0x1080, (short) 0x00a1, (short) 0x30c2,
                (short) 0x20e3, (short) 0x5004, (short) 0x4025, (short) 0x7046,
                (short) 0x6067, (short) 0x83b9, (short) 0x9398, (short) 0xa3fb,
                (short) 0xb3da, (short) 0xc33d, (short) 0xd31c, (short) 0xe37f,
                (short) 0xf35e, (short) 0x02b1, (short) 0x1290, (short) 0x22f3,
                (short) 0x32d2, (short) 0x4235, (short) 0x5214, (short) 0x6277,
                (short) 0x7256, (short) 0xb5ea, (short) 0xa5cb, (short) 0x95a8,
                (short) 0x8589, (short) 0xf56e, (short) 0xe54f, (short) 0xd52c,
                (short) 0xc50d, (short) 0x34e2, (short) 0x24c3, (short) 0x14a0,
                (short) 0x0481, (short) 0x7466, (short) 0x6447, (short) 0x5424,
                (short) 0x4405, (short) 0xa7db, (short) 0xb7fa, (short) 0x8799,
                (short) 0x97b8, (short) 0xe75f, (short) 0xf77e, (short) 0xc71d,
                (short) 0xd73c, (short) 0x26d3, (short) 0x36f2, (short) 0x0691,
                (short) 0x16b0, (short) 0x6657, (short) 0x7676, (short) 0x4615,
                (short) 0x5634, (short) 0xd94c, (short) 0xc96d, (short) 0xf90e,
                (short) 0xe92f, (short) 0x99c8, (short) 0x89e9, (short) 0xb98a,
                (short) 0xa9ab, (short) 0x5844, (short) 0x4865, (short) 0x7806,
                (short) 0x6827, (short) 0x18c0, (short) 0x08e1, (short) 0x3882,
                (short) 0x28a3, (short) 0xcb7d, (short) 0xdb5c, (short) 0xeb3f,
                (short) 0xfb1e, (short) 0x8bf9, (short) 0x9bd8, (short) 0xabbb,
                (short) 0xbb9a, (short) 0x4a75, (short) 0x5a54, (short) 0x6a37,
                (short) 0x7a16, (short) 0x0af1, (short) 0x1ad0, (short) 0x2ab3,
                (short) 0x3a92, (short) 0xfd2e, (short) 0xed0f, (short) 0xdd6c,
                (short) 0xcd4d, (short) 0xbdaa, (short) 0xad8b, (short) 0x9de8,
                (short) 0x8dc9, (short) 0x7c26, (short) 0x6c07, (short) 0x5c64,
                (short) 0x4c45, (short) 0x3ca2, (short) 0x2c83, (short) 0x1ce0,
                (short) 0x0cc1, (short) 0xef1f, (short) 0xff3e, (short) 0xcf5d,
                (short) 0xdf7c, (short) 0xaf9b, (short) 0xbfba, (short) 0x8fd9,
                (short) 0x9ff8, (short) 0x6e17, (short) 0x7e36, (short) 0x4e55,
                (short) 0x5e74, (short) 0x2e93, (short) 0x3eb2, (short) 0x0ed1,
                (short) 0x1ef0};

        short crc = 0x0000;
        byte da = 0x00;
        int index = 0;
        for (int i = posStart; i <= posEnd; ++i) {
            da = (byte) (crc >> 8);
            crc <<= 8;
            index = (da ^ data[i]);
            if (index < 0) {
                index += 256;
            }
            crc ^= crc_ta[index];
        }

        byteCrc = new byte[]{0x00, 0x00};
        byteCrc[0] = (byte) (crc >> 8);
        byteCrc[1] = (byte) crc;

        return byteCrc;
    }

    /**
     * 返回与数据对应的循环冗余校验（crc8校验 -->蓝牙协议）
     *
     * @param data     The command what ready to check
     * @param posStart The start position of the data what need to calculate.
     * @param posEnd   The end position of the data what need to calculate.
     * @return Cyclic Redundancy Check
     */
    public static byte getCrc8(byte[] data, int posStart, int posEnd) {
        byte crc8 = BleConstant.sinByteInvalid; // CRC8字节初始化
        if ((null == data) || (posEnd >= data.length) || (posEnd < 0)
                || (posStart < 0) || (posStart >= posEnd)) {
            return crc8;
        }

        byte[] crc_8 = {
                (byte) 0x00,
                (byte) 0X5E,
                (byte) 0XBC,
                (byte) 0XE2,
                (byte) 0X61,
                (byte) 0X3F,
                (byte) 0XDD,
                (byte) 0X83,
                (byte) 0XC2,
                (byte) 0X9C,
                (byte) 0X7E,
                (byte) 0X20,
                (byte) 0XA3,
                (byte) 0XFD,
                (byte) 0X1F,
                (byte) 0X41,// 0-15
                (byte) 0X9D,
                (byte) 0XC3,
                (byte) 0X21,
                (byte) 0X7F,
                (byte) 0XFC,
                (byte) 0XA2,
                (byte) 0X40,
                (byte) 0X1E,
                (byte) 0X5F,
                (byte) 0X01,
                (byte) 0XE3,
                (byte) 0XBD,
                (byte) 0X3E,
                (byte) 0X60,
                (byte) 0X82,
                (byte) 0XDC,// 16-31
                (byte) 0X23,
                (byte) 0X7D,
                (byte) 0X9F,
                (byte) 0XC1,
                (byte) 0X42,
                (byte) 0X1C,
                (byte) 0XFE,
                (byte) 0XA0,
                (byte) 0XE1,
                (byte) 0XBF,
                (byte) 0X5D,
                (byte) 0X03,
                (byte) 0X80,
                (byte) 0XDE,
                (byte) 0X3C,
                (byte) 0X62,// 32-47
                (byte) 0XBE,
                (byte) 0XE0,
                (byte) 0X02,
                (byte) 0X5C,
                (byte) 0XDF,
                (byte) 0X81,
                (byte) 0X63,
                (byte) 0X3D,
                (byte) 0X7C,
                (byte) 0X22,
                (byte) 0XC0,
                (byte) 0X9E,
                (byte) 0X1D,
                (byte) 0X43,
                (byte) 0XA1,
                (byte) 0XFF,// 48-63
                (byte) 0X46,
                (byte) 0X18,
                (byte) 0XFA,
                (byte) 0XA4,
                (byte) 0X27,
                (byte) 0X79,
                (byte) 0X9B,
                (byte) 0XC5,
                (byte) 0X84,
                (byte) 0XDA,
                (byte) 0X38,
                (byte) 0X66,
                (byte) 0XE5,
                (byte) 0XBB,
                (byte) 0X59,
                (byte) 0X07,// 64-79
                (byte) 0XDB,
                (byte) 0X85,
                (byte) 0X67,
                (byte) 0X39,
                (byte) 0XBA,
                (byte) 0XE4,
                (byte) 0X06,
                (byte) 0X58,
                (byte) 0X19,
                (byte) 0X47,
                (byte) 0XA5,
                (byte) 0XFB,
                (byte) 0X78,
                (byte) 0X26,
                (byte) 0XC4,
                (byte) 0X9A,// 80-95
                (byte) 0X65,
                (byte) 0X3B,
                (byte) 0XD9,
                (byte) 0X87,
                (byte) 0X04,
                (byte) 0X5A,
                (byte) 0XB8,
                (byte) 0XE6,
                (byte) 0XA7,
                (byte) 0XF9,
                (byte) 0X1B,
                (byte) 0X45,
                (byte) 0XC6,
                (byte) 0X98,
                (byte) 0X7A,
                (byte) 0X24,// 96-111
                (byte) 0XF8,
                (byte) 0XA6,
                (byte) 0X44,
                (byte) 0X1A,
                (byte) 0X99,
                (byte) 0XC7,
                (byte) 0X25,
                (byte) 0X7B,
                (byte) 0X3A,
                (byte) 0X64,
                (byte) 0X86,
                (byte) 0XD8,
                (byte) 0X5B,
                (byte) 0X05,
                (byte) 0XE7,
                (byte) 0XB9,// 112-127
                (byte) 0X8C,
                (byte) 0XD2,
                (byte) 0X30,
                (byte) 0X6E,
                (byte) 0XED,
                (byte) 0XB3,
                (byte) 0X51,
                (byte) 0X0F,
                (byte) 0X4E,
                (byte) 0X10,
                (byte) 0XF2,
                (byte) 0XAC,
                (byte) 0X2F,
                (byte) 0X71,
                (byte) 0X93,
                (byte) 0XCD,// 128-143
                (byte) 0X11,
                (byte) 0X4F,
                (byte) 0XAD,
                (byte) 0XF3,
                (byte) 0X70,
                (byte) 0X2E,
                (byte) 0XCC,
                (byte) 0X92,
                (byte) 0XD3,
                (byte) 0X8D,
                (byte) 0X6F,
                (byte) 0X31,
                (byte) 0XB2,
                (byte) 0XEC,
                (byte) 0X0E,
                (byte) 0X50,// 144-159
                (byte) 0XAF,
                (byte) 0XF1,
                (byte) 0X13,
                (byte) 0X4D,
                (byte) 0XCE,
                (byte) 0X90,
                (byte) 0X72,
                (byte) 0X2C,
                (byte) 0X6D,
                (byte) 0X33,
                (byte) 0XD1,
                (byte) 0X8F,
                (byte) 0X0C,
                (byte) 0X52,
                (byte) 0XB0,
                (byte) 0XEE,// 160-175
                (byte) 0X32,
                (byte) 0X6C,
                (byte) 0X8E,
                (byte) 0XD0,
                (byte) 0X53,
                (byte) 0X0D,
                (byte) 0XEF,
                (byte) 0XB1,
                (byte) 0XF0,
                (byte) 0XAE,
                (byte) 0X4C,
                (byte) 0X12,
                (byte) 0X91,
                (byte) 0XCF,
                (byte) 0X2D,
                (byte) 0X73,// 176-191
                (byte) 0XCA, (byte) 0X94, (byte) 0X76, (byte) 0X28,
                (byte) 0XAB,
                (byte) 0XF5,
                (byte) 0X17,
                (byte) 0X49,
                (byte) 0X08,
                (byte) 0X56,
                (byte) 0XB4,
                (byte) 0XEA,
                (byte) 0X69,
                (byte) 0X37,
                (byte) 0XD5,
                (byte) 0X8B,// 192-207
                (byte) 0X57, (byte) 0X09, (byte) 0XEB, (byte) 0XB5,
                (byte) 0X36, (byte) 0X68, (byte) 0X8A, (byte) 0XD4,
                (byte) 0X95,
                (byte) 0XCB,
                (byte) 0X29,
                (byte) 0X77,
                (byte) 0XF4,
                (byte) 0XAA,
                (byte) 0X48,
                (byte) 0X16,// 208-223
                (byte) 0XE9, (byte) 0XB7, (byte) 0X55, (byte) 0X0B,
                (byte) 0X88, (byte) 0XD6, (byte) 0X34, (byte) 0X6A,
                (byte) 0X2B, (byte) 0X75, (byte) 0X97, (byte) 0XC9,
                (byte) 0X4A,
                (byte) 0X14,
                (byte) 0XF6,
                (byte) 0XA8,// 224-239
                (byte) 0X74, (byte) 0X2A, (byte) 0XC8, (byte) 0X96,
                (byte) 0X15, (byte) 0X4B, (byte) 0XA9, (byte) 0XF7,
                (byte) 0XB6, (byte) 0XE8, (byte) 0X0A, (byte) 0X54,
                (byte) 0XD7, (byte) 0X89, (byte) 0X6B, (byte) 0X35 // 240-255
        };

        int index = 0; // CRC8校验表格索引

        // 进行CRC8位校验
        for (int i = posStart; i <= posEnd; ++i) {
            index = crc8 ^ data[i];
            if (index < 0) {
                index += 256;
            }
            crc8 = crc_8[index];
        }
        return (crc8);

    }
}
