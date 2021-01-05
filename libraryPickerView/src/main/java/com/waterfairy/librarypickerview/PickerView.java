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

    private boolean loopAble = true;
    private boolean flyingAble = true;

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
    private int allItemHeight = 50;
    //中心y
    private int centerX;
    private int centerY;
    //过度距离    (0 -> roundHeight -> 0)
    private int chaneHeightRange;
    //文本 差值
    private int dTextSize;
    //偏移 差值
    private int dTransX;


    //初始滚动X(非循环最大滚动Y)
    private float oriScrollY;
    //非循环 最小滚动y
    private float oriMinScrollY;
    //滚动x
    private float scrollY;
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

    //触摸移动
    private final int MOVE_TYPE_TOUCH = 0;
    //飞滚
    private final int MOVE_TYPE_FLYING = 1;
    //移动到指定目标的动画
    private final int MOVE_TYPE_ANIM = 2;

    private OnPickListener onPickListener;

    public PickerView(Context context) {
        this(context, null);
    }


    public PickerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        if (attrs != null) {
            TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PickerView);

            flyingAble = typedArray.getBoolean(R.styleable.PickerView_flyingAble, flyingAble);
            loopAble = typedArray.getBoolean(R.styleable.PickerView_loopAble, loopAble);
            currentPos = typedArray.getInt(R.styleable.PickerView_currentPos, currentPos);

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
                onMove(MOVE_TYPE_FLYING, y);
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
                calcPos();
            }

            @Override
            public void onAnimUpdating(float current) {
                onMove(MOVE_TYPE_ANIM, current);
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

    private void calcPos() {
        //根据偏移位置计算偏移下标
        //偏移位置 取正
        //(scrollY - oriScrollY) % allHeight + allHeight
        //偏移数量
        //(((scrollY - oriScrollY) % allHeight + allHeight) / itemHeight)
        //反向 去1 得当前下标
        float tempPos = dataList.size() - 1 -
                (((((scrollY - oriScrollY - itemHeight) % allItemHeight + allItemHeight)) % allItemHeight / itemHeight));
        currentPos = Math.round(tempPos);
        if (onPickListener != null) onPickListener.onPick(currentPos);
//                Log.i(TAG, "onAnimEnd: " + currentPos);
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        initData();
    }

    /**
     * 初始化数据
     */
    private void initData() {
        //对下标处理
        if (dataList.size() - 1 < currentPos) {
            currentPos = dataList.size() - 1;
        }
        if (currentPos < 0) currentPos = 0;
        //中心xy
        centerY = getHeight() >> 1;
        centerX = getWidth() >> 1;
        //半个条目高度
        halfItemHeight = itemHeight >> 1;
        //所有数据最高占用高度
        allItemHeight = dataList.size() * itemHeight;
        //文本可改变的最大垂直距离
        chaneHeightRange = (showDataSize >> 1) * itemHeight;
        //文本大小最大差距
        dTextSize = centerDataTextSize - firstDataTextSize;
        //y轴最初偏移距离
        oriScrollY = (int) (centerY + 0.5F * itemHeight);
        oriMinScrollY = oriScrollY - allItemHeight + itemHeight;
        scrollY = oriScrollY - currentPos * itemHeight;
        //x轴最大偏移距离
        dTransX = centerDataTransX - firstDataTransX;
        Log.i(TAG, "initData: oriScrollY:" + oriScrollY);


        if (showStyle == STYLE_CENTER) {
            //设置文本绘制样式
            paint.setTextAlign(Paint.Align.CENTER);
            //指示线 两条
            if (diverLineWidth != 0 && diverLineHeight != 0) {
                float halfLineWidth = diverLineWidth >> 1;
                float halfLineHeight = diverLineHeight >> 1;
                diverLineY1 = new RectF(centerX - halfLineWidth, centerY - halfLineHeight - halfItemHeight, centerX + halfLineWidth, centerY + halfLineHeight - halfItemHeight);
                diverLineY2 = new RectF(diverLineY1.left, diverLineY1.top + itemHeight, diverLineY1.right, diverLineY1.bottom + itemHeight);
            }
        }
    }


    public int getCurrentPos() {
        return currentPos;
    }

    public void setCurrentPos(int pos) {
        //对下标处理
        currentPos = pos;
        if (dataList.size() - 1 < currentPos) {
            currentPos = dataList.size() - 1;
        }
        if (currentPos < 0) currentPos = 0;
        scrollY = oriScrollY - currentPos * itemHeight;
        invalidate();
        calcPos();
    }

    public void setDataList(List<Object> dataList) {
        this.dataList = dataList;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            //手指按下, 停止
            gestureFlingTool.stop();
            animEndTool.stop();
        }
        if (flyingAble && detector.onTouchEvent(event)) return true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            downY = event.getY();
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            onMove(MOVE_TYPE_TOUCH, event.getY());
        } else if (event.getAction() == MotionEvent.ACTION_UP) {
            onUp(event.getY());
        }
        return true;
    }


    /**
     * 触摸移动中
     *
     * @param type
     * @param touchY
     */
    private void onMove(int type, float touchY) {
        //刷新
        if (loopAble) {
            scrollY += (touchY - downY);
            downY = touchY;
        } else {
            //非循环 边界判断
            float tempScrollY = (scrollY + (touchY - downY));
            //是否超出边界
            boolean isOverEdge;
            if (isOverEdge = tempScrollY > oriScrollY) {
                //上滚边界
                scrollY = oriScrollY;
            } else if (isOverEdge = tempScrollY < oriMinScrollY) {
                //下滚边界
                scrollY = oriMinScrollY;
            } else {
                //上下边界内
                scrollY += (touchY - downY);
                downY = touchY;
            }
            if (isOverEdge && type == MOVE_TYPE_FLYING) {
                //超出边界 不再滚动
                gestureFlingTool.stop();
                calcPos();
            }
        }
        invalidate();
    }


    /**
     * 触摸抬起 计算附近的目标位置
     *
     * @param touchY
     */
    private void onUp(float touchY) {
        scrollY += touchY - downY;
        downY = touchY;


        float absHeight = ((scrollY - centerY) % itemHeight + itemHeight) % itemHeight;

        float dY = absHeight - halfItemHeight;

        float posY = touchY - dY;

        Log.i(TAG, "onUp: " + posY + " scrollY:" + scrollY + " targetY:" + (scrollY + dY));

        animEndTool.startAnimEnd(touchY, posY);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //计算相对view的y轴坐标
        for (int i = 0; i < dataList.size(); i++) {
            calcTextParams(canvas, i);
        }

        //分割线

        if (diverLineY1 != null) {
            paint.setColor(diverLineColor);
            canvas.drawRoundRect(diverLineY1, diverLineRadius, diverLineRadius, paint);
            canvas.drawRoundRect(diverLineY2, diverLineRadius, diverLineRadius, paint);
        }

//        //中心线
//        paint.setColor(Color.RED);
//        canvas.drawLine(0, centerY, getWidth(), centerY, paint);

//        canvas.drawText(currentPos + "", 10, 100, paint);
    }


    /**
     * 计算在指定位置的 文本状态
     */
    private void calcTextParams(Canvas canvas, int pos) {

        float currentStartY = pos * itemHeight + scrollY;
        if (loopAble)
            //求余  为负? otalHeight  :  0;
            currentStartY = (currentStartY = currentStartY % allItemHeight) + (currentStartY < 0 ? allItemHeight : 0);

        //超出边界不绘制
        if (currentStartY < 0 || currentStartY - itemHeight - getHeight() > 0) return;

        //绘制text
        drawText(canvas, pos, currentStartY);
    }

    /**
     * 绘制文本
     *
     * @param canvas
     * @param pos
     * @param y
     * @return
     */
    private void drawText(Canvas canvas, int pos, float y) {

        //距离中心位置 差距
        float dHeight = y - halfItemHeight - centerY;

        //在 界限之外的处理为在界限上
        if (dHeight < -chaneHeightRange || dHeight > chaneHeightRange) dHeight = chaneHeightRange;

        //计算差距的比例
        float ratio = 1 - Math.abs(dHeight) / (float) chaneHeightRange;

        //计算此时的文本大小
        float currentTextSize = Math.round(firstDataTextSize + ratio * dTextSize);


        //计算颜色
        int currentColor = firstDataTextColor;
        if (firstDataTextColor != centerDataTextColor) {
            currentColor = transColor(firstDataTextColor, centerDataTextColor, ratio);
        }

        String text = dataList.get(pos).toString();
        paint.setTextSize(currentTextSize);
        paint.getTextBounds(text, 0, text.length(), tempRect);
        paint.setColor(currentColor);
//        if (pos == 0)
//            Log.i(TAG, "drawText: " + currentTextSize + "\t" + tempRect.width());

        //当前y
        float currentY = Math.round(y - ((itemHeight - tempRect.height()) >> 1) - tempRect.bottom);

        //当前x
        int currentX = 0;
        if (showStyle == STYLE_CENTER) {
            //中心位置
            currentX = centerX;
        } else if (showStyle == STYLE_LEFT) {
            //左侧
            currentX = (int) (firstDataTransX + dTransX * ratio);
        } else if (showStyle == STYLE_RIGHT) {
            //右侧
            currentX = getWidth() - tempRect.width() - (int) (firstDataTransX + dTransX * ratio);
        }
//        canvas.drawText(text + currentY + "-" + currentTextSize, currentX, currentY, paint);
        canvas.drawText(text, Math.round(currentX), currentY, paint);


        //文本边框
//        tempRect.bottom += currentY;
//        tempRect.top += currentY;
//        paint.setColor(Color.parseColor("#22000000"));
//        canvas.drawRect(tempRect, paint);
//        canvas.drawLine(0, y, getWidth(), y, paint);
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

    public void setOnPickListener(OnPickListener onPickListener) {
        this.onPickListener = onPickListener;
    }

    /**
     * 选择监听
     */
    public interface OnPickListener {
        void onPick(int pos);
    }
}
