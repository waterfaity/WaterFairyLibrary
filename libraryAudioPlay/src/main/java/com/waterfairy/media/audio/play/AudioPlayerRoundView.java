package com.waterfairy.media.audio.play;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.waterfairy.play.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 16:49
 * @info:
 */
public class AudioPlayerRoundView extends AbAudioPlayerView {


    public AudioPlayerRoundView(Context context) {
        this(context, null);
    }

    public AudioPlayerRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int getResLayout() {
        return R.layout.layout_audio_play_audio_play_round;
    }

    @Override
    protected AbAudioPlayButtonView getPlayButton() {
        return findViewById(R.id.bt_play);
    }

    @Override
    protected ImageView getIvLoading() {
        return findViewById(R.id.iv_loading);
    }

    @Override
    protected SeekBar getSeekBar() {
        return findViewById(R.id.progress_bar);
    }

    @Override
    protected TextView getTvTimeTotal() {
        return findViewById(R.id.time_total);
    }

    @Override
    protected TextView getTvTimeCurrent() {
        return findViewById(R.id.time_current);
    }
}
