package com.iloof.heydoblelibrary.heydos1new;

import android.content.Context;
import android.util.Log;

import com.iloof.heydoblelibrary.AssisstTimerBean;
import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.util.BleMorePicture;

import java.util.List;

import static com.iloof.heydoblelibrary.util.BlePackageCmd.packagingCommand;
import static com.iloof.heydoblelibrary.util.BleTransfer.Deci2Hex;

/**
 * S1-S水杯命令打包
 */
public final class BleCmdSetS1S {

    private final static String TAG = "BleCommand";

    /**
     * 设置高温预警阈值
     */
    public static byte[] getCmdOfSetHightemperatrue(int temperatrue) {

        byte[] cmd = new byte[1];
        byte[] temp;
        temp = Deci2Hex(
                (int) ((1 * Math.pow(2, 7)) +  temperatrue), 2);
        cmd[0] = temp[1];

        return packagingCommand(null, BleConstant.ID_SET_HIGH_TEMPERATURE, cmd, 1);
    }

    /**
     * 设置高安全模式和水杯单位
     *
     */
    public static byte[] getCmdOftHeighSafeControl(int openHigh,int tempUnit,int waterUnit) {
        byte[] cmd = new byte[1];
        byte[] temp;
        temp = Deci2Hex(
                (int) ((openHigh * Math.pow(2, 7)) + (openHigh * Math.pow(2, 5)) + (tempUnit * Math.pow(2, 3))+(waterUnit * Math.pow(2, 2))), 2);
        cmd[0] = temp[1];
        return packagingCommand(null, BleConstant.ID_HEIGH_SAFE_CONTROL, cmd, 1);
    }

    /**
     * 获取温度提醒
     * @return
     */
    public static byte[] getCmdOfRemindTemp() {

        return packagingCommand(null, BleConstant.ID_GET_REMIND_TEMP, null, 0);
    }

    /**
     * 设置温度提醒
     *
     * @param offOn      开关
     * @param temprature 自设置温度
     * @return
     */
    public static byte[] getCmdOfTempratureLow(int offOn, int temprature) {
        byte[] temp, cmd;
        cmd = new byte[1];
        temp = Deci2Hex((int) ((offOn * Math.pow(2, 7)) + (temprature)), 2);
        cmd[0] = temp[1];

        return packagingCommand(null, BleConstant.ID_WATER_TEMPRATURE_LOW, cmd, 1);
    }


    /**
     * 设置免打扰模式，包含提醒LED亮度,音量大小等设置
     * BIT[7]：1:开启免打扰      0:关闭免打扰
     * BIT[6]：1:关闭饮水提醒    0:开启饮水提醒
     * BIT[4~5]：LED亮度 (4级)  亮度建议设置为 2级别
     * BIT[0~3]：系统音量(16级)  音量建议设置为 2级别
     *
     * @param
     */
    public static byte[] getCmdOfSetNoDisturbing(int disturb, int remind, int light, int volume) {

        byte[] cmd = new byte[1];
        byte[] temp;
        temp = Deci2Hex(
                (int) ((disturb * Math.pow(2, 7)) + (remind * Math.pow(2, 6)) + (light * Math.pow(2, 4)) + volume), 2);

        cmd[0] = temp[1];

        return packagingCommand(null, BleConstant.ID_SET_NO_DISTURBING,
                cmd, 1);
//        }

    }


    /**
     * @return 设置闹钟的命令
     */
    public static byte[] getCmdOfS1SSetAlarm(Context context, List<AssisstTimerBean> datas) {


        Log.i("ActivityAssistSetTime", "datas = " + datas.toString());
        Log.i("ActivityAssistSetTime", "datas.size = " + datas.size());
        byte[] temp, cmd;
        cmd = new byte[64];
        //设置标志
        temp = Deci2Hex(1, 1);
        cmd[0] = temp[0];

        //有效设置组
        temp = Deci2Hex(datas.size(), 1);

        cmd[1] = temp[0];

        //保留
        temp = Deci2Hex(1, 2);
        cmd[2] = temp[0];
        cmd[3] = temp[1];

        if (datas.size() > 0) {
            for (int i = 1; i <= 15; i++) {
                if (i <= datas.size()) {

//                    temp = BleUtil.Deci2Hex((int) ((offOn * Math.pow(2, 6))+(speed * Math.pow(2, 5)) + tem), 2);
//                    cmd[0] = temp[1];
                    /** 设置属性*/
                    //开关
                    int openState = datas.get(i - 1).getOpenState();
                    //工作模式 0:一A；1:一B；2:每天重复；3:自定义
                    int mode = datas.get(i - 1).getMode();
                    int notifyWays = datas.get(i-1).getNotifyWays();


                    temp = Deci2Hex((int) ((openState * Math.pow(2, 7)) + (mode * Math.pow(2, 5)) + notifyWays), 2);

                    cmd[i * 4] = temp[1];

                    /** 星期选择*/
                    int sunday = datas.get(i - 1).getSunday();
                    int monday = datas.get(i - 1).getMonday();
                    int tuesday =datas.get(i - 1).getTuesday();
                    int wednesday = datas.get(i - 1).getWednesday();
                    int thursday = datas.get(i - 1).getThursday();
                    int friday = datas.get(i - 1).getFriday();
                    int saturday = datas.get(i - 1).getSaturday();
                    temp = Deci2Hex((int) ((0 * Math.pow(2, 7)) + saturday * Math.pow(2, 6) + friday * Math.pow(2, 5)
                            + thursday * Math.pow(2, 4) + wednesday * Math.pow(2, 3) + tuesday * Math.pow(2, 2) + monday * Math.pow(2, 1)
                            + sunday * Math.pow(2, 0)), 2);

                    cmd[i * 4 + 1] = temp[1];

                    /** 小时*/
                    int hour = datas.get(i - 1).getHour();
                    temp = Deci2Hex(hour, 1);
                    cmd[i * 4 + 2] = temp[0];

                    /** 分钟*/
                    int minutes = datas.get(i - 1).getMinutes();
                    temp = Deci2Hex(minutes, 1);
                    cmd[i * 4 + 3] = temp[0];

                } else {
                    temp = Deci2Hex(0, 4);
                    cmd[i * 4] = temp[0];
                    cmd[i * 4 + 1] = temp[1];
                    cmd[i * 4 + 2] = temp[2];
                    cmd[i * 4 + 3] = temp[3];
                }

            }
        }

        return packagingCommand(null, BleConstant.ID_SET_ALARM, cmd, 64);
    }



    /**
     * S1-S水杯升级文件打包命令
     *
     * @param index   目标地址
     * @param flag    数据包属性标志
     * @param dataPic 数据，儿童版默认192
     * @return
     */
    public static byte[] getPackageCmdOfWriteFile(int index, int flag, byte[] dataPic) {
        byte[] command = null;
        byte[] temp = null;
        byte[] data = null;

        if (null == dataPic) {
            return command;
        }
        int dateLen = 4 * BleConstant.RECORD_ID_SIZE + dataPic.length;
        data = new byte[dateLen];
        //
        int offset = 0;
        //

        /** ST-1 版需要包序号,儿童水杯不需要*/
        // 包序号
//		data[offset++] = 0;
//		data[offset++] = 0;

        // 目标地址
        temp = Deci2Hex(index, 4);
        for (byte b : temp) {
            data[offset++] = b;
        }

        //数据包属性标志
        temp = Deci2Hex(flag, 2);
        for (byte b : temp) {
            data[offset++] = b;
        }

        // 包长度
//        temp = BleUtil.Deci2Hex(192, 2);
        temp = Deci2Hex(dataPic.length, 2);
        for (byte b : temp) {
            data[offset++] = b;
        }

        System.arraycopy(dataPic, 0, data, offset, dataPic.length);
        offset += dataPic.length;
        command = packagingCommand(null, BleConstant.ID_SEND_FILE, data,
                data.length);

        return command;
    }

    /**
     * 显示JPG头像
     *
     * @return
     */
    public static byte[] getCmdOfShowJpgHead() {

        return packagingCommand(null, BleConstant.ID_SHOW_JPEG_HEAD, Deci2Hex(12, 1), 1);
    }

    /**
     * 获取显示静态图像
     */
    public static byte[] getCmdOfDisplay1P(int index) {
        byte[] command = null;
        byte[] cmd = new byte[2];
        byte[] temp = null;
        if (index < 0) {
            Log.w(TAG, "The index is out of range!");
            return command;
        }

        temp = Deci2Hex(index, 1);
        cmd[0] = temp[0];
        temp = Deci2Hex(50, 1);
        cmd[1] = temp[0];


        command = packagingCommand(null, BleConstant.ID_DISPLAY_ONE_PICTURE,
               cmd, 2);

        return command;
    }

    /**
     * 显示多帧图片
     */
    public static byte[] getCmdOfDisplayMP(BleMorePicture morePictureInfo) {
        byte[] command = null;

        byte[] info = morePictureInfo.getS1SBytes();
        if (info == null) {
            return command;
        }
        command = packagingCommand(null, BleConstant.ID_DISPLAY_MORE_PICTURE,
                info, info.length);

        return command;
    }

    /**
     * 干杯命令
     */
    public static byte[] getCmdOfCheers() {

        return packagingCommand(null, BleConstant.ID_CHEERS, Deci2Hex(0, 1), 1);
    }

    /**
     * 饮水提醒指令
     */
    public static byte[] getCmdOfWaterRemind() {

        return packagingCommand(null, BleConstant.ID_WATERREMIND, Deci2Hex(0, 1), 1);
    }

}
