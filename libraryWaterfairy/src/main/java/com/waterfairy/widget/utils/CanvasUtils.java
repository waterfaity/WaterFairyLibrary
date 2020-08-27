package com.waterfairy.widget.utils;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 10:48
 * @info:
 */
public class CanvasUtils {
    /**
     * 绘制圆角
     *
     * @param canvas      画笔
     * @param rect        边框
     * @param radius      半径
     * @param strokeWidth 线宽
     * @param strokeColor 线颜色
     * @param solid       背景颜色
     * @param paint       画笔
     */
    public static void drawCorner(Canvas canvas, RectF rect, int radius, float strokeWidth, int strokeColor, int solid, Paint paint) {
        if (canvas != null && rect != null) {
            float strokeWidthSrc = 0;
            Paint.Style styleSrc = null;
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setStrokeWidth(strokeWidth);
            } else {
                strokeWidthSrc = paint.getStrokeWidth();
                styleSrc = paint.getStyle();
                paint.setStrokeWidth(strokeWidth);
            }

            Path path = PathUtils.getCorner(rect, radius);
            //stroke
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(strokeColor);
            canvas.drawPath(path, paint);
            //solid
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(solid);
            canvas.drawPath(path, paint);
            //复原
            if (styleSrc != null)
                paint.setStyle(styleSrc);
            paint.setStrokeWidth(strokeWidthSrc);
        }
    }


    /**
     * 绘制水平文本 附带 颜色条
     * 说明:
     * 以最宽的文本为标准
     *
     * @param canvas
     * @param textListBean
     * @param colors
     * @param paint
     */
    public static void drawHorTextList(Canvas canvas, RectUtils.TextRectFBean textListBean, int[] colors, Paint paint) {
        if (canvas != null && textListBean != null && textListBean.texts != null) {
            if (textListBean.padding < 0) textListBean.padding = 0;
            if (colors == null || colors.length == 0) {
                colors = new int[]{Color.GRAY};
            }
            if (paint == null) {
                paint = new Paint();
                paint.setAntiAlias(true);
                paint.setTextSize(textListBean.textSize);
            }

            //源paint 属性
            float strokeWidth = paint.getStrokeWidth();
            int paintColor = paint.getColor();
            float textSize = paint.getTextSize();
            //线高度
            //每行高度
            float perHeight = textListBean.perHeight * (1 + textListBean.textTimes);
            paint.setStrokeWidth(textListBean.perHeight / 4);
            paint.setTextSize(textListBean.textSize);
            //线中心y
            float lineCenterY = 0;
            for (int i = 0; i < textListBean.lineNum; i++) {
                //每行文本y起点 第一行不加倍
                float y = 0;
                if (i == 0) {
                    y = textListBean.rectF.top + textListBean.padding + textListBean.perHeight;
                } else {
                    y = textListBean.rectF.top + textListBean.padding + textListBean.perHeight + perHeight * i;
                }
                //文本底线 - 文本高度的一半
                lineCenterY = y - textListBean.perHeight / 3;
                for (int j = 0; j < textListBean.columnNum; j++) {
                    int pos = i * textListBean.columnNum + j;
                    if (textListBean.texts.size() > pos) {
                        paint.setColor(colors[pos % colors.length]);
                        //获取文本
                        String text = textListBean.texts.get(pos);
                        //线/文本x起点
                        float startX = j * textListBean.perWidth + textListBean.padding + textListBean.rectF.left;
                        if (textListBean.hasColorLine) {
                            //画线
                            paint.setStrokeWidth(textListBean.perHeight / 4);
                            canvas.drawLine(startX, lineCenterY, startX + textListBean.singleTextWidth, lineCenterY, paint);
                            //文本x起点
                            startX += textListBean.singleTextWidth * 1.5F;
                        }
                        paint.setStrokeWidth(strokeWidth);
                        canvas.drawText(text, startX, y, paint);
                    }
                }
            }
            paint.setTextSize(textSize);
            paint.setColor(paintColor);
        }
    }
}
