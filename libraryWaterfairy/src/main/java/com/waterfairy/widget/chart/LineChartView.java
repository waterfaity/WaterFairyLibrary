package com.waterfairy.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;

import com.waterfairy.widget.baseview.Coordinate;

import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/8 13:45
 * @info:
 */
public class LineChartView extends BaseChartView {
    private Paint mPaintLine;
    private int radius;

    public LineChartView(Context context) {
        super(context);
    }

    public LineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void initPaint() {
        super.initPaint();
        mPaintLine = new Paint();
        mPaintLine.setColor(Color.parseColor("#289456"));
        mPaintLine.setAntiAlias(true);
        mPaintLine.setStrokeWidth(density * 1);
    }

    @Override
    protected void calcData() {
        super.calcData();
        radius = (int) (density * 2.5);
    }

    /**
     * @param canvas
     * @param currentCoordinateList
     * @param currentPos
     * @param currentX
     * @param mulDataSize
     * @param mulDataIndex
     */
    @Override
    protected void drawChart(Canvas canvas, List<Coordinate> currentCoordinateList, int currentPos, int currentX, int mulDataSize, int mulDataIndex) {
        Coordinate coordinate = currentCoordinateList.get(currentPos);
        boolean overSideLeft = false;
        boolean overSideRight = false;
        if (currentX < leftLine) overSideLeft = true;
        if (currentX > rightLine) overSideRight = true;
        //修改 不超出边界
        overSideLeft = false;
        overSideRight = false;

        if (!overSideLeft) {
            //没有超出左边界
            // 绘制x坐标刻度
            if (!overSideRight) {
                canvas.drawLine(currentX, bottomLine, currentX, bottomLine + textHeight / 2, mPaintLineY);
            }
            //绘制圆点
            canvas.drawCircle(currentX, coordinate.y, radius, mPaintLine);
            //绘制数据值
            if (!isMulti) {
                Rect textRect = getTextRect(coordinate.value + "", mTextSize);
                int widthHalf = textRect.width() / 2;
                canvas.drawText(coordinate.value + "", currentX - widthHalf, coordinate.y - textHeight, mPaintText);
            }
            if (currentPos != startPos + xNum && (currentPos + 1 < currentCoordinateList.size())) {
                //画折线
                Coordinate coordinate2 = currentCoordinateList.get(currentPos + 1);
                canvas.drawLine(currentX, coordinate.y, coordinate2.x + scrollX, coordinate2.y, mPaintLine);
            }
        }
    }
}
