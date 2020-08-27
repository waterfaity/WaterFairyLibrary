package com.waterfairy.widget.chart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;

import com.waterfairy.widget.baseview.BaseView;
import com.waterfairy.widget.baseview.Coordinate;
import com.waterfairy.widget.baseview.Entity;
import com.waterfairy.widget.utils.CanvasUtils;
import com.waterfairy.widget.utils.GestureFlingTool;
import com.waterfairy.widget.utils.RectUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/8 13:45
 * @info:
 */
public class BaseChartView extends BaseView implements GestureFlingTool.OnFlingListener {


    protected int[] colors = new int[]{
            Color.parseColor("#0182FF"),
            Color.parseColor("#0AD1A8"),
            Color.parseColor("#04912a"),
            Color.parseColor("#c904b5"),
            Color.parseColor("#008380"),
            Color.parseColor("#ec4f01"),
            Color.parseColor("#afac00"),
            Color.parseColor("#c40d00"),
            Color.parseColor("#ac7300"),
            Color.parseColor("#52af0b"),
            Color.parseColor("#04a2f7"),
            Color.parseColor("#ffb254"),
            Color.parseColor("#74c9f7"),
            Color.parseColor("#a260ff"),
            Color.parseColor("#ff62f5"),
            Color.parseColor("#fa629a"),
            Color.parseColor("#e1855d"),
            Color.parseColor("#5e59f9")
    };

    private OnScrollListener onScrollListener;

    private static final String TAG = "lineChartView";
    //传入数据
    private List<List<Entity>> mDataList;//输入数据
    private int maxValue;
    //data
    protected List<List<Coordinate>> mCoordinateList;//数据坐标
    private List<Coordinate> mDataInfoCoordinateList;//数据信息展示的坐标
    private List<Entity> mYLineList;//Y轴线坐标
    private int mWidthEntity;//间隔
    protected int mTextSize;
    private boolean showInfo = true;//展示坐标的具体内容
    private boolean isShowingInfo;//展示坐标的具体内容的状态
    //坐标
    protected int scrollX; //滚动距离
    private int maxScrollX;
    protected int deviationStartX;//x 坐标左偏移量
    protected int deviationEndX;//x 坐标右偏移量
    protected int bottomLine;//底部y坐标(0刻度线)
    protected int leftLine;//左侧x坐标(刻度线)
    protected int rightLine;//右侧x坐标(右侧绘图区域)
    private int topLine;//上侧y坐标(上侧绘图区域)
    private int yNum = 4;//y轴个数
    private int xMaxNum = 10;//x轴最大个数
    protected int xNum;//x轴显示个数
    protected int startPos = 0;//x轴开始绘制位置
    protected int perWidth;//x间隔
    protected int dNumDrawXText = 1;//每n个画x轴坐标

    private int textWidth;
    protected int textHeight;
    private int chinaTextWidth;
    private float perValueHeight;
    //画笔
    protected Paint mPaintChart;
    protected Paint mPaintLineY;
    protected Paint mPaintText;
    private Paint mPaintInfo;


    //用于判断是否是水平滑动
    private float downY;
    private float downX;
    //rect
//    private RectF mInfoRect;
    private RectUtils.TextRectFBean mInfoTextBean;
    //data
    private boolean parentDisTouch = false;
    protected List<String> xTitles;
    private int mSelectPos;
    //颜色
    private int mColorInfoBg;//提示信息背景颜色
    private int mColorNormalText;//文本颜色  坐标颜色
    private int mColorLineSelect;//选中的位置的 垂直线的颜色
    //titles
    private List<String> typeTitles;//类型文本list
    private ArrayList<String> titlesWithScore;//有得分的类型文本list  选中提示的信息
    //多类型
    protected boolean isMulti;
    //------- 类型提示 右侧 / 底部
    public static final int TYPE_DIR_NONE = 0;
    private static final int TYPE_DIR_LEFT = 1;
    public static final int TYPE_DIR_RIGHT = 2;
    private static final int TYPE_DIR_TOP = 3;
    public static final int TYPE_DIR_BOTTOM = 4;
    private int typeTextDir;//类型文字方向
    private float mTypeTextWidth;//类型文本宽
    private float mTypeTextHeight;//高
    private RectF typeTextListRectF;//类型文本边框
    private RectUtils.TextRectFBean typeTitleBean;//类型文本bean
    private GestureDetector gestureDetector;
    private GestureFlingTool gestureFlingTool;
    private boolean freshScrollState = true;
    //-------


    public BaseChartView(Context context) {
        this(context, null);
    }

    public BaseChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        getDensity();
        initText();
        initColor();
        initPaint();
    }

    private void initColor() {
        mColorInfoBg = Color.parseColor("#CCFFFFFF");
        mColorNormalText = Color.parseColor("#454545");
        mColorLineSelect = Color.parseColor("#66454545");
    }

    /**
     * 计算文本相关
     */
    private void initText() {
        mTextSize = (int) (getDensity() * 10);
        Rect textRect = getTextRect("0", mTextSize);
        textHeight = Math.abs(textRect.bottom + textRect.top);
        textWidth = textRect.right + textRect.left;
        chinaTextWidth = getTextRect("正", mTextSize).width();
    }

    protected void initPaint() {
        mPaintLineY = new Paint();
        mPaintLineY.setColor(Color.parseColor("#999999"));
        mPaintLineY.setStrokeWidth(density);

        mPaintText = new Paint();
        mPaintText.setColor(mColorNormalText);
        mPaintText.setTextSize(mTextSize);
        mPaintText.setAntiAlias(true);

        mPaintInfo = new Paint();
        mPaintInfo.setColor(Color.parseColor("#289456"));
        mPaintInfo.setAntiAlias(true);
        mPaintInfo.setStrokeWidth(density * 1);

        mPaintChart = new Paint();
        mPaintChart.setColor(Color.parseColor("#289456"));
        mPaintChart.setAntiAlias(true);
        mPaintChart.setStrokeWidth(density * 1);
    }

    public void iniLineColor(int color) {
        mPaintInfo.setColor(color);
    }

    public void setDeviationStartX(int deviationStartX) {
        this.deviationStartX = deviationStartX;
    }

    public void setDeviationEndX(int deviationEndX) {
        this.deviationEndX = deviationEndX;
    }

    public List<List<Entity>> getDataList() {
        return mDataList;
    }

    public void addData(List<Entity> dataList, int maxValue) {
        if (mDataList == null) mDataList = new ArrayList<>();

        mDataList.add(dataList);
        setData(mDataList, maxValue);
    }

    public void setSingleData(List<Entity> dataList, int maxValue) {
        mDataList = new ArrayList<>();
        mDataList.add(dataList);
        this.maxValue = maxValue;
        setData(mDataList, maxValue);
    }

    public void setData(List<List<Entity>> mDataList, int maxValue) {
        this.maxValue = maxValue;
        this.mDataList = mDataList;
        isMulti = mDataList.size() > 1;
        if (mDataList.size() > 0) {
            List<Entity> entities = mDataList.get(0);
            xTitles = new ArrayList<>();
            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);
                xTitles.add(entity.name);
            }
        }
    }

    public void fresh() {
        requestLayout();
        invalidate();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        width = right - left;
        height = bottom - top;
        if (width != 0 && height != 0) {
            calcData();
        }
    }

    /**
     * 计算数据坐标
     */
    protected void calcData() {
        //计算 typeText
        calcTypeText();
        //一般计算
        calcNormal();
        //计算y轴
        calcY();
        //计算x轴
        calcX();
        //计算位置
        reCalcTypeText();
    }

    /**
     * 由 typeTextDir 计算具体坐标
     */
    private void reCalcTypeText() {
        if (typeTextListRectF != null) {
            switch (typeTextDir) {

                case TYPE_DIR_LEFT:
                case TYPE_DIR_RIGHT:
                    if (typeTextDir == TYPE_DIR_LEFT) {
                        typeTextListRectF.left = leftLine - mTypeTextWidth;
                        typeTextListRectF.right = leftLine;
                    } else {
                        typeTextListRectF.left += rightLine;
                        typeTextListRectF.right += rightLine;
                    }

                    int dx = bottomLine - topLine;
                    if (typeTextListRectF.height() < (dx)) {
                        float dx2 = (dx - typeTextListRectF.height()) / 2;
                        typeTextListRectF.top += dx2;
                        typeTextListRectF.bottom += dx2;
                    }
                    break;
                case TYPE_DIR_TOP:
                case TYPE_DIR_BOTTOM:

                    if (typeTextDir == TYPE_DIR_TOP) {
                        typeTextListRectF.top = topLine - mTypeTextHeight;
                        typeTextListRectF.bottom = bottomLine;
                    } else {
                        Rect textRect = getTextRect("正", mTextSize);
                        typeTextListRectF.top += (bottomLine + 2 * textRect.height());
                        typeTextListRectF.bottom += (bottomLine + 2 * textRect.height());
                    }
                    int centerX = getWidth() / 2;
                    typeTextListRectF.left = centerX - mTypeTextWidth / 2;
                    typeTextListRectF.right = centerX + mTypeTextWidth / 2;
                    break;
            }

        }
    }

    /**
     * 计算typeTitleBean   主要是rect  不是具体位置
     */
    private void calcTypeText() {
        if (typeTitles != null && typeTitles.size() > 0 && typeTextDir != TYPE_DIR_NONE) {
            boolean isTypeTextVer = typeTextDir == TYPE_DIR_LEFT || typeTextDir == TYPE_DIR_RIGHT;
            typeTitleBean = RectUtils.getTextRectFBean(typeTitles, mTextSize, 0.5F, getWidth(), isTypeTextVer ? 10 : 0, isTypeTextVer ? 0 : 20, true,
                    isTypeTextVer);
            typeTextListRectF = typeTitleBean.rectF;
            mTypeTextWidth = typeTextListRectF.width();
            mTypeTextHeight = typeTextListRectF.height();
        }
    }


    private void calcNormal() {
        if (yNum < 1) yNum = 1;
        if (maxValue <= 0) maxValue = 100;
        rightLine = (int) (width - textWidth * 2 - getPaddingRight() - (typeTextDir == TYPE_DIR_RIGHT ? mTypeTextWidth : 0));
        leftLine = (int) ((("" + maxValue).length() + 2) * textWidth + getPaddingLeft() + (typeTextDir == TYPE_DIR_LEFT ? mTypeTextWidth : 0));

    }

    private void calcY() {
        //计算y轴坐标 上留 一个字符高度  下留2个字符高度
        for (int i = 0; i < yNum; i++) {
            if ((maxValue + i) % yNum == 0) {
                int tempMaxValue = maxValue + i;
                //设置底部线
                bottomLine = (int) (height - 3 * textHeight - getPaddingBottom() - (typeTextDir == TYPE_DIR_BOTTOM ? mTypeTextHeight : 0));
                topLine = (int) (textHeight * 2 + getPaddingTop() + (typeTextDir == TYPE_DIR_TOP ? mTypeTextHeight : 0));
                int perHeight = (bottomLine - topLine) / yNum;
                perValueHeight = (bottomLine - topLine) / (float) tempMaxValue;
                mYLineList = new ArrayList<>();
                for (int j = 0; j < yNum + 1; j++) {
                    Entity entity = new Entity();
                    //y坐标
                    entity.value = bottomLine - j * perHeight;
                    //y显示文字
                    entity.name = tempMaxValue / yNum * j + "";
                    mYLineList.add(entity);
                }
            }
        }
    }

    private void calcX() {
        if (mDataList != null && mDataList.size() > 0) {
            List<Entity> entities = mDataList.get(0);
            //计算数据坐标
            mCoordinateList = new ArrayList<>();
            int tempLineWidth = (rightLine - leftLine) - (deviationStartX + deviationEndX);
            xNum = entities.size();
            if (xNum > xMaxNum) xNum = xMaxNum;
            if (xNum == 1) {
                //只有一个
                for (int i = 0; i < mDataList.size(); i++) {
                    List<Entity> entitiesTemp = mDataList.get(i);
                    Entity entity = entitiesTemp.get(0);
                    List<Coordinate> list = new ArrayList<>();
                    list.add(new Coordinate(deviationStartX + leftLine + (tempLineWidth / 2), (int) (bottomLine - entity.getValue() * perValueHeight), entity.getValue(), entity.getName()));
                    mCoordinateList.add(list);
                }

            } else {
                perWidth = tempLineWidth / (xNum - 1);
                for (int j = 0; j < mDataList.size(); j++) {
                    List<Entity> entityList = mDataList.get(j);
                    List<Coordinate> list = new ArrayList<>();
                    for (int i = 0; i < entityList.size(); i++) {
                        Entity entity = entityList.get(i);
                        list.add(new Coordinate(deviationStartX + leftLine + i * perWidth, (int) (bottomLine - entity.getValue() * perValueHeight), entity.getValue(), entity.getName()));
                    }
                    mCoordinateList.add(list);
                }

                maxScrollX = perWidth * (mCoordinateList.get(0).size() - xNum) + deviationStartX - deviationEndX;
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaintText.setColor(mColorNormalText);

        //绘制y轴
        drawY(canvas);
        //绘制x轴
        drawX(canvas);
        //绘制typeText
        drawTypeText(canvas);
        //绘制提示信息
        drawInfo(canvas);
    }


    private void drawTypeText(Canvas canvas) {
//        CanvasUtils.drawCorner(canvas, typeTitleBean.rectF, 10, 2, mColorNormalText, mColorInfoBg, mPaintLine);
        CanvasUtils.drawHorTextList(canvas, typeTitleBean, colors, mPaintText);
    }

    /**
     * 点击某个位置  出现提示信息
     *
     * @param canvas
     */
    private void drawInfo(Canvas canvas) {
        if (showInfo && isShowingInfo && typeTitles != null) {
            //绘制选中的pos 垂直线
            mPaintInfo.setColor(mColorLineSelect);
            canvas.drawLine(mInfoTextBean.centerX, topLine, mInfoTextBean.centerX, bottomLine, mPaintInfo);
            //背景绘制 - 圆角
            CanvasUtils.drawCorner(canvas, mInfoTextBean.rectF, 10, 2, mColorNormalText, mColorInfoBg, mPaintInfo);
            //提示内容
            CanvasUtils.drawHorTextList(canvas, mInfoTextBean, colors, mPaintText);
        }
    }

    protected void drawX(Canvas canvas) {
        if (mCoordinateList == null || mCoordinateList.size() == 0) return;
        List<Coordinate> coordinates = mCoordinateList.get(0);
        if (coordinates == null || coordinates.size() == 0) return;
        int maxPos = startPos + xNum;
        if (dNumDrawXText < 0) dNumDrawXText = 1;
        for (int j = 0; j < mCoordinateList.size(); j++) {
            List<Coordinate> coordinateList = mCoordinateList.get(j);
            for (int i = startPos; i < maxPos + 1 && i < coordinateList.size(); i++) {
                Coordinate coordinate = coordinateList.get(i);
                int currentX = coordinate.x + scrollX;

                //绘制 图
                drawChart(canvas, coordinateList, i, currentX, mCoordinateList.size(), j);

                //最后一个数据集合绘制刻度
                if (j == mCoordinateList.size() - 1) {
                    canvas.drawLine(currentX, bottomLine, currentX, bottomLine + textHeight / 2, mPaintLineY);
                    String xTitle = "";
                    if (xTitles != null && xTitles.size() > i) {
                        xTitle = xTitles.get(i);
                    } else {
                        xTitle = coordinate.text;
                    }
                    if (i % dNumDrawXText == 0 && !TextUtils.isEmpty(xTitle)) {
                        Rect textRect = getTextRect(xTitle, mTextSize);
                        int width = textRect.right + Math.abs(textRect.left);
                        int height = Math.abs(textRect.bottom - textRect.top);
                        canvas.drawText(xTitle, currentX - width / 2, bottomLine + height * 2F, mPaintText);
                    }
                }
            }
        }
    }

    /**
     * @param canvas                画布
     * @param currentCoordinateList 当前数据集合的坐标
     * @param currentIndex          当前数据下标
     * @param currentX              当前x坐标
     * @param mulDataSize           数据集合总数
     * @param mulDataIndex          当前数据集合下标
     */
    protected void drawChart(Canvas canvas, List<Coordinate> currentCoordinateList, int currentIndex, int currentX, int mulDataSize, int mulDataIndex) {

    }

    private void drawY(Canvas canvas) {
        canvas.drawLine(leftLine, getPaddingTop() + textHeight, leftLine, bottomLine, mPaintLineY);
        if (mYLineList == null) return;
        for (int i = 0; i < mYLineList.size(); i++) {
            Entity entity = mYLineList.get(i);
            canvas.drawLine(leftLine, entity.getValue(), rightLine, entity.getValue(), mPaintLineY);
            canvas.drawText(entity.getName(), textWidth + getPaddingLeft(), entity.getValue() + textHeight / 2, mPaintText);
        }
    }


    public void setNumDrawXText(int dNumDrawXText) {
        this.dNumDrawXText = dNumDrawXText;
    }

    float lastMoveX = 0;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();


        if (gestureDetector == null) {
            gestureDetector = new GestureDetector(getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                    if (gestureFlingTool == null) {
                        gestureFlingTool = new GestureFlingTool();
                        gestureFlingTool.setOnFlingListener(BaseChartView.this);
                    }
                    gestureFlingTool.startFling(e1, e2, velocityX, velocityY);
                    return true;
                }

                @Override
                public boolean onSingleTapUp(MotionEvent e) {
//                    calcTapIndex(e.getX());
                    return super.onSingleTapUp(e);
                }
            });
        }
        gestureDetector.onTouchEvent(event);

        if (action == MotionEvent.ACTION_DOWN) {
            if (gestureFlingTool != null && gestureFlingTool.isFling()) {
                gestureFlingTool.stop();
            }
            freshScrollState = true;
            downY = event.getY();
            downX = event.getX();
            lastMoveX = event.getX();
            getParent().requestDisallowInterceptTouchEvent(parentDisTouch);
        } else if (action == MotionEvent.ACTION_MOVE) {
            isShowingInfo = false;
            handleMove(event.getX());
            if (onScrollListener != null && freshScrollState) {
                float dx = Math.abs(event.getX() - downX);
                float dy = Math.abs(event.getY() - downY);
                if (dx >= 5 || dy >= 5) {
                    freshScrollState = false;
                    boolean isHor = dx > dy;
                    onScrollListener.onScroll(isHor);
                }
            }
        } else if (action == MotionEvent.ACTION_UP) {
            float touchX = event.getX() + Math.abs(scrollX);
            if (mCoordinateList != null && mCoordinateList.size() > 0) {
                List<Coordinate> list = mCoordinateList.get(0);
                if (list != null && list.size() > 0) {
                    for (int i = 0; i < list.size(); i++) {
                        Coordinate coordinate = list.get(i);
                        if (touchX > coordinate.x - perWidth / 2 && touchX < coordinate.x + perWidth / 2) {
                            mSelectPos = i;
                            if (onPosClickListener != null)
                                onPosClickListener.onLineClick(i, coordinate.x + scrollX, event.getY());
                            if (showInfo && typeTitles != null) {
                                onTouchPos(i, coordinate.x + scrollX, event.getY());
                                invalidate();
                            }
                            return true;
                        }
                    }
                }
            }
        }
        return true;
    }

    private void onTouchPos(int pos, int x, float y) {
        if (isShowingInfo) {
            isShowingInfo = false;
        } else {
            isShowingInfo = true;
            //计算展示的位置
            if (mDataList != null && mDataList.size() > 0) {
                titlesWithScore = new ArrayList<>();
                for (int i = 0; i < mDataList.size(); i++) {
                    List<Entity> entities = mDataList.get(i);
                    String title = "";
                    if (typeTitles.size() > i) title = typeTitles.get(i);
                    if (entities != null && entities.size() > pos) {
                        Entity entity = entities.get(pos);
                        String text = title + ":" + entity.getValue();
                        titlesWithScore.add(text);
                    }
                }

                mInfoTextBean = RectUtils.getTextRectFBean(titlesWithScore, mTextSize, 0.2F, 0, 10, 0, true, true);
                mInfoTextBean.rectF = RectUtils.getRectF(mInfoTextBean.rectF, x, y, leftLine, topLine, rightLine, bottomLine);
                mInfoTextBean.centerX = x;
                mInfoTextBean.centerY = y;
            }
        }
        invalidate();
    }

    private void handleMove(float touchX) {
        float dx = touchX - lastMoveX;
        if (dx == 0 || xNum <= 1) return;
        scrollX += (touchX - lastMoveX);
        if (scrollX > 0) scrollX = 0;
        else if (scrollX < -maxScrollX) scrollX = -maxScrollX;
        startPos = -scrollX / perWidth;
        if (startPos < 0) startPos = 0;
        lastMoveX = touchX;
        invalidate();
    }

    public void setParentDisTouch(boolean parentDisTouch) {
        this.parentDisTouch = parentDisTouch;
    }

    public void setXTitles(List<String> titles) {
        this.xTitles = titles;
    }

    public void setShowInfo(boolean showInfo) {
        this.showInfo = showInfo;
    }

    private OnPosClickListener onPosClickListener;

    public void setOnPosClickListener(OnPosClickListener onPosClickListener) {
        this.onPosClickListener = onPosClickListener;
    }

    public OnPosClickListener getOnPosClickListener() {
        return onPosClickListener;
    }

    public void setTypeTitles(List<String> typeTitles) {
        this.typeTitles = typeTitles;
    }

    @Override
    public void onFlingStart(float x, float y) {
    }

    @Override
    public void onFling(float x, float y) {
        handleMove(x);
    }

    @Override
    public void onFlingEnd() {

    }

    public void setXMaxNum(int xMaxNum) {
        this.xMaxNum = xMaxNum;
    }

    public interface OnPosClickListener {
        void onLineClick(int pos, float x, float y);
    }

    public void setTypeTextDir(int typeTextDir) {
        this.typeTextDir = typeTextDir;
    }

    public void setOnScrollListener(OnScrollListener onScrollListener) {
        this.onScrollListener = onScrollListener;
    }

    public interface OnScrollListener {
        void onScroll(boolean isHorScroll);
    }
}
