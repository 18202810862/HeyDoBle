package com.iloof.heydoblelibrary.heydoc1;

import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.BleUtil.WriteType;

import java.util.List;

import static com.iloof.heydoblelibrary.BleConstant.D;
import static com.iloof.heydoblelibrary.util.BlePackageCmd.packagingCommand;
import static com.iloof.heydoblelibrary.util.BleTransfer.Deci2Hex;

/**
 * C1水杯命令打包
 */
public final class BleCmdSetC1 {

    private final static String TAG = "BleCommand";

    /**
     * 获取状态一
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfCurrentStatusOne() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_CURRENT_STATUS_ONE,
                null, 0);

        return command;

    }

    /**
     * 升级固件指令
     */
    public static byte[] getCmdOfUpdateHeydo() {

        byte[] cmd = new byte[6];
        for (int i = 0; i < 6; i++) {

            System.arraycopy(Deci2Hex(0, 1), 0, cmd, i, 1);
        }
        return packagingCommand(null, BleConstant.ID_UPDATEHEYDO, cmd, 6);

    }

    /**
     * 获取水杯软件版本
     */
    public static byte[] getCmdOfCupSoftVersion() {
        return packagingCommand(null, BleConstant.ID_CUP_SOFT_VERSION, null, 0);
    }

    /**
     * 获取存储器软硬件版本
     */
    public static byte[] getCmdOfMemorySoftVersion(int type) {
        Log.i(TAG, "获取水杯软件版本");
        return packagingCommand(null, BleConstant.ID_MEMORY_VERSION, Deci2Hex(type, 1), 1);
    }

    /**
     * 获取矫正杯子重量的命令
     */
    public static byte[] getCmdOfAdjustWeight() {
        return packagingCommand(null, BleConstant.ID_ADJUST_WEIGHT, null, 0);
    }


    /**
     * 设置系统工作模式
     */
    public static byte[] getCmdOfExtinguishFactory() {

        return packagingCommand(null, BleConstant.ID_EXTINGUISH_FACTORY, Deci2Hex(0, 1), 1);

    }

    /**
     * 获取系统校时命令
     */
    public static byte[] getCmdOfTiming(int time) {
        byte[] cmd = new byte[6];
        System.arraycopy(Deci2Hex(time, 4), 0, cmd, 0, 4);
        System.arraycopy(Deci2Hex(0, 1), 0, cmd, 4, 1);
        System.arraycopy(Deci2Hex(0, 1), 0, cmd, 5, 1);

        return packagingCommand(null, BleConstant.ID_TIMING, cmd, 6);

    }

    /**
     * 获取清空记录格式命令
     */
    public static byte[] getCmdOfDeleteRecords() {

        byte[] cmd = new byte[2];
        System.arraycopy(Deci2Hex(0, 1), 0, cmd, 0, 1);
        System.arraycopy(Deci2Hex(0, 1), 0, cmd, 1, 1);
        return packagingCommand(null, BleConstant.ID_DELETE_RECORDS,
                cmd, 2);

    }

    /**
     * 获取单色灯闪烁(单色灯闪烁功能只有C1才有)
     *
     * @param state 闪烁次数
     * @return
     */
    public static byte[] getCmdOfBreathingLightFlashing(int state, int r, int g, int b) {

        byte[] cmd = new byte[4];
        System.arraycopy(Deci2Hex(r, 1), 0, cmd, 0, 1);
        System.arraycopy(Deci2Hex(g, 1), 0, cmd, 1, 1);
        System.arraycopy(Deci2Hex(b, 1), 0, cmd, 2, 1);
        System.arraycopy(Deci2Hex(state, 1), 0, cmd, 3, 1);
        return packagingCommand(null, BleConstant.ID_SINGLE_LIGHT, cmd, 4);
    }

    /**
     * 获取颜色列表灯(单色灯闪烁功能只有C1才有)
     *
     * @param length 颜色列表长度
     * @param count  颜色变化间隔
     * @return
     */
    public static byte[] getCmdOfBreathingLightsFlashing(int length, int count, List<Integer> color1) {

        byte[] cmd = new byte[24];
        System.arraycopy(Deci2Hex(length, 1), 0, cmd, 0, 1);
        System.arraycopy(Deci2Hex(count, 2), 0, cmd, 1, 2);
        for (int i = 0; i < color1.size(); i++) {
            System.arraycopy(Deci2Hex(color1.get(i), 1), 0, cmd, i + 3, 1);
        }
        return packagingCommand(null, BleConstant.ID_LIST_LIGHT, cmd, 24);
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
     * 设置系统工作模式
     *
     * @param mode
     * @return
     */
    public static byte[] getCmdOfSetVoiceMode(int mode) {
        byte[] cmd = new byte[1];
        byte[] temp;
        temp = Deci2Hex(
                mode, 2);

        cmd[0] = temp[1];

        Log.i(TAG, "cmd = " + cmd[0]);

        return packagingCommand(null, BleConstant.ID_VOICE_MODE,
                cmd, 1);

    }

    /**
     * 设置音量调节
     *
     * @param
     */
    public static byte[] getCmdOfSetSoundAjust(int volume) {
        byte[] cmd = new byte[1];
        byte[] temp;
        temp = Deci2Hex(
                (int) (+volume), 2);

        cmd[0] = temp[1];

        Log.i(TAG, "cmd = " + ((int) cmd[0]));

        return packagingCommand(null, BleConstant.ID_SOUND_AJUST,
                cmd, 1);
//        }

    }

    /**
     * 设置LED亮度调节
     *
     * @param
     */
    public static byte[] getCmdOfSetLightAjust(int red, int green, int blue, int second, int light) {
        byte[] cmd = new byte[4];
        byte[] temp;
        System.arraycopy(Deci2Hex(red, 1), 0, cmd, 0, 1);
        System.arraycopy(Deci2Hex(green, 1), 0, cmd, 1, 1);
        System.arraycopy(Deci2Hex(blue, 1), 0, cmd, 2, 1);

        temp = Deci2Hex(
                (int) ((second * Math.pow(2, 2)) + light), 2);
        cmd[3] = temp[1];

        Log.i(TAG, "cmd = " + ((int) cmd[3]));

        return packagingCommand(null, BleConstant.ID_LIGHT_AJUST,
                cmd, 4);
//        }

    }

    /**
     * 获取最新饮水记录命令
     */
    public static byte[] getCmdOfDrinkRecord() {

        return packagingCommand(null, BleConstant.ID_DRINKING_RECORD, null, 0);
    }

    /***
     * 获取多条饮水记录
     */
    public static byte[] getCmdOfGetDrinksRecord(int number, int quantity) {
        byte[] cmd = new byte[3];
        System.arraycopy(Deci2Hex(number, 2), 0, cmd, 0, 2);
        System.arraycopy(Deci2Hex(quantity, 1), 0, cmd, 2, 1);
        return packagingCommand(null, BleConstant.ID_DRINKING_RECORDS, cmd, 3);
    }

    /**
     * 获取多条记录命令
     *
     * @param number   记录开始的编号
     * @param quantity 记录的数量
     */
    public static byte[] getCmdOftGetLibsState(int number, int quantity) {
        byte[] cmd = new byte[3];
        System.arraycopy(Deci2Hex(number, 2), 0, cmd, 0, 2);
        System.arraycopy(Deci2Hex(quantity, 1), 0, cmd, 2, 1);
        return packagingCommand(null, BleConstant.ID_GET_LIDS_STATE, cmd, 3);
    }

    /**
     * 获取定时器组*/
    public static byte[] getCmdOfTimer() {
        byte[] command = null;
        command = packagingCommand(null, BleConstant.ID_GET_TIMER, null, 0);
        return command;


    }

    /**
     * @return 设置闹钟的命令
     */
//    public static byte[] getCmdOfChildSetAlarm(Context context) {
//
//        List<HashMap<String, String>> datas =
//                DbHelper.getInstance(context).query(AssisstChildManager.TABLE_NAME, null, null, null, null);
//
//        Log.i("ActivityAssistSetTime", "datas = " + datas.toString());
//        Log.i("ActivityAssistSetTime", "datas.size = " + datas.size());
//        byte[] temp, cmd;
//        cmd = new byte[64];
//        //设置标志
//        temp = Deci2Hex(1, 1);
//        cmd[0] = temp[0];
//
//        //有效设置组
//        temp = Deci2Hex(datas.size(), 1);
//
//        cmd[1] = temp[0];
//
//        //保留
//        temp = Deci2Hex(1, 2);
//        cmd[2] = temp[0];
//        cmd[3] = temp[1];
//
//        if (datas.size() > 0) {
//            for (int i = 1; i <= 15; i++) {
//                if (i <= datas.size()) {
//
////                    temp = BleUtil.Deci2Hex((int) ((offOn * Math.pow(2, 6))+(speed * Math.pow(2, 5)) + tem), 2);
////                    cmd[0] = temp[1];
//                    /** 设置属性*/
//                    //开关
//                    int openState = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_STATUS));
//                    //工作模式 0:一A；1:一B；2:每天重复；3:自定义
//                    int mode = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_TYPE));
//                    Log.i("ActivityAssistSetTime", " BleCmdSet  mode " + mode);
//                    if (mode == 0) {
//                        temp = Deci2Hex((int) ((openState * Math.pow(2, 7)) + (0 * Math.pow(2, 5)) + 0 * Math.pow(2, 4) + 0x01), 2);
//                    } else if (mode == 1) {
//                        temp = Deci2Hex((int) ((openState * Math.pow(2, 7)) + (1 * Math.pow(2, 5)) + 0 * Math.pow(2, 4) + 0x01), 2);
//                    } else if (mode == 2) {
//                        temp = Deci2Hex((int) ((openState * Math.pow(2, 7)) + (1 * Math.pow(2, 6)) + (0 * Math.pow(2, 5)) + 0 * Math.pow(2, 4) + 0x01), 2);
//                    } else {
//                        temp = Deci2Hex((int) ((openState * Math.pow(2, 7)) + (1 * Math.pow(2, 6)) + (1 * Math.pow(2, 5)) + 0 * Math.pow(2, 4) + 0x01), 2);
//                    }
//                    cmd[i * 4] = temp[1];
//
//                    /** 星期选择*/
//                    int sunday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_SUNDAY));
//                    int monday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_MONDAY));
//                    int tuesday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_TUESDAY));
//                    int wednesday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_WEDNESDAY));
//                    int thursday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_THURSDAY));
//                    int friday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_FRIDAY));
//                    int saturday = Integer.parseInt(datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_SATURDAY));
//                    temp = Deci2Hex((int) ((0 * Math.pow(2, 7)) + saturday * Math.pow(2, 6) + friday * Math.pow(2, 5)
//                            + thursday * Math.pow(2, 4) + wednesday * Math.pow(2, 3) + tuesday * Math.pow(2, 2) + monday * Math.pow(2, 1)
//                            + sunday * Math.pow(2, 0)), 2);
//
//                    cmd[i * 4 + 1] = temp[1];
//
//                    String time = datas.get(i - 1).get(AssisstChildManager.AssisstChildColumns.CHILD_REMIND_TIME);
//                    time = HdUtils.formatHM(context,
//                            new Date(Long.parseLong(time) * 1000
//                                    - TimeZone.getDefault().getRawOffset()));
//                    /** 小时*/
//                    int hour = Integer.parseInt(time.split(":")[0]);
//                    temp = Deci2Hex(hour, 1);
//                    cmd[i * 4 + 2] = temp[0];
//
//                    /** 分钟*/
//                    int minutes = Integer.parseInt(time.split(":")[1]);
//                    temp = Deci2Hex(minutes, 1);
//                    cmd[i * 4 + 3] = temp[0];
//
//                } else {
//                    temp = Deci2Hex(0, 4);
//                    cmd[i * 4] = temp[0];
//                    cmd[i * 4 + 1] = temp[1];
//                    cmd[i * 4 + 2] = temp[2];
//                    cmd[i * 4 + 3] = temp[3];
//                }
//
//            }
//        }
//
//        return packagingCommand(null, BleConstant.ID_SET_ALARM, cmd, 64);
//    }

    /**
     * 获取设置每日饮水目标的命令
     */
    public static byte[] getCmdOfDayDrinkGoal(int data) {

        return packagingCommand(null, BleConstant.ID_SET_DAY_DRINK_GOAL,
                Deci2Hex(data, 2), 2);

    }

    /**
     * 获取每日饮水目标的命令
     */
    public static byte[] getCmdOfGetDrinkGoal() {
        return packagingCommand(null, BleConstant.ID_GET_DAY_DRINK_GOAL,
                null, 0);
    }

    /**
     * 获取最新饮水记录
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfDrinkingRecord() {
        byte[] command = null;


        command = packagingCommand(null, BleConstant.ID_DRINKING_RECORD, null,
                0);

        return command;
    }



    /**
     * 儿童水杯升级文件打包命令
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
     * We are packaging the command which require to write the data of picture
     * to cup. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @param index   The index of picture
     * @param curPkg  The current package
     * @param dataPic The data of picture.
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfWrtPic(int index, int curPkg, byte[] dataPic) {
        byte[] command = null;
        byte[] temp = null;
        byte[] data = null;

        if (null == dataPic) {
            return command;
        }

        int dtLen = 3 * BleConstant.RECORD_ID_SIZE + dataPic.length;
        data = new byte[dtLen];
        int offset = 0;

        temp = Deci2Hex(index, BleConstant.RECORD_ID_SIZE);
        for (byte b : temp) {
            data[offset++] = b;
        }

        temp = Deci2Hex(curPkg, BleConstant.RECORD_ID_SIZE);
        for (byte b : temp) {
            data[offset++] = b;
        }

        System.arraycopy(dataPic, 0, data, offset, dataPic.length);
        offset += dataPic.length;

        switch (dataPic.length) {
            case 32:
                command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_32,
                        data, dtLen);
                break;
            case 64:
                command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_64,
                        data, dtLen);
                break;
        /*
         * case 128: command = packagingCommand(null,
		 * BleConstant.ID_WRITE_PICTURE_64, data, dtLen); break; case 256:
		 * command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_64,
		 * data, dtLen); break; case 512: command = packagingCommand(null,
		 * BleConstant.ID_WRITE_PICTURE_64, data, dtLen); break;
		 */
            case 1024:
                short add = 0x0000;
                for (byte b : dataPic) {
                    add += (short) b;
                }
                byte lo = (byte) add;
                add >>= 8;
                byte hi = (byte) add;
                data[offset++] = hi;
                data[offset++] = lo;
                command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_1024,
                        data, dtLen);
                break;
            default:
                if (D)
                    Log.i(TAG,
                            "Something is Wrong with the packagingCommand function's parameter.");
                Log.w(TAG, "The packagingCommand is wrong about writing image.");
        }

        return command;
    }

    /**
     * We are packaging the command which require to write the data of picture
     * to cup. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @param type    The type of transfer
     * @param index   The index of picture
     * @param curPkg  The current package
     * @param dataPic The data of picture what must be 32bytes.
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfWrtPic(WriteType type, int index, int curPkg,
                                        byte[] dataPic) {
        byte[] command = null;
        byte[] temp = null;
        byte[] data = null;

        if (null == dataPic) {
            return command;
        }

        int dtLen = 2 * BleConstant.RECORD_ID_SIZE + dataPic.length;
        data = new byte[dtLen];
        int offset = 0;

        temp = Deci2Hex(index, BleConstant.RECORD_ID_SIZE);
        for (byte b : temp) {
            data[offset++] = b;
        }

        temp = Deci2Hex(curPkg, BleConstant.RECORD_ID_SIZE);
        for (byte b : temp) {
            data[offset++] = b;
        }

        System.arraycopy(dataPic, 0, data, offset, dataPic.length);
        switch (type) {
            case Type_32:
                command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_32,
                        data, dtLen);
                break;
            case Type_64:
                command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_64,
                        data, dtLen);
                break;
        /*
         * case Type_128: command = packagingCommand(null,
		 * BleConstant.ID_WRITE_PICTURE_64, data, dtLen); break; case Type_256:
		 * command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_64,
		 * data, dtLen); break; case Type_512: command = packagingCommand(null,
		 * BleConstant.ID_WRITE_PICTURE_64, data, dtLen); break;
		 */
            case Type_1024:
                command = packagingCommand(null, BleConstant.ID_WRITE_PICTURE_1024,
                        data, dtLen);
                break;
            default:
                if (D)
                    Log.i(TAG,
                            "Something is Wrong with the packagingCommand function's parameter.");
                Log.w(TAG, "The packagingCommand is wrong about writing image.");
        }

        return command;
    }

    /**
     * We are packaging the command which require to display one picture. And if
     * you want to know the detail, please refer to {@link }.
     *
     * @param index The index of picture ready to display
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfGetImgCrc(int index) {
        byte[] command = null;
        if (index < 0) {
            Log.w(TAG, "The index is out of range!");
            return command;
        }

        command = packagingCommand(null, BleConstant.ID_READ_PICTURE_CRC,
                Deci2Hex(index, 2), 2);

        return command;
    }

    /**
     * 修改蓝牙名称
     * @param rename The bluetooth's name.
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfRename(byte[] rename) {
        byte[] command = null;

        final int NAME_MAX_SIZE = 20;

        if ((null == rename) || (rename.length > NAME_MAX_SIZE)) {
            if (D)
                Log.i(TAG, "The size of name is too long.");
            Log.w(TAG, "Rename Fail");
            return command;
        }

        command = packagingCommand(null, BleConstant.ID_CHANGE_NAME, rename,
                rename.length);

        return command;
    }

    /**
     * 重启水杯
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfResetCup() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_RESET, null, 0);

        return command;
    }

    /**
     * 恢复出厂
     *
     * @return
     */
    public static byte[] getCmdOfFactoryReset() {

        return packagingCommand(null, BleConstant.ID_FACTORY_RESET, Deci2Hex(0, 1), 1);
    }

}
