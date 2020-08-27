package com.waterfairy.media.audio.play;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.jetbrains.annotations.NotNull;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2020/4/8 16:05
 * @info:
 */
public class AudioPlayViewShowTool {
    AudioPlayerView audioPlayerView;
    private OnPlayClickListener onPlayClickListener;
    private float dy;

    public OnPlayClickListener getOnPlayClickListener() {
        return onPlayClickListener;
    }

    public void setOnPlayClickListener(OnPlayClickListener onPlayClickListener) {
        this.onPlayClickListener = onPlayClickListener;
    }

    public AudioPlayViewShowTool(AudioPlayerView audioPlayerView, boolean initShow) {
        if (!initShow) {
            audioPlayerView.setVisibility(View.GONE);
        }
        this.audioPlayerView = audioPlayerView;
        dy = audioPlayerView.getResources().getDisplayMetrics().density * 75;
        audioPlayerView.setOnPlayClickListener(new OnPlayClickListener() {
            @Override
            public void onPlayClick() {
                if (onPlayClickListener != null) onPlayClickListener.onPlayClick();
            }

            @Override
            public void onPauseClick() {
                if (onPlayClickListener != null) onPlayClickListener.onPauseClick();

            }

            @Override
            public void onCloseClick() {
                if (onPlayClickListener != null) onPlayClickListener.onCloseClick();

                show(false);
            }
        });

    }


    public void show(boolean isShow) {
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(audioPlayerView, "translationY", isShow ? (new float[]{dy, 0}) : (new float[]{0, dy}));
        objectAnimator.setDuration(500);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                audioPlayerView.setClick(false);
                audioPlayerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                audioPlayerView.setClick(true);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        objectAnimator.start();
    }

    public void onResume() {
        audioPlayerView.onResume();
    }

    public void onPause() {
        audioPlayerView.onPause();
    }

    public void onDestroy() {
        audioPlayerView.release();
    }

    public void play(@NotNull String path) {
        audioPlayerView.play(path);
        show(true);
    }
}
