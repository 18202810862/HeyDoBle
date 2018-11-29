package com.iloof.heydoblelibrary;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SettingActivity extends AppCompatActivity {

    private Button sendEmoji;

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
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        sendEmoji = findViewById(R.id.send_emoji);

        click();
    }

    private void click() {
        sendEmoji.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bleHelper != null){
                    bleHelper.requestBuzzerSound(4);
                    bleHelper.requestDisplay1P(40);
                }
            }
        });
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
                Log.i("settingActivity", "Const.blueRealtimestate---->");
                bleHelper = blueBind.getHelper();
            }

        });


    }
}
