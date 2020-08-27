package com.waterfairy.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
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
public class HistogramChartView extends BaseChartView {

    private int itemSpace = 10;//条目内部不同数据间的间距
    private int itemWidth = 50;//条目宽度

    public HistogramChartView(Context context) {
        super(context);
    }

    public HistogramChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public int getItemSpace() {
        return itemSpace;
    }

    public void setItemSpace(int itemSpace) {
        this.itemSpace = itemSpace;
    }

    public int getItemWidth() {
        return itemWidth;
    }

    public void setItemWidth(int itemWidth) {
        this.itemWidth = itemWidth;
    }

    @Override
    protected void drawChart(Canvas canvas, List<Coordinate> currentCoordinateList, int currentIndex, int currentX, int mulDataSize, int mulDataIndex) {
        super.drawChart(canvas, currentCoordinateList, currentIndex, currentX, mulDataSize, mulDataIndex);
        Coordinate coordinate = currentCoordinateList.get(currentIndex);


        int rootWidth = mulDataSize * itemWidth + (mulDataSize - 1) * itemSpace;

        int rootStartX = currentX - rootWidth / 2;

        int currentItemStartX = rootStartX + mulDataIndex * itemWidth + mulDataIndex * itemSpace;

        int left = currentItemStartX;
        int right = left + itemWidth;

        int top = coordinate.y;
        int bottom = bottomLine;

        //完全越界
        if (right < leftLine || left > rightLine) return;

        //部分越界
        if (left < leftLine) left = leftLine;
        if (right > rightLine) right = rightLine;

        mPaintChart.setColor(colors[mulDataIndex]);
        canvas.drawRect(left, top, right, bottom, mPaintChart);

    }
}
