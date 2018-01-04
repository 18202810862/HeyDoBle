package com.iloof.heydoblelibrary.app;

/**
 * 程序所用常量
 * 
 * @author bingchuan
 *
 */
public class Const {

	/**
	 * 	保存水杯和蓝牙连接的实时状态
	 */
	public static boolean blueRealtimestate = false;

	//S1水杯是标准蓝牙,S1-S、C1是低功耗蓝牙，性能就没那么好
	//S1的发送文件的最大字节，最大支持1024，S1是标准蓝牙
	public static int S1_SEND_FILE_LENGTH = 1024;
	//S1-S的发送文件的最大字节，蓝牙协议说的是最大支持512，但512会出现重发现象
	public static int S1S_SEND_FILE_LENGTH = 256;
	//C1的发送文件的最大字节，最大支持192
	public static int C1_SEND_FILE_LENGTH = 192;

	//类型标识用于判断当前设备是否是这类水杯
	//S1的类型标识
	public static int S1_STYLE_FLAG = 0;
	//S1-S的类型标识
	public static int S1S_STYLE_FLAG = 2;
	//C1的类型标志
	public static int C1_STYLE_FLAG = 1;

	// 调试
	public static final String TAG = "Const";

	// notify 命令 用于广播的action
	/** 重复发送命令超过10次就断开蓝牙连接 */
	public static final String NOTIFY_RETRY_CONNECT_TEN= "NOTIFY:RETRY:CONNECT:TEN";
	/** 获取最新的饮水记录广播 */
	public static final String NOTIFY_GET_LATEST_DRINK_RECORD = "NOTIFY:GET:LATEST:DRINK:RECORD";
	/** 蓝牙发送图片 */
	public static final String NOTIFY_BLUETOOTH_SENDPIC = "notify:bluetooth:pic:send";
	/** 发送图片失败 */
	public static final String NOTIFY_BLUETOOTH_SENDPIC_FAIL = "notify:bluetooth:pic:sendfail";
	/** 蓝牙发送图片完成 */
	public static final String NOTIFY_BLUETOOTH_SENDPIC_FIN = "notify:bluetooth:pic:sended";
	/** 蓝牙正在发生图片 */
	public static final String NOTIFY_BLUETOOTH_SENDPICING = "notify:bluetooth:pic:sending";
	/** 蓝牙停止发送图片 */
	public static final String NOTIFY_BLUETOOTH_STOP_SENDPIC = "notify:bluetooth:pic:stopsend";

}
