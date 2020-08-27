package com.waterfairy.widget.utils;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.MotionEvent;
import android.view.animation.AccelerateDecelerateInterpolator;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2019-08-25 16:57
 * @info:
 */
public class GestureFlingTool {


    private OnFlingListener onFlingListener;

    private MyValueAnimator valueAnimator;

    private boolean isFling;

    public boolean isFling() {
        return isFling;
    }

    public void setOnFlingListener(OnFlingListener onFlingListener) {
        this.onFlingListener = onFlingListener;
    }

    /**
     * @param startEvent 起点
     * @param endEvent   终点
     * @param velocityX  x速度(per second)
     * @param velocityY  y速度
     */
    public void startFling(MotionEvent startEvent, final MotionEvent endEvent, final float velocityX, final float velocityY) {
        stop();
        valueAnimator = new MyValueAnimator(startEvent, endEvent, velocityX, velocityY);
        valueAnimator.setOnFlingListener(new OnFlingListener() {
            @Override
            public void onFlingStart(float x, float y) {
                isFling = true;
                if (onFlingListener != null) onFlingListener.onFlingStart(x, y);
            }

            @Override
            public void onFling(float x, float y) {
                if (onFlingListener != null) onFlingListener.onFling(x, y);
            }

            @Override
            public void onFlingEnd() {
                isFling = false;
                if (onFlingListener != null) onFlingListener.onFlingEnd();

            }
        });
        valueAnimator.start();
    }


    public void stop() {
        if (valueAnimator != null) {
            valueAnimator.work = false;
            valueAnimator.setOnFlingListener(null);
            valueAnimator.cancel();
            valueAnimator = null;
        }
    }


    public static class MyValueAnimator extends ValueAnimator {
        private static final String TAG = "myValue";
        public boolean work;//是否在工作

        private final MotionEvent startEvent;
        private final MotionEvent endEvent;
        private final float velocityX;//起始速度x
        private final float velocityY;

        final float endEventX;//飞滚起点x
        final float endEventY;//飞滚起点y

        private float currentX;
        private float currentY;

        private OnFlingListener onFlingListener;
        private long currentTime;

        MyValueAnimator setOnFlingListener(OnFlingListener onFlingListener) {
            this.onFlingListener = onFlingListener;
            return this;
        }

        MyValueAnimator(MotionEvent startEvent, MotionEvent endEvent, float velocityX, float velocityY) {

            this.startEvent = startEvent;
            this.endEvent = endEvent;
            this.velocityX = velocityX;
            this.velocityY = velocityY;

            endEventX = endEvent.getX();
            endEventY = endEvent.getY();

            currentX = endEventX;
            currentY = endEventY;

            currentTime = System.currentTimeMillis();

            initListener();

            initAnim();

        }

        private void initAnim() {
            setInterpolator(new AccelerateDecelerateInterpolator());
            setFloatValues(1, 0);
            setDuration(300);
        }

        private void initListener() {
            addUpdateListener(new AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    if (!work) return;
                    float radio = (float) animation.getAnimatedValue();
                    //时间差
                    long dTime = System.currentTimeMillis() - currentTime;
                    //位移=当前速度*时间差
                    float dX = velocityX * radio * (dTime / 1000F);
                    float dY = velocityY * radio * (dTime / 1000F);

                    //计算当前坐标
                    currentX += dX;
                    currentY += dY;

                    if (onFlingListener != null)
                        onFlingListener.onFling(currentX, currentY);
                    currentTime = System.currentTimeMillis();

                }
            });

            addListener(new AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    if (!work) return;
                    if (onFlingListener != null) onFlingListener.onFlingStart(endEventX, endEventY);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!work) return;
                    if (onFlingListener != null) onFlingListener.onFlingEnd();
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    if (!work) return;
                    if (onFlingListener != null) onFlingListener.onFlingEnd();
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


    public interface OnFlingListener {
        void onFlingStart(float x, float y);

        void onFling(float x, float y);

        void onFlingEnd();
    }
}
