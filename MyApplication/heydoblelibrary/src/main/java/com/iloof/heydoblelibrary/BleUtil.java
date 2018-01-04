package com.iloof.heydoblelibrary;

import android.util.Log;

import com.iloof.heydoblelibrary.app.Const;
import com.iloof.heydoblelibrary.util.BleMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static com.iloof.heydoblelibrary.util.BleCheckUtil.getCrc8;
import static com.iloof.heydoblelibrary.util.BleTransfer.Deci2Hex;
import static com.iloof.heydoblelibrary.util.BleTransfer.Hex2Deci;

/**
 * Implement the utility method set relevant to parse message Or package
 * command. The message what defined received from Bluetooth Low Energy(BLE),
 * And the command what defined send to BLE,The same below.
 *
 * @author UESTC-PRMI-Burial
 * @date 2014-12-13
 */
public final class BleUtil {

    private final static String TAG = "BlePackage";
    public static final String MYTAG = "BleUtil";

    /**
     * @author Burial
     * @date 2015-02-05
     */
    public static enum WriteType {
        Type_32, Type_64, Type_128, Type_256, Type_512, Type_1024
    }

    /**
     * 指定编号定时器
     */
    public static class BleTimerWithTime extends BleMessage {

        /**
         * 编号
         */
        int num;
        /**
         * 频率,每天或一次,默认为一次
         */
        int timerRate = 0;
        /**
         * 是否打开 1为打开
         */
        int timerStatus = 0;
        /**
         * 类型(吃药，瘦身，普通，定时关机)
         */
        int timerType = -1;
        /**
         * 饮水目标值(普通目标无效)
         */
        int goal = -1;
        /**
         * 提醒时间
         */
        int time = -1;

        int test = -1;

        public byte[] getBytes() {
            return bytes;
        }

        byte[] bytes;

        public int getNum() {
            return num;
        }

        public void setNum(int num) {
            this.num = num;
        }

        public int getTimerRate() {
            return timerRate;
        }

        public void setTimerRate(int timerRate) {
            this.timerRate = timerRate;
        }

        public int getTimerStatus() {
            return timerStatus;
        }

        public void setTimerStatus(int timerStatus) {
            this.timerStatus = timerStatus;
        }

        public int getTimerType() {
            return timerType;
        }

        public void setTimerType(int timerType) {
            this.timerType = timerType;
        }

        public int getGoal() {
            return goal;
        }

        public void setGoal(int goal) {
            this.goal = goal;
        }

        public int getTime() {
            return time;
        }

        public void setTime(int time) {
            this.time = time;
        }


        public BleTimerWithTime() {
            super();
        }

        public BleTimerWithTime(BleMessage msg) {
            super(msg);
            bytes = msg.data;
            initialize(super.data);

        }

        public BleTimerWithTime(byte[] message) {
            super(message);
            initialize(super.data);
        }

        private void initialize(byte[] bs) {
            if (null == bs)
                return;



            if (BleHelper.styleFlag == Const.S1_STYLE_FLAG) {
                int offset = 0;

                num = Hex2Deci(bs[offset++], false);

                int tempInt;
                tempInt = Hex2Deci(bs[offset++], false);

                if (tempInt >= 192) {
                    timerRate = 1;
                    timerStatus = 1;
                } else if (tempInt >= 128) {
                    timerRate = 1;
                    timerStatus = 0;
                } else if (tempInt >= 64) {
                    timerRate = 0;
                    timerStatus = 1;
                } else if (tempInt >= 0) {
                    timerRate = 0;
                    timerStatus = 0;
                }

			/*
             * if ((tempInt & 128) == 128) { timerRate = 1; } else if ((tempInt
			 * & 64) == 64) { timerStatus = 1; }
			 */

                byte[] tempByte = new byte[2];
                System.arraycopy(bs, offset, tempByte, 0, 2);
                tempInt = Hex2Deci(tempByte, false);
                goal = tempInt & 0x3fff;
                tempInt /= 256;
                if (tempInt >= 192) {
                    timerType = 3;
                } else if (tempInt >= 128) {
                    timerType = 2;
                } else if (tempInt >= 64) {
                    timerType = 1;
                } else if (tempInt >= 0) {
                    timerType = 0;
                }

                offset += 2;

                tempByte = new byte[4];
                System.arraycopy(bs, offset, tempByte, 0, 4);
                time = Hex2Deci(tempByte, false);
                offset += 4;
            }


        }

        public String toString() {
            return "num=" + num + "timerRate=" + timerRate + "timerStatus="
                    + timerStatus + "goal=" + goal + "timerType=" + timerType
                    + "time=" + time + " test=" + test;
        }

    }

    /**
     * 获取所有定时器倒计时
     *
     * @author lbc1234
     */
    public final static class BleAllTimersTime extends BleMessage {
        public List<String> timerTimes = new ArrayList<String>();

        public BleAllTimersTime() {
            super();
            // TODO 自动生成的构造函数存根
        }

        public BleAllTimersTime(BleMessage msg) {
            super(msg);
            initialize(super.data);
        }

        public BleAllTimersTime(byte[] message) {
            super(message);
            initialize(super.data);
        }

        private void initialize(byte[] bs) {
            if (null == bs)
                return;

            if (bs.length < 20)
                return;
            byte[] temp = new byte[4];
            int offset = 0;
            for (int i = 0; i < 5; i++) {

                System.arraycopy(bs, offset, temp, 0, 4);

                offset += 4;

                timerTimes.add(String.valueOf(Hex2Deci(temp, false)));

            }

        }

    }

    /**
     * @author lbc1234 <b>获取所有定时器</b>
     */
    public final static class BleAllTimers extends BleMessage {

        public static class BleTimers {

            public BleTimers(int num, byte[] data) {
                super();
                this.num = num;
                if (data != null && data.length == 3) {
                    byte temp = data[0];
                    if ((temp & 64) == 64) {
                        isOpened = true;
                    } else if ((temp & 128) == 128) {
                        timerRate = 1;
                    }
                    byte[] tem = new byte[2];
                    System.arraycopy(data, 1, tem, 0, 2);

                    int tempRemindType = data[1] * 256 + data[2];

                    goal = tempRemindType & 0x3fff;

                    timerType = tempRemindType >> 14;
                    // int temInteger=Hex2Deci(tem, false);

                }
            }

            public int getNum() {
                return num;
            }

            public void setNum(int num) {
                this.num = num;
            }

            /**
             * 编号
             */
            int num;
            /**
             * 频率,每天或一次,默认为一次
             */
            int timerRate = 0;
            /**
             * 是否打开
             */
            boolean isOpened = false;
            /**
             * 类型(吃药，瘦身，普通，定时关机)
             */
            int timerType = -1;

            /**
             * 饮水目标值(普通目标无效)
             */
            int goal = -1;

            public int getTimerRate() {
                return timerRate;
            }

            public void setTimerRate(int timerRate) {
                this.timerRate = timerRate;
            }

            public boolean isOpened() {
                return isOpened;
            }

            public void setOpened(boolean isOpened) {
                this.isOpened = isOpened;
            }

            public int getTimerType() {
                return timerType;
            }

            public void setTimerType(int timerType) {
                this.timerType = timerType;
            }

            public int getGoal() {
                return goal;
            }

            public void setGoal(int goal) {
                this.goal = goal;
            }

            public String toString() {
                return "num=" + num + "type=" + timerType + " rate="
                        + timerRate + " isOpened=" + isOpened + " goal=" + goal;
            }

        }

        public List<BleTimers> timers = new ArrayList<BleTimers>();

        public BleAllTimers() {
            super();
            // TODO 自动生成的构造函数存根
        }

        public BleAllTimers(BleMessage msg) {
            super(msg);
            initialize(super.data);
        }

        public BleAllTimers(byte[] message) {
            super(message);
            initialize(super.data);
        }

        private void initialize(byte[] bs) {
            if (null == bs)
                return;
            if (bs.length < 15)
                return;
            // 临时数组
            byte[] temp;
            // 偏移量
            int offset = 0;
            temp = new byte[3];
            for (int i = 0; i < 5; i++) {
                System.arraycopy(bs, offset, temp, 0, 3);
                offset += 3;
                timers.add(new BleTimers(i + 1, temp));
            }

        }

    }

    public final static class BleFactoryModeStatus extends BleMessage {
        /**
         * 是否处于工厂模式
         */
        public boolean isInFactoryMode = false;

        public BleFactoryModeStatus() {
            super();
            // TODO 自动生成的构造函数存根
        }

        public BleFactoryModeStatus(BleMessage msg) {
            super(msg);
            initialize(super.data);
        }

        public BleFactoryModeStatus(byte[] message) {
            super(message);
            initialize(super.data);
        }

        private void initialize(byte[] bs) {
            if (null == bs)
                return;

            isInFactoryMode = bs[0] == 0;
        }

    }

    /**
     * @author 冒国全 创建时间：2015年11月26日 下午3:29:10 获取硬件版本号
     */
    public final static class HardwareVersion extends BleMessage {
        public String hdVersion = "";
        public String model = "";

        public HardwareVersion(BleMessage msg) {
            super(msg);
            initialize(super.data);
        }

        public HardwareVersion(byte[] msg) {
            super(msg);
            initialize(super.data);
        }

        private void initialize(byte[] bs) {

            int testInt = Hex2Deci(bs[0], false);
            if (testInt == -1 || testInt == 255) {
                return;
            }
            byte data[] = bs;
            byte[] temp;
            int offset = 0;
            StringBuilder builder = new StringBuilder();
            // 公司代号
            temp = new byte[2];
            System.arraycopy(data, 0, temp, 0, 2);
            hdVersion = new String(temp);
            builder.append(hdVersion);
            builder.append('.');
            offset += 2;
            // 设备总标识号
            temp = new byte[2];
            System.arraycopy(data, offset, temp, 0, 2);
            hdVersion = new String(temp);
            builder.append(hdVersion);
            builder.append('.');
            offset += 2;
            // 硬件标识号
            temp = new byte[1];
            System.arraycopy(data, offset, temp, 0, 1);
            hdVersion = new String(temp);
            builder.append(hdVersion);
            offset += 1;
            // 结构相关标识 0:S1-T内胆：其他 1:S1-S 内胆：不锈钢 2:S1-X 内胆：不锈钢
            temp = new byte[1];
            System.arraycopy(data, offset, temp, 0, 1);
            try {
                hdVersion = new String(temp, "ASCII");
            } catch (UnsupportedEncodingException e1) {
                // TODO 自动生成的 catch 块
                e1.printStackTrace();
            }
            int st = 0;
            try {
                st = Integer.parseInt(hdVersion);
            } catch (NumberFormatException e) {

            }
            switch (st) {
                case 0:
                    model = "S1-T";
                    break;
                case 1:
                    model = "S1-S";
                    break;
                case 2:
                    model = "S1-X";
                    break;
            }
            builder.append(hdVersion);
            offset += 1;
            // 硬件主版本号
            temp = new byte[3];
            System.arraycopy(data, offset, temp, 0, 3);
            hdVersion = new String(temp);
            builder.append(hdVersion);
            builder.append('.');
            offset += 3;
            // 硬件从版本号
            temp = new byte[2];
            System.arraycopy(data, offset, temp, 0, 2);
            hdVersion = new String(temp);
            builder.append(hdVersion);
            offset += 2;
            hdVersion = builder.toString();
        }
    }

    /**
     * 水杯软件版本
     */
    public final static class CupSoftVersion extends BleMessage {
        public boolean isVersionValid = false;
        public String versionStr;

        public CupSoftVersion() {
            super();
            // TODO 自动生成的构造函数存根
        }

        public CupSoftVersion(BleMessage msg) {
            super(msg);
            initialize(super.data);
            // TODO 自动生成的构造函数存根
        }

        public CupSoftVersion(byte[] message) {
            super(message);
            initialize(super.data);
            // TODO 自动生成的构造函数存根
        }

        private void initialize(byte[] bs) {
            if (bs == null)
                return;

            byte[] data = bs;

            byte[] temp = new byte[11];

            System.arraycopy(data, 0, temp, 0, 11);

            for (int i = 0; i < 11; i++) {
                int tempInt = Hex2Deci(temp[i], false);
                if (tempInt != -1 && (tempInt != 0) && tempInt != 255) {
                    isVersionValid = true;
                }
            }

            if (isVersionValid) {
                versionStr = new String(temp);
                if (versionStr.contains("HC1-V")) {
                    versionStr = versionStr.substring(0, 10);
                    Log.i(TAG, "versionStr = " + versionStr);
                }
            }

            // result = bs[0];
            // // 第三字节为0表示发送成功
            // isUpdateSuccessed = result == 0;

        }
    }

    /**
     * 升级嘿逗固件
     */
    public final static class UpdateHeydo extends BleMessage {
        public int result = BleConstant.ERROR_INTEGER;
        public boolean isUpdateSuccessed = false;

        public UpdateHeydo() {
            super();
            // TODO 自动生成的构造函数存根
        }

        public UpdateHeydo(BleMessage msg) {
            super(msg);
            initialize(super.data);
            // TODO 自动生成的构造函数存根
        }

        public UpdateHeydo(byte[] message) {
            super(message);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        private void initialize(byte[] bs) {
            if (bs == null)
                return;
            result = bs[0];
            // 第三字节为0表示发送成功
            isUpdateSuccessed = result == 0;

        }

    }

    /**
     * 发送文件消息,由于文件一般较大,所以采取分包发送,默认每包1024字节
     *
     * @author lbc1234
     */
    public final static class SendFile extends BleMessage {
        /**
         * 包序号
         */
        public int packageNum;
        /**
         * 文件是否发生成功
         */
        public boolean isSendSuccessed = false;

        public SendFile(BleMessage msg) {
            super(msg);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        public SendFile(byte[] message) {
            super(message);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        private void initialize(byte[] bs) {

            byte[] temp = null;
            byte[] data = bs;
            int offset = 0;
            temp = new byte[2];
            System.arraycopy(data, offset, temp, 0, 2);
            packageNum = Hex2Deci(temp, false);
            // 第三字节为0表示发送成功
            isSendSuccessed = data[2] == 0;

        }

    }

    /**
     * 解析干杯
     */
    public final static class Cheers extends BleMessage {
        /**
         * 干杯结果
         */
        public int result = BleConstant.ERROR_INTEGER;

        public Cheers(BleMessage msg) {
            super(msg);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        public Cheers(byte[] message) {
            super(message);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        private void initialize(byte[] bs) {

            byte[] temp = null;

            temp = new byte[1];
            System.arraycopy(bs, 0, temp, 0, 1);
            result = Hex2Deci(temp, false);

        }

    }

    /***
     * 解析获取多条饮水记录返回的命令
     */
    public final static class DrinksRecord extends BleMessage {
        /**
         * 记录起始编号
         */
        int recordStartNum;
        /**
         * 记录总数
         */
        int recordCount;
        TimeZone timeZone = TimeZone.getDefault();

        public List<DrinkRecord> datas = new ArrayList<DrinkRecord>();

        public DrinksRecord(BleMessage msg) {
            super(msg);
            initialize(msg.data);
            // TODO 自动生成的构造函数存根
        }

        public DrinksRecord(byte[] message) {
            super(message);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        private void initialize(byte[] bs) {
            byte[] temp = null;
            byte[] data = bs;
            int offset = 0;
            // 起始记录编号
            temp = new byte[2];
            System.arraycopy(data, offset, temp, 0, 2);
            recordStartNum = Hex2Deci(temp, false);
            offset += 2;
            // 记录总数
            temp = new byte[1];
            System.arraycopy(data, offset, temp, 0, 1);
            recordCount = Hex2Deci(temp, false);
            datas.clear();
            for (int i = 0, index = 0; index < recordCount; i += 16, index++) {

                DrinkRecord record = parseDrinkRecord(data, i, recordStartNum
                        + index);
                datas.add(record);
            }

        }

        /**
         * 将从多条饮水数据命令获取到的数据解析成多个单条数据
         *
         * @param ds        多条饮水数据所有数据
         * @param index
         * @param recordNum
         * @return
         */
        private DrinkRecord parseDrinkRecord(byte[] ds, int index, int recordNum) {
            DrinkRecord record = new DrinkRecord();
            record.isRight = isRight;
            // 编号
            record.recordNum = recordNum;
            byte[] temp = null;
            byte[] data = ds;
            int offset = 3 + index;
            // 类型
            temp = new byte[1];
            System.arraycopy(data, offset, temp, 0, 1);
            record.recordType = Hex2Deci(temp, false);
            offset++;
            // 时间
            temp = new byte[4];
            System.arraycopy(data, offset, temp, 0, 4);
            long mSec = Hex2Deci(temp, false);
            mSec *= 1000;
            mSec -= timeZone.getRawOffset();
            record.recordTime = mSec;
            offset += 4;
            // 饮水量
            temp = new byte[2];
            System.arraycopy(data, offset, temp, 0, 2);
            record.waterDrink = Hex2Deci(temp, false);
            offset += 2;

            if (BleHelper.styleFlag == Const.S1_STYLE_FLAG){

                // 温度
                temp = new byte[2];
                System.arraycopy(data, offset, temp, 0, 2);
                record.temperature = Hex2Deci(temp, false);
                record.temperature /= 100;
                offset += 2;
                // ppm
                temp = new byte[2];
                System.arraycopy(data, offset, temp, 0, 2);
                record.ppm = Hex2Deci(temp, false);
                offset += 2;
            } else if (BleHelper.styleFlag == Const.S1S_STYLE_FLAG) {

                //饮水耗时
                offset += 2;
                // 当前的ppm值
                temp = new byte[2];
                System.arraycopy(data, offset, temp, 0, 2);
                record.ppm = Hex2Deci(temp, false);
                offset += 2;
//               当前盛水量
                offset += 2;
                // 温度
                temp = new byte[1];
                System.arraycopy(data, offset, temp, 0, 1);
                record.temperature = Hex2Deci(temp, false);

            }else {
                record.temperature = 0;
                record.ppm = 0;
            }

            // 电量

            // 保留
            return record;

        }

        @Override
        public String toString() {
            if (!isRight)
                return "error";
            JSONArray array = new JSONArray();
            for (int i = 0; i < datas.size(); i++) {
                try {
                    array.put(i, new JSONObject(datas.get(i).toString()));
                } catch (JSONException e) {
                    // TODO 自动生成的 catch 块
                    e.printStackTrace();
                }
            }

            // TODO 自动生成的方法存根
            return array.toString();
        }

    }

    /**
     * 解析单条用户饮水记录
     *
     * @author lbc1234
     */
    public final static class DrinkRecord extends BleMessage {
        /**
         * 记录编号
         */
        public int recordNum;
        /**
         * 记录类型
         */
        public int recordType;
        /**
         * 记录时间
         */
        public long recordTime;
        /**
         * 饮水量
         */
        public int waterDrink;
        /**
         * 温度
         */
        public int temperature;
        /**
         * ppm值
         */
        public int ppm;
        /**
         * 保留
         */
        int remain;

        TimeZone timeZone = TimeZone.getDefault();

        public DrinkRecord() {

        }

        public DrinkRecord(BleMessage msg) {
            super(msg);
            initialize(msg.data);
            // TODO 自动生成的构造函数存根
        }

        public DrinkRecord(byte[] message) {
            super(message);
            initialize(super.data);

            // TODO 自动生成的构造函数存根
        }

        private void initialize(byte[] bs) {

            byte[] data = bs, temp = null;

            if (data == null){
                return;
            }
            int offset = 0;
            // 当前记录 编号
            temp = new byte[2];
            System.arraycopy(data, 0, temp, 0, 2);
            recordNum = Hex2Deci(temp, false);
            if (recordNum == 0xffff) {
                isRight = false;
                return;
                // recordNum = 0;
            }
            offset += 2;
            // 当前记录 类型
            temp = new byte[1];
            System.arraycopy(data, offset, temp, 0, 1);
            recordType = Hex2Deci(temp, false);
            if (recordType == 0xffff) {
                isRight = false;
                return;
            }
            offset += 1;
            // 当前记录发生的时间
            temp = new byte[BleConstant.TS_SIZE];
            System.arraycopy(data, offset, temp, 0, BleConstant.TS_SIZE);
            long mSec = Hex2Deci(temp, false);
            mSec *= 1000;
            mSec -= timeZone.getRawOffset();
            recordTime = mSec;
            // rec = format.format(new Date(mSec));
            // Log.d(MYTAG, "timeStamp mSec:" + time);
            offset += BleConstant.TS_SIZE;

            // 饮水量
            temp = new byte[2];
            System.arraycopy(data, offset, temp, 0, 2);
            waterDrink = Hex2Deci(temp, false);
            offset += 2;

//            offset == 7
            if (BleHelper.styleFlag == Const.S1_STYLE_FLAG) {
                // 温度
                temp = new byte[2];
                System.arraycopy(data, offset, temp, 0, 2);
                temperature = Hex2Deci(temp, false);
                offset += 2;
                temperature /= 100;

                // 当前的ppm值
                temp = new byte[2];
                System.arraycopy(data, offset, temp, 0, 2);
                ppm = Hex2Deci(temp, false);
                offset += 2;
            } else if (BleHelper.styleFlag == Const.S1S_STYLE_FLAG) {

                //饮水耗时
                offset += 2;
                // 当前的ppm值
                temp = new byte[2];
                System.arraycopy(data, offset, temp, 0, 2);
                ppm = Hex2Deci(temp, false);
                offset += 2;
//               当前盛水量
                offset += 2;
                // 温度
                temp = new byte[1];
                System.arraycopy(data, offset, temp, 0, 1);
                temperature = Hex2Deci(temp, false);

            } else {
                Log.i(TAG,"儿童水杯饮水记录");
                temperature = 0;
                ppm = 0;
            }


        }

        @Override
        public String toString() {

            if (!isRight)// 数据出错
                return "error";

            JSONObject dataR = new JSONObject();

            try {
                dataR.put("time", recordTime);
                dataR.put("number", recordNum);
                dataR.put("type", recordType);
                dataR.put("temperature", temperature);
                dataR.put("waterDrink", waterDrink);
                dataR.put("ppm", ppm);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return dataR.toString();
        }

    }

    /**
     * 解析请求状态1返回的命令
     *
     * @author UESTC-PRMI-Burial
     * @date 2014-12-16
     */
    public final static class BleCurrentStaus1 extends BleMessage {
        /**
         * 水杯系统时间(S1、C1、S1-S)
         */
        public Date timeStamp;
        /**
         * 今日饮水量(S1、C1、S1-S)
         */
        public long TodayDrinkAmountOfWater = -1;
        /**
         * 温度(S1、C1、S1-S)
         */
        public float temperature = BleConstant.ERROR_FLOAT;
        /**
         * 当前水质(S1、C1、S1-S)
         */
        public float curWaterQuantity = BleConstant.ERROR_FLOAT;
        /**
         * 高温预警值(S1、S1-S)
         */
        public int highTemperature = BleConstant.ERROR_INTEGER;
        /**
         * 温度单位 1是摄氏度 2是华氏度(S1、S1-S)
         */
        public int temperatureStyle = BleConstant.ERROR_INTEGER;
        /**
         * 水量单位 1是ml 2是oz(S1、S1-S)
         */
        public int weightStyle = BleConstant.ERROR_INTEGER;

        /**
         * 即将发生的系统提醒喝水时间倒计时(C1、S1-S)
         */
        private int nextRemindWater = BleConstant.ERROR_INTEGER;

        //自定义界面((S1))
        public int customOne = BleConstant.ERROR_INTEGER;
        public int customTwo = BleConstant.ERROR_INTEGER;
        public int customThree = BleConstant.ERROR_INTEGER;
        public int customFour = BleConstant.ERROR_INTEGER;

        //免打扰四个时间段(S1)
        public int disturbOne = BleConstant.ERROR_INTEGER;
        public int disturbTwo = BleConstant.ERROR_INTEGER;
        public int disturbThree = BleConstant.ERROR_INTEGER;
        public int disturbFour = BleConstant.ERROR_INTEGER;

        public boolean isRight = false;

        /**
         * 电池电压(S1、C1、S1-S)
         */
        public int BatteryVoltage = -1;
        /**
         * 是否开启高安全(S1、S1-S)
         */
        public boolean isHighSecerytyEnable = false;
        /**
         * 是否允许提醒饮水(S1、C1、S1-S)
         */
        public boolean isAllowWaterRemind = false;
        /**
         * 是否开启免打扰(S1、C1、S1-S)
         */
        public boolean isNodisturbDisable = false;
        /**
         * 是否开启亮屏模式(S1)
         */
        public boolean isLightScreen = false;
        /**
         * 是否开启工厂模式(S1)
         */
        public boolean isFactoryPattern = false;

        /**
         * 水杯状态(S1、C1、S1-S)
         */
        public boolean cupStateBoolean = false;

        /**
         * 温度降低提醒(S1、S1-S)
         */
        public int lowTemperature = BleConstant.ERROR_INTEGER;
        public boolean lowTemperatureBoolean = false;

        /**
         * 系统音量(C1、S1-S)
         */
        public int volume = BleConstant.ERROR_INTEGER;

        /**
         * 系统LED灯亮度(C1)
         */
        public int LEDLuminance = BleConstant.ERROR_INTEGER;

        /**
         * 重量校准标识(C1、S1-S)
         */
        public int weightCalibrationMark = BleConstant.ERROR_INTEGER;

        /**
         * 系统语音模式(C1)
         */
        public int systemVoiceMode = BleConstant.ERROR_INTEGER;

        /**
         * 水杯倾斜度(C1、S1-S)
         */
        public int cupTilt = BleConstant.ERROR_INTEGER;
        /***/
        /**
         * The size of the DATA of CURrent STatuS(1/4).If you want to know more
         * information, please refer to {@link }
         */
        // private final static int DATA_STS1_SIZE = 36;
        public BleCurrentStaus1(BleMessage msg) {
            super(msg);
            initialize(msg.data);
        }

        public BleCurrentStaus1(byte[] message) {
            super(message); // BleMessage
            if (!super.isRight) {
                Log.i(TAG, "Data is wrong!");
                return;
            }
            initialize(super.data);
        }

        /**
         * 电压转换成电量
         **/
        public int getBattery(int battery) {
            int control_data[] = new int[]{3000, 3450, 3680, 3740, 3770,
                    3790, 3820, 3870, 3920, 3980, 4060, 4200};
            int dat;
            int i;
            int k;
            for (i = 0; i < 12; i++) {
                if (battery < control_data[i]) {
                    break;
                }
            }
            if (i > 11) {
                dat = 100;
            } else {
                if (i > 2) {
                    dat = (i - 2) * 10;
                    k = control_data[i] - control_data[i - 1];
                    k /= 10;// k
                    k = (battery - control_data[i - 1] + 5) / k;
                    dat += k;

                } else if (i > 1) {
                    dat = (i - 1) * 5;
                    k = control_data[i] - control_data[i - 1];
                    k /= 5;// k
                    k = (battery - control_data[i - 1] + 3) / k;
                    dat += k;

                } else {
                    dat = 0;
                }
            }


            return dat;

        }

        private void initialize(byte[] content) {
            byte[] data = content;

            if (data == null){
                return;
            }
            Log.d(MYTAG, "data length:" + content.length);

            byte[] temp = null;
            int offset = 0;

            if (BleHelper.styleFlag == Const.S1_STYLE_FLAG) {
                /**当前系统时间*/
                temp = new byte[BleConstant.TS_SIZE];
                System.arraycopy(data, 16, temp, 0, BleConstant.TS_SIZE);
                long mSec = Hex2Deci(temp, false);
                mSec *= 1000;
                Log.d(MYTAG, "timeStamp mSec" + mSec);
                timeStamp = new Date(mSec);

                /**当前温度*/
                temp = new byte[BleConstant.TP_SIZE];
                System.arraycopy(data, 20, temp, 0, BleConstant.TP_SIZE);
                temperature = Hex2Deci(temp, true);
                temperature /= BleConstant.FIXED_POINT_2BYTES;
                if (temperature < 0) {
                    temperature = 0;
                }
                /**当前水质*/
                temp = new byte[BleConstant.CUR_WQ_SIZE];
                System.arraycopy(data, 24, temp, 0, BleConstant.CUR_WQ_SIZE);
                curWaterQuantity = Hex2Deci(temp, false);
                curWaterQuantity /= 1000;
                if (curWaterQuantity < 0) {
                    curWaterQuantity = 0;
                }

                /** 温度格式*/
                temp = new byte[1];
                System.arraycopy(data, 26, temp, 0, 1);
                temperatureStyle = Hex2Deci(temp, false);

                // 水量格式
                temp = new byte[1];
                System.arraycopy(data, 27, temp, 0, 1);
                weightStyle = Hex2Deci(temp, false);

                /** 高温预警值*/
                temp = new byte[BleConstant.RECORD_ID_SIZE];
                System.arraycopy(data, 33, temp, 0, BleConstant.RECORD_ID_SIZE);
                highTemperature = Hex2Deci(temp, false);
                highTemperature /= 100;

                /** 今日已饮水 */
                temp = new byte[4];
                System.arraycopy(data, 48, temp, 0, 4);
                TodayDrinkAmountOfWater = Hex2Deci(temp, false);
                if (TodayDrinkAmountOfWater < 0) {
                    TodayDrinkAmountOfWater = 0;
                }

                /** 电池电量 */
                temp = new byte[2];
                System.arraycopy(data, 52, temp, 0, 2);
                BatteryVoltage = Hex2Deci(temp, false);
                BatteryVoltage = getBattery(BatteryVoltage);


                /**亮屏/工厂模式*/
                temp = new byte[1];
                System.arraycopy(data, 32, temp, 0, 1);
                int lightFactoryStatus = Hex2Deci(temp, false);
                byte a = (byte) lightFactoryStatus;
                //低位依次是(0x01工厂模式)
                //(0x02是亮屏模式)
                if ((a & 0x01) == 0x01) {
                    isFactoryPattern = true;
                } else {
                    isFactoryPattern = false;
                }
                if ((a & 0x02) == 0x02) {
                    isLightScreen = true;
                } else {
                    isLightScreen = false;
                }

                /**水杯状态*/
                temp = new byte[BleConstant.RECORD_ID_SIZE];
                System.arraycopy(data, 38, temp, 0, BleConstant.RECORD_ID_SIZE);
                byte cupState = (byte) Hex2Deci(temp, false);
                //			0X01 //拿起来了
                //			0X02//拿起倒置
                //			0X04//摇动
                //			0X08//杯子正立
                if ((cupState & 0x01) == 0x01) {
                    cupStateBoolean = false;
                } else if ((cupState & 0x02) == 0x02) {
                    cupStateBoolean = false;
                } else if ((cupState & 0x04) == 0x04) {
                    cupStateBoolean = false;
                } else if ((cupState & 0x08) == 0x08) {
                    cupStateBoolean = true;
                }


                /**自定义界面显示*/
                temp = new byte[1];
                System.arraycopy(data, 80, temp, 0, 1);
                customOne = Hex2Deci(temp, false);
                System.arraycopy(data, 81, temp, 0, 1);
                customTwo = Hex2Deci(temp, false);
                System.arraycopy(data, 82, temp, 0, 1);
                customThree = Hex2Deci(temp, false);
                System.arraycopy(data, 83, temp, 0, 1);
                customFour = Hex2Deci(temp, false);

                /**免打扰*/
                temp = new byte[2];
                System.arraycopy(data, 72, temp, 0, 2);
                disturbOne = Hex2Deci(temp, false);
                System.arraycopy(data, 74, temp, 0, 2);
                disturbTwo = Hex2Deci(temp, false);
                System.arraycopy(data, 76, temp, 0, 2);
                disturbThree = Hex2Deci(temp, false);
                System.arraycopy(data, 78, temp, 0, 2);
                disturbFour = Hex2Deci(temp, false);


                /**提醒状态*/
                temp = new byte[1];
                System.arraycopy(data, 66, temp, 0, 1);
                byte b = (byte) Hex2Deci(temp, false);
                // 低位依次是（0x01异物提醒允许）
                // （0x02高安全允许）
                // （0x04提醒饮水允许）
                // （0x08免打扰关闭）
                // 也上信息复用或（|）操作
                if ((b & 0x08) == 0x08) {
                    isNodisturbDisable = true;
                } else {
                    isNodisturbDisable = false;
                }
                if ((b & 0x04) == 0x04) {
                    isAllowWaterRemind = true;
                } else {
                    isAllowWaterRemind = false;
                }
                if ((b & 0x02) == 0x02) {
                    isHighSecerytyEnable = true;
                } else {
                    isHighSecerytyEnable = false;
                }

                /**水杯温度降低提醒*/
                temp = new byte[2];
                System.arraycopy(data, 70, temp, 0, 2);
                int lowTemp = Hex2Deci(temp, false);
                if (lowTemp != 0xFFFF){ //0xFFFF无效
                    if ((lowTemp & 0x8000) == 0) {
                        lowTemperatureBoolean = false;
                    } else {
                        lowTemperatureBoolean = true;
                    }
                    lowTemperature = (lowTemp & 0x7FFF);
                    lowTemperature /= 100;
                }else {
                    lowTemperatureBoolean = false;
                    lowTemperature = 0;
                }

            }else if (BleHelper.styleFlag == Const.C1_STYLE_FLAG){
                Log.i(TAG, "initialize ---->儿童水杯");

                /** 系统时间*/
                temp = new byte[4];
                System.arraycopy(data, 0, temp, 0, 4);
                long mSec = Hex2Deci(temp, false);
                mSec *= 1000;
                Log.d(MYTAG, "timeStamp mSec" + mSec);
                timeStamp = new Date(mSec);

                /** 今日饮水量*/
                temp = new byte[2];
                System.arraycopy(data, 6, temp, 0, 2);
                TodayDrinkAmountOfWater = Hex2Deci(temp, false);
                if (TodayDrinkAmountOfWater < 0) {
                    TodayDrinkAmountOfWater = 0;
                }

                /** 电池电量*/
                temp = new byte[1];
                System.arraycopy(data, 12, temp, 0, 1);
                BatteryVoltage = Hex2Deci(temp, false);
                if (BatteryVoltage < 0) {
                    BatteryVoltage = 0;
                }

                /** 水杯状态*/
                temp = new byte[1];
                System.arraycopy(data, 15, temp, 0, 1);
                byte cup = (byte) Hex2Deci(temp, false);

                if ((cup & (1 << 7)) != 0) {//BIT[7] 拿起状态,1:拿起；0：未拿起
                    cupStateBoolean = false;
                } else {
                    if ((cup & (1 << 6)) != 0) {//BIT[6] 倒置状态,1:倒置；0：未倒置
                        cupStateBoolean = false;
                    } else {
                        if ((cup & (1 << 5)) != 0) {//BIT[5] 摇动状态,1:摇动；0：未摇动
                            cupStateBoolean = false;
                        } else {
                            if ((cup & (1 << 4)) == 0) {//BIT[4] 正立状态,1:正立；0：未正立
                                cupStateBoolean = false;
                            } else {
                                cupStateBoolean = true;
                            }
                        }
                    }
                }


                /** 即将发生的系统提醒喝水时间倒计时*/
                temp = new byte[2];
                System.arraycopy(data, 16, temp, 0, 2);
                nextRemindWater = Hex2Deci(temp, false);
                if (nextRemindWater < 0) {
                    nextRemindWater = 0;
                }

                //BIT[3] 杯盖状态,1:盖上；0：未盖上,因为杯盖状态与所用水杯状态无关所以未做操作

                /**重量校准标志*/
                temp = new byte[2];
                System.arraycopy(data, 27, temp, 0, 2);
                weightCalibrationMark = Hex2Deci(temp, false);


                /** 系统工作模式*/
                temp = new byte[1];
                System.arraycopy(data, 32, temp, 0, 1);
                byte factory = (byte) Hex2Deci(temp, false);
                if ((factory & 0x01) == 0x01)
                    isFactoryPattern = true;
                else
                    isFactoryPattern = false;

                /** 提醒设置*/
                temp = new byte[1];
                System.arraycopy(data, 33, temp, 0, 1);
                byte notice = (byte) Hex2Deci(temp, false);
//                BIT[7]：1:开启免打扰   0:关闭免打扰
                if ((notice & (1 << 7)) != 0)

                    isNodisturbDisable = true;
                else
                    isNodisturbDisable = false;
//                BIT[6]：1:关闭饮水提醒             0:开启饮水提醒
                if ((notice & (1 << 6)) != 0)
                    isAllowWaterRemind = false;
                else
                    isAllowWaterRemind = true;
//                BIT[4~5]：系统LED亮度4级
                LEDLuminance = (notice & 0x30) >> 4;
//                BIT[0~3]：系统音量16级
                volume = notice & 0xf;

                /**语音模式*/
                temp = new byte[1];
                System.arraycopy(data, 36, temp, 0, 1);
                systemVoiceMode = Hex2Deci(temp, false);

                /**水杯倾斜度*/
                temp = new byte[2];
                System.arraycopy(data, 54, temp, 0, 2);
                cupTilt = Hex2Deci(temp, false) / 10;
            }  else {
                Log.i(TAG, "initialize ---->S1-S水杯");

                /** 系统时间*/
                temp = new byte[4];
                System.arraycopy(data, 0, temp, 0, 4);
                long mSec = Hex2Deci(temp, false);
                mSec *= 1000;
                Log.d(MYTAG, "timeStamp mSec" + mSec);
                timeStamp = new Date(mSec);

                /** 今日饮水量*/
                temp = new byte[2];
                System.arraycopy(data, 6, temp, 0, 2);
                TodayDrinkAmountOfWater = Hex2Deci(temp, false);
                if (TodayDrinkAmountOfWater < 0) {
                    TodayDrinkAmountOfWater = 0;
                }

                /**水质*/
                temp = new byte[2];
                System.arraycopy(data, 8, temp, 0, 2);
                curWaterQuantity = Hex2Deci(temp, false);
                curWaterQuantity /= 1000;
                if (curWaterQuantity < 0) {
                    curWaterQuantity = 0;
                }

                /**水温*/
                temp = new byte[2];
                System.arraycopy(data, 10, temp, 0, 2);
                temperature = Hex2Deci(temp, false);
                temperature /= 100;
                if (temperature < 0) {
                    temperature = 0;
                }


                /** 电池电量*/
                temp = new byte[1];
                System.arraycopy(data, 12, temp, 0, 1);
                BatteryVoltage = Hex2Deci(temp, false);
                if (BatteryVoltage < 0) {
                    BatteryVoltage = 0;
                }

                /** 水杯状态*/
                temp = new byte[1];
                System.arraycopy(data, 15, temp, 0, 1);
                byte cup = (byte) Hex2Deci(temp, false);

                if ((cup & (1 << 7)) != 0) {//BIT[7] 拿起状态,1:拿起；0：未拿起
                    cupStateBoolean = false;
                } else {
                    if ((cup & (1 << 6)) != 0) {//BIT[6] 倒置状态,1:倒置；0：未倒置
                        cupStateBoolean = false;
                    } else {
                        if ((cup & (1 << 5)) != 0) {//BIT[5] 摇动状态,1:摇动；0：未摇动
                            cupStateBoolean = false;
                        } else {
                            if ((cup & (1 << 4)) == 0) {//BIT[4] 正立状态,1:正立；0：未正立
                                cupStateBoolean = false;
                            } else {
                                cupStateBoolean = true;
                            }
                        }
                    }
                }

                /** 即将发生的系统提醒喝水时间倒计时*/
                temp = new byte[2];
                System.arraycopy(data, 16, temp, 0, 2);
                nextRemindWater = Hex2Deci(temp, false);
                if (nextRemindWater < 0) {
                    nextRemindWater = 0;
                }


                //BIT[3] 杯盖状态,1:盖上；0：未盖上,因为杯盖状态与所用水杯状态无关所以未做操作
                /**重量校准标志*/
                temp = new byte[2];
                System.arraycopy(data, 27, temp, 0, 2);
                weightCalibrationMark = Hex2Deci(temp, false);


                /** 系统工作模式*/
                temp = new byte[1];
                System.arraycopy(data, 32, temp, 0, 1);
                byte factory = (byte) Hex2Deci(temp, false);
                if ((factory & 0x01) == 0x01)
                    isFactoryPattern = true;
                else
                    isFactoryPattern = false;

                /** 提醒设置*/
                temp = new byte[1];
                System.arraycopy(data, 33, temp, 0, 1);
                byte notice = (byte) Hex2Deci(temp, false);
                //BIT[7]：1:开启免打扰   0:关闭免打扰
                if ((notice & (1 << 7)) != 0)

                    isNodisturbDisable = true;
                else
                    isNodisturbDisable = false;
                //  BIT[6]：1:关闭饮水提醒   0:开启饮水提醒
                if ((notice & (1 << 6)) != 0)
                    isAllowWaterRemind = false;
                else
                    isAllowWaterRemind = true;
                //  BIT[4~5]：系统LED亮度4级
                LEDLuminance = (notice & 0x30) >> 4;
                //  BIT[0~3]：系统音量16级
                volume = notice & 0xf;

                /**语音模式*/
                temp = new byte[1];
                System.arraycopy(data, 36, temp, 0, 1);
                byte VoiceMode = (byte) Hex2Deci(temp, false);
                systemVoiceMode = VoiceMode & 0x3;

                /** 功能设置*/
                temp = new byte[1];
                System.arraycopy(data, 37, temp, 0, 1);
                byte function = (byte) Hex2Deci(temp, false);
//                BIT[7]：1:开启异物预警   0:关闭异物预警
                if ((function & (1 << 7)) != 0)
                    isHighSecerytyEnable = true;
                else
                    isHighSecerytyEnable = false;
//                BIT[5]：1:温度单位(℉)  0:温度显示单位(℃)
                if ((function & (1 << 3)) != 0)
                    temperatureStyle = 2;
                else
                    temperatureStyle = 1;
//                BIT[4]：1:水量单位(盎司)    0:温度显示单位(ml)
                if ((function & (1 << 2)) != 0)
                    weightStyle = 2;
                else
                    weightStyle = 1;

                /** 温度预警*/
                temp = new byte[1];
                System.arraycopy(data, 38, temp, 0, 1);
                byte warning = (byte) Hex2Deci(temp, false);
//                BIT[7]：1:开启高温预警   0:关闭高温预警
                if ((notice & (1 << 7)) != 0)

                    isNodisturbDisable = true;
                else
                    isNodisturbDisable = false;
//                BIT[0~6]:高温预警值(℃)
                highTemperature = warning & 0x7f;
                highTemperature += 36;



                /**水杯倾斜度*/
                temp = new byte[2];
                System.arraycopy(data, 54, temp, 0, 2);
                cupTilt = Hex2Deci(temp, false) / 10;
            }


            isRight = true;
        }

    }

    /**
     * 解析请求最新饮水记录返回的命令
     *
     * @author UESTC-PRMI-Burial
     * @date 2014-12-17
     */
    public final static class BleDrinkingRecord extends BleMessage {
        /**
         * The index of drinking record
         */
        public int currentIndex = BleConstant.ERROR_INTEGER;
        /**
         * The timestamp of drinking record
         */
        public Date timeStamp = null;
        /**
         * The intake of drinking
         */
        public float intakeOfwater = BleConstant.ERROR_FLOAT;
        /**
         * The temperature of water
         */
        public float temperature = BleConstant.ERROR_FLOAT;
        /**
         * The quality of water
         */
        public float quality = BleConstant.ERROR_FLOAT;
        /**
         * The volum of power
         */
        public int volumPower = BleConstant.ERROR_INTEGER;
        /**
         * 0x01 mean that it have warn, and the others is trigger by user. Of
         * course, 0x88 is invalid.
         */
        public byte isWarn = BleConstant.sinByteInvalid;
        /**
         * 0x01 if shaking, 0x00 if no shaking. Of course, 0x88 is invalid.
         */
        public byte answer = BleConstant.sinByteInvalid;
        /**
         * crc8
         */
        public byte crc = BleConstant.sinByteInvalid;

        /**
         * True if we have updated data
         */
        public boolean isRight = false;

        /**
         * The size of the DATA of Drinking Record.If you want to know more
         * information, please refer to {@link }
         */
        private final static int DATA_DR_SIZE = 18;

        public BleDrinkingRecord(BleMessage msg) {
            super(msg);
            initialize(msg.data);
        }

        public BleDrinkingRecord(byte[] message) {
            super(message); // BleMessage
            if (super.isRight) {
                Log.i(TAG, "Data is wrong!");
                return;
            }

            initialize(super.data);
        }

        /**
         * Initializes every variable.
         *
         * @param data The data include
         */
        private void initialize(byte[] data) {
            if ((null == data) || (data.length != DATA_DR_SIZE)) {
                return;
            }
            int offset = 0;
            // index
            byte[] temp = new byte[BleConstant.TP_SIZE];
            System.arraycopy(data, offset, temp, 0, BleConstant.TP_SIZE);
            currentIndex = Hex2Deci(temp, false);
            offset += BleConstant.TP_SIZE;
            // timestamp
            temp = new byte[BleConstant.TS_SIZE];
            System.arraycopy(data, offset, temp, 0, BleConstant.TS_SIZE);
            long milSec = Hex2Deci(temp, false);
            if (BleConstant.ERROR_INTEGER != milSec) {
                milSec *= 1000;
                timeStamp = new Date(milSec);
            }
            offset += BleConstant.TS_SIZE;
            // intakeOfwater
            temp = new byte[BleConstant.TP_SIZE];
            System.arraycopy(data, offset, temp, 0, BleConstant.TP_SIZE);
            intakeOfwater = Hex2Deci(temp, false);
            intakeOfwater /= BleConstant.FIXED_POINT_2BYTES;
            offset += BleConstant.TP_SIZE;
            // temperature
            System.arraycopy(data, offset, temp, 0, BleConstant.TP_SIZE);
            temperature = Hex2Deci(temp, false);
            temperature /= BleConstant.FIXED_POINT_2BYTES;
            offset += BleConstant.TP_SIZE;
            // quality
            System.arraycopy(data, offset, temp, 0, BleConstant.TP_SIZE);
            temperature = Hex2Deci(temp, false);
            quality /= BleConstant.FIXED_POINT_2BYTES;
            offset += BleConstant.TP_SIZE;
            // volumPower
            volumPower = Hex2Deci(data[offset++], false);
            // isWarn
            isWarn = data[offset++];
            // answer
            answer = data[offset++];
            // The current index
            /*
             * System.arraycopy(data, offset, temp, 0, BleConstant.TP_SIZE);
			 * currentIndex = Hex2Deci(temp, false);
			 */
            offset += BleConstant.TP_SIZE;
            // crc8
            crc = data[offset];
            isRight = (crc == (getCrc8(data, 0, offset - 1)));

        }

        @Override
        public String toString() {
            String str = "";
            str = "TimeStamp: ";
            if (null == timeStamp) {
                str += "null\n";
            } else {
                str += timeStamp.toString();
                str += '\n';
            }

            str += String
                    .format("intakeOfwater: %#.2f\ntemperature: %#.2f\nquality: %#.2f\nvolumPower: %d\nisWarn: %02X\nanswer: %02X\ncurrentIndex: %d\ncrc: %02X\nAccuracy?: ",
                            intakeOfwater, temperature, quality, volumPower,
                            isWarn, answer, currentIndex, crc);
            str += isRight;
            return str;
        }
    }

    /**
     * 解析请求工厂模式返回的命令
     *
     * @author UESTC-PRMI-Burial
     * @date 2014-12-20
     */
    public final static class BleFactoryMode extends BleMessage {
        /**
         * {@link BleFactoryMode#SWITCHED} if we have switched to factory mode
         * successly, {@link BleFactoryMode#CANCEL} if we have cancelled the
         * request successful, {@link BleConstant#sinByteInvalid}, of course, it
         * means invalid data.
         */
        public byte isSwitched = BleConstant.sinByteInvalid;

        private final byte SWITCHED = 0x00;
        private final byte CANCEL = 0x01;

        /**
         * True if we have updated data
         */
        public boolean isRight = true;

        public BleFactoryMode(BleMessage msg) {
            super(msg);
            initialize(msg.data);
        }

        public BleFactoryMode(byte[] message) {
            super(message); // BleMessage
            if (super.isRight) {
                Log.i(TAG, "Data is wrong!");
                return;
            }

            initialize(super.data);
        }

        @Override
        public String toString() {
            String str = "";
            str = "Accuracy?: ";
            str += isRight;
            str += '\n';

            if (isSwitched == SWITCHED) {
                str += "Switch";
            } else if (isSwitched == CANCEL) {
                str += "Cancel Switched";
            } else {
                str += "Invalid";
            }

            return str;
        }

        /**
         * Initializes every variable.
         *
         * @param data The data include
         */
        private void initialize(byte[] data) {
            if (null == data) {
                isSwitched = SWITCHED;
                isRight = true;
            } else {
                int length = BleConstant.DATA_OF_CANCEL_REQUEST.length;
                if (length == data.length) {
                    for (int i = 0; i < length; ++i) {
                        if (BleConstant.DATA_OF_CANCEL_REQUEST[i] != data[i]) {
                            isRight = false;
                            break;
                        } else {
                            isRight = true;
                            continue;
                        }
                    }
                    if (isRight) {
                        isSwitched = CANCEL;
                    } else {
                        isSwitched = BleConstant.sinByteInvalid;
                    }
                } else {
                    isRight = false;
                    isSwitched = BleConstant.sinByteInvalid;
                }

            }

        }

    }

    /**
     * We have packaged the data of message what the answer to the request which
     * we was require write image to flash.And if you want to know more
     * infomation, please refer to {@link }
     *
     * @author UESTC-PRMI-Burial
     * @date 2015-02-05
     */
    public final static class BleWrtImg extends BleMessage {
        public int dataLength = BleConstant.ERROR_INTEGER;
        public int imgIndex = BleConstant.ERROR_INTEGER;
        public int curPack = BleConstant.ERROR_INTEGER;
        private int tolPack = BleConstant.ERROR_INTEGER;
        public boolean isSuccess = true; // The default value is true.
        private byte[] remain = null; // 1024b: CRC 16 + ADD 16 = 4b

        /**
         * True if we have updated data
         */
        public boolean isRight = true;

        public BleWrtImg(BleMessage msg) {
            super(msg);
            switch (msg.index) {
                case BleConstant.ID_WRITE_PICTURE_32:
                    dataLength = 38;
                    tolPack = 1024;
                    break;
                case BleConstant.ID_WRITE_PICTURE_64:
                    dataLength = 70;
                    tolPack = 512;
                    break;
                case BleConstant.ID_WRITE_PICTURE_1024:
                    dataLength = 10;
                    tolPack = 32;
                    break;
                default:
                    return;
            }
            initialize(msg.data);
        }

        public BleWrtImg(byte[] message) {
            super(message); // BleMessage
            if (super.isRight) {
                Log.i(TAG, "Data is wrong!");
                return;
            }

            switch (super.index) {
                case BleConstant.ID_WRITE_PICTURE_32:
                    dataLength = 38;
                    tolPack = 1024;
                    break;
                case BleConstant.ID_WRITE_PICTURE_64:
                    dataLength = 70;
                    tolPack = 512;
                    break;
                case BleConstant.ID_WRITE_PICTURE_1024:
                    dataLength = 10;
                    tolPack = 32;
                    break;
                default:
                    return;
            }
            initialize(super.data);
        }

        @Override
        public String toString() {
            String str = "";
            str = "Accuracy?: ";
            str += isRight;
            str += '\n';

            str += String.format("imgIndex: %d\ncurPack: %d/%d\nisSuccess:",
                    imgIndex, curPack, tolPack);
            str += isSuccess;

            return str;
        }

        /**
         * Initializes every variable.
         *
         * @param data The data include
         */
        private void initialize(byte[] data) {
            if ((null == data) || (dataLength != data.length)) {
                return;
            }

            int offset = 0;
            byte[] temp = new byte[2];
            System.arraycopy(data, 0, temp, 0, temp.length);
            imgIndex = Hex2Deci(temp, false);
            offset += temp.length;

            System.arraycopy(data, offset, temp, 0, temp.length);
            curPack = Hex2Deci(temp, false);
            offset += temp.length;

            if (0x00 == data[offset++]) {
                isSuccess = (0x00 == data[offset++]);
            } else {
                isSuccess = false;
                ++offset;
            }

            remain = new byte[dataLength - 6];
            System.arraycopy(data, offset, remain, 0, remain.length);

        }

    }

    /**
     * We have packaged the data of message what the answer to the request which
     * we was require to get the image's CRC.And if you want to know more
     * infomation, please refer to {@link }
     *
     * @author UESTC-PRMI-Burial
     * @date 2015-02-05
     */
    public final static class BleImgCrc extends BleMessage {
        public int imgIndex = BleConstant.ERROR_INTEGER;
        public boolean isSuccess = false;
        private byte[] imgCrc = null;

        /**
         * True if we have updated data
         */
        public boolean isRight = true;

        private final static int DATA_IMGCRC_SIZE = 6;

        public BleImgCrc(BleMessage msg) {
            super(msg);
            initialize(msg.data);
        }

        public BleImgCrc(byte[] message) {
            super(message); // BleMessage
            if (super.isRight) {
                Log.i(TAG, "Data is wrong!");
                return;
            }
            initialize(super.data);
        }

        @Override
        public String toString() {
            String str = "";
            str = "Accuracy?: ";
            str += isRight;
            str += '\n';

            if (null != imgCrc) {
                str += String
                        .format("imgIndex: %d\nimgCrc[0]: %02X\nimgCrc[1]: %02X\nisSuccess:",
                                imgIndex, imgCrc[0], imgCrc[1]);
            } else {
                str += String.format("imgIndex: %d\nimgCrc: null\nisSuccess:",
                        imgIndex);
            }
            str += isSuccess;

            return str;
        }

        /**
         * Initializes every variable.
         *
         * @param data The data include
         */
        private void initialize(byte[] data) {
            if ((null == data) || (DATA_IMGCRC_SIZE != data.length)) {
                isRight = false;
                return;
            }

            int offset = 0;
            byte[] temp = new byte[2];
            System.arraycopy(data, 0, temp, 0, temp.length);
            offset += temp.length;
            imgIndex = Hex2Deci(temp, false);

            if (0x00 == data[offset++]) {
                isSuccess = (0x00 == data[offset++]);
            } else {
                isSuccess = false;
                ++offset;
            }

            imgCrc = new byte[2];
            System.arraycopy(data, offset, imgCrc, 0, imgCrc.length);

        }

    }

    /**
     * Returns byte array included the current timestamp in seconds since
     * January 1, 1970 00:00:00.0 UTC
     *
     * @return Return null if fails, Or return byte array included the current
     * timestamp.
     */
    public static byte[] getTimeStamp() {
        byte[] retUTC = null;
        long millis = -1;

        millis = (System.currentTimeMillis() / 1000 + TimeZone.getDefault()
                .getRawOffset() / 1000);
        if ((millis < 0) || (millis > Integer.MAX_VALUE)) {
            return retUTC;
        }

        int seconds = (int) millis;

        retUTC = Deci2Hex(seconds, BleConstant.TS_SIZE);

        return retUTC;
    }


    /**
     * 将蓝牙收发数据字节数组转化成字符串
     *
     * @param msgBytes
     * @return
     */
    public static String DebugMsgBytes2String(byte[] msgBytes) {
        final StringBuilder stringBuilder = new StringBuilder(msgBytes.length);
        for (byte byteChar : msgBytes)
            stringBuilder.append(String.format("%02X ", byteChar));
        return stringBuilder.toString();
    }


}
