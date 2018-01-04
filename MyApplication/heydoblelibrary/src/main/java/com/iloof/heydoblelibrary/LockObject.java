package com.iloof.heydoblelibrary;

/**用于蓝牙发送命令线程与接收命令线程之间共享数据
 * @author lbc1234
 */
public class LockObject {
	/**标签*/
	public int tag;
	/**包序号*/
	public int packageSn;
	public int getTag() {
		return tag;
	}
	public void setTag(int tag) {
		this.tag = tag;
	}
	public int getPackageSn() {
		return packageSn;
	}
	public void setPackageSn(int packageSn) {
		this.packageSn = packageSn;
	}
	public synchronized void write(){
		
	}
	public synchronized void read(){
		
	}
	
}
