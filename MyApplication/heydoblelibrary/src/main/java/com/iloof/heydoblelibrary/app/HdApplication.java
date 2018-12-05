package com.iloof.heydoblelibrary.app;

import android.app.Application;

import com.clj.fastble.BleManager;

/**
 * 自定义 Application <p> 1.数据初始化 2.全局数据存取
 */
public class HdApplication extends Application {

    private static final String TAG = "HdApplication";
    private static HdApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();

        if (instance == null) {
            instance = this;
        }

        BleManager.getInstance().init(this);
    }


    public static HdApplication getInstance() {
        return instance;
    }


}
