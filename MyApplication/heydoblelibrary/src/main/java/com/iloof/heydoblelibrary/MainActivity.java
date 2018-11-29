package com.iloof.heydoblelibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iloof.heydoblelibrary.app.Const;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainActivity";

    private Button connect;
    private Button getRecord;
    private Button disconnect;
    private Button jump;
    private Button getCupState;
    private TextView connectState;

    /**
     * 蓝牙服务绑定工具类
     */
    private ToolsBluetoothBind blueBind;
    /**
     * 蓝牙帮助类
     */
    BleHelper bleHelper;

    /**
     * 蓝牙相关信息回调
     */
    private BleHandler handler = new BleHandler() {

        @Override
        protected void answerReady(boolean isReady) {
            super.answerReady(isReady);
            if (isReady) {
                Log.i(TAG, "读写通道建立成功----->");
                Toast.makeText(MainActivity.this, "读写通道建立成功", Toast.LENGTH_SHORT).show();
                connectState.setText("蓝牙连接成功");
                /**连接蓝牙时，在读写通道建立成功后获取blehelper实例,后可进行蓝牙收发命令相关操作*/
                bleHelper = blueBind.getHelper();


            } else {
                Log.i(TAG, "读写通道建立失败----->");
                Toast.makeText(MainActivity.this, "读写通道建立失败", Toast.LENGTH_SHORT).show();
                connectState.setText("蓝牙连接失败");
            }


        }

        @Override
        protected void answerConnectionState(boolean isLink) {
            if (!isLink) {
                /**超时、未搜索到该设备、连接不上、连接上意外断开都走这回调*/
                Log.i(TAG, "蓝牙连接状态改变----->断开连接或连接失败");
                Toast.makeText(MainActivity.this, "蓝牙连接状态改变----->断开连接或连接失败", Toast.LENGTH_SHORT).show();
                connectState.setText("蓝牙连接失败或断开");

            } else {
                Log.i(TAG, "蓝牙连接状态改变----->连接成功");
                Toast.makeText(MainActivity.this, "连接成功---->准备匹配读写服务uuid，建立读写通道", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        protected void answerSysBluToothNotEnable() {
            super.answerSysBluToothNotEnable();
            Log.i(TAG, "系统蓝牙未打开----->");
            Toast.makeText(MainActivity.this, "系统蓝牙未打开----->", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void answerMsgCurStatus1(BleUtil.BleCurrentStaus1 message) {
            super.answerMsgCurStatus1(message);
            Log.i(TAG, "今日因数量为------->" + message.TodayDrinkAmountOfWater);
            Log.i(TAG, "温度为------->" + message.temperature);
            Log.i(TAG, "水质为------->" + message.curWaterQuantity);
        }

        @Override
        protected void answerGetLatestDrinkRecord(BleUtil.DrinkRecord record) {
            super.answerGetLatestDrinkRecord(record);
            Log.i(TAG, "饮水记录时间------->" + record.recordTime);
            Log.i(TAG, "饮水记录饮水量------->" + record.waterDrink);
            Log.i(TAG, "饮水记录PPM------->" + record.ppm);
            Log.i(TAG, "饮水记录温度------->" + record.temperature);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        connect = findViewById(R.id.connect_ble);
        getRecord = findViewById(R.id.get_drink_records);
        disconnect = findViewById(R.id.disconnect_ble);
        connectState = findViewById(R.id.connect_state);
        jump = findViewById(R.id.jump_setting);
        getCupState = findViewById(R.id.get_cup_state);

        click();

    }

    private void click() {

        connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!Const.blueRealtimestate) {
                    connectBlue();
                }
            }
        });

        getCupState.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取水杯状态，返回的数据会回调answerMsgCurStatus1()里
                if (bleHelper!=null){
                    bleHelper.requestCurrentStatusOne();
                }
            }
        });

        getRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取最近饮水量,返回的数据回调在answerGetLatestDrinkRecord()
                if (bleHelper!=null){
                    bleHelper.requestLatestDrinkRecord();
                }
            }
        });

        disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bleHelper.disconnect();
                Log.i(TAG, "断开连接----->");
                Toast.makeText(MainActivity.this, "断开连接----->", Toast.LENGTH_SHORT).show();
            }
        });

        jump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {

            if (resultCode == Activity.RESULT_OK) {
                connectBlue();
            }
        }
    }

    /**
     * 是否打开系统蓝牙
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public boolean openSysBle() {
        BluetoothManager mBluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
//        Log.i(TAG, "openSysBle");
        if (mBluetoothManager != null) {
            BluetoothAdapter adapter = mBluetoothManager.getAdapter();
            if (adapter != null) {
                if (adapter.isEnabled()) {// 系统蓝牙已打开
                    return true;
                } else {
//                    Log.i(TAG, "请求打开权限");
                    //用于控制请求蓝牙权限对话框的弹出，如果不控制，在弹出对话框后程序退到后台在进来又会请求蓝牙权限
                    Intent enableBtIntent = new Intent(
                            BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, 2);
                }
            }
        }

        return false;
    }

    /**
     * 连接蓝牙
     */
    private void connectBlue() {

        if (!openSysBle()) {//检测系统蓝牙是否打开，未打开则打开
            return;
        }
        unbindBleService();
        blueBind.setNeedConnect(true);
        blueBind.connectBluetooth(this, handler, "20:91:48:55:C9:D0");
    }

    /**
     * 解绑蓝牙服务，APP和水杯的蓝牙通信为断开
     */
    public void unbindBleService() {
        if (blueBind.isCalledBind) {
            bleHelper = null;
            blueBind.isCalledBind = false;
            blueBind.unbind(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /**离开当前界面时解绑蓝牙服务*/
        unbindBleService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        /**如果蓝牙连接上了在其他界面onstart或onCreat时直接绑定蓝牙服务，就不用在连接蓝牙了*/
        if (blueBind == null) {
            blueBind = new ToolsBluetoothBind();
        }
        blueBind.onlyBindBluetooth(this, handler, new ToolsBluetoothBind.BleServiceListener() {

            @Override
            public void onConnected() {
                bleHelper = blueBind.getHelper();
            }

        });


    }
}
