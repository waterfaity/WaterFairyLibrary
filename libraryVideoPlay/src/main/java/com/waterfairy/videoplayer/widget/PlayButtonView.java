package com.waterfairy.videoplayer.widget;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;

import com.waterfairy.videoplayer.R;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/7/20 10:49
 * @info:
 */
public class PlayButtonView extends androidx.appcompat.widget.AppCompatImageView implements View.OnClickListener {

    private boolean isPlaying = true;

    public PlayButtonView(Context context) {
        this(context, null);

    }

    public PlayButtonView(Context context, AttributeSet attrs) {
        super(context, attrs);
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
            setImageResource(R.mipmap.icon_play);
        } else {
            setImageResource(R.mipmap.icon_pause);
        }
    }
}
