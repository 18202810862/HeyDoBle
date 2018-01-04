package com.iloof.heydoblelibrary;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;

import com.iloof.heydoblelibrary.BleHelper.LocalBinder;
import com.iloof.heydoblelibrary.app.Const;

/**
 * @author 冒国全 创建时间：2015年11月11日 上午10:24:13
 *         <P>
 *         绑定蓝牙，获取蓝牙服务，解绑蓝牙
 *         </p>
 */
public class ToolsBluetoothBind {
	protected static final String TAG = "ToolsBluetoothBind";//ToolsBluetoothBind
	/**
	 * 蓝牙服务对象
	 */
	BleHelper bleHelper;
	BleHandler bleHandler;
	private boolean needConnect = true;
	private BleServiceListener bleServiceListener;
	private String bleMac;
	/** 是否调用Bind函数*/
	public boolean isCalledBind = false;

	public interface BleServiceListener {
		public void onConnected();
	}

	ServiceConnection conn = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			Log.d(TAG, "onServiceDisconnected is called!");
			bleHelper = null;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder arg1) {
			Log.i(TAG, "OnServiceConnected is called!"  + " bind:" + ToolsBluetoothBind.this);

			// 获取service实例
			bleHelper = ((LocalBinder) arg1).getService(bleHandler);
			if (bleServiceListener != null && bleHelper.isConnected()){
				bleServiceListener.onConnected();
			}

			Log.i(TAG, "helper state 1:" + bleHelper.getCurrentConnectionState() + needConnect);

			//初始化蓝牙模块
			bleHelper.initialize();

			// 连接设备
			if (needConnect){
				bleHelper.startScanBLE(bleMac);

			}
			needConnect = false;
		}
	};

	/**
	 * 当蓝牙连接成功后,提供外部调用
	 * 
	 * @param act
	 * @param bleHandler
	 */
	public void onlyBindBluetooth(final Activity act,
			final BleHandler bleHandler, BleServiceListener bleServiceListener) {
		if (Const.blueRealtimestate){
			isCalledBind = true;
			needConnect = false;
			this.bleServiceListener = bleServiceListener;
			this.bleHandler = bleHandler;
			Intent service = new Intent(act, BleHelper.class);
			act.bindService(service, conn, Context.BIND_AUTO_CREATE);
		}

	}

	/**
	 * 连接蓝牙的调用函数
	 * 
	 * @param act 当前activity
	 * @param bleHandler  handler对象
	 * @param bleMac  水杯蓝牙MAC
	 */
	public void connectBluetooth(final Activity act, final BleHandler bleHandler,String bleMac) {
		Log.i(TAG, "connectBluetooth");
		isCalledBind = true;
		this.bleMac = bleMac;
		this.bleHandler = bleHandler;
		Intent service = new Intent(act, BleHelper.class);
		act.bindService(service, conn, Context.BIND_AUTO_CREATE);
	}

	/**解绑蓝牙服务
	 * @param act
	 */
	public void unbind(Activity act) {
		try {
			act.unbindService(conn);
		} catch (Exception e) {
			//..
		}
	}

	/**
	 * @return 是否需要连接
	 */
	public boolean isNeedConnect() {
		return needConnect;
	}

	/**设置是否需要连接蓝牙的属性
	 * @param state 属性状态
	 */
	public void setNeedConnect(boolean state){
		needConnect = state;
	}

	/**
	 * 外部湖区蓝牙服务接口
	 * @return 当前的蓝牙服务
	 */
	public BleHelper getHelper() {
		Log.i(TAG, "getHelper is called! bleHelper == null:" + (bleHelper == null) + " bind:" + this);
		return bleHelper;
	}
}
