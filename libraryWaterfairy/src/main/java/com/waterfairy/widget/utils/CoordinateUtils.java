package com.waterfairy.widget.utils;


import com.waterfairy.widget.baseview.Coordinate;

/**
 * @author water_fairy
 * @email 995637517@qq.com
 * @date 2018/6/15 11:21
 * @info:
 */
public class CoordinateUtils {

    /**
     * @param angle  角度
     * @param radius 长度
     * @param x      圆心x
     * @param y      圆心y
     * @return
     */

    public static Coordinate calcWithRadius(float angle, float radius, float x, float y) {
        double height = Math.sin(Math.toRadians(angle)) * radius;
        double width = Math.cos(Math.toRadians(angle)) * radius;
        return new Coordinate((int) (x + width), (int) (y + height));
    }

    public static double getAngle(float x, float y, float centerX, float centerY) {
        return getAngle(x, y, centerX, centerY, 0);

    }

    /**
     * @param x
     * @param y
     * @param centerX
     * @param centerY
     * @param transAngle
     * @return
     */
    public static double getAngle(float x, float y, float centerX, float centerY, double transAngle) {
        float dy = y - centerY;
        float dx = x - centerX;
        double degrees = Math.toDegrees(Math.atan((dy) / dx));
        if (dx >= 0 && dy >= 0) {
            //第一象限
        } else if (dx < 0 && dy > 0) {
            //二
            degrees = 180 - Math.abs(degrees);
        } else if (dx < 0 && dy < 0) {
            //三
            degrees += 180;
        } else {
            //四
            degrees = 360 - Math.abs(degrees);
        }
        if (transAngle != 0) {
            transAngle = -transAngle;
            degrees = degrees + transAngle;
            if (degrees > 360) degrees = degrees - 360;
            else if (degrees < 0) degrees = 360 + degrees;
        }
        return degrees;
    }

}
