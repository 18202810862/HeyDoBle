# HeyDoBle
HeyDo蓝牙模块

项目中使用了FastBle
支持版本为2.3.2

1.在你的gradle中添加以下:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  
  
  	dependencies {
	        compile 'com.github.18202810862:HeyDoBle:1.6'
	}
  
2.在androidManifest中添加:

 	<service android:name="com.iloof.heydoblelibrary.BleHelper" />
	
	
3.在application或要使用蓝牙连接的activity调用一次（只需要调用一次）

BleManager.getInstance().init(getApplication());

 
 
 
 
 
 
 
 2018/12/5:
 增加对添加饮水记录命令的支持
  
  
  
