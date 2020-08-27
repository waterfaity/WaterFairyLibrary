package com.waterfairy.widget.utils;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;

import java.util.List;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 13:27
 * @info:
 */
public class RectUtils {
    /**
     * 获取文本边框
     *
     * @param content
     * @param textSize
     * @return
     */
    public static Rect getTextRect(String content, int textSize) {
        Rect rect = new Rect();
        if (TextUtils.isEmpty(content) || textSize <= 0) return rect;
        Paint paint = new Paint();
        paint.setTextSize(textSize);
        paint.getTextBounds(content, 0, content.length(), rect);
        return rect;
    }

    /**
     * 有参照物的边框的转换
     * 有中心坐标
     *
     * @param rectF
     * @param centerX
     * @param centerY
     * @return
     */
    public static RectF getRectF(RectF rectF, float centerX, float centerY) {
        return getRectF(rectF, centerX, centerY, 0, 0, 0, 0);
    }

    /**
     * 有参照物的边框的转换
     * 1.有中心坐标
     * 2.确保不会超过边界
     *
     * @param centerX 将要显示的中心
     * @param centerY
     * @param left    边界
     * @param top
     * @param right
     * @param bottom
     * @return
     */
    public static RectF getRectF(RectF rectF,
                                 float centerX, float centerY,
                                 int left, int top, int right, int bottom) {
        //width
        float textWidth = rectF.width();
        float halfWidth = textWidth / 2;
        //height
        float textHeight = rectF.height();
        float halfHeight = textHeight / 2;

        float textLeft = 0, textRight = 0;
        if (right - left == 0) {
            textLeft = centerX - halfWidth;
            textRight = centerX + halfWidth;
        } else if (centerX - left < halfWidth) {
            textLeft = left;
            textRight = (int) (left + textWidth);
        } else if (right - centerX < halfWidth) {
            textRight = right;
            textLeft = right - textWidth;
        } else {
            textLeft = centerX - halfWidth;
            textRight = centerX + halfWidth;
        }

        float textTop = 0, textBottom = 0;
        if (bottom - top == 0) {
            textTop = centerY - halfHeight;
            textBottom = centerY + halfHeight;
        } else if (centerY - top < halfHeight) {
            textTop = top;
            textBottom = (int) (top + textHeight);
        } else if (bottom - centerY < halfHeight) {
            textBottom = bottom;
            textTop = bottom - textHeight;
        } else {
            textTop = centerY - halfHeight;
            textBottom = centerY + halfHeight;
        }
        return new RectF(textLeft, textTop, textRight, textBottom);
    }
    /*--------------------水平字体----------------------------------------------------------------------*/

    /**
     * * 获取 list 水平文本边框
     *
     * @param texts        文本list
     * @param textSize     字体大小
     * @param textTimes    字体间距倍数
     * @param canvasWidth  画布宽  保证文本最大长度小于画布宽
     * @param padding      边框距离
     * @param endWidth     字体后间距
     * @param hasColorLine 画标识线
     * @param singleColumn 单例
     * @return TextListBean
     */
    public static TextRectFBean getTextRectFBean(List<String> texts, float textSize, float textTimes,
                                                 float canvasWidth, float padding, float endWidth,
                                                 boolean hasColorLine, boolean singleColumn) {
        if (texts == null || texts.size() == 0) return null;
        if (padding < 0) padding = 0;
        int maxWidth = 0;
        int maxHeight = 0;
        float tempWidth = 0;
        //线长1倍字体  与字体间距0.5倍字体
        tempWidth = getTextRect("正", (int) textSize).width();
        for (int i = 0; i < texts.size(); i++) {
            String text = texts.get(i);
            Rect textRect = getTextRect(text, (int) textSize);
            int width = textRect.right + textRect.left;
            int height = textRect.bottom - textRect.top;
            maxWidth = Math.max(width, maxWidth);
            maxHeight = Math.max(height, maxHeight);
        }
        //每段字体长度   字体长度+线长度(线长1倍字体  与字体间距0.5倍字体)+1倍字体间距
        float lineTextMaxWidth = 0;
        if (hasColorLine) {
            lineTextMaxWidth = maxWidth + tempWidth * 1.5F + endWidth;
        } else {
            lineTextMaxWidth = maxWidth + endWidth;
        }
        //列数 行数
        int columnNum = 0, lineNum = 0;
        if (lineTextMaxWidth < canvasWidth || canvasWidth == 0) {
            //每行个数
            if (singleColumn) columnNum = 1;
            else columnNum = (int) ((canvasWidth - (2 * padding)) / lineTextMaxWidth);
            //行数
            lineNum = texts.size() / columnNum;
            if (texts.size() % columnNum != 0) {
                lineNum++;
            }
        }
        //底坐标,第一行不加倍
        float bottom = 0;
        if (lineNum > 1) {
            bottom = maxHeight + 2 * padding + maxHeight * (1 + textTimes) * (lineNum - 1);
        } else {
            bottom = maxHeight + 2 * padding;
        }
        //如果只有一行  列数等于 texts的长度
        if (lineNum == 1) columnNum = texts.size();
        TextRectFBean horTextListBean = new TextRectFBean();
        horTextListBean.rectF = new RectF(0, 0, lineTextMaxWidth * columnNum + 2 * padding, bottom);
        horTextListBean.columnNum = columnNum;
        horTextListBean.lineNum = lineNum;
        horTextListBean.padding = padding;
        horTextListBean.hasColorLine = hasColorLine;
        horTextListBean.textTimes = textTimes;
        horTextListBean.texts = texts;
        horTextListBean.textSize = textSize;
        horTextListBean.perWidth = lineTextMaxWidth;
        horTextListBean.perHeight = maxHeight;
        horTextListBean.singleTextWidth = tempWidth;
        horTextListBean.centerY = horTextListBean.rectF.centerY();
        horTextListBean.centerX = horTextListBean.rectF.centerX();
        return horTextListBean;
    }

    public static class TextRectFBean {
        public float centerX;//中心x 有边界限定的  指限定之前的中心
        public float centerY;//中心y
        public int columnNum;//每行几个数据
        public int lineNum;//行
        public RectF rectF;//边界
        public float padding;//padding
        public boolean hasColorLine;//颜色条
        public float textTimes; //间距倍数
        public List<String> texts; //文本
        public float textSize;//文本大小
        public float perWidth;//每个模块 宽
        public int perHeight;//文本高度
        public float singleTextWidth;//文本高度
    }
}
