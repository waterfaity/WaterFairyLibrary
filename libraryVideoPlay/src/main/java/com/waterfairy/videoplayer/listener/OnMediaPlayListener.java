package com.waterfairy.videoplayer.listener;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 11:13
 * @info:
 */
public interface OnMediaPlayListener {

    void onMediaError(String errMsg);

    void onMediaPrepared();

    void onMediaPlayComplete();

    void onMediaPause();

    void onMediaPlay();

    void onMediaRelease();
}
