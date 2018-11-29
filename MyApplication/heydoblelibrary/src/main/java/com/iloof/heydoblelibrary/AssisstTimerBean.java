package com.iloof.heydoblelibrary;

/**
 * 提醒设置定时器组bean
 */
public class AssisstTimerBean {

    //每组定时器对应编号
    int timeID = 0;
    /**
     * 开启状态
     * 0.关闭   1.开启
     */
    int openState = 0;
    /**
     * 定时器模式
     * 00一次定时A模式(周日~周六中的一天)
     * 01一次定时B模式
     * 10重复定时模式(每天重复)
     * 11自定义定时模式(每周重复)
     */
    int mode = 0;
    /**
     * 闹钟标识 0X4普通闹钟  0X5提醒吃药  0X6健身提醒饮水（成人水杯）
     * 儿童水杯则只有0x1提醒饮水
     */
    int notifyWays = 0;
    /**
     * 星期选择
     */
    int sunday = 0;
    int monday = 0;
    int tuesday = 0;
    int wednesday = 0;
    int thursday = 0;
    int friday = 0;
    int saturday = 0;

    /**
     * 时间
     */
    int hour = 0;
    int minutes = 0;

    public int getTimeID() {
        return timeID;
    }

    public void setTimeID(int timeID) {
        this.timeID = timeID;
    }

    public int getOpenState() {
        return openState;
    }

    public void setOpenState(int openState) {
        this.openState = openState;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getNotifyWays() {
        return notifyWays;
    }

    public void setNotifyWays(int notifyWays) {
        this.notifyWays = notifyWays;
    }

    public int getSunday() {
        return sunday;
    }

    public void setSunday(int sunday) {
        this.sunday = sunday;
    }

    public int getMonday() {
        return monday;
    }

    public void setMonday(int monday) {
        this.monday = monday;
    }

    public int getTuesday() {
        return tuesday;
    }

    public void setTuesday(int tuesday) {
        this.tuesday = tuesday;
    }

    public int getWednesday() {
        return wednesday;
    }

    public void setWednesday(int wednesday) {
        this.wednesday = wednesday;
    }

    public int getThursday() {
        return thursday;
    }

    public void setThursday(int thursday) {
        this.thursday = thursday;
    }

    public int getFriday() {
        return friday;
    }

    public void setFriday(int friday) {
        this.friday = friday;
    }

    public int getSaturday() {
        return saturday;
    }

    public void setSaturday(int saturday) {
        this.saturday = saturday;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    @Override
    public String toString() {
        return "AssisstTimerBean{" +
                "timeID=" + timeID +
                ", openState=" + openState +
                ", mode=" + mode +
                ", notifyWays=" + notifyWays +
                ", sunday=" + sunday +
                ", monday=" + monday +
                ", tuesday=" + tuesday +
                ", wednesday=" + wednesday +
                ", thursday=" + thursday +
                ", friday=" + friday +
                ", saturday=" + saturday +
                ", hour=" + hour +
                ", minutes=" + minutes +
                '}';
    }
}
