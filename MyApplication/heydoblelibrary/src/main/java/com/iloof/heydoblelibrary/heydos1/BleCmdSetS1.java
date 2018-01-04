package com.iloof.heydoblelibrary.heydos1;

import android.util.Log;

import com.iloof.heydoblelibrary.BleConstant;
import com.iloof.heydoblelibrary.BleUtil.WriteType;
import com.iloof.heydoblelibrary.util.BleMorePicture;

import static com.iloof.heydoblelibrary.BleConstant.D;
import static com.iloof.heydoblelibrary.BleUtil.MYTAG;
import static com.iloof.heydoblelibrary.BleUtil.getTimeStamp;
import static com.iloof.heydoblelibrary.util.BleCheckUtil.getCrc16;
import static com.iloof.heydoblelibrary.util.BlePackageCmd.packagingCommand;
import static com.iloof.heydoblelibrary.util.BleTransfer.Deci2Hex;


/**
 * S1水杯命令打包
 */
public class BleCmdSetS1 {

    private final static String TAG = "BleCommand";

    /**
     * 设置高温预警阈值
     */
    public static byte[] getCmdOfSetHightemperatrue(int temperatrue) {
        byte[] command = null;

//		byte nu = BleUtil.Deci2Hex(temperatrue);
//		byte[] data = new byte[2];
//		data[0] = nu;

        byte[] data = Deci2Hex(temperatrue, 2);
        command = packagingCommand(null, BleConstant.ID_SET_HIGH_TEMPERATURE,
                data, data.length);

        return command;
    }

    /**
     * 获取指定编号定时器
     */
    public static byte[] getCmdOfTimer(int num) {
        byte[] command = null;

        byte nu = Deci2Hex(num);
        byte[] data = new byte[1];
        data[0] = nu;
        command = packagingCommand(null, BleConstant.ID_GET_TIMER, data, 1);


        return command;


    }

    /**
     * <b>获取水杯所有定时器</b>
     */
    public static byte[] getCmdOfAllTimers() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_GET_ALL_TIMER, null, 0);

        return command;
    }

    /**
     * 获取工厂模式状态
     */
    public static byte[] getCmdOfFactoryStatus() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_GET_FACTORY_STATUS,
                null, 0);

        return command;
    }

    /**
     * We are packaging the command which require the hareware version of the
     * cup. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfCupHardWareVersion() {

        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_CUP_HARDWARE_VERSION,
                null, 0);

        return command;

    }

    /**
     * We are packaging the command which require the current status(1/4) of the
     * cup. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfCurrentStatusOne() {
        byte[] command = null;

        byte[] stamp = getTimeStamp();
        if (null == stamp) {
            return command;
        }
        byte[] crcStamp = getCrc16(stamp, stamp.length);
        if (null == crcStamp) {
            return command;
        }
        int length = stamp.length + crcStamp.length + 2;
        byte[] data = new byte[length];
        System.arraycopy(stamp, 0, data, 0, stamp.length);
        System.arraycopy(crcStamp, 0, data, stamp.length, crcStamp.length);
        byte[] remain = {(byte) 0xff, (byte) 0xff};
        System.arraycopy(remain, 0, data, stamp.length + crcStamp.length, 2);

        command = packagingCommand(null, BleConstant.ID_CURRENT_STATUS_ONE,
                data, length);

        return command;

    }

    /**
     * 升级固件指令
     */
    public static byte[] getCmdOfUpdateHeydo() {

        return packagingCommand(null, BleConstant.ID_UPDATEHEYDO, null, 0);

    }

    /**
     * 饮水提醒指令
     */
    public static byte[] getCmdOfWaterRemind() {
        byte[] data = new byte[8];
        byte[] temp = Deci2Hex(90, 4);
        System.arraycopy(temp, 0, data, 0, 4);
        System.arraycopy(temp, 0, data, 4, 4);

        return packagingCommand(null, BleConstant.ID_WATERREMIND, data, 8);
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
     * 干杯命令
     */
    public static byte[] getCmdOfCheers() {

        return packagingCommand(null, BleConstant.ID_CHEERS, null, 0);
    }

    /**
     * 水杯序列号
     *
     * @return
     */
    public static byte[] getCmdOfSeriousNum() {
        return packagingCommand(null, BleConstant.ID_SERIOUS_NUM, null, 0);
    }

    /**
     * 获取矫正杯子重量的命令
     */
    public static byte[] getCmdOfAdjustWeight() {
        return packagingCommand(null, BleConstant.ID_ADJUST_WEIGHT, null, 0);
    }

    /**
     * 获取开启工厂模式命令(未用)
     */
    public static byte[] getCmdOfFactoryPattern() {
        return packagingCommand(null, BleConstant.ID_FACTORY_PATTERN, null, 0);
    }

    /**
     * 获取关闭工厂模式命令
     */
    public static byte[] getCmdOfExtinguishFactory() {

        return packagingCommand(null, BleConstant.ID_EXTINGUISH_FACTORY, null, 0);

    }

    /**
     * 获取亮屏模式命令
     */
    public static byte[] getCmdOfLightScreen(int state) {
        return packagingCommand(null, BleConstant.ID_LIGTH_SCREEN,
                Deci2Hex(state, 1), 1);
    }

    /**
     * 获取温度格式命令
     */
    public static byte[] getCmdOfTemparatureStyle(int state) {
        return packagingCommand(null, BleConstant.ID_TEMPARATURE_STYLE,
                Deci2Hex(state, 1), 1);
    }

    /**
     * 获取水量计量格式命令
     */
    public static byte[] getCmdOfWeightStyle(int state) {
        return packagingCommand(null, BleConstant.ID_WEIGHT_STYLE,
                Deci2Hex(state, 1), 1);
    }

    /**
     * 获取清空记录格式命令
     */
    public static byte[] getCmdOfDeleteRecords() {

        byte[] cmd = new byte[5];
        for (int i = 0; i <= 4; i++) {
            System.arraycopy(Deci2Hex(0, 1), 0, cmd, i, 1);
        }
        return packagingCommand(null, BleConstant.ID_DELETE_RECORDS,
                cmd, 5);

    }

    /**
     * 获取蜂鸣器鸣叫命令
     */
    public static byte[] getCmdOfBuzzerSound(int state) {
        return packagingCommand(null, BleConstant.ID_BUZZER_SOUND, Deci2Hex(state, 1), 1);


    }

    /**
     * 获取自定义免打扰命令
     *
     * @param state           时间段
     * @param firstTimeStart  时间段1开始时间
     * @param firstTimeEnd    时间段1结束时间
     * @param secondTimeStart 时间段2开始时间
     * @param secondTimeEnd   时间段2结束时间
     */
    public static byte[] getCmdOfCustomNoDisturb(int state, int firstTimeStart, int firstTimeEnd, int secondTimeStart, int secondTimeEnd) {
        byte[] cmd = new byte[9];
        System.arraycopy(Deci2Hex(state, 1), 0, cmd, 0, 1);
        if (firstTimeStart != 0xffff) {
            System.arraycopy(Deci2Hex(firstTimeStart, 2), 0, cmd, 1, 2);
        } else {
            cmd[1] = Deci2Hex(-1);
            cmd[2] = Deci2Hex(-1);
        }
        if (firstTimeEnd != 0xffff) {
            System.arraycopy(Deci2Hex(firstTimeEnd, 2), 0, cmd, 3, 2);
        } else {
            cmd[3] = Deci2Hex(-1);
            cmd[4] = Deci2Hex(-1);
        }
        if (secondTimeStart != 0xffff) {
            System.arraycopy(Deci2Hex(secondTimeStart, 2), 0, cmd, 5, 2);
        } else {
            cmd[5] = Deci2Hex(-1);
            cmd[6] = Deci2Hex(-1);
        }
        if (secondTimeEnd != 0xffff) {
            System.arraycopy(Deci2Hex(secondTimeEnd, 2), 0, cmd, 7, 2);
        } else {
            cmd[7] = Deci2Hex(-1);
            cmd[8] = Deci2Hex(-1);
        }

        return packagingCommand(null, BleConstant.ID_CUSTOM_NO_DISTURB, cmd, 9);
    }

    /**
     * 获取自定义界面显示命令
     *
     * @param main  主界面
     * @param one   顺序1，依次到3
     * @param two
     * @param three 4到7保留，填充0xff
     * @return
     */
    public static byte[] getCmdOfCustomUiShow(int main, int one, int two, int three) {
//		, int four,int five,int six , int seven

        byte[] cmd = new byte[8];
        cmd[0] = Deci2Hex(main);
        if (one == 0xff) {
            cmd[1] = Deci2Hex(-1);
        } else {
            cmd[1] = Deci2Hex(one);
        }
        if (two == 0xff) {
            cmd[2] = Deci2Hex(-1);
        } else {
            cmd[2] = Deci2Hex(two);
        }
        if (three == 0xff) {
            cmd[3] = Deci2Hex(-1);
        } else {
            cmd[3] = Deci2Hex(three);
        }
        cmd[4] = Deci2Hex(-1);
        cmd[5] = Deci2Hex(-1);
        cmd[6] = Deci2Hex(-1);
        cmd[7] = Deci2Hex(-1);

        return packagingCommand(null, BleConstant.ID_CUSTOM_UI_SHOW, cmd, 8);
    }

    /**
     * 开水温度降低提醒
     *
     * @param offOn      开关
     * @param temprature 自设置温度
     * @return
     */
    public static byte[] getCmdOfTempratureLow(int offOn, int temprature) {
        byte[] temp, cmd;
        cmd = new byte[2];
        temp = Deci2Hex((int) ((offOn * Math.pow(2, 15)) + (temprature)), 3);
        cmd[0] = temp[1];
        cmd[1] = temp[2];

        return packagingCommand(null, BleConstant.ID_WATER_TEMPRATURE_LOW, cmd, 2);
    }

    /**
     * 水杯恒温
     *
     * @param offOn 开关第7位 offOn2开关第6位 (00仅打开，01打开，10关闭，11恢复默认)
     * @param speed 1：加速，2：慢速
     * @param tem   温度(已设置的恒温温度，外加30)
     * @return
     */
    public static byte[] getCmdOfCupConstantTemp(int offOn, int speed, int tem) {
        byte[] temp, cmd;
        cmd = new byte[1];
        temp = Deci2Hex((int) ((offOn * Math.pow(2, 6)) + (speed * Math.pow(2, 5)) + tem), 2);
        cmd[0] = temp[1];


        return packagingCommand(null, BleConstant.ID_CUP_CONSTANT_TEMPRATURE, cmd, 1);
    }


    /**
     * 获取最新饮水记录命令
     */
    public static byte[] getCmdOfDrinkRecord() {

        return packagingCommand(null, BleConstant.ID_DRINKING_RECORD, null, 0);
    }

    /**
     * 获取最新触摸记录命令
     *
     * @param number   记录开始的编号
     * @param quantity 记录的数量
     */
    public static byte[] getCmdOfGetTouchsRecord(int number, int quantity) {
        byte[] cmd = new byte[3];
        System.arraycopy(Deci2Hex(number, 2), 0, cmd, 0, 2);
        System.arraycopy(Deci2Hex(quantity, 1), 0, cmd, 2, 1);
        return packagingCommand(null, BleConstant.ID_GET_TOUCHS_RECORD, cmd, 3);
    }

    /**
     * 获取最新触摸记录命令
     */
    public static byte[] getCmdOftGetLatestTouchRecord() {
        return packagingCommand(null, BleConstant.ID_GET_LATEST_TOUCH_RECORD,
                null, 0);
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
     * 获取最新杯盖记录
     */
    public static byte[] getCmdOftGetLatestLidState() {
        return packagingCommand(null, BleConstant.ID_GET_LATEST_LID_STATE,
                null, 0);
    }

    /**
     * 获取相应命令
     *
     * @param state 0x00高安全状态， 0xff关闭异物提醒（默认状态）
     */
    public static byte[] getCmdOftHeighSafeControl(int state) {
        byte[] cmd = new byte[1], temp;
        temp = Deci2Hex(state, 2);
        cmd[0] = temp[1];
        return packagingCommand(null, BleConstant.ID_HEIGH_SAFE_CONTROL, cmd, 1);
    }

    /**
     * @param group      定时器组
     * @param setType    设置方式
     * @param frequency  定时频率
     * @param switcher   定时器开关
     * @param remindType 提醒方式
     * @param seconds    定时的时间与当前时间间隔的秒数
     * @param waterValue （瘦身启用）单次饮水量提醒
     * @return 设置闹钟的命令
     */
    public static byte[] getCmdOfSetAlarm(int group, int setType,
                                          int frequency, int switcher, int remindType, int seconds,
                                          int waterValue) {
        byte[] temp, cmd;
        cmd = new byte[9];
        temp = Deci2Hex(group, 1);
        cmd[0] = temp[0];

        temp = Deci2Hex(setType, 1);
        cmd[1] = temp[0];

        // 设置定时属性
        frequency = frequency * 128 + switcher * 64;
        Log.d(MYTAG, "temp == null:" + (temp == null) + " frequency:"
                + frequency + " switcher:" + switcher);
        temp = Deci2Hex(frequency, 2);
        cmd[2] = temp[1];

        temp = Deci2Hex(
                (int) ((remindType * Math.pow(2, 14)) + waterValue), 3);
        cmd[3] = temp[1];
        cmd[4] = temp[2];

        temp = Deci2Hex(seconds, 4);
        System.arraycopy(temp, 0, cmd, 5, temp.length);

        StringBuilder stringBuilder = new StringBuilder(cmd.length);
        for (byte byteChar : cmd)
            stringBuilder.append(String.format("%02X ", byteChar));
        Log.d(MYTAG, "data:" + stringBuilder);
        return packagingCommand(null, BleConstant.ID_SET_ALARM, cmd, 9);
    }

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
     * We are packaging the command which require the current status(2/4) of the
     * cup. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfCurrentStatusTwo() {
        byte[] command = null;
        command = packagingCommand(null, BleConstant.ID_CURRENT_STATUS_TWO,
                null, 0);

        return command;
    }

    /**
     * We are packaging the command which require the cup to send a drinking
     * record. And if you want to know the detail, please refer to
     * {@link }.
     *
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
     * We are packaging the command which require the cup to send a warning
     * record. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfWarningRecord(byte[] index) {
        byte[] command = null;

        if ((null == index) || (index.length != BleConstant.RECORD_ID_SIZE)) {
            return command;
        }

        command = packagingCommand(null, BleConstant.ID_WARNING_RECORD, index,
                BleConstant.RECORD_ID_SIZE);

        return command;
    }

    /**
     * We are packaging the command which require the cup to disconnect. And if
     * you want to know the detail, please refer to {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfDisconnect() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_DISCONNECT, null, 0);

        return command;
    }

    /**
     * We are packaging the command which require the cup to cancel the attempt
     * to disconnect. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfCancelDisconnect() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_DISCONNECT,
                BleConstant.DATA_OF_CANCEL_REQUEST,
                BleConstant.DATA_OF_CANCEL_REQUEST.length);

        return command;
    }

    /**
     * We are packaging the command which require the cup to switch to the
     * factory mode. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfFactoryMode() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_FACTORY_MODE, null, 0);

        return command;
    }

    /**
     * We are packaging the command which require the cup to cancel the attempt
     * to switch mode. And if you want to know the detail, please refer to
     * {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfCancelFactoryMode() {
        byte[] command = null;
        command = packagingCommand(null, BleConstant.ID_FACTORY_MODE,
                BleConstant.DATA_OF_CANCEL_REQUEST,
                BleConstant.DATA_OF_CANCEL_REQUEST.length);

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
     * 发送文件首包命令生成
     *
     * @param index
     * @param dataPic 默认包数据长度1024
     * @return
     */
    public static byte[] getFirstPackageCmdOfWriteFile(int index, byte[] dataPic) {
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
        // 包序号
        data[offset++] = 0;
        data[offset++] = 0;

        // 文件地址
        temp = Deci2Hex(index, 4);
        for (byte b : temp) {
            data[offset++] = b;
        }

        // 包长度
        temp = Deci2Hex(1024, 2);
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
     * 发送文件除首包外其他包数据生成
     *
     * @param curentPkg
     * @param dataPic   默认包数据长度1024
     * @return
     */
    public static byte[] getOtherPackageCmdOfWriteFile(int curentPkg,
                                                       byte[] dataPic) {
        byte[] command = null;
        byte[] temp = null;
        byte[] data = null;

        if (null == dataPic) {
            return command;
        }

        int dateLen = BleConstant.RECORD_ID_SIZE + dataPic.length;
        data = new byte[dateLen];
        int offset = 0;
        // 包序号
        temp = Deci2Hex(curentPkg, 2);
        for (byte b : temp) {
            data[offset++] = b;
        }
        // 包数据
        System.arraycopy(dataPic, 0, data, offset, dataPic.length);
        offset += dataPic.length;
        command = packagingCommand(null, BleConstant.ID_SEND_FILE, data,
                dateLen);

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
     * We are packaging the command which require to display one picture. And if
     * you want to know the detail, please refer to {@link }.
     *
     * @param index The index of picture ready to display
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfDisplay1P(int index) {
        byte[] command = null;
        if (index < 0) {
            Log.w(TAG, "The index is out of range!");
            return command;
        }

        command = packagingCommand(null, BleConstant.ID_DISPLAY_ONE_PICTURE,
                Deci2Hex(index, 2), 2);

        return command;
    }

    /**
     * 显示多帧图片
     */
    public static byte[] getCmdOfDisplayMP(BleMorePicture morePictureInfo) {
        byte[] command = null;

        byte[] info = morePictureInfo.getBytes();
        if (info == null) {
            return command;
        }
        command = packagingCommand(null, BleConstant.ID_DISPLAY_MORE_PICTURE,
                info, info.length);

        return command;
    }

    @Deprecated
    /**
     * We are packaging the command which require to display more picture about logo.
     * And if you want to know the detail, please refer to {@link BleProtocol}.
     * @param morePictureInfo The information of picture ready to display
     * @return The command which have packaged and ready to send, Or null if something is wrong.
     */
    public static byte[] getCmdOfLogo(BleMorePicture morePictureInfo) {
        byte[] command = null;

        byte[] info = morePictureInfo.getBytes();
        if (info == null) {
            return command;
        }
        /*
         * command = packagingCommand(null, BleConstant.ID_SET_LOGO_PICTURE,
		 * info, info.length);
		 */

        return command;
    }

    /**
     * We are packaging the command which require to reset the password of BLE.
     * And if you want to know the detail, please refer to {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfResetPassword() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_RESET_PASSWORD, null, 0);

        return command;
    }

    /**
     * We are packaging the command which require to change the mode of cup. And
     * if you want to know the detail, please refer to {@link }.
     *
     * @param which Ture if you want to set the experience model, or false
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfChangeMode(boolean which) {
        byte[] command = null;
        byte[] mode = {0x00, 0x00};
        if (which) {
            mode = new byte[]{0x55, (byte) 0xAA};
        }

        command = packagingCommand(null, BleConstant.ID_CHANGE_MODE, mode,
                mode.length);

        return command;
    }

    /**
     * We are packaging the command which require to rename. And if you want to
     * know the detail, please refer to {@link }.
     *
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
     * We are packaging the command which require to turn to high level. And if
     * you want to know the detail, please refer to {@link }.
     *
     * @param parameter The parameter about high level.
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfHighLevel(byte[] parameter) {
        byte[] command = null;

        final int PARA_SIZE = 2;

        if ((null == parameter) || (parameter.length != PARA_SIZE)) {
            if (D)
                Log.i(TAG, "The size of parameter is wrong.");
            Log.w(TAG, "High Level Fail");
            return command;
        }

        command = packagingCommand(null, BleConstant.ID_HIGH_LEVEL, parameter,
                parameter.length);

        return command;
    }

    /**
     * We are packaging the command which require to show the words. And if you
     * want to know the detail, please refer to {@link }.
     *
     * @param words The words ready to show.
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfWords(byte[] words) {
        byte[] command = null;

        final int WORDS_MAX_SIZE = 140;

        if ((null == words) || (words.length > WORDS_MAX_SIZE)) {
            if (D)
                Log.i(TAG, "The size of words is too long.");
            Log.w(TAG, "Words Fail");
            return command;
        }

        command = packagingCommand(null, BleConstant.ID_WORDS, words,
                words.length);

        return command;
    }

    /**
     * We are packaging the command which require to reset the cup. And if you
     * want to know the detail, please refer to {@link }.
     *
     * @return The command which have packaged and ready to send, Or null if
     * something is wrong.
     */
    public static byte[] getCmdOfResetCup() {
        byte[] command = null;

        command = packagingCommand(null, BleConstant.ID_RESET, null, 0);

        return command;
    }


}
