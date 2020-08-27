package com.waterfairy.widget.utils;

import android.graphics.Path;
import android.graphics.RectF;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/23 11:04
 * @info:
 */
public class PathUtils {
    /**
     * 指定 rect 内获取圆角
     *
     * @param rect
     * @param radius
     * @return
     */
    public static Path getCorner(RectF rect, int radius) {
        Path path = new Path();
        //计算path
        if (radius <= 0) {
            path.addRect(rect, Path.Direction.CW);
        } else {
            float radius2 = 2 * radius;
            path.moveTo(rect.left + radius, rect.top);
            //右上
            path.lineTo(rect.right - radius, rect.top);
            path.arcTo(new RectF(rect.right - radius2, rect.top, rect.right, rect.top + radius2), -90, 90);
            //右下
            path.lineTo(rect.right, rect.bottom - radius);
            path.arcTo(new RectF(rect.right - radius2, rect.bottom - radius2, rect.right, rect.bottom), 0, 90);
            //左下
            path.lineTo(rect.left + radius, rect.bottom);
            path.arcTo(new RectF(rect.left, rect.bottom - radius2, rect.left + radius2, rect.bottom), 90, 90);
            //左上
            path.lineTo(rect.left, rect.top + radius);
            path.arcTo(new RectF(rect.left, rect.top, rect.left + radius2, rect.top + radius2), 180, 90);
        }
        return path;
    }


    /**
     * @param rect
     * @param radius
     * @param corners 0,1,2,3
     * @return
     */
    public static Path getCorner(RectF rect, int radius, int... corners) {
        return getCorner(rect, radius, true, corners);
    }

    public static Path getCorner(RectF rect, int radius, boolean userQuad, int... corners) {

        Path path = new Path();
        boolean isHas0 = false;
        boolean isHas1 = false;
        boolean isHas2 = false;
        boolean isHas3 = false;
        for (int corner : corners) {
            switch (corner) {
                case 0:
                    isHas0 = true;
                    break;
                case 1:
                    isHas1 = true;
                    break;
                case 2:
                    isHas2 = true;
                    break;
                case 3:
                    isHas3 = true;
                    break;
            }
        }


        //启点
        if (!isHas0) {
            path.moveTo(rect.left, rect.top);
        } else {
            path.moveTo(rect.left + radius, rect.top);
        }
        //终点
        if (isHas1) {
            path.lineTo(rect.right - radius, rect.top);
            if (userQuad)
                path.quadTo(rect.right, rect.top, rect.right, rect.top + radius);
            else
                path.arcTo(new RectF(rect.right - radius * 2, rect.top, rect.right, rect.top + radius * 2), 270, 90);

        } else {
            path.lineTo(rect.right, rect.top);
        }

        if (isHas2) {
            path.lineTo(rect.right, rect.bottom - radius);
            if (userQuad)
                path.quadTo(rect.right, rect.bottom, rect.right - radius, rect.bottom);
            else
                path.arcTo(new RectF(rect.right - radius * 2, rect.bottom - radius * 2, rect.right, rect.bottom), 0, 90);

        } else {
            path.lineTo(rect.right, rect.bottom);
        }

        if (isHas3) {
            path.lineTo(rect.left + radius, rect.bottom);
            if (userQuad)
                path.quadTo(rect.left, rect.bottom, rect.left, rect.bottom - radius);
            else
                path.arcTo(new RectF(rect.left, rect.bottom - radius * 2, rect.left + radius * 2, rect.bottom), 90, 90);

        } else {
            path.lineTo(rect.left, rect.bottom);
        }

        if (isHas0) {
            path.lineTo(rect.left, rect.top + radius);
            if (userQuad)
                path.quadTo(rect.left, rect.top, rect.left + radius, rect.top);
            else
                path.arcTo(new RectF(rect.left, rect.top, rect.left + radius * 2, rect.top + radius * 2), 180, 90);

        } else {
            path.lineTo(rect.left, rect.top);
        }
        return path;
    }

    /**
     * corner 外部
     *
     * @param rect
     * @param radius
     * @param corners 0,1,2,3
     * @return
     */
    public static Path getCornerOut(RectF rect, int radius, int... corners) {

        Path path = new Path();
        boolean isHas0 = false;
        boolean isHas1 = false;
        boolean isHas2 = false;
        boolean isHas3 = false;
        for (int corner : corners) {
            switch (corner) {
                case 0:
                    isHas0 = true;
                    break;
                case 1:
                    isHas1 = true;
                    break;
                case 2:
                    isHas2 = true;
                    break;
                case 3:
                    isHas3 = true;
                    break;
            }
        }
        if (isHas0) {
            path.moveTo(rect.left, rect.top + radius);
            path.addArc(new RectF(rect.left, rect.top, rect.left + radius * 2, rect.top + radius * 2), 180, 90);
            path.lineTo(rect.left, rect.top);
            path.lineTo(rect.left, rect.top + radius);
        }
        if (isHas1) {
            path.moveTo(rect.right - radius, rect.top);
            path.addArc(new RectF(rect.right - radius * 2, rect.top, rect.right, rect.top + radius * 2), 270, 90);

            path.lineTo(rect.right, rect.top);
            path.lineTo(rect.right - radius, rect.top);
        }

        if (isHas2) {
            path.moveTo(rect.right, rect.bottom - radius);
            path.addArc(new RectF(rect.right - radius * 2, rect.bottom - radius * 2, rect.right, rect.bottom), 0, 90);
            path.lineTo(rect.right, rect.bottom);
            path.lineTo(rect.right, rect.bottom - radius);
        }

        if (isHas3) {
            path.moveTo(rect.left + radius, rect.bottom);
            path.addArc(new RectF(rect.left, rect.bottom - radius * 2, rect.left + radius * 2, rect.bottom), 90, 90);
            path.lineTo(rect.left, rect.bottom);
            path.lineTo(rect.left + radius, rect.bottom);
        }
        return path;
    }
}
