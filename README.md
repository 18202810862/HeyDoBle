# HeyDoBle
HeyDo蓝牙模块


1.在你的gradle中添加以下:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
  
  
  
  dependencies {
	        compile 'com.github.18202810862:HeyDoBle:1.2'
	}
  
2.在androidManifest中添加:

  <service android:name="com.iloof.heydoblelibrary.BleHelper" />
