package com.iloof.heydoblelibrary;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

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

;

/**
 * A Handler allows you to extend this class and implement the abstract method
 * which answer message.
 *
 * @author UESTC-PRMI-Burial
 * @date 2014-12-11
 */

/**
 * @author lbc1234
 *
 */
public abstract class BleHandlerAbstruct extends Handler {

    private String TAG = "BleHandlerAbstruct";

    /*
     * 判断要处理的消息类型，调用相应的函数
     */
    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Log.d(TAG, "handleMessage---msg.what=" + msg.what);
        switch (msg.what) {
            case BleConstant.HM_BLE_NONSUPPORT:
                answerNonsupport();
                break;
            case BleConstant.HM_BLE_CONNECTED:
                answerConnectionState(true);
                break;
            case BleConstant.HM_BLE_DISCONNECTED:
                answerConnectionState(false);
                break;
            case BleConstant.HM_BLE_READY:
                answerReady(true);
                break;
            case BleConstant.HM_BLE_READY_FAIL:
                answerReady(false);
                break;
            case BleConstant.HM_CMD_WRITED:
                answerWrite(true);
                break;
            case BleConstant.HM_CMD_FAILURE:
                answerWrite(false);
                break;
            case BleConstant.ID_CURRENT_STATUS_ONE:
                answerMsgCurStatus1((BleCurrentStaus1) msg.obj);
                break;
            case BleConstant.HM_MSG_DRINKRECORD:
                answerMsgDrinkingRecord((BleDrinkingRecord) msg.obj);
                break;
            case BleConstant.HM_MSG_FACTORYMODE:
                answerMsgFactoryMode((BleFactoryMode) msg.obj);
                break;
            case BleConstant.HM_MSG_WPICTURE32:
                answerMsgWriteImg32(msg.arg1);
                break;
            case BleConstant.HM_MSG_WPICTURE64:
                answerMsgWriteImg64(msg.arg1);
                break;
            case BleConstant.HM_MSG_WPICTURE1024:
                answerMsgWriteImg1024(msg.arg1);
                break;
            case BleConstant.HM_MSG_PICTURECRC:
                answerMsgImgCrc((BleImgCrc) msg.obj);
                break;
            case BleConstant.HM_MSG_PASSWORD:
                if (0 == msg.arg1) {
                    answerMsgPassword(false);
                } else {
                    answerMsgPassword(true);
                }
                break;
            case BleConstant.HM_MSG_CHANGEMODE:
                if (0 == msg.arg1) {
                    answerMsgChangeMode(false);
                } else {
                    answerMsgChangeMode(true);
                }
                break;
            case BleConstant.HM_MSG_SETAIM:
                if (0 == msg.arg1) {
                    answerMsgSetAim(false);
                } else {
                    answerMsgSetAim(true);
                }
                break;
            case BleConstant.HM_MSG_RENAME:
                    answerMsgRename((BleMessage) msg.obj);
                break;
            case BleConstant.HM_MSG_HIGHLEVEL:
                if (0 == msg.arg1) {
                    answerMsgHighLevel(false);
                } else {
                    answerMsgHighLevel(true);
                }
                break;
            case BleConstant.HM_MSG_WORDS:
                if (0 == msg.arg1) {
                    answerMsgWords(false);
                } else {
                    answerMsgWords(true);
                }
                break;

            case BleConstant.HM_MSG_DAY_DRINK_GOAL:
                answerDayDrinkGoal((BleMessage) msg.obj);
                break;

            case BleConstant.ID_RESET:
                answerReset((BleMessage) msg.obj);
                break;

            case BleConstant.ID_SET_ALARM:
                answerSetAlarm((BleMessage) msg.obj);
                break;
            case BleConstant.ID_SET_NO_DISTURBING:
                answerSetNoDisturbing((BleMessage) msg.obj);
                break;

            case BleConstant.ID_LIGTH_SCREEN:
                answerLightScreen((BleMessage) msg.obj);
                break;

//		case BleConstant.ID_FACTORY_PATTERN:
//			answerFactoryPattern((BleMessage) msg.obj);
//			break;

//		case BleConstant.ID_EXTINGUISH_FACTORY:
//			answerExitinguishFactory((BleMessage) msg.obj);
//			break;

            case BleConstant.ID_ADJUST_WEIGHT:
                answerAdjustWeight((BleMessage) msg.obj);
                break;

            case BleConstant.ID_SERIOUS_NUM:
                answerSeriousNum((BleMessage) msg.obj);
                break;

            case BleConstant.ID_CUP_HARDWARE_VERSION:
                answerHardVersion((HardwareVersion) msg.obj);
                break;

            case BleConstant.HM_DEBUG_TOAST:
                answerToastForDebug((String) msg.obj);
                break;
            case BleConstant.HM_DEBUG_CMD:
                answerCommandForDebug((String) msg.obj);
                break;
            case BleConstant.HM_DEBUG_MSG:
                answerMessageForDebug((String) msg.obj);
                break;

            case BleConstant.ID_DRINKING_RECORDS:
                answerGetDrinksRecord((DrinksRecord) msg.obj);
                break;
            case BleConstant.ID_DRINKING_RECORD:
                answerGetLatestDrinkRecord((DrinkRecord) msg.obj);
                break;
            case BleConstant.ID_SEND_FILE:
                answerSendFile((SendFile) msg.obj);
                break;
            case BleConstant.HM_MSG_SENDFAIL:
                answerSendFail((BleCommand) msg.obj);
                break;
            case BleConstant.ID_CHEERS:
                answerCheers((Cheers) msg.obj);
                // answerCheers((Cheers).msg.obj);
                break;
            case BleConstant.ID_UPDATEHEYDO:
                answerUpdateHeydo((UpdateHeydo) msg.obj);
                break;
            case BleConstant.ID_CUP_SOFT_VERSION:
                answerCupSoftVersion((CupSoftVersion) msg.obj);
                break;

            case BleConstant.ID_SYS_BLUETOOTH_NOT_ENABLE:
                answerSysBluToothNotEnable();
                break;
            case BleConstant.ID_GET_FACTORY_STATUS:
                answerBleFactoryModeStatus((BleFactoryModeStatus) msg.obj);
                break;
            case BleConstant.ID_GET_ALL_TIMER:
                answerBleAllTimers((BleAllTimers) msg.obj);
                break;
            case BleConstant.ID_GET_ALL_TIMER_TIME:
                answerBleAllTimersTime((BleAllTimersTime) msg.obj);
                break;
            case BleConstant.ID_GET_TIMER:
                answerBleTimerWithTime((BleTimerWithTime) msg.obj);
                break;
            case BleConstant.ID_SET_HIGH_TEMPERATURE:
                answerHighTemperature((BleMessage) msg.obj);
                break;
            case BleConstant.ID_TEMPARATURE_STYLE:
                answerTemperatureStyle((BleMessage) msg.obj);
                break;
            case BleConstant.ID_WEIGHT_STYLE:
                answerWeightStyle((BleMessage) msg.obj);
                break;
            case BleConstant.ID_DELETE_RECORDS:
                answerDeleteRecords((BleMessage) msg.obj);
                break;
            case BleConstant.ID_CUSTOM_NO_DISTURB:
                answerCustomNoDisturb((BleMessage) msg.obj);
                break;
            case BleConstant.ID_CUSTOM_UI_SHOW:
                answerCustomUiShow((BleMessage) msg.obj);
                break;
            case BleConstant.ID_WATER_TEMPRATURE_LOW:
                answerWaterTempLow((BleMessage) msg.obj);
                break;
            case BleConstant.ID_CUP_CONSTANT_TEMPRATURE:
                answerCupCntantTemp((BleMessage) msg.obj);
                break;
            case BleConstant.ID_GET_DAY_DRINK_GOAL:
                answerGetDrinkGoal((BleMessage) msg.obj);
                break;
            case BleConstant.ID_TIMING:
                answerTiming((BleMessage) msg.obj);
                break;
            case BleConstant.ID_SOUND_AJUST:
                answerSoundAjust((BleMessage) msg.obj);
                break;
            case BleConstant.ID_LIGHT_AJUST:
                answerLightAjust((BleMessage) msg.obj);
                break;
            case BleConstant.ID_MEMORY_VERSION:
                answerMemoryVersion((BleMessage) msg.obj);
                break;
            case BleConstant.ID_VOICE_MODE:
                answerVoiceMode((BleMessage) msg.obj);
                break;
            case BleConstant.ID_HEIGH_SAFE_CONTROL:
                answerHeighSafeControl((BleMessage) msg.obj);
                break;
            case BleConstant.ID_GET_REMIND_TEMP:
                answerGetRemindTemp((BleMessage) msg.obj);
                break;
            case BleConstant.ID_ADD_WATER_REMIND:
                answerAddWaterRemind((BleMessage) msg.obj);
                break;
            default:
                break;
        }
    }

    /**
     * 添加饮水记录
     * @param obj
     */
    protected abstract void answerAddWaterRemind(BleMessage obj);

    /**
     * 获取温度提醒(S1-S)
     * @param obj
     */
    protected abstract void answerGetRemindTemp(BleMessage obj);

    /**
     * 高安全和S1-S的单位
     * @param obj
     */
    protected abstract void answerHeighSafeControl(BleMessage obj);
    /**
     * 语音模式
     * @param obj
     */
    protected abstract void answerVoiceMode(BleMessage obj);

    /**
     * 获取存储器上软硬件版本
     * @param obj
     */
    protected abstract void answerMemoryVersion(BleMessage obj);

    /**
     * 修改蓝牙名称
     * @param obj
     */
    protected abstract void answerMsgRename(BleMessage obj);

    /**
     * LED亮度调节
     * @param obj
     */
    protected abstract void answerLightAjust(BleMessage obj);

    /**
     * 音量调节
     * @param obj
     */
    protected abstract void answerSoundAjust(BleMessage obj);

    /**
     * 系统时间校准
     * @param obj
     */
    protected abstract void answerTiming(BleMessage obj);

    /**
     * 获取每日饮水目标
     * @param obj
     */
    protected abstract void answerGetDrinkGoal(BleMessage obj);

    /**
     * 水杯恒温
     * @param obj
     */
    protected abstract void answerCupCntantTemp(BleMessage obj);

    /**
     * 水温降低提醒
     * @param obj
     */
    protected abstract void answerWaterTempLow(BleMessage obj);

    /**
     * 自定义界面显示
     * @param obj
     */
    protected abstract void answerCustomUiShow(BleMessage obj);

    /**
     * 自定义免打扰
     * @param obj
     */
    protected abstract void answerCustomNoDisturb(BleMessage obj);

    /**
     * <b>清空记录</>
     * @param obj
     */
    protected abstract void answerDeleteRecords(BleMessage obj);

    /**
     * <b>水量计量格式</b>
     */
    protected abstract void answerWeightStyle(BleMessage obj);

    /**
     * <b>温度格式</b>
     */
    protected abstract void answerTemperatureStyle(BleMessage obj);

    /**
     * <b>响应返回指定编号定时器</b>
     */
    protected abstract void answerBleTimerWithTime(BleTimerWithTime obj);

    /**
     * <b>响应返回所有定时器</b>
     */
    protected abstract void answerBleAllTimersTime(BleAllTimersTime times);

    /**
     * <b>响应返回所有定时器</b>
     */
    protected abstract void answerBleAllTimers(BleAllTimers times);

    /**响应工厂模式状态
     * @param obj */
    protected abstract void answerBleFactoryModeStatus(BleFactoryModeStatus obj);

    /**
     * 系统蓝牙未打开
     */
    protected abstract void answerSysBluToothNotEnable();

    /**
     * 水杯软件版本
     *
     * @param obj
     */
    protected abstract void answerCupSoftVersion(CupSoftVersion obj);

    /**
     * 干杯消息
     */
    protected abstract void answerUpdateHeydo(UpdateHeydo updateHeydo);

    /**
     * 干杯消息
     */
    protected abstract void answerCheers(Cheers cheers);

    /**
     * 发送命令失败
     *
     * @param obj
     */
    protected abstract void answerSendFail(BleCommand obj);

    /**
     * 发送文件
     *
     * @param obj
     */
    protected abstract void answerSendFile(SendFile obj);

    /**
     * 相应获取硬件版本号
     */
    protected abstract void answerHardVersion(HardwareVersion obj);

    /**
     * 相应获取序列号
     */
    protected abstract void answerSeriousNum(BleMessage obj);

    /**
     * 相应重置水杯命令
     */
    protected abstract void answerAdjustWeight(BleMessage obj);

    /**
     * 响应高温预警设置
     * @param obj
     */
    protected abstract void answerHighTemperature(BleMessage obj);

    /**
     * 相应重置水杯命令
     */
    protected abstract void answerReset(BleMessage obj);

    /**
     * 打开关闭工厂模式命令
     */
    protected abstract void answerFactoryPattern(BleMessage obj);

    /**
     * 相应关闭工厂模式命令
     */
    protected abstract void answerExitinguishFactory(BleMessage obj);

    /**
     * 响应设置亮屏模式命令
     */
    protected abstract void answerLightScreen(BleMessage obj);

    /**
     * 获取设置免打扰模式的状态
     */
    protected abstract void answerSetNoDisturbing(BleMessage obj);

    /** 获取最新饮水记录 **/
    protected abstract void answerGetLatestDrinkRecord(DrinkRecord record);

    /** 获取多条饮水记录 */
    protected abstract void answerGetDrinksRecord(DrinksRecord records);

    /**
     * 设置闹钟的返回结果
     */
    protected abstract void answerSetAlarm(BleMessage obj);

    /**
     * 每日饮水的请求的返回结果
     */
    protected abstract void answerDayDrinkGoal(BleMessage message);

    /**
     * What the abstract method aims to answer the wrong about writing picture
     * by 64b is waiting for you to implement. The function will be call, Only
     * when something is wrong during the update.
     */
    protected abstract void answerMsgWriteImg1024(int indexOfImg);

    /**
     * What the abstract method aims to answer the message about words is
     * waiting for you to implement.
     *
     * @param isSuccess
     *            Ture if success, or false.
     */
    protected abstract void answerMsgWords(boolean isSuccess);

    /**
     * What the abstract method aims to answer the message about high level is
     * waiting for you to implement.
     *
     * @param isSuccess
     *            Ture if success, or false.
     */
    protected abstract void answerMsgHighLevel(boolean isSuccess);


    /**
     * What the abstract method aims to answer the message about the drinking
     * aim is waiting for you to implement.
     *
     * @param isSuccess
     *            Ture if success, or false.
     */
    protected abstract void answerMsgSetAim(boolean isSuccess);

    /**
     * What the abstract method aims to answer the message about change mode is
     * waiting for you to implement.
     *
     * @param isSuccess
     *            Ture if success, or false.
     */
    protected abstract void answerMsgChangeMode(boolean isSuccess);

    /**
     * What the abstract method aims to answer the message about the image's CRC
     * is waiting for you to implement.
     */
    protected abstract void answerMsgImgCrc(BleImgCrc imgCrc);

    /**
     * What the abstract method aims to answer the wrong about writing picture
     * by 64b is waiting for you to implement. The function will be call, Only
     * when something is wrong during the update.
     */
    protected abstract void answerMsgWriteImg64(int indexOfImg);

    /**
     * What the abstract method aims to answer the wrong about writing picture
     * by 32b is waiting for you to implement. The function will be call, Only
     * when something is wrong during the update.
     */
    protected abstract void answerMsgWriteImg32(int indexOfImg);

    /**
     * What the abstract method aims to answer the message about the password is
     * waiting for you to implement.
     *
     * @param isSuccess
     *            Ture if we have reset the password, or false.
     */
    protected abstract void answerMsgPassword(boolean isSuccess);

    /**
     * What the abstract method aims to answer the message about the factory
     * mode is waiting for you to implement.
     *
     * @param message
     *            The BleFactoryMode class
     * @see BleFactoryMode
     */
    protected abstract void answerMsgFactoryMode(BleFactoryMode message);


    /**
     * What the abstract method aims to answer the message about drinking record
     * is waiting for you to implement.
     *
     * @param message
     *            The BleDrinkingRecord class
     * @see BleDrinkingRecord
     */
    protected abstract void answerMsgDrinkingRecord(BleDrinkingRecord message);


    /**
     * What the abstract method aims to answer the message about current
     * state(1/4) is waiting for you to implement.
     *
     * @param message
     *            The BleCurrentStaus1 class
     * @see BleCurrentStaus1
     */
    protected abstract void answerMsgCurStatus1(BleCurrentStaus1 message);

    /**
     * What the abstract method aims to answer the flag about writing command to
     * the remote device is waiting for you to implement.
     *
     * @param isWrited
     *            Ture if we are writed successful, Or false.
     *
     */
    protected abstract void answerWrite(boolean isWrited);

    /**
     * What the abstract method aims to answer the flag about readily to write
     * and read is waiting for you to implement.
     *
     * @param isReady
     *            Ture if the device is ready to communicate, Or false.
     *
     */
    protected abstract void answerReady(boolean isReady);

    /**
     * What the abstract method aims to answer the connection with BLE is
     * waiting for you to implement.
     *
     * @param isLink
     *            True if the connection is connected, Or false.
     *
     */
    protected abstract void answerConnectionState(boolean isLink);

    /**
     * What the abstract method aims to answer the nonsupport of BLE is waiting
     * for you to implement.
     *
     */
    protected abstract void answerNonsupport();

    protected abstract void answerMessageForDebug(String obj);

    protected abstract void answerCommandForDebug(String obj);

    protected abstract void answerToastForDebug(String msg);

}
