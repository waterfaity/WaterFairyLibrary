package com.waterfairy.librarypickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * user:xuchangwei
 * time:2020/12/31
 * info:
 */
public class PickerView extends View {
    public static final int STYLE_CENTER = 0;
    public static final int STYLE_LEFT = 1;
    public static final int STYLE_RIGHT = 2;
    private static final String TAG = "pickerView";
    //展示样式  (0中间 1左侧 2右侧)
    private int showStyle;
    private List<String> dataList;
    //当前位置
    private int currentPos = 0;
    //展示的数据个数
    private int showDataSize = 5;
    //偏移量
    private int firstDataTransX = 0;
    private int centerDataTransX = 80;
    //文本大小
    private int firstDataTextSize = 20;
    private int centerDataTextSize = 40;
    //文本颜色
    private int firstDataTextColor = Color.CYAN;
    private int centerDataTextColor = Color.BLUE;


    private int startX, endX = 0;
    private int startY, endY = 0;


    //第一个数据中心 y
    private int itemHeight = 50;
    //中心y
    private int centerY;
    //过度距离    (0 -> roundHeight -> 0)
    private int roundHeight;
    //文本 差值
    private int dTextSize;
    //偏移 差值
    private int dTransX;


    private int scrollY;

    private Rect tempRect = new Rect();

    //画笔
    private Paint paint;

    private GestureFlingTool gestureFlingTool;
    private float touchStartY;

    public PickerView(Context context) {
        this(context, null);
    }


    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);


        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PickerView);

            firstDataTextSize = typedArray.getDimensionPixelSize(R.styleable.PickerView_firstDataTextSize, firstDataTextSize);
            centerDataTextSize = typedArray.getDimensionPixelSize(R.styleable.PickerView_centerDataTextSize, centerDataTextSize);

            firstDataTextColor = typedArray.getColor(R.styleable.PickerView_firstDataTextColor, firstDataTextColor);
            centerDataTextColor = typedArray.getColor(R.styleable.PickerView_centerDataTextColor, centerDataTextColor);


            firstDataTransX = typedArray.getDimensionPixelSize(R.styleable.PickerView_firstDataTransX, firstDataTransX);
            centerDataTransX = typedArray.getDimensionPixelSize(R.styleable.PickerView_centerDataTransX, centerDataTransX);

            showStyle = typedArray.getInt(R.styleable.PickerView_showStyle, STYLE_CENTER);
            showDataSize = typedArray.getInt(R.styleable.PickerView_showDataSize, showDataSize);
            itemHeight = typedArray.getDimensionPixelSize(R.styleable.PickerView_itemHeight, itemHeight);

            typedArray.recycle();
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataList.add("数据" + i);
        }

        gestureFlingTool = new GestureFlingTool();
        gestureFlingTool.setOnFlingListener(new GestureFlingTool.OnFlingListener() {
            @Override
            public void onFling(int x, int y, int dX, int dY) {
                move(y);
            }

            @Override
            public void onFlingEnd() {

            }
        });

        detector = new GestureDetector(getContext(), getGestureListener());
    }

    GestureDetector detector;

    private GestureDetector.OnGestureListener getGestureListener() {
        return new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                gestureFlingTool.startFling(e1, e2, velocityX, velocityY);
                return true;
            }
        };
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //计算相对view的y轴坐标

        for (int i = 0; i < dataList.size(); i++) {
            calcTextParams(canvas, i);
        }
        paint.setColor(Color.RED);
        canvas.drawLine(0, getHeight() >> 1, getWidth(), getHeight() >> 1, paint);
    }


    /**
     * 计算在指定位置的 文本状态
     */
    private void calcTextParams(Canvas canvas, int pos) {

        int currentY = pos * itemHeight + scrollY;
        int totalHeight = dataList.size() * itemHeight;

        //求余  为负? otalHeight  :  0;
        int currentStartY = (currentStartY = currentY % totalHeight) + (currentStartY < 0 ? totalHeight : 0);


        //超出边界不绘制
        if (currentStartY < 0 || currentStartY - itemHeight - getHeight() > 0) return;

        //绘制text
        drawText(canvas, pos, currentStartY);

    }

    public void initData() {
        centerY = (getHeight() >> 1);
        roundHeight = (showDataSize >> 1) * itemHeight;
        dTextSize = centerDataTextSize - firstDataTextSize;
        scrollY = (int) ((getHeight() >> 1) + 0.5F * itemHeight);
        dTransX = centerDataTransX - firstDataTransX;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initData();
    }

    /**
     * 绘制文本
     *
     * @param canvas
     * @param pos
     * @param y
     * @return
     */
    private void drawText(Canvas canvas, int pos, int y) {


        //距离中心位置 差距
        int dHeight = y - (itemHeight >> 1) - centerY;

        //在 界限之外的处理为在界限上
        if (dHeight < -roundHeight || dHeight > roundHeight) dHeight = roundHeight;

        //计算差距的比例
        float ratio = 1 - Math.abs(dHeight) / (float) roundHeight;

        //计算此时的文本大小
        float currentTextSize = firstDataTextSize + ratio * dTextSize;

        //计算颜色
        int currentColor = firstDataTextColor;
        if (firstDataTextColor != centerDataTextColor) {
            currentColor = transColor(firstDataTextColor, centerDataTextColor, ratio);
        }

        String text = dataList.get(pos);
        paint.setTextSize(currentTextSize);
        paint.getTextBounds(text, 0, text.length(), tempRect);
        paint.setColor(currentColor);

        //当前y
        int currentY = y - ((itemHeight - tempRect.height()) >> 1) - tempRect.bottom;

        //当前x
        int currentX = (int) (firstDataTransX + dTransX * ratio);

        canvas.drawText(text, currentX, currentY, paint);

        tempRect.bottom += currentY;
        tempRect.top += currentY;
        paint.setColor(Color.parseColor("#22000000"));
        canvas.drawRect(tempRect, paint);
        canvas.drawLine(0, y, getWidth(), y, paint);

    }

    private int transColor(int colorFrom, int colorEnd, float ratio) {
        Log.i(TAG, "transColor: " + ratio);
        if (ratio >= 1) return colorEnd;
        if (ratio <= 0) return colorFrom;
        int alpha, red, green, blue;

        int dAlpha = Color.alpha(colorEnd) - (alpha = Color.alpha(colorFrom));
        int dRed = Color.red(colorEnd) - (red = Color.red(colorFrom));
        int dGreen = Color.green(colorEnd) - (green = Color.green(colorFrom));
        int dBlue = Color.blue(colorEnd) - (blue = Color.blue(colorFrom));

        return Color.argb((int) (alpha + dAlpha * ratio), (int) (red + dRed * ratio), (int) (green + dGreen * ratio), (int) (blue + dBlue * ratio));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (detector.onTouchEvent(event)) return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            move(event.getY());
        }
        return true;
    }

    /**
     * @param touchY
     */
    private void move(float touchY) {
        //刷新
        scrollY += (touchY - touchStartY);
        touchStartY = touchY;
        invalidate();
    }
}
