package com.waterfairy.downloader.base;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019/4/4 16:44
 * @info: 内部使用
 */
public class ProgressBean<T extends BaseBeanInfo> {
    private int state;
    T beanInfo;

    public static final int STATE_START = 1;
    public static final int STATE_RESULT = 2;
    public static final int STATE_PAUSED = 3;//暂停
    public static final int STATE_PROGRESS = 4;
    private int resultCode;

    private String resultData;

    private long total;
    private long current;

    public ProgressBean(int state, T beanInfo) {
        this.state = state;
        this.beanInfo = beanInfo;

    }

    public ProgressBean(T beanInfo) {
        this.beanInfo = beanInfo;

    }

    public int getResultCode() {
        return resultCode;
    }

    public ProgressBean<T> setResultCode(int resultCode) {
        this.resultCode = resultCode;
        return this;
    }

    public int getState() {
        return state;
    }

    public ProgressBean<T> setState(int state) {
        this.state = state;
        return this;
    }

    public T getBeanInfo() {
        return beanInfo;
    }

    public ProgressBean<T> setBeanInfo(T beanInfo) {
        this.beanInfo = beanInfo;
        return this;
    }

    public String getResultData() {
        return resultData;
    }

    public ProgressBean<T> setResultData(String resultData) {
        this.resultData = resultData;
        return this;
    }

    public long getTotal() {
        return total;
    }

    public ProgressBean<T> setTotal(long total) {
        this.total = total;
        return this;
    }

    public long getCurrent() {
        return current;
    }

    public ProgressBean<T> setCurrent(long current) {
        this.current = current;
        return this;
    }
}
