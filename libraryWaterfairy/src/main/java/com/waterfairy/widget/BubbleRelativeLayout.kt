package com.waterfairy.widget

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.widget.RelativeLayout
import com.waterfairy.library.R
import com.waterfairy.widget.utils.PathUtils

/**
 * @author  water_fairy
 * @email   995637517@qq.com
 * @date    2019-07-18 10:08
 * @info:
 */
class BubbleRelativeLayout(context: Context, attributes: AttributeSet) :
    RelativeLayout(context, attributes) {

    private var bgColor: Int
    private var rightMargin: Int//三角形定点位置
    private var triangleWidth: Int
    private var triangleHeight: Int
    private var radius: Int
    private var paint: Paint
    private var margin = 20//边缘间隔
    private var center = false

    /**
     * 阴影过度颜色
     */
    private var shadowColors = arrayOf(
        Color.parseColor("#49000000"),
        Color.parseColor("#42000000"),
        Color.parseColor("#35000000"),
        Color.parseColor("#28000000"),
        Color.parseColor("#21000000"),
        Color.parseColor("#14000000"),
        Color.parseColor("#07000000")
    )


    init {
        setBackgroundColor(Color.TRANSPARENT)
        val typedArray =
            context.obtainStyledAttributes(attributes, R.styleable.BubbleRelativeLayout)
        bgColor = typedArray.getColor(R.styleable.BubbleRelativeLayout_bubbleBGColor, Color.WHITE)
        rightMargin = typedArray.getDimensionPixelSize(
            R.styleable.BubbleRelativeLayout_bubbleRightMargin,
            (resources.displayMetrics.density * 30).toInt()
        )
        radius = typedArray.getDimensionPixelSize(
            R.styleable.BubbleRelativeLayout_bubbleRadius,
            (resources.displayMetrics.density * 10).toInt()
        )
        triangleWidth = typedArray.getDimensionPixelSize(
            R.styleable.BubbleRelativeLayout_bubbleTriangleWidth,
            (resources.displayMetrics.density * 20).toInt()
        )
        triangleHeight = typedArray.getDimensionPixelSize(
            R.styleable.BubbleRelativeLayout_bubbleTriangleHeight,
            (resources.displayMetrics.density * 15).toInt()
        )
        center = typedArray.getBoolean(R.styleable.BubbleRelativeLayout_bubbleCenter, false)
        setPadding(
            radius + shadowColors.size,
            (triangleHeight + radius) + shadowColors.size,
            radius + shadowColors.size,
            radius + shadowColors.size
        )

        typedArray.recycle()
        paint = Paint()
        paint.color = bgColor
        paint.isAntiAlias = true
    }

    override fun onDraw(canvas: Canvas?) {
        if (width != 0 && height != 0) {

            paint.style = Paint.Style.STROKE

            for (i in shadowColors.indices) {
                paint.color = shadowColors[i]
                if (i % 2 == 1) {
                    canvas?.drawPath(getS(i), paint)
                }
                val corner = PathUtils.getCorner(
                    RectF(
                        0F + margin - i + shadowColors.size / 3,
                        ((triangleHeight - 1).toFloat()) - i + margin + shadowColors.size / 2,
                        width.toFloat() - margin + i - shadowColors.size / 4,
                        height.toFloat() - margin + i
                    ), radius
                )
                canvas?.drawPath(corner, paint)
            }

            paint.color = bgColor
            paint.style = Paint.Style.FILL_AND_STROKE

            val corner = PathUtils.getCorner(
                RectF(
                    0F + margin,
                    ((triangleHeight - 1).toFloat()) + margin,
                    width.toFloat() - margin,
                    height.toFloat() - margin
                ), radius
            )
            canvas?.drawPath(corner, paint)
            canvas?.drawPath(getS(0), paint)
        }
    }

    /**
     * 三角形
     */
    private fun getS(marginTemp: Int): Path {
        if (center) {
            rightMargin = width / 2
        }
        val path = Path()
        //右点
        path.moveTo(
            (width - rightMargin + triangleWidth / 2).toFloat() + marginTemp,
            triangleHeight.toFloat() + margin
        )
        //左点
        path.lineTo(
            (width - rightMargin - triangleWidth / 2).toFloat() - marginTemp,
            triangleHeight.toFloat() + margin
        )
        //顶点
        path.lineTo((width - rightMargin).toFloat(), 0F - marginTemp + margin)
        //右点
        path.lineTo(
            (width - rightMargin + triangleWidth / 2).toFloat() + marginTemp,
            triangleHeight.toFloat() + margin
        )
        return path;
    }
}