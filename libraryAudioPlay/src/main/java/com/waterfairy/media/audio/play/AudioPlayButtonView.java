package com.waterfairy.media.audio.play;

import android.content.Context;
import android.util.AttributeSet;

import com.waterfairy.play.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 10:49
 * @info:
 */
public class AudioPlayButtonView extends AbAudioPlayButtonView {

    public AudioPlayButtonView(Context context) {
        this(context, null);
    }

    public AudioPlayButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getResPlay() {
        return R.drawable.bottom_audio_play_copy;
    }

    @Override
    protected int getResPause() {
        return R.drawable.bottom_audio_pause_copy;
    }
}