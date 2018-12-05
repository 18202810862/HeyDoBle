package com.iloof.heydoblelibrary;

import com.iloof.heydoblelibrary.BleUtil.BleAllTimers;
import com.iloof.heydoblelibrary.BleUtil.BleAllTimersTime;
import com.iloof.heydoblelibrary.BleUtil.BleCurrentStaus1;
import com.iloof.heydoblelibrary.BleUtil.BleDrinkingRecord;
import com.iloof.heydoblelibrary.BleUtil.BleFactoryMode;
import com.iloof.heydoblelibrary.BleUtil.BleFactoryModeStatus;
import com.iloof.heydoblelibrary.BleUtil.BleImgCrc;
import com.iloof.heydoblelibrary.BleUtil.BleTimerWithTime;
import com.iloof.heydoblelibrary.BleUtil.Cheers;
import com.iloof.heydoblelibrary.BleUtil.CupSoftVersion;
import com.iloof.heydoblelibrary.BleUtil.DrinkRecord;
import com.iloof.heydoblelibrary.BleUtil.DrinksRecord;
import com.iloof.heydoblelibrary.BleUtil.HardwareVersion;
import com.iloof.heydoblelibrary.BleUtil.SendFile;
import com.iloof.heydoblelibrary.BleUtil.UpdateHeydo;
import com.iloof.heydoblelibrary.util.BleCommand;
import com.iloof.heydoblelibrary.util.BleMessage;

/**
 * @author 冒国全 创建时间：2015年11月2日 下午5:43:39 BleHandlerAbstruct的实现类，所有的抽象方法都定义为空
 * 
 * 
 */
public class BleHandler extends BleHandlerAbstruct {

	private String TAG = "BleHandler";

	private String activity;

	public BleHandler(){

	}

	public BleHandler(String activity){
		this.activity = activity;
	}

	public String getActivity(){
		return activity;
	}

	@Override
	protected void answerMsgWriteImg1024(int indexOfImg) {
	}

	@Override
	protected void answerMsgWords(boolean isSuccess) {
	}

	@Override
	protected void answerMsgHighLevel(boolean isSuccess) {
	}


	@Override
	protected void answerMsgSetAim(boolean isSuccess) {
	}

	@Override
	protected void answerMsgChangeMode(boolean isSuccess) {
	}

	@Override
	protected void answerMsgImgCrc(BleImgCrc imgCrc) {
	}

	@Override
	protected void answerMsgWriteImg64(int indexOfImg) {
	}

	@Override
	protected void answerMsgWriteImg32(int indexOfImg) {
	}

	@Override
	protected void answerMsgPassword(boolean isSuccess) {
	}

	@Override
	protected void answerMsgFactoryMode(BleFactoryMode message) {
	}

	@Override
	protected void answerMsgDrinkingRecord(BleDrinkingRecord message) {
	}

	@Override
	protected void answerMsgCurStatus1(BleCurrentStaus1 message) {
	}

	@Override
	protected void answerWrite(boolean isWrited) {
	}

	@Override
	protected void answerReady(boolean isReady) {
	}

	@Override
	protected void answerConnectionState(boolean isLink) {
	}

	@Override
	protected void answerNonsupport() {
	}

	@Override
	protected void answerMessageForDebug(String obj) {
	}

	@Override
	protected void answerCommandForDebug(String obj) {
	}

	@Override
	protected void answerToastForDebug(String msg) {
	}

	@Override
	protected void answerDayDrinkGoal(BleMessage message) {
	}

	@Override
	protected void answerSetAlarm(BleMessage obj) {
	}

	@Override
	protected void answerSetNoDisturbing(BleMessage obj) {
	}

	@Override
	protected void answerLightScreen(BleMessage obj) {
	}

	@Override
	protected void answerExitinguishFactory(BleMessage obj) {
	}

	@Override
	protected void answerReset(BleMessage obj) {
	}

	@Override
	protected void answerAdjustWeight(BleMessage obj) {
	}

	@Override
	protected void answerSeriousNum(BleMessage obj) {
	}

	@Override
	protected void answerHardVersion(HardwareVersion obj) {
	}

	@Override
	protected void answerGetLatestDrinkRecord(DrinkRecord record) {

	}

	@Override
	protected void answerGetDrinksRecord(DrinksRecord records) {

	}

	@Override
	protected void answerSendFile(SendFile obj) {

	}

	@Override
	protected void answerSendFail(BleCommand obj) {
	}

	@Override
	protected void answerCheers(Cheers cheers) {

	}

	@Override
	protected void answerUpdateHeydo(UpdateHeydo updateHeydo) {
		// TODO 自动生成的方法存根

	}

	@Override
	protected void answerCupSoftVersion(CupSoftVersion obj) {
		// TODO 自动生成的方法存根

	}

	@Override
	protected void answerSysBluToothNotEnable() {

	}


	@Override
	protected void answerBleFactoryModeStatus(BleFactoryModeStatus obj) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected void answerBleAllTimers(BleAllTimers times) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected void answerBleAllTimersTime(BleAllTimersTime times) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected void answerAddWaterRemind(BleMessage obj) {

	}

	@Override
	protected void answerGetRemindTemp(BleMessage obj) {

	}

	@Override
	protected void answerHeighSafeControl(BleMessage obj) {

	}

	@Override
	protected void answerVoiceMode(BleMessage obj) {

	}

	@Override
	protected void answerMemoryVersion(BleMessage obj) {

	}

	@Override
	protected void answerMsgRename(BleMessage obj) {

	}

	@Override
	protected void answerLightAjust(BleMessage obj) {

	}

	@Override
	protected void answerSoundAjust(BleMessage obj) {

	}

	@Override
	protected void answerTiming(BleMessage obj) {

	}

	@Override
	protected void answerGetDrinkGoal(BleMessage obj) {

	}

	@Override
	protected void answerCupCntantTemp(BleMessage obj) {

	}

	@Override
	protected void answerWaterTempLow(BleMessage obj) {

	}

	@Override
	protected void answerCustomUiShow(BleMessage obj) {

	}

	@Override
	protected void answerCustomNoDisturb(BleMessage obj) {

	}

	@Override
	protected void answerDeleteRecords(BleMessage obj) {

	}

	@Override
	protected void answerWeightStyle(BleMessage obj) {

	}

	@Override
	protected void answerTemperatureStyle(BleMessage obj) {

	}

	@Override
	protected void answerBleTimerWithTime(BleTimerWithTime obj) {
		// TODO 自动生成的方法存根
		
	}

	@Override
	protected void answerFactoryPattern(BleMessage obj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void answerHighTemperature(BleMessage obj) {
		// TODO Auto-generated method stub
		
	}

//	@Override
//	protected void answerFactoryPattern(BleMessage obj) {
//		// TODO Auto-generated method stub
//		
//	}

}
