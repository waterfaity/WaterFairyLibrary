package com.waterfairy.librarypickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
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
    private List<Object> dataList;
    //当前位置
    private int currentPos = 0;
    //展示的数据个数
    private int showDataSize = 5;
    //偏移量
    private int firstDataTransX;
    private int centerDataTransX;
    //文本大小
    private int firstDataTextSize = 20;
    private int centerDataTextSize = 40;
    //文本颜色
    private int firstDataTextColor = Color.CYAN;
    private int centerDataTextColor = Color.BLUE;

    //分割线
    private int diverLineWidth;
    private int diverLineHeight;
    private int diverLineRadius;
    private int diverLineColor;
    //分割线1
    private RectF diverLineY1;
    //分割线2
    private RectF diverLineY2;

    //第一个数据中心 y
    private int itemHeight = 50;
    private int halfItemHeight = itemHeight >> 1;
    private int allHeight = 50;
    //中心y
    private int centerX;
    private int centerY;
    //过度距离    (0 -> roundHeight -> 0)
    private int chaneHeightRange;
    //文本 差值
    private int dTextSize;
    //偏移 差值
    private int dTransX;


    //初始滚动X
    private int oriScrollY;
    //滚动x
    private int scrollY;
    //中间数据: 计算文本宽高数值
    private final Rect tempRect = new Rect();
    //按下y坐标
    private float downY;
    //画笔
    private final Paint paint;
    //手势
    private final GestureDetector detector;
    //飞滚
    private final GestureFlingTool gestureFlingTool;
    //手指松开  自动滚到到当前下标
    private final AnimEndTool animEndTool;

    private OnPickListener onPickListener;

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

            diverLineWidth = typedArray.getDimensionPixelSize(R.styleable.PickerView_diverLineWidth, diverLineWidth);
            diverLineHeight = typedArray.getDimensionPixelSize(R.styleable.PickerView_diverLineHeight, diverLineHeight);
            diverLineRadius = typedArray.getDimensionPixelSize(R.styleable.PickerView_diverLineRadius, diverLineRadius);
            diverLineColor = typedArray.getColor(R.styleable.PickerView_diverLineColor, diverLineColor);

            typedArray.recycle();
        }

        paint = new Paint();
        paint.setAntiAlias(true);
        dataList = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            dataList.add("数据数据数据数据" + i);
        }
        //飞滚
        gestureFlingTool = new GestureFlingTool(new GestureFlingTool.OnFlingListener() {
            @Override
            public void onFling(int x, int y, int dX, int dY) {
                onMove(y);
            }

            @Override
            public void onFlingEnd(float x, float y) {
                onUp(y);
            }
        });
        //移动到指定下标的位置
        animEndTool = new AnimEndTool(new AnimEndTool.OnAnimEndListener() {
            @Override
            public void onAnimEnd(float current) {
                //根据偏移位置计算偏移下标
                //偏移位置 取正
                //(scrollY - oriScrollY) % allHeight + allHeight
                //偏移数量
                //(((scrollY - oriScrollY) % allHeight + allHeight) / itemHeight)
                //反向 去1 得当前下标
                currentPos = dataList.size() - 1 -
                        (((((scrollY - oriScrollY) % allHeight + allHeight)) % allHeight / itemHeight));
                if (onPickListener != null) onPickListener.onPick(currentPos);
//                Log.i(TAG, "onAnimEnd: " + currentPos);
            }

            @Override
            public void onAnimUpdating(float current) {
                onMove(current);
            }
        });
        //手势
        detector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                gestureFlingTool.startFling(e1, e2, velocityX, velocityY);
                return true;
            }
        });
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //分割线

        if (diverLineY1 != null) {
            paint.setColor(diverLineColor);
            canvas.drawRoundRect(diverLineY1, diverLineRadius, diverLineRadius, paint);
            canvas.drawRoundRect(diverLineY2, diverLineRadius, diverLineRadius, paint);
        }

        //计算相对view的y轴坐标
        for (int i = 0; i < dataList.size(); i++) {
            calcTextParams(canvas, i);
        }

        //中心线
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

    private void initData() {
        centerY = (getHeight() >> 1);
        centerX = (getWidth() >> 1);
        halfItemHeight = itemHeight >> 1;
        allHeight = dataList.size() * itemHeight;
        chaneHeightRange = (showDataSize >> 1) * itemHeight;
        dTextSize = centerDataTextSize - firstDataTextSize;
        scrollY = oriScrollY = (int) ((getHeight() >> 1) + 0.5F * itemHeight);
        dTransX = centerDataTransX - firstDataTransX;
        Log.i(TAG, "initData: oriScrollY:" + oriScrollY);

        if (showStyle == STYLE_CENTER) {
            paint.setTextAlign(Paint.Align.CENTER);
            if (diverLineWidth != 0 && diverLineHeight != 0) {

                float halfLineWidth = diverLineWidth >> 1;
                float halfLineHeight = diverLineHeight >> 1;
                diverLineY1 = new RectF(centerX - halfLineWidth, centerY - halfLineHeight - halfItemHeight, centerX + halfLineWidth, centerY + halfLineHeight - halfItemHeight);
                diverLineY2 = new RectF(diverLineY1.left, diverLineY1.top + itemHeight, diverLineY1.right, diverLineY1.bottom + itemHeight);
            }
        }

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
        int dHeight = y - halfItemHeight - centerY;

        //在 界限之外的处理为在界限上
        if (dHeight < -chaneHeightRange || dHeight > chaneHeightRange) dHeight = chaneHeightRange;

        //计算差距的比例
        float ratio = 1 - Math.abs(dHeight) / (float) chaneHeightRange;

        //计算此时的文本大小
        float currentTextSize = firstDataTextSize + ratio * dTextSize;

        //计算颜色
        int currentColor = firstDataTextColor;
        if (firstDataTextColor != centerDataTextColor) {
            currentColor = transColor(firstDataTextColor, centerDataTextColor, ratio);
        }

        String text = dataList.get(pos).toString();
        paint.setTextSize(currentTextSize);
        paint.getTextBounds(text, 0, text.length(), tempRect);
        paint.setColor(currentColor);

        //当前y
        int currentY = y - ((itemHeight - tempRect.height()) >> 1) - tempRect.bottom;

        //当前x
        int currentX = 0;
        if (showStyle == STYLE_CENTER) {
            //中心位置
            currentX = getWidth() >> 1;
        } else if (showStyle == STYLE_LEFT) {
            //左侧
            currentX = (int) (firstDataTransX + dTransX * ratio);
        } else if (showStyle == STYLE_RIGHT) {
            //右侧
            currentX = getWidth() - tempRect.width() - (int) (firstDataTransX + dTransX * ratio);
        }

        canvas.drawText(text, currentX, currentY, paint);

        tempRect.bottom += currentY;
        tempRect.top += currentY;
        paint.setColor(Color.parseColor("#22000000"));
        canvas.drawRect(tempRect, paint);
        canvas.drawLine(0, y, getWidth(), y, paint);
    }

    /**
     * 颜色过度
     *
     * @param colorFrom
     * @param colorEnd
     * @param ratio
     * @return
     */
    private int transColor(int colorFrom, int colorEnd, float ratio) {

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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            gestureFlingTool.stop();
            animEndTool.stop();
        }
        if (detector.onTouchEvent(event)) return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            onMove(event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onUp(event.getY());
        }
        return true;
    }

    /**
     * 触摸抬起
     *
     * @param touchY
     */
    private void onUp(float touchY) {
        scrollY += (touchY - downY);

        float absHeight = ((scrollY - centerY) % itemHeight + itemHeight) % itemHeight;

        Log.i(TAG, "onUp:  条目高度:" + ((int) itemHeight) + "\tscrollY:" + scrollY + "\t\t相对高度:" + ((int) absHeight));

        float dY = absHeight - halfItemHeight;

        float posY = touchY - dY;
        Log.i(TAG, "onUp: from:" + ((int) touchY) + " -> to:" + ((int) posY));
        animEndTool.startAnimEnd(touchY, posY);
    }

    /**
     * 触摸移动中
     *
     * @param touchY
     */
    private void onMove(float touchY) {
        //刷新
        scrollY += (touchY - downY);
        downY = touchY;
        invalidate();
    }

    /**
     * 选择监听
     */
    public interface OnPickListener {
        void onPick(int pos);
    }
}
