package com.waterfairy.media.audio.play;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.waterfairy.play.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 10:49
 * @info:
 */
public class AbAudioPlayButtonView extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener {

    private boolean isPlaying = true;
    private int resPlay = 0;
    private int resPause = 0;

    public AbAudioPlayButtonView(Context context) {
        this(context, null);

    }

    public AbAudioPlayButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.AudioPlayButtonView);
        resPlay = typedArray.getResourceId(R.styleable.AudioPlayButtonView_res_play, 0);
        resPause = typedArray.getResourceId(R.styleable.AudioPlayButtonView_res_pause, 0);
        setOnClickListener(this);
        setState(true);
    }

    private boolean canClick = true;
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            canClick = true;
        }
    };

    @Override
    public void onClick(View v) {
        if (canClick) {
            canClick = false;
            handler.sendEmptyMessageDelayed(0, 300);
        } else {
            return;
        }
        if (onPlayClickListener != null) onPlayClickListener.onPlayClick();
    }

    private OnPlayClickListener onPlayClickListener;

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

    public interface OnPlayClickListener {
        void onPlayClick();
    }

    /**
     * @param play
     */
    public void setState(boolean play) {
        this.isPlaying = !play;
        setViewState(play);
    }

    private void setViewState(boolean play) {
        if (play) {
            setImageResource(getResPlay());
        } else {
            setImageResource(getResPause());
        }
    }

    protected int getResPlay() {
        return resPlay;
    }

    protected int getResPause() {
        return resPause;
    }
}
