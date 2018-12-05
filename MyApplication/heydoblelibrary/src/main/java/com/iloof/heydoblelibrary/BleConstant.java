package com.iloof.heydoblelibrary;

import android.util.Log;

import java.util.UUID;

/**
 * Defined the constants or the static variables
 * 
 * @author UESTC-PRMI-Burial
 * @date 2014-12-13
 *
 */
public final class BleConstant {

	private final static String TAG = "BleConstant";
	/**
	 * Handler传递消息码定义. HM -- Handler Message BLE -- Bluetooth Low Energy CMD --
	 * APP下发指令 MSG -- 水杯传回APP的消息
	 * 
	 */
	/** 该手机不支持BLE */
	public final static int HM_BLE_NONSUPPORT = 0xFF;
	/** 请求开启蓝牙 */
	public final static int HM_BLE_ENABLEBT = 0xF2;
	/** 连接成功 */
	public final static int HM_BLE_CONNECTED = 0xF3;
	/** 连接失败或连接丢失 */
	public final static int HM_BLE_DISCONNECTED = 0xF4;
	/** 读写准备就绪 */
	public final static int HM_BLE_READY = 0xF5;
	/** 读写准备失败 */
	public final static int HM_BLE_READY_FAIL = 0xFE;
	/** 命令写入蓝牙成功 */
	public final static int HM_CMD_WRITED = 0xF6;
	/** 命令写入蓝牙失败 */
	public final static int HM_CMD_FAILURE = 0xFD;

	/** 消息,当前状态1 */
	public final static int HM_MSG_CUR1 = 0xF7;
	/** 消息,当前状态2 */
	public final static int HM_MSG_CUR2 = 0xF8;
	/** 消息,获取单条饮水记录 */
	public final static int HM_MSG_DRINKRECORD = 0xF9;
	/** 消息,获取单条警告记录 */
	public final static int HM_MSG_WARNRECORD = 0xFA;
	/** 消息,设置关断蓝牙连接 */
	/**
	 * 已删除 public final static int HM_MSG_DISCONNECT = 0x0B;
	 */
	/** 消息,设置进入免打扰模式(出厂模式) */
	public final static int HM_MSG_FACTORYMODE = 0x0C;
	/** 消息,设置动画数据（32字节）到FLASH，以备动画显示 */
	public final static int HM_MSG_WPICTURE32 = 0x0D;
	/** 消息,读出动画数据（32字节）到FLASH，以备动画显示 */
	/**
	 * 已删除 public final static int HM_MSG_RPICTURE32 = 0x0E;
	 */
	/** 消息,读出某幅动画数据的CCITT校验 */
	public final static int HM_MSG_PICTURECRC = 0x0F;
	/** 消息,设置动画数据（64字节）到FLASH，以备动画显示 */
	public final static int HM_MSG_WPICTURE64 = 0x10;
	/** 消息,重启蓝牙密码 */
	public final static int HM_MSG_PASSWORD = 0x11;
	/** 消息,模式设置 */
	public final static int HM_MSG_CHANGEMODE = 0x12;
	/** 消息,每日饮水目标设定 */
	public final static int HM_MSG_SETAIM = 0x13;
	/** 消息,修改蓝牙模块名称 */
	public final static int HM_MSG_RENAME = 0x14;
	/** 消息,高安全模式 */
	public final static int HM_MSG_HIGHLEVEL = 0x15;
	/** 消息,消息文字推送 */
	public final static int HM_MSG_WORDS = 0x16;
	/** 消息,设置动画数据(1024字节)到FLASH,以备动画显示 */
	public final static int HM_MSG_WPICTURE1024 = 0x17;

	/** 消息设置每日饮水目标 */
	public static final int HM_MSG_DAY_DRINK_GOAL = 0x18;
	/** 消息设置头像到flash */
	public static final int HM_MSG_SETPICTRUE = 0x19;
	/** 消息升级水杯固件 */
	public static final int HM_MSG_UPDATECUP = 0X20;
	/** Toast, 系统信息(测试用) */
	public final static int HM_DEBUG_TOAST = 0x88;
	/** Command(测试用) */
	public final static int HM_DEBUG_CMD = 0x89;
	/** Message(测试用) */
	public final static int HM_DEBUG_MSG = 0x90;
	/** 消息命令发送失败 */
	public final static int HM_MSG_SENDFAIL = 0x91;
	/** The flag which is drinking record */
	public final static int ID_DR = 1;
	/** The flag which is warning record */
	public final static int ID_WNR = 2;

	/** The fixed-point number of two bytes */
	public final static int FIXED_POINT_2BYTES = 100;
	/** The fixed-point number of three bytes */
	public final static int FIXED_POINT_4BYTES = 1000;

	/** The Invalid data for single byte */
	public final static byte sinByteInvalid = (byte) 0x88;
	/** The Invalid data for double bytes */
	public final static byte[] douByteInvalid = { (byte) 0x88, (byte) 0x88 };
	/** The Invalid data for trinary bytes */
	public final static byte[] triByteInvalid = { (byte) 0x88, (byte) 0x88,
			(byte) 0x88 };
	/** The Invalid data for quadruplex bytes */
	public final static byte[] quaByteInvalid = { (byte) 0x88, (byte) 0x88,
			(byte) 0x88, (byte) 0x88 };

	/** A file what save some cache */
	public final static String BLE_CACHE = "BleCache";
	/** The name of the preference about the bluetooth address */
	public final static String BLE_PREF_MAC = "BleMac";
	/** The name of the preference about the index of Drinking Record */
	public final static String BLE_PREF_DR = "BleDR";
	/** The name of the preference about the index of WarNing Record */
	public final static String BLE_PREF_WNR = "BleWNR";
	/** The name of the preference about the Equipment Code */
	public final static String BLE_PREF_ECH = "BleECHigh";
	/** The name of the preference about the Equipment Code */
	public final static String BLE_PREF_ECL = "BleECLow";

	/** 主界面中的蓝牙按钮状态是否为打开 */
	// public final static String BLE_IS_OPEN = "bleIsOpen";

	/** 网络请求的token */
	public final static String TOKEN = "token";
	/** 登录状态 */
	public final static String IS_LOGIN = "logined";

	/** ppm记录与杯盖记录是否同步 */
	public static final String ACT_LIBS_TOUCHES_INITED = "libsTouchesInited";
	/** 上次存储的杯盖记录 */
	public static final String ACT_SAVED_LIBS = "savedLibs";
	/** 上次存储的ppm记录条数 */
	public static final String ACT_SAVED_TOUCHES = "savedTouches";
	/** 饮水目标 */
	public static final String ACT_DRINK_GOAL = "actDrinkGoal";
	/** 小时数 */
	public static final String DG_HOUT = "DrinkGoalHour";
	/** 分钟数 */
	public static final String DG_MINUTE = "DrinkGoalMinute";
	/** 单次饮水量设置 */
	public static final String ONE_DRINK_VALUE = "oneDrinkValue";
	/** 硬件版本号 */
	public static final String HARD_VERSION = "hardVersion";
	/** 杯子型号 */
	public static final String HARD_MODEL = "hardModel";
	/** 饮水记录索引 */
	public static final String DRINKRECORDS_INDEX = "drinkRecords_index";
	/** 硬件序列号 */
	public static final String HARD_SERIOUS = "hardSerious";

	/** 经度 */
	public final static String LNG = "lng";
	/** 纬度 */
	public final static String LAT = "lat";
	/** 饮水量 */
	public final static String WATER_INTAKE = "waterIntake";

	/** True If You Want To print The Debug Information */
	public final static boolean D = true;
	/** The flag about the integer what is wrong */
	public final static int ERROR_INTEGER = 0x88888888;
	/** The flag about the float what is wrong */
	public final static float ERROR_FLOAT = 0x88888888;

	/** The UUID of the characteristic for write */
	// ffe9
	public final static UUID UUID_WRITE = UUID
			.fromString("0000ffe9-0000-1000-8000-00805f9b34fb");
	/** The UUID of the characteristic for read */
	// ffe4
	public final static UUID UUID_READ = UUID
			.fromString("0000ffe4-0000-1000-8000-00805f9b34fb");
	// ffe5
	public final static UUID UUID_FLAG = UUID
			.fromString("0000ffe5-0000-1000-8000-00805f9b34fb");

	/** The Size(byte) Of Head */
	public final static int HD_SIZE = 1;
	/** The Size(byte) Of Pack_sn */
	public final static int SN_SIZE = 2;
	/** The Size(byte) Of Index */
	public final static int ID_SIZE = 2;
	/** The Size(byte) Of Ack */
	public final static int AK_SIZE = 1;
	/** The Size(byte) Of Data Length */
	public final static int DL_SIZE = 2;
	/** The Size(byte) Of Check */
	public final static int CK_SIZE = 2;
	/** The Size(byte) Of End */
	public final static int ED_SIZE = 1;

	/** TimeStamp Size */
	public final static int TS_SIZE = 4;
	/** The Size Of Password */
	public final static int PASSWORD_SIZE = 6;
	/** The Size Of Trigger Condition */
	public final static int TRIGGER_CONDITION_SIZE = 8;

	/**
	 * The Data Of The Command Which Require Cup To Cancel Disconnect Or Switch
	 * Mode
	 */
	public final static byte[] DATA_OF_CANCEL_REQUEST = { (byte) 0x88,
			(byte) 0x88, (byte) 0x82, (byte) 0xB1 };

	/** The Head Code */
	public final static byte HEAD_CODE = 0x54;
	/** The End Code */
	public final static byte END_CODE = 0x53;
	/** The Ack Code */
	public final static byte ACK_CODE = 0x00;
	/** The Equipment Code */
	public static byte[] EQU_CODE = { (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
			(byte) 0xFF, (byte) 0xFF, (byte) 0xFF };
	/** The Size(byte) Of Equipment Code */
	public final static int EQ_SIZE = 6;

	/**
	 * Setting the equipment code, And write it to {@link BleConstant#BLE_CACHE}
	 * 
	 * @param equCode
	 *            The equipment code
	 * 
	 * @return Ture if setting successful, Or return false;
	 * 
	 */
	public static boolean setEquipmentCode(byte[] equCode) {
		if ((equCode == null) || (equCode.length != EQ_SIZE)) {
			return false;
		}

		System.arraycopy(equCode, 0, EQU_CODE, 0, EQ_SIZE);
		Log.i(TAG, "Setting equipment code successfully!");

		return true;
	}

	/**
	 * Index Code Array.The order of this array corresponding to
	 * {@link }. Please refer to {@link }
	 */

	public static byte[][] INDEX_ARRAY ;

	/**
	 * 获取系统状态信息1
	 */
	public final static int ID_CURRENT_STATUS_ONE = 0;
	/**
	 * 获取系统状态信息2
	 */
	public final static int ID_CURRENT_STATUS_TWO = 1;
	/**
	 * 获取系统校准时间
	 */
	public final static int ID_TIMING = 2;
	/**
	 * 获取系统状态信息4
	 */
	public final static int ID_CURRENT_STATUS_FOUR = 3;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get
	 * the common status. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_COMMON_STATUS = 4;
	/**
	 * 获取最新饮水记录
	 */
	public final static int ID_DRINKING_RECORD = 5;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get a
	 * drinking record then shut down BLE. And if you want to know the detail
	 * which about the form of the data of the command and the answer from BLE,
	 * please refer to the {@link }
	 */
	public final static int ID_DRINKING_RECORD_THEN_SHUT = 6;
	/**
	 * 获取最新警告记录
	 */
	public final static int ID_WARNING_RECORD = 7;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get a
	 * warning record then shut down BLE. And if you want to know the detail
	 * which about the form of the data of the command and the answer from BLE,
	 * please refer to the {@link }
	 */
	public final static int ID_WARNING_RECORD_THEN_SHUT = 8;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get a
	 * working record. And if you want to know the detail which about the form
	 * of the data of the command and the answer from BLE, please refer to the
	 * {@link }
	 */
	public final static int ID_WORKING_RECORD = 9;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get a
	 * working record then shut down BLE. And if you want to know the detail
	 * which about the form of the data of the command and the answer from BLE,
	 * please refer to the {@link }
	 */
	public final static int ID_WORKING_RECORD_THEN_SHUT = 10;
	/**
	 * 获取多条饮水记录
	 */
	public final static int ID_DRINKING_RECORDS = 11;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get
	 * some warning records. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_WARNING_RECORDS = 12;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get
	 * some working records. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_WORKING_RECORDS = 13;
	// /** The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to
	// get the factory serial number. And if you want to know the detail which
	// about the form of the data of the command and the answer from BLE, please
	// refer to the {@link BleProtocol} */
	// public final static int ID_FACTORY_SN = 14;
	/** 获取最新触摸记录命令号 */
	public final static int ID_GET_LATEST_TOUCH_RECORD = 14;
	/**
	 * 获取断开蓝牙连接
	 */
	public final static int ID_DISCONNECT = 15;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to set
	 * the factory mode. And if you want to know the detail which about the form
	 * of the data of the command and the answer from BLE, please refer to the
	 * {@link }
	 */
	public final static int ID_FACTORY_MODE = 16;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to write
	 * 32 byte of the picture to cup.
	 * 获取写
	 */
	public final static int ID_WRITE_PICTURE_32 = 17;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to read
	 * 32 byte of the picture from cup. And if you want to know the detail which
	 * about the form of the data of the command and the answer from BLE, please
	 * refer to the {@link }
	 */
	public final static int ID_READ_PICTURE_32 = 18;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to read
	 * the Cyclic Redundancy Check(CRC) which is related to the special picture.
	 * And if you want to know the detail which about the form of the data of
	 * the command and the answer from BLE, please refer to the
	 * {@link }
	 */
	public final static int ID_READ_PICTURE_CRC = 19;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to write
	 * 1024 byte of the picture to cup. And if you want to know the detail which
	 * about the form of the data of the command and the answer from BLE, please
	 * refer to the {@link }
	 */
	public final static int ID_WRITE_PICTURE_1024 = 20;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to read
	 * 1024 byte of the picture from cup. And if you want to know the detail
	 * which about the form of the data of the command and the answer from BLE,
	 * please refer to the {@link }
	 */
	public final static int ID_READ_PICTURE_1024 = 21;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to
	 * display one picture. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_DISPLAY_ONE_PICTURE = 22;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to
	 * display many picture. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_DISPLAY_MORE_PICTURE = 23;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to set
	 * the order of the logo picture. And if you want to know the detail which
	 * about the form of the data of the command and the answer from BLE, please
	 * refer to the {@link BleProtocol}
	 */
	// public final static int ID_SET_LOGO_PICTURE = 24;
	/** 设置每日饮水目标的命令索引 */
	public final static int ID_SET_DAY_DRINK_GOAL = 24;

	/** 获取每日饮水目标 */
	public final static int ID_GET_DAY_DRINK_GOAL = 25;
	/** 获取温度提醒(S1-S)*/
	 public final static int ID_GET_REMIND_TEMP= 26;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to reboot
	 * the cup. And if you want to know the detail which about the form of the
	 * data of the command and the answer from BLE, please refer to the
	 * {@link BleProtocol}
	 */
	// public final static int ID_REBOOT_CUP = 27;
	/** 高安全命令号 */
	public final static int ID_HEIGH_SAFE_CONTROL = 28;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to
	 * restore the factory settings. And if you want to know the detail which
	 * about the form of the data of the command and the answer from BLE, please
	 * refer to the {@link BleProtocol}
	 */
	// public final static int ID_FACTORY_SETTINGS = 29;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to write
	 * 64 byte of the picture to cup. And if you want to know the detail which
	 * about the form of the data of the command and the answer from BLE, please
	 * refer to the {@link }
	 */
	public final static int ID_WRITE_PICTURE_64 = 30;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to read
	 * 64 byte of the picture from cup. And if you want to know the detail which
	 * about the form of the data of the command and the answer from BLE, please
	 * refer to the {@link }
	 */
	public final static int ID_READ_PICTURE_64 = 31;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to reset
	 * the password of the BLE. And if you want to know the detail which about
	 * the form of the data of the command and the answer from BLE, please refer
	 * to the {@link }
	 */
	public final static int ID_RESET_PASSWORD = 32;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to change
	 * the mode of the cup. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_CHANGE_MODE = 33;
	/** 免打扰命令号 */
	public final static int ID_SET_NO_DISTURBING = 34;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to change
	 * the bluetooth's name. And if you want to know the detail which about the
	 * form of the data of the command and the answer from BLE, please refer to
	 * the {@link }
	 */
	public final static int ID_CHANGE_NAME = 35;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to the
	 * high level about the warm. And if you want to know the detail which about
	 * the form of the data of the command and the answer from BLE, please refer
	 * to the {@link }
	 */
	public final static int ID_HIGH_LEVEL = 36;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to pass
	 * the words. And if you want to know the detail which about the form of the
	 * data of the command and the answer from BLE, please refer to the
	 * {@link }
	 */
	public final static int ID_WORDS = 37;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean the message is
	 * send without request by BLE. And if you want to know the detail which
	 * about the form of the data of the message, please refer to the
	 * {@link }
	 */
	public final static int ID_CRASHER = 38;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to reset
	 * the cup. And if you want to know the detail which about the form of the
	 * data of the message, please refer to the {@link }
	 */
	public final static int ID_RESET = 39;
	/**
	 * 恢复出厂
	 */
	public final static int ID_FACTORY_RESET = 40;
	/**
	 * The Index of {@link BleConstant#INDEX_ARRAY} which mean we want to get
	 * the Cup hardWare version
	 */
	public final static int ID_CUP_HARDWARE_VERSION = 41;
	/** 设置闹钟的命令号 */
	public final static int ID_SET_ALARM = 42;
	/** 获取最新杯盖记录命令号 */
	public final static int ID_GET_LATEST_LID_STATE = 43;
	/** 获取多条杯盖记录命令号 */
	public final static int ID_GET_LIDS_STATE = 44;
	/** 获取多条触摸记录 */
	public final static int ID_GET_TOUCHS_RECORD = 45;
	/** 开启工厂模式 */
	public final static int ID_FACTORY_PATTERN = 46;
	/** 关闭工厂模式 */
	public final static int ID_EXTINGUISH_FACTORY = 47;
	/** 自动矫正杯子重量命令号 */
	public final static int ID_ADJUST_WEIGHT = 48;
	/** 获取序列号的命令 */
	public final static int ID_SERIOUS_NUM = 49;
	/** 发送文件ID */
	public final static int ID_SEND_FILE = 50;
	/** 干杯ID */
	public final static int ID_CHEERS = 51;
	/** 发送饮水提醒 */
	public final static int ID_WATERREMIND = 52;
	/** 升级固件 */
	public final static int ID_UPDATEHEYDO = 53;
	/** 水杯软件版本 */
	public final static int ID_CUP_SOFT_VERSION = 54;

	/** 获取工厂模式状态 */
	public final static int ID_GET_FACTORY_STATUS = 55;

	/** 获取所有定时器状态 */
	public final static int ID_GET_ALL_TIMER = 56;

	/** 获取所有定时器倒计时 */
	public final static int ID_GET_ALL_TIMER_TIME = 57;

	/** 获取指定定时器 */
	public final static int ID_GET_TIMER = 58;
	
	/**设置高温预警阈值*/
	public final static int ID_SET_HIGH_TEMPERATURE=59;
	/**获取高温预警阈值*/
	public final static int ID_GET_HIGH_TEMPERATURE=60;
	
	/** 亮屏的命令号 */
	public final static int ID_LIGTH_SCREEN = 61;

	/** 温度格式的命令号 */
	public final static int ID_TEMPARATURE_STYLE = 62;

	/** 水量计量格式的命令号 */
	public final static int ID_WEIGHT_STYLE = 63;

	/** 清空记录*/
	public final static int ID_DELETE_RECORDS = 64;

	/** 蜂鸣器鸣叫*/
	public final static int ID_BUZZER_SOUND = 65;

	/** 水杯恒温*/
	public final static int ID_CUP_CONSTANT_TEMPRATURE = 66;

	/** 开水温度降低提醒*/
	public final static int ID_WATER_TEMPRATURE_LOW= 67;

	/** 自定义免打扰*/
	public final static int ID_CUSTOM_NO_DISTURB = 68;

	/** 自定义界面显示*/
	public final static int ID_CUSTOM_UI_SHOW= 69;

	/** 单色灯闪烁*/
	public final static int ID_SINGLE_LIGHT= 70;

    /** 颜色列表闪烁*/
    public final static int ID_LIST_LIGHT= 71;

	/** 音量调节*/
	public final static int ID_SOUND_AJUST= 72;

	/** 亮度调节*/
	public final static int ID_LIGHT_AJUST= 73;

	/** 存储器上的软硬件版本*/
	public final static int ID_MEMORY_VERSION= 74;

	/** 语音模式*/
	public final static int ID_VOICE_MODE= 75;

	/** 显示JPG头像*/
	public final static int ID_SHOW_JPEG_HEAD= 76;

	/** 添加饮水记录*/
	public final static int ID_ADD_WATER_REMIND= 77;
	
	/** 系统蓝牙未打开 */
	public final static int ID_SYS_BLUETOOTH_NOT_ENABLE = 200;

	/** The maximum size of Command can send through the special characteristic */
	public final static int MSG_MAX_SIZE = 20;
	/** The maximum size of Command allow send */
	public final static int CMD_MAX_SIZE = 2048;
	/** The minimum size of Command */
	public static final int CMD_MIN_SIZE = 17;
	/** The size of queue what is used as a receiver for message or command */
	public final static int QUEUE_SIZE = 1024 * 100;
	/**
	 * The size of the Equipment code of Message what received from BLE,If you
	 * want to know more information, please refer to {@link }
	 */
	public final static int EM_SIZE = 16;
	/**
	 * The size of the TemPerature.If you want to know more information, please
	 * refer to {@link }
	 */
	public final static int TP_SIZE = 2;
	/**
	 * The size of the Volum of Water .If you want to know more information,
	 * please refer to {@link }
	 */
	public final static int VW_SIZE = 2;
	/**
	 * The size of the CURrent Water Quality.If you want to know more
	 * information, please refer to {@link }
	 */
	public final static int CUR_WQ_SIZE = 2;
	/**
	 * The size of the LaST Water Quality.if you want to know more information,
	 * please refer to {@link }
	 */
	public final static int LST_WQ_SIZE = 2;
	/**
	 * The size of Warn Drinking.If you want to know more information, please
	 * refer to {@link }
	 */
	public final static int WD_SIZE = 1;
	/**
	 * The size of the Volum of Electric.If you want to know more information,
	 * please refer to {@link }
	 */
	public final static int VE_SIZE = 1;
	/**
	 * The size of the index of Drinking Records.If you want to know more
	 * information, please refer to {@link }
	 */
	public final static int RECORD_ID_SIZE = 2;

	public final static int PICTURE_SIZE = 32 * 1024;

	public static void setIndexArray(int type){
		if (type == 0){
			INDEX_ARRAY = new byte[][]{
					{ 0x11, 0x02 },			//获取当前设备的状态之一						0
					{ 0x11, 0x03 },    		//获取系统的常用状态信息						1
					{ 0x11, 0x04 },			//无											2
					{ 0x11, 0x05 },			//获取系统的常用状态信息						3
					{ 0x11, 0x06 },			//获取系统的常用状态信息						4
					{ 0x11, 0x07 },			//获取最新饮水历史纪录							5
					{ 0x11, 0x08 },			//获取单条饮水历史纪录							6
					{ 0x11, 0x09 },			//获取最新告警纪录								7
					{ 0x11, 0x0A },			//获取单条告警纪录								8
					{ 0x11, 0x0B },			//获取最新提醒纪录								9
					{ 0x11, 0x0C },			//获取单条提醒纪录								10
					{ 0x11, 0x0D },			//获取多条饮水历史纪录							11
					{ 0x11, 0x0E },			//获取多条告警纪录								12
					{ 0x11, 0x0F },			//获取多条提醒纪录								13
					{ 0x11, 0x10 },			//获取最新触摸纪录								14
					{ 0x12, 0x01 },			//无											15
					{ 0x12, 0x02 },			//无											16
					{ 0x12, 0x10 },			//无											17
					{ 0x12, 0x11 },         //无											18
					{ 0x12, 0x12 },			//读出某幅动画32K数据的CCITT校验				19
					{ 0x12, 0x13 },			//无											20
					{ 0x12, 0x14 },			//无											21
					{ 0x12, 0x15 },			//设置静态显示一幅动画的编号					22
					{ 0x12, 0x16 },			//设置连续显示多幅动画的编号					23
					{ 0x12, 0x17 },			//每日饮水目标设置								24
					{ 0x12, 0x18 },			//获取每日饮水目标								25
					{ 0x12, 0x19 },			//												26
					{ 0x12, 0x1A },			//获取饮水提醒间隔ML							27
					{ 0x12, 0x1B },			//开启（关闭）水质高安全模式设置/获取			28
					{ 0x12, 0x1C },			//开启（关闭）饮水提醒设置/获取					29
					{ 0x12, 0x20 },			//无											30
					{ 0x12, 0x21 },			//无											31
					{ 0x12, 0x1B },			//重复											32
					{ 0x12, 0x1C },			//重复											33
					{ 0x12, 0x1D },			//开启（关闭）免打扰设置/获取					34
					{ 0x12, 0x1E },			//修改蓝牙名称									35
					{ 0x12, 0x1F },			//无											36
					{ 0x12, 0x22 },			//无											37
					{ 0x21, 0x02 },			//无											38
					{ 0x22, 0x02 },			//复位指令										39
					{ 0x22, 0x03 },			//保留指令										40
					{ 0x40, 0x42 },			//获取当前硬件版本号							41
					{ 0x55, 0x01 },			//设置指定定时器								42
					{ 0x11, 0x13 },			//获取最新杯盖纪录								43
					{ 0x11, 0x15 },			//获取多条杯盖纪录								44
					{ 0x11, 0x12 },			//获取多条触摸纪录								45
					{ 0x13, 0x21 },			//进入工厂模式									46
					{ 0X13, 0x22 },			//退出工厂模式									47
					{ 0x30, 0x32 },			//自动重量校准（把当前值作为去皮）				48
					{ 0X40, 0X44 },			//获取生产编号及SN号							49
					{ 0x13, 0x3A },			//发送文件										50
					{ 0x50, 0x01 },			//远程干杯										51
					{ 0x55, 0x05 },			//APP提醒饮水									52
					{ 0x13, 0x37 },			//开始升级APP，									53
					{ 0x40, 0x41 },			//获取当前软件版本号							54
					{ 0x60, 0x00 },			//获取工厂模式状态								55
					{ 0x55, 0x03 },			//获取所有定时器状态							56
					{ 0x55, 0x04 },			//一键获取所有定时器倒计时						57
					{ 0x55, 0x02 },			//获取指定定时器值								58
					{ 0x55, 0x06 },			//高温预警阀值设置								59
					{ 0x55, 0x07 },			//高温预警阀值获取								60
					{ 0x22, 0x0B },			//亮屏模式设置									61
					{ 0x22, 0x0C },			//设置温度显示格式								62
					{ 0x22, 0x0D },			//设置水量计量格式								63
					{ 0x22, 0x07 },			//清除记录										64
					{ 0x22, 0x06 },			//蜂鸣器鸣叫									65
					{ 0x22, 0x0E },			//水杯恒温										66
					{ 0x22, 0x0F },			//开水温度降低提醒								67
					{ 0x22, 0x10 },			//自定义免打扰									68
					{ 0x22, 0x11 } };		//自定义显示									69
		}else if (type == 1){
			INDEX_ARRAY = new byte[][]{

					{ 0x20, 0x03 },			//获取当前设备的状态之一						0
					{ 0x11, 0x03 },    		//												1
					{ 0x10, 0x01 },			//系统时间校准									2
					{ 0x11, 0x05 },			//												3
					{ 0x11, 0x06 },			//												4
					{ 0x20, 0x04 },			//获取最新饮水历史纪录							5
					{ 0x20, 0x05 },			//获取单条饮水历史纪录							6
					{ 0x11, 0x09 },			//												7
					{ 0x11, 0x0E },			//获取单条告警纪录								8
					{ 0x20, 0x06 },			//获取最新提醒纪录								9
					{ 0x20, 0x07 },			//获取单条提醒纪录								10
					{ 0x20, 0x08 },			//获取多条饮水历史纪录							11
					{ 0x11, 0x0E },			//												12
					{ 0x20, 0x09 },			//获取多条提醒纪录								13
					{ 0x11, 0x10 },			//												14
					{ 0x12, 0x01 },			//												15
					{ 0x12, 0x02 },			//												16
					{ 0x12, 0x10 },			//												17
					{ 0x12, 0x11 },         //												18
					{ 0x12, 0x12 },			//												19
					{ 0x12, 0x13 },			//												20
					{ 0x12, 0x14 },			//												21
					{ 0x12, 0x15 },			//												22
					{ 0x12, 0x16 },			//												23
					{ 0x10, 0x0B },			//每日饮水目标设置								24
					{ 0x10, 0x0C },			//获取每日饮水目标..... 						25
					{ 0x12, 0x19 },			//						                        26
					{ 0x12, 0x1A },			//获取饮水提醒间隔ML....						27
					{ 0x12, 0x1B },			//开启（关闭）水质高安全模式设置/获取....		28
					{ 0x12, 0x1C },			//开启（关闭）饮水提醒设置/获取...				29
					{ 0x12, 0x20 },			//无											30
					{ 0x12, 0x21 },			//无											31
					{ 0x12, 0x1B },			//重复	...										32
					{ 0x12, 0x1C },			//重复	...										33
					{ 0x10, 0x04 },			//设置提醒模式									34
					{ 0x11, 0x00 },			//修改蓝牙名称									35
					{ 0x12, 0x1F },			//												36
					{ 0x12, 0x22 },			//												37
					{ 0x21, 0x02 },			//												38
					{ (byte)0xA0, 0x07 },	//重启水杯										39
					{ 0x10, 0x06 },			//恢复出厂设置									40
					{ (byte)0xB0, 0x01 },	//获取当前硬件版本号							41
					{ 0x10, 0x0D },			//设置指定定时器....							42
					{ 0x11, 0x13 },			//获取最新杯盖纪录.....							43
					{ 0x11, 0x15 },			//获取多条杯盖纪录.....							44
					{ 0x11, 0x12 },			//获取多条触摸纪录.....							45
					{ 0x13, 0x21 },			//进入工厂模式.....								46
					{ 0x10, 0x02 },			//设置系统工作模式							47
					{ 0x10, 0x07 },			//自动重量校准（把当前值作为去皮）				48
					{ (byte)0xB0, 0x03 },			//获取生产编号及SN号					49
					{ 0x30, 0x01 },			//发送文件										50
					{ 0x50, 0x10 },			//远程干杯.....									51
					{ 0x55, 0x05 },			//APP提醒饮水.....								52
					{ (byte)0xA0, 0x02 },			//开始升级APP，							53
					{ (byte)0xA0, 0x05 },			//获取当前软件版本号					54
					{ 0x60, 0x00 },			//获取工厂模式状态.....							55
					{ 0x55, 0x03 },			//获取所有定时器状态.....						56
					{ 0x55, 0x04 },			//一键获取所有定时器倒计时.....					57
					{ 0x10, 0x0E },			//获取指定定时器值.....							58
					{ 0x55, 0x06 },			//高温预警阀值设置.....							59
					{ 0x55, 0x07 },			//高温预警阀值获取.....							60
					{ 0x22, 0x0B },			//亮屏模式设置.....								61
					{ 0x22, 0x0C },			//设置温度显示格式.....							62
					{ 0x22, 0x0D },			//设置水量计量格式.....							63
					{ (byte)0xA0, 0x01 },			//清除记录.....							64
					{ 0x40, 0x00 },			//蜂鸣器鸣叫									65
					{ 0x22, 0x0E },			//水杯恒温.....									66
					{ 0x22, 0x0F },			//开水温度降低提醒.....							67
					{ 0x22, 0x10 },			//自定义免打扰.....								68
					{ 0x22, 0x11 },         //自定义显示.....								69
					{ 0x40, 0x02 },         //单色灯闪烁									70
					{ 0x40, 0x04 },         //单色灯闪烁									71
					{ 0x50, 0x01 },         //音量测试										72
					{ 0x50, 0x02 },         //亮度测试										73
					{ (byte)0xA0, 0x06 },			//获取存储器上的软硬件版本			    74
					{ 0x11, 0x02 },			//语音模式			                            75
			};
		}else {
			INDEX_ARRAY = new byte[][]{

					{ 0x20, 0x03 },			//获取当前设备的状态之一						0
					{ 0x11, 0x03 },    		//												1
					{ 0x10, 0x01 },			//系统时间校准									2
					{ 0x11, 0x05 },			//												3
					{ 0x0F, 0x0F },			//无用												4
					{ 0x20, 0x04 },			//获取最新饮水历史纪录							5
					{ 0x20, 0x05 },			//获取单条饮水历史纪录							6
					{ 0x11, 0x0F },			//.....无用									    7
					{ 0x11, 0x0E },			//												8
					{ 0x20, 0x06 },			//获取最新提醒纪录								9
					{ 0x20, 0x07 },			//获取单条提醒纪录								10
					{ 0x20, 0x08 },			//获取多条饮水历史纪录							11
					{ 0x11, 0x0E },			//												12
					{ 0x20, 0x09 },			//获取多条提醒纪录								13
					{ 0x11, 0x10 },			//												14
					{ 0x12, 0x01 },			//												15
					{ 0x12, 0x02 },			//												16
					{ 0x12, 0x10 },			//												17
					{ 0x12, 0x11 },         //												18
					{ 0x12, 0x12 },			//												19
					{ 0x12, 0x13 },			//												20
					{ 0x12, 0x14 },			//												21
					{ 0x60, 0x01 },			//显示静态(单帧)图像							22
					{ 0x60, 0x03 },			//显示动态图像									23
					{ 0x10, 0x0B },			//每日饮水目标设置								24
					{ 0x10, 0x0C },			//获取每日饮水目标..... 						25
					{ 0x11, 0x09 },			//获取温度提醒....						        26
					{ 0x12, 0x1A },			//获取饮水提醒间隔ML....						27
					{ 0x11, 0x0A },			//开启（关闭）水质高安全模式设置/获取....		28
					{ 0x12, 0x1C },			//开启（关闭）饮水提醒设置/获取...				29
					{ 0x12, 0x20 },			//无											30
					{ 0x12, 0x21 },			//无											31
					{ 0x12, 0x1B },			//重复	...										32
					{ 0x12, 0x1C },			//重复	...										33
					{ 0x10, 0x04 },			//设置提醒模式									34
					{ 0x11, 0x00 },			//修改蓝牙名称									35
					{ 0x12, 0x1F },			//												36
					{ 0x12, 0x22 },			//												37
					{ 0x21, 0x02 },			//												38
					{ (byte)0xA0, 0x07 },	//重启水杯										39
					{ 0x10, 0x06 },			//恢复出厂设置									40
					{ (byte)0xB0, 0x01 },	//获取当前硬件版本号							41
					{ 0x10, 0x0D },			//设置指定定时器....							42
					{ 0x11, 0x13 },			//获取最新杯盖纪录.....							43
					{ 0x11, 0x15 },			//获取多条杯盖纪录.....							44
					{ 0x11, 0x12 },			//获取多条触摸纪录.....							45
					{ 0x13, 0x21 },			//进入工厂模式.....								46
					{ 0x10, 0x02 },			//设置系统工作模式							    47
					{ 0x10, 0x07 },			//自动重量校准（把当前值作为去皮）				48
					{ (byte)0xB0, 0x03 },			//获取生产编号及SN号					49
					{ 0x30, 0x01 },			//发送文件										50
					{ 0x40, 0x07 },			//远程干杯.....									51
					{ 0x40, 0x06 },			//APP提醒饮水.....								52
					{ (byte)0xA0, 0x02 },			//开始升级APP，							53
					{ (byte)0xA0, 0x05 },			//获取当前软件版本号					54
					{ 0x60, 0x00 },			//获取工厂模式状态.....							55
					{ 0x55, 0x03 },			//获取所有定时器状态.....						56
					{ 0x55, 0x04 },			//一键获取所有定时器倒计时.....					57
					{ 0x10, 0x0E },			//获取指定定时器值.....							58
					{ 0x11, 0x06 },			//高温预警阀值设置.....							59
					{ 0x11, 0x07 },			//高温预警阀值获取.....							60
					{ 0x22, 0x0B },			//亮屏模式设置.....								61
					{ 0x22, 0x0C },			//设置温度显示格式.....						--	62
					{ 0x22, 0x0D },			//设置水量计量格式.....						--	63
					{ (byte)0xA0, 0x01 },			//清除记录.....							64
					{ 0x40, 0x00 },			//蜂鸣器鸣叫									65
					{ 0x22, 0x0E },			//水杯恒温.....									66
					{ 0x11, 0x08 },			//开水温度降低提醒.....							67
					{ 0x22, 0x10 },			//自定义免打扰.....								68
					{ 0x22, 0x11 },         //自定义显示.....								69
					{ 0x40, 0x02 },         //单色灯闪烁									70
					{ 0x40, 0x04 },         //单色灯闪烁									71
					{ 0x50, 0x01 },         //音量											72
					{ 0x50, 0x02 },         //亮度											73
					{ (byte)0xA0, 0x06 },	//获取存储器上的已存储的固件信息			    74
					{ 0x11, 0x02 },			//语音模式			                            75
					{ 0x63, 0x00 },			//指定页编号JPG文件转换位图存储		            76
					{ 0x20, 0x0C },			//添加饮水			                            77
			};

		}




//		6.2.3	获取系统工作模式设置（ID:  0x1003）	17
//		6.2.5	获取提醒模式设置（ID:  0x1005）	18
//		6.2.8	获取重量校准值（ID   0x1008）	19
//		6.2.9	设置重量比例系数（ID:  0x1009）	19
//		6.2.10	获取重量比例系数（ID   0x100A）	19
//
//
//		6.2.23	设置温度提醒（ID:  0x1108）	25                  -----
//		6.2.24	获取设置温度提醒值（ID   0x1109）	26
//		6.2.25	安全及其他设置（ID:  0x110A）	26     ---- 这个还包含有温蒂重量单位设置
//		6.2.26	获取安全及其他值（ID   0x110B）	27
//		6.3.1	获取水杯属性（ID： 0x1000）	28
//
//		6.3.14	自定义添加的饮水纪录  （ID： 0x200C）	33   --到时试一会添加饮水也加到水杯上
	}

}
