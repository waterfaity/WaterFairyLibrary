package com.waterfairy.widget.baseview;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/4/9
 * @Description:
 */

public abstract class BaseView extends View {
    protected boolean isDrawing;
    protected float density;
    protected int width, height;

    public BaseView(Context context) {
        this(context, null);
    }

    public BaseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public Rect getTextRect(String content, int textSize) {
        Rect rect = new Rect();
        if (TextUtils.isEmpty(content) || textSize <= 0) return rect;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect;
    }

    public int getTextWidth(String content, int textSize) {
        Rect textRect = getTextRect(content, textSize);
        return textRect.right + Math.abs(textRect.left);
    }

    protected float dp2Px(int dp) {
        return dp * getDensity();
    }

    protected float getDensity() {
        if (density == 0) {
            density = getResources().getDisplayMetrics().density;
        }
        return density;
    }

    protected void fresh() {

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
        if (width != 0 && height != 0) {
            onViewMeasure(changed, width, height);
        }
    }

    public void onViewMeasure(boolean changed, int width, int height) {

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }
}
