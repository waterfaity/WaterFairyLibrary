package com.waterfairy.videoplayer.tool;

import android.content.Context;
import android.media.AudioManager;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/21
 * @info:
 */
public class AudioTool {

    public static boolean requestPlay(Context context, AudioManager.OnAudioFocusChangeListener onAudioFocusChangeListener) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        if (audioManager != null) {
            int result = audioManager.requestAudioFocus(onAudioFocusChangeListener, AudioManager.STREAM_MUSIC,
                    AudioManager.AUDIOFOCUS_GAIN);
            return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED;
        } else return false;
    }
}
