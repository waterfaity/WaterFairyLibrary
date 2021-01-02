package com.waterfairy.librarypickerview;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019-08-25 16:57
 * @info:
 */
public class AnimEndTool {
    private OnAnimEndListener onAnimEndListener;

    private MyValueAnimator valueAnimator;

    public AnimEndTool(OnAnimEndListener onAnimEndListener) {
        this.onAnimEndListener=onAnimEndListener;
    }

    public void setOnAnimEndListener(OnAnimEndListener onAnimEndListener) {
        this.onAnimEndListener = onAnimEndListener;
    }


    public void startAnimEnd(float start, float end) {
        stop();
        valueAnimator = new MyValueAnimator(start, end);
        valueAnimator.setOnAnimEndListener(onAnimEndListener);
        valueAnimator.start();
    }


    public void stop() {
        if (valueAnimator != null) {
            valueAnimator.work = false;
            valueAnimator.setOnAnimEndListener(null);
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }


    public static class MyValueAnimator extends ValueAnimator {
        private final float start;
        private final float end;
        public boolean work;//是否在工作
        private float current;

        private OnAnimEndListener onAnimEndListener;

        MyValueAnimator setOnAnimEndListener(OnAnimEndListener onAnimEndListener) {
            this.onAnimEndListener = onAnimEndListener;
            return this;
        }

        MyValueAnimator(float start, float end) {

            this.start = start;
            this.end = end;
            current = start;
            initListener();
            initAnim();

        }

        private void initAnim() {
            setInterpolator(new AccelerateDecelerateInterpolator());
            setFloatValues(start, end);
            setDuration(100);
        }

        private void initListener() {
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!work) return;
                    current = (float) animation.getAnimatedValue();
                    //计算当前坐标
                    if (onAnimEndListener != null)
                        onAnimEndListener.onAnimUpdating(current);
                }
            });

            addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!work) return;
                    onAnimEndListener.onAnimEnd(current);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (!work) return;
                    if (onAnimEndListener != null)
                        onAnimEndListener.onAnimEnd(current);
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

        }

        @Override
        public void start() {
            work = true;
            super.start();
        }
    }


    public interface OnAnimEndListener {

        void onAnimEnd(float current);

        void onAnimUpdating(float current);
    }
}
